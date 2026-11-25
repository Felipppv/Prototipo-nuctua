package com.nuctua.vista;

import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;

public class TarjetaMetrica extends VBox {

    private final Label valor = new Label();
    private final Label detalle = new Label();

    public TarjetaMetrica(String titulo, boolean destacada) {
        getStyleClass().addAll("tarjeta", "metrica");
        if (destacada) {
            getStyleClass().add("metrica-destacada");
        }
        Label etiqueta = new Label(titulo);
        etiqueta.getStyleClass().addAll("etiqueta-campo", "texto-suave-grande");
        valor.getStyleClass().add("metrica-valor");
        detalle.getStyleClass().add("texto-suave");
        detalle.setWrapText(true);
        detalle.setMinHeight(Region.USE_PREF_SIZE);
        getChildren().addAll(etiqueta, valor, detalle);
        setMinWidth(0);
        setPrefWidth(300);
        setMaxWidth(Double.MAX_VALUE);
        HBox.setHgrow(this, Priority.ALWAYS);
    }

    public void mostrar(String cifra, String texto) {
        valor.setText(cifra);
        detalle.setText(texto);
    }

    public void mostrar(int cifra, String texto) {
        mostrar(String.valueOf(cifra), texto);
    }

    public String getValor() {
        return valor.getText();
    }
}
