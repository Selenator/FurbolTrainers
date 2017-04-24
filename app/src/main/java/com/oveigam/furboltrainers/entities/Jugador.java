package com.oveigam.furboltrainers.entities;

import android.net.Uri;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by Oscarina on 18/04/2017.
 */
public class Jugador {
    private String nombre;
    private String imgURL;
    HashMap<String,Boolean> equipos;

    public Jugador(String nombre) {
        this.nombre = nombre;
    }

    public Jugador() {
    }

    public Jugador(String nombre, String imgURL) {
        this.nombre = nombre;
        this.imgURL =  imgURL;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getImgURL() {
        return imgURL;
    }

    public void setImgURL(String imgURL) {
        this.imgURL = imgURL;
    }

    public HashMap<String, Boolean> getEquipos() {
        return equipos;
    }

    public void setEquipos(HashMap<String, Boolean> equipos) {
        this.equipos = equipos;
    }
}
