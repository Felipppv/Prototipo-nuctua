package com.nuctua.modelo;

import java.time.LocalDate;

public class Cliente extends Usuario {

    private String telefono;
    private String direccion;
    private String ciudad;
    private String indicaciones = "";
    private String documento = "";
    private String cumpleanos = "";
    private LocalDate registro;

    public Cliente(String correo, String contrasena, String nombre, String telefono, String direccion, String ciudad) {
        super(correo, contrasena, nombre);
        this.telefono = telefono;
        this.direccion = direccion;
        this.ciudad = ciudad;
    }

    @Override
    public Rol getRol() {
        return Rol.CLIENTE;
    }

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    public String getDireccion() {
        return direccion;
    }

    public void setDireccion(String direccion) {
        this.direccion = direccion;
    }

    public String getCiudad() {
        return ciudad;
    }

    public void setCiudad(String ciudad) {
        this.ciudad = ciudad;
    }

    public String getIndicaciones() {
        return indicaciones;
    }

    public void setIndicaciones(String indicaciones) {
        this.indicaciones = indicaciones == null ? "" : indicaciones;
    }

    public String getDocumento() {
        return documento;
    }

    public void setDocumento(String documento) {
        this.documento = documento == null ? "" : documento;
    }

    public String getCumpleanos() {
        return cumpleanos;
    }

    public void setCumpleanos(String cumpleanos) {
        this.cumpleanos = cumpleanos == null ? "" : cumpleanos;
    }

    public LocalDate getRegistro() {
        return registro;
    }

    public void setRegistro(LocalDate registro) {
        this.registro = registro;
    }
}
