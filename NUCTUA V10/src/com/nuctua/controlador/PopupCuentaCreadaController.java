package com.nuctua.controlador;

import com.nuctua.modelo.Usuario;
import com.nuctua.util.Iconos;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;

public class PopupCuentaCreadaController extends PopupBase {

    @FXML
    private StackPane icono;
    @FXML
    private Label mensaje;
    @FXML
    private Button botonIr;

    @FXML
    private void initialize() {
        icono.getChildren().add(Iconos.okGrande(76));
        botonIr.setGraphic(Iconos.flechaDerecha(Iconos.CREMA, 18));
        botonIr.setContentDisplay(ContentDisplay.RIGHT);
    }

    public void setUsuario(Usuario usuario) {
        mensaje.setText("Bienvenido a Nuctua, " + usuario.getPrimerNombre()
                + ". Ya puedes iniciar sesión con " + usuario.getCorreo() + ".");
    }
}
