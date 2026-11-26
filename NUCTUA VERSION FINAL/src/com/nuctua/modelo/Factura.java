package com.nuctua.modelo;

import java.util.ArrayList;

public class Factura extends Documento {

    public static final int PORCENTAJE_IVA = 19;

    public Factura(Pedido pedido) {
        super(pedido);
    }

    @Override
    public String getNumero() {
        return "F-" + getPedido().getNumero();
    }

    @Override
    public String getCarpeta() {
        return "facturas";
    }

    @Override
    public String getNombreArchivo() {
        return "factura_" + getPedido().getCodigo() + ".txt";
    }

    public int getValorProductos() {
        TotalesCompra totales = getPedido().getTotales();
        return totales.getSubtotal() - totales.getDescuento();
    }

    public int getBaseGravable() {
        return (int) Math.round(getValorProductos() * 100.0 / (100 + PORCENTAJE_IVA));
    }

    public int getIva() {
        return getValorProductos() - getBaseGravable();
    }

    @Override
    public ArrayList<String> aTexto() {
        Pedido pedido = getPedido();
        ArrayList<String> texto = new ArrayList<>();
        TotalesCompra totales = pedido.getTotales();
        DatosEnvio envio = pedido.getEnvio();
        texto.add(DOBLE);
        texto.add(columnas("NUCTUA", "FACTURA DE COMPRA " + getNumero()));
        texto.add(DOBLE);
        texto.add(columnas("Pedido: #" + pedido.getCodigo(), pedido.getFechaTexto() + ", " + pedido.getHoraTexto()));
        texto.add("");
        texto.add("CLIENTE");
        agregar(texto, envio.getNombre());
        agregar(texto, envio.getCorreo());
        agregar(texto, "Tel. " + envio.getTelefono());
        texto.add("");
        texto.add("ENVÍO");
        agregar(texto, envio.getDireccion() + ", " + envio.getCiudad());
        agregar(texto, envio.getIndicaciones());
        texto.add("");
        texto.add("PAGO");
        agregar(texto, pedido.getTextoMetodo() + " · " + pedido.getEstadoPago());
        texto.add("");
        texto.add(SIMPLE);
        texto.add(columnas("PRODUCTO", "VALOR"));
        texto.add(SIMPLE);
        for (LineaPedido linea : pedido.getLineas()) {
            texto.add(columnas(linea.getNombre(), Zapato.formatearPrecio(linea.getSubtotal())));
            texto.add("  " + linea.getSku() + " · Talla " + linea.getTalla() + " · " + linea.getCantidad() + " x "
                    + Zapato.formatearPrecio(linea.getPrecio()));
        }
        texto.add(SIMPLE);
        texto.add(columnas("Subtotal (" + totales.getTextoPares() + ")", Zapato.formatearPrecio(totales.getSubtotal())));
        texto.add(columnas(getEtiquetaDescuento(), totales.getTextoDescuento()));
        texto.add(columnas("Base gravable", Zapato.formatearPrecio(getBaseGravable())));
        texto.add(columnas("IVA incluido (" + PORCENTAJE_IVA + "%)", Zapato.formatearPrecio(getIva())));
        texto.add(columnas("Envío", totales.getTextoEnvio()));
        texto.add(DOBLE);
        texto.add(columnas("TOTAL", Zapato.formatearPrecio(totales.getTotal())));
        texto.add(DOBLE);
        texto.add("Comprobante académico. No es una factura electrónica.");
        texto.add("Gracias por comprar en Nuctua.");
        return texto;
    }

    public String getEtiquetaDescuento() {
        return getPedido().getCupon().isEmpty() ? "Descuento" : "Descuento (" + getPedido().getCupon() + ")";
    }
}
