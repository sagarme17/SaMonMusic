package com.alucard.apolo;

import android.media.MediaMetadataRetriever;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;

public class ListaDetails extends AppCompatActivity {
    private RecyclerView recyclerView;
    private ListaDetailsAdapter listaDetailsAdapter;
    private String filename = "lista.txt";
    ImageView listaFoto, listaFoto1, listaFoto2, listaFoto3, listaFoto4 ;
    String nombreLista, foto, foto2, foto3, foto4;;
    TextView nameLista;
    int posicion, songs;
    ArchivoJson archivoJson;

    @Override
    protected void onCreate(Bundle savedInstance){
        super.onCreate(savedInstance);
        setContentView(R.layout.lista_details);
        nombreLista = getIntent().getStringExtra("listName");
        posicion = getIntent().getIntExtra("posicion", 0);
        songs = getIntent().getIntExtra("songs", 0);
        recyclerView = findViewById(R.id.listaElements);
        listaFoto = findViewById(R.id.listaPhoto);
        listaFoto1 = findViewById(R.id.listaPhotopt1);
        listaFoto2 = findViewById(R.id.listaPhotopt2);
        listaFoto3 = findViewById(R.id.listaPhotopt3);
        listaFoto4 = findViewById(R.id.listaPhotopt4);
        nameLista = findViewById(R.id.nombreLista);
        nameLista.setText(nombreLista);


        if(songs == 1 || songs == 2 || songs == 3)
        {
            archivoJson = new ArchivoJson(this, filename);
            foto = archivoJson.obtenerUna(posicion);
            listaFoto1.setVisibility(View.INVISIBLE);
            listaFoto2.setVisibility(View.INVISIBLE);
            listaFoto3.setVisibility(View.INVISIBLE);
            listaFoto4.setVisibility(View.INVISIBLE);
            listaFoto.setVisibility(View.VISIBLE);
            byte[] image = getAlbumArt(foto);
            Glide.with(this).asBitmap().load(image).into(listaFoto);
        }else if(songs >= 4)
        {
            archivoJson = new ArchivoJson(this, filename);
            foto = archivoJson.obtenerUna(posicion);
            foto2 = archivoJson.obtenerDos(posicion);
            foto3 = archivoJson.obtenerTres(posicion);
            foto4 = archivoJson.obtenerCuatro(posicion);

            listaFoto1.setVisibility(View.VISIBLE);
            listaFoto2.setVisibility(View.VISIBLE);
            listaFoto3.setVisibility(View.VISIBLE);
            listaFoto4.setVisibility(View.VISIBLE);
            listaFoto.setVisibility(View.INVISIBLE);

            byte[] image = getAlbumArt(foto);
            Glide.with(this).asBitmap().load(image).into(listaFoto1);
            byte[] image2 = getAlbumArt(foto2);
            Glide.with(this).asBitmap().load(image2).into(listaFoto2);
            byte[] image3 = getAlbumArt(foto3);
            Glide.with(this).asBitmap().load(image3).into(listaFoto3);
            byte[] image4 = getAlbumArt(foto4);
            Glide.with(this).asBitmap().load(image4).into(listaFoto4);
        }


        ArrayList<MusicFiles> data = new ArrayList<>();
        archivoJson = new ArchivoJson(this, filename);

        try {
            String jsonString = archivoJson.readJSON();
            JSONObject jsonObject = new JSONObject(jsonString);
            JSONArray canciones = jsonObject.getJSONArray("lista").getJSONObject(posicion).getJSONArray("canciones");

            for (int i=0; i<canciones.length(); i++){
                JSONObject item = canciones.getJSONObject(i);
                String nombre = item.getString("nombreCancion");
                String artista = item.getString("artista");
                String duracion = item.getString("duracion");
                String album = item.getString("album");
                String foto = item.getString("foto");
                MusicFiles musicFiles = new MusicFiles(foto, nombre, artista, album, duracion);
                data.add(musicFiles);
            }

            recyclerView = (RecyclerView)findViewById(R.id.listaElements);
            listaDetailsAdapter = new ListaDetailsAdapter(this, data);
            recyclerView.setAdapter(listaDetailsAdapter);
            recyclerView.setLayoutManager(new LinearLayoutManager(this));
        }catch (JSONException e){
        }
    }

    private byte[] getAlbumArt(String uri){
        MediaMetadataRetriever retriever = new MediaMetadataRetriever();
        retriever.setDataSource(uri);
        byte[] art = retriever.getEmbeddedPicture();
        retriever.release();
        return art;
    }


}
