package com.nuctua.servicio;

import com.nuctua.modelo.Usuario;

public final class ResultadoAcceso {

    public enum Tipo {
        EXITO,
        CAMPOS_VACIOS,
        CORREO_INVALIDO,
        CREDENCIALES,
        ROL,
        NOMBRE_INVALIDO,
        CORREO_EXISTENTE,
        CONTRASENA_DEBIL,
        CONFIRMACION,
        TERMINOS
    }

    private final Tipo tipo;
    private final String mensaje;
    private final Usuario usuario;

    private ResultadoAcceso(Tipo tipo, String mensaje, Usuario usuario) {
        this.tipo = tipo;
        this.mensaje = mensaje;
        this.usuario = usuario;
    }

    public static ResultadoAcceso exito(Usuario usuario) {
        return new ResultadoAcceso(Tipo.EXITO, "", usuario);
    }

    public static ResultadoAcceso error(Tipo tipo, String mensaje) {
        return new ResultadoAcceso(tipo, mensaje, null);
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

    public Usuario getUsuario() {
        return usuario;
    }
}
