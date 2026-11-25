package com.nuctua.controlador;

import com.nuctua.modelo.Pedido;
import com.nuctua.util.Iconos;
import com.nuctua.vista.Ventana;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;

public class PopupCompraConfirmadaController extends PopupBase {

    @FXML
    private StackPane icono;
    @FXML
    private Label pastilla;
    @FXML
    private Label mensaje;

    private boolean irAlHistorial;

    @FXML
    private void initialize() {
        icono.getChildren().add(Iconos.okGrande(70));
    }

    public static boolean mostrar(Pedido pedido) {
        PopupCompraConfirmadaController popup = Popup.abrir(Ventana.POPUP_COMPRA_CONFIRMADA);
        popup.setPedido(pedido);
        popup.mostrarYEsperar();
        return popup.irAlHistorial;
    }

    public void setPedido(Pedido pedido) {
        pastilla.setText("Pedido #" + pedido.getCodigo() + " · " + pedido.getFechaTexto());
        mensaje.setText(pedido.getTextoPago() + " Tu pedido ya aparece en tu historial.");
    }

    @FXML
    private void verHistorial() {
        irAlHistorial = true;
        cerrar();
    }
}
