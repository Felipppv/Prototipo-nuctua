package com.nuctua.controlador;

import com.nuctua.servicio.ResultadoAcceso;
import com.nuctua.util.Iconos;
import com.nuctua.vista.Ventana;
import javafx.scene.control.Button;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;

public abstract class AccesoBase {

    protected void decorarBoton(Button boton) {
        boton.setGraphic(Iconos.flechaDerecha(Iconos.CREMA, 18));
        boton.setContentDisplay(ContentDisplay.RIGHT);
        boton.setGraphicTextGap(12);
    }

    protected void decorarAyuda(Hyperlink enlace) {
        enlace.setGraphic(Iconos.ayuda(Iconos.OSCURO, 18));
        enlace.setGraphicTextGap(6);
    }

    protected void decorarSeguridad(HBox fila) {
        fila.getChildren().add(0, Iconos.candado(Iconos.OSCURO, 15));
    }

    protected boolean procesar(ResultadoAcceso resultado, Label mensaje) {
        if (resultado.esExito()) {
            ocultarMensaje(mensaje);
            Navegador.irAInicioSegunRol();
            return true;
        }
        if (resultado.getTipo() == ResultadoAcceso.Tipo.CREDENCIALES || resultado.getTipo() == ResultadoAcceso.Tipo.ROL) {
            ocultarMensaje(mensaje);
            PopupDatosIncorrectosController popup = Popup.abrir(Ventana.POPUP_DATOS_INCORRECTOS);
            popup.setContenido(resultado.getTipo() == ResultadoAcceso.Tipo.ROL ? "SIN PERMISOS" : "DATOS INCORRECTOS",
                    resultado.getMensaje());
            popup.mostrarYEsperar();
        } else {
            mostrarMensaje(mensaje, resultado.getMensaje());
        }
        return false;
    }

    protected void mostrarMensaje(Label mensaje, String texto) {
        mensaje.setText(texto);
        mensaje.setVisible(true);
        mensaje.setManaged(true);
    }

    protected void ocultarMensaje(Label mensaje) {
        mensaje.setVisible(false);
        mensaje.setManaged(false);
    }
}
