package com.nuctua.controlador;

import com.nuctua.estructuras.CursorDoble;
import com.nuctua.modelo.TallaStock;
import com.nuctua.modelo.Zapato;
import com.nuctua.servicio.CarritoServicio;
import com.nuctua.servicio.DeseosServicio;
import com.nuctua.servicio.OrdenDeseos;
import com.nuctua.servicio.ResultadoSeleccion;
import com.nuctua.servicio.SeleccionServicio;
import com.nuctua.util.Iconos;
import com.nuctua.vista.TarjetaDeseo;
import com.nuctua.vista.Ventana;
import java.util.ArrayList;
import javafx.fxml.FXML;
import javafx.geometry.Bounds;
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
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
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
    private HBox aviso;
    @FXML
    private Label textoAviso;
    @FXML
    private Hyperlink enlaceDeshacer;
    @FXML
    private HBox barraRecorrido;
    @FXML
    private Button botonAnterior;
    @FXML
    private Label textoRecorrido;
    @FXML
    private Button botonSiguiente;
    @FXML
    private FlowPane grilla;

    private final DeseosServicio deseos = new DeseosServicio();
    private final SeleccionServicio seleccion = new SeleccionServicio();
    private final ContextMenu menuOrden = new ContextMenu();
    private final ContextMenu menuTallas = new ContextMenu();
    private OrdenDeseos orden = OrdenDeseos.MAS_RECIENTES;
    private CursorDoble<Zapato> cursor;
    private Zapato ultimoQuitado;
    private int posicionQuitado;

    @FXML
    private void initialize() {
        barraController.marcar(Ventana.LISTA_DESEOS);
        enlaceVolver.setGraphic(Iconos.flechaIzquierda(Iconos.OSCURO, 20));
        enlaceVolver.setText(Navegador.textoVolver());
        enlaceVolver.setOnAction(e -> Navegador.volver());
        prepararOrden();
        prepararRecorrido();
        enlaceDeshacer.setOnAction(e -> deshacer());
        grilla.widthProperty().addListener((obs, antes, ahora) -> ajustar());
        ArrayList<String> descartados = deseos.sincronizar();
        barraController.actualizarContadores();
        cursor = deseos.cursor();
        pintar();
        if (!descartados.isEmpty()) {
            mostrarAviso(descartados.size() == 1
                    ? "1 par ya no existe en el catálogo y salió de tu lista."
                    : descartados.size() + " pares ya no existen en el catálogo y salieron de tu lista.", false);
        }
    }

    private void prepararOrden() {
        botonOrdenar.setGraphic(Iconos.flechaAbajo(Iconos.OSCURO, 18));
        botonOrdenar.setContentDisplay(ContentDisplay.RIGHT);
        botonOrdenar.setGraphicTextGap(24);
        menuOrden.getStyleClass().add("menu-orden");
        menuTallas.getStyleClass().add("menu-orden");
        for (OrdenDeseos opcion : OrdenDeseos.values()) {
            MenuItem item = new MenuItem(opcion.getEtiqueta());
            item.setOnAction(e -> ordenarPor(opcion));
            menuOrden.getItems().add(item);
        }
        botonOrdenar.setOnAction(e -> menuOrden.show(botonOrdenar, Side.BOTTOM, 0, 6));
    }

    private void prepararRecorrido() {
        botonAnterior.setGraphic(Iconos.flechaIzquierda(Iconos.OSCURO, 16));
        botonSiguiente.setGraphic(Iconos.flechaDerecha(Iconos.OSCURO, 16));
        botonSiguiente.setContentDisplay(ContentDisplay.RIGHT);
        botonAnterior.setOnAction(e -> retroceder());
        botonSiguiente.setOnAction(e -> avanzar());
        scroll.addEventFilter(KeyEvent.KEY_PRESSED, evento -> {
            if (evento.isAltDown() || !barraRecorrido.isVisible()) {
                return;
            }
            if (evento.getCode() == KeyCode.RIGHT) {
                avanzar();
                evento.consume();
            } else if (evento.getCode() == KeyCode.LEFT) {
                retroceder();
                evento.consume();
            }
        });
    }

    public void ordenarPor(OrdenDeseos nuevo) {
        orden = nuevo == null ? OrdenDeseos.MAS_RECIENTES : nuevo;
        if (orden == OrdenDeseos.MAS_ANTIGUOS) {
            cursor.irAlFinal();
        } else {
            cursor.irAlInicio();
        }
        pintar();
    }

    private boolean vaHaciaAtras() {
        return orden == OrdenDeseos.MAS_ANTIGUOS;
    }

    public void avanzar() {
        if (vaHaciaAtras()) {
            cursor.anterior();
        } else {
            cursor.siguiente();
        }
        pintarRecorrido(true);
    }

    public void retroceder() {
        if (vaHaciaAtras()) {
            cursor.siguiente();
        } else {
            cursor.anterior();
        }
        pintarRecorrido(true);
    }

    public Zapato getActual() {
        return cursor.actual();
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
            tarjeta.getBotonCarrito().setOnAction(e -> mostrarTallas(tarjeta));
            tarjeta.getBotonCorazon().setOnAction(e -> quitar(zapato));
            tarjeta.getBotonQuitar().setOnAction(e -> quitar(zapato));
            grilla.getChildren().add(tarjeta);
        }
        grilla.getChildren().add(crearExplorar(total == 0));
        ajustar();
        pintarRecorrido(false);
    }

    private void pintarRecorrido(boolean desplazar) {
        int total = deseos.cantidad();
        boolean visible = orden.esRecorrido() && total > 1 && cursor.actual() != null;
        barraRecorrido.setVisible(visible);
        barraRecorrido.setManaged(visible);
        Zapato actual = visible ? cursor.actual() : null;
        if (visible) {
            int posicion = vaHaciaAtras() ? total - cursor.posicion() : cursor.posicion() + 1;
            textoRecorrido.setText("PAR " + posicion + " DE " + total + " · " + actual.getNombre().toUpperCase());
            botonAnterior.setDisable(vaHaciaAtras() ? !cursor.haySiguiente() : !cursor.hayAnterior());
            botonSiguiente.setDisable(vaHaciaAtras() ? !cursor.hayAnterior() : !cursor.haySiguiente());
        }
        for (Node nodo : grilla.getChildren()) {
            if (nodo instanceof TarjetaDeseo tarjeta) {
                boolean esActual = actual != null && tarjeta.getZapato().getSku().equals(actual.getSku());
                tarjeta.setActual(esActual);
                if (esActual && desplazar) {
                    llevarALaVista(tarjeta);
                }
            }
        }
    }

    private void llevarALaVista(Node nodo) {
        double altoContenido = scroll.getContent().getBoundsInLocal().getHeight();
        double altoVista = scroll.getViewportBounds().getHeight();
        if (altoContenido <= altoVista) {
            return;
        }
        Bounds limites = scroll.getContent().sceneToLocal(nodo.localToScene(nodo.getBoundsInLocal()));
        double arriba = (altoContenido - altoVista) * scroll.getVvalue();
        if (limites.getMinY() < arriba || limites.getMaxY() > arriba + altoVista) {
            double destino = Math.max(0, limites.getMinY() - 120);
            scroll.setVvalue(Math.min(1, destino / (altoContenido - altoVista)));
        }
    }

    private void mostrarTallas(TarjetaDeseo tarjeta) {
        menuTallas.getItems().setAll(opcionesDeTalla(tarjeta.getZapato()));
        menuTallas.show(tarjeta.getBotonCarrito(), Side.BOTTOM, 0, 6);
    }

    public ArrayList<MenuItem> opcionesDeTalla(Zapato zapato) {
        ArrayList<MenuItem> opciones = new ArrayList<>();
        CarritoServicio carrito = new CarritoServicio();
        for (TallaStock talla : zapato.getTallas()) {
            if (talla.getStock() == 0) {
                continue;
            }
            int libres = talla.getStock() - carrito.cantidadDe(zapato.getSku(), talla.getTalla());
            MenuItem opcion = new MenuItem("Talla " + talla.getTalla() + " · "
                    + (libres > 0 ? seleccion.describirStock(libres) : "Ya están en tu carrito"));
            opcion.setDisable(libres <= 0);
            opcion.setOnAction(e -> moverACarrito(zapato, talla.getTalla()));
            opciones.add(opcion);
        }
        return opciones;
    }

    private void moverACarrito(Zapato zapato, int talla) {
        boolean eraActual = esActual(zapato);
        Zapato vecino = eraActual ? vecinoDeActual() : cursor.actual();
        ResultadoSeleccion resultado = deseos.moverACarrito(zapato.getSku(), talla);
        if (!resultado.esValida()) {
            if (eraActual) {
                recolocar(zapato);
            }
            mostrarAviso(resultado.getMensaje(), false);
            return;
        }
        ultimoQuitado = null;
        ocultarAviso();
        recolocar(vecino);
        BarraClienteController.refrescar();
        pintar();
        PopupAgregadoCarritoController popup = Popup.abrir(Ventana.POPUP_AGREGADO_CARRITO);
        popup.setAgregado(zapato, talla, 1, new CarritoServicio().cantidadTotal());
        popup.mostrarYEsperar();
        if (popup.quiereVerCarrito()) {
            Navegador.mostrar(Ventana.CARRITO);
        }
    }

    private void quitar(Zapato zapato) {
        Zapato vecino = esActual(zapato) ? vecinoDeActual() : cursor.actual();
        int posicion = deseos.quitar(zapato.getSku());
        if (posicion < 0) {
            return;
        }
        ultimoQuitado = zapato;
        posicionQuitado = posicion;
        recolocar(vecino);
        BarraClienteController.refrescar();
        pintar();
        mostrarAviso("Quitaste " + zapato.getNombre() + " de tu lista.", true);
    }

    private void deshacer() {
        if (ultimoQuitado == null) {
            return;
        }
        Zapato restaurado = ultimoQuitado;
        ultimoQuitado = null;
        deseos.restaurar(restaurado, posicionQuitado);
        recolocar(restaurado);
        BarraClienteController.refrescar();
        ocultarAviso();
        pintar();
    }

    private boolean esActual(Zapato zapato) {
        return cursor.actual() != null && cursor.actual().getSku().equals(zapato.getSku());
    }

    private Zapato vecinoDeActual() {
        if (cursor.haySiguiente()) {
            return cursor.siguiente();
        }
        return cursor.hayAnterior() ? cursor.anterior() : null;
    }

    private void recolocar(Zapato zapato) {
        if (zapato == null || !cursor.moverA(z -> z.getSku().equals(zapato.getSku()))) {
            cursor.irAlInicio();
        }
    }

    private void mostrarAviso(String texto, boolean conDeshacer) {
        textoAviso.setText(texto);
        enlaceDeshacer.setVisible(conDeshacer);
        enlaceDeshacer.setManaged(conDeshacer);
        aviso.setVisible(true);
        aviso.setManaged(true);
    }

    private void ocultarAviso() {
        aviso.setVisible(false);
        aviso.setManaged(false);
    }

    private VBox crearExplorar(boolean vacia) {
        HBox huellas = new HBox(40, Iconos.huellaPequena(Color.web("#8C7B6B"), -25), Iconos.huellaPequena(Iconos.OSCURO, -25));
        huellas.setAlignment(Pos.CENTER);
        huellas.setMaxWidth(Region.USE_PREF_SIZE);
        huellas.setMouseTransparent(true);
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
        if (disponible <= 0 || grilla.getChildren().isEmpty()) {
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
