package com.nuctua.servicio;

import com.nuctua.algoritmos.MetodoOrdenamiento;
import com.nuctua.modelo.Zapato;
import java.util.Comparator;

public enum OrdenProductos {
    SKU("SKU", MetodoOrdenamiento.INSERCION, (a, b) -> a.getSku().compareToIgnoreCase(b.getSku())),
    NOMBRE("nombre", MetodoOrdenamiento.SELECCION, (a, b) -> CatalogoServicio.normalizar(a.getNombre())
            .compareTo(CatalogoServicio.normalizar(b.getNombre()))),
    STOCK("stock", MetodoOrdenamiento.BURBUJA, (a, b) -> Integer.compare(a.getStockTotal(), b.getStockTotal())),
    PRECIO("precio", MetodoOrdenamiento.QUICKSORT, (a, b) -> Integer.compare(a.getPrecio(), b.getPrecio()));

    private final String texto;
    private final MetodoOrdenamiento metodo;
    private final Comparator<Zapato> comparador;

    OrdenProductos(String texto, MetodoOrdenamiento metodo, Comparator<Zapato> comparador) {
        this.texto = texto;
        this.metodo = metodo;
        this.comparador = comparador;
    }

    public String getTexto() {
        return texto;
    }

    public MetodoOrdenamiento getMetodo() {
        return metodo;
    }

    public Comparator<Zapato> getComparador(boolean ascendente) {
        return ascendente ? comparador : comparador.reversed();
    }
}
