package com.oveigam.furboltrainers.activities;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.KeyEvent;
import android.view.MenuItem;
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
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.oveigam.furboltrainers.R;
import com.oveigam.furboltrainers.adapterslist.EquipoAdapter;
import com.oveigam.furboltrainers.entities.Equipo;

import java.util.ArrayList;
import java.util.Map;

/**
 * Created by Oscarina on 13/06/2017.
 */
public class BuscarEquipoActivity extends AppCompatActivity {

    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference myRef = database.getReference();

    String userID;
    private EquipoAdapter adapter;
    EditText editBuscar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_invitar);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        userID = getIntent().getStringExtra("jugadorID");

        adapter = new EquipoAdapter(getBaseContext(),new ArrayList<Equipo>());
        ListView listView = (ListView) findViewById(R.id.lista);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                confirmarInvi(position);
            }
        });

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

    private void cargarSpinner(ArrayList<String> equiposKeys) {

    }


    public void confirmarInvi(int position){
        final Equipo equipo = adapter.getItem(position);
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setMessage("Pedir acceso a " + equipo.getNombre() + " ?")
                .setPositiveButton("Si", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        pedir(equipo);
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

    public void pedir(Equipo equipo) {
        //myRef.child("equipos").child(equipoID).child(jugador.getId()).setValue(false);
            myRef.child("equipos").child(equipo.getId()).child("peticiones").child(userID).setValue(true);
        Toast.makeText(getBaseContext(), "Petición enviada", Toast.LENGTH_LONG).show();
    }

    public void buscar() {
        String busqueda = editBuscar.getText().toString();
        String buscarPor = "nombre";
        adapter.clear();

        //Query query = myRef.child("jugadores").orderByChild(buscarPor).equalTo(busqueda);

        Query query = myRef.child("equipos").orderByChild(buscarPor).startAt(busqueda).endAt(busqueda + "\uf8ff");
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        if(snapshot.child("jugadores").child(userID).exists())
                            continue;
                        Equipo e = snapshot.getValue(Equipo.class);
                        e.setId(snapshot.getKey());
                        adapter.add(e);
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

    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem) {
        if (menuItem.getItemId() == android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(menuItem);
    }
}