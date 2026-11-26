package com.nuctua.servicio;

import com.nuctua.estructuras.ListaEnlazada;
import com.nuctua.modelo.Cliente;
import com.nuctua.modelo.DatosEnvio;
import com.nuctua.modelo.Direccion;
import com.nuctua.modelo.SesionActual;
import com.nuctua.modelo.Usuario;
import com.nuctua.persistencia.ArchivoUsuarios;
import java.util.function.Consumer;

public class ClienteServicio {

    private final ArchivoUsuarios archivo = new ArchivoUsuarios();

    public boolean guardarDatosEnvio(DatosEnvio datos) {
        return datos != null && actualizar(cliente -> {
            cliente.setTelefono(datos.getTelefono());
            cliente.setCiudad(datos.getCiudad());
            cliente.setDireccion(datos.getDireccion());
            cliente.setIndicaciones(datos.getIndicaciones());
        });
    }

    public boolean guardarDireccion(Direccion direccion) {
        return actualizar(cliente -> {
            cliente.setCiudad(direccion == null ? "" : direccion.getCiudad());
            cliente.setDireccion(direccion == null ? "" : direccion.getDireccion());
            cliente.setIndicaciones(direccion == null ? "" : direccion.getIndicaciones());
        });
    }

    public boolean actualizar(Consumer<Cliente> cambio) {
        Cliente enSesion = SesionActual.getInstancia().getCliente();
        if (enSesion == null) {
            return false;
        }
        ListaEnlazada<Usuario> usuarios = archivo.cargar();
        Usuario encontrado = usuarios.buscar(u -> u.getCorreo().equalsIgnoreCase(enSesion.getCorreo()));
        if (!(encontrado instanceof Cliente guardado)) {
            return false;
        }
        cambio.accept(guardado);
        archivo.guardar(usuarios);
        cambio.accept(enSesion);
        return true;
    }
}
