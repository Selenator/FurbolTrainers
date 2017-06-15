package com.oveigam.furboltrainers.activities;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Spinner;
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
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Oscarina on 13/06/2017.
 */
public class PeticionesActivity extends AppCompatActivity {

    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference myRef = database.getReference();

    HashMap<String, String> diccionarioEquipos;

    String jugadorID;
    private JugadorAdapter adapter;
    Spinner spinner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_peticiones);

        diccionarioEquipos = new HashMap<>();

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        jugadorID = getIntent().getStringExtra("jugadorID");

        spinner = (Spinner) findViewById(R.id.spinner);

        adapter = new JugadorAdapter(getBaseContext());
        ListView listView = (ListView) findViewById(R.id.lista);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                confirmarInvi(position);
            }
        });

        getEquipos();


    }

    private void getEquipos() {
        final ArrayList<String> equiposKeys = new ArrayList<>();
        DatabaseReference ref = database.getReference();

        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                GenericTypeIndicator<Map<String, Boolean>> t = new GenericTypeIndicator<Map<String, Boolean>>() {
                };
                Map<String, Boolean> equiposID = dataSnapshot.child("jugadores").child(jugadorID).child("equipos").getValue(t);
                for (Map.Entry<String, Boolean> entry : equiposID.entrySet()) {
                    if (entry.getValue() && dataSnapshot.child("equipos").child(entry.getKey()).child("jugadores").child(jugadorID).getValue(boolean.class)) {
                        String nombre = dataSnapshot.child("equipos").child(entry.getKey()).child("nombre").getValue(String.class);
                        if (diccionarioEquipos.containsKey(nombre))
                            nombre += "0";
                        diccionarioEquipos.put(nombre, entry.getKey());
                        equiposKeys.add(nombre);
                    }
                }
                cargarSpinner(equiposKeys);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void cargarSpinner(ArrayList<String> equiposKeys) {
        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this, android.R.layout.simple_spinner_item, equiposKeys);

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                mostrarLista(diccionarioEquipos.get(spinner.getSelectedItem().toString()));
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    private void mostrarLista(final String equipoId) {
        DatabaseReference ref = database.getReference();
        adapter.clear();
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                GenericTypeIndicator<Map<String, Boolean>> t = new GenericTypeIndicator<Map<String, Boolean>>() {
                };
                Map<String, Boolean> peticiones = dataSnapshot.child("equipos").child(equipoId).child("peticiones").getValue(t);
                if (peticiones != null) {
                    for (String jugId : peticiones.keySet()) {
                        Jugador jugador = dataSnapshot.child("jugadores").child(jugId).getValue(Jugador.class);
                        jugador.setId(jugId);
                        adapter.add(jugador);
                    }
                    adapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public void confirmarInvi(int position) {
        final Jugador jugador = adapter.getItem(position);
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setMessage("Aceptar la peticion de " + jugador.getNombre() + "?")
                .setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        contestarInvi(jugador, true);
                    }
                })
                .setNegativeButton("Rechazar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                        contestarInvi(jugador, false);
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

    public void contestarInvi(Jugador jugador, boolean aceptar) {
        String equipoID = diccionarioEquipos.get(spinner.getSelectedItem().toString());
        if (aceptar) {
            myRef.child("equipos").child(equipoID).child("jugadores").child(jugador.getId()).setValue(false);
            myRef.child("jugadores").child(jugador.getId()).child("equipos").child(equipoID).setValue(true);
            myRef.child("equipos").child(equipoID).child("peticiones").child(jugador.getId()).removeValue();
            Toast.makeText(getBaseContext(), "Petición aceptada", Toast.LENGTH_LONG).show();
        } else {
            myRef.child("jugadores").child(jugador.getId()).child("equipos").child(jugador.getId()).removeValue();
            myRef.child("equipos").child(equipoID).child("peticiones").child(jugador.getId()).removeValue();
            Toast.makeText(getBaseContext(), "Petición Rechazada", Toast.LENGTH_LONG).show();
        }
        adapter.remove(jugador);
        adapter.notifyDataSetChanged();
    }

    public void aceptarTodo(View view) {
        for (int i = 0; i < adapter.getCount(); i++) {
            contestarInvi(adapter.getItem(i), true);
        }
    }

    public void rechazarTodo(View view) {
        for (int i = 0; i < adapter.getCount(); i++) {
            contestarInvi(adapter.getItem(i), false);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem) {
        if (menuItem.getItemId() == android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(menuItem);
    }
}
