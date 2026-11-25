package com.nuctua.util;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.StackPane;

public class CampoContrasena extends StackPane {

    private final PasswordField oculto = new PasswordField();
    private final TextField visible = new TextField();
    private final Button ojo = new Button();

    public CampoContrasena() {
        getStyleClass().add("campo-contrasena");
        oculto.getStyleClass().add("campo");
        visible.getStyleClass().add("campo");
        visible.setVisible(false);
        visible.setManaged(false);
        visible.textProperty().bindBidirectional(oculto.textProperty());
        ojo.getStyleClass().add("boton-barra");
        ojo.setGraphic(Iconos.ojo(Iconos.OSCURO, 20));
        ojo.setFocusTraversable(false);
        ojo.setOnAction(e -> alternar());
        setAlignment(Pos.CENTER_RIGHT);
        StackPane.setMargin(ojo, new Insets(0, 8, 0, 0));
        getChildren().addAll(oculto, visible, ojo);
    }

    public void alternar() {
        boolean mostrar = oculto.isVisible();
        oculto.setVisible(!mostrar);
        oculto.setManaged(!mostrar);
        visible.setVisible(mostrar);
        visible.setManaged(mostrar);
    }

    public String getTexto() {
        return oculto.getText();
    }

    public void setTexto(String texto) {
        oculto.setText(texto);
    }

    public String getPrompt() {
        return oculto.getPromptText();
    }

    public void setPrompt(String prompt) {
        oculto.setPromptText(prompt);
        visible.setPromptText(prompt);
    }

    public PasswordField getCampo() {
        return oculto;
    }
}
