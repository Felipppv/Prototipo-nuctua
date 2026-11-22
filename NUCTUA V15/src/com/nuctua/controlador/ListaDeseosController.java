package com.nuctua.controlador;

import com.nuctua.modelo.Zapato;
import com.nuctua.servicio.DeseosServicio;
import com.nuctua.servicio.OrdenDeseos;
import com.nuctua.util.Iconos;
import com.nuctua.vista.TarjetaDeseo;
import com.nuctua.vista.Ventana;
import java.util.ArrayList;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.geometry.Side;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;

public class ListaDeseosController {

    private static final int COLUMNAS = 3;

    @FXML
    private BarraClienteController barraController;
    @FXML
    private ScrollPane scroll;
    @FXML
    private Hyperlink enlaceVolver;
    @FXML
    private Label contador;
    @FXML
    private Button botonOrdenar;
    @FXML
    private Label infoRecorrido;
    @FXML
    private FlowPane grilla;

    private final DeseosServicio deseos = new DeseosServicio();
    private final ContextMenu menuOrden = new ContextMenu();
    private OrdenDeseos orden = OrdenDeseos.MAS_RECIENTES;

    @FXML
    private void initialize() {
        barraController.marcar(Ventana.LISTA_DESEOS);
        enlaceVolver.setGraphic(Iconos.flechaIzquierda(Iconos.OSCURO, 20));
        enlaceVolver.setText(Navegador.textoVolver());
        enlaceVolver.setOnAction(e -> Navegador.volver());
        botonOrdenar.setGraphic(Iconos.flechaAbajo(Iconos.OSCURO, 18));
        botonOrdenar.setContentDisplay(ContentDisplay.RIGHT);
        botonOrdenar.setGraphicTextGap(24);
        menuOrden.getStyleClass().add("menu-orden");
        for (OrdenDeseos opcion : OrdenDeseos.values()) {
            MenuItem item = new MenuItem(opcion.getEtiqueta());
            item.setOnAction(e -> {
                orden = opcion;
                pintar();
            });
            menuOrden.getItems().add(item);
        }
        botonOrdenar.setOnAction(e -> menuOrden.show(botonOrdenar, Side.BOTTOM, 0, 6));
        grilla.widthProperty().addListener((obs, antes, ahora) -> ajustar());
        pintar();
    }

    public void ordenarPor(OrdenDeseos nuevo) {
        orden = nuevo == null ? OrdenDeseos.MAS_RECIENTES : nuevo;
        pintar();
    }

    public ArrayList<Zapato> getMostrados() {
        return deseos.ordenados(orden);
    }

    private void pintar() {
        ArrayList<Zapato> lista = deseos.ordenados(orden);
        int total = lista.size();
        contador.setText(total == 0 ? "Sin pares guardados" : total + (total == 1 ? " par guardado" : " pares guardados"));
        botonOrdenar.setText("Ordenar: " + orden.getEtiqueta());
        botonOrdenar.setDisable(total == 0);
        infoRecorrido.setText(orden.getRecorrido());
        grilla.getChildren().clear();
        for (Zapato zapato : lista) {
            TarjetaDeseo tarjeta = new TarjetaDeseo(zapato);
            tarjeta.setOnMouseClicked(e -> abrirDetalle(zapato));
            tarjeta.getBotonCarrito().setOnAction(e -> abrirDetalle(zapato));
            tarjeta.getBotonCorazon().setOnAction(e -> quitar(zapato));
            tarjeta.getBotonQuitar().setOnAction(e -> quitar(zapato));
            grilla.getChildren().add(tarjeta);
        }
        grilla.getChildren().add(crearExplorar(total == 0));
        ajustar();
    }

    private void quitar(Zapato zapato) {
        deseos.quitar(zapato.getSku());
        BarraClienteController.refrescar();
        pintar();
    }

    private VBox crearExplorar(boolean vacia) {
        HBox huellas = new HBox(40, Iconos.huellaPequena(Color.web("#8C7B6B"), -25), Iconos.huellaPequena(Iconos.OSCURO, -25));
        huellas.setAlignment(Pos.CENTER);
        huellas.setScaleX(1.6);
        huellas.setScaleY(1.6);
        Label titulo = new Label(vacia ? "TU LISTA ESTÁ VACÍA" : "¿MÁS PARES EN LA MIRA?");
        titulo.getStyleClass().add("titulo-explorar");
        titulo.setWrapText(true);
        Label texto = new Label("Toca el corazón en cualquier tenis y aparecerá aquí, guardado para cuando quieras.");
        texto.getStyleClass().add("subtitulo");
        texto.setWrapText(true);
        texto.setMaxWidth(440);
        texto.setMinHeight(Region.USE_PREF_SIZE);
        texto.setStyle("-fx-text-alignment: center;");
        Button ir = new Button("IR AL CATÁLOGO");
        ir.getStyleClass().add("boton-primario");
        ir.setGraphic(Iconos.flechaDerecha(Iconos.CREMA, 18));
        ir.setContentDisplay(ContentDisplay.RIGHT);
        ir.setMinWidth(300);
        ir.setOnAction(e -> Navegador.mostrar(Ventana.CATALOGO));
        VBox tarjeta = new VBox(22, huellas, titulo, texto, ir);
        tarjeta.getStyleClass().addAll("tarjeta-punteada", "tarjeta-sugerencia");
        tarjeta.setMinHeight(520);
        return tarjeta;
    }

    private void ajustar() {
        double disponible = grilla.getWidth();
        if (disponible <= 0) {
            return;
        }
        double ancho = Math.floor((disponible - (COLUMNAS - 1) * grilla.getHgap() - 8) / COLUMNAS);
        int tarjetas = 0;
        for (Node nodo : grilla.getChildren()) {
            if (nodo instanceof TarjetaDeseo tarjeta) {
                tarjeta.ajustarAncho(ancho);
                tarjetas++;
            }
        }
        int libres = COLUMNAS - tarjetas % COLUMNAS;
        double anchoExplorar = libres * ancho + (libres - 1) * grilla.getHgap();
        Node ultimo = grilla.getChildren().get(grilla.getChildren().size() - 1);
        if (ultimo instanceof VBox explorar && !(ultimo instanceof TarjetaDeseo)) {
            explorar.setMinWidth(anchoExplorar);
            explorar.setPrefWidth(anchoExplorar);
            explorar.setMaxWidth(anchoExplorar);
        }
    }

    private void abrirDetalle(Zapato zapato) {
        DetalleProductoController detalle = Navegador.mostrar(Ventana.DETALLE_PRODUCTO);
        if (detalle != null) {
            detalle.mostrar(zapato);
        }
    }
}
