package com.nuctua.estructuras;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.function.Predicate;

public class ListaDoble<T> implements Iterable<T> {

    private NodoDoble<T> cabeza;
    private NodoDoble<T> cola;
    private int tamanio;

    public boolean estaVacia() {
        return cabeza == null;
    }

    public int tamanio() {
        return tamanio;
    }

    public void insertarInicio(T dato) {
        NodoDoble<T> nuevo = new NodoDoble<>(dato);
        if (estaVacia()) {
            cabeza = nuevo;
            cola = nuevo;
        } else {
            nuevo.setSiguiente(cabeza);
            cabeza.setAnterior(nuevo);
            cabeza = nuevo;
        }
        tamanio++;
    }

    public void insertarFinal(T dato) {
        NodoDoble<T> nuevo = new NodoDoble<>(dato);
        if (estaVacia()) {
            cabeza = nuevo;
            cola = nuevo;
        } else {
            nuevo.setAnterior(cola);
            cola.setSiguiente(nuevo);
            cola = nuevo;
        }
        tamanio++;
    }

    public void insertarEn(int indice, T dato) {
        if (indice <= 0) {
            insertarInicio(dato);
            return;
        }
        if (indice >= tamanio) {
            insertarFinal(dato);
            return;
        }
        NodoDoble<T> proximo = nodoEn(indice);
        NodoDoble<T> previo = proximo.getAnterior();
        NodoDoble<T> nuevo = new NodoDoble<>(dato);
        nuevo.setAnterior(previo);
        nuevo.setSiguiente(proximo);
        previo.setSiguiente(nuevo);
        proximo.setAnterior(nuevo);
        tamanio++;
    }

    public T primero() {
        return cabeza == null ? null : cabeza.getDato();
    }

    public T ultimo() {
        return cola == null ? null : cola.getDato();
    }

    public T obtener(int indice) {
        NodoDoble<T> nodo = nodoEn(indice);
        return nodo == null ? null : nodo.getDato();
    }

    public int indiceDe(Predicate<T> condicion) {
        int indice = 0;
        for (NodoDoble<T> actual = cabeza; actual != null; actual = actual.getSiguiente()) {
            if (condicion.test(actual.getDato())) {
                return indice;
            }
            indice++;
        }
        return -1;
    }

    public T buscar(Predicate<T> condicion) {
        NodoDoble<T> nodo = buscarNodo(condicion);
        return nodo == null ? null : nodo.getDato();
    }

    public boolean contiene(Predicate<T> condicion) {
        return buscarNodo(condicion) != null;
    }

    public T eliminar(Predicate<T> condicion) {
        NodoDoble<T> nodo = buscarNodo(condicion);
        if (nodo == null) {
            return null;
        }
        desenlazar(nodo);
        return nodo.getDato();
    }

    public void vaciar() {
        cabeza = null;
        cola = null;
        tamanio = 0;
    }

    public ArrayList<T> aArrayList() {
        ArrayList<T> lista = new ArrayList<>();
        for (NodoDoble<T> actual = cabeza; actual != null; actual = actual.getSiguiente()) {
            lista.add(actual.getDato());
        }
        return lista;
    }

    public ArrayList<T> aArrayListInverso() {
        ArrayList<T> lista = new ArrayList<>();
        for (NodoDoble<T> actual = cola; actual != null; actual = actual.getAnterior()) {
            lista.add(actual.getDato());
        }
        return lista;
    }

    protected NodoDoble<T> getCabeza() {
        return cabeza;
    }

    protected NodoDoble<T> getCola() {
        return cola;
    }

    protected NodoDoble<T> nodoEn(int indice) {
        if (indice < 0 || indice >= tamanio) {
            return null;
        }
        if (indice < tamanio / 2) {
            NodoDoble<T> actual = cabeza;
            for (int i = 0; i < indice; i++) {
                actual = actual.getSiguiente();
            }
            return actual;
        }
        NodoDoble<T> actual = cola;
        for (int i = tamanio - 1; i > indice; i--) {
            actual = actual.getAnterior();
        }
        return actual;
    }

    protected NodoDoble<T> buscarNodo(Predicate<T> condicion) {
        for (NodoDoble<T> actual = cabeza; actual != null; actual = actual.getSiguiente()) {
            if (condicion.test(actual.getDato())) {
                return actual;
            }
        }
        return null;
    }

    protected void desenlazar(NodoDoble<T> nodo) {
        NodoDoble<T> previo = nodo.getAnterior();
        NodoDoble<T> proximo = nodo.getSiguiente();
        if (previo == null) {
            cabeza = proximo;
        } else {
            previo.setSiguiente(proximo);
        }
        if (proximo == null) {
            cola = previo;
        } else {
            proximo.setAnterior(previo);
        }
        nodo.setAnterior(null);
        nodo.setSiguiente(null);
        tamanio--;
    }

    @Override
    public Iterator<T> iterator() {
        return new Iterator<>() {
            private NodoDoble<T> actual = cabeza;

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
