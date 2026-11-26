package com.nuctua.modelo;

import java.io.File;

public class FotoPar {

    private final File archivo;
    private final String ruta;

    private FotoPar(File archivo, String ruta) {
        this.archivo = archivo;
        this.ruta = ruta;
    }

    public static FotoPar nueva(File archivo) {
        return new FotoPar(archivo, null);
    }

    public static FotoPar guardada(String ruta) {
        return new FotoPar(null, ruta);
    }

    public boolean esNueva() {
        return archivo != null;
    }

    public File getArchivo() {
        return archivo;
    }

    public String getRuta() {
        return ruta;
    }

    public boolean esMisma(FotoPar otra) {
        return esNueva() ? archivo.equals(otra.archivo) : ruta.equals(otra.ruta);
    }
}
