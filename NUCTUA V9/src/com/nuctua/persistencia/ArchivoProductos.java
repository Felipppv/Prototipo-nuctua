package com.nuctua.persistencia;

import com.nuctua.estructuras.ListaEnlazada;
import com.nuctua.modelo.TallaStock;
import com.nuctua.modelo.Zapato;
import java.util.ArrayList;

public class ArchivoProductos {

    private static final String NOMBRE = "productos.txt";

    public ListaEnlazada<Zapato> cargar() {
        ListaEnlazada<Zapato> productos = new ListaEnlazada<>();
        for (String linea : GestorArchivos.leerLineas(NOMBRE)) {
            String[] c = linea.split(GestorArchivos.SEPARADOR_REGEX, -1);
            if (c.length < 10) {
                continue;
            }
            Zapato zapato = new Zapato(c[0], c[1], c[2], c[3], c[4], Integer.parseInt(c[5]), c[6],
                    Boolean.parseBoolean(c[7]), c[8]);
            if (!c[9].isBlank()) {
                for (String par : c[9].split(",")) {
                    String[] tallaStock = par.split(":");
                    zapato.agregarTalla(Integer.parseInt(tallaStock[0].trim()), Integer.parseInt(tallaStock[1].trim()));
                }
            }
            productos.insertarFinal(zapato);
        }
        return productos;
    }

    public void guardar(ListaEnlazada<Zapato> productos) {
        ArrayList<String> lineas = new ArrayList<>();
        for (Zapato z : productos) {
            StringBuilder tallas = new StringBuilder();
            for (TallaStock t : z.getTallas()) {
                if (tallas.length() > 0) {
                    tallas.append(',');
                }
                tallas.append(t.getTalla()).append(':').append(t.getStock());
            }
            String s = GestorArchivos.SEPARADOR;
            lineas.add(GestorArchivos.limpiar(z.getSku()) + s + GestorArchivos.limpiar(z.getNombre()) + s
                    + GestorArchivos.limpiar(z.getMarca()) + s + GestorArchivos.limpiar(z.getCategoria()) + s
                    + GestorArchivos.limpiar(z.getColores()) + s + z.getPrecio() + s
                    + GestorArchivos.limpiar(z.getImagen()) + s + z.isActivo() + s
                    + GestorArchivos.limpiar(z.getDescripcion()) + s + tallas);
        }
        GestorArchivos.escribirLineas(NOMBRE, lineas);
    }
}
