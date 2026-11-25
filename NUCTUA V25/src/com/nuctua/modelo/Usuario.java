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

    public String getApellido() {
        String limpio = nombre.trim();
        int espacio = limpio.indexOf(' ');
        return espacio < 0 ? "" : limpio.substring(espacio + 1).trim();
    }

    public String getIniciales() {
        String[] partes = nombre.trim().split("[ ]+");
        String primera = partes[0].isEmpty() ? "N" : partes[0].substring(0, 1);
        String ultima = partes.length > 1 ? partes[partes.length - 1].substring(0, 1) : "";
        return (primera + ultima).toUpperCase();
    }

    public String getPrimerNombre() {
        String[] partes = nombre.trim().split("\\s+");
        return partes.length > 0 ? partes[0] : nombre;
    }
}
