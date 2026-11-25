package com.nuctua.controlador;

import com.nuctua.modelo.Zapato;
import com.nuctua.util.Iconos;
import com.nuctua.util.Recursos;
import com.nuctua.vista.Ventana;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;

public class PopupEliminarProductoController extends PopupBase {

    @FXML
    private StackPane icono;
    @FXML
    private Label titulo;
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

    private boolean confirmado;

    @FXML
    private void initialize() {
        Circle fondo = new Circle(33, Color.web("#FFE3CC"));
        fondo.setStroke(Iconos.OSCURO);
        fondo.setStrokeWidth(3);
        icono.getChildren().addAll(fondo, Iconos.basura(Iconos.OSCURO, 36));
    }

    public static boolean confirmar(Zapato zapato) {
        int pares = zapato.getStockTotal();
        return confirmar("¿ELIMINAR ESTE PAR?", Recursos.vistaCubierta(Recursos.imagenProducto(zapato), 76, 62),
                zapato.getNombre(), "SKU " + zapato.getSku() + " · " + pares + (pares == 1 ? " par" : " pares") + " en bodega",
                zapato.getPrecioTexto(),
                "Se borrará del archivo de productos y dejará de verse en el catálogo. No se puede deshacer.");
    }

    public static boolean confirmar(String pregunta, Node imagen, String nombre, String detalle, String precio, String texto) {
        PopupEliminarProductoController popup = Popup.abrir(Ventana.POPUP_ELIMINAR_PRODUCTO);
        popup.titulo.setText(pregunta);
        popup.miniatura.getChildren().setAll(imagen);
        popup.nombre.setText(nombre.toUpperCase());
        popup.detalle.setText(detalle);
        popup.precio.setText(precio);
        popup.mensaje.setText(texto);
        popup.mostrarYEsperar();
        return popup.confirmado;
    }

    @FXML
    private void eliminar() {
        confirmado = true;
        cerrar();
    }
}
