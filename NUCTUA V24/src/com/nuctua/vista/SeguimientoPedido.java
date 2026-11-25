package com.nuctua.vista;

import com.nuctua.modelo.EstadoPedido;
import com.nuctua.util.Iconos;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

public class SeguimientoPedido extends HBox {

    private static final EstadoPedido[] PASOS = {EstadoPedido.NUEVO, EstadoPedido.PREPARANDO, EstadoPedido.ENVIADO, EstadoPedido.ENTREGADO};

    public SeguimientoPedido(EstadoPedido estado) {
        super(10);
        setAlignment(Pos.TOP_LEFT);
        if (estado == EstadoPedido.CANCELADO) {
            getChildren().add(crearPaso(EstadoPedido.CANCELADO, 1, false, true));
            return;
        }
        for (int i = 0; i < PASOS.length; i++) {
            boolean hecho = i < estado.ordinal() || estado == EstadoPedido.ENTREGADO;
            if (i > 0) {
                Region linea = new Region();
                linea.getStyleClass().add(i <= estado.ordinal() ? "seguimiento-linea-hecha" : "seguimiento-linea");
                VBox caja = new VBox(linea);
                caja.setAlignment(Pos.CENTER);
                caja.setMinHeight(32);
                caja.setMaxHeight(32);
                HBox.setHgrow(caja, Priority.ALWAYS);
                getChildren().add(caja);
            }
            getChildren().add(crearPaso(PASOS[i], i + 1, hecho, PASOS[i] == estado));
        }
    }

    private VBox crearPaso(EstadoPedido paso, int numero, boolean hecho, boolean actual) {
        StackPane circulo = new StackPane();
        circulo.getStyleClass().add("seguimiento-paso");
        if (hecho) {
            circulo.getStyleClass().add("seguimiento-hecho");
            circulo.getChildren().add(Iconos.check(Iconos.CREMA, 16));
        } else if (paso == EstadoPedido.CANCELADO) {
            circulo.getStyleClass().add("seguimiento-cancelado");
            circulo.getChildren().add(Iconos.cerrar(Iconos.OSCURO, 14));
        } else {
            if (actual) {
                circulo.getStyleClass().add("seguimiento-actual");
            }
            Label cifra = new Label(String.valueOf(numero));
            cifra.getStyleClass().addAll("texto", "negrita");
            circulo.getChildren().add(cifra);
        }
        Label nombre = new Label(paso.getTexto());
        nombre.getStyleClass().add(actual ? "texto" : "texto-suave");
        if (actual) {
            nombre.getStyleClass().add("negrita");
        }
        VBox caja = new VBox(6, circulo, nombre);
        caja.setAlignment(Pos.TOP_CENTER);
        caja.setMinWidth(Region.USE_PREF_SIZE);
        return caja;
    }
}
