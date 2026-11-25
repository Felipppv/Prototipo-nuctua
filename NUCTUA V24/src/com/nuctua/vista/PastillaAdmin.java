package com.nuctua.vista;

import com.nuctua.modelo.EstadoCliente;
import com.nuctua.modelo.EstadoPedido;
import com.nuctua.modelo.Zapato;
import javafx.scene.control.Label;
import javafx.scene.layout.Region;

public class PastillaAdmin extends Label {

    private PastillaAdmin(String texto, String tono) {
        super(texto.toUpperCase());
        getStyleClass().addAll("pastilla-tabla", "pastilla-tabla-" + tono);
        setMinWidth(Region.USE_PREF_SIZE);
    }

    public static PastillaAdmin de(Zapato zapato, int stock) {
        return zapato.isActivo() ? porStock(stock) : new PastillaAdmin("Oculto", "beige");
    }

    public static PastillaAdmin porStock(int stock) {
        if (stock == 0) {
            return new PastillaAdmin("Agotado", "naranja");
        }
        return stock <= 5 ? new PastillaAdmin("Poco stock", "amarilla") : new PastillaAdmin("En stock", "oscura");
    }

    public static PastillaAdmin activo(Zapato zapato) {
        if (!zapato.isActivo()) {
            return new PastillaAdmin("Oculto", "beige");
        }
        return zapato.estaAgotado() ? new PastillaAdmin("Agotado", "roja") : new PastillaAdmin("Activo", "oscura");
    }

    public static PastillaAdmin de(EstadoPedido estado) {
        String tono = switch (estado) {
            case NUEVO -> "naranja";
            case PREPARANDO -> "amarilla";
            case ENVIADO -> "oscura";
            case ENTREGADO -> "clara";
            case CANCELADO -> "beige";
        };
        return new PastillaAdmin(estado.getTexto(), tono);
    }

    public static PastillaAdmin de(EstadoCliente estado) {
        String tono = switch (estado) {
            case NUEVO -> "naranja";
            case RECURRENTE -> "oscura";
            case INACTIVO -> "beige";
        };
        return new PastillaAdmin(estado.getTexto(), tono);
    }
}
