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

public class ListaDetailsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private Context context;
    private LayoutInflater inflater;
    Dialog myDialog;
    static ArrayList<MusicFiles> listFiles;
    MusicFiles current;
    int currentPos = 0;
    String name, artist, title, time, prueba;
    private String filename = "lista.txt";
    ArchivoJson archivoJson;
    Button favoritoD;
    String favD;

    public ListaDetailsAdapter(Context context, ArrayList<MusicFiles> listFiles) {
        this.context = context;
        inflater = LayoutInflater.from(context);
        this.listFiles = listFiles;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.music_items, parent, false);
        MyHolder holder = new MyHolder(view);
        myDialog = new Dialog(context);
        myDialog.setContentView(R.layout.dialog_cancionesmenu);
        return holder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        MyHolder myHolder = (MyHolder) holder;
        current = listFiles.get(position);
        myHolder.file_name.setText(current.getTitle());
        myHolder.artist_name.setText(current.getArtist());
        myHolder.album_name.setText(current.getAlbum());
        myHolder.duration.setText(current.getDuration());
        myHolder.album_foto.setText(current.getPath());
        byte[] image = getAlbumArt(current.getPath());
        if(image != null){
            Glide.with(context).asBitmap().load(image).into(myHolder.album_art);
        }else{
            Glide.with(context).asBitmap().load(R.drawable.no_cover).into(myHolder.album_art);
        }
        //Generar intento pata el reproductor
        myHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                reproduccion = false;
                Intent intent = new Intent(context, MusicPlayActivity.class);
                intent.putExtra("position", position);
                intent.putExtra("sender", "listDetails");
                context.startActivity(intent);
            }
        });

        myHolder.setOnClickListeners();
    }

    @Override
    public int getItemCount() {
        return listFiles.size();
    }

    private byte[] getAlbumArt(String uri){
        MediaMetadataRetriever retriever = new MediaMetadataRetriever();
        retriever.setDataSource(uri);
        byte[] art = retriever.getEmbeddedPicture();
        retriever.release();
        return art;
    }


    class MyHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView file_name, artist_name, album_name, duration, album_foto;
        ImageView album_art;
        ImageButton menu;

        public MyHolder(@NonNull View itemView) {
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
            TextView music_file_album_op = (TextView) myDialog.findViewById(R.id.music_file_album_op);
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

            archivoJson = new ArchivoJson(context, filename);
            favD = archivoJson.BuscarFavorito(file_name.getText().toString());
            favoritoD = (Button)myDialog.findViewById(R.id.fav);
            if (favD.equals("True")){
                favoritoD.setBackgroundResource(R.drawable.liked);
                text.setText("Quitar de favoritos");
            }
            else {
                favoritoD.setBackgroundResource(R.drawable.like);
                text.setText("Agregar a favoritos");
            }

            myDialog.show();

            Button album = (Button) myDialog.findViewById(R.id.botonalbum);
            album.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    myDialog.hide();
                    String name = album_name.getText().toString();
                    Intent intento = new Intent(context, AlbumDetails.class);
                    intento.putExtra("albumName", name);
                    intento.putExtra("tipo", 2);
                    context.startActivity(intento);
                    //((BibliotecaActivity)mContext).getViewPager().setCurrentItem(2);

                }
            });

            Button artista = (Button) myDialog.findViewById(R.id.botonartista);
            artista.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    myDialog.hide();
                    String artista = artist_name.getText().toString();
                    Intent intento = new Intent(context, AlbumDetails.class);
                    intento.putExtra("albumName", artista);
                    intento.putExtra("tipo", 3);
                    context.startActivity(intento);
                }
            });

            Button agregarlista = (Button) myDialog.findViewById(R.id.add_playlist);
            agregarlista.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    myDialog.hide();
                    Intent intent = new Intent(context, Listas.class);
                    intent.putExtra("titulo", title);
                    intent.putExtra("artista", artist);
                    intent.putExtra("tiempo", time);
                    intent.putExtra("album", name);
                    intent.putExtra("caratula", prueba);
                    context.startActivity(intent);
                }
            });

            favoritoD.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    myDialog.hide();
                    if(favD.equals("False")){
                        archivoJson = new ArchivoJson(context, filename);
                        archivoJson.AgregarCancion(0, title, artist, time, name, prueba);
                        Intent intent = new Intent(context, ListaDetailsAdapter.class);
                        context.startActivity(intent);
                    }else if(favD.equals("True")) {
                        archivoJson = new ArchivoJson(context, filename);
                        archivoJson.QuitarDeFavoritos(title);
                        Intent intent = new Intent(context, ListaDetailsAdapter.class);
                        context.startActivity(intent);
                    }else {
                        archivoJson = new ArchivoJson(context, filename);
                        archivoJson.AgregarCancion(0, title, artist, time, name, prueba);
                        Intent intent = new Intent(context, ListaDetailsAdapter.class);
                        context.startActivity(intent);
                    }
                }
            });
        }
    }
}
