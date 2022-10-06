package com.alucard.apolo;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

public class DialogNombreLista extends DialogFragment {
    private static ListasDeReproduccion lista = new ListasDeReproduccion("Nombre", "0");
    private String filename = "lista.txt";
    EditText nombre;

    @Override
    public Dialog onCreateDialog(Bundle savedInstance){
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = requireActivity().getLayoutInflater();

        View MyView = inflater.inflate(R.layout.dialog_nombre_lista, null);
        nombre = (EditText)MyView.findViewById(R.id.name);
        builder.setView(MyView);
        return builder.create();
    }


}
