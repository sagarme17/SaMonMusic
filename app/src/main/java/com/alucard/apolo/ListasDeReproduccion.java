package com.alucard.apolo;

public class ListasDeReproduccion {
    private String nombre;
    private String numCanciones;

    public ListasDeReproduccion(String nombre, String numCanciones){
        this.nombre = nombre;
        this.numCanciones = numCanciones;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getNumCanciones() {
        return numCanciones;
    }

    public void setNumCanciones(String numCanciones) {
        this.numCanciones = numCanciones;
    }

}
