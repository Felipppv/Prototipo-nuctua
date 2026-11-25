package com.nuctua.vista;

import com.nuctua.util.Iconos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;

public class SelectorCantidad extends HBox {

    private final Button menos = new Button();
    private final Button mas = new Button();
    private final Label cifra = new Label();

    public SelectorCantidad() {
        getStyleClass().add("selector-cantidad");
        menos.getStyleClass().add("boton-cantidad");
        menos.setGraphic(Iconos.menos(Iconos.OSCURO, 16));
        menos.setTooltip(new Tooltip("Quitar un par"));
        mas.getStyleClass().addAll("boton-cantidad", "boton-cantidad-mas");
        mas.setGraphic(Iconos.mas(Iconos.CREMA, 16));
        mas.setTooltip(new Tooltip("Agregar un par"));
        cifra.getStyleClass().add("numero-cantidad");
        getChildren().addAll(menos, cifra, mas);
        setMinWidth(Region.USE_PREF_SIZE);
        setMaxWidth(Region.USE_PREF_SIZE);
    }

    public void mostrar(int valor, boolean cambiado) {
        cifra.setText(String.valueOf(valor));
        cifra.getStyleClass().remove("cantidad-cambiada");
        if (cambiado) {
            cifra.getStyleClass().add("cantidad-cambiada");
        }
    }

    public String getCifra() {
        return cifra.getText();
    }

    public Button getMenos() {
        return menos;
    }

    public Button getMas() {
        return mas;
    }
}
