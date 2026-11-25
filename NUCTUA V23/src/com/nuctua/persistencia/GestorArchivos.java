package com.nuctua.persistencia;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.FileWriter;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;

public final class GestorArchivos {

    public static final String SEPARADOR = "|";
    public static final String SEPARADOR_REGEX = "\\|";

    private static final String RUTA_SEMILLA = "/com/nuctua/datos/";
    private static File carpetaDatos = new File(System.getProperty("user.dir"), "datos");

    private GestorArchivos() {
    }

    public static void usarCarpeta(File carpeta) {
        carpetaDatos = carpeta;
    }

    public static File getCarpetaDatos() {
        if (!carpetaDatos.exists()) {
            carpetaDatos.mkdirs();
        }
        return carpetaDatos;
    }

    public static File getArchivo(String nombre) {
        File archivo = new File(getCarpetaDatos(), nombre);
        if (!archivo.exists()) {
            copiarSemilla(nombre, archivo);
        }
        return archivo;
    }

    public static ArrayList<String> leerLineas(String nombre) {
        ArrayList<String> lineas = new ArrayList<>();
        File archivo = getArchivo(nombre);
        try (BufferedReader lector = new BufferedReader(new FileReader(archivo, StandardCharsets.UTF_8))) {
            String linea;
            while ((linea = lector.readLine()) != null) {
                if (!linea.isBlank()) {
                    lineas.add(linea.strip());
                }
            }
        } catch (IOException e) {
            throw new IllegalStateException("No se pudo leer " + archivo.getAbsolutePath(), e);
        }
        return lineas;
    }

    public static void escribirLineas(String nombre, ArrayList<String> lineas) {
        escribirEn(new File(getCarpetaDatos(), nombre), lineas);
    }

    public static void escribirEn(File archivo, ArrayList<String> lineas) {
        File carpeta = archivo.getAbsoluteFile().getParentFile();
        if (carpeta != null && !carpeta.exists()) {
            carpeta.mkdirs();
        }
        try (BufferedWriter escritor = new BufferedWriter(new FileWriter(archivo, StandardCharsets.UTF_8, false))) {
            for (String linea : lineas) {
                escritor.write(linea);
                escritor.newLine();
            }
        } catch (IOException e) {
            throw new IllegalStateException("No se pudo escribir " + archivo.getAbsolutePath(), e);
        }
    }

    public static String limpiar(String texto) {
        if (texto == null) {
            return "";
        }
        return texto.replace(SEPARADOR, "/").replace("\r", " ").replace("\n", " ").strip();
    }

    public static void reemplazarDe(String nombre, String clave, ArrayList<String> nuevas) {
        ArrayList<String> lineas = new ArrayList<>();
        for (String linea : leerLineas(nombre)) {
            if (!linea.split(SEPARADOR_REGEX, -1)[0].equalsIgnoreCase(clave.trim())) {
                lineas.add(linea);
            }
        }
        lineas.addAll(nuevas);
        escribirLineas(nombre, lineas);
    }

    private static void copiarSemilla(String nombre, File destino) {
        try (InputStream entrada = GestorArchivos.class.getResourceAsStream(RUTA_SEMILLA + nombre)) {
            if (entrada == null) {
                destino.createNewFile();
                return;
            }
            try (FileOutputStream salida = new FileOutputStream(destino)) {
                entrada.transferTo(salida);
            }
        } catch (IOException e) {
            throw new IllegalStateException("No se pudo preparar " + destino.getAbsolutePath(), e);
        }
    }
}
