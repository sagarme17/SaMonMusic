package com.alucard.apolo;


import android.app.Dialog;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.List;

public class ListasFragment extends Fragment {
    private RecyclerView recyclerView;
    private ListAdapter listAdapter;
    private String filename = "lista.txt";
    Dialog listas;
    Button crear;
    EditText nombre;
    FileInputStream in = null;
    ArchivoJson archivoJson;
    static int vistaLista = 0;


    public ListasFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_listas, container, false);
        listas = new Dialog(getContext());
        listas.setContentView(R.layout.dialog_nombre_lista);
        //Recuperar de Json
        List<ListasDeReproduccion> data = new ArrayList<>();

        archivoJson = new ArchivoJson(getActivity(), filename);

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

            recyclerView = (RecyclerView) view.findViewById(R.id.recyclerlistas);
            listAdapter = new ListAdapter(getContext(), data);
            recyclerView.setAdapter(listAdapter);
            recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        }catch (JSONException e) {
            System.out.println(e);
        }

        crear = (Button)view.findViewById(R.id.crear);
        crear.setOnClickListener(new View.OnClickListener() {
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
                        archivoJson.crearLista(nombre.getText().toString());
                        listas.hide();
                        FragmentManager fm = getFragmentManager();
                        FragmentTransaction ft = fm.beginTransaction();
                        ListasFragment ln = new ListasFragment();
                        ft.replace(R.id.framelistas, ln);
                        ft.commit();
                    }
                });
            }
        });
        vistaLista = 1;
        return view;
    }

    @Override
    public void onResume(){
        super.onResume();

        List<ListasDeReproduccion> data = new ArrayList<>();

        archivoJson = new ArchivoJson(getActivity(), filename);

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

            listAdapter = new ListAdapter(getContext(), data);
            recyclerView.setAdapter(listAdapter);
            recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        }catch (JSONException e) {
            System.out.println(e);
        }

    }
}
