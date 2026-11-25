package com.nuctua.modelo;

public class ErrorCampo {

    private final Enum<?> campo;
    private final String mensaje;

    public ErrorCampo(Enum<?> campo, String mensaje) {
        this.campo = campo;
        this.mensaje = mensaje;
    }

    public Enum<?> getCampo() {
        return campo;
    }

    public String getMensaje() {
        return mensaje;
    }
}
