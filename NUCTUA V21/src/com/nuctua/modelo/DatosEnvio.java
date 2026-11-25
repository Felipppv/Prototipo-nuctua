package com.nuctua.modelo;

import com.nuctua.estructuras.ListaEnlazada;
import com.nuctua.util.Validaciones;

public class DatosEnvio {

    private final String nombre;
    private final String telefono;
    private final String correo;
    private final String ciudad;
    private final String direccion;
    private final String indicaciones;

    public DatosEnvio(String nombre, String telefono, String correo, String ciudad, String direccion, String indicaciones) {
        this.nombre = Validaciones.limpiar(nombre);
        this.telefono = Validaciones.digitos(telefono);
        this.correo = Validaciones.limpiar(correo).toLowerCase();
        this.ciudad = Validaciones.limpiar(ciudad);
        this.direccion = Validaciones.limpiar(direccion);
        this.indicaciones = Validaciones.limpiar(indicaciones);
    }

    public static DatosEnvio de(Cliente cliente) {
        return new DatosEnvio(cliente.getNombre(), cliente.getTelefono(), cliente.getCorreo(), cliente.getCiudad(),
                cliente.getDireccion(), cliente.getIndicaciones());
    }

    public ListaEnlazada<ErrorCampo> validar() {
        ListaEnlazada<ErrorCampo> errores = new ListaEnlazada<>();
        if (!Validaciones.esNombreCompleto(nombre)) {
            errores.insertarFinal(new ErrorCampo(CampoCompra.NOMBRE, "Escribe tu nombre y apellido, solo letras."));
        }
        if (!Validaciones.esTelefono(telefono)) {
            errores.insertarFinal(new ErrorCampo(CampoCompra.TELEFONO, "Escribe 10 números: celular (3…) o fijo (60…)."));
        }
        if (!Validaciones.esCorreo(correo)) {
            errores.insertarFinal(new ErrorCampo(CampoCompra.CORREO, "Escribe un correo válido, como tu@correo.com."));
        }
        if (!Validaciones.esCiudad(ciudad)) {
            errores.insertarFinal(new ErrorCampo(CampoCompra.CIUDAD, "Escribe la ciudad de entrega."));
        }
        if (!Validaciones.esDireccion(direccion)) {
            errores.insertarFinal(new ErrorCampo(CampoCompra.DIRECCION, "Escribe la dirección con vía y número."));
        }
        return errores;
    }

    public String getNombre() {
        return nombre;
    }

    public String getTelefono() {
        return telefono;
    }

    public String getCorreo() {
        return correo;
    }

    public String getCiudad() {
        return ciudad;
    }

    public String getDireccion() {
        return direccion;
    }

    public String getIndicaciones() {
        return indicaciones;
    }
}
