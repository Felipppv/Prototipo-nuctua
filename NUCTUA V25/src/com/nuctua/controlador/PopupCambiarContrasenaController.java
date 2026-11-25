package com.nuctua.controlador;

import com.nuctua.servicio.AutenticacionServicio;
import com.nuctua.servicio.ResultadoAcceso;
import com.nuctua.util.CampoContrasena;
import com.nuctua.util.Iconos;
import com.nuctua.vista.Ventana;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.scene.shape.Circle;

public class PopupCambiarContrasenaController extends PopupBase {

    @FXML
    private StackPane icono;
    @FXML
    private CampoContrasena campoActual;
    @FXML
    private CampoContrasena campoNueva;
    @FXML
    private CampoContrasena campoConfirmacion;
    @FXML
    private Label mensaje;

    private boolean cambiada;

    @FXML
    private void initialize() {
        Circle fondo = new Circle(30, Iconos.AMARILLO);
        fondo.setStroke(Iconos.OSCURO);
        fondo.setStrokeWidth(3);
        icono.getChildren().addAll(fondo, Iconos.candado(Iconos.OSCURO, 28));
    }

    public static boolean mostrar() {
        PopupCambiarContrasenaController popup = Popup.abrir(Ventana.POPUP_CAMBIAR_CONTRASENA);
        popup.mostrarYEsperar();
        return popup.cambiada;
    }

    @FXML
    private void cambiar() {
        ResultadoAcceso resultado;
        try {
            resultado = new AutenticacionServicio().cambiarContrasena(campoActual.getTexto(), campoNueva.getTexto(),
                    campoConfirmacion.getTexto());
        } catch (IllegalStateException e) {
            mostrarError("No pudimos guardar el cambio. Inténtalo de nuevo.");
            return;
        }
        ResultadoAcceso.Tipo tipo = resultado.getTipo();
        marcar(campoActual, tipo == ResultadoAcceso.Tipo.CREDENCIALES);
        marcar(campoNueva, tipo == ResultadoAcceso.Tipo.CONTRASENA_DEBIL);
        marcar(campoConfirmacion, tipo == ResultadoAcceso.Tipo.CONFIRMACION);
        if (!resultado.esExito()) {
            mostrarError(resultado.getMensaje());
            return;
        }
        cambiada = true;
        cerrar();
    }

    private void mostrarError(String texto) {
        mensaje.setText(texto);
        mensaje.setVisible(true);
        mensaje.setManaged(true);
    }

    private void marcar(CampoContrasena campo, boolean error) {
        campo.getCampo().getStyleClass().remove("campo-error");
        if (error) {
            campo.getCampo().getStyleClass().add("campo-error");
        }
    }
}
