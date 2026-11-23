package com.nuctua.persistencia;

import com.nuctua.estructuras.ListaEnlazada;
import com.nuctua.modelo.Administrador;
import com.nuctua.modelo.Cliente;
import com.nuctua.modelo.Rol;
import com.nuctua.modelo.Usuario;
import java.util.ArrayList;

public class ArchivoUsuarios {

    private static final String NOMBRE = "usuarios.txt";

    public ListaEnlazada<Usuario> cargar() {
        ListaEnlazada<Usuario> usuarios = new ListaEnlazada<>();
        for (String linea : GestorArchivos.leerLineas(NOMBRE)) {
            String[] campos = linea.split(GestorArchivos.SEPARADOR_REGEX, -1);
            if (campos.length < 4) {
                continue;
            }
            Rol rol = Rol.valueOf(campos[0]);
            if (rol == Rol.ADMINISTRADOR) {
                usuarios.insertarFinal(new Administrador(campos[1], campos[2], campos[3], valor(campos, 4)));
            } else {
                usuarios.insertarFinal(new Cliente(campos[1], campos[2], campos[3],
                        valor(campos, 4), valor(campos, 5), valor(campos, 6)));
            }
        }
        return usuarios;
    }

    public void guardar(ListaEnlazada<Usuario> usuarios) {
        ArrayList<String> lineas = new ArrayList<>();
        for (Usuario u : usuarios) {
            StringBuilder linea = new StringBuilder();
            linea.append(u.getRol().name()).append(GestorArchivos.SEPARADOR)
                    .append(GestorArchivos.limpiar(u.getCorreo())).append(GestorArchivos.SEPARADOR)
                    .append(GestorArchivos.limpiar(u.getContrasena())).append(GestorArchivos.SEPARADOR)
                    .append(GestorArchivos.limpiar(u.getNombre()));
            if (u instanceof Administrador a) {
                linea.append(GestorArchivos.SEPARADOR).append(GestorArchivos.limpiar(a.getCargo()));
            } else if (u instanceof Cliente c) {
                linea.append(GestorArchivos.SEPARADOR).append(GestorArchivos.limpiar(c.getTelefono()))
                        .append(GestorArchivos.SEPARADOR).append(GestorArchivos.limpiar(c.getDireccion()))
                        .append(GestorArchivos.SEPARADOR).append(GestorArchivos.limpiar(c.getCiudad()));
            }
            lineas.add(linea.toString());
        }
        GestorArchivos.escribirLineas(NOMBRE, lineas);
    }

    private String valor(String[] campos, int indice) {
        return indice < campos.length ? campos[indice] : "";
    }
}
