package com.nuctua.servicio;

import java.util.ArrayList;

public class FiltroCatalogo {

    public static final String NIKE = "NIKE";
    public static final String JORDAN = "JORDAN";
    public static final String YEEZY = "YEEZY";
    public static final String BALENCIAGA = "BALENCIAGA";
    public static final String OFF_WHITE = "OFF-WHITE";
    public static final String YEEZY_OFF_WHITE = "YEEZY_OFF_WHITE";

    private String texto = "";
    private String grupoChip;
    private final ArrayList<String> gruposMarcados = new ArrayList<>();
    private int talla;
    private String color;
    private int precioMaximo = Integer.MAX_VALUE;

    public void limpiar() {
        texto = "";
        grupoChip = null;
        gruposMarcados.clear();
        talla = 0;
        color = null;
        precioMaximo = Integer.MAX_VALUE;
    }

    public String getTexto() {
        return texto;
    }

    public void setTexto(String texto) {
        this.texto = texto == null ? "" : texto.trim();
    }

    public String getGrupoChip() {
        return grupoChip;
    }

    public void setGrupoChip(String grupoChip) {
        this.grupoChip = grupoChip;
    }

    public ArrayList<String> getGruposMarcados() {
        return gruposMarcados;
    }

    public void marcarGrupo(String grupo, boolean marcado) {
        gruposMarcados.remove(grupo);
        if (marcado) {
            gruposMarcados.add(grupo);
        }
    }

    public int getTalla() {
        return talla;
    }

    public void setTalla(int talla) {
        this.talla = talla;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public int getPrecioMaximo() {
        return precioMaximo;
    }

    public void setPrecioMaximo(int precioMaximo) {
        this.precioMaximo = precioMaximo;
    }
}
