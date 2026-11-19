package com.nuctua.controlador;

import com.nuctua.estructuras.ListaEnlazada;
import com.nuctua.modelo.Zapato;
import com.nuctua.persistencia.ArchivoProductos;
import com.nuctua.persistencia.ArchivoUsuarios;
import com.nuctua.util.Recursos;
import com.nuctua.vista.Area;
import com.nuctua.vista.Ventana;
import java.util.ArrayList;
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
        Recursos.cargarFuentes();
        ListaEnlazada<Zapato> productos = new ArchivoProductos().cargar();
        ArrayList<String> faltantes = Recursos.imagenesFaltantes(productos);
        for (String faltante : faltantes) {
            System.err.println("Imagen no encontrada: " + faltante);
        }
        escenario.setTitle("Nuctua · Calzado exclusivo");
        Label marca = new Label("NUCTUA");
        marca.getStyleClass().add("titulo-gigante");
        VBox raiz = new VBox(12, marca);
        raiz.setAlignment(Pos.CENTER);
        raiz.getStyleClass().add("pagina");
        for (Area area : Area.values()) {
            Label resumen = new Label(area.getNombre() + ": " + Ventana.delArea(area).size() + " ventanas");
            resumen.getStyleClass().add("subtitulo");
            raiz.getChildren().add(resumen);
        }
        int usuarios = new ArchivoUsuarios().cargar().tamanio();
        Label datos = new Label("Datos cargados: " + productos.tamanio() + " pares en catálogo · " + usuarios
                + " usuarios · " + (productos.tamanio() - faltantes.size()) + " fotos verificadas");
        datos.getStyleClass().add("pastilla");
        raiz.getChildren().add(datos);
        Scene escena = new Scene(raiz, 1280, 800);
        escena.getStylesheets().add(Recursos.hojaDeEstilos());
        escenario.setScene(escena);
        escenario.show();
    }

    public static Stage getEscenario() {
        return escenario;
    }
}
