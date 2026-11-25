package com.nuctua.controlador;

import com.nuctua.estructuras.ListaEnlazada;
import com.nuctua.modelo.CambioStock;
import com.nuctua.modelo.ResumenInventario;
import com.nuctua.modelo.SesionActual;
import com.nuctua.modelo.TallaStock;
import com.nuctua.modelo.Zapato;
import com.nuctua.servicio.FiltroProductos;
import com.nuctua.servicio.InventarioServicio;
import com.nuctua.servicio.OrdenProductos;
import com.nuctua.servicio.ProductosServicio;
import com.nuctua.servicio.ResultadoInventario;
import com.nuctua.util.Iconos;
import com.nuctua.util.Recursos;
import com.nuctua.vista.Miniatura;
import com.nuctua.vista.PastillaAdmin;
import com.nuctua.vista.TarjetaMetrica;
import com.nuctua.vista.Ventana;
import java.util.ArrayList;
import javafx.fxml.FXML;
import javafx.geometry.HPos;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Rectangle;

public class AdminProductosController implements ConEstadoNavegacion {

    private static final int POR_PAGINA = 6;
    private static final double[] COLUMNAS = {33, 13, 12, 14, 14, 14};

    @FXML
    private BarraAdminController barraController;
    @FXML
    private Hyperlink enlaceVolver;
    @FXML
    private Label sello;
    @FXML
    private HBox metricas;
    @FXML
    private HBox aviso;
    @FXML
    private Label textoAviso;
    @FXML
    private StackPane iconoBuscar;
    @FXML
    private TextField campoBuscar;
    @FXML
    private FlowPane filtros;
    @FXML
    private Button botonRegistrar;
    @FXML
    private GridPane encabezado;
    @FXML
    private VBox filas;
    @FXML
    private Label textoPagina;
    @FXML
    private Button botonAnterior;
    @FXML
    private Button botonSiguiente;
    @FXML
    private VBox panelDetalle;
    @FXML
    private VBox panelVacio;
    @FXML
    private StackPane foto;
    @FXML
    private Label nombre;
    @FXML
    private Label datos;
    @FXML
    private Label precio;
    @FXML
    private StackPane estado;
    @FXML
    private FlowPane tallas;
    @FXML
    private Button botonEditar;
    @FXML
    private Hyperlink enlaceStock;
    @FXML
    private Hyperlink enlaceVisible;
    @FXML
    private Hyperlink enlaceEliminar;

    private final ProductosServicio servicio = new ProductosServicio();
    private final InventarioServicio inventario = new InventarioServicio();
    private final ToggleGroup grupo = new ToggleGroup();
    private ArrayList<Zapato> visibles = new ArrayList<>();
    private FiltroProductos filtro = FiltroProductos.TODOS;
    private OrdenProductos orden = OrdenProductos.SKU;
    private boolean ascendente = true;
    private int pagina;
    private String elegido = "";

    @FXML
    private void initialize() {
        barraController.marcar(Ventana.ADMIN_PRODUCTOS);
        enlaceVolver.setGraphic(Iconos.flechaIzquierda(Iconos.OSCURO, 18));
        enlaceVolver.setText(Navegador.textoVolver());
        enlaceVolver.setOnAction(e -> Navegador.volver());
        iconoBuscar.getChildren().add(Iconos.buscar(Iconos.OSCURO, 18));
        campoBuscar.textProperty().addListener((obs, antes, ahora) -> filtrar());
        for (FiltroProductos opcion : FiltroProductos.values()) {
            agregarFiltro(opcion);
        }
        botonRegistrar.setOnAction(e -> Navegador.mostrar(Ventana.ADMIN_REGISTRAR_PRODUCTO));
        botonAnterior.setGraphic(Iconos.flechaIzquierda(Iconos.OSCURO, 14));
        botonSiguiente.setGraphic(Iconos.flechaDerecha(Iconos.OSCURO, 14));
        botonSiguiente.setContentDisplay(ContentDisplay.RIGHT);
        botonAnterior.setOnAction(e -> irAPagina(pagina - 1));
        botonSiguiente.setOnAction(e -> irAPagina(pagina + 1));
        botonEditar.setOnAction(e -> editar(actual()));
        enlaceStock.setOnAction(e -> ajustarStock(actual()));
        enlaceVisible.setOnAction(e -> cambiarVisible(actual()));
        enlaceEliminar.setOnAction(e -> eliminar(actual()));
        columnas(encabezado);
        pintarEncabezado();
        cargar();
    }

    private void agregarFiltro(FiltroProductos opcion) {
        ToggleButton boton = new ToggleButton(opcion.getTexto());
        boton.getStyleClass().addAll("chip-perfil", "chip-admin");
        boton.setToggleGroup(grupo);
        boton.setSelected(opcion == filtro);
        boton.setOnAction(e -> {
            boton.setSelected(true);
            filtro = opcion;
            filtrar();
        });
        filtros.getChildren().add(boton);
    }

    private void columnas(GridPane tabla) {
        for (double ancho : COLUMNAS) {
            ColumnConstraints columna = new ColumnConstraints();
            columna.setPercentWidth(ancho);
            columna.setMinWidth(0);
            tabla.getColumnConstraints().add(columna);
        }
    }

    private void pintarEncabezado() {
        encabezado.getChildren().clear();
        encabezado.add(titulo("PRODUCTO", OrdenProductos.NOMBRE), 0, 0);
        encabezado.add(titulo("CATEGORÍA"), 1, 0);
        encabezado.add(titulo("STOCK", OrdenProductos.STOCK), 2, 0);
        encabezado.add(titulo("PRECIO", OrdenProductos.PRECIO), 3, 0);
        encabezado.add(titulo("ESTADO"), 4, 0);
    }

    private Label titulo(String texto) {
        Label titulo = new Label(texto);
        titulo.getStyleClass().addAll("etiqueta-campo", "texto-suave-grande");
        return titulo;
    }

    private Hyperlink titulo(String texto, OrdenProductos criterio) {
        String flecha = orden != criterio ? " ↕" : (ascendente ? " ↑" : " ↓");
        Hyperlink enlace = new Hyperlink(texto + flecha);
        enlace.getStyleClass().add("titulo-columna");
        enlace.setTooltip(new Tooltip("Ordenar por " + criterio.getTexto() + " con " + criterio.getMetodo().getNombre()));
        enlace.setOnAction(e -> ordenarPor(criterio));
        return enlace;
    }

    public void ordenarPor(OrdenProductos criterio) {
        if (orden != criterio) {
            orden = criterio;
            ascendente = true;
        } else if (ascendente) {
            ascendente = false;
        } else {
            orden = OrdenProductos.SKU;
            ascendente = true;
        }
        pintarEncabezado();
        filtrar();
        irAPagina(0);
    }

    private void cargar() {
        ResumenInventario resumen = inventario.resumen();
        int total = servicio.listar("", FiltroProductos.TODOS, OrdenProductos.SKU, true).size();
        sello.setText(total + (total == 1 ? " producto en el archivo" : " productos en el archivo"));
        TarjetaMetrica todos = new TarjetaMetrica("TOTAL PRODUCTOS", true);
        todos.mostrar(total, "Registrados en el archivo");
        TarjetaMetrica activos = new TarjetaMetrica("ACTIVOS", false);
        activos.mostrar(resumen.getActivos(), "Visibles en el catálogo");
        TarjetaMetrica agotados = new TarjetaMetrica("AGOTADOS", false);
        agotados.mostrar(resumen.getAgotados(), "Sin stock en todas las tallas");
        TarjetaMetrica pares = new TarjetaMetrica("PARES EN BODEGA", false);
        pares.mostrar(resumen.getPares(), "Suma del stock por talla");
        metricas.getChildren().setAll(todos, activos, agotados, pares);
        filtrar();
    }

    private void filtrar() {
        visibles = servicio.listar(campoBuscar.getText(), filtro, orden, ascendente);
        if (posicionDe(elegido) < 0) {
            elegido = visibles.isEmpty() ? "" : visibles.get(0).getSku();
        }
        irAPagina(Math.max(0, posicionDe(elegido)) / POR_PAGINA);
        pintarDetalle();
    }

    private int posicionDe(String sku) {
        for (int i = 0; i < visibles.size(); i++) {
            if (visibles.get(i).getSku().equalsIgnoreCase(sku)) {
                return i;
            }
        }
        return -1;
    }

    private Zapato actual() {
        int posicion = posicionDe(elegido);
        return posicion < 0 ? null : visibles.get(posicion);
    }

    private void irAPagina(int nueva) {
        int paginas = Math.max(1, (visibles.size() + POR_PAGINA - 1) / POR_PAGINA);
        pagina = Math.max(0, Math.min(nueva, paginas - 1));
        int desde = pagina * POR_PAGINA;
        int hasta = Math.min(visibles.size(), desde + POR_PAGINA);
        filas.getChildren().clear();
        for (int i = desde; i < hasta; i++) {
            filas.getChildren().add(crearFila(visibles.get(i)));
        }
        if (visibles.isEmpty()) {
            Label vacio = new Label("Ningún producto coincide con tu búsqueda.");
            vacio.getStyleClass().addAll("texto", "fila-vacia");
            filas.getChildren().add(vacio);
        }
        textoPagina.setText("Mostrando " + (hasta - desde) + " de " + visibles.size()
                + (visibles.size() == 1 ? " producto" : " productos") + " · Ordenado por " + orden.getTexto());
        botonAnterior.setDisable(pagina == 0);
        botonSiguiente.setDisable(pagina >= paginas - 1);
    }

    private GridPane crearFila(Zapato zapato) {
        GridPane fila = new GridPane();
        columnas(fila);
        fila.getStyleClass().add("fila-admin");
        if (zapato.getSku().equalsIgnoreCase(elegido)) {
            fila.getStyleClass().add("fila-admin-activa");
        }
        Label sku = new Label(zapato.getSku() + " · " + zapato.getMarca());
        sku.getStyleClass().add("texto-suave");
        VBox textos = new VBox(texto(zapato.getNombre(), "negrita"), sku);
        textos.setAlignment(Pos.CENTER_LEFT);
        textos.setMinWidth(0);
        HBox producto = new HBox(12, new Miniatura(zapato, 46), textos);
        producto.setAlignment(Pos.CENTER_LEFT);
        Label pares = texto(zapato.getStockTotal() + (zapato.getStockTotal() == 1 ? " par" : " pares"), "texto-suave-grande");
        if (zapato.estaAgotado()) {
            pares.getStyleClass().add("texto-alerta");
        }
        Button editar = boton(Iconos.lapiz(Iconos.OSCURO, 15), "Editar producto");
        editar.setOnAction(e -> editar(zapato));
        Button ver = boton(Iconos.ojo(Iconos.OSCURO, 17), "Ver detalle");
        ver.setOnAction(e -> elegir(zapato.getSku()));
        HBox acciones = new HBox(6, editar, ver);
        acciones.setAlignment(Pos.CENTER_RIGHT);
        GridPane.setHalignment(acciones, HPos.RIGHT);
        fila.add(producto, 0, 0);
        fila.add(texto(zapato.getCategoria(), "texto-suave-grande"), 1, 0);
        fila.add(pares, 2, 0);
        fila.add(texto(zapato.getPrecioTexto(), "negrita"), 3, 0);
        fila.add(PastillaAdmin.activo(zapato), 4, 0);
        fila.add(acciones, 5, 0);
        fila.setOnMouseClicked(e -> elegir(zapato.getSku()));
        return fila;
    }

    private Button boton(Node icono, String ayuda) {
        Button boton = new Button();
        boton.getStyleClass().addAll("boton-icono", "boton-icono-fila");
        boton.setGraphic(icono);
        boton.setTooltip(new Tooltip(ayuda));
        boton.setAccessibleText(ayuda);
        return boton;
    }

    private Label texto(String valor, String estilo) {
        Label label = new Label(valor);
        label.getStyleClass().addAll("texto", estilo);
        return label;
    }

    public void elegir(String sku) {
        elegido = sku;
        if (posicionDe(sku) < 0) {
            grupo.getToggles().get(0).setSelected(true);
            filtro = FiltroProductos.TODOS;
            campoBuscar.clear();
            filtrar();
            return;
        }
        irAPagina(posicionDe(sku) / POR_PAGINA);
        pintarDetalle();
    }

    private void pintarDetalle() {
        Zapato zapato = actual();
        alternar(panelDetalle, zapato != null);
        alternar(panelVacio, zapato == null);
        if (zapato == null) {
            return;
        }
        StackPane imagen = new StackPane(Recursos.vistaCubierta(Recursos.imagenProducto(zapato), 244, 182));
        Rectangle recorte = new Rectangle(244, 182);
        recorte.setArcWidth(24);
        recorte.setArcHeight(24);
        imagen.setClip(recorte);
        imagen.setMaxSize(244, 182);
        foto.getChildren().setAll(imagen);
        nombre.setText(zapato.getNombre());
        datos.setText(zapato.getSku() + " · " + zapato.getMarca() + " · " + zapato.getCategoria());
        precio.setText(zapato.getPrecioTexto());
        estado.getChildren().setAll(PastillaAdmin.activo(zapato));
        tallas.getChildren().clear();
        for (TallaStock talla : zapato.getTallas()) {
            Label numero = new Label(String.valueOf(talla.getTalla()));
            numero.getStyleClass().add("texto-suave");
            Label stock = new Label(String.valueOf(talla.getStock()));
            stock.getStyleClass().addAll("cifra-perfil", "cifra-chica");
            VBox caja = new VBox(numero, stock);
            caja.setAlignment(Pos.CENTER);
            caja.setMinWidth(27);
            tallas.getChildren().add(caja);
        }
        enlaceVisible.setText(zapato.isActivo() ? "Ocultar de la tienda" : "Mostrar en la tienda");
    }

    private void editar(Zapato zapato) {
        elegido = zapato.getSku();
        AdminRegistrarProductoController editor = Navegador.mostrar(Ventana.ADMIN_REGISTRAR_PRODUCTO);
        if (editor != null) {
            editor.editar(zapato);
        }
    }

    private void ajustarStock(Zapato zapato) {
        AdminStockTallaController editor = Navegador.mostrar(Ventana.ADMIN_STOCK_TALLA);
        if (editor != null) {
            editor.mostrar(zapato);
        }
    }

    private void cambiarVisible(Zapato zapato) {
        ResultadoInventario resultado = inventario.guardar(new ListaEnlazada<>(), zapato.getSku(), !zapato.isActivo());
        cargar();
        mostrarAviso(resultado.esExito() ? zapato.getNombre() + (zapato.isActivo() ? " quedó oculto en la tienda."
                : " ya se ve en la tienda.") : resultado.getMensaje());
        barraController.actualizarAvisos();
    }

    private void eliminar(Zapato zapato) {
        if (!PopupEliminarProductoController.confirmar(zapato)) {
            return;
        }
        ResultadoInventario resultado = inventario.eliminar(zapato.getSku());
        if (resultado.esExito()) {
            CambioStock.olvidar(SesionActual.getInstancia().getCambiosStock(), zapato.getSku());
            barraController.actualizarAvisos();
        }
        cargar();
        mostrarAviso(resultado.getMensaje());
    }

    private void mostrarAviso(String texto) {
        textoAviso.setText(texto);
        alternar(aviso, true);
    }

    private void alternar(Node nodo, boolean visible) {
        nodo.setVisible(visible);
        nodo.setManaged(visible);
    }

    @Override
    public String capturarEstado() {
        return elegido;
    }

    @Override
    public void restaurarEstado(String estado) {
        elegir(estado);
    }
}
