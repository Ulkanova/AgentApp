package com.ulkanova.agentapp.room;

import android.os.AsyncTask;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Transaction;
import androidx.room.Update;

import com.ulkanova.agentapp.model.ImagenPropiedad;
import com.ulkanova.agentapp.model.Propiedad;

import java.util.ArrayList;
import java.util.List;

@Dao
public interface PropiedadDao {

    @Query("SELECT * FROM propiedad")
    List<Propiedad> getAll();

    @Query("SELECT * FROM propiedad WHERE idPropiedad = :id")
    Propiedad buscar(long id);

    @Transaction
    @Insert
    long insertarPropiedad(Propiedad propiedad);

    @Insert
    void insertarImagenes(List<ImagenPropiedad> imagenes);

    @Delete
    void borrar(Propiedad propiedad);

    @Update
    void actualizar(Propiedad propiedad);

    @Query("SELECT * FROM propiedad")
    List<Propiedad> buscarTodas();

    @Transaction
    @Query("SELECT * FROM imagenpropiedad WHERE propiedad=:idPropiedad")
    List <ImagenPropiedad> getImagenes(long idPropiedad);

}

interface OnPropiedadResultCallback {
    void onResult(List<Propiedad> propiedad);
}

interface OnImagenPropiedadResultCallback {
    void onResultImagen(List<ImagenPropiedad> imagen);
}
class BuscarPropiedadById extends AsyncTask<Long, Void, Propiedad> {

    private PropiedadDao dao;
    private OnPropiedadResultCallback callback;

    public BuscarPropiedadById(PropiedadDao dao, OnPropiedadResultCallback context) {
        this.dao = dao;
        this.callback = context;
    }

    @Override
    protected Propiedad doInBackground(Long... longs) {
        Propiedad propiedad = dao.buscar(longs[0]);
        return propiedad;
    }

    @Override
    protected void onPostExecute(Propiedad propiedad) {
        super.onPostExecute(propiedad);
        List<Propiedad> lista = new ArrayList<>();
        lista.add(propiedad);
        callback.onResult(lista);
    }
}
class BuscarPropiedades extends AsyncTask<String, Void, List<Propiedad>> {

    private PropiedadDao dao;
    private OnPropiedadResultCallback callback;

    public BuscarPropiedades(PropiedadDao dao, OnPropiedadResultCallback context) {
        this.dao = dao;
        this.callback = context;
    }

    @Override
    protected List<Propiedad> doInBackground(String... strings) {
        List<Propiedad> propiedades = dao.buscarTodas();
        return propiedades;
    }

    @Override
    protected void onPostExecute(List<Propiedad> propiedades) {
        super.onPostExecute(propiedades);
        callback.onResult(propiedades);
    }
}

class BuscarImagenes extends AsyncTask<Long, Void, List<ImagenPropiedad>> {

    private PropiedadDao dao;
    private OnImagenPropiedadResultCallback callback;

    public BuscarImagenes(PropiedadDao dao, OnImagenPropiedadResultCallback context) {
        this.dao = dao;
        this.callback = context;
    }

    @Override
    protected List<ImagenPropiedad> doInBackground(Long... longs) {
        List<ImagenPropiedad> imagenes = dao.getImagenes(longs[0]);
        return imagenes;
    }

    @Override
    protected void onPostExecute(List<ImagenPropiedad> imagenes) {
        super.onPostExecute(imagenes);
        callback.onResultImagen(imagenes);
    }
}
