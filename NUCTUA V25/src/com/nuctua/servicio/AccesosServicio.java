package com.nuctua.servicio;

import com.nuctua.modelo.Administrador;
import com.nuctua.modelo.Usuario;
import com.nuctua.persistencia.GestorArchivos;
import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;

public class AccesosServicio {

    private static final String NOMBRE = "accesos.txt";

    public void registrar(Usuario usuario) {
        if (!(usuario instanceof Administrador)) {
            return;
        }
        try {
            String anterior = campo(usuario, 2);
            ArrayList<String> linea = new ArrayList<>();
            linea.add(GestorArchivos.limpiar(usuario.getCorreo()) + GestorArchivos.SEPARADOR + anterior
                    + GestorArchivos.SEPARADOR + LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS));
            GestorArchivos.reemplazarDe(NOMBRE, usuario.getCorreo(), linea);
        } catch (IllegalStateException e) {
            System.err.println("No se pudo registrar el acceso.");
        }
    }

    public LocalDateTime anterior(Usuario usuario) {
        try {
            return LocalDateTime.parse(campo(usuario, 1));
        } catch (DateTimeParseException | IllegalStateException e) {
            return null;
        }
    }

    private String campo(Usuario usuario, int indice) {
        for (String linea : GestorArchivos.leerLineas(NOMBRE)) {
            String[] campos = linea.split(GestorArchivos.SEPARADOR_REGEX, -1);
            if (campos.length > 2 && campos[0].equalsIgnoreCase(usuario.getCorreo())) {
                return campos[indice];
            }
        }
        return "";
    }
}
