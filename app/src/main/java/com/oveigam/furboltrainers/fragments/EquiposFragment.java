package com.oveigam.furboltrainers.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.database.ValueEventListener;
import com.oveigam.furboltrainers.R;
import com.oveigam.furboltrainers.adapterslist.EquipoAdapter;
import com.oveigam.furboltrainers.entities.Equipo;
import com.oveigam.furboltrainers.entities.Jugador;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Created by Oscarina on 17/04/2017.
 */
public class EquiposFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener {
    String userID = FirebaseAuth.getInstance().getCurrentUser().getUid();
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference myRef = database.getReference();

    EquipoAdapter adapter;

    SwipeRefreshLayout swipeRefreshLayout;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        adapter = new EquipoAdapter(getContext(), new ArrayList<Equipo>());

        View rootView = inflater.inflate(R.layout.fragment_equipos, container, false);
        ListView listView = (ListView) rootView.findViewById(R.id.lista_equipos);
        listView.setAdapter(adapter);
        listView.setEmptyView(rootView.findViewById(android.R.id.empty));

        swipeRefreshLayout = (SwipeRefreshLayout) rootView.findViewById(R.id.refresh_equipos);
        swipeRefreshLayout.setOnRefreshListener(this);
//        swipeRefreshLayout.post(new Runnable() {
//                                    @Override
//                                    public void run() {
//                                        onRefresh();
//                                    }
//                                }
//        );


        return rootView;
    }

    @Override
    public void onRefresh() {
        swipeRefreshLayout.setRefreshing(true);
        myRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                adapter.clear();
                GenericTypeIndicator<Map<String, Boolean>> t = new GenericTypeIndicator<Map<String, Boolean>>() {
                };
                Map<String, Boolean> equiposID = dataSnapshot.child("jugadores").child(userID).child("equipos").getValue(t);
                if (equiposID != null) {
                    for (Map.Entry<String, Boolean> entry : equiposID.entrySet()) {
                        if (entry.getValue()) {
                            Equipo e = dataSnapshot.child("equipos").child(entry.getKey()).getValue(Equipo.class);
                            if (e != null) {
                                adapter.add(e);
                            } else {
                                dataSnapshot.child("jugadores").child(userID).child("equipos").child(entry.getKey()).getRef().removeValue();
                            }
                        }
                    }
                    adapter.sort(new Comparator<Equipo>() {
                        @Override
                        public int compare(Equipo o1, Equipo o2) {
                            return o1.getNombre().compareTo(o2.getNombre());
                        }
                    });
                    adapter.notifyDataSetChanged();
                } else {
                    System.out.println("NULITO");
                }
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
