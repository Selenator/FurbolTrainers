package com.oveigam.furboltrainers.entities;

import android.net.Uri;

import com.google.firebase.database.Exclude;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by Oscarina on 18/04/2017.
 */
public class Jugador {
    private String id;
    private String nombre;
    private String imgURL;
    private String email;
    private boolean entrenador;
    HashMap<String,Boolean> equipos;

    public Jugador(String nombre) {
        this.nombre = nombre;
    }

    public Jugador() {
    }

    public Jugador(String nombre, String imgURL, String email) {
        this.nombre = nombre;
        this.imgURL =  imgURL;
        this.email = email;
    }

    @Exclude
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
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

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    @Exclude
    public boolean isEntrenador() {
        return entrenador;
    }

    public void setEntrenador(boolean entrenador) {
        this.entrenador = entrenador;
    }

    public HashMap<String, Boolean> getEquipos() {
        return equipos;
    }

    public void setEquipos(HashMap<String, Boolean> equipos) {
        this.equipos = equipos;
    }
}
