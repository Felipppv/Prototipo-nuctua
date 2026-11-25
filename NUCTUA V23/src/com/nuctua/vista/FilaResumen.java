package com.nuctua.vista;

import com.nuctua.modelo.ItemCarrito;
import com.nuctua.modelo.Zapato;
import com.nuctua.util.Recursos;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Rectangle;

public class FilaResumen extends HBox {

    private static final double LADO = 60;

    public FilaResumen(ItemCarrito item) {
        super(14);
        setAlignment(Pos.CENTER_LEFT);
        Zapato zapato = item.getZapato();

        StackPane imagen = new StackPane(Recursos.vistaCubierta(Recursos.imagenProducto(zapato), LADO, LADO));
        Rectangle recorte = new Rectangle(LADO, LADO);
        recorte.setArcWidth(20);
        recorte.setArcHeight(20);
        imagen.setClip(recorte);
        StackPane foto = new StackPane(imagen);
        foto.getStyleClass().add("foto-resumen");
        foto.setMinSize(LADO + 4, LADO + 4);
        foto.setMaxSize(LADO + 4, LADO + 4);

        Label nombre = new Label(zapato.getNombre());
        nombre.getStyleClass().addAll("texto", "negrita");
        nombre.setWrapText(true);
        nombre.setMinHeight(Region.USE_PREF_SIZE);
        String pares = item.getCantidad() == 1 ? "" : " · " + item.getCantidad() + " pares";
        Label detalle = new Label("Talla " + item.getTalla() + pares + " · " + textoColores(zapato));
        detalle.getStyleClass().add("texto-suave");
        detalle.setWrapText(true);
        detalle.setMinHeight(Region.USE_PREF_SIZE);
        VBox textos = new VBox(2, nombre, detalle);
        textos.setAlignment(Pos.CENTER_LEFT);
        textos.setMinWidth(0);
        HBox.setHgrow(textos, Priority.ALWAYS);

        Label precio = new Label(Zapato.formatearPrecio(item.getSubtotal()));
        precio.getStyleClass().addAll("texto", "negrita");
        precio.setMinWidth(Region.USE_PREF_SIZE);
        getChildren().addAll(foto, textos, precio);
    }

    private String textoColores(Zapato zapato) {
        String[] colores = zapato.getColores().split("·");
        StringBuilder texto = new StringBuilder();
        for (int i = 0; i < colores.length; i++) {
            if (i > 0) {
                texto.append(i == colores.length - 1 ? " y " : ", ");
            }
            String color = colores[i].trim();
            texto.append(i == 0 ? color : color.toLowerCase());
        }
        return texto.toString();
    }
}
