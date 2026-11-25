package com.nuctua.servicio;

public class EnvioServicio {

    public static final int COSTO = 12000;
    public static final int GRATIS_DESDE = 150000;

    public int costo(int subtotal) {
        return subtotal <= 0 || subtotal >= GRATIS_DESDE ? 0 : COSTO;
    }

    public int faltaParaGratis(int subtotal) {
        return subtotal <= 0 ? 0 : Math.max(0, GRATIS_DESDE - subtotal);
    }
}
