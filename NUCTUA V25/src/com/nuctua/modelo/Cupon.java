package com.nuctua.modelo;

public class Cupon {

    private final String codigo;
    private final TipoCupon tipo;
    private final int valor;
    private final int minimo;
    private final boolean activo;

    public Cupon(String codigo, TipoCupon tipo, int valor, int minimo, boolean activo) {
        this.codigo = codigo;
        this.tipo = tipo;
        this.valor = Math.max(0, valor);
        this.minimo = Math.max(0, minimo);
        this.activo = activo;
    }

    public String getCodigo() {
        return codigo;
    }

    public TipoCupon getTipo() {
        return tipo;
    }

    public int getMinimo() {
        return minimo;
    }

    public boolean isActivo() {
        return activo;
    }

    public boolean alcanza(int subtotal) {
        return subtotal >= minimo;
    }

    public int descuentoSobre(int subtotal) {
        if (subtotal <= 0 || !alcanza(subtotal)) {
            return 0;
        }
        return switch (tipo) {
            case PORCENTAJE -> (int) ((long) subtotal * Math.min(100, valor) / 100);
            case VALOR -> Math.min(valor, subtotal);
            case ENVIO -> 0;
        };
    }

    public boolean cubreEnvio(int subtotal) {
        return tipo == TipoCupon.ENVIO && alcanza(subtotal);
    }

    public String getDescripcion() {
        return switch (tipo) {
            case PORCENTAJE -> valor + "% de descuento";
            case VALOR -> Zapato.formatearPrecio(valor) + " de descuento";
            case ENVIO -> "Envío gratis";
        };
    }
}
