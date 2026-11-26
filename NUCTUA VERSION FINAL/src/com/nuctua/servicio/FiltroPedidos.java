package com.nuctua.servicio;

import com.nuctua.modelo.EstadoPedido;
import com.nuctua.modelo.Pedido;

public enum FiltroPedidos {
    TODOS("Todos", null),
    NUEVOS("Nuevos", EstadoPedido.NUEVO),
    PREPARANDO("Preparando", EstadoPedido.PREPARANDO),
    ENVIADOS("Enviados", EstadoPedido.ENVIADO),
    ENTREGADOS("Entregados", EstadoPedido.ENTREGADO),
    CANCELADOS("Cancelados", EstadoPedido.CANCELADO);

    private final String texto;
    private final EstadoPedido estado;

    FiltroPedidos(String texto, EstadoPedido estado) {
        this.texto = texto;
        this.estado = estado;
    }

    public String getTexto() {
        return texto;
    }

    public boolean acepta(Pedido pedido) {
        return estado == null || pedido.getEstado() == estado;
    }
}
