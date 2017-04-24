package com.oveigam.furboltrainers.entities;

import com.google.firebase.database.Exclude;

import java.util.HashMap;

/**
 * Created by Oscarina on 18/04/2017.
 */
public class Equipo {
    String nombre;
    String imgURL;
    HashMap<String,Jugador> jugadores;

    public Equipo() {
    }

    public Equipo(String nombre, String imgURL) {
        this.nombre = nombre;
        this.imgURL = imgURL;
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
