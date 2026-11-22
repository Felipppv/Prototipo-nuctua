package com.nuctua.controlador;

import com.nuctua.modelo.SesionActual;
import com.nuctua.modelo.TallaStock;
import com.nuctua.modelo.Zapato;
import com.nuctua.servicio.CatalogoServicio;
import com.nuctua.util.Iconos;
import com.nuctua.util.Recursos;
import com.nuctua.vista.TarjetaZapato;
import com.nuctua.vista.Ventana;
import java.util.ArrayList;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;

public class DetalleProductoController {

    private static final int MAXIMO_VISTOS = 4;

    @FXML
    private BarraClienteController barraController;
    @FXML
    private ScrollPane scroll;
    @FXML
    private StackPane foto;
    @FXML
    private Label etiqueta;
    @FXML
    private Label chipSku;
    @FXML
    private Label nombre;
    @FXML
    private Label precio;
    @FXML
    private Label estadoStock;
    @FXML
    private HBox filaColores;
    @FXML
    private Label descripcion;
    @FXML
    private FlowPane panelTallas;
    @FXML
    private Label pastillaStock;
    @FXML
    private Label textoStock;
    @FXML
    private Button botonCarrito;
    @FXML
    private Button botonDeseos;
    @FXML
    private FlowPane panelVistos;

    private final ToggleGroup grupoTallas = new ToggleGroup();
    private final CatalogoServicio servicio = new CatalogoServicio();
    private Zapato zapato;

    @FXML
    private void initialize() {
        barraController.marcar(Ventana.DETALLE_PRODUCTO);
        botonCarrito.setGraphic(Iconos.flechaDerecha(Iconos.CREMA, 18));
        botonCarrito.setContentDisplay(ContentDisplay.RIGHT);
        botonDeseos.setGraphic(Iconos.corazon(Iconos.OSCURO, 18, false));
        grupoTallas.selectedToggleProperty().addListener((obs, antes, ahora) -> actualizarStockTalla());
    }

    public void mostrar(Zapato elegido) {
        Zapato actualizado = servicio.buscarPorSku(elegido.getSku());
        zapato = actualizado == null ? elegido : actualizado;
        SesionActual.getInstancia().registrarVisto(zapato);
        pintarProducto();
        pintarVistos();
        scroll.setVvalue(0);
    }

    public Zapato getZapato() {
        return zapato;
    }

    private void pintarProducto() {
        foto.getChildren().removeIf(n -> n != etiqueta);
        StackPane imagen = new StackPane(Recursos.vistaCubierta(Recursos.imagenProducto(zapato), 518, 458));
        Rectangle recorte = new Rectangle(518, 458);
        recorte.setArcWidth(28);
        recorte.setArcHeight(28);
        imagen.setClip(recorte);
        foto.getChildren().add(0, imagen);
        etiqueta.setText(zapato.estaAgotado() ? "Agotado" : zapato.getMarca());
        chipSku.setText("SKU " + zapato.getSku() + " · " + zapato.getMarca().toUpperCase());
        nombre.setText(zapato.getNombre().toUpperCase());
        precio.setText(zapato.getPrecioTexto());
        int total = zapato.getStockTotal();
        estadoStock.setText(total == 0 ? "AGOTADO" : "EN STOCK · " + total + (total == 1 ? " PAR" : " PARES"));
        filaColores.getChildren().clear();
        for (String color : zapato.getColores().split("·")) {
            Circle punto = new Circle(11, TarjetaZapato.colorDe(color.trim()));
            punto.setStroke(Iconos.OSCURO);
            punto.setStrokeWidth(2);
            filaColores.getChildren().add(punto);
        }
        Label colores = new Label(zapato.getColores());
        colores.getStyleClass().add("subtitulo");
        filaColores.getChildren().add(colores);
        descripcion.setText(zapato.getDescripcion());
        panelTallas.getChildren().clear();
        ToggleButton primera = null;
        for (TallaStock talla : zapato.getTallas()) {
            ToggleButton boton = new ToggleButton(String.valueOf(talla.getTalla()));
            boton.getStyleClass().add("talla");
            boton.setUserData(talla);
            boton.setToggleGroup(grupoTallas);
            boton.setDisable(talla.getStock() == 0);
            panelTallas.getChildren().add(boton);
            if (primera == null && talla.getStock() > 0) {
                primera = boton;
            }
        }
        if (primera != null) {
            primera.setSelected(true);
        }
        botonCarrito.setDisable(zapato.estaAgotado());
        actualizarStockTalla();
    }

    private void actualizarStockTalla() {
        if (zapato == null) {
            return;
        }
        if (grupoTallas.getSelectedToggle() == null) {
            pastillaStock.setText(zapato.estaAgotado() ? "Sin stock" : "Elige una talla");
            textoStock.setText(zapato.estaAgotado() ? "Este par está agotado en todas las tallas." : "");
            return;
        }
        TallaStock talla = (TallaStock) grupoTallas.getSelectedToggle().getUserData();
        int stock = talla.getStock();
        pastillaStock.setText(stock == 1 ? "Queda 1 par" : "Quedan " + stock + " pares");
        ArrayList<String> agotadas = new ArrayList<>();
        for (TallaStock t : zapato.getTallas()) {
            if (t.getStock() == 0) {
                agotadas.add(String.valueOf(t.getTalla()));
            }
        }
        String extra = agotadas.isEmpty() ? "" : " · Agotadas: " + String.join(", ", agotadas) + ".";
        textoStock.setText("en talla " + talla.getTalla() + extra);
    }

    private void pintarVistos() {
        panelVistos.getChildren().clear();
        ArrayList<Zapato> vistos = SesionActual.getInstancia().getVistosRecientemente().aArrayList();
        for (int i = 0; i < vistos.size() && i < MAXIMO_VISTOS; i++) {
            Zapato visto = vistos.get(i);
            panelVistos.getChildren().add(crearVisto(visto, i == 0));
        }
    }

    private HBox crearVisto(Zapato visto, boolean actual) {
        StackPane miniatura = new StackPane(Recursos.vistaCubierta(Recursos.imagenProducto(visto), 112, 90));
        Rectangle recorte = new Rectangle(112, 90);
        recorte.setArcWidth(18);
        recorte.setArcHeight(18);
        miniatura.setClip(recorte);
        Label insignia = new Label(actual ? "VIENDO AHORA" : "VISTO ANTES");
        insignia.getStyleClass().add(actual ? "pastilla-naranja" : "pastilla");
        Label nombreVisto = new Label(visto.getNombre().toUpperCase());
        nombreVisto.getStyleClass().add("nombre-tarjeta");
        nombreVisto.setMaxWidth(190);
        nombreVisto.setWrapText(true);
        Label precioVisto = new Label(visto.getPrecioTexto());
        precioVisto.getStyleClass().addAll("texto", "negrita");
        VBox datos = new VBox(6, insignia, nombreVisto, precioVisto);
        HBox tarjeta = new HBox(miniatura, datos);
        tarjeta.getStyleClass().add("visto");
        tarjeta.setAlignment(Pos.CENTER_LEFT);
        if (!actual) {
            tarjeta.setOnMouseClicked(e -> mostrar(visto));
        }
        return tarjeta;
    }

    @FXML
    private void volver() {
        Navegador.mostrar(Ventana.CATALOGO);
    }
}
