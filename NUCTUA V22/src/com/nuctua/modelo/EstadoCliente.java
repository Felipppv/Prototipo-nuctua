package com.nuctua.modelo;

public enum EstadoCliente {
    NUEVO("Nuevo"),
    RECURRENTE("Recurrente"),
    INACTIVO("Inactivo");

    private final String texto;

    EstadoCliente(String texto) {
        this.texto = texto;
    }

    public String getTexto() {
        return texto;
    }
}
