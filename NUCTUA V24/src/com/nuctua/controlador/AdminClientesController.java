package com.nuctua.controlador;

import com.nuctua.estructuras.ListaEnlazada;
import com.nuctua.modelo.Cliente;
import com.nuctua.modelo.Pedido;
import com.nuctua.modelo.ResumenCliente;
import com.nuctua.modelo.Zapato;
import com.nuctua.servicio.ClientesServicio;
import com.nuctua.servicio.FiltroClientes;
import com.nuctua.servicio.OrdenClientes;
import com.nuctua.util.FormatoCampo;
import com.nuctua.util.Iconos;
import com.nuctua.vista.Avatar;
import com.nuctua.vista.Miniatura;
import com.nuctua.vista.PastillaAdmin;
import com.nuctua.vista.TarjetaMetrica;
import com.nuctua.vista.Ventana;
import java.time.LocalDate;
import java.util.ArrayList;
import javafx.fxml.FXML;
import javafx.geometry.HPos;
import javafx.geometry.Pos;
import javafx.geometry.Side;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.control.Tooltip;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

public class AdminClientesController implements ConEstadoNavegacion {

    private static final int POR_PAGINA = 6;
    private static final double[] COLUMNAS = {27, 12, 10, 15, 12, 17, 7};

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
    private VBox filas;
    @FXML
    private GridPane encabezado;
    @FXML
    private Label textoPagina;
    @FXML
    private Button botonAnterior;
    @FXML
    private Button botonSiguiente;
    @FXML
    private Button botonAgregar;
    @FXML
    private VBox panelPerfil;
    @FXML
    private VBox panelVacio;
    @FXML
    private StackPane avatar;
    @FXML
    private Label nombrePerfil;
    @FXML
    private Label correoPerfil;
    @FXML
    private StackPane estadoPerfil;
    @FXML
    private Label telefonoPerfil;
    @FXML
    private Label desdePerfil;
    @FXML
    private Label direccionPerfil;
    @FXML
    private Label cifraPedidos;
    @FXML
    private Label cifraPares;
    @FXML
    private Label cifraGastado;
    @FXML
    private VBox ultimosPedidos;
    @FXML
    private VBox favoritos;
    @FXML
    private Button botonPedidos;
    @FXML
    private Hyperlink enlaceCorreo;

    private final ClientesServicio servicio = new ClientesServicio();
    private final ToggleGroup grupo = new ToggleGroup();
    private final ContextMenu menuOrden = new ContextMenu();
    private final Button botonOrden = new Button();
    private ArrayList<ResumenCliente> clientes = new ArrayList<>();
    private ArrayList<ResumenCliente> visibles = new ArrayList<>();
    private FiltroClientes filtro = FiltroClientes.TODOS;
    private OrdenClientes orden = OrdenClientes.MAS_RECIENTES;
    private int pagina;
    private String elegido = "";

    @FXML
    private void initialize() {
        barraController.marcar(Ventana.ADMIN_CLIENTES);
        enlaceVolver.setGraphic(Iconos.flechaIzquierda(Iconos.OSCURO, 18));
        enlaceVolver.setText(Navegador.textoVolver());
        enlaceVolver.setOnAction(e -> Navegador.volver());
        iconoBuscar.getChildren().add(Iconos.buscar(Iconos.OSCURO, 18));
        campoBuscar.textProperty().addListener((obs, antes, ahora) -> filtrar());
        prepararFiltros();
        prepararEncabezado();
        botonAnterior.setGraphic(Iconos.flechaIzquierda(Iconos.OSCURO, 14));
        botonSiguiente.setGraphic(Iconos.flechaDerecha(Iconos.OSCURO, 14));
        botonSiguiente.setContentDisplay(ContentDisplay.RIGHT);
        botonAnterior.setOnAction(e -> irAPagina(pagina - 1));
        botonSiguiente.setOnAction(e -> irAPagina(pagina + 1));
        botonAgregar.setGraphic(Iconos.mas(Iconos.OSCURO, 16));
        botonAgregar.setOnAction(e -> agregar());
        botonPedidos.setGraphic(Iconos.flechaDerecha(Iconos.CREMA, 18));
        botonPedidos.setContentDisplay(ContentDisplay.RIGHT);
        botonPedidos.setOnAction(e -> Navegador.mostrar(Ventana.ADMIN_PEDIDOS));
        enlaceCorreo.setOnAction(e -> copiarCorreo());
        cargar();
    }

    private void prepararFiltros() {
        for (FiltroClientes opcion : FiltroClientes.values()) {
            ToggleButton boton = new ToggleButton(opcion.getTexto());
            boton.getStyleClass().addAll("chip-perfil", "chip-admin");
            boton.setToggleGroup(grupo);
            boton.setSelected(opcion == filtro);
            boton.setMinWidth(Region.USE_PREF_SIZE);
            boton.setOnAction(e -> {
                boton.setSelected(true);
                filtro = opcion;
                filtrar();
            });
            filtros.getChildren().add(boton);
        }
        botonOrden.getStyleClass().addAll("chip-perfil", "chip-admin");
        filtros.getChildren().add(botonOrden);
        menuOrden.getStyleClass().add("menu-orden");
        for (OrdenClientes opcion : OrdenClientes.values()) {
            MenuItem item = new MenuItem(opcion.getTexto());
            item.setOnAction(e -> ordenarPor(opcion));
            menuOrden.getItems().add(item);
        }
        botonOrden.setGraphic(Iconos.flechaAbajo(Iconos.OSCURO, 14));
        botonOrden.setContentDisplay(ContentDisplay.RIGHT);
        botonOrden.setOnAction(e -> menuOrden.show(botonOrden, Side.BOTTOM, 0, 6));
    }

    private void prepararEncabezado() {
        columnas(encabezado);
        String[] titulos = {"CLIENTE", "CIUDAD", "PEDIDOS", "GASTADO", "ÚLTIMA COMPRA", "ESTADO"};
        for (int i = 0; i < titulos.length; i++) {
            Label titulo = new Label(titulos[i]);
            titulo.setWrapText(true);
            titulo.getStyleClass().addAll("etiqueta-campo", "texto-suave-grande");
            encabezado.add(titulo, i, 0);
        }
    }

    private void columnas(GridPane tabla) {
        for (double ancho : COLUMNAS) {
            ColumnConstraints columna = new ColumnConstraints();
            columna.setPercentWidth(ancho);
            columna.setMinWidth(0);
            tabla.getColumnConstraints().add(columna);
        }
    }

    public void ordenarPor(OrdenClientes nuevo) {
        orden = nuevo;
        filtrar();
    }

    private void cargar() {
        clientes = servicio.todos();
        int total = clientes.size();
        sello.setText(total + (total == 1 ? " cliente registrado" : " clientes registrados"));
        TarjetaMetrica todos = new TarjetaMetrica("TOTAL CLIENTES", true);
        todos.mostrar(total, "Con cuenta en la tienda");
        TarjetaMetrica nuevos = new TarjetaMetrica("NUEVOS ESTE MES", false);
        nuevos.mostrar(servicio.nuevosEsteMes(clientes), "Se registraron en " + Pedido.nombreMes(LocalDate.now()));
        TarjetaMetrica recurrentes = new TarjetaMetrica("RECURRENTES", false);
        recurrentes.mostrar(servicio.recurrentes(clientes), "Compraron más de una vez");
        TarjetaMetrica conFavoritos = new TarjetaMetrica("CON FAVORITOS", false);
        conFavoritos.mostrar(servicio.conFavoritos(clientes), "Guardan pares en favoritos");
        metricas.getChildren().setAll(todos, nuevos, recurrentes, conFavoritos);
        filtrar();
    }

    private void filtrar() {
        visibles = servicio.buscar(clientes, campoBuscar.getText(), filtro, orden);
        botonOrden.setText(orden.getTexto());
        if (posicionDe(elegido) < 0) {
            elegido = visibles.isEmpty() ? "" : visibles.get(0).getCliente().getCorreo();
        }
        irAPagina(Math.max(0, posicionDe(elegido)) / POR_PAGINA);
        pintarPerfil();
    }

    private int posicionDe(String correo) {
        for (int i = 0; i < visibles.size(); i++) {
            if (visibles.get(i).getCliente().getCorreo().equalsIgnoreCase(correo)) {
                return i;
            }
        }
        return -1;
    }

    private void irAPagina(int nueva) {
        int paginas = Math.max(1, (visibles.size() + POR_PAGINA - 1) / POR_PAGINA);
        pagina = Math.max(0, Math.min(nueva, paginas - 1));
        int desde = pagina * POR_PAGINA;
        int hasta = Math.min(visibles.size(), desde + POR_PAGINA);
        filas.getChildren().clear();
        for (int i = desde; i < hasta; i++) {
            filas.getChildren().add(crearFila(visibles.get(i), i));
        }
        if (visibles.isEmpty()) {
            Label vacio = new Label(clientes.isEmpty() ? "Aún no hay clientes registrados." : "Ningún cliente coincide con tu búsqueda.");
            vacio.getStyleClass().addAll("texto", "fila-vacia");
            filas.getChildren().add(vacio);
        }
        textoPagina.setText("Mostrando " + (hasta - desde) + " de " + visibles.size() + (visibles.size() == 1 ? " cliente" : " clientes"));
        botonAnterior.setDisable(pagina == 0);
        botonSiguiente.setDisable(pagina >= paginas - 1);
    }

    private GridPane crearFila(ResumenCliente resumen, int posicion) {
        Cliente cliente = resumen.getCliente();
        GridPane fila = new GridPane();
        columnas(fila);
        fila.getStyleClass().add("fila-admin");
        if (cliente.getCorreo().equalsIgnoreCase(elegido)) {
            fila.getStyleClass().add("fila-admin-activa");
        }
        Label nombre = texto(cliente.getNombre(), "negrita");
        Label correo = new Label(cliente.getCorreo());
        correo.getStyleClass().add("texto-suave");
        VBox datos = new VBox(nombre, correo);
        datos.setAlignment(Pos.CENTER_LEFT);
        datos.setMinWidth(0);
        HBox.setHgrow(datos, Priority.ALWAYS);
        HBox persona = new HBox(10, new Avatar(cliente, 36, posicion), datos);
        persona.setAlignment(Pos.CENTER_LEFT);
        Button ojo = new Button();
        ojo.getStyleClass().add("boton-icono");
        ojo.setGraphic(Iconos.ojo(Iconos.OSCURO, 18));
        ojo.setTooltip(new Tooltip("Ver perfil"));
        ojo.setOnAction(e -> elegir(cliente.getCorreo()));
        GridPane.setHalignment(ojo, HPos.RIGHT);
        fila.add(persona, 0, 0);
        fila.add(texto(cliente.getCiudad().isEmpty() ? "—" : cliente.getCiudad(), "texto-suave-grande"), 1, 0);
        fila.add(texto(String.valueOf(resumen.getCompras()), "texto-suave-grande"), 2, 0);
        fila.add(texto(Zapato.formatearPrecio(resumen.getGastado()), "negrita"), 3, 0);
        fila.add(texto(resumen.getUltimaCompra() == null ? "—" : Pedido.textoFechaCorta(resumen.getUltimaCompra()),
                "texto-suave-grande"), 4, 0);
        fila.add(PastillaAdmin.de(resumen.getEstado()), 5, 0);
        fila.add(ojo, 6, 0);
        fila.setOnMouseClicked(e -> elegir(cliente.getCorreo()));
        return fila;
    }

    private Label texto(String valor, String estilo) {
        Label label = new Label(valor);
        label.getStyleClass().addAll("texto", estilo);
        return label;
    }

    public void elegir(String correo) {
        elegido = correo;
        irAPagina(Math.max(0, posicionDe(correo)) / POR_PAGINA);
        pintarPerfil();
    }

    public void buscarCliente(String correo) {
        grupo.getToggles().get(0).setSelected(true);
        filtro = FiltroClientes.TODOS;
        elegido = correo;
        campoBuscar.clear();
        filtrar();
    }

    private void pintarPerfil() {
        int posicion = posicionDe(elegido);
        alternar(panelPerfil, posicion >= 0);
        alternar(panelVacio, posicion < 0);
        if (posicion < 0) {
            return;
        }
        ResumenCliente resumen = visibles.get(posicion);
        Cliente cliente = resumen.getCliente();
        avatar.getChildren().setAll(new Avatar(cliente, 64, posicion));
        nombrePerfil.setText(cliente.getNombre());
        correoPerfil.setText(cliente.getCorreo());
        estadoPerfil.getChildren().setAll(PastillaAdmin.de(resumen.getEstado()));
        telefonoPerfil.setText(cliente.getTelefono().isEmpty() ? "Sin teléfono" : FormatoCampo.agruparTelefono(cliente.getTelefono()));
        desdePerfil.setText(cliente.getRegistro() == null ? "Sin fecha" : Pedido.textoMes(cliente.getRegistro()));
        String ciudad = cliente.getCiudad();
        direccionPerfil.setText(cliente.getDireccion().isEmpty() ? (ciudad.isEmpty() ? "Sin dirección guardada" : ciudad)
                : cliente.getDireccion() + (ciudad.isEmpty() ? "" : ", " + ciudad));
        cifraPedidos.setText(String.valueOf(resumen.getCompras()));
        cifraPares.setText(String.valueOf(resumen.getPares()));
        cifraGastado.setText(Zapato.formatearPrecio(resumen.getGastado()));
        pintarPedidos(resumen);
        pintarFavoritos(resumen);
        botonPedidos.setDisable(resumen.getPedidos().estaVacia());
    }

    private void pintarPedidos(ResumenCliente resumen) {
        ultimosPedidos.getChildren().clear();
        ListaEnlazada<Pedido> pedidos = servicio.ultimosPedidos(resumen, 2);
        for (Pedido pedido : pedidos) {
            Label codigo = texto("#" + pedido.getCodigo(), "negrita");
            Label fecha = new Label(Pedido.textoFechaCorta(pedido.getFecha().toLocalDate()));
            fecha.getStyleClass().add("texto-suave");
            Region espacio = new Region();
            HBox.setHgrow(espacio, Priority.ALWAYS);
            Label total = texto(Zapato.formatearPrecio(pedido.getTotales().getTotal()), "negrita");
            total.setMinWidth(Region.USE_PREF_SIZE);
            HBox fila = new HBox(10, new VBox(codigo, fecha), espacio, PastillaAdmin.de(pedido.getEstado()), total);
            fila.setAlignment(Pos.CENTER_LEFT);
            ultimosPedidos.getChildren().add(fila);
        }
        if (pedidos.estaVacia()) {
            ultimosPedidos.getChildren().add(suave("Aún no ha comprado."));
        }
    }

    private void pintarFavoritos(ResumenCliente resumen) {
        favoritos.getChildren().clear();
        ListaEnlazada<Zapato> pares = servicio.favoritos(resumen, 2);
        for (Zapato zapato : pares) {
            VBox datos = new VBox(texto(zapato.getNombre(), "negrita"), suave(zapato.estaAgotado() ? "Agotado ahora"
                    : zapato.getPrecioTexto() + " · " + zapato.getStockTotal() + (zapato.getStockTotal() == 1 ? " par" : " pares")));
            datos.setAlignment(Pos.CENTER_LEFT);
            datos.setMinWidth(0);
            HBox fila = new HBox(12, new Miniatura(zapato, 44), datos);
            fila.setAlignment(Pos.CENTER_LEFT);
            favoritos.getChildren().add(fila);
        }
        if (pares.estaVacia()) {
            favoritos.getChildren().add(suave("No tiene pares guardados."));
        }
    }

    private Label suave(String valor) {
        Label label = new Label(valor);
        label.getStyleClass().add("texto-suave");
        return label;
    }

    private void agregar() {
        Cliente nuevo = PopupNuevoClienteController.mostrar();
        if (nuevo != null) {
            elegido = nuevo.getCorreo();
            grupo.getToggles().get(0).setSelected(true);
            filtro = FiltroClientes.TODOS;
            campoBuscar.clear();
            cargar();
            barraController.actualizarAvisos();
            avisar(nuevo.getNombre() + " ya tiene cuenta. Crea su contraseña en “¿Olvidaste tu contraseña?” con su correo y su nombre.");
        }
    }

    private void copiarCorreo() {
        ClipboardContent contenido = new ClipboardContent();
        contenido.putString(correoPerfil.getText());
        Clipboard.getSystemClipboard().setContent(contenido);
        avisar("Copiamos " + correoPerfil.getText() + ". Pégalo en tu correo para escribirle.");
    }

    private void avisar(String texto) {
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
        buscarCliente(estado);
    }
}
