package com.nuctua.estructuras;

import java.util.function.BiPredicate;

public class PilaHistorial<T> extends Pila<T> {

    private final BiPredicate<T, T> mismoElemento;

    public PilaHistorial(int capacidad, BiPredicate<T, T> mismoElemento) {
        super(capacidad);
        this.mismoElemento = mismoElemento;
    }

    public void visitar(T dato) {
        if (dato == null) {
            return;
        }
        T tope = cima();
        if (tope != null && mismoElemento.test(tope, dato)) {
            return;
        }
        apilar(dato);
    }

    public void registrarSinRepetir(T dato) {
        if (dato == null) {
            return;
        }
        quitar(existente -> mismoElemento.test(existente, dato));
        apilar(dato);
    }

    public T retroceder() {
        return desapilar();
    }

    public boolean puedeRetroceder() {
        return !estaVacia();
    }

    public void vaciar() {
        while (!estaVacia()) {
            desapilar();
        }
    }
}
