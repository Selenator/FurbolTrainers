package com.oveigam.furboltrainers.activities;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.ParcelFileDescriptor;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.oveigam.furboltrainers.R;

import java.io.FileDescriptor;
import java.io.IOException;

/**
 * Created by Oscarina on 23/04/2017.
 */
public class EquipoCrearActivity extends AppCompatActivity {
    String userID = FirebaseAuth.getInstance().getCurrentUser().getUid();

    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference myRef = database.getReference();

    Button crear, camara, galeria, limpiar;
    EditText nombre;
    ImageView escudo;

    private static final int OPEN_REQUEST_CODE = 41;
    private static final int CAMERA_REQUEST = 1888;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_equipo_crear);


        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        crear = (Button) findViewById(R.id.boton_crear);
        camara = (Button) findViewById(R.id.boton_camara);
        galeria = (Button) findViewById(R.id.boton_galeria);
        limpiar = (Button) findViewById(R.id.boton_limpiar);

        nombre = (EditText) findViewById(R.id.editTexto);
        final TextInputLayout inputL = (TextInputLayout) findViewById(R.id.input1);

        escudo = (ImageView) findViewById(R.id.escudo_img);

        crear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (nombre.getText() != null && !nombre.getText().toString().trim().isEmpty()) {
                    String key = myRef.child("equipos").push().getKey();
                    myRef.child("equipos").child(key).child("nombre").setValue(nombre.getText().toString());
                    myRef.child("equipos").child(key).child("jugadores").child(userID).setValue(true);
                    myRef.child("jugadores").child(userID).child("equipos").child(key).setValue(true);
                    Toast.makeText(getBaseContext(), "EXITO", Toast.LENGTH_LONG).show();
                    finish();
                } else {
                    inputL.setError("Te has olvidado de algo!");
                }
            }
        });

        camara.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(cameraIntent, CAMERA_REQUEST);
            }
        });

        galeria.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
                intent.addCategory(Intent.CATEGORY_OPENABLE);
                intent.setType("image/*");
                startActivityForResult(intent, OPEN_REQUEST_CODE);
            }
        });

        limpiar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                escudo.setImageResource(R.drawable.escudo);
            }
        });


    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        return super.onOptionsItemSelected(item);
    }

    public void onActivityResult(int requestCode, int resultCode, Intent resultData) {

        Uri currentUri = null;

        if (resultCode == Activity.RESULT_OK) {
            switch (requestCode) {
                case OPEN_REQUEST_CODE:
                    if (resultData != null) {
                        currentUri = resultData.getData();
                        try {
                            escudo.setImageBitmap(getBitmapFromUri(currentUri));
                        } catch (IOException e) {
                            // Handle error here
                        }
                    }
                    break;
                case CAMERA_REQUEST:
                    Bitmap photo = (Bitmap) resultData.getExtras().get("data");
                    escudo.setImageBitmap(photo);
                    break;
            }
        }
    }


    private Bitmap getBitmapFromUri(Uri uri) throws IOException {
        ParcelFileDescriptor parcelFileDescriptor =
                getContentResolver().openFileDescriptor(uri, "r");
        FileDescriptor fileDescriptor = parcelFileDescriptor.getFileDescriptor();
        Bitmap image = BitmapFactory.decodeFileDescriptor(fileDescriptor);
        parcelFileDescriptor.close();
        return image;
    }
}
