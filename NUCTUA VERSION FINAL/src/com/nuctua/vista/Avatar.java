package com.nuctua.vista;

import com.nuctua.modelo.Usuario;
import com.nuctua.util.Iconos;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;

public class Avatar extends StackPane {

    private static final Color[] FONDOS = {Iconos.NARANJA, Iconos.AMARILLO, Color.web("#F0E2C8"), Color.web("#FFE3CC")};

    public Avatar(Usuario usuario, double lado, int tono) {
        Circle fondo = new Circle(lado / 2 - 1.5, FONDOS[Math.floorMod(tono, FONDOS.length)]);
        fondo.setStroke(Iconos.OSCURO);
        fondo.setStrokeWidth(lado > 60 ? 3 : 2);
        Label iniciales = new Label(usuario.getIniciales());
        iniciales.getStyleClass().add("iniciales");
        iniciales.setStyle("-fx-font-size: " + Math.round(lado * 0.42) + "px;");
        getChildren().addAll(fondo, iniciales);
        setMinSize(lado, lado);
        setMaxSize(lado, lado);
    }
}
