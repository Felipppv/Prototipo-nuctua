package com.nuctua.servicio;

import com.nuctua.modelo.Factura;
import com.nuctua.modelo.Pedido;

public class FacturaServicio extends DocumentosServicio {

    public Factura crear(Pedido pedido) {
        return pedido == null ? null : new Factura(pedido);
    }

    public boolean emitir(Pedido pedido) {
        return guardar(crear(pedido));
    }
}
