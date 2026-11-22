package com.nuctua.controlador;

import com.nuctua.modelo.Usuario;
import com.nuctua.servicio.AutenticacionServicio;
import com.nuctua.servicio.ResultadoAcceso;
import com.nuctua.util.CampoContrasena;
import com.nuctua.util.Iconos;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.Control;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.StackPane;
import javafx.scene.shape.Circle;

public class PopupRecuperarContrasenaController extends PopupBase {

    @FXML
    private StackPane icono;
    @FXML
    private TextField campoCorreo;
    @FXML
    private TextField campoNombre;
    @FXML
    private CampoContrasena campoNueva;
    @FXML
    private CampoContrasena campoConfirmacion;
    @FXML
    private Label mensaje;
    @FXML
    private Button botonCambiar;

    private final AutenticacionServicio servicio = new AutenticacionServicio();
    private Usuario recuperado;

    @FXML
    private void initialize() {
        Circle fondo = new Circle(30, Iconos.AMARILLO);
        fondo.setStroke(Iconos.OSCURO);
        fondo.setStrokeWidth(3);
        icono.getChildren().addAll(fondo, Iconos.candado(Iconos.OSCURO, 28));
        botonCambiar.setGraphic(Iconos.flechaDerecha(Iconos.CREMA, 18));
        botonCambiar.setContentDisplay(ContentDisplay.RIGHT);
    }

    public void setCorreo(String correo) {
        campoCorreo.setText(correo == null ? "" : correo.trim());
    }

    public Usuario getRecuperado() {
        return recuperado;
    }

    @FXML
    private void cambiar() {
        ResultadoAcceso resultado = servicio.recuperarContrasena(campoCorreo.getText(), campoNombre.getText(),
                campoNueva.getTexto(), campoConfirmacion.getTexto());
        ResultadoAcceso.Tipo tipo = resultado.getTipo();
        marcar(campoCorreo, tipo == ResultadoAcceso.Tipo.CORREO_INVALIDO || tipo == ResultadoAcceso.Tipo.CREDENCIALES);
        marcar(campoNombre, tipo == ResultadoAcceso.Tipo.CREDENCIALES);
        marcar(campoNueva.getCampo(), tipo == ResultadoAcceso.Tipo.CONTRASENA_DEBIL);
        marcar(campoConfirmacion.getCampo(), tipo == ResultadoAcceso.Tipo.CONFIRMACION);
        if (!resultado.esExito()) {
            mensaje.setText(resultado.getMensaje());
            mensaje.setVisible(true);
            mensaje.setManaged(true);
            return;
        }
        recuperado = resultado.getUsuario();
        cerrar();
    }

    private void marcar(Control campo, boolean error) {
        campo.getStyleClass().remove("campo-error");
        if (error) {
            campo.getStyleClass().add("campo-error");
        }
    }
}
