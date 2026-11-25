package com.nuctua.persistencia;

import com.nuctua.util.Recursos;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

public class ArchivoImagenes {

    public static final int MAXIMO = 3;
    public static final String[] EXTENSIONES = {"jpg", "jpeg", "png"};

    private static final String CARPETA = "imagenes";

    public static String extension(File archivo) {
        String nombre = archivo.getName().toLowerCase();
        int punto = nombre.lastIndexOf('.');
        return punto < 0 ? "" : nombre.substring(punto + 1);
    }

    public static boolean esExtensionValida(File archivo) {
        for (String extension : EXTENSIONES) {
            if (extension.equals(extension(archivo))) {
                return true;
            }
        }
        return false;
    }

    public String copiar(File origen, String sku, int numero) {
        String nombre = base(sku, numero) + "." + extension(origen);
        File destino = new File(carpeta(), nombre);
        try (FileInputStream entrada = new FileInputStream(origen); FileOutputStream salida = new FileOutputStream(destino)) {
            byte[] bloque = new byte[8192];
            int leidos = entrada.read(bloque);
            while (leidos > 0) {
                salida.write(bloque, 0, leidos);
                leidos = entrada.read(bloque);
            }
        } catch (IOException e) {
            destino.delete();
            throw new IllegalStateException("No se pudo copiar la imagen " + origen.getName(), e);
        }
        Recursos.olvidarImagen(Recursos.PREFIJO_IMAGEN_LOCAL + nombre);
        return Recursos.PREFIJO_IMAGEN_LOCAL + nombre;
    }

    public String buscar(String sku, int numero) {
        for (String extension : EXTENSIONES) {
            String nombre = base(sku, numero) + "." + extension;
            if (new File(carpeta(), nombre).isFile()) {
                return Recursos.PREFIJO_IMAGEN_LOCAL + nombre;
            }
        }
        return null;
    }

    public void borrarDe(String sku) {
        for (int numero = 1; numero <= MAXIMO; numero++) {
            for (String extension : EXTENSIONES) {
                File archivo = new File(carpeta(), base(sku, numero) + "." + extension);
                if (archivo.isFile() && archivo.delete()) {
                    Recursos.olvidarImagen(Recursos.PREFIJO_IMAGEN_LOCAL + archivo.getName());
                }
            }
        }
    }

    private String base(String sku, int numero) {
        return sku.toLowerCase() + (numero == 1 ? "" : "-" + numero);
    }

    private File carpeta() {
        File carpeta = new File(GestorArchivos.getCarpetaDatos(), CARPETA);
        if (!carpeta.exists()) {
            carpeta.mkdirs();
        }
        return carpeta;
    }
}
