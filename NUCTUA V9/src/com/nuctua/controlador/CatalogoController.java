package com.nuctua.controlador;

import com.nuctua.modelo.Zapato;
import com.nuctua.servicio.CatalogoServicio;
import com.nuctua.servicio.CriterioOrden;
import com.nuctua.servicio.FiltroCatalogo;
import com.nuctua.util.Iconos;
import com.nuctua.util.Recursos;
import com.nuctua.vista.TarjetaZapato;
import com.nuctua.vista.Ventana;
import java.util.ArrayList;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Slider;
import javafx.scene.control.TextField;
import javafx.scene.control.Toggle;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.TilePane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;

public class CatalogoController {

    private static final int[] TALLAS = {38, 39, 40, 41, 42, 43, 44, 45};
    private static final String[][] COLORES = {{"Azul", "#B5DBE6"}, {"Negro", "#1B1410"}, {"Rojo", "#C8202A"}, {"Blanco", "#FFFFFF"}};

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

    private final CatalogoServicio servicio = new CatalogoServicio();
    private final FiltroCatalogo filtro = new FiltroCatalogo();
    private final ToggleGroup grupoTallas = new ToggleGroup();
    private final ToggleGroup grupoColores = new ToggleGroup();
    private CriterioOrden orden = CriterioOrden.PRECIO_MENOR;
    private ArrayList<Zapato> visibles = new ArrayList<>();
    private boolean reiniciando;

    @FXML
    private void initialize() {
        barraController.marcar(Ventana.CATALOGO);
        iconoBuscar.getChildren().add(Iconos.buscar(Iconos.OSCURO, 20));
        textoHero.setText("Dunk, SB, Jordan, Yeezy, Balenciaga y Off-White desde "
                + Zapato.formatearPrecio(servicio.precioMinimo()) + ".");
        Zapato destacado = servicio.buscarPorSku("NCT-003");
        if (destacado != null) {
            fotoHero.getChildren().add(Recursos.vistaCubierta(Recursos.imagenProducto(destacado), 292, 252));
        }
        prepararChips();
        prepararMarcas();
        prepararTallas();
        prepararColores();
        sliderPrecio.valueProperty().addListener((obs, antes, ahora) -> {
            int maximo = redondear(ahora.doubleValue());
            etiquetaPrecioMaximo.setText(Zapato.formatearPrecio(maximo));
            filtro.setPrecioMaximo(maximo >= (int) sliderPrecio.getMax() ? Integer.MAX_VALUE : maximo);
            aplicar();
        });
        campoBusqueda.textProperty().addListener((obs, antes, ahora) -> {
            filtro.setTexto(ahora);
            aplicar();
        });
        campoBusqueda.setOnAction(e -> aplicar());
        botonBuscar.setOnAction(e -> {
            filtro.setTexto(campoBusqueda.getText());
            aplicar();
        });
        botonNuevo.setOnAction(e -> {
            orden = CriterioOrden.MAS_NUEVOS;
            aplicar();
            scroll.setVvalue(0.45);
        });
        botonOrdenar.setOnAction(e -> elegirOrden());
        enlaceLimpiar.setOnAction(e -> limpiar());
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

    private void prepararMarcas() {
        vincular(checkNike, FiltroCatalogo.NIKE);
        vincular(checkJordan, FiltroCatalogo.JORDAN);
        vincular(checkBalenciaga, FiltroCatalogo.BALENCIAGA);
        vincular(checkYeezyOff, FiltroCatalogo.YEEZY_OFF_WHITE);
    }

    private void vincular(CheckBox casilla, String grupo) {
        casilla.selectedProperty().addListener((obs, antes, ahora) -> {
            filtro.marcarGrupo(grupo, ahora);
            aplicar();
        });
    }

    private void prepararTallas() {
        for (int talla : TALLAS) {
            ToggleButton boton = new ToggleButton(String.valueOf(talla));
            boton.getStyleClass().add("talla");
            boton.setUserData(talla);
            boton.setToggleGroup(grupoTallas);
            panelTallas.getChildren().add(boton);
        }
        grupoTallas.selectedToggleProperty().addListener((obs, antes, ahora) -> {
            filtro.setTalla(ahora == null ? 0 : (Integer) ahora.getUserData());
            aplicar();
        });
    }

    private void prepararColores() {
        for (String[] color : COLORES) {
            Circle muestra = new Circle(13, Color.web(color[1]));
            muestra.setStroke(Iconos.OSCURO);
            muestra.setStrokeWidth(2);
            ToggleButton boton = new ToggleButton();
            boton.getStyleClass().setAll("muestra-color");
            boton.setGraphic(muestra);
            boton.setUserData(color[0]);
            boton.setToggleGroup(grupoColores);
            boton.setTooltip(new Tooltip(color[0]));
            panelColores.getChildren().add(boton);
        }
        grupoColores.selectedToggleProperty().addListener((obs, antes, ahora) -> {
            filtro.setColor(ahora == null ? null : (String) ahora.getUserData());
            aplicar();
        });
    }

    private void elegirOrden() {
        PopupOrdenarCatalogoController popup = Popup.abrir(Ventana.POPUP_ORDENAR_CATALOGO);
        popup.setSeleccion(orden);
        popup.mostrarYEsperar();
        if (popup.getElegido() != null) {
            orden = popup.getElegido();
            aplicar();
        }
    }

    private void limpiar() {
        reiniciando = true;
        filtro.limpiar();
        campoBusqueda.clear();
        chipTodos.setSelected(true);
        checkNike.setSelected(false);
        checkJordan.setSelected(false);
        checkBalenciaga.setSelected(false);
        checkYeezyOff.setSelected(false);
        Toggle talla = grupoTallas.getSelectedToggle();
        if (talla != null) {
            talla.setSelected(false);
        }
        Toggle color = grupoColores.getSelectedToggle();
        if (color != null) {
            color.setSelected(false);
        }
        sliderPrecio.setValue(sliderPrecio.getMax());
        orden = CriterioOrden.PRECIO_MENOR;
        reiniciando = false;
        aplicar();
    }

    private void aplicar() {
        if (reiniciando) {
            return;
        }
        visibles = servicio.filtrar(filtro);
        servicio.ordenar(visibles, orden);
        botonOrdenar.setText("Ordenar: " + orden.getEtiqueta() + "  ▾");
        renderizar(visibles);
    }

    private void renderizar(ArrayList<Zapato> zapatos) {
        grilla.getChildren().clear();
        for (Zapato zapato : zapatos) {
            TarjetaZapato tarjeta = new TarjetaZapato(zapato);
            tarjeta.setOnMouseClicked(e -> abrirDetalle(zapato));
            grilla.getChildren().add(tarjeta);
        }
        tituloResultados.setText("RESULTADOS · " + zapatos.size() + (zapatos.size() == 1 ? " MODELO" : " MODELOS"));
        boolean vacio = zapatos.isEmpty();
        mensajeVacio.setText("No encontramos pares con esos criterios. Prueba con otra búsqueda o limpia los filtros.");
        mensajeVacio.setVisible(vacio);
        mensajeVacio.setManaged(vacio);
    }

    private void abrirDetalle(Zapato zapato) {
        DetalleProductoController detalle = Navegador.mostrar(Ventana.DETALLE_PRODUCTO);
        if (detalle != null) {
            detalle.mostrar(zapato);
        }
    }

    public ArrayList<Zapato> getVisibles() {
        return visibles;
    }

    private int redondear(double valor) {
        return (int) (Math.round(valor / 10000.0) * 10000);
    }
}
