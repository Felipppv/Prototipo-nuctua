package com.nuctua.servicio;

import com.nuctua.estructuras.ListaEnlazada;
import com.nuctua.modelo.Administrador;
import com.nuctua.modelo.CampoPerfil;
import com.nuctua.modelo.Cliente;
import com.nuctua.modelo.ErrorCampo;
import com.nuctua.modelo.ResumenCliente;
import com.nuctua.modelo.SesionActual;
import com.nuctua.modelo.Usuario;
import com.nuctua.persistencia.ArchivoDeseos;
import com.nuctua.persistencia.ArchivoPedidos;
import com.nuctua.persistencia.ArchivoUsuarios;
import com.nuctua.util.Validaciones;
import java.time.LocalDate;

public class PerfilServicio {

    private final ClienteServicio clientes = new ClienteServicio();

    public ResumenCliente resumen(Cliente cliente) {
        return new ResumenCliente(cliente, new ArchivoPedidos().cargarDe(cliente.getCorreo()),
                new ArchivoDeseos().cargar(cliente.getCorreo()).size(), LocalDate.now());
    }

    public ListaEnlazada<ErrorCampo> validarCliente(String nombre, String apellido, String telefono, String cumpleanos,
            String documento, LocalDate hoy) {
        ListaEnlazada<ErrorCampo> errores = new ListaEnlazada<>();
        if (!Validaciones.esNombre(nombre)) {
            errores.insertarFinal(new ErrorCampo(CampoPerfil.NOMBRE, "Escribe tu nombre, solo letras."));
        }
        if (!Validaciones.esNombre(apellido)) {
            errores.insertarFinal(new ErrorCampo(CampoPerfil.APELLIDO, "Escribe tu apellido, solo letras."));
        }
        validarTelefono(telefono, errores);
        if (!Validaciones.digitos(cumpleanos).isEmpty() && Validaciones.fechaNacimiento(cumpleanos, hoy) == null) {
            errores.insertarFinal(new ErrorCampo(CampoPerfil.CUMPLEANOS, "Escribe una fecha real: día, mes y año."));
        }
        if (!Validaciones.digitos(documento).isEmpty() && !Validaciones.esDocumento(documento)) {
            errores.insertarFinal(new ErrorCampo(CampoPerfil.DOCUMENTO, "El documento lleva de 6 a 10 números."));
        }
        return errores;
    }

    public ListaEnlazada<ErrorCampo> validarAdministrador(String nombre, String telefono, String ciudad) {
        ListaEnlazada<ErrorCampo> errores = new ListaEnlazada<>();
        if (!Validaciones.esNombre(nombre)) {
            errores.insertarFinal(new ErrorCampo(CampoPerfil.NOMBRE, "Escribe tu nombre, solo letras."));
        }
        validarTelefono(telefono, errores);
        if (!Validaciones.limpiar(ciudad).isEmpty() && !Validaciones.esCiudad(ciudad)) {
            errores.insertarFinal(new ErrorCampo(CampoPerfil.CIUDAD, "Escribe una ciudad válida."));
        }
        return errores;
    }

    public boolean guardarCliente(String nombre, String apellido, String telefono, String cumpleanos, String documento) {
        if (!validarCliente(nombre, apellido, telefono, cumpleanos, documento, LocalDate.now()).estaVacia()) {
            return false;
        }
        try {
            boolean guardado = clientes.actualizar(cliente -> {
                cliente.setNombre(Validaciones.limpiar(nombre) + " " + Validaciones.limpiar(apellido));
                cliente.setTelefono(Validaciones.digitos(telefono));
                cliente.setCumpleanos(Validaciones.digitos(cumpleanos));
                cliente.setDocumento(Validaciones.digitos(documento));
            });
            if (guardado) {
                SesionActual.getInstancia().setBorradorEnvio(null);
            }
            return guardado;
        } catch (IllegalStateException e) {
            return false;
        }
    }

    public boolean guardarAdministrador(String nombre, String telefono, String ciudad) {
        Usuario enSesion = SesionActual.getInstancia().getUsuario();
        if (!(enSesion instanceof Administrador sesion) || !validarAdministrador(nombre, telefono, ciudad).estaVacia()) {
            return false;
        }
        try {
            ArchivoUsuarios archivo = new ArchivoUsuarios();
            ListaEnlazada<Usuario> usuarios = archivo.cargar();
            Usuario encontrado = usuarios.buscar(u -> u.getCorreo().equalsIgnoreCase(sesion.getCorreo()));
            if (!(encontrado instanceof Administrador guardado)) {
                return false;
            }
            aplicar(guardado, nombre, telefono, ciudad);
            archivo.guardar(usuarios);
            aplicar(sesion, nombre, telefono, ciudad);
            return true;
        } catch (IllegalStateException e) {
            return false;
        }
    }

    private void aplicar(Administrador admin, String nombre, String telefono, String ciudad) {
        admin.setNombre(Validaciones.limpiar(nombre));
        admin.setTelefono(Validaciones.digitos(telefono));
        admin.setCiudad(Validaciones.limpiar(ciudad));
    }

    private void validarTelefono(String telefono, ListaEnlazada<ErrorCampo> errores) {
        if (!Validaciones.digitos(telefono).isEmpty() && !Validaciones.esTelefono(telefono)) {
            errores.insertarFinal(new ErrorCampo(CampoPerfil.TELEFONO, "Escribe 10 números: celular (3…) o fijo (60…)."));
        }
    }
}
