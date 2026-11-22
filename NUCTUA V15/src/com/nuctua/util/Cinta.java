package com.nuctua.util;

import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;

public class Cinta extends HBox {

    private static final String[] PALABRAS = {"COMODIDAD", "DISEÑO", "PASO A PASO", "CALLE"};

    public Cinta() {
        getStyleClass().add("cinta");
        setAlignment(Pos.CENTER_LEFT);
        setRotate(-1.2);
        setMinHeight(62);
        setMinWidth(0);
        Rectangle recorte = new Rectangle();
        recorte.widthProperty().bind(widthProperty());
        recorte.heightProperty().bind(heightProperty());
        setClip(recorte);
        for (int i = 0; i < 4; i++) {
            for (String palabra : PALABRAS) {
                Circle punto = new Circle(5);
                punto.getStyleClass().add("cinta-punto");
                Label texto = new Label(palabra);
                texto.getStyleClass().add("cinta-texto");
                texto.setMinWidth(Label.USE_PREF_SIZE);
                getChildren().addAll(punto, texto);
            }
        }
    }
}
