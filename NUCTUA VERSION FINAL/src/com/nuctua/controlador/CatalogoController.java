package com.nuctua.controlador;

import com.nuctua.modelo.Zapato;
import com.nuctua.servicio.CriterioOrden;
import com.nuctua.servicio.FiltroCatalogo;
import com.nuctua.util.Iconos;
import com.nuctua.util.Recursos;
import com.nuctua.vista.Ventana;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Toggle;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.StackPane;

public class CatalogoController extends ResultadosBaseController {

    @FXML
    private BarraClienteController barraController;
    @FXML
    private ScrollPane scroll;
    @FXML
    private StackPane iconoBuscar;
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
    private ToggleButton chipTodos;
    @FXML
    private ToggleButton chipNike;
    @FXML
    private ToggleButton chipJordan;
    @FXML
    private ToggleButton chipYeezy;
    @FXML
    private ToggleButton chipBalenciaga;
    @FXML
    private ToggleButton chipOffWhite;

    @FXML
    private void initialize() {
        barraController.marcar(Ventana.CATALOGO);
        iconoBuscar.getChildren().add(Iconos.buscar(Iconos.OSCURO, 20));
        textoHero.setText("Dunk, SB, Jordan, Yeezy, Balenciaga y Off-White desde "
                + Zapato.formatearPrecio(servicio.precioMinimo()) + ".");
        Zapato destacado = servicio.buscarPorSku("NCT-003");
        if (destacado != null) {
            fotoHero.getChildren().add(Recursos.vistaCubierta(Recursos.imagenProducto(destacado), 292, 282));
        }
        prepararChips();
        prepararResultados();
        botonBuscar.setOnAction(e -> confirmarBusqueda());
        botonNuevo.setOnAction(e -> {
            orden = CriterioOrden.MAS_NUEVOS;
            aplicar();
            scroll.setVvalue(0.45);
        });
        aplicar();
    }

    private void prepararChips() {
        chipTodos.setUserData(null);
        chipNike.setUserData(FiltroCatalogo.NIKE);
        chipJordan.setUserData(FiltroCatalogo.JORDAN);
        chipYeezy.setUserData(FiltroCatalogo.YEEZY);
        chipBalenciaga.setUserData(FiltroCatalogo.BALENCIAGA);
        chipOffWhite.setUserData(FiltroCatalogo.OFF_WHITE);
        grupoMarcas.selectedToggleProperty().addListener((obs, antes, ahora) -> {
            if (ahora == null) {
                grupoMarcas.selectToggle(antes);
                return;
            }
            filtro.setGrupoChip((String) ahora.getUserData());
            aplicar();
        });
    }

    @Override
    protected void limpiarExtra() {
        chipTodos.setSelected(true);
    }

    @Override
    protected void agregarFiltrosExtra() {
        Toggle chip = grupoMarcas.getSelectedToggle();
        if (chip != null && chip != chipTodos) {
            agregarChip(((ToggleButton) chip).getText(), () -> chipTodos.setSelected(true));
        }
    }

    public void buscar(String texto) {
        buscarTexto(texto == null ? "" : texto);
    }
}
