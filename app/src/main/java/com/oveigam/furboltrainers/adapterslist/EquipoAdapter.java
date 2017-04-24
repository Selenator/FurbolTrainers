package com.oveigam.furboltrainers.adapterslist;

import android.content.Context;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.oveigam.furboltrainers.R;
import com.oveigam.furboltrainers.entities.Equipo;
import com.oveigam.furboltrainers.entities.Evento;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Comparator;

import static com.oveigam.furboltrainers.R.drawable.balon;

/**
 * Created by Oscarina on 18/04/2017.
 */
public class EquipoAdapter extends ArrayAdapter<Equipo> {

    public EquipoAdapter(@NonNull Context context, ArrayList<Equipo> equipos) {
        super(context, 0, equipos);

    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.ficha_equipo, parent, false);
        }

        Equipo equipo = getItem(position);

        TextView nombre = (TextView) convertView.findViewById(R.id.nombre_equipo);
        nombre.setText(equipo.getNombre());

        ImageView escudo = (ImageView) convertView.findViewById(R.id.escudo_img);
        if (equipo.getImgURL() != null && !equipo.getImgURL().isEmpty()) {
            Picasso.with(getContext()).load(equipo.getImgURL()).into(escudo);
        } else {
            escudo.setImageResource(R.drawable.escudo);
        }

        return convertView;
    }
}
