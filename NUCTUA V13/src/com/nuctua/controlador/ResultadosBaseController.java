package com.nuctua.controlador;

import com.nuctua.algoritmos.ResultadoBusqueda;
import com.nuctua.modelo.SesionActual;
import com.nuctua.modelo.Zapato;
import com.nuctua.servicio.CatalogoServicio;
import com.nuctua.servicio.CriterioOrden;
import com.nuctua.servicio.FiltroCatalogo;
import com.nuctua.util.Iconos;
import com.nuctua.util.RangoPrecio;
import com.nuctua.vista.TarjetaZapato;
import com.nuctua.vista.Ventana;
import java.util.ArrayList;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.Toggle;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.layout.TilePane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;

public abstract class ResultadosBaseController {

    private static final int[] TALLAS = {38, 39, 40, 41, 42, 43, 44, 45};
    private static final String[][] COLORES = {{"Azul", "#B5DBE6"}, {"Negro", "#1B1410"}, {"Rojo", "#C8202A"}, {"Blanco", "#FFFFFF"}};

    @FXML
    protected TextField campoBusqueda;
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
    private Label infoAlgoritmos;
    @FXML
    private Button botonOrdenar;
    @FXML
    private FlowPane filtrosActivos;
    @FXML
    private FlowPane grilla;

    protected final CatalogoServicio servicio = new CatalogoServicio();
    protected final FiltroCatalogo filtro = new FiltroCatalogo();
    private final ToggleGroup grupoTallas = new ToggleGroup();
    private final ToggleGroup grupoColores = new ToggleGroup();
    protected CriterioOrden orden = CriterioOrden.PRECIO_MENOR;
    private ArrayList<Zapato> visibles = new ArrayList<>();
    private ResultadoBusqueda<Zapato> ultimaBusqueda = new ResultadoBusqueda<>();
    private int ultimasComparacionesOrden;
    private boolean reiniciando;

    protected void prepararResultados() {
        vincular(checkNike, FiltroCatalogo.NIKE);
        vincular(checkJordan, FiltroCatalogo.JORDAN);
        vincular(checkBalenciaga, FiltroCatalogo.BALENCIAGA);
        vincular(checkYeezyOff, FiltroCatalogo.YEEZY_OFF_WHITE);
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
        campoBusqueda.setOnAction(e -> confirmarBusqueda());
        botonOrdenar.setOnAction(e -> elegirOrden());
        enlaceLimpiar.setOnAction(e -> limpiar());
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

    protected void confirmarBusqueda() {
        filtro.setTexto(campoBusqueda.getText());
        SesionActual.getInstancia().registrarBusqueda(campoBusqueda.getText());
        aplicar();
    }

    protected void buscarTexto(String texto) {
        if (texto.equals(campoBusqueda.getText())) {
            aplicar();
        } else {
            campoBusqueda.setText(texto);
        }
        campoBusqueda.positionCaret(texto.length());
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

    protected void limpiar() {
        reiniciando = true;
        filtro.limpiar();
        campoBusqueda.clear();
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
        limpiarExtra();
        reiniciando = false;
        aplicar();
    }

    protected void limpiarExtra() {
    }

    protected void agregarFiltrosExtra() {
    }

    protected void alAplicar() {
    }

    protected void aplicar() {
        if (reiniciando) {
            return;
        }
        ultimaBusqueda = servicio.buscar(filtro);
        visibles = ultimaBusqueda.getEncontrados();
        ultimasComparacionesOrden = servicio.ordenar(visibles, orden);
        botonOrdenar.setText("Ordenar: " + orden.getEtiquetaCorta() + "  ▾");
        infoAlgoritmos.setText("Búsqueda " + ultimaBusqueda.getDescripcion().toLowerCase() + " · "
                + plural(ultimaBusqueda.getComparaciones(), "comparación", "comparaciones")
                + "   ·   Orden con " + orden.getMetodo() + " · "
                + plural(ultimasComparacionesOrden, "comparación", "comparaciones"));
        renderizar();
        alAplicar();
    }

    private static String plural(int cantidad, String singular, String varios) {
        return cantidad + " " + (cantidad == 1 ? singular : varios);
    }

    private void renderizar() {
        grilla.getChildren().clear();
        for (Zapato zapato : visibles) {
            TarjetaZapato tarjeta = new TarjetaZapato(zapato);
            tarjeta.setResaltado(filtro.getTexto());
            tarjeta.setOnMouseClicked(e -> abrirDetalle(zapato));
            tarjeta.getBotonAgregar().setOnAction(e -> abrirDetalle(zapato));
            grilla.getChildren().add(tarjeta);
        }
        boolean hayFiltros = pintarFiltrosActivos();
        if (hayFiltros || visibles.isEmpty()) {
            grilla.getChildren().add(crearSugerencia(visibles.isEmpty()));
        }
        ajustarTarjetas();
        String texto = filtro.getTexto();
        int total = visibles.size();
        if (texto.isEmpty()) {
            tituloResultados.setText("RESULTADOS · " + total + (total == 1 ? " MODELO" : " MODELOS"));
        } else {
            tituloResultados.setText(total + (total == 1 ? " RESULTADO" : " RESULTADOS")
                    + " PARA “" + texto.toUpperCase() + "”");
        }
    }

    private boolean pintarFiltrosActivos() {
        filtrosActivos.getChildren().clear();
        if (!filtro.getTexto().isEmpty()) {
            agregarChip("“" + filtro.getTexto() + "”", () -> campoBusqueda.clear());
        }
        agregarFiltrosExtra();
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

    protected void agregarChip(String texto, Runnable quitar) {
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
        ver.setOnAction(e -> verCatalogoCompleto());
        HBox huellas = new HBox(10, Iconos.huellaPequena(Color.web("#8C7B6B"), -20), Iconos.huellaPequena(Iconos.OSCURO, -20));
        huellas.setAlignment(Pos.CENTER);
        VBox tarjeta = new VBox(huellas, titulo, texto, ver);
        tarjeta.getStyleClass().addAll("tarjeta-punteada", "tarjeta-sugerencia");
        tarjeta.setMinHeight(300);
        return tarjeta;
    }

    protected void verCatalogoCompleto() {
        limpiar();
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

    public ResultadoBusqueda<Zapato> getUltimaBusqueda() {
        return ultimaBusqueda;
    }

    public int getUltimasComparacionesOrden() {
        return ultimasComparacionesOrden;
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
