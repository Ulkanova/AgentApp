package com.ulkanova.agentapp.model;

import androidx.room.Embedded;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.PrimaryKey;
import androidx.room.Relation;

import java.util.List;

@Entity
public class ImagenPropiedad {
    @PrimaryKey(autoGenerate = true)
    public long idImagen;
    @ForeignKey
            (entity = Propiedad.class,
                    parentColumns = "idPropiedad",
                    childColumns = "propiedad"
            )
    public long propiedad;
    public String pathLocal;
    public String uri;

    public ImagenPropiedad(String pathLocal, String uri) {
        this.pathLocal = pathLocal;
        this.uri = uri;
    }

    public long getIdImagen() {
        return idImagen;
    }

    public long getPropiedad() {
        return propiedad;
    }

    public void setPropiedad(long propiedad) {
        this.propiedad = propiedad;
    }

    public String getPathLocal() {
        return pathLocal;
    }

    public void setPathLocal(String pathLocal) {
        this.pathLocal = pathLocal;
    }

    public String getUri() {
        return uri;
    }

    public void setUri(String uri) {
        this.uri = uri;
    }
}

