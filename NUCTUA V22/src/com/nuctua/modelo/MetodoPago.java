package com.nuctua.modelo;

public enum MetodoPago {
    TARJETA("Tarjeta", "Crédito o débito"),
    PSE("PSE", "Débito desde tu banco"),
    NEQUI("Nequi", "Paga desde tu celular"),
    CONTRA_ENTREGA("Contra entrega", "Paga cuando recibas");

    private final String titulo;
    private final String descripcion;

    MetodoPago(String titulo, String descripcion) {
        this.titulo = titulo;
        this.descripcion = descripcion;
    }

    public String getTitulo() {
        return titulo;
    }

    public String getDescripcion() {
        return descripcion;
    }
}
