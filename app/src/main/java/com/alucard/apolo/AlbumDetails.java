package com.alucard.apolo;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.media.MediaMetadataRetriever;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;
import com.bumptech.glide.Glide;
import java.util.ArrayList;
import static com.alucard.apolo.BibliotecaActivity.musicFiles;
import static com.alucard.apolo.BibliotecaActivity.tipoVista;

public class AlbumDetails extends AppCompatActivity {
    RecyclerView recyclerView;
    ImageView albumPhoto;
    String albumName;
    int tipo;
    ArrayList<MusicFiles> albumSongs = new ArrayList<>();
    AlbumDetailsAdapter albumDetailsAdapter;
    TextView nameAlbum;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_album_details);
        recyclerView = findViewById(R.id.recyclerView);
        albumPhoto = findViewById(R.id.albumPhoto);
        nameAlbum = findViewById(R.id.nombreAlbum);
        albumName = getIntent().getStringExtra("albumName");
        tipo = getIntent().getIntExtra("tipo",0);
        nameAlbum.setText(albumName);
        if (tipoVista == 2 || tipo == 2) {
            int j = 0;
            for (int i = 0; i < musicFiles.size(); i++) {
                if (albumName.equals(musicFiles.get(i).getAlbum())) {
                    albumSongs.add(j, musicFiles.get(i));
                    j++;
                }
            }
        }else if (tipoVista == 3 || tipo == 3){
            int j = 0;
            for (int i = 0; i < musicFiles.size(); i++) {
                if (albumName.equals(musicFiles.get(i).getArtist())) {
                    albumSongs.add(j, musicFiles.get(i));
                    j++;
                }
            }
        }
        byte[] image = getAlbumArt(albumSongs.get(0).getPath());
        if (image != null){
            Glide.with(this).load(image).into(albumPhoto);
        }else{
            Glide.with(this).load(R.drawable.no_cover).into(albumPhoto);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (!(musicFiles.size() < 1)){
            albumDetailsAdapter = new AlbumDetailsAdapter(this, albumSongs);
            recyclerView.setAdapter(albumDetailsAdapter);
            recyclerView.setLayoutManager(new LinearLayoutManager(this, RecyclerView.VERTICAL, false));
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
