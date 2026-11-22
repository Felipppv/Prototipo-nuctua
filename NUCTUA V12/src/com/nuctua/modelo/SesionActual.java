package com.nuctua.modelo;

import com.nuctua.estructuras.Pila;

public final class SesionActual {

    private static final int MAXIMO_VISTOS = 6;
    private static final int MAXIMO_BUSQUEDAS = 3;
    private static SesionActual instancia;

    private Usuario usuario;
    private final Pila<Zapato> vistosRecientemente;
    private final Pila<String> busquedasRecientes;

    private SesionActual() {
        vistosRecientemente = new Pila<>(MAXIMO_VISTOS);
        busquedasRecientes = new Pila<>(MAXIMO_BUSQUEDAS);
    }

    public static SesionActual getInstancia() {
        if (instancia == null) {
            instancia = new SesionActual();
        }
        return instancia;
    }

    public void iniciar(Usuario usuario) {
        this.usuario = usuario;
        vaciarHistoriales();
    }

    public void cerrar() {
        usuario = null;
        vaciarHistoriales();
    }

    private void vaciarHistoriales() {
        while (!vistosRecientemente.estaVacia()) {
            vistosRecientemente.desapilar();
        }
        while (!busquedasRecientes.estaVacia()) {
            busquedasRecientes.desapilar();
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
        if (zapato == null) {
            return;
        }
        vistosRecientemente.quitar(z -> z.getSku().equals(zapato.getSku()));
        vistosRecientemente.apilar(zapato);
    }

    public Pila<Zapato> getVistosRecientemente() {
        return vistosRecientemente;
    }

    public void registrarBusqueda(String texto) {
        if (texto == null || texto.isBlank()) {
            return;
        }
        String limpio = texto.trim().replaceAll("\s+", " ");
        quitarBusqueda(limpio);
        busquedasRecientes.apilar(limpio);
    }

    public void quitarBusqueda(String texto) {
        if (texto != null) {
            busquedasRecientes.quitar(b -> b.equalsIgnoreCase(texto.trim()));
        }
    }

    public Pila<String> getBusquedasRecientes() {
        return busquedasRecientes;
    }
}
