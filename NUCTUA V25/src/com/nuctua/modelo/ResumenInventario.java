package com.nuctua.modelo;

public class ResumenInventario {

    private int pares;
    private int activos;
    private int pocoStock;
    private int agotados;

    public ResumenInventario(Iterable<Zapato> productos) {
        for (Zapato zapato : productos) {
            pares += zapato.getStockTotal();
            if (zapato.isActivo()) {
                activos++;
            }
            if (zapato.estaAgotado()) {
                agotados++;
            } else if (zapato.tienePocoStock()) {
                pocoStock++;
            }
        }
    }

    public int getPares() {
        return pares;
    }

    public int getActivos() {
        return activos;
    }

    public int getPocoStock() {
        return pocoStock;
    }

    public int getAgotados() {
        return agotados;
    }
}
