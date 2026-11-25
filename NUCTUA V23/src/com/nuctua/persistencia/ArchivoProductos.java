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
            Zapato zapato = leer(linea);
            if (zapato != null) {
                productos.insertarFinal(zapato);
            }
        }
        return productos;
    }

    public void guardar(ListaEnlazada<Zapato> productos) {
        ArrayList<String> lineas = new ArrayList<>();
        for (Zapato z : productos) {
            lineas.add(escribir(z));
        }
        for (String linea : GestorArchivos.leerLineas(NOMBRE)) {
            if (leer(linea) == null) {
                lineas.add(linea);
            }
        }
        GestorArchivos.escribirLineas(NOMBRE, lineas);
    }

    private Zapato leer(String linea) {
        String[] c = linea.split(GestorArchivos.SEPARADOR_REGEX, -1);
        if (c.length < 10 || c[0].isBlank() || !esEntero(c[5])) {
            return null;
        }
        Zapato zapato = new Zapato(c[0].trim(), c[1], c[2], c[3], c[4], Integer.parseInt(c[5].trim()), c[6],
                Boolean.parseBoolean(c[7].trim()), c[8]);
        if (c[9].isBlank()) {
            return zapato;
        }
        for (String par : c[9].split(",")) {
            String[] tallaStock = par.split(":");
            if (tallaStock.length != 2 || !esEntero(tallaStock[0]) || !esEntero(tallaStock[1])) {
                return null;
            }
            zapato.agregarTalla(Integer.parseInt(tallaStock[0].trim()), Integer.parseInt(tallaStock[1].trim()));
        }
        return zapato;
    }

    private String escribir(Zapato z) {
        StringBuilder tallas = new StringBuilder();
        for (TallaStock t : z.getTallas()) {
            if (tallas.length() > 0) {
                tallas.append(',');
            }
            tallas.append(t.getTalla()).append(':').append(t.getStock());
        }
        String s = GestorArchivos.SEPARADOR;
        return GestorArchivos.limpiar(z.getSku()) + s + GestorArchivos.limpiar(z.getNombre()) + s
                + GestorArchivos.limpiar(z.getMarca()) + s + GestorArchivos.limpiar(z.getCategoria()) + s
                + GestorArchivos.limpiar(z.getColores()) + s + z.getPrecio() + s
                + GestorArchivos.limpiar(z.getImagen()) + s + z.isActivo() + s
                + GestorArchivos.limpiar(z.getDescripcion()) + s + tallas;
    }

    private boolean esEntero(String texto) {
        return texto.trim().matches("[0-9]{1,9}");
    }
}
