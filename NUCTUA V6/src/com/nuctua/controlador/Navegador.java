package com.nuctua.controlador;

import com.nuctua.persistencia.ArchivoProductos;
import com.nuctua.util.Recursos;
import com.nuctua.vista.Ventana;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public final class Navegador {

    private static Stage escenario;
    private static Scene escena;
    private static Ventana actual;

    private Navegador() {
    }

    public static void iniciar(Stage principal) {
        escenario = principal;
        Recursos.cargarFuentes();
        ArrayList<String> faltantes = Recursos.imagenesFaltantes(new ArchivoProductos().cargar());
        for (String faltante : faltantes) {
            System.err.println("Imagen no encontrada: " + faltante);
        }
        escenario.setTitle("Nuctua · Calzado exclusivo");
        escenario.setMinWidth(1100);
        escenario.setMinHeight(700);
        mostrar(Ventana.LOGIN_CLIENTE);
        escenario.show();
    }

    public static <T> T mostrar(Ventana ventana) {
        URL ruta = Navegador.class.getResource(ventana.getRutaFxml());
        if (ruta == null) {
            throw new IllegalStateException("Vista no encontrada: " + ventana.getRutaFxml());
        }
        FXMLLoader cargador = new FXMLLoader(ruta);
        Parent raiz;
        try {
            raiz = cargador.load();
        } catch (IOException e) {
            throw new IllegalStateException("No se pudo cargar la vista " + ventana.getTitulo(), e);
        }
        if (escena == null) {
            escena = new Scene(raiz, 1360, 860);
            escena.getStylesheets().add(Recursos.hojaDeEstilos());
            escenario.setScene(escena);
        } else {
            escena.setRoot(raiz);
        }
        actual = ventana;
        escenario.setTitle("Nuctua · " + ventana.getTitulo());
        return cargador.getController();
    }

    public static Ventana getActual() {
        return actual;
    }

    public static Stage getEscenario() {
        return escenario;
    }
}
