package com.nuctua.vista;

import javafx.scene.Node;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Control;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;

public class CampoFormulario extends VBox {

    private final Label etiqueta = new Label();
    private final Label error = new Label();
    private TextField campo = new TextField();
    private ComboBox<String> lista;

    public CampoFormulario() {
        super(8);
        setMinWidth(0);
        etiqueta.getStyleClass().add("etiqueta-campo");
        error.getStyleClass().addAll("mensaje-error", "error-campo");
        error.setWrapText(true);
        error.setMinHeight(Region.USE_PREF_SIZE);
        getChildren().addAll(etiqueta, campo, error);
        prepararTexto();
        limpiarError();
    }

    public void setEtiqueta(String texto) {
        etiqueta.setText(texto);
    }

    public String getEtiqueta() {
        return etiqueta.getText();
    }

    public void setPrompt(String texto) {
        campo.setPromptText(texto);
        if (lista != null) {
            lista.setPromptText(texto);
        }
    }

    public String getPrompt() {
        return campo.getPromptText();
    }

    public void setSecreto(boolean secreto) {
        if (secreto && !(campo instanceof PasswordField)) {
            TextField nuevo = new PasswordField();
            nuevo.setPromptText(campo.getPromptText());
            getChildren().set(1, nuevo);
            campo = nuevo;
            prepararTexto();
        }
    }

    public boolean isSecreto() {
        return campo instanceof PasswordField;
    }

    public void usarLista(String... opciones) {
        lista = new ComboBox<>();
        lista.getItems().addAll(opciones);
        lista.setPromptText(campo.getPromptText());
        lista.setMaxWidth(Double.MAX_VALUE);
        lista.getStyleClass().addAll("campo", "campo-lista", "sin-valor");
        lista.valueProperty().addListener((obs, antes, ahora) -> {
            lista.getStyleClass().remove("sin-valor");
            if (ahora == null) {
                lista.getStyleClass().add("sin-valor");
            }
            limpiarError();
        });
        getChildren().set(1, lista);
    }

    private void prepararTexto() {
        campo.getStyleClass().add("campo");
        campo.setMaxWidth(Double.MAX_VALUE);
        etiqueta.setLabelFor(campo);
        campo.textProperty().addListener((obs, antes, ahora) -> limpiarError());
    }

    public void mostrarError(String mensaje) {
        error.setText(mensaje);
        error.setVisible(true);
        error.setManaged(true);
        Node control = getControl();
        control.getStyleClass().remove("campo-error");
        control.getStyleClass().add("campo-error");
    }

    public final void limpiarError() {
        error.setText("");
        error.setVisible(false);
        error.setManaged(false);
        getControl().getStyleClass().remove("campo-error");
    }

    public String getError() {
        return error.getText();
    }

    public Control getControl() {
        return lista == null ? campo : lista;
    }

    public TextField getCampo() {
        return campo;
    }

    public ComboBox<String> getLista() {
        return lista;
    }

    public String getTexto() {
        if (lista != null) {
            return lista.getValue() == null ? "" : lista.getValue();
        }
        return campo.getText() == null ? "" : campo.getText();
    }

    public void setTexto(String texto) {
        campo.setText(texto == null ? "" : texto);
    }
}
