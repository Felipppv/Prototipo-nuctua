package com.nuctua.controlador;

import com.nuctua.util.Iconos;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;

public class PopupDatosIncorrectosController extends PopupBase {

    @FXML
    private StackPane icono;
    @FXML
    private Label titulo;
    @FXML
    private Label mensaje;

    @FXML
    private void initialize() {
        icono.getChildren().add(Iconos.alertaGrande(76));
    }

    public void setContenido(String textoTitulo, String textoMensaje) {
        titulo.setText(textoTitulo);
        mensaje.setText(textoMensaje);
    }
}
