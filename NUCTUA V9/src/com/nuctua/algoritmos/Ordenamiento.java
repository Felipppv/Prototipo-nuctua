package com.nuctua.algoritmos;

import java.util.ArrayList;
import java.util.Comparator;

public final class Ordenamiento {

    private Ordenamiento() {
    }

    public static <T> void burbuja(ArrayList<T> lista, Comparator<T> criterio) {
        int n = lista.size();
        for (int i = 0; i < n - 1; i++) {
            boolean huboCambio = false;
            for (int j = 0; j < n - 1 - i; j++) {
                if (criterio.compare(lista.get(j), lista.get(j + 1)) > 0) {
                    intercambiar(lista, j, j + 1);
                    huboCambio = true;
                }
            }
            if (!huboCambio) {
                return;
            }
        }
    }

    public static <T> void intercambio(ArrayList<T> lista, Comparator<T> criterio) {
        int n = lista.size();
        for (int i = 0; i < n - 1; i++) {
            for (int j = i + 1; j < n; j++) {
                if (criterio.compare(lista.get(i), lista.get(j)) > 0) {
                    intercambiar(lista, i, j);
                }
            }
        }
    }

    public static <T> void seleccion(ArrayList<T> lista, Comparator<T> criterio) {
        int n = lista.size();
        for (int i = 0; i < n - 1; i++) {
            int menor = i;
            for (int j = i + 1; j < n; j++) {
                if (criterio.compare(lista.get(j), lista.get(menor)) < 0) {
                    menor = j;
                }
            }
            if (menor != i) {
                intercambiar(lista, i, menor);
            }
        }
    }

    private static <T> void intercambiar(ArrayList<T> lista, int a, int b) {
        T temporal = lista.get(a);
        lista.set(a, lista.get(b));
        lista.set(b, temporal);
    }
}
