package com.nuctua.controlador;

import com.nuctua.servicio.AutenticacionServicio;
import com.nuctua.servicio.ResultadoAcceso;
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

    private final AutenticacionServicio servicio = new AutenticacionServicio();

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
        String recordado = servicio.correoRecordado();
        if (!recordado.isEmpty()) {
            campoCorreo.setText(recordado);
            checkRecordar.setSelected(true);
        }
        botonIngresar.setOnAction(e -> iniciarSesion());
        enlaceOlvido.setOnAction(e -> mostrarMensaje(mensaje, "Pide a un administrador de Nuctua que restablezca tu contraseña."));
    }

    public void precargarCorreo(String correo) {
        campoCorreo.setText(correo);
        campoContrasena.getCampo().requestFocus();
    }

    private void iniciarSesion() {
        ResultadoAcceso resultado = servicio.iniciarSesion(campoCorreo.getText(), campoContrasena.getTexto(), null);
        if (resultado.esExito()) {
            servicio.recordarCorreo(campoCorreo.getText(), checkRecordar.isSelected());
        }
        procesar(resultado, mensaje);
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
