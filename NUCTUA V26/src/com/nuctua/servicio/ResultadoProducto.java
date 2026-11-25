package com.nuctua.servicio;

import com.nuctua.modelo.Zapato;

public class ResultadoProducto {

    private final Zapato zapato;
    private final String mensaje;

    private ResultadoProducto(Zapato zapato, String mensaje) {
        this.zapato = zapato;
        this.mensaje = mensaje;
    }

    public static ResultadoProducto exito(Zapato zapato, String mensaje) {
        return new ResultadoProducto(zapato, mensaje);
    }

    public static ResultadoProducto error(String mensaje) {
        return new ResultadoProducto(null, mensaje);
    }

    public boolean esExito() {
        return zapato != null;
    }

    public Zapato getZapato() {
        return zapato;
    }

    public String getMensaje() {
        return mensaje;
    }
}
