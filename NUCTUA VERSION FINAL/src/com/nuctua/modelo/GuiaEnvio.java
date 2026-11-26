package com.nuctua.modelo;

import java.util.ArrayList;

public class GuiaEnvio extends Documento {

    public GuiaEnvio(Pedido pedido) {
        super(pedido);
    }

    @Override
    public String getNumero() {
        return "G-" + getPedido().getNumero();
    }

    @Override
    public String getCarpeta() {
        return "guias";
    }

    @Override
    public String getNombreArchivo() {
        return "guia_" + getPedido().getCodigo() + ".txt";
    }

    @Override
    public ArrayList<String> aTexto() {
        Pedido pedido = getPedido();
        DatosEnvio envio = pedido.getEnvio();
        TotalesCompra totales = pedido.getTotales();
        ArrayList<String> texto = new ArrayList<>();
        texto.add(DOBLE);
        texto.add(columnas("NUCTUA", "GUÍA DE ENVÍO " + getNumero()));
        texto.add(DOBLE);
        texto.add(columnas("Pedido: #" + pedido.getCodigo(), pedido.getFechaTexto()));
        texto.add("");
        texto.add("ENTREGAR A");
        agregar(texto, envio.getNombre());
        agregar(texto, "Tel. " + envio.getTelefono());
        agregar(texto, envio.getDireccion());
        agregar(texto, envio.getIndicaciones());
        agregar(texto, envio.getCiudad());
        texto.add("");
        texto.add(SIMPLE);
        texto.add(columnas("CONTENIDO", "PARES"));
        texto.add(SIMPLE);
        for (LineaPedido linea : pedido.getLineas()) {
            texto.add(columnas(linea.getNombre() + " · Talla " + linea.getTalla(), String.valueOf(linea.getCantidad())));
        }
        texto.add(SIMPLE);
        texto.add(columnas("TOTAL DE PARES", String.valueOf(totales.getPares())));
        texto.add(columnas("COBRAR AL ENTREGAR", Zapato.formatearPrecio(getCobro())));
        texto.add(DOBLE);
        texto.add("Recibido por: ______________________  Fecha: ______________");
        return texto;
    }

    public int getCobro() {
        boolean cobra = getPedido().getMetodo() == MetodoPago.CONTRA_ENTREGA && getPedido().getEstado() != EstadoPedido.ENTREGADO;
        return cobra ? getPedido().getTotales().getTotal() : 0;
    }
}
