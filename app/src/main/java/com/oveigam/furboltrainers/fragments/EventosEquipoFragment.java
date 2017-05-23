package com.oveigam.furboltrainers.fragments;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.database.ValueEventListener;
import com.oveigam.furboltrainers.R;
import com.oveigam.furboltrainers.activities.EquipoCrearActivity;
import com.oveigam.furboltrainers.activities.EventoCrearActivity;
import com.oveigam.furboltrainers.activities.EventoEditarActivity;
import com.oveigam.furboltrainers.adapterslist.EventoAdapter;
import com.oveigam.furboltrainers.entities.Evento;

import java.util.Comparator;
import java.util.Date;
import java.util.Map;

/**
 * Created by Oscarina on 25/04/2017.
 */
public class EventosEquipoFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener {
    String equipoID;
    String userID;
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference myRef = database.getReference();

    private EventoAdapter adapter;
    private SwipeRefreshLayout swipeRefreshLayout;
    private TextView emptyText;
    ListView listView;
    boolean entrenador;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        adapter = new EventoAdapter(getContext(), savedInstanceState);

        equipoID = getActivity().getIntent().getStringExtra("equipoID");
        userID = getActivity().getIntent().getStringExtra("userID");

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

        listView = (ListView) rootView.findViewById(R.id.lista);
        listView.setAdapter(adapter);
        listView.setEmptyView(rootView.findViewById(android.R.id.empty));

        listView.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                adapter.collapseCurrent(listView);
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {

            }
        });

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
                EventoAdapter adapter = ((EventoAdapter) listView.getAdapter());
                if (view.findViewById(R.id.expandible).getVisibility() == View.VISIBLE) {
                    adapter.collpaseItem(view);
                } else {
                    adapter.collapseCurrent(listView);
                    if (adapter.expandItem(position, view)) {
                        view.findViewById(R.id.editar_but).setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                editarEvento(position);
                            }
                        });
                        view.findViewById(R.id.eliminar_but).setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                eliminarEvento(position);
                            }
                        });
                        listView.setSelection(position);
                        listView.smoothScrollToPosition(position);
                    }
                }
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

    private void cargarBoton(View rootView) {
        //BOTON FLOTANTE DE ABAJO
        final FloatingActionButton fab = (FloatingActionButton) rootView.findViewById(R.id.fab);
        if(entrenador){
            fab.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    crearEvento();
                }
            });
        }else{
            fab.setVisibility(View.GONE);
        }
    }


    @Override
    public void onRefresh() {
        swipeRefreshLayout.setRefreshing(true);
        ((EventoAdapter) listView.getAdapter()).collapseCurrent(listView);
        emptyText.setVisibility(View.INVISIBLE);

        final Date ahoraDate = new Date();
        ahoraDate.setHours(ahoraDate.getHours() - 3);
        myRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                adapter.clear();

                boolean entrenador = dataSnapshot.child("equipos").child(equipoID).child("jugadores").child(userID).getValue(boolean.class);
                String nombreEquipo = dataSnapshot.child("equipos").child(equipoID).child("nombre").getValue(String.class);
                String imgEquipo = dataSnapshot.child("equipos").child(equipoID).child("imgURL").getValue(String.class);

                GenericTypeIndicator<Map<String, Boolean>> t = new GenericTypeIndicator<Map<String, Boolean>>() {
                };

                Map<String, Boolean> eventosID = dataSnapshot.child("equipos").child(equipoID).child("eventos").getValue(t);
                if (eventosID != null) {
                    for (String id : eventosID.keySet()) {
                        Evento evento = dataSnapshot.child("eventos").child(id).getValue(Evento.class);
                        if (evento != null) {
                            evento.setNombreEquipo(nombreEquipo);
                            evento.setImgEquipoURL(imgEquipo);
                            evento.setEditable(entrenador);
                            evento.setId(id);
                            if (evento.getFecha_hora_menos1900().before(ahoraDate)) { //si el evento esta en el pasado se borra
                                dataSnapshot.child("eventos").child(id).getRef().removeValue();
                            } else {
                                adapter.add(evento);
                            }
                        } else {
                            dataSnapshot.child("equipos").child(equipoID).child("eventos").child(id).getRef().removeValue();
                        }
                    }
                } else {
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


    private void crearEvento() {
        Intent intent = new Intent(getContext(), EventoCrearActivity.class);
        intent.putExtra("equipoID", equipoID);
        startActivity(intent);
    }

    @Override
    public void onResume() {
        super.onResume();
        onRefresh();
    }

    public void editarEvento(int position) {
        Evento e = (Evento) listView.getAdapter().getItem(position);
        Intent intent = new Intent(getContext(), EventoEditarActivity.class);
        intent.putExtra("evento", e);
        startActivity(intent);
    }

    public void eliminarEvento(int position) {
        final Evento e = (Evento) listView.getAdapter().getItem(position);
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());

        builder.setMessage("Seguro que quieres eliminar?")
                .setPositiveButton("Si", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        eliminar(e);
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                })
                .show();
    }

    private void eliminar(Evento e) {
        myRef.child("eventos").child(e.getId()).getRef().removeValue();
        myRef.child("equipos").child(equipoID).child("eventos").child(e.getId()).getRef().removeValue();
        adapter.remove(e);
        adapter.notifyDataSetChanged();
    }


}
