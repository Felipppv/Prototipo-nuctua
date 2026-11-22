package com.nuctua.modelo;

public abstract class Usuario {

    private String correo;
    private String contrasena;
    private String nombre;

    protected Usuario(String correo, String contrasena, String nombre) {
        this.correo = correo;
        this.contrasena = contrasena;
        this.nombre = nombre;
    }

    public abstract Rol getRol();

    public boolean validarCredenciales(String correoIngresado, String contrasenaIngresada) {
        return correo.equalsIgnoreCase(correoIngresado.trim()) && contrasena.equals(contrasenaIngresada);
    }

    public String getCorreo() {
        return correo;
    }

    public void setCorreo(String correo) {
        this.correo = correo;
    }

    public String getContrasena() {
        return contrasena;
    }

    public void setContrasena(String contrasena) {
        this.contrasena = contrasena;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getPrimerNombre() {
        String[] partes = nombre.trim().split("\\s+");
        return partes.length > 0 ? partes[0] : nombre;
    }
}
