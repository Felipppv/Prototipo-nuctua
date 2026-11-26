package com.nuctua.servicio;

public enum ResultadoCupon {
    APLICADO("Código aplicado."),
    VACIO("Escribe un código de descuento."),
    INEXISTENTE("Ese código no existe. Revísalo e inténtalo de nuevo."),
    INACTIVO("Ese código ya no está activo."),
    MINIMO_NO_ALCANZADO("Tu compra no llega al mínimo de ese código."),
    ENVIO_YA_GRATIS("Tu envío ya es gratis, guarda este código para otra compra."),
    YA_USADO("Ya usaste este código en una compra anterior. Cada código sirve una vez por cliente."),
    CARRITO_VACIO("Agrega un par al carrito antes de usar un código.");

    private final String mensaje;

    ResultadoCupon(String mensaje) {
        this.mensaje = mensaje;
    }

    public String getMensaje() {
        return mensaje;
    }

    public boolean esValido() {
        return this == APLICADO;
    }
}
