package com.nuctua.modelo;

public class TallaStock {

    private final int talla;
    private int stock;

    public TallaStock(int talla, int stock) {
        this.talla = talla;
        this.stock = Math.max(0, stock);
    }

    public int getTalla() {
        return talla;
    }

    public int getStock() {
        return stock;
    }

    public void setStock(int stock) {
        this.stock = Math.max(0, stock);
    }

    public boolean descontar(int cantidad) {
        if (cantidad <= 0 || cantidad > stock) {
            return false;
        }
        stock -= cantidad;
        return true;
    }
}
