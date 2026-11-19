package com.nuctua.controlador;

import com.nuctua.util.CampoContrasena;
import com.nuctua.vista.Ventana;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

public class LoginController extends AccesoBase {

    @FXML
    private VBox panel;
    @FXML
    private Hyperlink enlaceAdmin;
    @FXML
    private Hyperlink enlaceOlvido;
    @FXML
    private TextField campoCorreo;
    @FXML
    private CampoContrasena campoContrasena;
    @FXML
    private CheckBox checkRecordar;
    @FXML
    private Label mensaje;
    @FXML
    private Button botonIngresar;
    @FXML
    private HBox filaSeguridad;

    @FXML
    private void initialize() {
        decorarBoton(botonIngresar);
        decorarAyuda(enlaceAdmin);
        decorarSeguridad(filaSeguridad);
    }

    @FXML
    private void irALoginAdmin() {
        Navegador.mostrar(Ventana.LOGIN_ADMINISTRADOR);
    }

    @FXML
    private void irARegistro() {
        Navegador.mostrar(Ventana.REGISTRO_USUARIO);
    }
}
