package com.nuctua.controlador;

import com.nuctua.util.Recursos;
import com.nuctua.vista.Ventana;
import java.io.IOException;
import java.net.URL;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.input.KeyCode;
import javafx.scene.paint.Color;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

public final class Popup {

    private Popup() {
    }

    public static <T extends PopupBase> T abrir(Ventana ventana) {
        if (!ventana.esPopup()) {
            throw new IllegalArgumentException(ventana.getTitulo() + " no es una ventana emergente");
        }
        URL ruta = Popup.class.getResource(ventana.getRutaFxml());
        if (ruta == null) {
            throw new IllegalStateException("Popup no encontrado: " + ventana.getRutaFxml());
        }
        FXMLLoader cargador = new FXMLLoader(ruta);
        Parent raiz;
        try {
            raiz = cargador.load();
        } catch (IOException e) {
            throw new IllegalStateException("No se pudo cargar el popup " + ventana.getTitulo(), e);
        }
        Stage principal = Navegador.getEscenario();
        Stage escenario = new Stage(StageStyle.TRANSPARENT);
        escenario.initOwner(principal);
        escenario.initModality(Modality.APPLICATION_MODAL);
        escenario.setTitle("Nuctua · " + ventana.getTitulo());
        Scene escena = new Scene(raiz, principal.getScene().getWidth(), principal.getScene().getHeight(), Color.TRANSPARENT);
        escena.getStylesheets().add(Recursos.hojaDeEstilos());
        escena.setOnKeyPressed(evento -> {
            if (evento.getCode() == KeyCode.ESCAPE) {
                escenario.close();
            }
        });
        escenario.setScene(escena);
        escenario.setX(principal.getX() + principal.getScene().getX());
        escenario.setY(principal.getY() + principal.getScene().getY());
        T controlador = cargador.getController();
        controlador.setEscenario(escenario);
        return controlador;
    }
}
