package com.nuctua.servicio;

import com.nuctua.estructuras.ListaEnlazada;
import com.nuctua.modelo.Cliente;
import com.nuctua.modelo.DatosEnvio;
import com.nuctua.modelo.SesionActual;
import com.nuctua.modelo.Usuario;
import com.nuctua.persistencia.ArchivoUsuarios;

public class ClienteServicio {

    private final ArchivoUsuarios archivo = new ArchivoUsuarios();

    public boolean guardarDatosEnvio(DatosEnvio datos) {
        Cliente enSesion = SesionActual.getInstancia().getCliente();
        if (enSesion == null || datos == null) {
            return false;
        }
        ListaEnlazada<Usuario> usuarios = archivo.cargar();
        Usuario encontrado = usuarios.buscar(u -> u.getCorreo().equalsIgnoreCase(enSesion.getCorreo()));
        if (!(encontrado instanceof Cliente guardado)) {
            return false;
        }
        aplicar(guardado, datos);
        archivo.guardar(usuarios);
        aplicar(enSesion, datos);
        return true;
    }

    private void aplicar(Cliente cliente, DatosEnvio datos) {
        cliente.setTelefono(datos.getTelefono());
        cliente.setCiudad(datos.getCiudad());
        cliente.setDireccion(datos.getDireccion());
        cliente.setIndicaciones(datos.getIndicaciones());
    }
}
