package com.oveigam.furboltrainers.activities;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import android.widget.TextView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.oveigam.furboltrainers.R;
import com.oveigam.furboltrainers.fragments.PagerAdapterEquipo;

import java.util.Map;

public class EquipoActivity extends AppCompatActivity {

    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference myRef;
    private String equipoID, userID;
    boolean isTrainer;
    private Menu menu;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_equipo);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(getIntent().getStringExtra("equipoNombre"));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        equipoID = getIntent().getStringExtra("equipoID");
        userID = getIntent().getStringExtra("userID");
        myRef = database.getReference().child("equipos").child(equipoID).child("jugadores").child(userID);
        myRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists() && dataSnapshot.getValue(boolean.class)) {
                    isTrainer = true;
                    getMenuInflater().inflate(R.menu.menu_equipo, menu);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        //BARRA DE PESTAÑAS
        final TabLayout tabLayout = (TabLayout) findViewById(R.id.tab_layout);
        tabLayout.addTab(tabLayout.newTab().setText("Eventos"));
        tabLayout.addTab(tabLayout.newTab().setText("Jugadores"));
        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);

        //ADAPTADOR PARA GESTIONAR LOS FRAGMENTOS
        final ViewPager viewPager = (ViewPager) findViewById(R.id.container);
        final PagerAdapterEquipo adapter = new PagerAdapterEquipo(getSupportFragmentManager(), tabLayout.getTabCount());
        viewPager.setAdapter(adapter);
        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        tabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        this.menu = menu;
        if (isTrainer)
            getMenuInflater().inflate(R.menu.menu_equipo, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem) {
        if (menuItem.getItemId() == android.R.id.home) {
            finish();
        } else if (menuItem.getItemId() == R.id.action_editar) {
            finish();
        } else if (menuItem.getItemId() == R.id.action_eliminar) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);

            builder.setMessage("Seguro que quieres eliminar el equipo " + getIntent().getStringExtra("equipoNombre") + "?")
                    .setPositiveButton("Si", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int id) {
                            ProgressDialog.show(EquipoActivity.this, "Eliminando", "Nunca fuisteis muy buenos de todas formas...");
                            eliminarEquipo();
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

        return super.onOptionsItemSelected(menuItem);
    }

    private void eliminarEquipo() {
        StorageReference storageRef = FirebaseStorage.getInstance().getReferenceFromUrl("gs://furbol-trainers.appspot.com");
        // Create a reference to the file to delete
        StorageReference cloudImg = storageRef.child("escudos").child(equipoID+".png");
        // Delete the file
        cloudImg.delete();

        final DatabaseReference ref = database.getReference();
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.child("equipos").child(equipoID).child("eventos").exists()) {
                    System.out.println("AKA");
                    GenericTypeIndicator<Map<String, Boolean>> t = new GenericTypeIndicator<Map<String, Boolean>>() {
                    };
                    Map<String, Boolean> eventosID = dataSnapshot.child("equipos").child(equipoID).child("eventos").getValue(t);
                    for (String id : eventosID.keySet()) {
                        ref.child("eventos").child(id).removeValue();
                    }
                }
                ref.child("equipos").child(equipoID).removeValue();
                finish();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }


}
