package com.oveigam.furboltrainers.adapterslist;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.oveigam.furboltrainers.R;
import com.oveigam.furboltrainers.entities.Jugador;
import com.oveigam.furboltrainers.tools.CircleTransform;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

/**
 * Created by Oscarina on 25/04/2017.
 */
public class JugadorAdapter extends ArrayAdapter<Jugador> {

    public JugadorAdapter(@NonNull Context context) {
        super(context, 0, new ArrayList<Jugador>());
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.ficha_equipo, parent, false);
        }

        Jugador jugador = getItem(position);


        TextView nombre = (TextView) convertView.findViewById(R.id.nombre_equipo);
        nombre.setText(jugador.getNombre());

        ImageView escudo = (ImageView) convertView.findViewById(R.id.escudo_img);
        if (jugador.getImgURL() != null && !jugador.getImgURL().isEmpty()) {
            Picasso.with(getContext()).load(jugador.getImgURL()).transform(new CircleTransform()).into(escudo);
        } else {
            escudo.setImageResource(R.drawable.balon);
        }


        if(jugador.isEntrenador())
            convertView.findViewById(R.id.silbato).setVisibility(View.VISIBLE);
        else
            convertView.findViewById(R.id.silbato).setVisibility(View.GONE);

        return convertView;
    }
}
