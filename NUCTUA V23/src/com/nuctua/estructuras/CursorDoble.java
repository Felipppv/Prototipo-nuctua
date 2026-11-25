package com.nuctua.estructuras;

import java.util.function.Predicate;

public class CursorDoble<T> {

    private final ListaDoble<T> lista;
    private NodoDoble<T> actual;

    public CursorDoble(ListaDoble<T> lista) {
        this.lista = lista;
        this.actual = lista.getCabeza();
    }

    public T actual() {
        return actual == null ? null : actual.getDato();
    }

    public boolean haySiguiente() {
        return actual != null && actual.getSiguiente() != null;
    }

    public boolean hayAnterior() {
        return actual != null && actual.getAnterior() != null;
    }

    public T siguiente() {
        if (haySiguiente()) {
            actual = actual.getSiguiente();
        }
        return actual();
    }

    public T anterior() {
        if (hayAnterior()) {
            actual = actual.getAnterior();
        }
        return actual();
    }

    public T irAlInicio() {
        actual = lista.getCabeza();
        return actual();
    }

    public T irAlFinal() {
        actual = lista.getCola();
        return actual();
    }

    public boolean moverA(Predicate<T> condicion) {
        NodoDoble<T> encontrado = lista.buscarNodo(condicion);
        if (encontrado == null) {
            return false;
        }
        actual = encontrado;
        return true;
    }

    public int posicion() {
        int posicion = 0;
        for (NodoDoble<T> nodo = lista.getCabeza(); nodo != null; nodo = nodo.getSiguiente()) {
            if (nodo == actual) {
                return posicion;
            }
            posicion++;
        }
        return -1;
    }
}
