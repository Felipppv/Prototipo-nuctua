package com.nuctua.controlador;

import com.nuctua.modelo.SesionActual;
import com.nuctua.modelo.Usuario;
import com.nuctua.util.Cinta;
import com.nuctua.vista.Ventana;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;

final class VistaPendiente {

    private VistaPendiente() {
    }

    static Parent crear(Ventana ventana) {
        Usuario usuario = SesionActual.getInstancia().getUsuario();
        Label logo = new Label("NUCTUA");
        logo.getStyleClass().add("logo");
        Region espacio = new Region();
        HBox.setHgrow(espacio, Priority.ALWAYS);
        Button salir = new Button("Cerrar sesión");
        salir.getStyleClass().add("enlace");
        salir.setOnAction(e -> Navegador.cerrarSesion());
        HBox barra = new HBox(logo, espacio, salir);
        barra.getStyleClass().add("barra");
        barra.setAlignment(Pos.CENTER_LEFT);
        if (SesionActual.getInstancia().esAdministrador()) {
            Label admin = new Label("ADMIN");
            admin.getStyleClass().add("etiqueta-admin");
            barra.getChildren().add(1, admin);
        }
        Label pastilla = new Label(ventana.getCodigo() + " · " + ventana.getArea().getNombre());
        pastilla.getStyleClass().add("pastilla");
        Label titulo = new Label(ventana.getTitulo().toUpperCase());
        titulo.getStyleClass().add("titulo-grande");
        String quien = usuario == null ? "Sin sesión" : usuario.getNombre() + " · " + usuario.getCorreo()
                + " · " + (usuario.getRol() == null ? "" : usuario.getRol().name());
        Label sesion = new Label("Sesión iniciada: " + quien);
        sesion.getStyleClass().add("subtitulo");
        Label nota = new Label("Esta pantalla se construye en un próximo commit.");
        nota.getStyleClass().add("texto-suave");
        VBox centro = new VBox(14, pastilla, titulo, sesion, nota);
        centro.setAlignment(Pos.CENTER_LEFT);
        centro.setPadding(new Insets(48, 64, 48, 64));
        BorderPane raiz = new BorderPane(centro);
        raiz.setTop(barra);
        raiz.setBottom(new Cinta());
        raiz.getStyleClass().add("pagina");
        return raiz;
    }
}
