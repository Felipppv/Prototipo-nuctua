package com.nuctua.modelo;

public class LineaPedido {

    private final String sku;
    private final String nombre;
    private final int talla;
    private final int cantidad;
    private final int precio;

    public LineaPedido(String sku, String nombre, int talla, int cantidad, int precio) {
        this.sku = sku;
        this.nombre = nombre;
        this.talla = talla;
        this.cantidad = Math.max(1, cantidad);
        this.precio = Math.max(0, precio);
    }

    public static LineaPedido de(ItemCarrito item) {
        Zapato zapato = item.getZapato();
        return new LineaPedido(zapato.getSku(), zapato.getNombre(), item.getTalla(), item.getCantidad(), zapato.getPrecio());
    }

    public String getSku() {
        return sku;
    }

    public String getNombre() {
        return nombre;
    }

    public int getTalla() {
        return talla;
    }

    public int getCantidad() {
        return cantidad;
    }

    public int getPrecio() {
        return precio;
    }

    public int getSubtotal() {
        return precio * cantidad;
    }
}
