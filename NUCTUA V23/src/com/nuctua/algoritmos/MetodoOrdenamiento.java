package com.nuctua.algoritmos;

import java.util.ArrayList;
import java.util.Comparator;

public enum MetodoOrdenamiento {
    BURBUJA("Burbuja"),
    INTERCAMBIO("Intercambio"),
    SELECCION("Selección"),
    INSERCION("Inserción"),
    QUICKSORT("QuickSort");

    private final String nombre;

    MetodoOrdenamiento(String nombre) {
        this.nombre = nombre;
    }

    public <T> int ordenar(ArrayList<T> lista, Comparator<T> criterio) {
        return switch (this) {
            case BURBUJA -> Ordenamiento.burbuja(lista, criterio);
            case INTERCAMBIO -> Ordenamiento.intercambio(lista, criterio);
            case SELECCION -> Ordenamiento.seleccion(lista, criterio);
            case INSERCION -> Ordenamiento.insercion(lista, criterio);
            case QUICKSORT -> Ordenamiento.quickSort(lista, criterio);
        };
    }

    public String getNombre() {
        return nombre;
    }
}
