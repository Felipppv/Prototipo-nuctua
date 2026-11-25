package com.nuctua.servicio;

import com.nuctua.estructuras.ListaEnlazada;
import com.nuctua.estructuras.Pila;
import com.nuctua.modelo.Actividad;
import com.nuctua.modelo.SesionActual;
import com.nuctua.persistencia.GestorArchivos;
import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;

public class ActividadServicio {

    private static final String NOMBRE = "actividad.txt";
    private static final int MAXIMO = 200;

    public void registrar(String texto) {
        if (!SesionActual.getInstancia().esAdministrador() || texto == null || texto.isBlank()) {
            return;
        }
        try {
            ArrayList<String> lineas = GestorArchivos.leerLineas(NOMBRE);
            while (lineas.size() >= MAXIMO) {
                lineas.remove(0);
            }
            lineas.add(LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS) + GestorArchivos.SEPARADOR
                    + GestorArchivos.limpiar(SesionActual.getInstancia().getUsuario().getCorreo())
                    + GestorArchivos.SEPARADOR + GestorArchivos.limpiar(texto));
            GestorArchivos.escribirLineas(NOMBRE, lineas);
        } catch (IllegalStateException e) {
            System.err.println("No se pudo registrar la actividad.");
        }
    }

    public ListaEnlazada<Actividad> recientes(int maximo) {
        Pila<Actividad> pila = new Pila<>();
        if (SesionActual.getInstancia().esAdministrador()) {
            String correo = SesionActual.getInstancia().getUsuario().getCorreo();
            for (String linea : GestorArchivos.leerLineas(NOMBRE)) {
                String[] campos = linea.split(GestorArchivos.SEPARADOR_REGEX, -1);
                if (campos.length > 2 && campos[1].equalsIgnoreCase(correo)) {
                    apilar(pila, campos);
                }
            }
        }
        ListaEnlazada<Actividad> recientes = new ListaEnlazada<>();
        while (!pila.estaVacia() && recientes.tamanio() < maximo) {
            recientes.insertarFinal(pila.desapilar());
        }
        return recientes;
    }

    private void apilar(Pila<Actividad> pila, String[] campos) {
        try {
            pila.apilar(new Actividad(LocalDateTime.parse(campos[0]), campos[2]));
        } catch (DateTimeParseException e) {
            return;
        }
    }
}
