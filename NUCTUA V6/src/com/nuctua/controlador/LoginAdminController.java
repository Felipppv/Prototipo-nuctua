package com.nuctua.controlador;

import com.nuctua.util.CampoContrasena;
import com.nuctua.vista.Ventana;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;

public class LoginAdminController extends AccesoBase {

    @FXML
    private Hyperlink enlaceVolver;
    @FXML
    private TextField campoCorreo;
    @FXML
    private CampoContrasena campoContrasena;
    @FXML
    private Label mensaje;
    @FXML
    private Button botonIngresar;
    @FXML
    private HBox filaSeguridad;

    @FXML
    private void initialize() {
        decorarBoton(botonIngresar);
        decorarSeguridad(filaSeguridad);
    }

    @FXML
    private void irALogin() {
        Navegador.mostrar(Ventana.LOGIN_CLIENTE);
    }
}
