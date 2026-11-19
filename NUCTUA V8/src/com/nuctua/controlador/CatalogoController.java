package com.nuctua.controlador;

import com.nuctua.estructuras.ListaEnlazada;
import com.nuctua.modelo.Zapato;
import com.nuctua.persistencia.ArchivoProductos;
import com.nuctua.util.Iconos;
import com.nuctua.util.Recursos;
import com.nuctua.vista.Ventana;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Slider;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.TilePane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;

public class CatalogoController {

    private static final int[] TALLAS = {38, 39, 40, 41, 42, 43, 44, 45};
    private static final String[] COLORES = {"#B5DBE6", "#1B1410", "#C8202A", "#FFFFFF"};

    @FXML
    private BarraClienteController barraController;
    @FXML
    private ScrollPane scroll;
    @FXML
    private StackPane iconoBuscar;
    @FXML
    private TextField campoBusqueda;
    @FXML
    private Button botonBuscar;
    @FXML
    private Label textoHero;
    @FXML
    private Button botonNuevo;
    @FXML
    private StackPane fotoHero;
    @FXML
    private ToggleGroup grupoMarcas;
    @FXML
    private Hyperlink enlaceLimpiar;
    @FXML
    private CheckBox checkNike;
    @FXML
    private CheckBox checkJordan;
    @FXML
    private CheckBox checkBalenciaga;
    @FXML
    private CheckBox checkYeezyOff;
    @FXML
    private TilePane panelTallas;
    @FXML
    private HBox panelColores;
    @FXML
    private Slider sliderPrecio;
    @FXML
    private Label etiquetaPrecioMaximo;
    @FXML
    private Label tituloResultados;
    @FXML
    private Button botonOrdenar;
    @FXML
    private FlowPane grilla;
    @FXML
    private Label mensajeVacio;

    protected ListaEnlazada<Zapato> catalogo;

    @FXML
    private void initialize() {
        barraController.marcar(Ventana.CATALOGO);
        iconoBuscar.getChildren().add(Iconos.buscar(Iconos.OSCURO, 20));
        catalogo = new ArchivoProductos().cargar();
        int menor = Integer.MAX_VALUE;
        int activos = 0;
        for (Zapato zapato : catalogo) {
            if (zapato.isActivo()) {
                activos++;
                menor = Math.min(menor, zapato.getPrecio());
            }
        }
        if (activos > 0) {
            textoHero.setText("Dunk, SB, Jordan, Yeezy, Balenciaga y Off-White desde " + Zapato.formatearPrecio(menor) + ".");
        }
        tituloResultados.setText("RESULTADOS · " + activos + " MODELOS");
        Zapato destacado = catalogo.buscar(z -> z.getSku().equals("NCT-003"));
        if (destacado != null) {
            fotoHero.getChildren().add(Recursos.vistaCubierta(Recursos.imagenProducto(destacado), 292, 252));
        }
        for (int talla : TALLAS) {
            ToggleButton boton = new ToggleButton(String.valueOf(talla));
            boton.getStyleClass().add("talla");
            panelTallas.getChildren().add(boton);
        }
        for (String color : COLORES) {
            Circle muestra = new Circle(15, Color.web(color));
            muestra.setStroke(Iconos.OSCURO);
            muestra.setStrokeWidth(2);
            panelColores.getChildren().add(muestra);
        }
        sliderPrecio.valueProperty().addListener((obs, antes, ahora) ->
                etiquetaPrecioMaximo.setText(Zapato.formatearPrecio(redondear(ahora.doubleValue()))));
        grupoMarcas.selectedToggleProperty().addListener((obs, antes, ahora) -> {
            if (ahora == null) {
                grupoMarcas.selectToggle(antes);
            }
        });
    }

    private int redondear(double valor) {
        return (int) (Math.round(valor / 10000.0) * 10000);
    }
}
