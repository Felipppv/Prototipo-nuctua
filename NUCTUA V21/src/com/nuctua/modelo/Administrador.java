package com.nuctua.modelo;

public class Administrador extends Usuario {

    private String cargo;

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
}
