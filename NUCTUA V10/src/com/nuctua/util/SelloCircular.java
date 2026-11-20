package com.nuctua.util;

import javafx.scene.Group;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;

public class SelloCircular extends StackPane {

    private static final String LEYENDA = "PASO A PASO · NUCTUA · CALLE · ";
    private static final double RADIO = 50;

    public SelloCircular() {
        setMinSize(RADIO * 2 + 8, RADIO * 2 + 8);
        setMaxSize(RADIO * 2 + 8, RADIO * 2 + 8);
        Circle fondo = new Circle(RADIO);
        fondo.setFill(Color.web("#FFFBF3"));
        fondo.setStroke(Color.web("#1B1410"));
        fondo.setStrokeWidth(2.5);
        Group letras = new Group();
        double paso = 360.0 / LEYENDA.length();
        for (int i = 0; i < LEYENDA.length(); i++) {
            Text letra = new Text(String.valueOf(LEYENDA.charAt(i)));
            letra.setFont(Font.font("Space Grotesk", FontWeight.BOLD, 10.5));
            letra.setFill(Color.web("#1B1410"));
            double angulo = Math.toRadians(i * paso - 90);
            double r = RADIO - 12;
            letra.setX(Math.cos(angulo) * r - 3);
            letra.setY(Math.sin(angulo) * r + 4);
            letra.setRotate(i * paso);
            letras.getChildren().add(letra);
        }
        Circle centro = new Circle(17, Color.web("#1B1410"));
        StackPane flecha = new StackPane(centro, Iconos.flechaDerecha(Color.web("#FFF4E4"), 18));
        Group capa = new Group(letras);
        getChildren().addAll(fondo, capa, flecha);
        setMouseTransparent(true);
    }
}
