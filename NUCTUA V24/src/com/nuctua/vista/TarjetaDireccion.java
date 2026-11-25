package com.nuctua.vista;

import com.nuctua.modelo.Direccion;
import javafx.geometry.Pos;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;

public class TarjetaDireccion extends VBox {

    private final Direccion direccion;
    private final Hyperlink enlaceEditar = new Hyperlink("Editar");

    public TarjetaDireccion(Direccion direccion) {
        super(8);
        this.direccion = direccion;
        getStyleClass().add("tarjeta-direccion");
        setMinWidth(0);
        setMaxWidth(Double.MAX_VALUE);
        Label etiqueta = new Label(direccion.getEtiqueta().toUpperCase());
        etiqueta.getStyleClass().add("pastilla-borde");
        HBox titulo = new HBox(8, etiqueta);
        titulo.setAlignment(Pos.CENTER_LEFT);
        if (direccion.isPrincipal()) {
            getStyleClass().add("tarjeta-direccion-principal");
            etiqueta.getStyleClass().add("pastilla-pedido");
            etiqueta.setStyle("-fx-font-size: 12px;");
            Label principal = new Label("Principal");
            principal.getStyleClass().addAll("texto-naranja", "negrita");
            titulo.getChildren().add(principal);
        }
        Label recibe = texto(direccion.getRecibe(), "texto");
        recibe.getStyleClass().add("negrita");
        enlaceEditar.getStyleClass().add("enlace-texto");
        getChildren().addAll(titulo, recibe, texto(direccion.getLinea(), "texto-suave-grande"),
                texto(direccion.getCiudad(), "texto-suave-grande"), enlaceEditar);
    }

    private Label texto(String valor, String estilo) {
        Label label = new Label(valor);
        label.getStyleClass().add(estilo);
        label.setWrapText(true);
        label.setMinHeight(Region.USE_PREF_SIZE);
        return label;
    }

    public Direccion getDireccion() {
        return direccion;
    }

    public Hyperlink getEnlaceEditar() {
        return enlaceEditar;
    }
}
