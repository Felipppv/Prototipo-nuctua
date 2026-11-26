package com.nuctua.persistencia;

import com.nuctua.modelo.ItemCarrito;
import java.util.ArrayList;

public class ArchivoCarrito {

    private static final String NOMBRE = "carrito.txt";

    public ArrayList<String[]> cargar(String correo) {
        ArrayList<String[]> registros = new ArrayList<>();
        if (correo == null || correo.isBlank()) {
            return registros;
        }
        for (String linea : GestorArchivos.leerLineas(NOMBRE)) {
            String[] campos = linea.split(GestorArchivos.SEPARADOR_REGEX, -1);
            if (campos.length >= 4 && campos[0].equalsIgnoreCase(correo.trim())
                    && campos[2].matches("\\d{1,3}") && campos[3].matches("\\d{1,4}")) {
                registros.add(new String[]{campos[1].trim(), campos[2], campos[3]});
            }
        }
        return registros;
    }

    public void guardar(String correo, Iterable<ItemCarrito> items) {
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
        String s = GestorArchivos.SEPARADOR;
        for (ItemCarrito item : items) {
            lineas.add(GestorArchivos.limpiar(correo.trim().toLowerCase()) + s
                    + GestorArchivos.limpiar(item.getZapato().getSku()) + s + item.getTalla() + s + item.getCantidad());
        }
        GestorArchivos.escribirLineas(NOMBRE, lineas);
    }
}
