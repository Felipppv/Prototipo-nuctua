package com.nuctua.controlador;

import com.nuctua.modelo.Zapato;
import com.nuctua.servicio.CarritoServicio;
import com.nuctua.util.Iconos;
import com.nuctua.util.Recursos;
import com.nuctua.vista.Ventana;
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

    public static void mostrar(Zapato zapato, int talla, int cantidad) {
        PopupAgregadoCarritoController popup = Popup.abrir(Ventana.POPUP_AGREGADO_CARRITO);
        popup.setAgregado(zapato, talla, cantidad, new CarritoServicio().cantidadTotal());
        popup.mostrarYEsperar();
        if (popup.quiereVerCarrito()) {
            Navegador.mostrar(Ventana.CARRITO);
        }
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
