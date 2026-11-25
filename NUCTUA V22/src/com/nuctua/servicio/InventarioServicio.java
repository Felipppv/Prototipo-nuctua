package com.nuctua.servicio;

import com.nuctua.estructuras.ListaEnlazada;
import com.nuctua.modelo.LineaPedido;
import com.nuctua.modelo.TallaStock;
import com.nuctua.modelo.Zapato;
import com.nuctua.persistencia.ArchivoProductos;

public class InventarioServicio {

    private final ArchivoProductos archivo = new ArchivoProductos();

    public LineaPedido descontar(Iterable<LineaPedido> lineas) {
        ListaEnlazada<Zapato> productos = archivo.cargar();
        for (LineaPedido linea : lineas) {
            Zapato zapato = buscar(productos, linea);
            TallaStock talla = zapato == null || !zapato.isActivo() ? null : zapato.getTallaStock(linea.getTalla());
            if (talla == null || !talla.descontar(linea.getCantidad())) {
                return linea;
            }
        }
        archivo.guardar(productos);
        return null;
    }

    public void reponer(Iterable<LineaPedido> lineas) {
        ListaEnlazada<Zapato> productos = archivo.cargar();
        for (LineaPedido linea : lineas) {
            Zapato zapato = buscar(productos, linea);
            if (zapato != null) {
                zapato.agregarTalla(linea.getTalla(), zapato.getStock(linea.getTalla()) + linea.getCantidad());
            }
        }
        archivo.guardar(productos);
    }

    private Zapato buscar(ListaEnlazada<Zapato> productos, LineaPedido linea) {
        return productos.buscar(z -> z.getSku().equalsIgnoreCase(linea.getSku()));
    }
}
