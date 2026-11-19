package com.nuctua.modelo;

public class Cliente extends Usuario {

    private String telefono;
    private String direccion;
    private String ciudad;

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
}
