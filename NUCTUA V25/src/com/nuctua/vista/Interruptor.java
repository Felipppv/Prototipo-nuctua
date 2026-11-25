package com.nuctua.vista;

import com.nuctua.util.Iconos;
import javafx.geometry.Pos;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.ToggleButton;
import javafx.scene.layout.StackPane;
import javafx.scene.shape.Circle;

public class Interruptor extends ToggleButton {

    private final Circle perilla = new Circle(10, Iconos.OSCURO);

    public Interruptor() {
        getStyleClass().setAll("interruptor");
        StackPane caja = new StackPane(perilla);
        caja.setMinSize(40, 20);
        caja.setMaxSize(40, 20);
        caja.setMouseTransparent(true);
        setGraphic(caja);
        setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
        selectedProperty().addListener((obs, antes, ahora) -> pintar());
        pintar();
    }

    private void pintar() {
        perilla.setFill(isSelected() ? Iconos.CREMA : Iconos.OSCURO);
        StackPane.setAlignment(perilla, isSelected() ? Pos.CENTER_RIGHT : Pos.CENTER_LEFT);
    }
}
