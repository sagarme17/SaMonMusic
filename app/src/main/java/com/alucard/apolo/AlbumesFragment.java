package com.alucard.apolo;


import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
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
public class AlbumesFragment extends Fragment {

    RecyclerView recyclerView;
    AlbumAdapter albumAdapter;
    ArrayList<MusicFiles> canciones = new ArrayList<>();
    List<String> nombresAlbumes = new ArrayList<>();


    public AlbumesFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        //return inflater.inflate(R.layout.fragment_canciones, container, false);
        View view = inflater.inflate(R.layout.fragment_albumes, container, false);
        recyclerView = view.findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);
        for (int i=0; i< musicFiles.size();i++){
            if (!nombresAlbumes.contains(musicFiles.get(i).getAlbum())){
                canciones.add(musicFiles.get(i));
                nombresAlbumes.add(musicFiles.get(i).getAlbum());
            }
        }
        if (!(canciones.size() < 1)){
            albumAdapter = new AlbumAdapter(getContext(), canciones);
            recyclerView.setAdapter(albumAdapter);
            recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 2));
        }
        return view;
    }

}
