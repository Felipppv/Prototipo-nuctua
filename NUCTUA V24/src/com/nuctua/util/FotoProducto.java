package com.nuctua.util;

import com.nuctua.persistencia.ArchivoImagenes;
import java.io.File;
import javafx.scene.image.Image;

public final class FotoProducto {

    private static final long MAXIMO_BYTES = 8L * 1024 * 1024;
    private static final int MINIMO_LADO = 300;

    private FotoProducto() {
    }

    public static String revisar(File archivo) {
        if (archivo == null || !archivo.isFile()) {
            return "No encontramos ese archivo.";
        }
        if (!ArchivoImagenes.esExtensionValida(archivo)) {
            return "Elige una foto PNG o JPG.";
        }
        if (archivo.length() > MAXIMO_BYTES) {
            return "La foto pesa más de 8 MB. Elige una más liviana.";
        }
        Image imagen = new Image(archivo.toURI().toString());
        if (imagen.isError() || imagen.getWidth() <= 0) {
            return "No pudimos leer esa foto. Prueba con otra.";
        }
        if (imagen.getWidth() < MINIMO_LADO || imagen.getHeight() < MINIMO_LADO) {
            return "La foto es muy pequeña. Debe medir al menos 300 x 300 px.";
        }
        return "";
    }

    public static Image cargar(File archivo) {
        return new Image(archivo.toURI().toString(), 900, 0, true, true);
    }
}
