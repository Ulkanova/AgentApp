package com.ulkanova.agentapp.room;

import androidx.room.Embedded;
import androidx.room.Relation;

import com.ulkanova.agentapp.model.ImagenPropiedad;
import com.ulkanova.agentapp.model.Propiedad;

import java.util.List;


public class PropiedadConFotos {
    @Embedded
    public Propiedad propiedad;
    @Relation(
            parentColumn = "idPropiedad",
            entityColumn = "propiedad"
    )
    public List<ImagenPropiedad> imagenes;

    public PropiedadConFotos(Propiedad propiedad, List<ImagenPropiedad> imagenes) {
        this.propiedad = propiedad;
        this.imagenes = imagenes;
    }
//    public Propiedad getPropiedad() {
//        return propiedad;
//    }
//
//    public void setPropiedad(Propiedad propiedad) {
//        this.propiedad = propiedad;
//    }
//
//    public List<ImagenPropiedad> getImagenes() {
//        return imagenes;
//    }
//
//    public void setImagenes(List<ImagenPropiedad> imagenes) {
//        this.imagenes = imagenes;
//    }
}
