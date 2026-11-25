package com.nuctua.vista;

import com.nuctua.modelo.EstadoPedido;
import javafx.scene.control.Label;

public class PastillaEstado extends Label {

    public PastillaEstado(EstadoPedido estado) {
        getStyleClass().add("estado-pedido");
        setEstado(estado);
    }

    public final void setEstado(EstadoPedido estado) {
        for (EstadoPedido otro : EstadoPedido.values()) {
            getStyleClass().remove(estilo(otro));
        }
        setText(estado.getTexto().toUpperCase());
        getStyleClass().add(estilo(estado));
    }

    private String estilo(EstadoPedido estado) {
        return "estado-pedido-" + estado.name().toLowerCase();
    }
}
