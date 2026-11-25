package com.nuctua.servicio;

import com.nuctua.estructuras.ListaEnlazada;
import com.nuctua.modelo.ErrorCampo;
import com.nuctua.modelo.Pedido;

public class ResultadoCompra {

    public enum Tipo {
        EXITO,
        SIN_SESION,
        CARRITO_VACIO,
        CARRITO_CAMBIO,
        DATOS_INVALIDOS,
        SIN_GUARDAR
    }

    private final Tipo tipo;
    private final String mensaje;
    private final ListaEnlazada<ErrorCampo> errores;
    private final Pedido pedido;

    private ResultadoCompra(Tipo tipo, String mensaje, ListaEnlazada<ErrorCampo> errores, Pedido pedido) {
        this.tipo = tipo;
        this.mensaje = mensaje;
        this.errores = errores;
        this.pedido = pedido;
    }

    public static ResultadoCompra exito(Pedido pedido) {
        return new ResultadoCompra(Tipo.EXITO, "", new ListaEnlazada<>(), pedido);
    }

    public static ResultadoCompra error(Tipo tipo, String mensaje) {
        return new ResultadoCompra(tipo, mensaje, new ListaEnlazada<>(), null);
    }

    public static ResultadoCompra invalida(ListaEnlazada<ErrorCampo> errores) {
        String mensaje = errores.tamanio() == 1 ? "Revisa el campo marcado para confirmar."
                : "Revisa los " + errores.tamanio() + " campos marcados para confirmar.";
        return new ResultadoCompra(Tipo.DATOS_INVALIDOS, mensaje, errores, null);
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

    public ListaEnlazada<ErrorCampo> getErrores() {
        return errores;
    }

    public Pedido getPedido() {
        return pedido;
    }
}
