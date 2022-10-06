package com.alucard.apolo;


import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.alucard.apolo.R;

import java.util.ArrayList;
import java.util.List;

import static com.alucard.apolo.BibliotecaActivity.musicFiles;

/**
 * A simple {@link Fragment} subclass.
 */
public class ArtistasFragment extends Fragment {

    RecyclerView recyclerView;
    ArtistAdapter artistAdapter;
    ArrayList<MusicFiles> canciones = new ArrayList<>();
    List<String> nombresArtistas = new ArrayList<>();


    public ArtistasFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        //return inflater.inflate(R.layout.fragment_artistas, container, false);
        View view = inflater.inflate(R.layout.fragment_albumes, container, false);
        recyclerView = view.findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);
        for (int i=0; i< musicFiles.size();i++){
            if (!nombresArtistas.contains(musicFiles.get(i).getArtist())){
                canciones.add(musicFiles.get(i));
                nombresArtistas.add(musicFiles.get(i).getArtist());
            }
        }
        if (!(canciones.size() < 1)){
            artistAdapter = new ArtistAdapter(getContext(), canciones);
            recyclerView.setAdapter(artistAdapter);
            recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 2));
        }
        return view;
    }
}
