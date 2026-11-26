package com.nuctua.modelo;

import java.util.ArrayList;

public abstract class Documento {

    protected static final int ANCHO = 64;
    protected static final String DOBLE = "=".repeat(ANCHO);
    protected static final String SIMPLE = "-".repeat(ANCHO);

    private final Pedido pedido;

    protected Documento(Pedido pedido) {
        this.pedido = pedido;
    }

    public abstract String getNumero();

    public abstract String getCarpeta();

    public abstract String getNombreArchivo();

    public abstract ArrayList<String> aTexto();

    public Pedido getPedido() {
        return pedido;
    }

    protected void agregar(ArrayList<String> texto, String valor) {
        if (!valor.isEmpty()) {
            texto.add("  " + valor);
        }
    }

    protected String columnas(String izquierda, String derecha) {
        int espacio = ANCHO - derecha.length() - 1;
        String recortada = izquierda.length() <= espacio ? izquierda : izquierda.substring(0, espacio - 1) + "…";
        return recortada + " ".repeat(ANCHO - recortada.length() - derecha.length()) + derecha;
    }
}
