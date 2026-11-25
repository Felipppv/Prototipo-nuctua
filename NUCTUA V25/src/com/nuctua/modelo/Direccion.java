package com.nuctua.modelo;

import com.nuctua.estructuras.ListaEnlazada;
import com.nuctua.util.Validaciones;

public class Direccion {

    private final String etiqueta;
    private final String recibe;
    private final String direccion;
    private final String indicaciones;
    private final String ciudad;
    private boolean principal;

    public Direccion(String etiqueta, String recibe, String direccion, String indicaciones, String ciudad, boolean principal) {
        this.etiqueta = Validaciones.limpiar(etiqueta).isEmpty() ? "Casa" : Validaciones.limpiar(etiqueta);
        this.recibe = Validaciones.limpiar(recibe);
        this.direccion = Validaciones.limpiar(direccion);
        this.indicaciones = Validaciones.limpiar(indicaciones);
        this.ciudad = Validaciones.limpiar(ciudad);
        this.principal = principal;
    }

    public ListaEnlazada<ErrorCampo> validar() {
        ListaEnlazada<ErrorCampo> errores = new ListaEnlazada<>();
        if (!Validaciones.esNombreCompleto(recibe)) {
            errores.insertarFinal(new ErrorCampo(CampoPerfil.RECIBE, "Escribe nombre y apellido de quien recibe."));
        }
        if (!Validaciones.esDireccion(direccion)) {
            errores.insertarFinal(new ErrorCampo(CampoPerfil.DIRECCION, "Escribe la dirección con vía y número."));
        }
        if (!Validaciones.esCiudad(ciudad)) {
            errores.insertarFinal(new ErrorCampo(CampoPerfil.CIUDAD, "Escribe la ciudad de entrega."));
        }
        return errores;
    }

    public String getEtiqueta() {
        return etiqueta;
    }

    public String getRecibe() {
        return recibe;
    }

    public String getDireccion() {
        return direccion;
    }

    public String getIndicaciones() {
        return indicaciones;
    }

    public String getCiudad() {
        return ciudad;
    }

    public String getLinea() {
        return indicaciones.isEmpty() ? direccion : direccion + ", " + indicaciones;
    }

    public boolean isPrincipal() {
        return principal;
    }

    public void setPrincipal(boolean principal) {
        this.principal = principal;
    }
}
