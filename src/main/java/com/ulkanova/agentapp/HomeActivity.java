package com.ulkanova.agentapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.ulkanova.agentapp.maps.PermissionUtils;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class HomeActivity extends AppCompatActivity {
    Toolbar toolbar;
    TextView toolbarTitle, dormitorios, txtDireccion;
    SeekBar dormitoriosSeek;
    Spinner spinnerInmuebles, spinnerVencimientos;
    ImageButton btnCamara, btnLocalizar;
    Button btnGuardar;
    ImageView imgPropiedad;
    LatLng ubicacion;
    String imagenURI=null;
    private FirebaseAuth mAuth;
    FirebaseStorage storage = FirebaseStorage.getInstance();

    static final int CAMARA_REQUEST = 1;
    static final int GALERIA_REQUEST = 2;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1 ;
    private static final int CODIGO_MAPA = 222;
    String currentPhotoPath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        toolbar = findViewById(R.id.toolbar);
        toolbarTitle = findViewById(R.id.toolbarText);
        toolbarTitle.setText("Cargar Propiedad");
        dormitoriosSeek = findViewById(R.id.skDormitorios);
        dormitorios = findViewById(R.id.txtDormitorios);
        spinnerInmuebles = findViewById(R.id.spinnerInmuebles);
        spinnerVencimientos = findViewById(R.id.spinnerVencimiento);
        btnCamara = findViewById(R.id.btnCamara);
        btnLocalizar = findViewById(R.id.btnLocalizar);
        btnGuardar = findViewById(R.id.btnGuardarPropiedad);

        //CAMBIAR--------------------------------------------------------------------
        btnGuardar.setEnabled(true);
        imgPropiedad = findViewById(R.id.imgPropiedad);
        txtDireccion = findViewById(R.id.txtDireccion);

        // Inicializar Firebase Auth
        mAuth = FirebaseAuth.getInstance();
        // Iniciar Session como usuario anónimo
        signInAnonymously();

        // Adaptadores para los spinner
        ArrayAdapter<CharSequence> adapterInmueble = ArrayAdapter.createFromResource(this, R.array.inmuebles, android.R.layout.simple_spinner_item);
        adapterInmueble.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerInmuebles.setAdapter(adapterInmueble);
        ArrayAdapter<CharSequence> adapterVencimiento = ArrayAdapter.createFromResource(this, R.array.vencimientos, android.R.layout.simple_spinner_item);
        adapterVencimiento.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerVencimientos.setAdapter(adapterVencimiento);


//      Seekbar Dormitorios
        dormitoriosSeek.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                switch (progress) {
                    case 0:
                        dormitorios.setText("Dormitorios: Monoambiente");
                        break;
                    case 5:
                        dormitorios.setText("Dormitorios: más de 4");
                        break;
                    default:
                        dormitorios.setText("Dormitorios: "+progress);
                        break;
                }
            }
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                switch (seekBar.getProgress()) {
                    case 0:
                        dormitorios.setText("Dormitorios: Monoambiente");
                        break;
                    case 5:
                        dormitorios.setText("Dormitorios: más de 4");
                        break;
                    default:
                        dormitorios.setText("Dormitorios: "+String.valueOf(seekBar.getProgress()));
                        break;
                }
            }
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });
        btnCamara.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                lanzarCamara();
            }
        });

        btnLocalizar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                localizar();
            }
        });

        btnGuardar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                guardarImagen();
            }
        });
    }

    private void lanzarCamara() {
        Intent camaraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        File photoFile = null;
        try {
            photoFile = createImageFile();
        } catch (IOException ex) {
            // Error occurred while creating the File
        }
        if (photoFile != null) {
            Log.d("PHOTO", "lanzarCamara: "+photoFile);
            Uri photoURI = FileProvider.getUriForFile(getApplicationContext(),
                    "com.ulkanova.agentapp.fileprovider",
                    photoFile);
            camaraIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
            startActivityForResult(camaraIntent, GALERIA_REQUEST);
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CAMARA_REQUEST && resultCode == RESULT_OK) {
            Bundle extras = data.getExtras();
            Bitmap imageBitmap = (Bitmap) extras.get("data");
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            imageBitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
            byte[] dato = baos.toByteArray(); // Imagen en arreglo de bytes
            imgPropiedad.setImageBitmap(imageBitmap);

            try {
                guardarImagen(dato);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        else if (requestCode == GALERIA_REQUEST && resultCode == RESULT_OK){
            Log.d("CAMARA", "onActivityResult: "+currentPhotoPath);
            Uri imgUri=Uri.parse(currentPhotoPath);
            imgPropiedad.setImageURI(imgUri);
        }
        else if (requestCode == CODIGO_MAPA && resultCode == RESULT_OK) {
            ubicacion = new LatLng(data.getDoubleExtra("lat", 0), data.getDoubleExtra("lng", 0));
            Log.d("PEDIDO", "Ubicacion encontrada: "+ubicacion);
            txtDireccion.setText("Longitud: "+String.valueOf(ubicacion.latitude)+"  Latitud: "+String.valueOf(ubicacion.longitude));

        }
    }
    private File createImageFile() throws IOException {
        StorageReference storageRef = storage.getReference();
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "propiedad_"+timeStamp+"_";
        Log.d("PHOTO", "imageFileName: "+imageFileName);
//        StorageReference propiedadImagesRef = storageRef.child("images/propiedad_"+timeStamp+".jpg");
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );
        currentPhotoPath = image.getAbsolutePath();

        return image;
    }
    private void guardarImagen(byte[] data) throws IOException {
        // Creamos una referencia a nuestro Storage
        StorageReference storageRef = storage.getReference();

        // Creamos una referencia a la imagen
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "propiedad_"+timeStamp+".jpg";
        StorageReference propiedadImagesRef = storageRef.child("images/propiedad_"+timeStamp+".jpg");
        Log.d("URL IMAGEN", "guardarImagen: "+"images/propiedad_"+Math.random()*1000+".jpg");
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );
        UploadTask uploadTask = propiedadImagesRef.putBytes(data);

        // Registramos un listener para saber el resultado de la operación
        Task<Uri> urlTask = uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
            @Override
            public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                if (!task.isSuccessful()) {
                    throw task.getException();
                }
                // Continuamos con la tarea para obtener la URL
                return propiedadImagesRef.getDownloadUrl();
            }
        }).addOnCompleteListener(new OnCompleteListener<Uri>() {
            @Override
            public void onComplete(@NonNull Task<Uri> task) {
                if (task.isSuccessful()) {
                    // URL de descarga del archivo
                    Uri downloadUri = task.getResult();
                    imagenURI = downloadUri.toString();
                } else {
                    // Fallo
                }
            }
        });
    }
    private void localizar() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
//            if (mMap != null) {
//                map.setMyLocationEnabled(true);
//            }
            // CREAR MAPA ENVIÁNDOLE MI UBICACIÓN
            Intent localizarIntent = new Intent(HomeActivity.this, Localizar.class);
            startActivityForResult(localizarIntent,CODIGO_MAPA);
        } else {
            PermissionUtils.requestPermission(this, LOCATION_PERMISSION_REQUEST_CODE,
                    Manifest.permission.ACCESS_FINE_LOCATION, true);
        }
    }

    private void signInAnonymously() {
        mAuth.signInAnonymously()
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Exito
                            Log.d("FIREBASE", "signInAnonymously:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                        } else {
                            // Error
                            Log.w("FIREBASE", "signInAnonymously:failure", task.getException());
                            Toast.makeText(HomeActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }
}