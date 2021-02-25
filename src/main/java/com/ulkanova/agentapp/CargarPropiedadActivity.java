package com.ulkanova.agentapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import android.Manifest;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.Switch;
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
import com.ulkanova.agentapp.model.ImagenPropiedad;
import com.ulkanova.agentapp.model.Propiedad;
import com.ulkanova.agentapp.room.AppRepositoryRoom;
import com.ulkanova.agentapp.room.PropiedadConFotos;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class CargarPropiedadActivity extends AppCompatActivity implements AppRepositoryRoom.OnResultCallback{
    static final int CAMARA_REQUEST = 1;
    static final int GALERIA_REQUEST = 2;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1 ;
    private static final int CODIGO_MAPA = 222;

    Toolbar toolbar;
    EditText  txtDireccion, txtCoordenadas, txtNombrePropiedad, txtPrecio, txtDetalle;
    TextView dormitorios, toolbarTitle;
    SeekBar dormitoriosSeek;
    Spinner spinnerInmuebles, spinnerVencimientos;
    ImageButton btnCamara, btnLocalizar;
    Button btnGuardar;
    ImageView imgPropiedad;
    RadioButton btnVenta, btnAlquiler, btnPesos, btnUsd;
    SeekBar skDormitorios;
    Switch swCochera;

    boolean movioSeek=false;

    BroadcastReceiver br;

    List<ImagenPropiedad> imagenesDeLaPropiedad;
    LatLng ubicacion;
    String imagenURI=null;
    private FirebaseAuth mAuth;
    FirebaseStorage storage = FirebaseStorage.getInstance();
    AppRepositoryRoom repository;
    String currentPhotoPath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cargar_propiedad);
        toolbar = findViewById(R.id.toolbar);
        toolbarTitle = findViewById(R.id.toolbarText);
        toolbarTitle.setText("Cargar Propiedad");
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        dormitoriosSeek = findViewById(R.id.skDormitorios);
        dormitorios = findViewById(R.id.txtDormitorios);
        spinnerInmuebles = findViewById(R.id.spinnerInmuebles);
        spinnerVencimientos = findViewById(R.id.spinnerVencimiento);
        btnCamara = findViewById(R.id.btnCamara);
        btnLocalizar = findViewById(R.id.btnLocalizar);
        btnGuardar = findViewById(R.id.btnGuardarPropiedad);
        btnVenta=findViewById(R.id.btnVenta);
        btnAlquiler=findViewById(R.id.btnAlquiler);
        btnUsd=findViewById(R.id.btnUSD);
        btnPesos = findViewById(R.id.btnARS);
        skDormitorios = findViewById(R.id.skDormitorios);
        swCochera = findViewById(R.id.swtCochera);

        txtCoordenadas = findViewById(R.id.txtCoordenadas);
        txtNombrePropiedad=findViewById(R.id.txtNombrePropiedad);
        txtDireccion = findViewById(R.id.txtDireccion);
        txtPrecio = findViewById(R.id.txtPrecio);
        txtDetalle = findViewById(R.id.txtDetalle);

        imgPropiedad = findViewById(R.id.imgPropiedad);

        crearCanal(this);

        repository = new AppRepositoryRoom(this.getApplication(), this);
        imagenesDeLaPropiedad = new ArrayList<>();


        btnGuardar.setEnabled(true);


        // Inicializar Firebase Auth
        mAuth = FirebaseAuth.getInstance();
        // Iniciar Session como usuario anónimo
        signInAnonymously();

        // registro el brodcast receiver
        br = new PropiedadReceiver();
        IntentFilter filter = new IntentFilter();
        filter.addAction(PropiedadReceiver.PROPIEDAD_CARGADA);
        this.registerReceiver(br, filter);

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
                movioSeek=true;
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
                movioSeek=true;
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
                guardarPropiedad();
            }
        });

        View.OnFocusChangeListener focusListener = new View.OnFocusChangeListener() {
            public void onFocusChange(View v, boolean hasFocus) {
             if (v instanceof EditText && !hasFocus) {
                    if (vacio((EditText) v)) {
                        ((EditText) v).setError("Campo requerido");
                    } else ((EditText) v).setError(null);
                }
            }
        };

        txtNombrePropiedad.setOnFocusChangeListener(focusListener);
        txtDetalle.setOnFocusChangeListener(focusListener);
        txtPrecio.setOnFocusChangeListener(focusListener);
        txtDireccion.setOnFocusChangeListener(focusListener);

    }
    //    ------------------------------------INICIO---MENU------------------------------------------
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu,menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.itmCargarPropiedad:
                Intent intentRegistro = new Intent(this,CargarPropiedadActivity.class);
                startActivity(intentRegistro);
                return true;
            case R.id.itmListarPropiedades:
                Intent intentCrearItem = new Intent(this,ListaPropiedades.class);
                startActivity(intentCrearItem);
                return true;
            case R.id.itmCerrarSesion:
                FirebaseAuth.getInstance().signOut();
                Toast.makeText(CargarPropiedadActivity.this, "Hasta la próxima!", Toast.LENGTH_SHORT).show();
                Intent intentCerrarSesion = new Intent(this,LoginActivity.class);
                startActivity(intentCerrarSesion);
                return true;
            case android.R.id.home: onBackPressed(); return true;
            default:
                Toast.makeText(this, "Menú no válido", Toast.LENGTH_SHORT).show();
        }
        return super.onOptionsItemSelected(item);
    }
    //    ------------------------------------FIN---MENU------------------------------------------

    private void guardarPropiedad() {
        if(validarCampos()) {
            int vencimiento = Integer.parseInt(spinnerVencimientos.getSelectedItem().toString().substring(0, 2));
            Propiedad propiedad = new Propiedad(txtNombrePropiedad.getText().toString(),
                    btnVenta.isChecked(),
                    skDormitorios.getProgress(),
                    spinnerInmuebles.getSelectedItem().toString(),
                    swCochera.isChecked(),
                    txtDireccion.getText().toString(),
                    txtCoordenadas.getText().toString(),
                    Integer.parseInt(txtPrecio.getText().toString()),
                    btnPesos.isChecked(),
                    txtDetalle.getText().toString(),
                    vencimiento);
            PropiedadConFotos propiedadConFotos = new PropiedadConFotos(propiedad, imagenesDeLaPropiedad);
            repository.insertar(propiedadConFotos);
            limpiar();
        }
        else {
            Toast.makeText(this, "Complete el formulario correctamente", Toast.LENGTH_SHORT).show();
        }
    }

    private boolean validarCampos() {
        if(
                !vacio(txtNombrePropiedad) &&
                (btnAlquiler.isChecked() || btnVenta.isChecked())
                && movioSeek
                && spinnerInmuebles.getSelectedItemPosition()!=0
                && !vacio(txtDireccion)
                && !vacio(txtPrecio)
                && (btnPesos.isChecked() || btnUsd.isChecked())
                && !vacio(txtDetalle)
                && spinnerVencimientos.getSelectedItemPosition()!=0
        ) {
            return true;
        }
        else return false;
    }

    private boolean vacio (EditText campo){
        if(campo.getText().length()==0) return true;
        else return false;
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
            Uri imgUri=Uri.parse(currentPhotoPath);
            ImagenPropiedad imagen = new ImagenPropiedad(currentPhotoPath,"");

            imagenesDeLaPropiedad.add(imagen);
            Log.d("IMAGEN PROPIEDAD", "imagenesDeLaPropiedad: "+imagenesDeLaPropiedad);
            imgPropiedad.setImageURI(imgUri);
        }
        else if (requestCode == CODIGO_MAPA && resultCode == RESULT_OK) {
            ubicacion = new LatLng(data.getDoubleExtra("lat", 0), data.getDoubleExtra("lng", 0));
            txtCoordenadas.setText("Longitud: "+String.valueOf(ubicacion.latitude)+"  Latitud: "+String.valueOf(ubicacion.longitude));
            txtCoordenadas.setTextSize(12);
        }
    }
    private File createImageFile() throws IOException {
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
            // CREAR MAPA ENVIÁNDOLE MI UBICACIÓN
            Intent localizarIntent = new Intent(CargarPropiedadActivity.this, Localizar.class);
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
                        Toast.makeText(CargarPropiedadActivity.this, "Authentication failed.",
                                Toast.LENGTH_SHORT).show();
                    }
                }
            });
    }

    @Override
    public void onResult(List result) {
        Intent i = new Intent(this,MyIntentServices.class);
        startService(i);
    }

    public void crearCanal(Context context) {
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel notificationChannel = new NotificationChannel("1", "NOTIFICACIONES", importance);
            notificationManager.createNotificationChannel(notificationChannel);
        }
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        this.unregisterReceiver(br);
    }

    private void limpiar() {
        txtNombrePropiedad.setText("");
        btnVenta.setChecked(false);
        btnAlquiler.setChecked(false);
        skDormitorios.setProgress(0);
        spinnerInmuebles.setSelection(0);
        swCochera.setChecked(false);
        txtDireccion.setText("");
        txtCoordenadas.setText("");
        txtPrecio.setText("");
        btnUsd.setChecked(false);
        btnPesos.setChecked(false);
        txtDetalle.setText("");
        spinnerVencimientos.setSelection(0);
    }
}