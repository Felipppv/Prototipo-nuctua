package com.nuctua.controlador;

import com.nuctua.persistencia.ArchivoProductos;
import com.nuctua.persistencia.ArchivoUsuarios;
import com.nuctua.vista.Area;
import com.nuctua.vista.Ventana;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public final class Navegador {

    private static Stage escenario;

    private Navegador() {
    }

    public static void iniciar(Stage principal) {
        escenario = principal;
        escenario.setTitle("Nuctua · Calzado exclusivo");
        Label marca = new Label("NUCTUA");
        marca.setStyle("-fx-font-size: 64px; -fx-font-weight: bold; -fx-text-fill: #1B1410;");
        VBox raiz = new VBox(12, marca);
        raiz.setAlignment(Pos.CENTER);
        for (Area area : Area.values()) {
            Label resumen = new Label(area.getNombre() + ": " + Ventana.delArea(area).size() + " ventanas");
            resumen.setStyle("-fx-font-size: 16px; -fx-text-fill: #1B1410;");
            raiz.getChildren().add(resumen);
        }
        int productos = new ArchivoProductos().cargar().tamanio();
        int usuarios = new ArchivoUsuarios().cargar().tamanio();
        Label datos = new Label("Datos cargados: " + productos + " pares en catálogo · " + usuarios + " usuarios");
        datos.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: #FF6A2B;");
        raiz.getChildren().add(datos);
        raiz.setStyle("-fx-background-color: #FFF4E4;");
        escenario.setScene(new Scene(raiz, 1280, 800));
        escenario.show();
    }

    public static Stage getEscenario() {
        return escenario;
    }
}
