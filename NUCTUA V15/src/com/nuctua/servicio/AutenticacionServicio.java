package com.nuctua.servicio;

import com.nuctua.estructuras.ListaEnlazada;
import com.nuctua.modelo.Cliente;
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

    public ResultadoAcceso registrar(String nombre, String correo, String contrasena, String confirmacion, boolean aceptaTerminos) {
        String nombreLimpio = nombre == null ? "" : nombre.trim().replaceAll("\\s+", " ");
        String correoLimpio = correo == null ? "" : correo.trim().toLowerCase();
        String clave = contrasena == null ? "" : contrasena;
        if (nombreLimpio.isEmpty() || correoLimpio.isEmpty() || clave.isEmpty() || confirmacion == null || confirmacion.isEmpty()) {
            return ResultadoAcceso.error(ResultadoAcceso.Tipo.CAMPOS_VACIOS, "Completa todos los campos para crear tu cuenta.");
        }
        if (nombreLimpio.length() < 3 || !nombreLimpio.matches("[\\p{L} ]+")) {
            return ResultadoAcceso.error(ResultadoAcceso.Tipo.NOMBRE_INVALIDO, "Escribe tu nombre completo usando solo letras.");
        }
        if (!esCorreoValido(correoLimpio)) {
            return ResultadoAcceso.error(ResultadoAcceso.Tipo.CORREO_INVALIDO, "El correo no tiene un formato válido.");
        }
        usuarios = archivo.cargar();
        if (usuarios.contiene(u -> u.getCorreo().equalsIgnoreCase(correoLimpio))) {
            return ResultadoAcceso.error(ResultadoAcceso.Tipo.CORREO_EXISTENTE, "Ya existe una cuenta con ese correo. Inicia sesión.");
        }
        ResultadoAcceso claveInvalida = validarClave(clave, confirmacion);
        if (claveInvalida != null) {
            return claveInvalida;
        }
        if (!aceptaTerminos) {
            return ResultadoAcceso.error(ResultadoAcceso.Tipo.TERMINOS, "Debes aceptar los términos y condiciones.");
        }
        Cliente nuevo = new Cliente(correoLimpio, clave, nombreLimpio, "", "", "");
        usuarios.insertarFinal(nuevo);
        archivo.guardar(usuarios);
        return ResultadoAcceso.exito(nuevo);
    }

    public ResultadoAcceso recuperarContrasena(String correo, String nombre, String nueva, String confirmacion) {
        String correoLimpio = correo == null ? "" : correo.trim();
        String nombreLimpio = nombre == null ? "" : nombre.trim();
        String clave = nueva == null ? "" : nueva;
        if (correoLimpio.isEmpty() || nombreLimpio.isEmpty() || clave.isEmpty() || confirmacion == null || confirmacion.isEmpty()) {
            return ResultadoAcceso.error(ResultadoAcceso.Tipo.CAMPOS_VACIOS, "Completa todos los campos para recuperar tu cuenta.");
        }
        if (!esCorreoValido(correoLimpio)) {
            return ResultadoAcceso.error(ResultadoAcceso.Tipo.CORREO_INVALIDO, "El correo no tiene un formato válido.");
        }
        usuarios = archivo.cargar();
        Usuario cuenta = usuarios.buscar(u -> u.getCorreo().equalsIgnoreCase(correoLimpio));
        if (cuenta == null || cuenta.getRol() != Rol.CLIENTE
                || !CatalogoServicio.normalizar(cuenta.getNombre()).replaceAll("\\s+", " ")
                        .equals(CatalogoServicio.normalizar(nombreLimpio).replaceAll("\\s+", " "))) {
            return ResultadoAcceso.error(ResultadoAcceso.Tipo.CREDENCIALES,
                    "Los datos no coinciden con ninguna cuenta de cliente. Revisa tu correo y tu nombre completo.");
        }
        ResultadoAcceso claveInvalida = validarClave(clave, confirmacion);
        if (claveInvalida != null) {
            return claveInvalida;
        }
        if (cuenta.getContrasena().equals(clave)) {
            return ResultadoAcceso.error(ResultadoAcceso.Tipo.CONTRASENA_DEBIL, "La nueva contraseña debe ser distinta a la anterior.");
        }
        cuenta.setContrasena(clave);
        archivo.guardar(usuarios);
        return ResultadoAcceso.exito(cuenta);
    }

    private ResultadoAcceso validarClave(String clave, String confirmacion) {
        if (clave.length() < 8 || !clave.matches(".*\\p{L}.*") || !clave.matches(".*\\d.*")) {
            return ResultadoAcceso.error(ResultadoAcceso.Tipo.CONTRASENA_DEBIL,
                    "La contraseña debe tener mínimo 8 caracteres, con letras y números.");
        }
        if (!clave.equals(confirmacion)) {
            return ResultadoAcceso.error(ResultadoAcceso.Tipo.CONFIRMACION, "Las contraseñas no coinciden.");
        }
        return null;
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
