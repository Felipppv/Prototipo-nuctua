package com.nuctua.servicio;

import com.nuctua.modelo.Zapato;

public enum FiltroProductos {
    TODOS("Todos"),
    BAJOS("Bajos"),
    ALTOS("Altos"),
    AGOTADOS("Agotados"),
    OCULTOS("Ocultos");

    private final String texto;

    FiltroProductos(String texto) {
        this.texto = texto;
    }

    public String getTexto() {
        return texto;
    }

    public boolean acepta(Zapato zapato) {
        return switch (this) {
            case TODOS -> true;
            case BAJOS -> "Bajo".equalsIgnoreCase(zapato.getCategoria());
            case ALTOS -> "Alto".equalsIgnoreCase(zapato.getCategoria());
            case AGOTADOS -> zapato.estaAgotado();
            case OCULTOS -> !zapato.isActivo();
        };
    }
}
