package com.nuctua.algoritmos;

import java.util.ArrayList;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.ToIntFunction;

public final class Busqueda {

    public static final String LINEAL = "Lineal";
    public static final String BINARIA = "Binaria";

    private Busqueda() {
    }

    public static <T> ArrayList<T> secuencial(ArrayList<T> lista, Predicate<T> condicion) {
        return lineal(lista, condicion).getEncontrados();
    }

    public static <T> ResultadoBusqueda<T> lineal(ArrayList<T> lista, Predicate<T> condicion) {
        ResultadoBusqueda<T> resultado = new ResultadoBusqueda<>();
        resultado.agregarMetodo(LINEAL);
        if (lista == null || condicion == null) {
            return resultado;
        }
        for (int i = 0; i < lista.size(); i++) {
            resultado.contar(1);
            if (condicion.test(lista.get(i))) {
                resultado.getEncontrados().add(lista.get(i));
            }
        }
        return resultado;
    }

    public static <T> int secuencialPrimero(ArrayList<T> lista, Predicate<T> condicion) {
        if (lista == null || condicion == null) {
            return -1;
        }
        for (int i = 0; i < lista.size(); i++) {
            if (condicion.test(lista.get(i))) {
                return i;
            }
        }
        return -1;
    }

    public static <T> int binaria(ArrayList<T> ordenada, String clave, Function<T, String> extraer) {
        if (clave == null) {
            return -1;
        }
        return binaria(ordenada, elemento -> extraer.apply(elemento).compareToIgnoreCase(clave), new int[1]);
    }

    public static <T> int binaria(ArrayList<T> ordenada, ToIntFunction<T> compararConClave, int[] comparaciones) {
        if (ordenada == null) {
            return -1;
        }
        int inicio = 0;
        int fin = ordenada.size() - 1;
        while (inicio <= fin) {
            int medio = inicio + (fin - inicio) / 2;
            comparaciones[0]++;
            int comparacion = compararConClave.applyAsInt(ordenada.get(medio));
            if (comparacion == 0) {
                return medio;
            }
            if (comparacion < 0) {
                inicio = medio + 1;
            } else {
                fin = medio - 1;
            }
        }
        return -1;
    }

    public static <T> int limiteInferior(ArrayList<T> ordenada, ToIntFunction<T> compararConClave, int[] comparaciones) {
        int inicio = 0;
        int fin = ordenada == null ? 0 : ordenada.size();
        while (inicio < fin) {
            int medio = inicio + (fin - inicio) / 2;
            comparaciones[0]++;
            if (compararConClave.applyAsInt(ordenada.get(medio)) < 0) {
                inicio = medio + 1;
            } else {
                fin = medio;
            }
        }
        return inicio;
    }

    public static <T> int limiteSuperior(ArrayList<T> ordenada, ToIntFunction<T> compararConClave, int[] comparaciones) {
        int inicio = 0;
        int fin = ordenada == null ? 0 : ordenada.size();
        while (inicio < fin) {
            int medio = inicio + (fin - inicio) / 2;
            comparaciones[0]++;
            if (compararConClave.applyAsInt(ordenada.get(medio)) <= 0) {
                inicio = medio + 1;
            } else {
                fin = medio;
            }
        }
        return inicio;
    }
}
