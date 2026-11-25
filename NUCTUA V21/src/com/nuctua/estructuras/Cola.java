package com.nuctua.estructuras;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.function.Predicate;

public class Cola<T> implements Iterable<T> {

    private Nodo<T> frente;
    private Nodo<T> fin;
    private int tamanio;

    public Cola() {
        frente = null;
        fin = null;
        tamanio = 0;
    }

    public boolean estaVacia() {
        return frente == null;
    }

    public int tamanio() {
        return tamanio;
    }

    public boolean encolar(T dato) {
        if (dato == null) {
            return false;
        }
        insertarAlFin(dato);
        return true;
    }

    public T desencolar() {
        if (estaVacia()) {
            return null;
        }
        T dato = frente.getDato();
        frente = frente.getSiguiente();
        if (frente == null) {
            fin = null;
        }
        tamanio--;
        return dato;
    }

    public T frente() {
        return estaVacia() ? null : frente.getDato();
    }

    public int posicionDe(Predicate<T> condicion) {
        int posicion = 1;
        Nodo<T> actual = frente;
        while (actual != null) {
            if (condicion.test(actual.getDato())) {
                return posicion;
            }
            posicion++;
            actual = actual.getSiguiente();
        }
        return 0;
    }

    public boolean quitar(Predicate<T> condicion) {
        boolean quitado = false;
        int vueltas = tamanio;
        for (int i = 0; i < vueltas; i++) {
            T dato = desencolar();
            if (!quitado && condicion.test(dato)) {
                quitado = true;
            } else {
                insertarAlFin(dato);
            }
        }
        return quitado;
    }

    public void vaciar() {
        frente = null;
        fin = null;
        tamanio = 0;
    }

    public ArrayList<T> aArrayList() {
        ArrayList<T> lista = new ArrayList<>();
        for (T dato : this) {
            lista.add(dato);
        }
        return lista;
    }

    private void insertarAlFin(T dato) {
        Nodo<T> nuevo = new Nodo<>(dato);
        if (estaVacia()) {
            frente = nuevo;
        } else {
            fin.setSiguiente(nuevo);
        }
        fin = nuevo;
        tamanio++;
    }

    @Override
    public Iterator<T> iterator() {
        return new Iterator<T>() {
            private Nodo<T> actual = frente;

            @Override
            public boolean hasNext() {
                return actual != null;
            }

            @Override
            public T next() {
                if (actual == null) {
                    throw new NoSuchElementException();
                }
                T dato = actual.getDato();
                actual = actual.getSiguiente();
                return dato;
            }
        };
    }
}
