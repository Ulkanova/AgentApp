package com.ulkanova.agentapp.model;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class Propiedad {
    @PrimaryKey (autoGenerate = true)
    public long idPropiedad;
    public String nombre;
    public boolean venta;
    public int dormitorios;
    public String tipo;
    public boolean cochera;
    public String direccion;
    public String coordenadas;
    public int precio;
    public boolean pesos;
    public String detalle;
    public int vencimiento;

    public Propiedad(String nombre, boolean venta, int dormitorios, String tipo, boolean cochera, String direccion, String coordenadas, int precio, boolean pesos, String detalle, int vencimiento) {
        this.nombre = nombre;
        this.venta = venta;
        this.dormitorios = dormitorios;
        this.tipo = tipo;
        this.cochera = cochera;
        this.direccion = direccion;
        this.coordenadas = coordenadas;
        this.precio = precio;
        this.pesos = pesos;
        this.detalle = detalle;
        this.vencimiento = vencimiento;
    }

    public long getIdPropiedad() {
        return idPropiedad;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public boolean isVenta() {
        return venta;
    }

    public void setVenta(boolean venta) {
        this.venta = venta;
    }

    public int getDormitorios() {
        return dormitorios;
    }

    public void setDormitorios(int dormitorios) {
        this.dormitorios = dormitorios;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public boolean isCochera() {
        return cochera;
    }

    public void setCochera(boolean cochera) {
        this.cochera = cochera;
    }

    public String getDireccion() {
        return direccion;
    }

    public void setDireccion(String direccion) {
        this.direccion = direccion;
    }

    public String getCoordenadas() {
        return coordenadas;
    }

    public void setCoordenadas(String coordenadas) {
        this.coordenadas = coordenadas;
    }

    public int getPrecio() {
        return precio;
    }

    public void setPrecio(int precio) {
        this.precio = precio;
    }

    public boolean isPesos() {
        return pesos;
    }

    public void setPesos(boolean pesos) {
        this.pesos = pesos;
    }

    public String getDetalle() {
        return detalle;
    }

    public void setDetalle(String detalle) {
        this.detalle = detalle;
    }

    public int getVencimiento() {
        return vencimiento;
    }

    public void setVencimiento(int vencimiento) {
        this.vencimiento = vencimiento;
    }
}
