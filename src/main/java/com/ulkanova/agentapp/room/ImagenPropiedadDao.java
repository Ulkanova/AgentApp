package com.ulkanova.agentapp.room;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Transaction;
import androidx.room.Update;

import com.ulkanova.agentapp.model.ImagenPropiedad;

import java.util.List;

@Dao
public interface ImagenPropiedadDao {

    @Query("SELECT * FROM imagenpropiedad")
    List<ImagenPropiedad> getAll();

    @Query("SELECT * FROM imagenpropiedad WHERE idImagen = :id")
    ImagenPropiedad buscar(long id);

    @Insert
    void insertar(ImagenPropiedad imagen);

    @Delete
    void borrar(ImagenPropiedad imagen);

    @Update
    void actualizar(ImagenPropiedad imagen);

}
