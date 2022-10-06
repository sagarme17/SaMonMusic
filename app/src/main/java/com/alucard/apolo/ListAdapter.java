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

import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.Collections;
import java.util.List;

import static com.alucard.apolo.Listas.vistaCrear;
import static com.alucard.apolo.ListasFragment.vistaLista;

public class ListAdapter  extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private Context context;
    private LayoutInflater inflater;
    List<ListasDeReproduccion> data = Collections.emptyList();
    ListasDeReproduccion current;
    int currentPos = 0;
    private String filename = "lista.txt";
    String nombreCancion, nombreArtista, tiempoDuracion, nombreAlbum, prueba;
    ArchivoJson archivoJson;
    Dialog dialog, nombre, eliminar;
    String numero, foto, foto2, foto3, foto4;
    int num;

    public ListAdapter(Context context, List<ListasDeReproduccion> data){
        this.context = context;
        inflater = LayoutInflater.from(context);
        this.data = data;
    }

    public ListAdapter(Context context, List<ListasDeReproduccion> data, String nombreCancion,
                       String nombreArtista, String tiempoDuracion, String nombreAlbum, String prueba){
        this.context = context;
        inflater = LayoutInflater.from(context);
        this.data = data;
        this.nombreCancion = nombreCancion;
        this.nombreArtista = nombreArtista;
        this.tiempoDuracion = tiempoDuracion;
        this.nombreAlbum = nombreAlbum;
        this.prueba = prueba;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType){
        View view = inflater.inflate(R.layout.list_items, parent, false);
        MyHolder holder = new MyHolder(view);
        dialog = new Dialog(context);
        dialog.setContentView(R.layout.dialog_listasmenu);
        nombre = new Dialog(context);
        nombre.setContentView(R.layout.dialog_nuevonombre);
        eliminar = new Dialog(context);
        eliminar.setContentView(R.layout.dialog_eliminar);
        return  holder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder  holder, final int position){

        final MyHolder myHolder = (MyHolder)holder;
        final ListasDeReproduccion current = data.get(position);
        myHolder.name.setText(current.getNombre());
        myHolder.num.setText(current.getNumCanciones() + " canciones");
        numero = Integer.toString(position);
        num =  Integer.parseInt(current.getNumCanciones());
        myHolder.id.setText(numero);
        if(num == 1 || num == 2 || num == 3)
        {
            archivoJson = new ArchivoJson(context, filename);
            foto = archivoJson.obtenerUna(position);
            foto2 = "null";
            foto3 = "null";
            foto4 = "null";
            myHolder.img_pt1.setVisibility(View.INVISIBLE);
            myHolder.img_pt2.setVisibility(View.INVISIBLE);
            myHolder.img_pt3.setVisibility(View.INVISIBLE);
            myHolder.img_pt4.setVisibility(View.INVISIBLE);
            myHolder.list_img.setVisibility(View.VISIBLE);
            byte[] image = getAlbumArt(foto);
            Glide.with(context).asBitmap().load(image).into(myHolder.list_img);
        }else if(num >= 4)
        {
            archivoJson = new ArchivoJson(context, filename);
            foto = archivoJson.obtenerUna(position);
            foto2 = archivoJson.obtenerDos(position);
            foto3 = archivoJson.obtenerTres(position);
            foto4 = archivoJson.obtenerCuatro(position);
            myHolder.img_pt1.setVisibility(View.VISIBLE);
            myHolder.img_pt2.setVisibility(View.VISIBLE);
            myHolder.img_pt3.setVisibility(View.VISIBLE);
            myHolder.img_pt4.setVisibility(View.VISIBLE);
            myHolder.list_img.setVisibility(View.INVISIBLE);

            byte[] image = getAlbumArt(foto);
            Glide.with(context).asBitmap().load(image).into(myHolder.img_pt1);
            byte[] image2 = getAlbumArt(foto2);
            Glide.with(context).asBitmap().load(image2).into(myHolder.img_pt2);
            byte[] image3 = getAlbumArt(foto3);
            Glide.with(context).asBitmap().load(image3).into(myHolder.img_pt3);
            byte[] image4 = getAlbumArt(foto4);
            Glide.with(context).asBitmap().load(image4).into(myHolder.img_pt4);
        }

        if(vistaCrear == 0 && vistaLista == 1){
            myHolder.menu_list.setVisibility(View.VISIBLE);
            myHolder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(context, ListaDetails.class);
                    intent.putExtra("listName", current.getNombre());
                    intent.putExtra("songs", Integer.parseInt(current.getNumCanciones()));
                    intent.putExtra("posicion", position);
                    context.startActivity(intent);
                }
            });
        }
        else if(vistaCrear == 2 && vistaLista == 1){
            myHolder.menu_list.setVisibility(View.INVISIBLE);
            myHolder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    archivoJson = new ArchivoJson(context, filename);
                    archivoJson.AgregarCancion(position, nombreCancion, nombreArtista, tiempoDuracion, nombreAlbum, prueba);
                    ((Listas)context).finish();
                }
            });
        }
        myHolder.setOnClickListeners();
    }

    @Override
    public int getItemCount(){
        return data.size();
    }

    private byte[] getAlbumArt(String uri){
        MediaMetadataRetriever retriever = new MediaMetadataRetriever();
        retriever.setDataSource(uri);
        byte[] art = retriever.getEmbeddedPicture();
        retriever.release();
        return art;
    }

    class MyHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        TextView name, nuevonombre;
        TextView num;
        ImageButton menu_list;
        TextView id;
        ImageView list_img, img_pt1, img_pt2, img_pt3, img_pt4;

        public MyHolder(View itemView){
            super(itemView);
            name = (TextView)itemView.findViewById(R.id.list_file_name);
            num = (TextView)itemView.findViewById(R.id.num_songs);
            menu_list = (ImageButton)itemView.findViewById(R.id.menu_list);
            id = (TextView)itemView.findViewById(R.id.idItem);
            list_img = (ImageView)itemView.findViewById(R.id.list_img_uno);
            img_pt1 = (ImageView)itemView.findViewById(R.id.img_pt1);
            img_pt2 = (ImageView)itemView.findViewById(R.id.img_pt2);
            img_pt3 = (ImageView)itemView.findViewById(R.id.img_pt3);
            img_pt4 = (ImageView)itemView.findViewById(R.id.img_pt4);
        }

        void setOnClickListeners(){menu_list.setOnClickListener(this);}

        public  void onClick(View v){
            ImageView list_img_uno = (ImageView)dialog.findViewById(R.id.list_img_uno);
            final TextView list_name = (TextView)dialog.findViewById(R.id.list_name);
            TextView list_songs = (TextView)dialog.findViewById(R.id.list_songs);
            final TextView id2 = (TextView) dialog.findViewById(R.id.idItem2);
            ImageView list_img2 = (ImageView)dialog.findViewById(R.id.list_img2);
            ImageView list_img3 = (ImageView)dialog.findViewById(R.id.list_img3);
            ImageView list_img4 = (ImageView)dialog.findViewById(R.id.list_img4);
            ImageView list_img5 = (ImageView)dialog.findViewById(R.id.list_img5);

            list_name.setText(name.getText().toString());
            list_songs.setText(num.getText().toString());
            id2.setText(id.getText().toString());
            char aux = (num.getText().toString()).charAt(0);
            int auxnum = Character.getNumericValue(aux);
            if(auxnum == 1 || auxnum == 2 || auxnum == 3)
            {
                list_img_uno.setVisibility(View.VISIBLE);
                list_img2.setVisibility(View.INVISIBLE);
                list_img3.setVisibility(View.INVISIBLE);
                list_img4.setVisibility(View.INVISIBLE);
                list_img5.setVisibility(View.INVISIBLE);
                list_img_uno.setImageDrawable(list_img.getDrawable());
            }else if(auxnum >= 4)
            {
                list_img.setVisibility(View.INVISIBLE);
                list_img2.setVisibility(View.VISIBLE);
                list_img3.setVisibility(View.VISIBLE);
                list_img4.setVisibility(View.VISIBLE);
                list_img5.setVisibility(View.VISIBLE);
                list_img2.setImageDrawable(img_pt1.getDrawable());
                list_img3.setImageDrawable(img_pt2.getDrawable());
                list_img4.setImageDrawable(img_pt3.getDrawable());
                list_img5.setImageDrawable(img_pt4.getDrawable());
            }else if(auxnum == 0){
                list_img.setVisibility(View.VISIBLE);
                list_img2.setVisibility(View.INVISIBLE);
                list_img3.setVisibility(View.INVISIBLE);
                list_img4.setVisibility(View.INVISIBLE);
                list_img5.setVisibility(View.INVISIBLE);
            }

            nuevonombre = (TextView)nombre.findViewById(R.id.newname);
            nuevonombre.setText(name.getText().toString());

            dialog.show();

            Button edit = (Button)dialog.findViewById(R.id.edit_list);
            edit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.hide();
                    nombre.show();
                    Button cancelarnuevo = (Button)nombre.findViewById(R.id.cancelarnuevo);
                    cancelarnuevo.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            nombre.hide();
                        }
                    });

                    Button aceptarnuevo = (Button)nombre.findViewById(R.id.aceptarnuevo);
                    aceptarnuevo.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            nombre.hide();
                            int nuevoValor = Integer.parseInt(id2.getText().toString());
                            archivoJson = new ArchivoJson(context, filename);
                            archivoJson.ModificarNombre(nuevoValor, nuevonombre.getText().toString());
                            FragmentManager fm = ((BibliotecaActivity)context).getSupportFragmentManager();
                            FragmentTransaction ft = fm.beginTransaction();
                            ListasFragment cf = new ListasFragment();
                            ft.replace(R.id.framelistas, cf);
                            ft.commit();
                        }
                    });
                }
            });

            Button quitar = (Button)dialog.findViewById(R.id.quitar);
            quitar.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.hide();
                    eliminar.show();

                    Button eliminaAcepta = (Button)eliminar.findViewById(R.id.elimina_acepta);
                    eliminaAcepta.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            eliminar.hide();
                            int nuevoValor = Integer.parseInt(id2.getText().toString());
                            archivoJson = new ArchivoJson(context, filename);
                            archivoJson.EliminarLista(nuevoValor);
                            FragmentManager fm = ((BibliotecaActivity)context).getSupportFragmentManager();
                            FragmentTransaction ft = fm.beginTransaction();
                            ListasFragment cf = new ListasFragment();
                            ft.replace(R.id.framelistas, cf);
                            ft.commit();
                        }
                    });

                    Button eliminaCancela = (Button)eliminar.findViewById(R.id.eliminaCancela);
                    eliminaCancela.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            eliminar.hide();
                        }
                    });
                }
            });
        }
    }
}
