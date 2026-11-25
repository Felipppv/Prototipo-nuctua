package com.nuctua.persistencia;

import java.util.ArrayList;

public class ArchivoDeseos {

    private static final String NOMBRE = "deseos.txt";

    public ArrayList<String> cargar(String correo) {
        ArrayList<String> skus = new ArrayList<>();
        if (correo == null || correo.isBlank()) {
            return skus;
        }
        for (String linea : GestorArchivos.leerLineas(NOMBRE)) {
            String[] campos = linea.split(GestorArchivos.SEPARADOR_REGEX, -1);
            if (campos.length < 2 || !campos[0].equalsIgnoreCase(correo.trim())) {
                continue;
            }
            for (String sku : campos[1].split(",")) {
                String limpio = sku.trim();
                if (!limpio.isEmpty() && !skus.contains(limpio)) {
                    skus.add(limpio);
                }
            }
        }
        return skus;
    }

    public void guardar(String correo, ArrayList<String> skus) {
        if (correo == null || correo.isBlank()) {
            return;
        }
        ArrayList<String> lineas = new ArrayList<>();
        for (String linea : GestorArchivos.leerLineas(NOMBRE)) {
            String[] campos = linea.split(GestorArchivos.SEPARADOR_REGEX, -1);
            if (!campos[0].equalsIgnoreCase(correo.trim())) {
                lineas.add(linea);
            }
        }
        if (!skus.isEmpty()) {
            lineas.add(GestorArchivos.limpiar(correo.trim().toLowerCase()) + GestorArchivos.SEPARADOR
                    + GestorArchivos.limpiar(String.join(",", skus)));
        }
        GestorArchivos.escribirLineas(NOMBRE, lineas);
    }
}
