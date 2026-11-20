package com.nuctua.servicio;

public enum CriterioOrden {
    PRECIO_MENOR("Precio: menor a mayor", "Precio menor a mayor", "Burbuja"),
    PRECIO_MAYOR("Precio: mayor a menor", "Precio mayor a menor", "Intercambio"),
    NOMBRE_AZ("Nombre: A – Z", "Nombre A – Z", "Selección"),
    NOMBRE_ZA("Nombre: Z – A", "Nombre Z – A", "Selección"),
    MAS_NUEVOS("Más nuevos primero", "Más nuevos", "Burbuja");

    private final String etiqueta;
    private final String etiquetaCorta;
    private final String metodo;

    CriterioOrden(String etiqueta, String etiquetaCorta, String metodo) {
        this.etiqueta = etiqueta;
        this.etiquetaCorta = etiquetaCorta;
        this.metodo = metodo;
    }

    public String getEtiqueta() {
        return etiqueta;
    }

    public String getEtiquetaCorta() {
        return etiquetaCorta;
    }

    public String getMetodo() {
        return metodo;
    }
}
