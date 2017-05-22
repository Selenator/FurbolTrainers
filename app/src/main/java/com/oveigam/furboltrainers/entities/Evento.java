package com.oveigam.furboltrainers.entities;

import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.database.Exclude;

import java.util.Date;

/**
 * Created by Oscarina on 19/04/2017.
 */
public class Evento {
    String id;
    String tipo;
    Date fecha_hora;
    String localizacionDescripcion;
    String nombreEquipo;
    String imgEquipoURL;
    String comentario;
    double latitude;
    double longitude;

    public Evento() {
    }

    public Evento(String tipo, Date fecha_hora, String localizacionDescripcion, LatLng coordenadas, String nombreEquipo,String imgEquipoURL, String comentario) {
        this.tipo = tipo;
        this.fecha_hora = fecha_hora;
        this.localizacionDescripcion = localizacionDescripcion;
        latitude = coordenadas.latitude;
        longitude = coordenadas.longitude;
        this.nombreEquipo = nombreEquipo;
        this.imgEquipoURL = imgEquipoURL;
        this.comentario = comentario;
    }

    public Evento(String tipo, Date fecha_hora, String localizacionDescripcion,LatLng coordenadas, String comentario) {
        this.tipo = tipo;
        this.fecha_hora = fecha_hora;
        this.localizacionDescripcion = localizacionDescripcion;
        this.comentario = comentario;
        latitude = coordenadas.latitude;
        longitude = coordenadas.longitude;
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
    public LatLng getCoordenadas() {
        return new LatLng(latitude,longitude);
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
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

    @Exclude
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getComentario() {
        return comentario;
    }

    public void setComentario(String comentario) {
        this.comentario = comentario;
    }
}
