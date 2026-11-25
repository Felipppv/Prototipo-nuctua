package com.nuctua.modelo;

import com.nuctua.estructuras.ListaEnlazada;
import java.time.LocalDate;

public abstract class Pago {

    private final MetodoPago metodo;

    protected Pago(MetodoPago metodo) {
        this.metodo = metodo;
    }

    public abstract ListaEnlazada<ErrorCampo> validar(LocalDate hoy);

    public abstract String getReferencia();

    public MetodoPago getMetodo() {
        return metodo;
    }
}
