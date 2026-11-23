package com.nuctua.util;

import javafx.scene.Group;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Ellipse;
import javafx.scene.shape.Line;
import javafx.scene.shape.StrokeLineCap;

public class Huellas extends Pane {

    private static final int PASOS_POR_PIE = 4;

    public Huellas() {
        setMinHeight(200);
        setPrefHeight(240);
        setMouseTransparent(true);
        widthProperty().addListener((obs, a, b) -> dibujar());
        heightProperty().addListener((obs, a, b) -> dibujar());
    }

    private void dibujar() {
        getChildren().clear();
        double ancho = getWidth();
        double alto = getHeight();
        if (ancho <= 0 || alto <= 0) {
            return;
        }
        int total = PASOS_POR_PIE * 2;
        for (int i = 0; i < total; i++) {
            int paso = i / 2;
            boolean derecho = i % 2 == 1;
            double x = 0.02 + paso * 0.21 + (derecho ? 0.12 : 0);
            double y = 0.80 - paso * 0.24 + (derecho ? 0.10 : 0);
            Group huella = huella(0.35 + 0.65 * i / (total - 1));
            huella.setLayoutX(x * ancho * 0.78 + 30);
            huella.setLayoutY(y * alto);
            huella.setRotate(-24);
            getChildren().add(huella);
        }
    }

    private Group huella(double opacidad) {
        Color oscuro = Color.web("#1B1410");
        Ellipse planta = new Ellipse(22, 0, 23, 15);
        planta.setFill(oscuro);
        Ellipse talon = new Ellipse(-16, 0, 13, 11);
        talon.setFill(oscuro);
        Group grupo = new Group(talon, planta);
        for (int i = 0; i < 4; i++) {
            Line raya = new Line(10 + i * 7.5, -10, 15 + i * 7.5, 10);
            raya.setStroke(Color.web("#FF6A2B"));
            raya.setStrokeWidth(2.8);
            raya.setStrokeLineCap(StrokeLineCap.ROUND);
            grupo.getChildren().add(raya);
        }
        grupo.setOpacity(opacidad);
        return grupo;
    }
}
