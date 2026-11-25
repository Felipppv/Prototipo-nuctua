package com.nuctua.persistencia;

import com.nuctua.estructuras.ListaEnlazada;
import com.nuctua.modelo.DatosEnvio;
import com.nuctua.modelo.EstadoPedido;
import com.nuctua.modelo.LineaPedido;
import com.nuctua.modelo.MetodoPago;
import com.nuctua.modelo.Pedido;
import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;

public class ArchivoPedidos {

    private static final String NOMBRE = "pedidos.txt";
    private static final String DETALLE = "pedidos_detalle.txt";
    private static final int PRIMER_NUMERO = 1001;
    private static final int CAMPOS = 14;

    public ListaEnlazada<Pedido> cargar() {
        ListaEnlazada<Pedido> pedidos = new ListaEnlazada<>();
        for (String linea : GestorArchivos.leerLineas(NOMBRE)) {
            Pedido pedido = leerPedido(linea.split(GestorArchivos.SEPARADOR_REGEX, -1));
            if (pedido != null && !pedidos.contiene(p -> p.getNumero() == pedido.getNumero())) {
                pedidos.insertarFinal(pedido);
            }
        }
        for (String linea : GestorArchivos.leerLineas(DETALLE)) {
            String[] campos = linea.split(GestorArchivos.SEPARADOR_REGEX, -1);
            if (campos.length < 6 || !esEntero(campos[0]) || !esEntero(campos[3]) || !esEntero(campos[4]) || !esEntero(campos[5])) {
                continue;
            }
            int numero = Integer.parseInt(campos[0]);
            Pedido pedido = pedidos.buscar(p -> p.getNumero() == numero);
            if (pedido != null) {
                pedido.agregarLinea(new LineaPedido(campos[1], campos[2], Integer.parseInt(campos[3]),
                        Integer.parseInt(campos[4]), Integer.parseInt(campos[5])));
            }
        }
        return pedidos;
    }

    public ListaEnlazada<Pedido> cargarDe(String correo) {
        ListaEnlazada<Pedido> propios = new ListaEnlazada<>();
        for (Pedido pedido : cargar()) {
            if (pedido.getCorreoCliente().equalsIgnoreCase(correo)) {
                propios.insertarFinal(pedido);
            }
        }
        return propios;
    }

    public int siguienteNumero() {
        return Math.max(mayorNumero(NOMBRE), mayorNumero(DETALLE)) + 1;
    }

    public void agregar(Pedido pedido) {
        ArrayList<String> detalle = GestorArchivos.leerLineas(DETALLE);
        for (LineaPedido linea : pedido.getLineas()) {
            detalle.add(unir(String.valueOf(pedido.getNumero()), linea.getSku(), linea.getNombre(),
                    String.valueOf(linea.getTalla()), String.valueOf(linea.getCantidad()), String.valueOf(linea.getPrecio())));
        }
        ArrayList<String> cabeceras = GestorArchivos.leerLineas(NOMBRE);
        cabeceras.add(escribirPedido(pedido));
        GestorArchivos.escribirLineas(DETALLE, detalle);
        GestorArchivos.escribirLineas(NOMBRE, cabeceras);
    }

    public boolean actualizar(Pedido pedido) {
        ArrayList<String> cabeceras = GestorArchivos.leerLineas(NOMBRE);
        for (int i = 0; i < cabeceras.size(); i++) {
            Pedido guardado = leerPedido(cabeceras.get(i).split(GestorArchivos.SEPARADOR_REGEX, -1));
            if (guardado != null && guardado.getNumero() == pedido.getNumero()) {
                cabeceras.set(i, escribirPedido(pedido));
                GestorArchivos.escribirLineas(NOMBRE, cabeceras);
                return true;
            }
        }
        return false;
    }

    private String escribirPedido(Pedido pedido) {
        DatosEnvio envio = pedido.getEnvio();
        return unir(String.valueOf(pedido.getNumero()), fecha(pedido.getFecha()),
                pedido.getCorreoCliente(), envio.getNombre(), envio.getTelefono(), envio.getCorreo(), envio.getCiudad(),
                envio.getDireccion(), envio.getIndicaciones(), pedido.getMetodo().name(), pedido.getReferenciaPago(),
                String.valueOf(pedido.getCostoEnvio()), String.valueOf(pedido.getDescuento()), pedido.getCupon(),
                pedido.getEstado().name(), fecha(pedido.getFechaEstado()));
    }

    private String fecha(LocalDateTime momento) {
        return momento.truncatedTo(ChronoUnit.SECONDS).toString();
    }

    private LocalDateTime leerFecha(String texto) {
        try {
            return LocalDateTime.parse(texto);
        } catch (DateTimeParseException e) {
            return null;
        }
    }

    private Pedido leerPedido(String[] campos) {
        if (campos.length < CAMPOS || !esEntero(campos[0]) || !esEntero(campos[11]) || !esEntero(campos[12])) {
            return null;
        }
        MetodoPago metodo = null;
        for (MetodoPago opcion : MetodoPago.values()) {
            if (opcion.name().equals(campos[9])) {
                metodo = opcion;
            }
        }
        if (metodo == null) {
            return null;
        }
        LocalDateTime fecha = leerFecha(campos[1]);
        EstadoPedido estado = campos.length > 14 ? EstadoPedido.de(campos[14]) : EstadoPedido.NUEVO;
        if (fecha == null || estado == null) {
            return null;
        }
        DatosEnvio envio = new DatosEnvio(campos[3], campos[4], campos[5], campos[6], campos[7], campos[8]);
        Pedido pedido = new Pedido(Integer.parseInt(campos[0]), fecha, campos[2], envio, metodo, campos[10],
                Integer.parseInt(campos[11]), Integer.parseInt(campos[12]), campos[13]);
        LocalDateTime cambio = campos.length > 15 ? leerFecha(campos[15]) : null;
        pedido.setEstado(estado, cambio == null ? fecha : cambio);
        return pedido;
    }

    private int mayorNumero(String archivo) {
        int mayor = PRIMER_NUMERO - 1;
        for (String linea : GestorArchivos.leerLineas(archivo)) {
            String numero = linea.split(GestorArchivos.SEPARADOR_REGEX, -1)[0];
            if (esEntero(numero)) {
                mayor = Math.max(mayor, Integer.parseInt(numero));
            }
        }
        return mayor;
    }

    private boolean esEntero(String texto) {
        return texto.matches("[0-9]{1,9}");
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
}
