package com.nuctua.controlador;

import com.nuctua.modelo.Zapato;
import com.nuctua.util.Iconos;
import com.nuctua.util.Recursos;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;

public class PopupAgregadoCarritoController extends PopupBase {

    @FXML
    private StackPane icono;
    @FXML
    private StackPane miniatura;
    @FXML
    private Label nombre;
    @FXML
    private Label detalle;
    @FXML
    private Label precio;
    @FXML
    private Label mensaje;

    private boolean irAlCarrito;

    @FXML
    private void initialize() {
        icono.getChildren().add(Iconos.okGrande(70));
    }

    public void setAgregado(Zapato zapato, int talla, int cantidad, int totalCarrito) {
        miniatura.getChildren().setAll(Recursos.vistaCubierta(Recursos.imagenProducto(zapato), 76, 62));
        nombre.setText(zapato.getNombre().toUpperCase());
        detalle.setText("Talla " + talla + " · " + cantidad + (cantidad == 1 ? " par" : " pares"));
        precio.setText(Zapato.formatearPrecio(zapato.getPrecio() * cantidad));
        mensaje.setText("Ahora tienes " + totalCarrito + (totalCarrito == 1 ? " par" : " pares") + " en tu carrito.");
    }

    @FXML
    private void verCarrito() {
        irAlCarrito = true;
        cerrar();
    }

    public boolean quiereVerCarrito() {
        return irAlCarrito;
    }
}
