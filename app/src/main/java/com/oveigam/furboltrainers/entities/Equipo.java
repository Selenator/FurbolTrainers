package com.oveigam.furboltrainers.entities;

import com.google.firebase.database.Exclude;

import java.util.HashMap;

/**
 * Created by Oscarina on 18/04/2017.
 */
public class Equipo {
    private String id;
    private String nombre;
    private String imgURL;
    private HashMap<String,Jugador> jugadores;

    public Equipo() {
    }

    public Equipo(String id, String nombre, String imgURL) {
        this.id = id;
        this.nombre = nombre;
        this.imgURL = imgURL;
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

    @Exclude
    public HashMap<String, Jugador> getJugadores() {
        return jugadores;
    }

    public void setJugadores(HashMap<String, Jugador> jugadores) {
        this.jugadores = jugadores;
    }
}
