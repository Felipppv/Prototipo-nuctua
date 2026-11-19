package com.nuctua.vista;

public enum Area {
    ACCESO("Acceso"),
    CLIENTE("Cliente"),
    ADMINISTRADOR("Administrador"),
    POPUP("Ventana emergente");

    private final String nombre;

    Area(String nombre) {
        this.nombre = nombre;
    }

    public String getNombre() {
        return nombre;
    }
}
