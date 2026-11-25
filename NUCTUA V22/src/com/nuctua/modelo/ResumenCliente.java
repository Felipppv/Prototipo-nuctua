package com.nuctua.modelo;

import com.nuctua.estructuras.ListaEnlazada;
import java.time.LocalDate;

public class ResumenCliente {

    private static final int DIAS_INACTIVO = 60;

    private final Cliente cliente;
    private final ListaEnlazada<Pedido> pedidos;
    private final int deseos;
    private final EstadoCliente estado;
    private int compras;
    private int pares;
    private int gastado;
    private LocalDate ultimaCompra;

    public ResumenCliente(Cliente cliente, ListaEnlazada<Pedido> pedidos, int deseos, LocalDate hoy) {
        this.cliente = cliente;
        this.pedidos = pedidos;
        this.deseos = deseos;
        for (Pedido pedido : pedidos) {
            if (pedido.getEstado() == EstadoPedido.CANCELADO) {
                continue;
            }
            compras++;
            pares += pedido.getTotales().getPares();
            gastado += pedido.getTotales().getTotal();
            LocalDate fecha = pedido.getFecha().toLocalDate();
            if (ultimaCompra == null || fecha.isAfter(ultimaCompra)) {
                ultimaCompra = fecha;
            }
        }
        this.estado = calcularEstado(hoy);
    }

    private EstadoCliente calcularEstado(LocalDate hoy) {
        LocalDate referencia = ultimaCompra == null ? cliente.getRegistro() : ultimaCompra;
        if (referencia != null && referencia.isBefore(hoy.minusDays(DIAS_INACTIVO))) {
            return EstadoCliente.INACTIVO;
        }
        return compras > 1 ? EstadoCliente.RECURRENTE : EstadoCliente.NUEVO;
    }

    public Cliente getCliente() {
        return cliente;
    }

    public ListaEnlazada<Pedido> getPedidos() {
        return pedidos;
    }

    public int getDeseos() {
        return deseos;
    }

    public EstadoCliente getEstado() {
        return estado;
    }

    public int getCompras() {
        return compras;
    }

    public int getPares() {
        return pares;
    }

    public int getGastado() {
        return gastado;
    }

    public LocalDate getUltimaCompra() {
        return ultimaCompra;
    }
}
