package com.ulkanova.agentapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.ulkanova.agentapp.model.Propiedad;
import com.ulkanova.agentapp.model.PropiedadAdapter;
import com.ulkanova.agentapp.room.AppRepositoryRoom;

import java.util.ArrayList;
import java.util.List;

public class ListaPropiedades extends AppCompatActivity implements PropiedadAdapter.OnPropiedadListener, AppRepositoryRoom.OnResultCallback {
    Toolbar toolbar;
    TextView toolbarTitle;
    private static PropiedadAdapter mAdapter;
    private RecyclerView recyclerView;
    private RecyclerView.LayoutManager layoutManager;
    AppRepositoryRoom repository;
    List<Propiedad> propiedades = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lista_propiedades);
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        toolbarTitle = findViewById(R.id.toolbarText);
        toolbarTitle.setText("Lista de Propiedades");
        recyclerView=findViewById(R.id.recyclerPropiedades);
        recyclerView.setHasFixedSize(true);
        layoutManager=new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        repository = new AppRepositoryRoom(this.getApplication(), this);
        repository.buscarTodas();

    }

    @Override
    public void onPropiedadClick(int posicion) {
    }

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
                Toast.makeText(ListaPropiedades.this, "Hasta la próxima!", Toast.LENGTH_SHORT).show();
                Intent intentCerrarSesion = new Intent(this,LoginActivity.class);
                startActivity(intentCerrarSesion);
                return true;
            case android.R.id.home: onBackPressed(); return true;
            default:
                Toast.makeText(this, "Menú no válido", Toast.LENGTH_SHORT).show();
        }
        return super.onOptionsItemSelected(item);
    }
    @Override
    public void onResult(List result) {
        mAdapter = new PropiedadAdapter(result,this);
        Log.d("PROPIEDADES RESULT", " EN LISTA PROPIEDADES onResult: "+result);
        propiedades = result;
        recyclerView.setAdapter(mAdapter);
    }
}