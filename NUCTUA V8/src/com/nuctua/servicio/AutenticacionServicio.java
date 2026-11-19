package com.nuctua.servicio;

import com.nuctua.estructuras.ListaEnlazada;
import com.nuctua.modelo.Rol;
import com.nuctua.modelo.SesionActual;
import com.nuctua.modelo.Usuario;
import com.nuctua.persistencia.ArchivoUsuarios;
import com.nuctua.persistencia.GestorArchivos;
import java.util.ArrayList;

public class AutenticacionServicio {

    private static final String ARCHIVO_RECORDAR = "recordar.txt";

    private final ArchivoUsuarios archivo;
    private ListaEnlazada<Usuario> usuarios;

    public AutenticacionServicio() {
        archivo = new ArchivoUsuarios();
        usuarios = archivo.cargar();
    }

    public ResultadoAcceso iniciarSesion(String correo, String contrasena, Rol rolRequerido) {
        String correoLimpio = correo == null ? "" : correo.trim();
        String clave = contrasena == null ? "" : contrasena;
        if (correoLimpio.isEmpty() || clave.isEmpty()) {
            return ResultadoAcceso.error(ResultadoAcceso.Tipo.CAMPOS_VACIOS, "Escribe tu correo y tu contraseña.");
        }
        if (!esCorreoValido(correoLimpio)) {
            return ResultadoAcceso.error(ResultadoAcceso.Tipo.CORREO_INVALIDO, "El correo no tiene un formato válido.");
        }
        usuarios = archivo.cargar();
        Usuario encontrado = usuarios.buscar(u -> u.validarCredenciales(correoLimpio, clave));
        if (encontrado == null) {
            return ResultadoAcceso.error(ResultadoAcceso.Tipo.CREDENCIALES,
                    "El correo o la contraseña no coinciden con ninguna cuenta. Revisa e inténtalo de nuevo.");
        }
        if (rolRequerido != null && encontrado.getRol() != rolRequerido) {
            return ResultadoAcceso.error(ResultadoAcceso.Tipo.ROL,
                    "Esta cuenta no tiene permisos de administrador. Entra desde el acceso de clientes.");
        }
        SesionActual.getInstancia().iniciar(encontrado);
        return ResultadoAcceso.exito(encontrado);
    }

    public void cerrarSesion() {
        SesionActual.getInstancia().cerrar();
    }

    public static boolean esCorreoValido(String correo) {
        int arroba = correo.indexOf('@');
        int punto = correo.lastIndexOf('.');
        return arroba > 0 && arroba == correo.lastIndexOf('@') && punto > arroba + 1
                && punto < correo.length() - 1 && !correo.contains(" ");
    }

    public void recordarCorreo(String correo, boolean recordar) {
        ArrayList<String> lineas = new ArrayList<>();
        if (recordar && correo != null && !correo.isBlank()) {
            lineas.add(GestorArchivos.limpiar(correo.trim()));
        }
        GestorArchivos.escribirLineas(ARCHIVO_RECORDAR, lineas);
    }

    public String correoRecordado() {
        ArrayList<String> lineas = GestorArchivos.leerLineas(ARCHIVO_RECORDAR);
        return lineas.isEmpty() ? "" : lineas.get(0);
    }
}
