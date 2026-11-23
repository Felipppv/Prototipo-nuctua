package com.nuctua.controlador;

import com.nuctua.vista.Ventana;

public class EntradaNavegacion {

    private final Ventana ventana;
    private final String estado;

    public EntradaNavegacion(Ventana ventana, String estado) {
        this.ventana = ventana;
        this.estado = estado == null ? "" : estado;
    }

    public Ventana getVentana() {
        return ventana;
    }

    public String getEstado() {
        return estado;
    }

    public boolean esIgualA(EntradaNavegacion otra) {
        return otra != null && ventana == otra.ventana && estado.equalsIgnoreCase(otra.estado);
    }

    @Override
    public String toString() {
        return estado.isEmpty() ? ventana.name() : ventana.name() + ":" + estado;
    }
}
