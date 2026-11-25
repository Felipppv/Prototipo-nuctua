package com.nuctua.controlador;

import com.nuctua.modelo.Zapato;
import com.nuctua.util.Iconos;
import com.nuctua.util.Recursos;
import com.nuctua.vista.Ventana;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;

public class PopupCambiosGuardadosController extends PopupBase {

    @FXML
    private StackPane icono;
    @FXML
    private StackPane miniatura;
    @FXML
    private Label nombre;
    @FXML
    private Label detalle;
    @FXML
    private Label mensaje;

    @FXML
    private void initialize() {
        icono.getChildren().add(Iconos.okGrande(70));
    }

    public static void mostrar(Zapato zapato, String texto) {
        int tallas = zapato.getTallas().size();
        int pares = zapato.getStockTotal();
        mostrar(Recursos.vistaCubierta(Recursos.imagenProducto(zapato), 76, 62), zapato.getNombre(),
                "SKU " + zapato.getSku() + " · " + pares + (pares == 1 ? " par en " : " pares en ")
                + tallas + (tallas == 1 ? " talla" : " tallas"), texto);
    }

    public static void mostrar(Node imagen, String titulo, String descripcion, String texto) {
        PopupCambiosGuardadosController popup = Popup.abrir(Ventana.POPUP_CAMBIOS_GUARDADOS);
        popup.miniatura.getChildren().setAll(imagen);
        popup.nombre.setText(titulo.toUpperCase());
        popup.detalle.setText(descripcion);
        popup.mensaje.setText(texto);
        popup.mostrarYEsperar();
    }
}
