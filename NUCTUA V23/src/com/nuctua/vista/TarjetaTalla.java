package com.nuctua.vista;

import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;

public class TarjetaTalla extends VBox {

    private static final int POCOS = 2;

    private final int talla;
    private final SelectorCantidad selector = new SelectorCantidad();
    private final Label estado = new Label();

    public TarjetaTalla(int talla) {
        super(12);
        this.talla = talla;
        getStyleClass().add("tarjeta-talla");
        setAlignment(Pos.TOP_CENTER);
        Label numero = new Label(String.valueOf(talla));
        numero.getStyleClass().add("titulo-chico");
        getChildren().addAll(numero, selector, estado);
    }

    public void mostrar(int stock, boolean cambiado, int maximo) {
        selector.mostrar(stock, cambiado);
        selector.getMenos().setDisable(stock <= 0);
        selector.getMas().setDisable(stock >= maximo);
        estado.setText(stock == 0 ? "Agotada" : (stock <= POCOS ? "Quedan pocos" : "Disponible"));
        estado.getStyleClass().setAll("label", stock == 0 ? "estado-agotado" : (stock <= POCOS ? "estado-poco" : "texto-suave"));
    }

    public int getTalla() {
        return talla;
    }

    public SelectorCantidad getSelector() {
        return selector;
    }

    public String getEstado() {
        return estado.getText();
    }
}
