package com.nuctua.controlador;

import com.nuctua.estructuras.ListaEnlazada;
import com.nuctua.estructuras.Pila;
import com.nuctua.modelo.LineaPedido;
import com.nuctua.modelo.Pedido;
import com.nuctua.modelo.Zapato;
import com.nuctua.servicio.HistorialServicio;
import com.nuctua.servicio.ResultadoSeleccion;
import com.nuctua.vista.MenuTallas;
import com.nuctua.vista.TarjetaPedido;
import com.nuctua.vista.TarjetaVisto;
import com.nuctua.vista.Ventana;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

public class HistorialController {

    private static final int MAXIMO_VISTOS = 4;

    @FXML
    private BarraClienteController barraController;
    @FXML
    private ScrollPane scroll;
    @FXML
    private Hyperlink enlaceVolver;
    @FXML
    private Label contador;
    @FXML
    private HBox aviso;
    @FXML
    private Label textoAviso;
    @FXML
    private Hyperlink enlaceAviso;
    @FXML
    private VBox compras;
    @FXML
    private VBox comprasVacias;
    @FXML
    private VBox seccionVistos;
    @FXML
    private FlowPane vistos;
    @FXML
    private Button botonCatalogo;

    private final HistorialServicio historial = new HistorialServicio();
    private final MenuTallas menuTallas = new MenuTallas();

    @FXML
    private void initialize() {
        barraController.marcar(Ventana.HISTORIAL_COMPRAS);
        enlaceVolver.setText("← " + Navegador.textoVolver());
        enlaceVolver.setOnAction(e -> Navegador.volver());
        botonCatalogo.setOnAction(e -> Navegador.mostrar(Ventana.CATALOGO));
        pintarCompras();
        pintarVistos();
    }

    private void pintarCompras() {
        Pila<Pedido> pila = historial.compras();
        int total = pila.tamanio();
        contador.setText(total == 0 ? "SIN PEDIDOS" : total + (total == 1 ? " PEDIDO" : " PEDIDOS"));
        compras.getChildren().clear();
        while (!pila.estaVacia()) {
            Pedido pedido = pila.desapilar();
            TarjetaPedido tarjeta = new TarjetaPedido(pedido, historial, this::volverAComprar);
            tarjeta.getEnlaceFactura().setOnAction(e -> PopupFacturaController.mostrar(pedido));
            compras.getChildren().add(tarjeta);
        }
        alternar(compras, total > 0);
        alternar(comprasVacias, total == 0);
    }

    private void pintarVistos() {
        ListaEnlazada<Zapato> lista = historial.vistos(MAXIMO_VISTOS);
        vistos.getChildren().clear();
        for (Zapato zapato : lista) {
            TarjetaVisto tarjeta = new TarjetaVisto(zapato, vistos.getChildren().isEmpty());
            tarjeta.setOnMouseClicked(e -> abrirDetalle(zapato));
            tarjeta.getBotonCarrito().setOnAction(e -> menuTallas.mostrar(tarjeta.getBotonCarrito(), zapato,
                    talla -> agregarVisto(zapato, talla)));
            vistos.getChildren().add(tarjeta);
        }
        alternar(seccionVistos, !lista.estaVacia());
    }

    public void volverAComprar(LineaPedido linea) {
        Zapato zapato = historial.producto(linea);
        ResultadoSeleccion resultado = historial.volverAComprar(linea);
        if (!resultado.esValida()) {
            boolean yaEsta = resultado == ResultadoSeleccion.STOCK_INSUFICIENTE && historial.enCarrito(linea) > 0;
            mostrarAviso(yaEsta ? "Ya tienes pares de " + linea.getNombre() + " talla " + linea.getTalla()
                    + " en tu carrito y no hay stock para sumar más." : linea.getNombre() + ": " + resultado.getMensaje(), zapato);
            return;
        }
        agregado(zapato, linea.getTalla(), linea.getCantidad());
    }

    public void agregarVisto(Zapato zapato, int talla) {
        ResultadoSeleccion resultado = historial.agregar(zapato, talla);
        if (!resultado.esValida()) {
            mostrarAviso(zapato.getNombre() + ": " + resultado.getMensaje(), zapato);
            return;
        }
        agregado(zapato, talla, 1);
    }

    private void agregado(Zapato zapato, int talla, int cantidad) {
        alternar(aviso, false);
        BarraClienteController.refrescar();
        PopupAgregadoCarritoController.mostrar(zapato, talla, cantidad);
    }

    private void mostrarAviso(String texto, Zapato zapato) {
        textoAviso.setText(texto);
        alternar(enlaceAviso, zapato != null);
        enlaceAviso.setOnAction(e -> abrirDetalle(zapato));
        alternar(aviso, true);
        scroll.setVvalue(0);
    }

    private void abrirDetalle(Zapato zapato) {
        DetalleProductoController detalle = Navegador.mostrar(Ventana.DETALLE_PRODUCTO);
        if (detalle != null) {
            detalle.mostrar(zapato);
        }
    }

    private void alternar(Node nodo, boolean visible) {
        nodo.setVisible(visible);
        nodo.setManaged(visible);
    }
}
