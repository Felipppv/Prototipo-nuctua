package com.nuctua.modelo;

public enum EstadoPedido {
    NUEVO("Nuevo"),
    PREPARANDO("Preparando"),
    ENVIADO("Enviado"),
    ENTREGADO("Entregado"),
    CANCELADO("Cancelado");

    private final String texto;

    EstadoPedido(String texto) {
        this.texto = texto;
    }

    public String getTexto() {
        return texto;
    }

    public EstadoPedido siguiente() {
        return switch (this) {
            case NUEVO -> PREPARANDO;
            case PREPARANDO -> ENVIADO;
            case ENVIADO -> ENTREGADO;
            default -> null;
        };
    }

    public boolean esFinal() {
        return this == ENTREGADO || this == CANCELADO;
    }

    public boolean puedePasarA(EstadoPedido destino) {
        if (destino == null || destino == this) {
            return false;
        }
        return destino == siguiente() || (destino == CANCELADO && (this == NUEVO || this == PREPARANDO));
    }

    public static EstadoPedido de(String nombre) {
        for (EstadoPedido estado : values()) {
            if (estado.name().equalsIgnoreCase(nombre)) {
                return estado;
            }
        }
        return null;
    }
}
