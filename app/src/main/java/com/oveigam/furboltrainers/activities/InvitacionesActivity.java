package com.oveigam.furboltrainers.activities;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.oveigam.furboltrainers.R;
import com.oveigam.furboltrainers.adapterslist.EquipoAdapter;
import com.oveigam.furboltrainers.adapterslist.JugadorAdapter;
import com.oveigam.furboltrainers.entities.Equipo;
import com.oveigam.furboltrainers.entities.Jugador;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Map;

/**
 * Created by Oscarina on 05/06/2017.
 */
public class InvitacionesActivity extends AppCompatActivity {

    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference myRef = database.getReference();

    String jugadorID;
    private EquipoAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_invitaciones);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        jugadorID = getIntent().getStringExtra("jugadorID");

        adapter = new EquipoAdapter(getBaseContext(),new ArrayList<Equipo>());
        ListView listView = (ListView) findViewById(R.id.lista);
        listView.setAdapter(adapter);
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                adapter.clear();
                GenericTypeIndicator<Map<String, Boolean>> t = new GenericTypeIndicator<Map<String, Boolean>>() {
                };
                Map<String, Boolean> equiposID = dataSnapshot.child("jugadores").child(jugadorID).child("equipos").getValue(t);
                if (equiposID != null) {
                    for (Map.Entry<String, Boolean> entry : equiposID.entrySet()) {
                        if (!entry.getValue()) {
                            Equipo e = dataSnapshot.child("equipos").child(entry.getKey()).getValue(Equipo.class);
                            if (e != null) {
                                e.setId(entry.getKey());
                                adapter.add(e);
                            } else {
                                //si no se encuentra el equipo se elimina
                                dataSnapshot.child("jugadores").child(jugadorID).child("equipos").child(entry.getKey()).getRef().removeValue();
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
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                confirmarInvi(position);
            }
        });


    }

    public void confirmarInvi(int position){
        final Equipo equipo = adapter.getItem(position);
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setMessage("Aceptar la invitacion de "+equipo.getNombre()+"?")
                .setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        contestarInvi(equipo,true);
                    }
                })
                .setNegativeButton("Rechazar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                        contestarInvi(equipo,false);
                    }
                })
                .setNeutralButton("Cancelar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                })
                .show();
    }

    public void contestarInvi(Equipo equipo, boolean aceptar) {
        if(aceptar){
            myRef.child("equipos").child(equipo.getId()).child("jugadores").child(jugadorID).setValue(false);
            myRef.child("jugadores").child(jugadorID).child("equipos").child(equipo.getId()).setValue(true);
            Toast.makeText(getBaseContext(), "Invitación aceptada", Toast.LENGTH_LONG).show();
        }else{
            myRef.child("jugadores").child(jugadorID).child("equipos").child(equipo.getId()).removeValue();
            Toast.makeText(getBaseContext(), "Invitación Rechazada", Toast.LENGTH_LONG).show();
        }
        adapter.remove(equipo);
        adapter.notifyDataSetChanged();
    }

    public void aceptarTodo(View view) {
        for(int i = 0; i<adapter.getCount(); i++){
            contestarInvi(adapter.getItem(i),true);
        }
    }

    public void rechazarTodo(View view) {
        for(int i = 0; i<adapter.getCount(); i++){
            contestarInvi(adapter.getItem(i),false);
        }
    }
}

