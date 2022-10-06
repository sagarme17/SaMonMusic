package com.alucard.apolo;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.media.MediaMetadataRetriever;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import java.util.ArrayList;

import static com.alucard.apolo.BibliotecaActivity.reproduccion;

public class MusicAdapter extends RecyclerView.Adapter<MusicAdapter.MyViewHolder> {
    Context mContext;
    ArrayList<MusicFiles> mFiles;
    Dialog myDialog;
    String name, artist, title, time, prueba;
    byte[] image;
    ArchivoJson archivoJson;
    Button favorito;
    String fav = "null";
    private String filename = "lista.txt";

    MusicAdapter(Context mContext, ArrayList<MusicFiles> mFiles){
        this.mFiles = mFiles;
        this.mContext = mContext;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.music_items,parent,false);
        myDialog = new Dialog(mContext);
        myDialog.setContentView(R.layout.dialog_cancionesmenu);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, final int position) {
        String duracion="";
        holder.file_name.setText(mFiles.get(position).getTitle());
        holder.artist_name.setText(mFiles.get(position).getArtist());
        holder.album_name.setText(mFiles.get(position).getAlbum());
        final int min = Integer.parseInt (mFiles.get(position).getDuration()) / 1000 / 60;
        int sec = Integer.parseInt (mFiles.get(position).getDuration()) / 1000 % 60;

        duracion = min+":";
        if (sec<10){
            duracion += "0";
        }
        duracion+=sec;

        holder.duration.setText(duracion);
        holder.album_foto.setText(mFiles.get(position).getPath());
        image = getAlbumArt(mFiles.get(position).getPath());
        if(image != null){
            Glide.with(mContext).asBitmap().load(image).into(holder.album_art);
        }else{
            Glide.with(mContext).asBitmap().load(R.drawable.no_cover).into(holder.album_art);
        }
        //Generamos el intento para iniciar el reproductor
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                reproduccion = false;
                Intent intent = new Intent(mContext, MusicPlayActivity.class);
                intent.putExtra("position", position);
                mContext.startActivity(intent);
            }
        });

        holder.setOnClickListeners();
    }

    @Override
    public int getItemCount() {
        return mFiles.size();
    }

    private byte[] getAlbumArt(String uri){
        MediaMetadataRetriever retriever = new MediaMetadataRetriever();
        retriever.setDataSource(uri);
        byte[] art = retriever.getEmbeddedPicture();
        retriever.release();
        return art;
    }

    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView file_name, artist_name, album_name, duration, album_foto;
        ImageView album_art;
        ImageButton menu;

        public MyViewHolder(@NonNull View itemView){
            super(itemView);
            file_name = itemView.findViewById(R.id.music_file_name);
            artist_name = itemView.findViewById(R.id.music_file_artist);
            album_name = itemView.findViewById(R.id.music_file_album);
            duration = itemView.findViewById(R.id.music_file_duration);
            album_art = itemView.findViewById(R.id.music_img);
            album_foto = itemView.findViewById(R.id.music_file_foto);
            menu = itemView.findViewById(R.id.menu);
        }

        void setOnClickListeners() {
            menu.setOnClickListener(this);
        }


        public void onClick(View v) {
            ImageView music_img_op = (ImageView) myDialog.findViewById(R.id.music_img_op);
            TextView music_file_name_op = (TextView) myDialog.findViewById(R.id.music_file_name_op);
            TextView music_file_artist_op = (TextView) myDialog.findViewById(R.id.music_file_artist_op);
            TextView music_file_album_op = (TextView)myDialog.findViewById(R.id.music_file_album_op);
            TextView music_file_foto_op = (TextView)myDialog.findViewById(R.id.music_file_foto_op);
            TextView text = (TextView)myDialog.findViewById(R.id.add_fav_txt);

            music_file_name_op.setText(file_name.getText().toString());
            music_file_artist_op.setText(artist_name.getText().toString());
            music_img_op.setImageDrawable(album_art.getDrawable());
            music_file_album_op.setText(album_name.getText().toString());
            music_file_foto_op.setText(album_foto.getText().toString());

            name = album_name.getText().toString();
            artist = artist_name.getText().toString();
            title = file_name.getText().toString();
            time = duration.getText().toString();
            prueba = album_foto.getText().toString();

            archivoJson = new ArchivoJson(mContext, filename);
            fav = archivoJson.BuscarFavorito(title);
            favorito = (Button)myDialog.findViewById(R.id.fav);
            if (fav.equals("True")){
                favorito.setBackgroundResource(R.drawable.liked);
                text.setText("Quitar de favoritos");
            }
            else {
                favorito.setBackgroundResource(R.drawable.like);
                text.setText("Agregar a favoritos");
            }
            myDialog.show();

            Button album = (Button) myDialog.findViewById(R.id.botonalbum);
            album.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    myDialog.hide();
                    Intent intento = new Intent(mContext, AlbumDetails.class);
                    intento.putExtra("albumName", name);
                    intento.putExtra("tipo", 2);
                    mContext.startActivity(intento);
                }
            });

            Button artista = (Button)myDialog.findViewById(R.id.botonartista);
            artista.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    myDialog.hide();
                    Intent intento = new Intent(mContext, AlbumDetails.class);
                    intento.putExtra("albumName", artist);
                    intento.putExtra("tipo", 3);
                    mContext.startActivity(intento);
                }
            });

            Button agregarlista = (Button)myDialog.findViewById(R.id.add_playlist);
            agregarlista.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    myDialog.hide();
                    Intent intent = new Intent(mContext, Listas.class);
                    intent.putExtra("titulo", title);
                    intent.putExtra("artista", artist);
                    intent.putExtra("tiempo", time);
                    intent.putExtra("album", name);
                    intent.putExtra("caratula", prueba);
                    mContext.startActivity(intent);
                }
            });

            favorito.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    myDialog.hide();
                    if(fav.equals("False")){
                        archivoJson = new ArchivoJson(mContext, filename);
                        archivoJson.AgregarCancion(0, title, artist, time, name, prueba);
                        Intent intent = new Intent(mContext, BibliotecaActivity.class);
                        mContext.startActivity(intent);
                    }else if(fav.equals("True")) {
                        archivoJson = new ArchivoJson(mContext, filename);
                        archivoJson.QuitarDeFavoritos(title);
                        Intent intent = new Intent(mContext, BibliotecaActivity.class);
                        mContext.startActivity(intent);
                    }else {
                        archivoJson = new ArchivoJson(mContext, filename);
                        archivoJson.AgregarCancion(0, title, artist, time, name, prueba);
                        Intent intent = new Intent(mContext, BibliotecaActivity.class);
                        mContext.startActivity(intent);
                    }
                }
            });
        }
    }



    // Anotacion por el nivel de la API 29 o superior, para obtener la imagen a traves de la uri
    // declare la propiedad
    // android:requestLegacyExternalStorage="true"
    // en el manifest, en la seccion aplication
}
