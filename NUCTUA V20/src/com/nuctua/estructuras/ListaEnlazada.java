package com.nuctua.estructuras;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.function.Predicate;

public class ListaEnlazada<T> implements Iterable<T> {

    private Nodo<T> cabeza;
    private Nodo<T> cola;
    private int tamanio;

    public ListaEnlazada() {
        cabeza = null;
        cola = null;
        tamanio = 0;
    }

    public boolean estaVacia() {
        return cabeza == null;
    }

    public int tamanio() {
        return tamanio;
    }

    public void insertarInicio(T dato) {
        Nodo<T> nuevo = new Nodo<>(dato);
        if (estaVacia()) {
            cabeza = nuevo;
            cola = nuevo;
        } else {
            nuevo.setSiguiente(cabeza);
            cabeza = nuevo;
        }
        tamanio++;
    }

    public void insertarFinal(T dato) {
        Nodo<T> nuevo = new Nodo<>(dato);
        if (estaVacia()) {
            cabeza = nuevo;
            cola = nuevo;
        } else {
            cola.setSiguiente(nuevo);
            cola = nuevo;
        }
        tamanio++;
    }

    public T obtener(int indice) {
        if (indice < 0 || indice >= tamanio) {
            throw new IndexOutOfBoundsException("Índice fuera de la lista: " + indice);
        }
        Nodo<T> actual = cabeza;
        for (int i = 0; i < indice; i++) {
            actual = actual.getSiguiente();
        }
        return actual.getDato();
    }

    public T buscar(Predicate<T> condicion) {
        Nodo<T> actual = cabeza;
        while (actual != null) {
            if (condicion.test(actual.getDato())) {
                return actual.getDato();
            }
            actual = actual.getSiguiente();
        }
        return null;
    }

    public boolean contiene(Predicate<T> condicion) {
        return buscar(condicion) != null;
    }

    public boolean eliminar(Predicate<T> condicion) {
        Nodo<T> anterior = null;
        Nodo<T> actual = cabeza;
        while (actual != null) {
            if (condicion.test(actual.getDato())) {
                if (anterior == null) {
                    cabeza = actual.getSiguiente();
                } else {
                    anterior.setSiguiente(actual.getSiguiente());
                }
                if (actual == cola) {
                    cola = anterior;
                }
                tamanio--;
                return true;
            }
            anterior = actual;
            actual = actual.getSiguiente();
        }
        return false;
    }

    public boolean reemplazar(Predicate<T> condicion, T nuevo) {
        Nodo<T> actual = cabeza;
        while (actual != null) {
            if (condicion.test(actual.getDato())) {
                actual.setDato(nuevo);
                return true;
            }
            actual = actual.getSiguiente();
        }
        return false;
    }

    public void vaciar() {
        cabeza = null;
        cola = null;
        tamanio = 0;
    }

    public ArrayList<T> aArrayList() {
        ArrayList<T> lista = new ArrayList<>();
        Nodo<T> actual = cabeza;
        while (actual != null) {
            lista.add(actual.getDato());
            actual = actual.getSiguiente();
        }
        return lista;
    }

    @Override
    public Iterator<T> iterator() {
        return new Iterator<T>() {
            private Nodo<T> actual = cabeza;

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
