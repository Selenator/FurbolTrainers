package com.oveigam.furboltrainers.entities;

import com.google.firebase.database.Exclude;

import java.util.Date;

/**
 * Created by Oscarina on 19/04/2017.
 */
public class Evento {
    String tipo;
    Date fecha_hora;
    String localizacionDescripcion;
    String nombreEquipo;
    String imgEquipoURL;
    String comentario;

    public Evento() {
    }

    public Evento(String tipo, Date fecha_hora, String localizacionDescripcion, String nombreEquipo,String imgEquipoURL, String comentario) {
        this.tipo = tipo;
        this.fecha_hora = fecha_hora;
        this.localizacionDescripcion = localizacionDescripcion;
        this.nombreEquipo = nombreEquipo;
        this.imgEquipoURL = imgEquipoURL;
        this.comentario = comentario;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public Date getFecha_hora() {
        return fecha_hora;
    }

    public void setFecha_hora(Date fecha_hora) {
        this.fecha_hora = fecha_hora;
    }

    public String getLocalizacionDescripcion() {
        return localizacionDescripcion;
    }

    public void setLocalizacionDescripcion(String localizacionDescripcion) {
        this.localizacionDescripcion = localizacionDescripcion;
    }

    @Exclude
    public String getNombreEquipo() {
        return nombreEquipo;
    }

    public void setNombreEquipo(String nombreEquipo) {
        this.nombreEquipo = nombreEquipo;
    }

    @Exclude
    public String getImgEquipoURL() {
        return imgEquipoURL;
    }

    public void setImgEquipoURL(String imgEquipoURL) {
        this.imgEquipoURL = imgEquipoURL;
    }

    public String getComentario() {
        return comentario;
    }

    public void setComentario(String comentario) {
        this.comentario = comentario;
    }
}
