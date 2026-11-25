package com.nuctua.servicio;

import com.nuctua.estructuras.ListaEnlazada;
import com.nuctua.estructuras.Pila;
import com.nuctua.modelo.Cliente;
import com.nuctua.modelo.LineaPedido;
import com.nuctua.modelo.Pedido;
import com.nuctua.modelo.SesionActual;
import com.nuctua.modelo.Zapato;

public class HistorialServicio {

    private final PedidosServicio pedidos = new PedidosServicio();
    private final CarritoServicio carrito = new CarritoServicio();
    private final SeleccionServicio seleccion = new SeleccionServicio();
    private final CatalogoServicio catalogo = new CatalogoServicio();

    public Pila<Pedido> compras() {
        Pila<Pedido> pila = new Pila<>();
        Cliente cliente = SesionActual.getInstancia().getCliente();
        if (cliente == null) {
            return pila;
        }
        for (Pedido pedido : pedidos.delCliente(cliente.getCorreo())) {
            pila.apilar(pedido);
        }
        return pila;
    }

    public ListaEnlazada<Zapato> vistos(int maximo) {
        ListaEnlazada<Zapato> vistos = new ListaEnlazada<>();
        for (Zapato visto : SesionActual.getInstancia().getVistosRecientemente().aArrayList()) {
            Zapato vigente = catalogo.buscarPorSku(visto.getSku());
            if (vigente != null && vigente.isActivo() && vistos.tamanio() < maximo) {
                vistos.insertarFinal(vigente);
            }
        }
        return vistos;
    }

    public Zapato producto(LineaPedido linea) {
        return linea == null ? null : catalogo.buscarPorSku(linea.getSku());
    }

    public ResultadoSeleccion disponibilidad(LineaPedido linea) {
        return seleccion.validar(producto(linea), linea.getTalla(), 1);
    }

    public ResultadoSeleccion volverAComprar(LineaPedido linea) {
        Zapato zapato = producto(linea);
        return zapato == null ? ResultadoSeleccion.SIN_PRODUCTO
                : carrito.agregar(zapato, linea.getTalla(), linea.getCantidad());
    }

    public ResultadoSeleccion agregar(Zapato zapato, int talla) {
        return carrito.agregar(zapato, talla, 1);
    }

    public int enCarrito(LineaPedido linea) {
        return carrito.cantidadDe(linea.getSku(), linea.getTalla());
    }

    public String seguimiento(Pedido pedido) {
        String fecha = pedido.getFechaEstadoTexto();
        return switch (pedido.getEstado()) {
            case NUEVO -> pedidos.describirTurno(pedido.getNumero());
            case PREPARANDO -> "Estamos empacando tus pares desde el " + fecha + ".";
            case ENVIADO -> "Salió el " + fecha + " hacia " + pedido.getEnvio().getCiudad() + ".";
            case ENTREGADO -> "Entregado el " + fecha + " en " + pedido.getEnvio().getDireccion() + ".";
            case CANCELADO -> "Cancelado el " + fecha + ".";
        };
    }
}
