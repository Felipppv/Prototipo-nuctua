package com.nuctua.servicio;

import com.nuctua.modelo.Pedido;

public class ResultadoEstado {

    public enum Tipo {
        EXITO,
        SIN_PERMISO,
        NO_EXISTE,
        COLA_VACIA,
        NO_ES_SU_TURNO,
        NO_PERMITIDO,
        SIN_GUARDAR
    }

    private final Tipo tipo;
    private final String mensaje;
    private final Pedido pedido;

    private ResultadoEstado(Tipo tipo, String mensaje, Pedido pedido) {
        this.tipo = tipo;
        this.mensaje = mensaje;
        this.pedido = pedido;
    }

    public static ResultadoEstado exito(Pedido pedido) {
        return new ResultadoEstado(Tipo.EXITO, "El pedido #" + pedido.getCodigo() + " quedó en estado "
                + pedido.getEstado().getTexto() + ".", pedido);
    }

    public static ResultadoEstado error(Tipo tipo, String mensaje) {
        return new ResultadoEstado(tipo, mensaje, null);
    }

    public boolean esExito() {
        return tipo == Tipo.EXITO;
    }

    public Tipo getTipo() {
        return tipo;
    }

    public String getMensaje() {
        return mensaje;
    }

    public Pedido getPedido() {
        return pedido;
    }
}
