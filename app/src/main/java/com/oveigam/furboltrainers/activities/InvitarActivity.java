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
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.oveigam.furboltrainers.R;
import com.oveigam.furboltrainers.adapterslist.JugadorAdapter;
import com.oveigam.furboltrainers.entities.Jugador;

/**
 * Created by Oscarina on 03/06/2017.
 */
public class InvitarActivity extends AppCompatActivity {

    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference myRef = database.getReference();

    String equipoID;
    private JugadorAdapter adapter;
    EditText editBuscar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_invitar);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        adapter = new JugadorAdapter(getBaseContext());
        ListView listView = (ListView) findViewById(R.id.lista);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                confirmarInvi(position);
            }
        });


        equipoID = getIntent().getStringExtra("equipoID");

        editBuscar = (EditText) findViewById(R.id.edit_buscar);

        editBuscar.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    buscar();
                    return true;
                }
                return false;
            }
        });
    }

    public void confirmarInvi(int position){
        final Jugador jugador = adapter.getItem(position);
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setMessage("Invitar a " + jugador.getNombre() + " ?")
                .setPositiveButton("Si", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        invitar(jugador);
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

    public void invitar(Jugador jugador) {
        //myRef.child("equipos").child(equipoID).child(jugador.getId()).setValue(false);
        myRef.child("jugadores").child(jugador.getId()).child("equipos").child(equipoID).setValue(false);
        Toast.makeText(getBaseContext(), "Invitación enviada", Toast.LENGTH_LONG).show();
        adapter.remove(jugador);
        adapter.notifyDataSetChanged();
    }

    public void buscar() {
        String busqueda = editBuscar.getText().toString();
        String buscarPor = busqueda.contains("@") ? "email" : "nombre";
        adapter.clear();

        Query query = myRef.child("jugadores").orderByChild(buscarPor).equalTo(busqueda);
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        if (snapshot.child("equipos").child(equipoID).exists())
                            continue;
                        Jugador j = snapshot.getValue(Jugador.class);
                        j.setId(snapshot.getKey());
                        adapter.add(j);
                    }
                    adapter.notifyDataSetChanged();
                } else {
                    Toast.makeText(getBaseContext(), "No encontré nada", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                System.out.println("WAKA ERROR");
            }
        });

    }
}
