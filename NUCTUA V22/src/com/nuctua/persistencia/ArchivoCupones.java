package com.nuctua.persistencia;

import com.nuctua.estructuras.ListaEnlazada;
import com.nuctua.modelo.Cupon;
import com.nuctua.modelo.TipoCupon;
import java.util.ArrayList;

public class ArchivoCupones {

    private static final String NOMBRE = "cupones.txt";
    private static final String APLICADOS = "carrito_cupon.txt";

    public ListaEnlazada<Cupon> cargar() {
        ListaEnlazada<Cupon> cupones = new ListaEnlazada<>();
        for (String linea : GestorArchivos.leerLineas(NOMBRE)) {
            String[] c = linea.split(GestorArchivos.SEPARADOR_REGEX, -1);
            if (c.length < 5 || c[0].isBlank() || !c[2].matches("\\d{1,8}") || !c[3].matches("\\d{1,9}")) {
                continue;
            }
            TipoCupon tipo = tipoDe(c[1]);
            if (tipo != null) {
                cupones.insertarFinal(new Cupon(c[0].trim().toUpperCase(), tipo, Integer.parseInt(c[2]),
                        Integer.parseInt(c[3]), Boolean.parseBoolean(c[4].trim())));
            }
        }
        return cupones;
    }

    private TipoCupon tipoDe(String texto) {
        for (TipoCupon tipo : TipoCupon.values()) {
            if (tipo.name().equalsIgnoreCase(texto.trim())) {
                return tipo;
            }
        }
        return null;
    }

    public String codigoAplicado(String correo) {
        if (correo == null || correo.isBlank()) {
            return "";
        }
        for (String linea : GestorArchivos.leerLineas(APLICADOS)) {
            String[] c = linea.split(GestorArchivos.SEPARADOR_REGEX, -1);
            if (c.length >= 2 && c[0].equalsIgnoreCase(correo.trim())) {
                return c[1].trim();
            }
        }
        return "";
    }

    public void guardarAplicado(String correo, String codigo) {
        if (correo == null || correo.isBlank()) {
            return;
        }
        ArrayList<String> lineas = new ArrayList<>();
        for (String linea : GestorArchivos.leerLineas(APLICADOS)) {
            if (!linea.split(GestorArchivos.SEPARADOR_REGEX, -1)[0].equalsIgnoreCase(correo.trim())) {
                lineas.add(linea);
            }
        }
        if (codigo != null && !codigo.isBlank()) {
            lineas.add(GestorArchivos.limpiar(correo.toLowerCase()) + GestorArchivos.SEPARADOR
                    + GestorArchivos.limpiar(codigo.toUpperCase()));
        }
        GestorArchivos.escribirLineas(APLICADOS, lineas);
    }
}
