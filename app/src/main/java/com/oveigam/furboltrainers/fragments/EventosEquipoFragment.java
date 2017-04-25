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
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.database.ValueEventListener;
import com.oveigam.furboltrainers.R;
import com.oveigam.furboltrainers.adapterslist.EventoAdapter;
import com.oveigam.furboltrainers.entities.Evento;

import java.util.Comparator;
import java.util.Map;

/**
 * Created by Oscarina on 25/04/2017.
 */
public class EventosEquipoFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener {
    String equipoID;
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference myRef = database.getReference();

    private EventoAdapter adapter;
    private SwipeRefreshLayout swipeRefreshLayout;
    private TextView emptyText;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        adapter = new EventoAdapter(getContext());

        equipoID = getActivity().getIntent().getStringExtra("equipoID");

        View rootView = inflater.inflate(R.layout.fragment_con_lista, container, false);

        emptyText = (TextView) rootView.findViewById(R.id.empty);
        emptyText.setVisibility(View.INVISIBLE);

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

        return rootView;
    }


    @Override
    public void onRefresh() {
        swipeRefreshLayout.setRefreshing(true);
        emptyText.setVisibility(View.INVISIBLE);
        myRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                adapter.clear();

                String nombreEquipo = dataSnapshot.child("equipos").child(equipoID).child("nombre").getValue(String.class);
                String imgEquipo = dataSnapshot.child("equipos").child(equipoID).child("imgURL").getValue(String.class);

                GenericTypeIndicator<Map<String, Boolean>> t = new GenericTypeIndicator<Map<String, Boolean>>() {
                };

                Map<String, Boolean> eventosID = dataSnapshot.child("equipos").child(equipoID).child("eventos").getValue(t);
                if(eventosID!=null){
                    for(String id : eventosID.keySet()){
                        Evento evento = dataSnapshot.child("eventos").child(id).getValue(Evento.class);
                        if(evento!=null){
                            evento.setNombreEquipo(nombreEquipo);
                            evento.setImgEquipoURL(imgEquipo);
                            adapter.add(evento);
                        }else{
                            dataSnapshot.child("equipos").child(equipoID).child("eventos").child(id).getRef().removeValue();
                        }
                    }
                }else {
                    emptyText.setVisibility(View.VISIBLE);
                }
                adapter.sort(new Comparator<Evento>() {
                    @Override
                    public int compare(Evento o1, Evento o2) {
                        return o1.getFecha_hora().compareTo(o2.getFecha_hora());
                    }
                });
                adapter.notifyDataSetChanged();
                swipeRefreshLayout.setRefreshing(false);

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                swipeRefreshLayout.setRefreshing(false);
            }
        });

    }

    @Override
    public void onResume() {
        super.onResume();
        onRefresh();
    }
}
