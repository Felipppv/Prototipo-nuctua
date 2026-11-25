package com.nuctua.servicio;

import com.nuctua.modelo.EstadoCliente;
import com.nuctua.modelo.ResumenCliente;

public enum FiltroClientes {
    TODOS("Todos", null),
    NUEVOS("Nuevos", EstadoCliente.NUEVO),
    RECURRENTES("Recurrentes", EstadoCliente.RECURRENTE),
    INACTIVOS("Inactivos", EstadoCliente.INACTIVO);

    private final String texto;
    private final EstadoCliente estado;

    FiltroClientes(String texto, EstadoCliente estado) {
        this.texto = texto;
        this.estado = estado;
    }

    public String getTexto() {
        return texto;
    }

    public boolean acepta(ResumenCliente resumen) {
        return estado == null || resumen.getEstado() == estado;
    }
}
