package com.nuctua.util;

import javafx.scene.control.Label;
import javafx.scene.text.Font;
import javafx.scene.text.Text;

public class TituloAjustable extends Label {

    private static final double MARGEN = 6;

    private double tamano = 74;

    public TituloAjustable() {
        setMaxWidth(Double.MAX_VALUE);
        widthProperty().addListener((obs, antes, ahora) -> ajustar());
        textProperty().addListener((obs, antes, ahora) -> ajustar());
        fontProperty().addListener((obs, antes, ahora) -> ajustar());
    }

    public double getTamano() {
        return tamano;
    }

    public void setTamano(double tamano) {
        this.tamano = tamano;
        ajustar();
    }

    private void ajustar() {
        double disponible = getWidth() - getPadding().getLeft() - getPadding().getRight() - MARGEN;
        if (disponible <= 0 || getText() == null || getText().isEmpty()) {
            setStyle("-fx-font-size: " + tamano + "px;");
            return;
        }
        Text medida = new Text(getText());
        medida.setFont(Font.font(getFont().getFamily(), tamano));
        double necesario = medida.getLayoutBounds().getWidth();
        double nuevo = necesario <= disponible ? tamano : Math.floor(tamano * disponible / necesario);
        String estilo = "-fx-font-size: " + nuevo + "px;";
        if (!estilo.equals(getStyle())) {
            setStyle(estilo);
        }
    }
}
