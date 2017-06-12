package com.oveigam.furboltrainers.activities;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.database.ValueEventListener;
import com.oveigam.furboltrainers.R;
import com.oveigam.furboltrainers.entities.Jugador;
import com.oveigam.furboltrainers.fragments.PagerAdapter;
import com.oveigam.furboltrainers.tools.CircleTransform;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import static java.security.AccessController.getContext;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, GoogleApiClient.OnConnectionFailedListener {

    private static final String TAG = "MAIN ACTIVITY";

    //usuario de google
    GoogleApiClient mGoogleApiClient;

    //usuario de firebase
    FirebaseAuth fAuth;
    FirebaseAuth.AuthStateListener fAuthListener;

    //referencias a la base de datos
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference myRef = database.getReference();
    DatabaseReference jugRef;

    int invitaciones = 0;
    String jugadorID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        conectarUsuario();

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        //BARRA DE PESTAÑAS
        TabLayout tabLayout = (TabLayout) findViewById(R.id.tab_layout);
        tabLayout.addTab(tabLayout.newTab().setText("Eventos"));
        tabLayout.addTab(tabLayout.newTab().setText("Equipos"));
        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);

        //ADAPTADOR PARA GESTIONAR LOS FRAGMENTOS
        final ViewPager viewPager = (ViewPager) findViewById(R.id.container);
        final PagerAdapter adapter = new PagerAdapter
                (getSupportFragmentManager(), tabLayout.getTabCount());
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


        //CAJON LATERAL
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);


    }

    private void conectarUsuario() {
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this /* FragmentActivity */, this /* OnConnectionFailedListener */)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();


        fAuth = FirebaseAuth.getInstance();
        fAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    cargarJugador(user);
                } else {
                    goLogin();
                }
            }
        };
    }

    private void goLogin() {
        Intent intent = new Intent(this, LoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

    private void cargarJugador(final FirebaseUser user) {
        //crea el jugador si no existe
        jugRef = myRef.child("jugadores").child(user.getUid());
        jugRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Jugador jugador = dataSnapshot.getValue(Jugador.class);
                if (jugador == null) {
                    jugador = new Jugador(user.getDisplayName(), user.getPhotoUrl().toString(), user.getEmail());
                    jugador.setId(dataSnapshot.getKey());
                    jugador.setEquipos(new HashMap<String, Boolean>());
                    setUserData(jugador);
                    jugRef.setValue(jugador);
                } else {
                    jugador.setId(dataSnapshot.getKey());
                    setUserData(jugador);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void setUserData(Jugador jugador) {
        jugadorID = jugador.getId();
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        View cabecera = navigationView.getHeaderView(0);

        invitaciones = 0;
        if (jugador.getEquipos() != null)
            for (Map.Entry<String, Boolean> entry : jugador.getEquipos().entrySet()) {
                if (!entry.getValue()) {
                    invitaciones++;
                }
            }
        if (invitaciones > 0) {
            navigationView.getMenu().getItem(1).setTitle("Invitaciones (" + invitaciones + ")");
        } else {
            navigationView.getMenu().getItem(1).setTitle("Invitaciones");
        }

        TextView titulo = (TextView) cabecera.findViewById(R.id.titulo_cabecera);
        //titulo.setText(user.getDisplayName());
        titulo.setText(jugador.getNombre());

        TextView mail = (TextView) cabecera.findViewById(R.id.mail_cabecera);
        mail.setText(jugador.getEmail());

        ImageView foto = (ImageView) cabecera.findViewById(R.id.imagen_cabecera);
//        foto.setImageURI(user.getPhotoUrl());
        //Picasso.with(this).load(user.getPhotoUrl()).transform(new CircleTransform()).into(foto);
        if(jugador.getImgURL() == null || jugador.getImgURL().isEmpty())
            foto.setImageResource(R.drawable.balon);
        else
            Picasso.with(this).load(jugador.getImgURL()).transform(new CircleTransform()).into(foto);
    }

    @Override
    protected void onStart() {
        super.onStart();
        fAuth.addAuthStateListener(fAuthListener);
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (fAuthListener != null) {
            fAuth.removeAuthStateListener(fAuthListener);
        }

    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        //getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
//        int id = item.getItemId();
//
//        //noinspection SimplifiableIfStatement
//        if (id == R.id.action_settings) {
//            return true;
//        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.invitaciones) {
            if (invitaciones > 0) {
                Intent intent = new Intent(getBaseContext(), InvitacionesActivity.class);
                intent.putExtra("jugadorID", jugadorID);
                startActivity(intent);
            } else {
                Snackbar.make(getCurrentFocus(), "No tienes invitaciones :(", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        } else if (id == R.id.perfil) {
            Intent intent = new Intent(getBaseContext(), PerfilEditarActivity.class);
            intent.putExtra("jugadorID", jugadorID);
            startActivity(intent);
        } else if (id == R.id.nav_slideshow) {

        } else if (id == R.id.nav_manage) {

        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {
            FirebaseAuth.getInstance().signOut();
            goLogin();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.e(TAG, "Connection Failed:\n" + connectionResult.getErrorMessage());
    }
}
