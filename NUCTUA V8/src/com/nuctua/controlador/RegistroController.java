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

public class RegistroController extends AccesoBase {

    @FXML
    private VBox panel;
    @FXML
    private Hyperlink enlaceAdmin;
    @FXML
    private TextField campoNombre;
    @FXML
    private TextField campoCorreo;
    @FXML
    private CampoContrasena campoContrasena;
    @FXML
    private CampoContrasena campoConfirmacion;
    @FXML
    private CheckBox checkTerminos;
    @FXML
    private Label mensaje;
    @FXML
    private Button botonCrear;
    @FXML
    private HBox filaSeguridad;

    @FXML
    private void initialize() {
        decorarBoton(botonCrear);
        decorarAyuda(enlaceAdmin);
        decorarSeguridad(filaSeguridad);
    }

    @FXML
    private void irALogin() {
        Navegador.mostrar(Ventana.LOGIN_CLIENTE);
    }

    @FXML
    private void irALoginAdmin() {
        Navegador.mostrar(Ventana.LOGIN_ADMINISTRADOR);
    }
}
