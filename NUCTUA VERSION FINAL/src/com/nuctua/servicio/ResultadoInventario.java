package com.nuctua.servicio;

public class ResultadoInventario {

    private final boolean exito;
    private final String mensaje;

    private ResultadoInventario(boolean exito, String mensaje) {
        this.exito = exito;
        this.mensaje = mensaje;
    }

    public static ResultadoInventario exito(String mensaje) {
        return new ResultadoInventario(true, mensaje);
    }

    public static ResultadoInventario error(String mensaje) {
        return new ResultadoInventario(false, mensaje);
    }

    public boolean esExito() {
        return exito;
    }

    public String getMensaje() {
        return mensaje;
    }
}
