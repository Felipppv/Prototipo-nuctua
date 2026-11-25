package com.nuctua.controlador;

import com.nuctua.modelo.SesionActual;
import com.nuctua.util.Iconos;
import com.nuctua.vista.Ventana;
import java.util.ArrayList;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.control.Tooltip;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;

public class BuscarZapatosController extends ResultadosBaseController {

    private static final String[][] POPULARES = {{"Dunk / SB", "Dunk"}, {"Jordan", "Jordan"}, {"Yeezy", "Yeezy"}, {"Balenciaga", "Balenciaga"}};

    @FXML
    private BarraClienteController barraController;
    @FXML
    private Hyperlink enlaceVolver;
    @FXML
    private StackPane iconoBuscar;
    @FXML
    private Button botonBorrar;
    @FXML
    private Button botonBuscar;
    @FXML
    private FlowPane panelRecientes;
    @FXML
    private FlowPane panelPopulares;

    @FXML
    private void initialize() {
        barraController.marcar(Ventana.BUSCAR_ZAPATOS);
        enlaceVolver.setGraphic(Iconos.flechaIzquierda(Iconos.OSCURO, 20));
        enlaceVolver.setText(Navegador.textoVolver());
        enlaceVolver.setOnAction(e -> Navegador.volver());
        iconoBuscar.getChildren().add(Iconos.buscar(Iconos.OSCURO, 28));
        botonBorrar.setGraphic(Iconos.cerrar(Iconos.OSCURO, 18));
        botonBorrar.setTooltip(new Tooltip("Borrar búsqueda"));
        botonBorrar.setOnAction(e -> {
            campoBusqueda.clear();
            campoBusqueda.requestFocus();
        });
        botonBuscar.setGraphic(Iconos.flechaDerecha(Iconos.CREMA, 20));
        botonBuscar.setContentDisplay(ContentDisplay.RIGHT);
        botonBuscar.setOnAction(e -> confirmarBusqueda());
        prepararPopulares();
        prepararResultados();
        aplicar();
        Platform.runLater(campoBusqueda::requestFocus);
    }

    private void prepararPopulares() {
        for (String[] popular : POPULARES) {
            HBox chip = crearChip(Iconos.tendencia(Iconos.OSCURO, 18), popular[0], null);
            chip.setOnMouseClicked(e -> elegirSugerencia(popular[1]));
            panelPopulares.getChildren().add(chip);
        }
    }

    private void pintarRecientes() {
        panelRecientes.getChildren().clear();
        ArrayList<String> recientes = SesionActual.getInstancia().getBusquedasRecientes().aArrayList();
        if (recientes.isEmpty()) {
            Label vacio = new Label("Tus últimas búsquedas aparecerán aquí.");
            vacio.getStyleClass().add("texto-suave");
            panelRecientes.getChildren().add(vacio);
            return;
        }
        for (String reciente : recientes) {
            HBox chip = crearChip(Iconos.reloj(Iconos.OSCURO, 18), reciente, () -> {
                SesionActual.getInstancia().quitarBusqueda(reciente);
                pintarRecientes();
            });
            chip.setOnMouseClicked(e -> elegirSugerencia(reciente));
            panelRecientes.getChildren().add(chip);
        }
    }

    private HBox crearChip(Node icono, String texto, Runnable quitar) {
        Label etiqueta = new Label(texto);
        etiqueta.getStyleClass().add("chip-sugerencia-texto");
        HBox chip = new HBox(icono, etiqueta);
        chip.getStyleClass().add("chip-sugerencia");
        if (quitar != null) {
            Button boton = new Button();
            boton.setGraphic(Iconos.cerrar(Iconos.OSCURO, 14));
            boton.getStyleClass().add("chip-sugerencia-quitar");
            boton.setTooltip(new Tooltip("Quitar de recientes"));
            boton.setOnAction(e -> quitar.run());
            boton.addEventHandler(MouseEvent.MOUSE_CLICKED, MouseEvent::consume);
            chip.getChildren().add(boton);
        }
        return chip;
    }

    private void elegirSugerencia(String texto) {
        SesionActual.getInstancia().registrarBusqueda(texto);
        buscarTexto(texto);
        pintarRecientes();
    }

    @Override
    protected void confirmarBusqueda() {
        super.confirmarBusqueda();
        pintarRecientes();
    }

    @Override
    protected void alAplicar() {
        boolean hayTexto = !campoBusqueda.getText().isEmpty();
        botonBorrar.setVisible(hayTexto);
        botonBorrar.setManaged(hayTexto);
        if (panelRecientes.getChildren().isEmpty()) {
            pintarRecientes();
        }
    }

    @Override
    protected void verCatalogoCompleto() {
        Navegador.mostrar(Ventana.CATALOGO);
    }

    public void buscar(String texto) {
        buscarTexto(texto == null ? "" : texto);
    }
}
