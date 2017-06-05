package com.oveigam.furboltrainers.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.database.ValueEventListener;
import com.oveigam.furboltrainers.R;
import com.oveigam.furboltrainers.activities.EquipoActivity;
import com.oveigam.furboltrainers.activities.EquipoCrearActivity;
import com.oveigam.furboltrainers.activities.InvitarActivity;
import com.oveigam.furboltrainers.adapterslist.EquipoAdapter;
import com.oveigam.furboltrainers.adapterslist.JugadorAdapter;
import com.oveigam.furboltrainers.entities.Equipo;
import com.oveigam.furboltrainers.entities.Jugador;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Map;

/**
 * Created by Oscarina on 25/04/2017.
 */
public class JugadoresEquipoFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener {

    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference myRef = database.getReference();

    private JugadorAdapter adapter;
    private SwipeRefreshLayout swipeRefreshLayout;
    private String equipoID;
    TextView emptyText;
    private boolean entrenador;
    private String userID;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        adapter = new JugadorAdapter(getContext());

        equipoID = getActivity().getIntent().getStringExtra("equipoID");
        userID = getActivity().getIntent().getStringExtra("userID");
        setHasOptionsMenu(true);

        final View rootView = inflater.inflate(R.layout.fragment_con_lista, container, false);

        entrenador = false;
        myRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                DataSnapshot jugador = dataSnapshot.child("equipos").child(equipoID).child("jugadores").child(userID);
                if(jugador.exists())
                    entrenador = jugador.getValue(boolean.class);
                cargarBoton(rootView);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


        emptyText = (TextView) rootView.findViewById(R.id.empty);
        emptyText.setVisibility(View.INVISIBLE);

        final ListView listView = (ListView) rootView.findViewById(R.id.lista);
        listView.setAdapter(adapter);
        listView.setEmptyView(rootView.findViewById(android.R.id.empty));

        swipeRefreshLayout = (SwipeRefreshLayout) rootView.findViewById(R.id.refresh);
        swipeRefreshLayout.setOnRefreshListener(this);


        return rootView;
    }

    private void cargarBoton(View rootView) {
        //BOTON FLOTANTE DE ABAJO
        final FloatingActionButton fab = (FloatingActionButton) rootView.findViewById(R.id.fab);
        if(entrenador){
            fab.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(getContext(),InvitarActivity.class);
                    intent.putExtra("equipoID",equipoID);
                    startActivity(intent);
                }
            });
        }else{
            fab.setVisibility(View.GONE);
        }
    }


    @Override
    public void onRefresh() {
        swipeRefreshLayout.setRefreshing(true);
        emptyText.setVisibility(View.INVISIBLE);
        myRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                adapter.clear();

                GenericTypeIndicator<Map<String, Boolean>> t = new GenericTypeIndicator<Map<String, Boolean>>() {
                };
                Map<String, Boolean> jugadoresID = dataSnapshot.child("equipos").child(equipoID).child("jugadores").getValue(t);
                if (jugadoresID != null) {
                    for (String id : jugadoresID.keySet()) {
                        DataSnapshot jugEq = dataSnapshot.child("jugadores").child(id).child("equipos").child(equipoID);
                        if (jugEq.exists()) {
                            if (jugEq.getValue(Boolean.class)) {
                                Jugador jugador = dataSnapshot.child("jugadores").child(id).getValue(Jugador.class);
                                jugador.setEntrenador(true);
                                adapter.add(jugador);
                            }
                        } else {
                            dataSnapshot.child("equipos").child(equipoID).child("jugadores").child(id).getRef().removeValue();
                        }
                    }
                } else {
                    emptyText.setVisibility(View.VISIBLE);

                }
                adapter.sort(new Comparator<Jugador>() {
                    @Override
                    public int compare(Jugador o1, Jugador o2) {
                        if (o1.isEntrenador()) {
                            return 1;
                        } else if (o2.isEntrenador()) {
                            return -1;
                        } else {
                            return o1.getNombre().compareTo(o1.getNombre());
                        }
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