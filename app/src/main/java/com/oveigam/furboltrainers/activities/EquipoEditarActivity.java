package com.oveigam.furboltrainers.activities;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.ParcelFileDescriptor;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.oveigam.furboltrainers.R;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;
import java.io.FileDescriptor;
import java.io.IOException;

/**
 * Created by Oscarina on 13/06/2017.
 */
public class EquipoEditarActivity extends AppCompatActivity {

    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference myRef = database.getReference();

    Button crear, camara, galeria, limpiar;
    EditText nombre;
    ImageView escudo;

    private static final int OPEN_REQUEST_CODE = 41;
    private static final int CAMERA_REQUEST = 1888;

    boolean subirIMG, mantenerIMG, borrarIMG;
    Bitmap bitmap;

    String equipoID, equipoNombre, equipoImg;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_equipo_crear);

        equipoID = getIntent().getStringExtra("equipoID");
        equipoNombre = getIntent().getStringExtra("nombre");
        equipoImg = getIntent().getStringExtra("img");


        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Editar " + equipoNombre);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        crear = (Button) findViewById(R.id.boton_crear);
        camara = (Button) findViewById(R.id.boton_camara);
        galeria = (Button) findViewById(R.id.boton_galeria);
        limpiar = (Button) findViewById(R.id.boton_limpiar);

        nombre = (EditText) findViewById(R.id.editTexto);
        nombre.setText(equipoNombre);
//        final TextInputLayout inputL = (TextInputLayout) findViewById(R.id.input1);

        escudo = (ImageView) findViewById(R.id.escudo_img);
        if (equipoImg != null) {
            mantenerIMG = true;
            Picasso.with(getBaseContext()).load(equipoImg).into(escudo);
        }

        crear.setText("Editar");
        crear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (nombre.getText() != null && !nombre.getText().toString().trim().isEmpty()) {
                    ProgressDialog.show(EquipoEditarActivity.this, "Guardando", "Espera mientras pulo las botas...");
                    crearEquipo();
                } else {
                    Snackbar.make(v, "Creo que te olvidas del nombre!", Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();
                }
            }
        });

        camara.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(cameraIntent, CAMERA_REQUEST);
                subirIMG = true;
            }
        });

        galeria.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
                intent.addCategory(Intent.CATEGORY_OPENABLE);
                intent.setType("image/*");
                startActivityForResult(intent, OPEN_REQUEST_CODE);
                subirIMG = true;
            }
        });

        limpiar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                escudo.setImageResource(R.drawable.escudo);
                subirIMG = false;
                borrarIMG = true;
                mantenerIMG = false;
            }
        });


    }

    private void crearEquipo() {

        if (subirIMG) {
            crearConImagen();
        } else {
            if (mantenerIMG) {
                crearManteniendo();
            } else {
                crearSinImagen();
            }
        }


    }
    private void crearManteniendo() {
        myRef.child("equipos").child(equipoID).child("nombre").setValue(nombre.getText().toString());
        Toast.makeText(getBaseContext(), "EXITO", Toast.LENGTH_LONG).show();
        finish();
    }
    private void crearSinImagen() {
        myRef.child("equipos").child(equipoID).child("nombre").setValue(nombre.getText().toString());
        if (borrarIMG)
            myRef.child("equipos").child(equipoID).child("imgURL").removeValue();
        Toast.makeText(getBaseContext(), "EXITO", Toast.LENGTH_LONG).show();
        finish();
    }

    private void crearConImagen() {
        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageRef = storage.getReferenceFromUrl("gs://furbol-trainers.appspot.com");
        StorageReference imagesRef = storageRef.child("escudos").child(equipoID + ".png");
        escudo.setDrawingCacheEnabled(true);
        escudo.buildDrawingCache();
        //Bitmap bitmap = escudo.getDrawingCache();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
        byte[] data = baos.toByteArray();

        UploadTask uploadTask = imagesRef.putBytes(data);
        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // Handle unsuccessful uploads
                Toast.makeText(getBaseContext(), "ALGO SALIO MAL", Toast.LENGTH_LONG).show();
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                // taskSnapshot.getMetadata() contains file metadata such as size, content-type, and download URL.
                @SuppressWarnings("VisibleForTests") Uri downloadUrl = taskSnapshot.getDownloadUrl();
                myRef.child("equipos").child(equipoID).child("nombre").setValue(nombre.getText().toString());
                myRef.child("equipos").child(equipoID).child("imgURL").setValue(downloadUrl.toString());
                Toast.makeText(getBaseContext(), "EXITO", Toast.LENGTH_LONG).show();
                finish();
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

    public void onActivityResult(int requestCode, int resultCode, Intent resultData) {

        Uri currentUri = null;

        if (resultCode == Activity.RESULT_OK) {
            switch (requestCode) {
                case OPEN_REQUEST_CODE:
                    if (resultData != null) {
                        currentUri = resultData.getData();
                        try {
                            bitmap = getBitmapFromUri(currentUri);
                            escudo.setImageBitmap(bitmap);
                        } catch (IOException e) {
                            // Handle error here
                        }
                    }
                    break;
                case CAMERA_REQUEST:
                    Bitmap photo = (Bitmap) resultData.getExtras().get("data");
                    bitmap = photo;
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

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }


}
