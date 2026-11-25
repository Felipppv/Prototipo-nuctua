package com.nuctua.modelo;

import com.nuctua.estructuras.ListaEnlazada;

public class CambioStock {

    private final String sku;
    private final int talla;
    private int diferencia;

    public CambioStock(String sku, int talla, int diferencia) {
        this.sku = sku;
        this.talla = talla;
        this.diferencia = diferencia;
    }

    public String getSku() {
        return sku;
    }

    public int getTalla() {
        return talla;
    }

    public int getDiferencia() {
        return diferencia;
    }

    public void sumar(int cantidad) {
        diferencia += cantidad;
    }

    public boolean esDe(String otroSku, int otraTalla) {
        return talla == otraTalla && sku.equalsIgnoreCase(otroSku);
    }

    public static void anotar(ListaEnlazada<CambioStock> cambios, String sku, int talla, int cantidad) {
        CambioStock cambio = cambios.buscar(c -> c.esDe(sku, talla));
        if (cambio == null) {
            cambios.insertarFinal(new CambioStock(sku, talla, cantidad));
            return;
        }
        cambio.sumar(cantidad);
        if (cambio.diferencia == 0) {
            cambios.eliminar(c -> c == cambio);
        }
    }

    public static void olvidar(ListaEnlazada<CambioStock> cambios, String sku) {
        boolean quedan = true;
        while (quedan) {
            quedan = cambios.eliminar(c -> c.sku.equalsIgnoreCase(sku));
        }
    }
}
