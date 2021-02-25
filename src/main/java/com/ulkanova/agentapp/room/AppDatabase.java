package com.ulkanova.agentapp.room;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.ulkanova.agentapp.model.ImagenPropiedad;
import com.ulkanova.agentapp.model.Propiedad;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Database(entities = {Propiedad.class, ImagenPropiedad.class}, version = 3)
public abstract class AppDatabase extends RoomDatabase {
    public abstract PropiedadDao propiedadDao();
    public abstract ImagenPropiedadDao imagenPropiedadDao();

    //    public abstract PedidoConPlatosDao pedidoConPlatosDao();
//    public abstract PedidoPlatoDao pedidoPlatosDao();
    private static volatile AppDatabase INSTANCE;
    private static final int NUMBER_OF_THREADS = 1;
    static final ExecutorService databaseWriteExecutor =
            Executors.newFixedThreadPool(NUMBER_OF_THREADS);

    static AppDatabase getInstance(final Context context) {
        if (INSTANCE == null) {
            synchronized (AppDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                            AppDatabase.class, "agentapp_database")
                            .fallbackToDestructiveMigration()
                            .build();
                }
            }
        }
        return INSTANCE;
    }
}