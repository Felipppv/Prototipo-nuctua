package com.nuctua.servicio;

public enum CriterioOrden {
    PRECIO_MENOR("Precio: menor a mayor", "Burbuja"),
    PRECIO_MAYOR("Precio: mayor a menor", "Intercambio"),
    NOMBRE_AZ("Nombre: A – Z", "Selección"),
    NOMBRE_ZA("Nombre: Z – A", "Selección"),
    MAS_NUEVOS("Más nuevos primero", "Burbuja");

    private final String etiqueta;
    private final String metodo;

    CriterioOrden(String etiqueta, String metodo) {
        this.etiqueta = etiqueta;
        this.metodo = metodo;
    }

    public String getEtiqueta() {
        return etiqueta;
    }

    public String getMetodo() {
        return metodo;
    }
}
