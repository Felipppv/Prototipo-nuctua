package com.nuctua.modelo;

import com.nuctua.estructuras.ListaDobleDeseos;
import com.nuctua.estructuras.ListaCarrito;
import com.nuctua.estructuras.ListaEnlazada;
import com.nuctua.estructuras.PilaHistorial;

public final class SesionActual {

    private static final int MAXIMO_VISTOS = 6;
    private static final int MAXIMO_BUSQUEDAS = 3;
    private static final int MAXIMO_BUSQUEDAS_ADMIN = 4;
    private static SesionActual instancia;

    private Usuario usuario;
    private final PilaHistorial<Zapato> vistosRecientemente;
    private final PilaHistorial<String> busquedasRecientes;
    private ListaDobleDeseos deseos;
    private ListaCarrito carrito;
    private DatosEnvio borradorEnvio;
    private final ListaEnlazada<CambioStock> cambiosStock = new ListaEnlazada<>();
    private final PilaHistorial<String> busquedasAdmin;

    private SesionActual() {
        vistosRecientemente = new PilaHistorial<>(MAXIMO_VISTOS, (a, b) -> a.getSku().equalsIgnoreCase(b.getSku()));
        busquedasRecientes = new PilaHistorial<>(MAXIMO_BUSQUEDAS, String::equalsIgnoreCase);
        busquedasAdmin = new PilaHistorial<>(MAXIMO_BUSQUEDAS_ADMIN, String::equalsIgnoreCase);
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
        vistosRecientemente.vaciar();
        busquedasRecientes.vaciar();
        deseos = null;
        carrito = null;
        borradorEnvio = null;
        cambiosStock.vaciar();
        busquedasAdmin.vaciar();
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
        vistosRecientemente.registrarSinRepetir(zapato);
    }

    public PilaHistorial<Zapato> getVistosRecientemente() {
        return vistosRecientemente;
    }

    public void registrarBusqueda(String texto) {
        if (texto == null || texto.isBlank()) {
            return;
        }
        String limpio = texto.trim().replaceAll("\s+", " ");
        busquedasRecientes.registrarSinRepetir(limpio);
    }

    public void quitarBusqueda(String texto) {
        if (texto != null) {
            busquedasRecientes.quitar(b -> b.equalsIgnoreCase(texto.trim()));
        }
    }

    public PilaHistorial<String> getBusquedasRecientes() {
        return busquedasRecientes;
    }

    public ListaDobleDeseos getDeseos() {
        return deseos;
    }

    public void setDeseos(ListaDobleDeseos deseos) {
        this.deseos = deseos;
    }

    public ListaCarrito getCarrito() {
        return carrito;
    }

    public void setCarrito(ListaCarrito carrito) {
        this.carrito = carrito;
    }

    public DatosEnvio getBorradorEnvio() {
        return borradorEnvio;
    }

    public void setBorradorEnvio(DatosEnvio borradorEnvio) {
        this.borradorEnvio = borradorEnvio;
    }

    public ListaEnlazada<CambioStock> getCambiosStock() {
        return cambiosStock;
    }

    public PilaHistorial<String> getBusquedasAdmin() {
        return busquedasAdmin;
    }
}
