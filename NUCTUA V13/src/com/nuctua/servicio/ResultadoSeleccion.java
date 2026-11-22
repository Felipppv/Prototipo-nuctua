package com.nuctua.servicio;

public enum ResultadoSeleccion {
    VALIDA(""),
    SIN_PRODUCTO("No encontramos este par en el catálogo."),
    PRODUCTO_INACTIVO("Este par ya no está disponible en el catálogo."),
    PRODUCTO_AGOTADO("Este par está agotado en todas las tallas."),
    SIN_TALLA("Elige una talla antes de continuar."),
    TALLA_INEXISTENTE("Esa talla no existe para este modelo."),
    TALLA_AGOTADA("Esa talla está agotada. Elige otra."),
    CANTIDAD_INVALIDA("La cantidad debe ser de al menos 1 par."),
    STOCK_INSUFICIENTE("No hay suficientes pares en esa talla.");

    private final String mensaje;

    ResultadoSeleccion(String mensaje) {
        this.mensaje = mensaje;
    }

    public String getMensaje() {
        return mensaje;
    }

    public boolean esValida() {
        return this == VALIDA;
    }
}
