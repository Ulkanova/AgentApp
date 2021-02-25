package com.ulkanova.agentapp.room;

import android.app.Application;
import android.os.AsyncTask;
import android.util.Log;

import com.ulkanova.agentapp.model.ImagenPropiedad;
import com.ulkanova.agentapp.model.Propiedad;

import java.util.ArrayList;
import java.util.List;

public class AppRepositoryRoom implements OnPropiedadResultCallback, OnImagenPropiedadResultCallback{
    private PropiedadDao propiedadDao;
    private ImagenPropiedadDao imagenPropiedadDao;
    private static OnResultCallback callback;

    public AppRepositoryRoom(Application application, OnResultCallback context){
        AppDatabase db = AppDatabase.getInstance(application);
        propiedadDao = db.propiedadDao();
        imagenPropiedadDao = db.imagenPropiedadDao();
        callback = context;
    }

    public void insertar(PropiedadConFotos propiedadConFotos){
        new insertAsync(propiedadDao).execute(propiedadConFotos);
    }

    public void insertarPropiedad(final Propiedad propiedad){
        AppDatabase.databaseWriteExecutor.execute(new Runnable() {
            @Override
            public void run() {
                propiedadDao.insertarPropiedad(propiedad);
            }
        });
        callback.onResult(new ArrayList());
    }

    public void borrarPropiedad(final Propiedad propiedad){
        AppDatabase.databaseWriteExecutor.execute(new Runnable() {
            @Override
            public void run() {
                propiedadDao.borrar(propiedad);
            }
        });
    }

    public void actualizarPropiedad(final Propiedad propiedad){
        AppDatabase.databaseWriteExecutor.execute(new Runnable() {
            @Override
            public void run() {
                propiedadDao.actualizar(propiedad);
            }
        });

    }

    public void insertarImagen(final ImagenPropiedad imagen){
        AppDatabase.databaseWriteExecutor.execute(new Runnable() {
            @Override
            public void run() {
                imagenPropiedadDao.insertar(imagen);
            }
        });
        callback.onResult(new ArrayList());
    }

    @Override
    public void onResultImagen(List<ImagenPropiedad> imagen) {
        callback.onResult(imagen);
    }

    private static class insertAsync extends AsyncTask<PropiedadConFotos, Void, Void> {
        private PropiedadDao propiedadDaoAsync;

        insertAsync(PropiedadDao propiedadDao) {
            propiedadDaoAsync = propiedadDao;
        }

        @Override
        protected Void doInBackground(PropiedadConFotos... propiedadConFotos) {

            long identifier = propiedadDaoAsync.insertarPropiedad(propiedadConFotos[0].propiedad);

            for (ImagenPropiedad imagen : propiedadConFotos[0].imagenes) {
                imagen.setPropiedad(identifier);
            }
            propiedadDaoAsync.insertarImagenes(propiedadConFotos[0].imagenes);
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            callback.onResult(new ArrayList());
        }
    }
    public void buscarPropiedad(Long id) { new BuscarPropiedadById(propiedadDao, this).execute(id);  }

    public void buscarImagenes(Long id) { new BuscarImagenes(propiedadDao, this).execute(id);  }

    public void buscarTodas() {
        new BuscarPropiedades(propiedadDao, this).execute();
    }

    @Override
    public void onResult(List<Propiedad> propiedad) {
        callback.onResult(propiedad);
    }

    public interface OnResultCallback<T> {
        void onResult(List<T> result);
    }

}
