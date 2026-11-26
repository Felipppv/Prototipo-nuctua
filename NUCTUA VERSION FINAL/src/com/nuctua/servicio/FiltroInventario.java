package com.nuctua.servicio;

import com.nuctua.modelo.Zapato;

public enum FiltroInventario {
    TODOS("Todos"),
    POCO_STOCK("Poco stock"),
    AGOTADOS("Agotados");

    private final String texto;

    FiltroInventario(String texto) {
        this.texto = texto;
    }

    public String getTexto() {
        return texto;
    }

    public boolean acepta(Zapato zapato) {
        return switch (this) {
            case TODOS -> true;
            case POCO_STOCK -> zapato.tienePocoStock();
            case AGOTADOS -> zapato.estaAgotado();
        };
    }
}
