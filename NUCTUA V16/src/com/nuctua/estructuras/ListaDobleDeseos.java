package com.nuctua.estructuras;

import com.nuctua.modelo.Zapato;
import java.util.ArrayList;

public class ListaDobleDeseos extends ListaDoble<Zapato> {

    private final String dueno;

    public ListaDobleDeseos(String dueno) {
        this.dueno = dueno == null ? "" : dueno;
    }

    public String getDueno() {
        return dueno;
    }

    public boolean contiene(String sku) {
        return sku != null && contiene(z -> z.getSku().equalsIgnoreCase(sku));
    }

    public boolean agregar(Zapato zapato) {
        if (zapato == null || contiene(zapato.getSku())) {
            return false;
        }
        insertarInicio(zapato);
        return true;
    }

    public void agregarAlFinal(Zapato zapato) {
        if (zapato != null && !contiene(zapato.getSku())) {
            insertarFinal(zapato);
        }
    }

    public int posicionDe(String sku) {
        return sku == null ? -1 : indiceDe(z -> z.getSku().equalsIgnoreCase(sku));
    }

    public boolean agregarEn(int indice, Zapato zapato) {
        if (zapato == null || contiene(zapato.getSku())) {
            return false;
        }
        insertarEn(indice, zapato);
        return true;
    }

    public boolean quitar(String sku) {
        return sku != null && eliminar(z -> z.getSku().equalsIgnoreCase(sku)) != null;
    }

    public boolean alternar(Zapato zapato) {
        if (zapato == null) {
            return false;
        }
        if (quitar(zapato.getSku())) {
            return false;
        }
        insertarInicio(zapato);
        return true;
    }

    public ArrayList<Zapato> masRecientes() {
        return aArrayList();
    }

    public ArrayList<Zapato> masAntiguos() {
        return aArrayListInverso();
    }

    public ArrayList<String> skus() {
        ArrayList<String> skus = new ArrayList<>();
        for (Zapato zapato : this) {
            skus.add(zapato.getSku());
        }
        return skus;
    }
}
