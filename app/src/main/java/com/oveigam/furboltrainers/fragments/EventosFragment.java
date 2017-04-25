package com.oveigam.furboltrainers.fragments;

import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.database.ValueEventListener;
import com.oveigam.furboltrainers.R;
import com.oveigam.furboltrainers.adapterslist.EventoAdapter;
import com.oveigam.furboltrainers.entities.Equipo;
import com.oveigam.furboltrainers.entities.Evento;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.Map;

/**
 * Created by Oscarina on 17/04/2017.
 */
public class EventosFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener {
    String userID = FirebaseAuth.getInstance().getCurrentUser().getUid();
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference myRef = database.getReference();

    EventoAdapter adapter;

    SwipeRefreshLayout swipeRefreshLayout;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        adapter = new EventoAdapter(getContext());

        View rootView = inflater.inflate(R.layout.fragment_con_lista, container, false);
        ListView listView = (ListView) rootView.findViewById(R.id.lista);
        listView.setAdapter(adapter);
        listView.setEmptyView(rootView.findViewById(android.R.id.empty));

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //TODO abrir actividad con detalles del evento
            }
        });


        swipeRefreshLayout = (SwipeRefreshLayout) rootView.findViewById(R.id.refresh);
        swipeRefreshLayout.setOnRefreshListener(this);
        swipeRefreshLayout.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        onRefresh();
                                    }
                                }
        );

//        adapter.add(new Evento("Partido", new Date(), "Coliseo", "Osasuna", "Vamos campeones"));
//        adapter.add(new Evento("Otros", new Date(), "Coliseo", "Osasuna", "Vamos campeones"));
//        adapter.add(new Evento("Entrenamiento", new Date(), "Coliseo", "Osasuna", "We put the bad in the past now we alright"));
//        adapter.add(new Evento("Entrenamiento", new Date(), "Coliseo", "Osasuna", "F. F. F. We don't need them, only thing they godd is leaving"));
//        adapter.add(new Evento("Partido", new Date(), "Coliseo", "Osasuna", "Guala Guala"));
//        adapter.notifyDataSetChanged();


        return rootView;
    }

    @Override
    public void onRefresh() {
        swipeRefreshLayout.setRefreshing(true);
//        for(int i = 0; i<adapter.getCount(); i++){
//            myRef.child("eventos").push().setValue(adapter.getItem(i));
//
//        }
//        swipeRefreshLayout.setRefreshing(false);

        myRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                adapter.clear();
                //GenericType que permite extraer maps de firebase
                GenericTypeIndicator<Map<String, Boolean>> t = new GenericTypeIndicator<Map<String, Boolean>>() {
                };
                //Se accede al jugador con el id de usuario, y se guardan en un hashmap su codigos de equipo (que vinen acompañados de un boolean que usare para indicar
                //si esta pendiente de aceptar invitación o no)
                Map<String, Boolean> equiposID = dataSnapshot.child("jugadores").child(userID).child("equipos").getValue(t);
                if (equiposID != null) { //se comprueba que no sea nulo, si fuese nulo el jugador no tendria ningun equipo y no se hace nada
                    for (Map.Entry<String, Boolean> entry : equiposID.entrySet()) { //se recorren las claves de los equipos y sus valores
                        if (entry.getValue()) {//si el valor es true el jugador esta dentro del equipo y se procede, si no esta pendiente de invitacion
                            //se cogen los id de los eventos y se añaden a una lista(los valores boolean se ingnoran ya que no se usan para nada)
                            String nombreEquipo = dataSnapshot.child("equipos").child(entry.getKey()).child("nombre").getValue(String.class);
                            String imgEquipo = dataSnapshot.child("equipos").child(entry.getKey()).child("imgURL").getValue(String.class);
                            Map<String, Boolean> eventosID = dataSnapshot.child("equipos").child(entry.getKey()).child("eventos").getValue(t);
                            if (eventosID != null) {
                                for (String id : eventosID.keySet()) {
                                    Evento evento = dataSnapshot.child("eventos").child(id).getValue(Evento.class);
                                    if (evento != null) {
                                        evento.setNombreEquipo(nombreEquipo);
                                        evento.setImgEquipoURL(imgEquipo);
                                        adapter.add(evento);
                                    } else {
                                        dataSnapshot.child("equipos").child(entry.getKey()).child("eventos").child(id).getRef().removeValue();
                                    }
                                }
                            }
                        }
                    }
                    adapter.sort(new Comparator<Evento>() {
                        @Override
                        public int compare(Evento o1, Evento o2) {
                            return o1.getFecha_hora().compareTo(o2.getFecha_hora());
                        }
                    });
                    adapter.notifyDataSetChanged();
                } else {
                    Snackbar.make(getView(), "No tienes ningun evento previsto.", Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();
                }
                swipeRefreshLayout.setRefreshing(false);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                swipeRefreshLayout.setRefreshing(false);
            }
        });


    }
}
