package com.nuctua.servicio;

import com.nuctua.estructuras.ListaEnlazada;
import com.nuctua.modelo.Cliente;
import com.nuctua.modelo.DatosEnvio;
import com.nuctua.modelo.Direccion;
import com.nuctua.modelo.SesionActual;
import com.nuctua.persistencia.GestorArchivos;
import java.util.ArrayList;

public class DireccionesServicio {

    public static final int MAXIMO = 6;

    private static final String NOMBRE = "direcciones.txt";

    private final ClienteServicio clientes = new ClienteServicio();

    public ListaEnlazada<Direccion> lista() {
        ListaEnlazada<Direccion> lista = new ListaEnlazada<>();
        Cliente cliente = SesionActual.getInstancia().getCliente();
        if (cliente == null) {
            return lista;
        }
        for (String linea : GestorArchivos.leerLineas(NOMBRE)) {
            String[] c = linea.split(GestorArchivos.SEPARADOR_REGEX, -1);
            if (c.length > 6 && c[0].equalsIgnoreCase(cliente.getCorreo())) {
                lista.insertarFinal(new Direccion(c[1], c[2], c[3], c[4], c[5], Boolean.parseBoolean(c[6].trim())));
            }
        }
        if (lista.estaVacia() && !cliente.getDireccion().isBlank()) {
            lista.insertarFinal(new Direccion("Casa", cliente.getNombre(), cliente.getDireccion(),
                    cliente.getIndicaciones(), cliente.getCiudad(), true));
        }
        asegurarPrincipal(lista);
        return lista;
    }

    public boolean guardar(int posicion, Direccion direccion) {
        ListaEnlazada<Direccion> lista = lista();
        boolean nueva = posicion < 0 || posicion >= lista.tamanio();
        if (direccion == null || !direccion.validar().estaVacia() || (nueva && lista.tamanio() >= MAXIMO)) {
            return false;
        }
        if (direccion.isPrincipal()) {
            for (Direccion otra : lista) {
                otra.setPrincipal(false);
            }
        }
        if (nueva) {
            lista.insertarFinal(direccion);
        } else {
            Direccion anterior = lista.obtener(posicion);
            lista.reemplazar(d -> d == anterior, direccion);
        }
        return persistir(lista);
    }

    public boolean eliminar(int posicion) {
        ListaEnlazada<Direccion> lista = lista();
        if (posicion < 0 || posicion >= lista.tamanio()) {
            return false;
        }
        Direccion quitada = lista.obtener(posicion);
        lista.eliminar(d -> d == quitada);
        return persistir(lista);
    }

    public void recordar(DatosEnvio envio) {
        ListaEnlazada<Direccion> lista = lista();
        Direccion principal = lista.buscar(Direccion::isPrincipal);
        Direccion nueva = new Direccion(principal == null ? "Casa" : principal.getEtiqueta(), envio.getNombre(),
                envio.getDireccion(), envio.getIndicaciones(), envio.getCiudad(), true);
        if (principal == null) {
            lista.insertarFinal(nueva);
        } else {
            lista.reemplazar(d -> d == principal, nueva);
        }
        persistir(lista);
    }

    private boolean persistir(ListaEnlazada<Direccion> lista) {
        Cliente cliente = SesionActual.getInstancia().getCliente();
        if (cliente == null) {
            return false;
        }
        asegurarPrincipal(lista);
        ArrayList<String> lineas = new ArrayList<>();
        String s = GestorArchivos.SEPARADOR;
        for (Direccion d : lista) {
            lineas.add(GestorArchivos.limpiar(cliente.getCorreo()) + s + GestorArchivos.limpiar(d.getEtiqueta()) + s
                    + GestorArchivos.limpiar(d.getRecibe()) + s + GestorArchivos.limpiar(d.getDireccion()) + s
                    + GestorArchivos.limpiar(d.getIndicaciones()) + s + GestorArchivos.limpiar(d.getCiudad()) + s + d.isPrincipal());
        }
        try {
            GestorArchivos.reemplazarDe(NOMBRE, cliente.getCorreo(), lineas);
            clientes.guardarDireccion(lista.buscar(Direccion::isPrincipal));
        } catch (IllegalStateException e) {
            return false;
        }
        SesionActual.getInstancia().setBorradorEnvio(null);
        return true;
    }

    private void asegurarPrincipal(ListaEnlazada<Direccion> lista) {
        boolean hay = false;
        for (Direccion direccion : lista) {
            direccion.setPrincipal(direccion.isPrincipal() && !hay);
            hay = hay || direccion.isPrincipal();
        }
        if (!hay && !lista.estaVacia()) {
            lista.obtener(0).setPrincipal(true);
        }
    }
}
