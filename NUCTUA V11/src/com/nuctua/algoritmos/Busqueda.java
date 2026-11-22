package com.nuctua.algoritmos;

import java.util.ArrayList;
import java.util.function.Function;
import java.util.function.Predicate;

public final class Busqueda {

    private Busqueda() {
    }

    public static <T> ArrayList<T> secuencial(ArrayList<T> lista, Predicate<T> condicion) {
        ArrayList<T> encontrados = new ArrayList<>();
        for (int i = 0; i < lista.size(); i++) {
            if (condicion.test(lista.get(i))) {
                encontrados.add(lista.get(i));
            }
        }
        return encontrados;
    }

    public static <T> int secuencialPrimero(ArrayList<T> lista, Predicate<T> condicion) {
        for (int i = 0; i < lista.size(); i++) {
            if (condicion.test(lista.get(i))) {
                return i;
            }
        }
        return -1;
    }

    public static <T> int binaria(ArrayList<T> ordenada, String clave, Function<T, String> extraer) {
        int inicio = 0;
        int fin = ordenada.size() - 1;
        while (inicio <= fin) {
            int medio = (inicio + fin) / 2;
            int comparacion = extraer.apply(ordenada.get(medio)).compareToIgnoreCase(clave);
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
}
