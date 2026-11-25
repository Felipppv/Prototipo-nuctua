package com.nuctua.modelo;

import com.nuctua.estructuras.ListaEnlazada;
import java.time.LocalDateTime;

public class Pedido {

    private static final String[] MESES = {"ene", "feb", "mar", "abr", "may", "jun", "jul", "ago", "sep", "oct", "nov", "dic"};

    private final int numero;
    private final LocalDateTime fecha;
    private final String correoCliente;
    private final DatosEnvio envio;
    private final MetodoPago metodo;
    private final String referenciaPago;
    private final int costoEnvio;
    private final int descuento;
    private final String cupon;
    private final ListaEnlazada<LineaPedido> lineas = new ListaEnlazada<>();
    private EstadoPedido estado;
    private LocalDateTime fechaEstado;

    public Pedido(int numero, LocalDateTime fecha, String correoCliente, DatosEnvio envio, MetodoPago metodo,
            String referenciaPago, int costoEnvio, int descuento, String cupon) {
        this.numero = numero;
        this.fecha = fecha;
        this.correoCliente = correoCliente;
        this.envio = envio;
        this.metodo = metodo;
        this.referenciaPago = referenciaPago;
        this.costoEnvio = costoEnvio;
        this.descuento = descuento;
        this.cupon = cupon;
        this.estado = EstadoPedido.NUEVO;
        this.fechaEstado = fecha;
    }

    public void agregarLinea(LineaPedido linea) {
        if (linea != null) {
            lineas.insertarFinal(linea);
        }
    }

    public TotalesCompra getTotales() {
        int pares = 0;
        int subtotal = 0;
        for (LineaPedido linea : lineas) {
            pares += linea.getCantidad();
            subtotal += linea.getSubtotal();
        }
        return new TotalesCompra(pares, subtotal, costoEnvio, descuento);
    }

    public String getCodigo() {
        return "NCT-" + numero;
    }

    public String getFechaTexto() {
        return fecha.getDayOfMonth() + " " + MESES[fecha.getMonthValue() - 1] + " " + fecha.getYear();
    }

    public String getHoraTexto() {
        return String.format("%02d:%02d", fecha.getHour(), fecha.getMinute());
    }

    public String getTextoMetodo() {
        return switch (metodo) {
            case TARJETA -> "Tarjeta terminada en " + referenciaPago;
            case PSE -> "PSE desde " + referenciaPago;
            case NEQUI -> "Nequi terminado en " + referenciaPago;
            case CONTRA_ENTREGA -> "Contra entrega";
        };
    }

    public String getEstadoPago() {
        if (estado == EstadoPedido.CANCELADO) {
            return "Pedido cancelado";
        }
        if (metodo != MetodoPago.CONTRA_ENTREGA) {
            return "Pagado";
        }
        return estado == EstadoPedido.ENTREGADO ? "Pagado al recibir" : "Se paga al recibir";
    }

    public String getTextoPago() {
        String total = Zapato.formatearPrecio(getTotales().getTotal());
        return switch (metodo) {
            case TARJETA -> "Pagaste " + total + " con tarjeta terminada en " + referenciaPago + ".";
            case PSE -> "Pagaste " + total + " por PSE desde " + referenciaPago + ".";
            case NEQUI -> "Pagaste " + total + " con el Nequi terminado en " + referenciaPago + ".";
            case CONTRA_ENTREGA -> "Pagarás " + total + " cuando recibas tu pedido.";
        };
    }

    public int getNumero() {
        return numero;
    }

    public LocalDateTime getFecha() {
        return fecha;
    }

    public String getCorreoCliente() {
        return correoCliente;
    }

    public DatosEnvio getEnvio() {
        return envio;
    }

    public MetodoPago getMetodo() {
        return metodo;
    }

    public String getReferenciaPago() {
        return referenciaPago;
    }

    public int getCostoEnvio() {
        return costoEnvio;
    }

    public int getDescuento() {
        return descuento;
    }

    public String getCupon() {
        return cupon;
    }

    public ListaEnlazada<LineaPedido> getLineas() {
        return lineas;
    }

    public EstadoPedido getEstado() {
        return estado;
    }

    public LocalDateTime getFechaEstado() {
        return fechaEstado;
    }

    public void setEstado(EstadoPedido estado, LocalDateTime momento) {
        if (estado != null && momento != null) {
            this.estado = estado;
            this.fechaEstado = momento;
        }
    }
}
