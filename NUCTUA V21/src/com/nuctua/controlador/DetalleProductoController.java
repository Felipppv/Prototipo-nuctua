package com.nuctua.controlador;

import com.nuctua.modelo.SesionActual;
import com.nuctua.modelo.TallaStock;
import com.nuctua.modelo.Zapato;
import com.nuctua.servicio.CarritoServicio;
import com.nuctua.servicio.CatalogoServicio;
import com.nuctua.servicio.DeseosServicio;
import com.nuctua.servicio.ResultadoSeleccion;
import com.nuctua.servicio.SeleccionServicio;
import com.nuctua.util.Iconos;
import com.nuctua.util.Recursos;
import com.nuctua.vista.TarjetaZapato;
import com.nuctua.vista.Ventana;
import java.util.ArrayList;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;

public class DetalleProductoController implements ConEstadoNavegacion {

    private static final int MAXIMO_VISTOS = 3;

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
    private Label mensajeSeleccion;
    @FXML
    private Hyperlink enlaceVolver;
    @FXML
    private FlowPane panelVistos;

    private final ToggleGroup grupoTallas = new ToggleGroup();
    private final CatalogoServicio servicio = new CatalogoServicio();
    private final SeleccionServicio seleccion = new SeleccionServicio();
    private final DeseosServicio deseos = new DeseosServicio();
    private final CarritoServicio carrito = new CarritoServicio();
    private Zapato zapato;

    @FXML
    private void initialize() {
        barraController.marcar(Ventana.DETALLE_PRODUCTO);
        botonCarrito.setGraphic(Iconos.flechaDerecha(Iconos.CREMA, 18));
        botonCarrito.setContentDisplay(ContentDisplay.RIGHT);
        botonCarrito.setOnAction(e -> agregarAlCarrito());
        botonDeseos.setOnAction(e -> alternarDeseo());
        grupoTallas.selectedToggleProperty().addListener((obs, antes, ahora) -> {
            ocultarMensaje();
            actualizarStockTalla();
        });
    }

    public void mostrar(Zapato elegido) {
        if (elegido == null) {
            return;
        }
        Zapato actualizado = servicio.buscarPorSku(elegido.getSku());
        Zapato nuevo = actualizado == null ? elegido : actualizado;
        if (zapato != null && !zapato.getSku().equals(nuevo.getSku())) {
            Navegador.recordar(new EntradaNavegacion(Ventana.DETALLE_PRODUCTO, zapato.getSku()));
        }
        zapato = nuevo;
        enlaceVolver.setText("← " + Navegador.textoVolver());
        SesionActual.getInstancia().registrarVisto(zapato);
        pintarProducto();
        pintarVistos();
        scroll.setVvalue(0);
    }

    public Zapato getZapato() {
        return zapato;
    }

    public int getTallaElegida() {
        if (grupoTallas.getSelectedToggle() == null) {
            return 0;
        }
        return ((TallaStock) grupoTallas.getSelectedToggle().getUserData()).getTalla();
    }

    public void elegirTalla(int talla) {
        for (Node nodo : panelTallas.getChildren()) {
            ToggleButton boton = (ToggleButton) nodo;
            if (((TallaStock) boton.getUserData()).getTalla() == talla && !boton.isDisable()) {
                boton.setSelected(true);
                return;
            }
        }
    }

    public ResultadoSeleccion validarSeleccion() {
        return seleccion.validar(zapato, getTallaElegida(), 1);
    }

    private void pintarProducto() {
        foto.getChildren().removeIf(n -> n != etiqueta);
        StackPane imagen = new StackPane(Recursos.vistaCubierta(Recursos.imagenProducto(zapato), 518, 458));
        Rectangle recorte = new Rectangle(518, 458);
        recorte.setArcWidth(28);
        recorte.setArcHeight(28);
        imagen.setClip(recorte);
        foto.getChildren().add(0, imagen);
        etiqueta.setText(zapato.estaAgotado() ? "Agotado" : TarjetaZapato.etiquetaDe(zapato));
        etiqueta.getStyleClass().setAll("label", zapato.estaAgotado() ? "pastilla-naranja" : "pastilla");
        chipSku.setText("SKU " + zapato.getSku() + " · " + zapato.getMarca().toUpperCase()
                + " · " + SeleccionServicio.describirCategoria(zapato));
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
        grupoTallas.selectToggle(null);
        panelTallas.getChildren().clear();
        for (TallaStock talla : zapato.getTallas()) {
            ToggleButton boton = new ToggleButton(String.valueOf(talla.getTalla()));
            boton.getStyleClass().add("talla");
            boton.setUserData(talla);
            boton.setToggleGroup(grupoTallas);
            boton.setDisable(talla.getStock() == 0);
            Tooltip.install(boton, new Tooltip(talla.getStock() == 0
                    ? "Talla " + talla.getTalla() + " agotada"
                    : "Talla " + talla.getTalla() + " · " + seleccion.describirStock(talla.getStock())));
            panelTallas.getChildren().add(boton);
        }
        botonCarrito.setDisable(zapato.estaAgotado() || !zapato.isActivo());
        pintarDeseo();
        ocultarMensaje();
        actualizarStockTalla();
    }

    private void actualizarStockTalla() {
        if (zapato == null) {
            return;
        }
        panelTallas.getStyleClass().remove("tallas-error");
        if (zapato.estaAgotado()) {
            pintarPastilla("Agotado", "pastilla-naranja");
            textoStock.setText("Este par está agotado en todas las tallas.");
            return;
        }
        if (grupoTallas.getSelectedToggle() == null) {
            int disponibles = zapato.getTallas().size() - seleccion.tallasAgotadas(zapato).size();
            pintarPastilla("Elige una talla", "pastilla-borde");
            textoStock.setText(disponibles + (disponibles == 1 ? " talla disponible · " : " tallas disponibles · ")
                    + seleccion.describirAgotadas(zapato));
            return;
        }
        TallaStock talla = (TallaStock) grupoTallas.getSelectedToggle().getUserData();
        pintarPastilla(seleccion.describirStock(talla.getStock()), "pastilla");
        String agotadas = seleccion.tallasAgotadas(zapato).isEmpty() ? "" : " · " + seleccion.describirAgotadas(zapato);
        textoStock.setText("en talla " + talla.getTalla() + agotadas);
    }

    private void alternarDeseo() {
        if (zapato == null) {
            return;
        }
        boolean guardado = deseos.alternar(zapato);
        pintarDeseo();
        BarraClienteController.refrescar();
        mostrarMensaje(guardado ? "Guardado en tu lista de deseos." : "Quitado de tu lista de deseos.", "mensaje-ok");
    }

    private void pintarDeseo() {
        boolean guardado = deseos.estaGuardado(zapato);
        botonDeseos.setText(guardado ? "EN TU LISTA DE DESEOS" : "AGREGAR A DESEOS");
        botonDeseos.setGraphic(Iconos.corazon(guardado ? Iconos.NARANJA : Iconos.OSCURO, 18, guardado));
    }

    private void pintarPastilla(String texto, String estilo) {
        pastillaStock.setText(texto);
        pastillaStock.getStyleClass().setAll("label", estilo);
    }

    private void agregarAlCarrito() {
        int talla = getTallaElegida();
        ResultadoSeleccion resultado = carrito.agregar(zapato, talla, 1);
        if (!resultado.esValida()) {
            if (resultado == ResultadoSeleccion.SIN_TALLA && !panelTallas.getStyleClass().contains("tallas-error")) {
                panelTallas.getStyleClass().add("tallas-error");
            }
            boolean todoEnCarrito = resultado == ResultadoSeleccion.STOCK_INSUFICIENTE && carrito.cantidadDe(zapato.getSku(), talla) > 0;
            mostrarMensaje(todoEnCarrito ? "Ya tienes en tu carrito todos los pares disponibles de la talla " + talla + "."
                    : resultado.getMensaje(), "mensaje-error");
            return;
        }
        ocultarMensaje();
        BarraClienteController.refrescar();
        PopupAgregadoCarritoController.mostrar(zapato, talla, 1);
    }

    private void mostrarMensaje(String texto, String estilo) {
        mensajeSeleccion.setText(texto);
        mensajeSeleccion.getStyleClass().setAll("label", estilo);
        mensajeSeleccion.setVisible(true);
        mensajeSeleccion.setManaged(true);
    }

    private void ocultarMensaje() {
        mensajeSeleccion.setText("");
        mensajeSeleccion.setVisible(false);
        mensajeSeleccion.setManaged(false);
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
        nombreVisto.setMaxWidth(230);
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
        Navegador.volver();
    }

    @Override
    public String capturarEstado() {
        return zapato == null ? "" : zapato.getSku();
    }

    @Override
    public void restaurarEstado(String estado) {
        Zapato guardado = servicio.buscarPorSku(estado);
        if (guardado != null) {
            mostrar(guardado);
        }
    }
}
