package com.nuctua.vista;

import com.nuctua.modelo.Zapato;
import com.nuctua.util.Recursos;
import javafx.scene.layout.StackPane;
import javafx.scene.shape.Rectangle;

public class Miniatura extends StackPane {

    private static final double BORDE = 4;

    public Miniatura(Zapato zapato, double lado) {
        double interior = lado - BORDE;
        StackPane imagen = new StackPane(Recursos.vistaCubierta(Recursos.imagenProducto(zapato), interior, interior));
        Rectangle recorte = new Rectangle(interior, interior);
        recorte.setArcWidth(20);
        recorte.setArcHeight(20);
        imagen.setClip(recorte);
        getChildren().add(imagen);
        getStyleClass().add("foto-resumen");
        setMinSize(lado, lado);
        setMaxSize(lado, lado);
    }
}
