package com.nuctua.algoritmos;

import java.util.ArrayList;

public class ResultadoBusqueda<T> {

    private final ArrayList<T> encontrados = new ArrayList<>();
    private final ArrayList<String> metodos = new ArrayList<>();
    private int comparaciones;

    public ArrayList<T> getEncontrados() {
        return encontrados;
    }

    public int getComparaciones() {
        return comparaciones;
    }

    public void contar(int cantidad) {
        comparaciones += cantidad;
    }

    public void agregarMetodo(String metodo) {
        if (metodo != null && !metodos.contains(metodo)) {
            metodos.add(metodo);
        }
    }

    public boolean usoMetodo(String metodo) {
        return metodos.contains(metodo);
    }

    public String getDescripcion() {
        return String.join(" + ", metodos);
    }

    public void absorber(ResultadoBusqueda<T> otro) {
        if (otro == null) {
            return;
        }
        for (String metodo : otro.metodos) {
            agregarMetodo(metodo);
        }
        comparaciones += otro.comparaciones;
        encontrados.clear();
        encontrados.addAll(otro.encontrados);
    }
}
