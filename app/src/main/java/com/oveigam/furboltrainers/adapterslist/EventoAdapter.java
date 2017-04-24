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
import com.oveigam.furboltrainers.entities.Evento;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

/**
 * Created by Oscarina on 19/04/2017.
 */
public class EventoAdapter extends ArrayAdapter<Evento> {

    public EventoAdapter(@NonNull Context context) {
        super(context, 0, new ArrayList<Evento>());

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
}
