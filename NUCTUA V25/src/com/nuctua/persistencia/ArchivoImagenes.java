package com.nuctua.persistencia;

import com.nuctua.estructuras.ListaEnlazada;
import com.nuctua.modelo.FotoPar;
import com.nuctua.util.Recursos;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

public class ArchivoImagenes {

    public static final int MAXIMO = 3;
    public static final String[] EXTENSIONES = {"jpg", "jpeg", "png"};

    private static final String CARPETA = "imagenes";
    private static final String TEMPORAL = "nuevo-";

    public static String extension(String nombre) {
        int punto = nombre.lastIndexOf('.');
        return punto < 0 ? "" : nombre.substring(punto + 1).toLowerCase();
    }

    public static boolean esExtensionValida(File archivo) {
        for (String extension : EXTENSIONES) {
            if (extension.equals(extension(archivo.getName()))) {
                return true;
            }
        }
        return false;
    }

    public String preparar(ListaEnlazada<FotoPar> fotos, String sku) {
        descartar(sku);
        String principal = null;
        int numero = 1;
        for (FotoPar foto : fotos) {
            String ruta = foto.getRuta();
            if (foto.esNueva() || esLocal(ruta)) {
                File origen = foto.esNueva() ? foto.getArchivo() : archivoDe(ruta);
                String nombre = base(sku, numero) + "." + extension(origen.getName());
                copiar(origen, new File(carpeta(), TEMPORAL + nombre));
                ruta = Recursos.PREFIJO_IMAGEN_LOCAL + nombre;
            }
            principal = principal == null ? ruta : principal;
            numero++;
        }
        return principal;
    }

    public void confirmar(String sku) {
        for (int numero = 1; numero <= MAXIMO; numero++) {
            for (String extension : EXTENSIONES) {
                String nombre = base(sku, numero) + "." + extension;
                File definitivo = new File(carpeta(), nombre);
                File temporal = new File(carpeta(), TEMPORAL + nombre);
                if (definitivo.isFile()) {
                    definitivo.delete();
                }
                if (temporal.isFile()) {
                    temporal.renameTo(definitivo);
                }
                Recursos.olvidarImagen(Recursos.PREFIJO_IMAGEN_LOCAL + nombre);
            }
        }
    }

    public void descartar(String sku) {
        borrar(TEMPORAL, sku);
    }

    public void borrarDe(String sku) {
        borrar("", sku);
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

    public static boolean esLocal(String ruta) {
        return ruta != null && ruta.startsWith(Recursos.PREFIJO_IMAGEN_LOCAL);
    }

    private void borrar(String prefijo, String sku) {
        for (int numero = 1; numero <= MAXIMO; numero++) {
            for (String extension : EXTENSIONES) {
                String nombre = prefijo + base(sku, numero) + "." + extension;
                File archivo = new File(carpeta(), nombre);
                if (archivo.isFile() && archivo.delete()) {
                    Recursos.olvidarImagen(Recursos.PREFIJO_IMAGEN_LOCAL + nombre);
                }
            }
        }
    }

    private void copiar(File origen, File destino) {
        try (FileInputStream entrada = new FileInputStream(origen); FileOutputStream salida = new FileOutputStream(destino)) {
            byte[] bloque = new byte[8192];
            int leidos = entrada.read(bloque);
            while (leidos > 0) {
                salida.write(bloque, 0, leidos);
                leidos = entrada.read(bloque);
            }
        } catch (IOException e) {
            throw new IllegalStateException("No se pudo copiar la imagen " + origen.getName(), e);
        }
    }

    private File archivoDe(String ruta) {
        return new File(carpeta(), ruta.substring(Recursos.PREFIJO_IMAGEN_LOCAL.length()));
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
