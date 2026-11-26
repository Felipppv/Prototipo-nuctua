package com.nuctua.controlador;

import com.nuctua.estructuras.ColaPedidos;
import com.nuctua.modelo.Actividad;
import com.nuctua.modelo.DatosEnvio;
import com.nuctua.modelo.EstadoPedido;
import com.nuctua.modelo.GuiaEnvio;
import com.nuctua.modelo.LineaPedido;
import com.nuctua.modelo.Pedido;
import com.nuctua.modelo.TotalesCompra;
import com.nuctua.modelo.Zapato;
import com.nuctua.servicio.CatalogoServicio;
import com.nuctua.servicio.FiltroPedidos;
import com.nuctua.servicio.PedidosServicio;
import com.nuctua.servicio.ResultadoEstado;
import com.nuctua.util.FormatoCampo;
import com.nuctua.util.Iconos;
import com.nuctua.vista.Miniatura;
import com.nuctua.vista.PastillaAdmin;
import com.nuctua.vista.TarjetaMetrica;
import com.nuctua.vista.Ventana;
import java.time.LocalDate;
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
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

public class AdminPedidosController implements ConEstadoNavegacion {

    private static final int POR_PAGINA = 6;
    private static final int MAXIMO_EN_COLA = 8;
    private static final double[] COLUMNAS = {17, 20, 12, 9, 16, 19, 7};

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
    private Label textoCola;
    @FXML
    private FlowPane fichas;
    @FXML
    private Button botonAtender;
    @FXML
    private StackPane iconoBuscar;
    @FXML
    private TextField campoBuscar;
    @FXML
    private FlowPane filtros;
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
    private Label pedidoDetalle;
    @FXML
    private StackPane estadoDetalle;
    @FXML
    private Label turnoDetalle;
    @FXML
    private Label clienteDetalle;
    @FXML
    private Label contactoDetalle;
    @FXML
    private Label direccionDetalle;
    @FXML
    private Label indicacionesDetalle;
    @FXML
    private Label pagoDetalle;
    @FXML
    private Label tituloPares;
    @FXML
    private VBox lineas;
    @FXML
    private VBox cuentas;
    @FXML
    private Label total;
    @FXML
    private Button botonEstado;
    @FXML
    private Button botonAvanzar;
    @FXML
    private Hyperlink enlaceGuia;
    @FXML
    private Hyperlink enlaceFactura;

    private final PedidosServicio servicio = new PedidosServicio();
    private final ToggleGroup grupo = new ToggleGroup();
    private final Label textoEstado = new Label();
    private ArrayList<Pedido> visibles = new ArrayList<>();
    private FiltroPedidos filtro = FiltroPedidos.TODOS;
    private int pagina;
    private int elegido;

    @FXML
    private void initialize() {
        barraController.marcar(Ventana.ADMIN_PEDIDOS);
        enlaceVolver.setGraphic(Iconos.flechaIzquierda(Iconos.OSCURO, 18));
        enlaceVolver.setText(Navegador.textoVolver());
        enlaceVolver.setOnAction(e -> Navegador.volver());
        iconoBuscar.getChildren().add(Iconos.buscar(Iconos.OSCURO, 18));
        campoBuscar.textProperty().addListener((obs, antes, ahora) -> filtrar());
        for (FiltroPedidos opcion : FiltroPedidos.values()) {
            agregarFiltro(opcion);
        }
        botonAtender.setOnAction(e -> atenderSiguiente());
        botonAnterior.setGraphic(Iconos.flechaIzquierda(Iconos.OSCURO, 14));
        botonSiguiente.setGraphic(Iconos.flechaDerecha(Iconos.OSCURO, 14));
        botonSiguiente.setContentDisplay(ContentDisplay.RIGHT);
        botonAnterior.setOnAction(e -> irAPagina(pagina - 1));
        botonSiguiente.setOnAction(e -> irAPagina(pagina + 1));
        Region hueco = new Region();
        HBox.setHgrow(hueco, Priority.ALWAYS);
        HBox contenido = new HBox(textoEstado, hueco, Iconos.flechaAbajo(Iconos.OSCURO, 16));
        contenido.setAlignment(Pos.CENTER_LEFT);
        contenido.prefWidthProperty().bind(botonEstado.widthProperty().subtract(38));
        textoEstado.getStyleClass().addAll("texto", "negrita");
        botonEstado.setGraphic(contenido);
        botonEstado.setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
        botonEstado.setOnAction(e -> elegirEstado(actual()));
        botonAvanzar.setOnAction(e -> avanzar(actual()));
        enlaceGuia.setOnAction(e -> PopupGuiaController.mostrar(new GuiaEnvio(actual())));
        enlaceFactura.setOnAction(e -> PopupFacturaController.mostrar(actual()));
        columnas(encabezado);
        String[] titulos = {"PEDIDO", "CLIENTE", "FECHA", "PARES", "TOTAL", "ESTADO"};
        for (int i = 0; i < titulos.length; i++) {
            Label titulo = new Label(titulos[i]);
            titulo.getStyleClass().addAll("etiqueta-campo", "texto-suave-grande");
            encabezado.add(titulo, i, 0);
        }
        cargar();
    }

    private void agregarFiltro(FiltroPedidos opcion) {
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

    private void cargar() {
        int nuevos = servicio.cuantos(EstadoPedido.NUEVO);
        int preparando = servicio.cuantos(EstadoPedido.PREPARANDO);
        int enviados = servicio.cuantos(EstadoPedido.ENVIADO);
        int activos = nuevos + preparando + enviados;
        sello.setText(activos + (activos == 1 ? " pedido activo" : " pedidos activos"));
        TarjetaMetrica tarjetaNuevos = new TarjetaMetrica("NUEVOS", true);
        tarjetaNuevos.mostrar(nuevos, "Esperando confirmación");
        TarjetaMetrica tarjetaPreparando = new TarjetaMetrica("EN PREPARACIÓN", false);
        tarjetaPreparando.mostrar(preparando, "Empacando pares");
        TarjetaMetrica tarjetaEnviados = new TarjetaMetrica("ENVIADOS", false);
        tarjetaEnviados.mostrar(enviados, "En camino al cliente");
        TarjetaMetrica tarjetaEntregados = new TarjetaMetrica("ENTREGADOS HOY", false);
        tarjetaEntregados.mostrar(servicio.entregadosEl(LocalDate.now()), "Entregados con éxito");
        metricas.getChildren().setAll(tarjetaNuevos, tarjetaPreparando, tarjetaEnviados, tarjetaEntregados);
        pintarCola();
        filtrar();
    }

    private void pintarCola() {
        ColaPedidos cola = servicio.cola();
        fichas.getChildren().clear();
        int turno = 1;
        for (Pedido pedido : cola) {
            if (turno > MAXIMO_EN_COLA) {
                Label resto = new Label("+" + (cola.tamanio() - MAXIMO_EN_COLA) + " más");
                resto.getStyleClass().addAll("texto", "negrita");
                fichas.getChildren().add(resto);
                break;
            }
            Button ficha = new Button(turno + " · #" + pedido.getCodigo());
            ficha.getStyleClass().add("ficha-cola");
            if (turno == 1) {
                ficha.getStyleClass().add("ficha-cola-frente");
            }
            ficha.setTooltip(new Tooltip(pedido.getEnvio().getNombre() + " · " + pedido.getTotales().getTextoPares()));
            ficha.setOnAction(e -> elegir(pedido.getNumero()));
            fichas.getChildren().add(ficha);
            turno++;
        }
        int pares = cola.paresPorPreparar();
        textoCola.setText(cola.estaVacia() ? "No hay pedidos esperando. Los nuevos entran al final de la cola."
                : cola.tamanio() + (cola.tamanio() == 1 ? " pedido · " : " pedidos · ") + pares
                + (pares == 1 ? " par por empacar. " : " pares por empacar. ") + "Se atiende primero el que llegó primero.");
        botonAtender.setDisable(cola.estaVacia());
        alternar(fichas, !cola.estaVacia());
    }

    private void filtrar() {
        visibles = servicio.buscar(campoBuscar.getText(), filtro);
        if (posicionDe(elegido) < 0) {
            elegido = visibles.isEmpty() ? 0 : visibles.get(0).getNumero();
        }
        irAPagina(Math.max(0, posicionDe(elegido)) / POR_PAGINA);
        pintarDetalle();
    }

    private int posicionDe(int numero) {
        for (int i = 0; i < visibles.size(); i++) {
            if (visibles.get(i).getNumero() == numero) {
                return i;
            }
        }
        return -1;
    }

    private Pedido actual() {
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
            Label vacio = new Label(servicio.todos().estaVacia() ? "Aún no hay pedidos. Aparecerán cuando un cliente compre."
                    : "Ningún pedido coincide con tu búsqueda.");
            vacio.getStyleClass().addAll("texto", "fila-vacia");
            filas.getChildren().add(vacio);
        }
        textoPagina.setText("Mostrando " + (hasta - desde) + " de " + visibles.size() + (visibles.size() == 1 ? " pedido" : " pedidos"));
        botonAnterior.setDisable(pagina == 0);
        botonSiguiente.setDisable(pagina >= paginas - 1);
    }

    private GridPane crearFila(Pedido pedido) {
        GridPane fila = new GridPane();
        columnas(fila);
        fila.getStyleClass().add("fila-admin");
        if (pedido.getNumero() == elegido) {
            fila.getStyleClass().add("fila-admin-activa");
        }
        TotalesCompra totales = pedido.getTotales();
        Button ver = new Button();
        ver.getStyleClass().addAll("boton-icono", "boton-icono-fila");
        ver.setGraphic(Iconos.ojo(Iconos.OSCURO, 17));
        ver.setTooltip(new Tooltip("Ver detalle"));
        ver.setOnAction(e -> elegir(pedido.getNumero()));
        GridPane.setHalignment(ver, HPos.RIGHT);
        fila.add(texto("#" + pedido.getCodigo(), "negrita"), 0, 0);
        fila.add(texto(pedido.getEnvio().getNombre(), "texto"), 1, 0);
        fila.add(texto(Pedido.textoFechaCorta(pedido.getFecha().toLocalDate()), "texto-suave-grande"), 2, 0);
        fila.add(texto(String.valueOf(totales.getPares()), "texto-suave-grande"), 3, 0);
        fila.add(texto(Zapato.formatearPrecio(totales.getTotal()), "negrita"), 4, 0);
        fila.add(PastillaAdmin.de(pedido.getEstado()), 5, 0);
        fila.add(ver, 6, 0);
        fila.setOnMouseClicked(e -> elegir(pedido.getNumero()));
        return fila;
    }

    private Label texto(String valor, String estilo) {
        Label label = new Label(valor);
        label.setWrapText(true);
        label.setMinHeight(Region.USE_PREF_SIZE);
        label.getStyleClass().addAll("texto", estilo);
        return label;
    }

    public void elegir(int numero) {
        elegido = numero;
        if (posicionDe(numero) < 0) {
            limpiarFiltros();
            return;
        }
        irAPagina(posicionDe(numero) / POR_PAGINA);
        pintarDetalle();
    }

    public void mostrarDe(String correo) {
        elegido = 0;
        grupo.getToggles().get(0).setSelected(true);
        filtro = FiltroPedidos.TODOS;
        campoBuscar.setText(correo);
        filtrar();
    }

    private void limpiarFiltros() {
        grupo.getToggles().get(0).setSelected(true);
        filtro = FiltroPedidos.TODOS;
        campoBuscar.clear();
        filtrar();
    }

    private void pintarDetalle() {
        Pedido pedido = actual();
        alternar(panelDetalle, pedido != null);
        alternar(panelVacio, pedido == null);
        if (pedido == null) {
            return;
        }
        DatosEnvio envio = pedido.getEnvio();
        pedidoDetalle.setText("Pedido #" + pedido.getCodigo() + " · " + Pedido.textoFechaCorta(pedido.getFecha().toLocalDate())
                + ", " + Actividad.describirHora(pedido.getFecha()));
        estadoDetalle.getChildren().setAll(PastillaAdmin.de(pedido.getEstado()));
        String turno = servicio.describirTurno(pedido.getNumero());
        turnoDetalle.setText(turno);
        alternar(turnoDetalle, !turno.isEmpty());
        clienteDetalle.setText(envio.getNombre());
        contactoDetalle.setText(envio.getCorreo() + " · " + FormatoCampo.agruparTelefono(envio.getTelefono()));
        direccionDetalle.setText(envio.getDireccion() + ", " + envio.getCiudad());
        indicacionesDetalle.setText(envio.getIndicaciones());
        alternar(indicacionesDetalle, !envio.getIndicaciones().isEmpty());
        pagoDetalle.setText(pedido.getTextoMetodo() + " · " + pedido.getEstadoPago());
        pintarLineas(pedido);
        pintarCuentas(pedido);
        pintarAcciones(pedido);
    }

    private void pintarLineas(Pedido pedido) {
        CatalogoServicio catalogo = new CatalogoServicio();
        tituloPares.setText("PARES (" + pedido.getTotales().getPares() + ")");
        lineas.getChildren().clear();
        for (LineaPedido linea : pedido.getLineas()) {
            Zapato zapato = catalogo.buscarPorSku(linea.getSku());
            Label detalle = new Label("Talla " + linea.getTalla() + " · x" + linea.getCantidad());
            detalle.getStyleClass().add("texto-suave");
            Label nombre = texto(linea.getNombre(), "negrita");
            nombre.setWrapText(true);
            VBox textos = new VBox(nombre, detalle);
            textos.setAlignment(Pos.CENTER_LEFT);
            textos.setMinWidth(0);
            HBox.setHgrow(textos, Priority.ALWAYS);
            Label precio = new Label(Zapato.formatearPrecio(linea.getSubtotal()));
            precio.getStyleClass().add("nombre-visto");
            precio.setMinWidth(Region.USE_PREF_SIZE);
            HBox fila = new HBox(12, textos, precio);
            if (zapato != null) {
                fila.getChildren().add(0, new Miniatura(zapato, 48));
            }
            fila.setAlignment(Pos.CENTER_LEFT);
            lineas.getChildren().add(fila);
        }
        if (pedido.getLineas().estaVacia()) {
            lineas.getChildren().add(texto("No encontramos el detalle de este pedido.", "texto-suave-grande"));
        }
    }

    private void pintarCuentas(Pedido pedido) {
        TotalesCompra totales = pedido.getTotales();
        cuentas.getChildren().setAll(cuenta("Subtotal (" + totales.getTextoPares() + ")", Zapato.formatearPrecio(totales.getSubtotal())));
        if (totales.getDescuento() > 0) {
            cuentas.getChildren().add(cuenta(pedido.getCupon().isEmpty() ? "Descuento" : "Descuento (" + pedido.getCupon() + ")",
                    totales.getTextoDescuento()));
        }
        cuentas.getChildren().add(cuenta("Envío", totales.getTextoEnvio()));
        total.setText(Zapato.formatearPrecio(totales.getTotal()));
    }

    private HBox cuenta(String titulo, String valor) {
        Region espacio = new Region();
        HBox.setHgrow(espacio, Priority.ALWAYS);
        return new HBox(texto(titulo, "texto-suave-grande"), espacio, texto(valor, "negrita"));
    }

    private void pintarAcciones(Pedido pedido) {
        EstadoPedido estado = pedido.getEstado();
        textoEstado.setText(estado.getTexto());
        botonEstado.setAccessibleText("Estado del pedido: " + estado.getTexto());
        botonEstado.setDisable(estado.esFinal());
        alternar(botonAvanzar, !estado.esFinal());
        boolean esperaTurno = estado == EstadoPedido.NUEVO && !servicio.cola().esSuTurno(pedido.getNumero());
        botonAvanzar.setDisable(esperaTurno);
        botonAvanzar.setText(switch (estado) {
            case NUEVO -> esperaTurno ? "ESPERA SU TURNO" : "PASAR A PREPARACIÓN →";
            case PREPARANDO -> "DESPACHAR PEDIDO →";
            default -> "MARCAR ENTREGADO →";
        });
        alternar(enlaceGuia, estado != EstadoPedido.CANCELADO);
    }

    private void atenderSiguiente() {
        ResultadoEstado resultado = servicio.atenderSiguiente();
        if (resultado.esExito()) {
            elegido = resultado.getPedido().getNumero();
        }
        terminar(resultado);
    }

    private void avanzar(Pedido pedido) {
        terminar(servicio.cambiarEstado(pedido.getNumero(), pedido.getEstado().siguiente()));
    }

    private void elegirEstado(Pedido pedido) {
        EstadoPedido destino = PopupEstadoPedidoController.elegir(pedido);
        if (destino != null && destino != pedido.getEstado()) {
            terminar(servicio.cambiarEstado(pedido.getNumero(), destino));
        }
    }

    private void terminar(ResultadoEstado resultado) {
        int numero = elegido;
        cargar();
        if (resultado.esExito() && posicionDe(numero) < 0) {
            elegir(numero);
        }
        barraController.actualizarAvisos();
        textoAviso.setText(resultado.getMensaje());
        alternar(aviso, true);
    }

    private void alternar(Node nodo, boolean visible) {
        nodo.setVisible(visible);
        nodo.setManaged(visible);
    }

    @Override
    public String capturarEstado() {
        return elegido == 0 ? "" : elegido + " " + campoBuscar.getText().trim();
    }

    @Override
    public void restaurarEstado(String estado) {
        int corte = estado.indexOf(' ');
        String numero = corte < 0 ? estado : estado.substring(0, corte);
        if (corte >= 0) {
            campoBuscar.setText(estado.substring(corte + 1));
        }
        if (numero.matches("[0-9]{1,9}")) {
            elegir(Integer.parseInt(numero));
        }
    }
}
