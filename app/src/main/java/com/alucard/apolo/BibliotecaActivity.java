package com.alucard.apolo;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.viewpager.widget.ViewPager;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.google.android.material.tabs.TabItem;
import com.google.android.material.tabs.TabLayout;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static com.alucard.apolo.MusicPlayActivity.position;

public class BibliotecaActivity extends AppCompatActivity {

    TabLayout tabLayout;
    ViewPager viewPager;
    TabItem canciones, listas, albumes, artistas;
    ViewPagerAdapter pagerAdapter;
    private List<Integer> fragmentsIcons = new ArrayList<>(Arrays.asList(R.drawable.canciones, R.drawable.listas, R.drawable.album, R.drawable.artist));
    public static int REQUEST_CODE = 1;
    static ArrayList<MusicFiles> musicFiles;
    static int tipoVista = 0;
    ArchivoJson archivoJson;

    static boolean shuffle = false, repeat = false;
    static boolean reproduccion = false;

    public static BarraInferior barrita;

    FileInputStream in = null;
    String filename = "lista.txt";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_biblioteca);
        permission();
        findViewById(R.id.barraReproduccion).setVisibility(View.INVISIBLE);
        archivoJson = new ArchivoJson(this, filename);
        archivoJson.crearArchivo(filename);

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition());
                switch (tab.getPosition()){
                    case 0:
                        tabLayout.getTabAt(0).setIcon(R.drawable.cancionesselect);
                        tabLayout.getTabAt(1).setIcon(R.drawable.listas);
                        tabLayout.getTabAt(2).setIcon(R.drawable.album);
                        tabLayout.getTabAt(3).setIcon(R.drawable.artist);
                        tipoVista = 0;
                        break;
                    case 1:
                        tabLayout.getTabAt(0).setIcon(R.drawable.canciones);
                        tabLayout.getTabAt(1).setIcon(R.drawable.listasselect);
                        tabLayout.getTabAt(2).setIcon(R.drawable.album);
                        tabLayout.getTabAt(3).setIcon(R.drawable.artist);
                        tipoVista = 1;
                        break;
                    case 2:
                        tabLayout.getTabAt(0).setIcon(R.drawable.canciones);
                        tabLayout.getTabAt(1).setIcon(R.drawable.listas);
                        tabLayout.getTabAt(2).setIcon(R.drawable.albumselect);
                        tabLayout.getTabAt(3).setIcon(R.drawable.artist);
                        tipoVista = 2;
                        break;
                    case 3:
                        tabLayout.getTabAt(0).setIcon(R.drawable.canciones);
                        tabLayout.getTabAt(1).setIcon(R.drawable.listas);
                        tabLayout.getTabAt(2).setIcon(R.drawable.album);
                        tabLayout.getTabAt(3).setIcon(R.drawable.artistselect);
                        tipoVista = 3;
                        break;
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }

        });

        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

        barrita = new BarraInferior();
        fragmentTransaction.add(R.id.barraReproduccion, barrita);
        fragmentTransaction.commit();

    }

    @Override
    protected void onResume() {
        super.onResume();
        if (position!=-1){
            findViewById(R.id.barraReproduccion).setVisibility(View.VISIBLE);
        }
    }

    private void permission() {
        if (ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(BibliotecaActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}
            , REQUEST_CODE);
        }else{
            //Toast.makeText(this, "Permiso concedido", Toast.LENGTH_SHORT).show();
            musicFiles = getAllAudio(this);
            setupView();
            setUpViewPagerAdapter();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_CODE){
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED){
                //Toast.makeText(this, "Permiso concedido", Toast.LENGTH_SHORT).show();
                musicFiles = getAllAudio(this);
                setupView();
                setUpViewPagerAdapter();
            }else{
                ActivityCompat.requestPermissions(BibliotecaActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}
                        , REQUEST_CODE);
            }
        }
    }

    private void setupView(){
        tabLayout = findViewById(R.id.tabLayout);
        viewPager = findViewById(R.id.viewPager);

        pagerAdapter = new ViewPagerAdapter(getSupportFragmentManager());
    }

    private void setUpViewPagerAdapter(){
        pagerAdapter.addFragment(new CancionesFragment(), "Canciones");
        pagerAdapter.addFragment(new ListasFragment(), "Listas");
        pagerAdapter.addFragment(new AlbumesFragment(), "Albumes");
        pagerAdapter.addFragment(new ArtistasFragment(), "Artistas");
        viewPager.setAdapter(pagerAdapter);

        tabLayout.setupWithViewPager(viewPager);

        //Iconos
        for (int i = 0; i < tabLayout.getTabCount(); i++) {
            tabLayout.getTabAt(i).setIcon(fragmentsIcons.get(i));
        }
        tabLayout.getTabAt(0).setIcon(R.drawable.cancionesselect);
    }

    private static ArrayList<MusicFiles> getAllAudio(Context context) {
        ArrayList<MusicFiles> tempAudioList = new ArrayList<>();
        Uri uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;

        String[] projection = {
            MediaStore.Audio.Media.ALBUM,
            MediaStore.Audio.Media.TITLE,
            MediaStore.Audio.Media.DURATION,
            MediaStore.Audio.Media.DATA, //Para el path (ruta)
            MediaStore.Audio.Media.ARTIST,
            MediaStore.Audio.Media._ID,
        };
        Cursor cursor = context.getContentResolver().query(uri,projection,
                null, null, null);
        if (cursor != null){
            while (cursor.moveToNext()){
                String album = cursor.getString(0);
                String title = cursor.getString(1);
                String duration = cursor.getString(2);
                String path = cursor.getString(3);
                String artist = cursor.getString(4);
                String id = cursor.getString(5);

                MusicFiles musicFiles = new MusicFiles(path, title, artist, album, duration, id);

                //Revisamos
                Log.e("Path: "+path, "Album: "+ album);

                tempAudioList.add(musicFiles);
            }
            cursor.close();
        }
        return tempAudioList;
    }

    public ViewPager getViewPager(){
        viewPager = (ViewPager)findViewById(R.id.viewPager);
        return viewPager;
    }

}
