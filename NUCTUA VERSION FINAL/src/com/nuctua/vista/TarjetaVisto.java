package com.nuctua.vista;

import com.nuctua.modelo.Zapato;
import com.nuctua.util.Recursos;
import javafx.event.Event;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Rectangle;

public class TarjetaVisto extends VBox {

    private static final double ANCHO = 252;
    private static final double ANCHO_FOTO = 214;
    private static final double ALTO_FOTO = 134;

    private final Zapato zapato;
    private final Button botonCarrito;

    public TarjetaVisto(Zapato zapato, boolean ultimo) {
        this.zapato = zapato;
        getStyleClass().addAll("tarjeta", "tarjeta-visto");
        setMinWidth(ANCHO);
        setMaxWidth(ANCHO);

        StackPane imagen = new StackPane(Recursos.vistaCubierta(Recursos.imagenProducto(zapato), ANCHO_FOTO, ALTO_FOTO));
        Rectangle recorte = new Rectangle(ANCHO_FOTO, ALTO_FOTO);
        recorte.setArcWidth(22);
        recorte.setArcHeight(22);
        imagen.setClip(recorte);
        Label insignia = new Label(ultimo ? "ÚLTIMO VISTO" : "VISTO ANTES");
        insignia.getStyleClass().add("insignia-visto");
        StackPane foto = new StackPane(imagen, insignia);
        foto.getStyleClass().add("foto-resumen");
        foto.setMinSize(ANCHO_FOTO + 4, ALTO_FOTO + 4);
        foto.setMaxSize(ANCHO_FOTO + 4, ALTO_FOTO + 4);
        StackPane.setAlignment(insignia, Pos.TOP_LEFT);
        StackPane.setMargin(insignia, new Insets(4, 0, 0, 4));

        Label nombre = new Label(zapato.getNombre().toUpperCase());
        nombre.getStyleClass().add("nombre-visto");
        nombre.setWrapText(true);
        nombre.setMinHeight(Region.USE_PREF_SIZE);
        Label colores = new Label(zapato.getColores().replace("·", "•"));
        colores.getStyleClass().add("texto-suave");
        Label precio = new Label(zapato.getPrecioTexto());
        precio.getStyleClass().add("nombre-visto");
        Region espacio = new Region();
        VBox.setVgrow(espacio, Priority.ALWAYS);
        botonCarrito = new Button(zapato.estaAgotado() ? "AGOTADO" : "AGREGAR AL CARRITO");
        botonCarrito.getStyleClass().add("boton-agregar");
        botonCarrito.setMaxWidth(Double.MAX_VALUE);
        botonCarrito.setDisable(zapato.estaAgotado());
        botonCarrito.addEventHandler(MouseEvent.MOUSE_CLICKED, Event::consume);
        getChildren().addAll(foto, nombre, colores, espacio, precio, botonCarrito);
        setOnMouseEntered(e -> setTranslateY(-3));
        setOnMouseExited(e -> setTranslateY(0));
    }

    public Zapato getZapato() {
        return zapato;
    }

    public Button getBotonCarrito() {
        return botonCarrito;
    }
}
