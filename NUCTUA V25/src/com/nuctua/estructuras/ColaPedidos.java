package com.nuctua.estructuras;

import com.nuctua.modelo.EstadoPedido;
import com.nuctua.modelo.Pedido;

public class ColaPedidos extends Cola<Pedido> {

    public ColaPedidos() {
    }

    public ColaPedidos(Iterable<Pedido> pedidos) {
        for (Pedido pedido : pedidos) {
            encolar(pedido);
        }
    }

    @Override
    public boolean encolar(Pedido pedido) {
        if (pedido == null || pedido.getEstado() != EstadoPedido.NUEVO || contiene(pedido.getNumero())) {
            return false;
        }
        return super.encolar(pedido);
    }

    public boolean contiene(int numero) {
        return posicionDe(numero) > 0;
    }

    public int posicionDe(int numero) {
        return posicionDe(p -> p.getNumero() == numero);
    }

    public boolean quitar(int numero) {
        return quitar(p -> p.getNumero() == numero);
    }

    public boolean esSuTurno(int numero) {
        return frente() != null && frente().getNumero() == numero;
    }

    public int paresPorPreparar() {
        int pares = 0;
        for (Pedido pedido : this) {
            pares += pedido.getTotales().getPares();
        }
        return pares;
    }
}
