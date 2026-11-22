package com.nuctua.modelo;

import com.nuctua.estructuras.Pila;

public final class SesionActual {

    private static final int MAXIMO_VISTOS = 6;
    private static SesionActual instancia;

    private Usuario usuario;
    private final Pila<Zapato> vistosRecientemente;

    private SesionActual() {
        vistosRecientemente = new Pila<>(MAXIMO_VISTOS);
    }

    public static SesionActual getInstancia() {
        if (instancia == null) {
            instancia = new SesionActual();
        }
        return instancia;
    }

    public void iniciar(Usuario usuario) {
        this.usuario = usuario;
        while (!vistosRecientemente.estaVacia()) {
            vistosRecientemente.desapilar();
        }
    }

    public void cerrar() {
        usuario = null;
        while (!vistosRecientemente.estaVacia()) {
            vistosRecientemente.desapilar();
        }
    }

    public boolean haySesion() {
        return usuario != null;
    }

    public boolean esAdministrador() {
        return usuario != null && usuario.getRol() == Rol.ADMINISTRADOR;
    }

    public Usuario getUsuario() {
        return usuario;
    }

    public Cliente getCliente() {
        return usuario instanceof Cliente ? (Cliente) usuario : null;
    }

    public void registrarVisto(Zapato zapato) {
        vistosRecientemente.quitar(z -> z.getSku().equals(zapato.getSku()));
        vistosRecientemente.apilar(zapato);
    }

    public Pila<Zapato> getVistosRecientemente() {
        return vistosRecientemente;
    }
}
