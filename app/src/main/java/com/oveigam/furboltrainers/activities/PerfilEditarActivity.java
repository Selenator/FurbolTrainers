package com.oveigam.furboltrainers.activities;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.ParcelFileDescriptor;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.oveigam.furboltrainers.R;
import com.oveigam.furboltrainers.tools.CircleTransform;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;
import java.io.FileDescriptor;
import java.io.IOException;

/**
 * Created by Oscarina on 12/06/2017.
 */
public class PerfilEditarActivity extends AppCompatActivity {
    String userID;

    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference myRef;

    Button crear, camara, galeria, limpiar;
    EditText nombre;
    ImageView escudo;

    private static final int OPEN_REQUEST_CODE = 41;
    private static final int CAMERA_REQUEST = 1888;

    boolean subirIMG;
    String imgURL;
    Bitmap bitmap;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_equipo_crear);

        pedirPermisos(findViewById(android.R.id.content));

        userID = getIntent().getStringExtra("jugadorID");

        myRef = database.getReference().child("jugadores").child(userID);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        ((TextView) findViewById(R.id.escudo_text)).setText("Foto de Perfil:");

        crear = (Button) findViewById(R.id.boton_crear);
        camara = (Button) findViewById(R.id.boton_camara);
        galeria = (Button) findViewById(R.id.boton_galeria);
        limpiar = (Button) findViewById(R.id.boton_limpiar);

        nombre = (EditText) findViewById(R.id.editTexto);
//        final TextInputLayout inputL = (TextInputLayout) findViewById(R.id.input1);

        escudo = (ImageView) findViewById(R.id.escudo_img);

        myRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String nombreUser = dataSnapshot.child("nombre").getValue(String.class);
                String imgDir = dataSnapshot.child("imgURL").getValue(String.class);
                mostrarDatos(nombreUser, imgDir);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        crear.setText("Editar");

        crear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (nombre.getText() != null && !nombre.getText().toString().trim().isEmpty()) {
                    ProgressDialog.show(PerfilEditarActivity.this, "Guardando", "Procesar tu careto lleva tiempo...");
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
                escudo.setImageResource(R.drawable.ic_profile);
                bitmap = null;
                subirIMG = false;
            }
        });

    }

    public void mostrarDatos(String nombre, String img) {
        imgURL = img;
        this.nombre.setText(nombre);
        if (img != null && !img.isEmpty()) {
            Picasso.with(getBaseContext()).load(img).transform(new CircleTransform()).into(escudo);
        } else {
            escudo.setImageResource(R.drawable.ic_profile);
        }
    }

    private void crearEquipo() {
        if (subirIMG) {
            editarConImagen();
        } else {
            editarSinImagen();
        }

    }

    private void editarSinImagen() {
        StorageReference storageRef = FirebaseStorage.getInstance().getReferenceFromUrl("gs://furbol-trainers.appspot.com");
        // Create a reference to the file to delete
        StorageReference cloudImg = storageRef.child("caretos").child(userID+".png");
        // Delete the file
        cloudImg.delete();
        myRef.child("nombre").setValue(nombre.getText().toString());
        myRef.child("imgURL").removeValue();
        Toast.makeText(getBaseContext(), "EXITO", Toast.LENGTH_LONG).show();
        finish();
    }

    private void editarConImagen() {
        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageRef = storage.getReferenceFromUrl("gs://furbol-trainers.appspot.com");
        StorageReference imagesRef = storageRef.child("caretos").child(userID + ".png");
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
                myRef.child("nombre").setValue(nombre.getText().toString());
                myRef.child("imgURL").setValue(downloadUrl.toString());
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
                            Picasso.with(getBaseContext()).load(resultData.getData().toString()).transform(new CircleTransform()).into(escudo);
                            //escudo.setImageBitmap(getBitmapFromUri(currentUri));
                        } catch (Exception e) {
                            // Handle error here
                        }
                    }
                    break;
                case CAMERA_REQUEST:
                    Bitmap photo = (Bitmap) resultData.getExtras().get("data");
                    bitmap = photo;
                    //escudo.setImageBitmap(photo);
                    Picasso.with(getBaseContext()).load(getImageUri(photo)).transform(new CircleTransform()).into(escudo);
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

    public Uri getImageUri(Bitmap image) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        image.compress(Bitmap.CompressFormat.PNG, 100, bytes);
        String path =  MediaStore.Images.Media.insertImage(getContentResolver(),image,"Careto","El careto");
        return Uri.parse(path);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    public void pedirPermisos(View v){
        if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            Snackbar.make(v, "Necesito gestionar imagenes subidas",
                    Snackbar.LENGTH_INDEFINITE)
                    .setAction("ok", new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            ActivityCompat.requestPermissions(PerfilEditarActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},10);
                        }
                    })
                    .show();
        } else {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 10);
        }
    }
}
