package com.oveigam.furboltrainers.activities;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.annotation.IdRes;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.format.DateFormat;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlacePicker;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.oveigam.furboltrainers.R;
import com.oveigam.furboltrainers.entities.Evento;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by Oscarina on 22/05/2017.
 */
public class EventoEditarActivity extends AppCompatActivity implements OnMapReadyCallback {
    RadioGroup grupoTipo;
    static TextView fecha, hora;
    static int evYear, evMonth, evDay, evHour, evMinute;
    CheckBox placeCheck;
    int PLACE_PICKER_REQUEST = 1;
    PlacePicker.IntentBuilder builder = new PlacePicker.IntentBuilder();

    MapView mapView;
    GoogleMap map;
    Marker marker;

    EditText sitioDesc;
    private LatLng coordenadas;

    String equipoID;
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference myRef = database.getReference();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_crear_evento);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Evento evento = (Evento) getIntent().getSerializableExtra("evento");

        if (evento.getLatitude() != 0 && evento.getLongitude() != 0)
            coordenadas = evento.getCoordenadas();

        mapView = (MapView) findViewById(R.id.mapview);
        mapView.onCreate(savedInstanceState);
        mapView.getMapAsync(this);
//        mapView.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                showPlacePickerDialog(v);
//            }
//        });

        Date date = evento.getFecha_hora();
        evYear = date.getYear();
        evMonth = date.getMonth();
        evDay = date.getDate();
        evHour = date.getHours();
        evMinute = date.getMinutes();

        fecha = (TextView) findViewById(R.id.fecha);
        fecha.setText(SimpleDateFormat.getDateInstance().format(new Date(evYear - 1900, evMonth, evDay)));
        hora = (TextView) findViewById(R.id.hora);
        hora.setText(SimpleDateFormat.getTimeInstance().format(new Date(1, 1, 1900, evHour, evMinute)));

        sitioDesc = (EditText) findViewById(R.id.sitio_desc);
        sitioDesc.setText(evento.getLocalizacionDescripcion());
        placeCheck = (CheckBox) findViewById(R.id.placeCheck);
        if(coordenadas!=null){
            placeCheck.setChecked(true);
            mapView.setVisibility(View.VISIBLE);
        }
        final EditText tipoDesc = (EditText) findViewById(R.id.tipo_desc);
        grupoTipo = (RadioGroup) findViewById(R.id.radio_tipo);
        switch (evento.getTipo()){
            case "Partido":
                grupoTipo.check(R.id.r_partido);
                break;
            case "Entrenamiento":
                grupoTipo.check(R.id.r_entrenamiento);
                break;
            default:
                grupoTipo.check(R.id.r_otros);
                tipoDesc.setText(evento.getTipo());
                tipoDesc.setVisibility(View.VISIBLE);
                break;
        }
        grupoTipo.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, @IdRes int checkedId) {
                if (checkedId == R.id.r_otros)
                    tipoDesc.setVisibility(View.VISIBLE);
                else
                    tipoDesc.setVisibility(View.GONE);
            }
        });
        final String id = evento.getId();

        Button crear = (Button) findViewById(R.id.boton_crear);
        crear.setText("Editar");
        crear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String tipo = "";
                switch (grupoTipo.getCheckedRadioButtonId()) {
                    case R.id.r_entrenamiento:
                        tipo = "Entrenamiento";
                        break;
                    case R.id.r_partido:
                        tipo = "Partido";
                        break;
                    case R.id.r_otros:
                        tipo = tipoDesc.getText().toString();
                        if (tipo.isEmpty()) {
                            tipo = "Otros";
                        }
                        break;
                }
                String fechaT = fecha.getText().toString();
                String horaT = hora.getText().toString();
                String ubicacion = sitioDesc.getText().toString();
                if (fechaT.isEmpty() || horaT.isEmpty() || ubicacion.isEmpty()) {
                    Snackbar.make(v, "Te olvidas de algun dato importante!", Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();
                    return;
                }
                Date eventoDate = new Date(evYear - 1900, evMonth, evDay, evHour, evMinute);
                Date ahoraDate = new Date();
                ahoraDate.setHours(ahoraDate.getHours() - 3);
                if (eventoDate.before(ahoraDate)) {
                    Snackbar.make(v, "No todo el mundo puede viajar al pasado!", Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();
                    return;
                }

                String desc = ((EditText) findViewById(R.id.ev_comentario)).getText().toString();

                Evento evento = new Evento(tipo, eventoDate, ubicacion, coordenadas, desc);
                editarEvento(evento,id);

            }
        });

    }

    private void editarEvento(Evento evento, String key) {
        myRef.child("eventos").child(key).setValue(evento);
        //myRef.child("equipos").child(equipoID).child("eventos").child(key).setValue(true);
        ProgressDialog.show(EventoEditarActivity.this, "Editando", "Espera un poco ansioso...");
        Toast.makeText(getBaseContext(), "EXITO", Toast.LENGTH_LONG).show();
        finish();
    }

    @Override
    public void onResume() {
        mapView.onResume();
        super.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        mapView.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapView.onLowMemory();
    }

    @Override
    public void onSaveInstanceState(Bundle outState, PersistableBundle outPersistentState) {
        super.onSaveInstanceState(outState, outPersistentState);
        mapView.onSaveInstanceState(outState);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    public void showTimePickerDialog(View v) {
        DialogFragment newFragment = new TimePickerFragment();
        newFragment.show(getSupportFragmentManager(), "timePicker");
    }

    public void showDatePickerDialog(View v) {
        DialogFragment newFragment = new DatePickerFragment();
        newFragment.show(getSupportFragmentManager(), "datePicker");
    }

    public void showPlacePickerDialog(View v) {
        if (placeCheck.isChecked()) {
            mapView.setVisibility(View.VISIBLE);
            try {
                startActivityForResult(builder.build(this), PLACE_PICKER_REQUEST);
            } catch (GooglePlayServicesRepairableException e) {
                System.out.println("ERROR 1");
                e.printStackTrace();
            } catch (GooglePlayServicesNotAvailableException e) {
                System.out.println("ERROR 2");
                e.printStackTrace();
            }
        } else {
            mapView.setVisibility(View.GONE);
        }

    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == PLACE_PICKER_REQUEST) {
            if (resultCode == RESULT_OK) {
                Place place = PlacePicker.getPlace(data, this);
                marker.remove();
                coordenadas = place.getLatLng();
                marker = map.addMarker(new MarkerOptions().position(place.getLatLng()).title(place.getName().toString()));
                map.moveCamera(CameraUpdateFactory.newLatLngZoom(place.getLatLng(), 15));
                marker.showInfoWindow();
                if (sitioDesc.getText().toString().isEmpty())
                    sitioDesc.setText(place.getName());
            } else {
                coordenadas = null;
                mapView.setVisibility(View.GONE);
                placeCheck.setChecked(false);
            }
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        map = googleMap;
        map.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                showPlacePickerDialog(mapView);
            }
        });

        if(coordenadas!=null){
            marker = map.addMarker(new MarkerOptions().position(coordenadas).title("Más Opciones"));
            map.moveCamera(CameraUpdateFactory.newLatLngZoom(coordenadas,15));
        }else{
            LatLng sydney = new LatLng(-34, 151);
            marker = map.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));

            map.moveCamera(CameraUpdateFactory.newLatLng(sydney));
        }

    }

    public static class DatePickerFragment extends DialogFragment
            implements DatePickerDialog.OnDateSetListener {

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            // Use the current date as the default date in the picker
            final Calendar c = Calendar.getInstance();
            int year = c.get(Calendar.YEAR);
            int month = c.get(Calendar.MONTH);
            int day = c.get(Calendar.DAY_OF_MONTH);

            // Create a new instance of DatePickerDialog and return it
            return new DatePickerDialog(getActivity(), this, year, month, day);
        }

        public void onDateSet(DatePicker view, int year, int month, int day) {
            evDay = day;
            evMonth = month;
            evYear = year;
            fecha.setText(SimpleDateFormat.getDateInstance().format(new Date(year - 1900, month, day)));
        }
    }

    public static class TimePickerFragment extends DialogFragment
            implements TimePickerDialog.OnTimeSetListener {

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            // Use the current time as the default values for the picker
            final Calendar c = Calendar.getInstance();
            int hour = c.get(Calendar.HOUR_OF_DAY);
            int minute = c.get(Calendar.MINUTE);

            // Create a new instance of TimePickerDialog and return it
            return new TimePickerDialog(getActivity(), this, hour, minute,
                    DateFormat.is24HourFormat(getActivity()));
        }

        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
            evMinute = minute;
            evHour = hourOfDay;
            hora.setText(SimpleDateFormat.getTimeInstance().format(new Date(1, 1, 1900, hourOfDay, minute)));
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
