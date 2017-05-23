package com.oveigam.furboltrainers.adapterslist;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.oveigam.furboltrainers.R;
import com.oveigam.furboltrainers.entities.Evento;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

/**
 * Created by Oscarina on 19/04/2017.
 */
public class EventoAdapter extends ArrayAdapter<Evento> implements OnMapReadyCallback {

    private Bundle savedInstanceState;
    private int currentPosition = -1;

    public EventoAdapter(@NonNull Context context,Bundle savedInstanceState) {
        super(context, 0, new ArrayList<Evento>());
        this.savedInstanceState = savedInstanceState;
    }



    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.ficha_evento, parent, false);
        }

        Evento evento = getItem(position);

        TextView tipo = (TextView) convertView.findViewById(R.id.evento_tipo);
        TextView nombreEq = (TextView) convertView.findViewById(R.id.evento_nombre_equipo);
        TextView fecha = (TextView) convertView.findViewById(R.id.evento_fecha);
        TextView hora = (TextView) convertView.findViewById(R.id.evento_hora);
        TextView localizacion = (TextView) convertView.findViewById(R.id.evento_localizacion);

        MapView mapView = (MapView) convertView.findViewById(R.id.mapview);
        mapView.onCreate(savedInstanceState);

        tipo.setText(evento.getTipo());
        localizacion.setText(evento.getLocalizacionDescripcion());
        nombreEq.setText(evento.getNombreEquipo());

        Date cuando = evento.getFecha_hora();
        fecha.setText(new SimpleDateFormat("dd/MM/yy").format(cuando));
        hora.setText(new SimpleDateFormat("HH:mm").format(cuando));


        ImageView escudo = (ImageView) convertView.findViewById(R.id.icono_evento_escudo);
        if (evento.getImgEquipoURL() != null && !evento.getImgEquipoURL().isEmpty()) {
            Picasso.with(getContext()).load(evento.getImgEquipoURL()).into(escudo);
        } else {
            escudo.setImageDrawable(null);
        }
        escudo.setImageAlpha(125);

        ImageView icono = (ImageView) convertView.findViewById(R.id.icono_evento);
        switch (evento.getTipo()) {
            case "Partido":
                icono.setImageResource(R.drawable.ic_partido);
                break;
            case "Entrenamiento":
                icono.setImageResource(R.drawable.ic_train);
                break;
            default:
                icono.setImageResource(R.drawable.ic_team);
                break;
        }


        return convertView;
    }

    public boolean expandItem(int position, View view){
        Evento e = getItem(position);
        currentPosition = position;

        //if((e.getComentario() == null || e.getComentario().isEmpty()) && (e.getLatitude() == 0 && e.getLatitude() == 0)) return false;

        //View view = getViewByPosition(position,listView);

        if(!(e.getComentario() == null || e.getComentario().isEmpty())){
            TextView comentario = ((TextView)view.findViewById(R.id.comentario_evento));
            comentario.setText(e.getComentario());
            comentario.setVisibility(View.VISIBLE);
        }

        if(!(e.getLatitude() == 0 && e.getLatitude() == 0)){
            MapView map = ((MapView)view.findViewById(R.id.mapview));
            map.onResume();
            map.getMapAsync(this);

            map.setVisibility(View.VISIBLE);
        }

        if(e.isEditable()){
            view.findViewById(R.id.botones_opciones).setVisibility(View.VISIBLE);
        }

        LinearLayout expandible = (LinearLayout) view.findViewById(R.id.expandible);
        expandible.setVisibility(View.VISIBLE);

        return  true;

    }

    public void collpaseItem(View view){
        view.findViewById(R.id.comentario_evento).setVisibility(View.GONE);
        view.findViewById(R.id.mapview).setVisibility(View.GONE);
        view.findViewById(R.id.botones_opciones).setVisibility(View.GONE);
        view.findViewById(R.id.expandible).setVisibility(View.GONE);
        currentPosition = -1;
    }

    private View getViewByPosition(int pos, ListView listView) {
        final int firstListItemPosition = listView.getFirstVisiblePosition();
        final int lastListItemPosition = firstListItemPosition + listView.getChildCount() - 1;

        if (pos < firstListItemPosition || pos > lastListItemPosition ) {
            return listView.getAdapter().getView(pos, null, listView);
        } else {
            final int childIndex = pos - firstListItemPosition;
            return listView.getChildAt(childIndex);
        }
    }

    public void collapseCurrent(ListView listView){
        if(currentPosition < 0) return;
        View view = getViewByPosition(currentPosition,listView);
        collpaseItem(view);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        Evento e = getItem(currentPosition);
        LatLng coords = e.getCoordenadas();
        googleMap.addMarker(new MarkerOptions().position(coords).title("Más opciones")).showInfoWindow();
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(coords, 15));
        googleMap.getUiSettings().setScrollGesturesEnabled(false);
    }


    public boolean isExpanded(){
        return currentPosition > -1;
    }



}
