package com.nuctua.modelo;

import java.time.LocalDate;

public class Administrador extends Usuario {

    private String cargo;
    private String telefono = "";
    private String ciudad = "";
    private LocalDate registro;

    public Administrador(String correo, String contrasena, String nombre, String cargo) {
        super(correo, contrasena, nombre);
        this.cargo = cargo;
    }

    @Override
    public Rol getRol() {
        return Rol.ADMINISTRADOR;
    }

    public String getCargo() {
        return cargo;
    }

    public void setCargo(String cargo) {
        this.cargo = cargo;
    }

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono == null ? "" : telefono;
    }

    public String getCiudad() {
        return ciudad;
    }

    public void setCiudad(String ciudad) {
        this.ciudad = ciudad == null ? "" : ciudad;
    }

    public LocalDate getRegistro() {
        return registro;
    }

    public void setRegistro(LocalDate registro) {
        this.registro = registro;
    }
}
