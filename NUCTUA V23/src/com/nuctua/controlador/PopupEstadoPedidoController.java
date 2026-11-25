package com.nuctua.controlador;

import com.nuctua.modelo.EstadoPedido;
import com.nuctua.modelo.Pedido;
import com.nuctua.modelo.TotalesCompra;
import com.nuctua.modelo.Zapato;
import com.nuctua.vista.Ventana;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.Toggle;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;

public class PopupEstadoPedidoController extends PopupBase {

    @FXML
    private Label resumen;
    @FXML
    private VBox opciones;
    @FXML
    private Label mensaje;
    @FXML
    private Button botonGuardar;

    private final ToggleGroup grupo = new ToggleGroup();
    private EstadoPedido actual;
    private EstadoPedido elegido;

    public static EstadoPedido elegir(Pedido pedido) {
        PopupEstadoPedidoController popup = Popup.abrir(Ventana.POPUP_ESTADO_PEDIDO);
        popup.setPedido(pedido);
        popup.mostrarYEsperar();
        return popup.elegido;
    }

    public void setPedido(Pedido pedido) {
        actual = pedido.getEstado();
        TotalesCompra totales = pedido.getTotales();
        resumen.setText("#" + pedido.getCodigo() + " · " + pedido.getEnvio().getNombre() + " · " + totales.getTextoPares()
                + " · " + Zapato.formatearPrecio(totales.getTotal()));
        opciones.getChildren().clear();
        for (EstadoPedido estado : EstadoPedido.values()) {
            opciones.getChildren().add(crearFila(estado));
        }
        if (actual.esFinal()) {
            mensaje.setText("Este pedido ya está " + actual.getTexto().toLowerCase() + " y no puede cambiar.");
        } else {
            mensaje.setText("Cancelar devuelve los pares al inventario.");
        }
        botonGuardar.setDisable(true);
        grupo.selectedToggleProperty().addListener((obs, antes, ahora) ->
                botonGuardar.setDisable(ahora == null || ahora.getUserData() == actual));
    }

    private HBox crearFila(EstadoPedido estado) {
        boolean permitido = estado == actual || actual.puedePasarA(estado);
        Label texto = new Label(estado.getTexto());
        texto.getStyleClass().add("texto-agregado");
        Region espacio = new Region();
        HBox.setHgrow(espacio, Priority.ALWAYS);
        RadioButton radio = new RadioButton();
        radio.setUserData(estado);
        radio.setToggleGroup(grupo);
        radio.setDisable(!permitido);
        radio.getStyleClass().add("radio-estado");
        radio.setAccessibleText(estado.getTexto());
        HBox fila = new HBox(texto, espacio, radio);
        fila.setAlignment(Pos.CENTER_LEFT);
        fila.getStyleClass().add("fila-estado");
        fila.setDisable(!permitido);
        fila.setOnMouseClicked(e -> radio.setSelected(true));
        radio.selectedProperty().addListener((obs, antes, ahora) -> {
            fila.getStyleClass().remove("fila-estado-elegida");
            if (ahora) {
                fila.getStyleClass().add("fila-estado-elegida");
            }
        });
        radio.setSelected(estado == actual);
        return fila;
    }

    public void marcar(EstadoPedido estado) {
        for (Toggle opcion : grupo.getToggles()) {
            if (opcion.getUserData() == estado) {
                opcion.setSelected(true);
            }
        }
    }

    @FXML
    private void guardar() {
        if (grupo.getSelectedToggle() != null) {
            elegido = (EstadoPedido) grupo.getSelectedToggle().getUserData();
        }
        cerrar();
    }
}
