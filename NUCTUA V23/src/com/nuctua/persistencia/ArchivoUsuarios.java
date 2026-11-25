package com.nuctua.persistencia;

import com.nuctua.estructuras.ListaEnlazada;
import com.nuctua.modelo.Administrador;
import com.nuctua.modelo.Cliente;
import com.nuctua.modelo.Rol;
import com.nuctua.modelo.Usuario;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;

public class ArchivoUsuarios {

    private static final String NOMBRE = "usuarios.txt";

    public ListaEnlazada<Usuario> cargar() {
        ListaEnlazada<Usuario> usuarios = new ListaEnlazada<>();
        for (String linea : GestorArchivos.leerLineas(NOMBRE)) {
            Usuario usuario = leer(linea);
            if (usuario != null) {
                usuarios.insertarFinal(usuario);
            }
        }
        return usuarios;
    }

    public void guardar(ListaEnlazada<Usuario> usuarios) {
        ArrayList<String> lineas = new ArrayList<>();
        for (Usuario usuario : usuarios) {
            lineas.add(escribir(usuario));
        }
        for (String linea : GestorArchivos.leerLineas(NOMBRE)) {
            if (leer(linea) == null) {
                lineas.add(linea);
            }
        }
        GestorArchivos.escribirLineas(NOMBRE, lineas);
    }

    private Usuario leer(String linea) {
        String[] campos = linea.split(GestorArchivos.SEPARADOR_REGEX, -1);
        if (campos.length < 4 || campos[1].isBlank()) {
            return null;
        }
        if (campos[0].equals(Rol.ADMINISTRADOR.name())) {
            Administrador admin = new Administrador(campos[1], campos[2], campos[3], valor(campos, 4));
            admin.setTelefono(valor(campos, 5));
            admin.setCiudad(valor(campos, 6));
            admin.setRegistro(fecha(valor(campos, 7)));
            return admin;
        }
        if (!campos[0].equals(Rol.CLIENTE.name())) {
            return null;
        }
        Cliente cliente = new Cliente(campos[1], campos[2], campos[3], valor(campos, 4), valor(campos, 5), valor(campos, 6));
        cliente.setIndicaciones(valor(campos, 7));
        cliente.setDocumento(valor(campos, 8));
        cliente.setCumpleanos(valor(campos, 9));
        cliente.setRegistro(fecha(valor(campos, 10)));
        return cliente;
    }

    private String escribir(Usuario usuario) {
        if (usuario instanceof Administrador a) {
            return unir(a.getRol().name(), a.getCorreo(), a.getContrasena(), a.getNombre(), a.getCargo(),
                    a.getTelefono(), a.getCiudad(), texto(a.getRegistro()));
        }
        Cliente c = (Cliente) usuario;
        return unir(c.getRol().name(), c.getCorreo(), c.getContrasena(), c.getNombre(), c.getTelefono(), c.getDireccion(),
                c.getCiudad(), c.getIndicaciones(), c.getDocumento(), c.getCumpleanos(), texto(c.getRegistro()));
    }

    private String unir(String... campos) {
        StringBuilder linea = new StringBuilder();
        for (int i = 0; i < campos.length; i++) {
            if (i > 0) {
                linea.append(GestorArchivos.SEPARADOR);
            }
            linea.append(GestorArchivos.limpiar(campos[i]));
        }
        return linea.toString();
    }

    private String valor(String[] campos, int indice) {
        return indice < campos.length ? campos[indice] : "";
    }

    private String texto(LocalDate fecha) {
        return fecha == null ? "" : fecha.toString();
    }

    private LocalDate fecha(String texto) {
        try {
            return texto.isBlank() ? null : LocalDate.parse(texto);
        } catch (DateTimeParseException e) {
            return null;
        }
    }
}
