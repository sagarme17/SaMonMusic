package com.alucard.apolo;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.List;

public class Listas extends AppCompatActivity {

    private RecyclerView recyclerView;
    private ListAdapter listAdapter;
    private String filename = "lista.txt";
    Dialog listas;
    EditText nombre;
    Button nueva;
    String title, artist, time, name, prueba;
    ArchivoJson archivoJson;
    static int vistaCrear = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_dialog_listas);
        title = getIntent().getStringExtra("titulo");
        artist = getIntent().getStringExtra("artista");
        time = getIntent().getStringExtra("tiempo");
        name = getIntent().getStringExtra("album");
        prueba = getIntent().getStringExtra("caratula");
        getSupportActionBar().hide();
        listas = new Dialog(this);
        listas.setContentView(R.layout.dialog_nombre_lista);
        nueva = (Button)findViewById(R.id.nueva_lista_app);

        List<ListasDeReproduccion> data = new ArrayList<>();

        archivoJson = new ArchivoJson(this, filename);

        try {
            String jsonString = archivoJson.readJSON();

            JSONObject jsonObject = new JSONObject(jsonString);
            JSONArray arreglo = jsonObject.getJSONArray("lista");

            for (int i = 0; i < arreglo.length(); i++) {
                JSONObject itemObj = arreglo.getJSONObject(i);
                String name = itemObj.getString("nombre");
                String canciones = itemObj.getString("numCanciones");
                ListasDeReproduccion listasDeReproduccion = new ListasDeReproduccion(name, canciones);
                data.add(listasDeReproduccion);
            }

            recyclerView = (RecyclerView)findViewById(R.id.recyclernew);
            listAdapter = new ListAdapter(this, data, title, artist, time, name, prueba);
            recyclerView.setAdapter(listAdapter);
            recyclerView.setLayoutManager(new LinearLayoutManager(this));
        }catch (JSONException e) {
            e.printStackTrace();
        }

        nueva.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listas.show();
                nombre = (EditText)listas.findViewById(R.id.name);

                Button cancelar = (Button)listas.findViewById(R.id.cancelar);
                cancelar.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        listas.hide();
                    }
                });

                Button aceptar = (Button)listas.findViewById(R.id.aceptar);
                aceptar.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        archivoJson.crearListaCancion(nombre.getText().toString(), title, artist, time, name, prueba);
                        listas.hide();
                        finish();
                    }
                });
            }
        });
        vistaCrear = 2;
    }

    @Override
    protected void onPause(){
        super.onPause();
        vistaCrear = 0;
        finish();
    }
}
