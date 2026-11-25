package com.nuctua.vista;

import com.nuctua.util.FormatoCampo;
import com.nuctua.util.Iconos;
import com.nuctua.util.Validaciones;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

public class ContadorTalla extends VBox {

    private static final int MAXIMO = 999;

    private final int talla;
    private final TextField campo = new TextField("0");
    private final Button menos = new Button();
    private final Button mas = new Button();

    public ContadorTalla(int talla) {
        super(4);
        this.talla = talla;
        getStyleClass().add("contador-talla");
        setAlignment(Pos.CENTER);
        Label titulo = new Label(String.valueOf(talla));
        titulo.getStyleClass().addAll("etiqueta-campo", "texto-suave-grande");
        campo.getStyleClass().add("cifra-contador");
        campo.setAccessibleText("Pares de la talla " + talla);
        FormatoCampo.soloDigitos(campo, 3);
        menos.getStyleClass().add("boton-contador");
        menos.setGraphic(Iconos.menos(Iconos.OSCURO, 12));
        mas.getStyleClass().add("boton-contador");
        mas.setGraphic(Iconos.mas(Iconos.OSCURO, 12));
        menos.setOnAction(e -> setValor(getValor() - 1));
        mas.setOnAction(e -> setValor(getValor() + 1));
        campo.textProperty().addListener((obs, antes, ahora) -> {
            menos.setDisable(getValor() == 0);
            mas.setDisable(getValor() >= MAXIMO);
        });
        campo.focusedProperty().addListener((obs, antes, ahora) -> {
            if (!ahora) {
                setValor(getValor());
            }
        });
        menos.setDisable(true);
        HBox botones = new HBox(8, menos, mas);
        botones.setAlignment(Pos.CENTER);
        getChildren().addAll(titulo, campo, botones);
    }

    public int getTalla() {
        return talla;
    }

    public int getValor() {
        String digitos = Validaciones.digitos(campo.getText());
        return digitos.isEmpty() ? 0 : Integer.parseInt(digitos);
    }

    public void setValor(int valor) {
        campo.setText(String.valueOf(Math.max(0, Math.min(MAXIMO, valor))));
    }

    public TextField getCampo() {
        return campo;
    }

    public Button getMenos() {
        return menos;
    }

    public Button getMas() {
        return mas;
    }
}
