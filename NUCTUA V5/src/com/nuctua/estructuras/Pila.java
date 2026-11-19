package com.nuctua.estructuras;

import java.util.ArrayList;
import java.util.function.Predicate;

public class Pila<T> {

    private Nodo<T> tope;
    private int tamanio;
    private final int capacidad;

    public Pila() {
        this(Integer.MAX_VALUE);
    }

    public Pila(int capacidad) {
        this.tope = null;
        this.tamanio = 0;
        this.capacidad = capacidad;
    }

    public boolean estaVacia() {
        return tope == null;
    }

    public int tamanio() {
        return tamanio;
    }

    public void apilar(T dato) {
        Nodo<T> nuevo = new Nodo<>(dato);
        nuevo.setSiguiente(tope);
        tope = nuevo;
        tamanio++;
        if (tamanio > capacidad) {
            recortarFondo();
        }
    }

    public T desapilar() {
        if (estaVacia()) {
            return null;
        }
        T dato = tope.getDato();
        tope = tope.getSiguiente();
        tamanio--;
        return dato;
    }

    public T cima() {
        return estaVacia() ? null : tope.getDato();
    }

    public void quitar(Predicate<T> condicion) {
        Pila<T> auxiliar = new Pila<>();
        while (!estaVacia()) {
            T dato = desapilar();
            if (!condicion.test(dato)) {
                auxiliar.apilar(dato);
            }
        }
        while (!auxiliar.estaVacia()) {
            apilar(auxiliar.desapilar());
        }
    }

    public ArrayList<T> aArrayList() {
        ArrayList<T> lista = new ArrayList<>();
        Nodo<T> actual = tope;
        while (actual != null) {
            lista.add(actual.getDato());
            actual = actual.getSiguiente();
        }
        return lista;
    }

    private void recortarFondo() {
        Nodo<T> actual = tope;
        for (int i = 1; i < capacidad; i++) {
            actual = actual.getSiguiente();
        }
        actual.setSiguiente(null);
        tamanio = capacidad;
    }
}
