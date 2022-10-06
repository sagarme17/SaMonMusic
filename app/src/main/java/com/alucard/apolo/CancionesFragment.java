package com.alucard.apolo;


import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.alucard.apolo.MusicAdapter;
import com.alucard.apolo.R;

import static com.alucard.apolo.BibliotecaActivity.musicFiles;

/**
 * A simple {@link Fragment} subclass.
 */
public class CancionesFragment extends Fragment {

    RecyclerView recyclerView;
    MusicAdapter musicAdapter;

    public CancionesFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        //return inflater.inflate(R.layout.fragment_canciones, container, false);
        View view = inflater.inflate(R.layout.fragment_canciones, container, false);
        recyclerView = view.findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);
        if (!(musicFiles.size() < 1)){
            musicAdapter = new MusicAdapter(getContext(), musicFiles);
            recyclerView.setAdapter(musicAdapter);
            recyclerView.setLayoutManager(new LinearLayoutManager(getContext(), RecyclerView.VERTICAL, false));
        }
        return view;
    }
}
