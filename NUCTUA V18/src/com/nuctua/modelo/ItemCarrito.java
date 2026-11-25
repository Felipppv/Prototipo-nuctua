package com.nuctua.modelo;

public class ItemCarrito {

    private final Zapato zapato;
    private final int talla;
    private int cantidad;

    public ItemCarrito(Zapato zapato, int talla, int cantidad) {
        this.zapato = zapato;
        this.talla = talla;
        this.cantidad = Math.max(1, cantidad);
    }

    public Zapato getZapato() {
        return zapato;
    }

    public int getTalla() {
        return talla;
    }

    public int getCantidad() {
        return cantidad;
    }

    public void setCantidad(int cantidad) {
        this.cantidad = Math.max(1, cantidad);
    }

    public void sumar(int unidades) {
        cantidad = Math.max(1, cantidad + unidades);
    }

    public int getSubtotal() {
        return zapato.getPrecio() * cantidad;
    }

    public boolean esMismo(String sku, int otraTalla) {
        return talla == otraTalla && zapato.getSku().equalsIgnoreCase(sku);
    }
}
