package com.nuctua.vista;

import com.nuctua.modelo.MetodoPago;
import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.layout.VBox;

public class OpcionPago extends RadioButton {

    private final MetodoPago metodo;

    public OpcionPago(MetodoPago metodo) {
        this.metodo = metodo;
        Label titulo = new Label(metodo.getTitulo());
        titulo.getStyleClass().add("opcion-pago-titulo");
        Label descripcion = new Label(metodo.getDescripcion());
        descripcion.getStyleClass().add("texto-suave");
        VBox textos = new VBox(2, titulo, descripcion);
        textos.setPadding(new Insets(0, 0, 0, 14));
        textos.setMouseTransparent(true);
        setGraphic(textos);
        setAccessibleText(metodo.getTitulo() + ", " + metodo.getDescripcion());
        getStyleClass().add("opcion-pago-radio");
        setMaxWidth(Double.MAX_VALUE);
    }

    public MetodoPago getMetodo() {
        return metodo;
    }
}
