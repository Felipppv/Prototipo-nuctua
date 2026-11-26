package com.nuctua.controlador;

import com.nuctua.servicio.AutenticacionServicio;
import com.nuctua.servicio.ResultadoAcceso;
import com.nuctua.util.CampoContrasena;
import com.nuctua.vista.Ventana;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Control;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

public class RegistroController extends AccesoBase {

    private final AutenticacionServicio servicio = new AutenticacionServicio();

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
        botonCrear.setOnAction(e -> crearCuenta());
    }

    private void crearCuenta() {
        ResultadoAcceso resultado = servicio.registrar(campoNombre.getText(), campoCorreo.getText(),
                campoContrasena.getTexto(), campoConfirmacion.getTexto(), checkTerminos.isSelected());
        marcarError(campoNombre, resultado.getTipo() == ResultadoAcceso.Tipo.NOMBRE_INVALIDO);
        marcarError(campoCorreo, resultado.getTipo() == ResultadoAcceso.Tipo.CORREO_INVALIDO
                || resultado.getTipo() == ResultadoAcceso.Tipo.CORREO_EXISTENTE);
        marcarError(campoContrasena.getCampo(), resultado.getTipo() == ResultadoAcceso.Tipo.CONTRASENA_DEBIL);
        marcarError(campoConfirmacion.getCampo(), resultado.getTipo() == ResultadoAcceso.Tipo.CONFIRMACION);
        if (!resultado.esExito()) {
            mostrarMensaje(mensaje, resultado.getMensaje());
            return;
        }
        ocultarMensaje(mensaje);
        PopupCuentaCreadaController popup = Popup.abrir(Ventana.POPUP_CUENTA_CREADA);
        popup.setUsuario(resultado.getUsuario());
        popup.mostrarYEsperar();
        LoginController login = Navegador.mostrar(Ventana.LOGIN_CLIENTE);
        if (login != null) {
            login.precargarCorreo(resultado.getUsuario().getCorreo());
        }
    }

    private void marcarError(Control campo, boolean error) {
        campo.getStyleClass().remove("campo-error");
        if (error) {
            campo.getStyleClass().add("campo-error");
        }
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
