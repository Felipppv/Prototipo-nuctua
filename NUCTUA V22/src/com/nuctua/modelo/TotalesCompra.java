package com.nuctua.modelo;

public class TotalesCompra {

    private final int pares;
    private final int subtotal;
    private final int envio;
    private final int descuento;

    public TotalesCompra(int pares, int subtotal, int envio, int descuento) {
        this.pares = Math.max(0, pares);
        this.subtotal = Math.max(0, subtotal);
        this.envio = Math.max(0, envio);
        this.descuento = Math.max(0, Math.min(descuento, this.subtotal));
    }

    public int getPares() {
        return pares;
    }

    public int getSubtotal() {
        return subtotal;
    }

    public int getEnvio() {
        return envio;
    }

    public int getDescuento() {
        return descuento;
    }

    public int getTotal() {
        return subtotal - descuento + envio;
    }

    public String getTextoPares() {
        return pares + (pares == 1 ? " par" : " pares");
    }

    public String getTextoEnvio() {
        if (pares == 0) {
            return Zapato.formatearPrecio(0);
        }
        return envio == 0 ? "Gratis" : Zapato.formatearPrecio(envio);
    }

    public String getTextoDescuento() {
        return "− " + Zapato.formatearPrecio(descuento);
    }
}
