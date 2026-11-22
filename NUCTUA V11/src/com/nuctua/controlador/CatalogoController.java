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
import javafx.geometry.Pos;
import javafx.scene.control.ContentDisplay;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import com.nuctua.util.RangoPrecio;
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
    private RangoPrecio rangoPrecio;
    @FXML
    private Label etiquetaPrecioMinimo;
    @FXML
    private Label etiquetaPrecioMaximo;
    @FXML
    private Label tituloResultados;
    @FXML
    private Label infoOrden;
    @FXML
    private Button botonOrdenar;
    @FXML
    private FlowPane filtrosActivos;
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
            fotoHero.getChildren().add(Recursos.vistaCubierta(Recursos.imagenProducto(destacado), 292, 282));
        }
        prepararChips();
        prepararMarcas();
        prepararTallas();
        prepararColores();
        rangoPrecio.minimoProperty().addListener((obs, antes, ahora) -> {
            int minimo = (int) ahora.doubleValue();
            etiquetaPrecioMinimo.setText(Zapato.formatearPrecio(minimo));
            filtro.setPrecioMinimo(minimo <= (int) rangoPrecio.getLimiteInferior() ? 0 : minimo);
            aplicar();
        });
        rangoPrecio.maximoProperty().addListener((obs, antes, ahora) -> {
            int maximo = (int) ahora.doubleValue();
            etiquetaPrecioMaximo.setText(Zapato.formatearPrecio(maximo));
            filtro.setPrecioMaximo(maximo >= (int) rangoPrecio.getLimiteSuperior() ? Integer.MAX_VALUE : maximo);
            aplicar();
        });
        grilla.widthProperty().addListener((obs, antes, ahora) -> ajustarTarjetas());
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
            boolean disponible = servicio.hayStockEnTalla(talla);
            boton.setDisable(!disponible);
            Tooltip.install(boton, new Tooltip(disponible ? "Talla " + talla : "Sin stock en talla " + talla));
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
        rangoPrecio.reiniciar();
        orden = CriterioOrden.PRECIO_MENOR;
        reiniciando = false;
        aplicar();
    }

    private void aplicar() {
        if (reiniciando) {
            return;
        }
        visibles = servicio.filtrar(filtro);
        int comparaciones = servicio.ordenar(visibles, orden);
        botonOrdenar.setText("Ordenar: " + orden.getEtiquetaCorta() + "  ▾");
        infoOrden.setText("Ordenado con " + orden.getMetodo() + " · " + comparaciones
                + (comparaciones == 1 ? " comparación" : " comparaciones"));
        renderizar(visibles);
    }

    private void renderizar(ArrayList<Zapato> zapatos) {
        grilla.getChildren().clear();
        for (Zapato zapato : zapatos) {
            TarjetaZapato tarjeta = new TarjetaZapato(zapato);
            tarjeta.setResaltado(filtro.getTexto());
            tarjeta.setOnMouseClicked(e -> abrirDetalle(zapato));
            tarjeta.getBotonAgregar().setOnAction(e -> abrirDetalle(zapato));
            grilla.getChildren().add(tarjeta);
        }
        boolean hayFiltros = pintarFiltrosActivos();
        if (hayFiltros) {
            grilla.getChildren().add(crearSugerencia(zapatos.isEmpty()));
        }
        ajustarTarjetas();
        String texto = filtro.getTexto();
        if (texto.isEmpty()) {
            tituloResultados.setText("RESULTADOS · " + zapatos.size() + (zapatos.size() == 1 ? " MODELO" : " MODELOS"));
        } else {
            tituloResultados.setText(zapatos.size() + (zapatos.size() == 1 ? " RESULTADO" : " RESULTADOS")
                    + " PARA “" + texto.toUpperCase() + "”");
        }
        mensajeVacio.setVisible(false);
        mensajeVacio.setManaged(false);
    }

    private boolean pintarFiltrosActivos() {
        filtrosActivos.getChildren().clear();
        if (!filtro.getTexto().isEmpty()) {
            agregarChip("“" + filtro.getTexto() + "”", () -> campoBusqueda.clear());
        }
        Toggle chip = grupoMarcas.getSelectedToggle();
        if (chip != null && chip != chipTodos) {
            agregarChip(((ToggleButton) chip).getText(), () -> chipTodos.setSelected(true));
        }
        for (CheckBox casilla : new CheckBox[]{checkNike, checkJordan, checkBalenciaga, checkYeezyOff}) {
            if (casilla.isSelected()) {
                agregarChip(casilla.getText(), () -> casilla.setSelected(false));
            }
        }
        Toggle talla = grupoTallas.getSelectedToggle();
        if (talla != null) {
            agregarChip("Talla " + talla.getUserData(), () -> talla.setSelected(false));
        }
        Toggle color = grupoColores.getSelectedToggle();
        if (color != null) {
            agregarChip("Color " + color.getUserData(), () -> color.setSelected(false));
        }
        if (filtro.getPrecioMinimo() > 0) {
            agregarChip("Desde " + Zapato.formatearPrecio(filtro.getPrecioMinimo()),
                    () -> rangoPrecio.setMinimo(rangoPrecio.getLimiteInferior()));
        }
        if (filtro.getPrecioMaximo() != Integer.MAX_VALUE) {
            agregarChip("Hasta " + Zapato.formatearPrecio(filtro.getPrecioMaximo()),
                    () -> rangoPrecio.setMaximo(rangoPrecio.getLimiteSuperior()));
        }
        boolean hay = !filtrosActivos.getChildren().isEmpty();
        if (hay) {
            Hyperlink todo = new Hyperlink("Limpiar todo");
            todo.getStyleClass().add("enlace-texto");
            todo.setOnAction(e -> limpiar());
            filtrosActivos.getChildren().add(todo);
        }
        filtrosActivos.setVisible(hay);
        filtrosActivos.setManaged(hay);
        return hay;
    }

    private void agregarChip(String texto, Runnable quitar) {
        Button chip = new Button(texto + "   ×");
        chip.getStyleClass().add("chip-filtro");
        chip.setOnAction(e -> quitar.run());
        filtrosActivos.getChildren().add(chip);
    }

    private VBox crearSugerencia(boolean sinResultados) {
        Label titulo = new Label(sinResultados ? "SIN RESULTADOS" : "¿NO ES LO QUE BUSCAS?");
        titulo.getStyleClass().add("titulo-chico");
        titulo.setWrapText(true);
        titulo.setStyle("-fx-text-alignment: center;");
        Label texto = new Label(sinResultados
                ? "Ningún par cumple con esos filtros. Quita alguno o mira todo el catálogo."
                : "Mira los " + servicio.activos().size() + " pares del catálogo y filtra por marca, talla o precio.");
        texto.getStyleClass().add("texto");
        texto.setWrapText(true);
        texto.setMinHeight(Region.USE_PREF_SIZE);
        texto.setStyle("-fx-text-alignment: center;");
        Button ver = new Button("VER CATÁLOGO");
        ver.getStyleClass().add("boton-primario");
        ver.setGraphic(Iconos.flechaDerecha(Iconos.CREMA, 16));
        ver.setContentDisplay(ContentDisplay.RIGHT);
        ver.setOnAction(e -> limpiar());
        HBox huellas = new HBox(10, Iconos.huellaPequena(Color.web("#8C7B6B"), -20), Iconos.huellaPequena(Iconos.OSCURO, -20));
        huellas.setAlignment(Pos.CENTER);
        VBox tarjeta = new VBox(huellas, titulo, texto, ver);
        tarjeta.getStyleClass().addAll("tarjeta-punteada", "tarjeta-sugerencia");
        tarjeta.setMinHeight(300);
        return tarjeta;
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

    private void ajustarTarjetas() {
        double disponible = grilla.getWidth();
        if (disponible <= 0) {
            return;
        }
        double ancho = (disponible - 2 * grilla.getHgap() - 8) / 3;
        for (Node nodo : grilla.getChildren()) {
            if (nodo instanceof TarjetaZapato tarjeta) {
                tarjeta.ajustarAncho(ancho);
            } else if (nodo instanceof VBox sugerencia) {
                double fijo = Math.max(240, Math.floor(ancho));
                sugerencia.setMinWidth(fijo);
                sugerencia.setPrefWidth(fijo);
                sugerencia.setMaxWidth(fijo);
            }
        }
    }
}
