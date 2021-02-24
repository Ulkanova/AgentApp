package com.ulkanova.agentapp;

import androidx.fragment.app.FragmentActivity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnSuccessListener;

public class Localizar extends FragmentActivity implements OnMapReadyCallback {
    private FusedLocationProviderClient fusedLocationClient;
    private GoogleMap myMap;
    private LatLng ubicacionUsuario, ubicacionMarcador, ubicacion;
    private MarkerOptions marcador;
    Button btnConfirmar;
    private static final int CODIGO_MAPA = 222;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_localizar);
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        btnConfirmar = findViewById(R.id.confirmarUbicacion);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        btnConfirmar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("MARCADOR", "onClick: "+ubicacionMarcador);
                Log.d("MARCADOR USUARIO", "onClick: "+ubicacionUsuario);
                if(ubicacionMarcador==null){
                    ubicacion = ubicacionUsuario;
                }
                else{
                    ubicacion = ubicacionMarcador;
                }
                Intent localizado = new Intent();
                localizado.putExtra("lat", ubicacion.latitude);
                localizado.putExtra("lng", ubicacion.longitude);
                setResult(Activity.RESULT_OK, localizado);
                finish();
            }
        });
    }


    @SuppressLint("MissingPermission")
    @Override
    public void onMapReady(GoogleMap googleMap) {
        fusedLocationClient.getLastLocation()
                .addOnSuccessListener(this, new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {
                        if (location != null) {
                            ubicacionUsuario= new LatLng(location.getLatitude(),location.getLongitude());
                            CameraPosition cUbicacion = new CameraPosition.Builder().target(ubicacionUsuario).zoom(14f).build();
                            myMap.animateCamera(CameraUpdateFactory.newCameraPosition(cUbicacion));
                            btnConfirmar.setEnabled(true);
                        }
                        else if (! localizacionActiva()){
                            new AlertDialog.Builder(Localizar.this)
                                    .setMessage( "Active la ubicación del dispositivo" )
                                    .setPositiveButton( "Activar" , new
                                            DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick (DialogInterface paramDialogInterface , int paramInt) {
                                                    startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS )) ;
                                                }
                                            })
                                    .setNegativeButton( "Cancelar" , null )
                                    .show();
                        }
                    }
                });
        myMap = googleMap;
        myMap.setMyLocationEnabled(true);
        myMap.getUiSettings().setScrollGesturesEnabled(true);
        myMap.getUiSettings().setZoomControlsEnabled(true);
        myMap.getUiSettings().setCompassEnabled(true);
        myMap.setOnMapLongClickListener(new GoogleMap.OnMapLongClickListener() {
            @Override
            public void onMapLongClick(LatLng latLng) {
                    myMap.clear();
                    marcador = new MarkerOptions().position(latLng).draggable(true).title("Ubicación propiedad");
                    ubicacionMarcador = latLng;
                    myMap.addMarker(marcador);
                    btnConfirmar.setEnabled(true);
            }
        });

    }

    private boolean localizacionActiva() {
        LocationManager lm = (LocationManager)  getSystemService(Context.LOCATION_SERVICE) ;
        boolean gps_enabled = false;
        boolean network_enabled = false;
        try {
            gps_enabled = lm.isProviderEnabled(LocationManager.GPS_PROVIDER) ;
        } catch (Exception e) {
            e.printStackTrace() ;
        }
        try {
            network_enabled = lm.isProviderEnabled(LocationManager.NETWORK_PROVIDER) ;
        } catch (Exception e) {
            e.printStackTrace() ;
        }
       return (gps_enabled || network_enabled);
    }
}