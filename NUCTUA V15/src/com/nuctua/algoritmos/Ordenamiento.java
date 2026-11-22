package com.nuctua.algoritmos;

import java.util.ArrayList;
import java.util.Comparator;

public final class Ordenamiento {

    private Ordenamiento() {
    }

    public static <T> int burbuja(ArrayList<T> lista, Comparator<T> criterio) {
        int comparaciones = 0;
        int n = lista.size();
        for (int i = 0; i < n - 1; i++) {
            boolean huboCambio = false;
            for (int j = 0; j < n - 1 - i; j++) {
                comparaciones++;
                if (criterio.compare(lista.get(j), lista.get(j + 1)) > 0) {
                    intercambiar(lista, j, j + 1);
                    huboCambio = true;
                }
            }
            if (!huboCambio) {
                break;
            }
        }
        return comparaciones;
    }

    public static <T> int intercambio(ArrayList<T> lista, Comparator<T> criterio) {
        int comparaciones = 0;
        int n = lista.size();
        for (int i = 0; i < n - 1; i++) {
            for (int j = i + 1; j < n; j++) {
                comparaciones++;
                if (criterio.compare(lista.get(i), lista.get(j)) > 0) {
                    intercambiar(lista, i, j);
                }
            }
        }
        return comparaciones;
    }

    public static <T> int seleccion(ArrayList<T> lista, Comparator<T> criterio) {
        int comparaciones = 0;
        int n = lista.size();
        for (int i = 0; i < n - 1; i++) {
            int menor = i;
            for (int j = i + 1; j < n; j++) {
                comparaciones++;
                if (criterio.compare(lista.get(j), lista.get(menor)) < 0) {
                    menor = j;
                }
            }
            if (menor != i) {
                intercambiar(lista, i, menor);
            }
        }
        return comparaciones;
    }

    public static <T> int insercion(ArrayList<T> lista, Comparator<T> criterio) {
        int comparaciones = 0;
        for (int i = 1; i < lista.size(); i++) {
            T actual = lista.get(i);
            int j = i - 1;
            while (j >= 0) {
                comparaciones++;
                if (criterio.compare(lista.get(j), actual) <= 0) {
                    break;
                }
                lista.set(j + 1, lista.get(j));
                j--;
            }
            lista.set(j + 1, actual);
        }
        return comparaciones;
    }

    public static <T> int quickSort(ArrayList<T> lista, Comparator<T> criterio) {
        int[] comparaciones = {0};
        quickSort(lista, criterio, 0, lista.size() - 1, comparaciones);
        return comparaciones[0];
    }

    private static <T> void quickSort(ArrayList<T> lista, Comparator<T> criterio, int inicio, int fin, int[] comparaciones) {
        if (inicio >= fin) {
            return;
        }
        int medio = inicio + (fin - inicio) / 2;
        intercambiar(lista, medio, fin);
        T pivote = lista.get(fin);
        int frontera = inicio;
        for (int i = inicio; i < fin; i++) {
            comparaciones[0]++;
            if (criterio.compare(lista.get(i), pivote) < 0) {
                intercambiar(lista, i, frontera);
                frontera++;
            }
        }
        intercambiar(lista, frontera, fin);
        quickSort(lista, criterio, inicio, frontera - 1, comparaciones);
        quickSort(lista, criterio, frontera + 1, fin, comparaciones);
    }

    private static <T> void intercambiar(ArrayList<T> lista, int a, int b) {
        if (a == b) {
            return;
        }
        T temporal = lista.get(a);
        lista.set(a, lista.get(b));
        lista.set(b, temporal);
    }
}
