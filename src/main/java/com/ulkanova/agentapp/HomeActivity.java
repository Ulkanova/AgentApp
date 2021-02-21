package com.ulkanova.agentapp;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.Bundle;
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

import com.ulkanova.agentapp.maps.PermissionUtils;

import java.io.ByteArrayOutputStream;

public class HomeActivity extends AppCompatActivity {
    Toolbar toolbar;
    TextView toolbarTitle, dormitorios;
    SeekBar dormitoriosSeek;
    Spinner spinnerInmuebles, spinnerVencimientos;
    ImageButton btnCamara, btnLocalizar;
    Button btnGuardar;
    ImageView imgPropiedad;

    static final int CAMARA_REQUEST = 1;
    static final int GALERIA_REQUEST = 2;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1 ;
    private static final int CODIGO_MAPA = 222;

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
        imgPropiedad = findViewById(R.id.imgPropiedad);

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
    }

    private void lanzarCamara() {
        Intent camaraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(camaraIntent, CAMARA_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if ((requestCode == CAMARA_REQUEST || requestCode == GALERIA_REQUEST) && resultCode == RESULT_OK) {
            Bundle extras = data.getExtras();
            Bitmap imageBitmap = (Bitmap) extras.get("data");
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            imageBitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
            byte[] dato = baos.toByteArray(); // Imagen en arreglo de bytes
            imgPropiedad.setImageBitmap(imageBitmap);
//            guardarImagen(dato);
        }
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

}