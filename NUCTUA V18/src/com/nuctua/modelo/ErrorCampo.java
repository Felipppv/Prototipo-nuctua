package com.nuctua.modelo;

public class ErrorCampo {

    private final CampoCompra campo;
    private final String mensaje;

    public ErrorCampo(CampoCompra campo, String mensaje) {
        this.campo = campo;
        this.mensaje = mensaje;
    }

    public CampoCompra getCampo() {
        return campo;
    }

    public String getMensaje() {
        return mensaje;
    }
}
