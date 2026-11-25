package com.nuctua.controlador;

import com.nuctua.modelo.Cliente;
import com.nuctua.modelo.LineaPedido;
import com.nuctua.modelo.Pedido;
import com.nuctua.modelo.ResumenCliente;
import com.nuctua.modelo.SesionActual;
import com.nuctua.modelo.Zapato;
import com.nuctua.servicio.BusquedaGlobalServicio;
import com.nuctua.servicio.FiltroGlobal;
import com.nuctua.servicio.ResultadoGlobal;
import com.nuctua.util.Iconos;
import com.nuctua.vista.Avatar;
import com.nuctua.vista.Miniatura;
import com.nuctua.vista.PastillaAdmin;
import com.nuctua.vista.Ventana;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.function.Consumer;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleButton;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;

public class AdminBuscarController implements ConEstadoNavegacion {

    private static final int FILAS_INICIALES = 6;
    private static final double[] COLUMNAS_PRODUCTO = {38, 14, 14, 17, 17};
    private static final double[] COLUMNAS_PEDIDO = {30, 24, 14, 17, 15};
    private static final double[] COLUMNAS_CLIENTE = {40, 22, 20, 18};

    @FXML
    private BarraAdminController barraController;
    @FXML
    private Hyperlink enlaceVolver;
    @FXML
    private HBox cajaBuscar;
    @FXML
    private TextField campoBuscar;
    @FXML
    private Button botonBuscar;
    @FXML
    private FlowPane alcances;
    @FXML
    private Label infoBusqueda;
    @FXML
    private VBox secciones;
    @FXML
    private Hyperlink enlaceLimpiar;
    @FXML
    private FlowPane filtroEstado;
    @FXML
    private FlowPane filtroFecha;
    @FXML
    private FlowPane filtroCategoria;
    @FXML
    private VBox recientes;

    private final BusquedaGlobalServicio servicio = new BusquedaGlobalServicio();
    private final FiltroGlobal filtro = new FiltroGlobal();
    private final boolean[] completas = new boolean[4];
    private int alcance;

    @FXML
    private void initialize() {
        barraController.marcar(Ventana.ADMIN_BUSCAR);
        enlaceVolver.setGraphic(Iconos.flechaIzquierda(Iconos.OSCURO, 18));
        enlaceVolver.setText(Navegador.textoVolver());
        enlaceVolver.setOnAction(e -> Navegador.volver());
        cajaBuscar.getChildren().add(0, Iconos.buscar(Iconos.OSCURO, 22));
        campoBuscar.textProperty().addListener((obs, antes, ahora) -> buscar());
        campoBuscar.setOnAction(e -> confirmar());
        botonBuscar.setOnAction(e -> confirmar());
        enlaceLimpiar.setOnAction(e -> limpiar());
        for (FiltroGlobal.Estado estado : FiltroGlobal.Estado.values()) {
            agregarChip(filtroEstado, estado.getTexto(), activo -> filtro.setEstado(activo ? estado : null));
        }
        for (FiltroGlobal.Fecha fecha : FiltroGlobal.Fecha.values()) {
            agregarChip(filtroFecha, fecha.getTexto(), activo -> filtro.setFecha(activo ? fecha : null));
        }
        for (String categoria : servicio.categorias()) {
            agregarChip(filtroCategoria, categoria, activo -> filtro.setCategoria(activo ? categoria : null));
        }
        pintarRecientes();
        buscar();
    }

    private void agregarChip(FlowPane grupo, String texto, Consumer<Boolean> alCambiar) {
        ToggleButton chip = new ToggleButton(texto);
        chip.getStyleClass().add("chip-perfil");
        chip.setOnAction(e -> {
            for (Node otro : grupo.getChildren()) {
                if (otro != chip) {
                    ((ToggleButton) otro).setSelected(false);
                }
            }
            alCambiar.accept(chip.isSelected());
            buscar();
        });
        grupo.getChildren().add(chip);
    }

    private void limpiar() {
        filtro.limpiar();
        for (FlowPane grupo : new FlowPane[]{filtroEstado, filtroFecha, filtroCategoria}) {
            for (Node chip : grupo.getChildren()) {
                ((ToggleButton) chip).setSelected(false);
            }
        }
        buscar();
    }

    private void confirmar() {
        String texto = campoBuscar.getText().trim();
        if (!texto.isEmpty()) {
            SesionActual.getInstancia().getBusquedasAdmin().registrarSinRepetir(texto);
            pintarRecientes();
        }
        buscar();
    }

    private void pintarRecientes() {
        recientes.getChildren().clear();
        for (String texto : SesionActual.getInstancia().getBusquedasAdmin().aArrayList()) {
            Hyperlink enlace = new Hyperlink(texto);
            enlace.getStyleClass().add("enlace-reciente");
            enlace.setGraphic(Iconos.reloj(Iconos.OSCURO, 16));
            enlace.setOnAction(e -> {
                campoBuscar.setText(texto);
                confirmar();
            });
            recientes.getChildren().add(enlace);
        }
        if (recientes.getChildren().isEmpty()) {
            Label vacio = new Label("Aquí quedan tus últimas búsquedas.");
            vacio.getStyleClass().add("texto-suave");
            recientes.getChildren().add(vacio);
        }
    }

    private void buscar() {
        filtro.setTexto(campoBuscar.getText());
        ResultadoGlobal resultado = servicio.buscar(filtro, LocalDate.now());
        pintarAlcances(resultado);
        infoBusqueda.setText(resultado.getDescripcion());
        secciones.getChildren().clear();
        if (muestra(1, resultado.getProductos().size())) {
            secciones.getChildren().add(seccion("PRODUCTOS", 1, resultado.getProductos(), this::filaProducto));
        }
        if (muestra(2, resultado.getPedidos().size())) {
            secciones.getChildren().add(seccion("PEDIDOS", 2, resultado.getPedidos(), this::filaPedido));
        }
        if (muestra(3, resultado.getClientes().size())) {
            secciones.getChildren().add(seccion("CLIENTES", 3, resultado.getClientes(), this::filaCliente));
        }
        if (secciones.getChildren().isEmpty()) {
            Label titulo = new Label("SIN RESULTADOS");
            titulo.getStyleClass().add("titulo-chico");
            Label texto = new Label("Prueba con otra palabra o limpia los filtros.");
            texto.getStyleClass().add("texto-suave");
            VBox vacia = new VBox(6, titulo, texto);
            vacia.setAlignment(Pos.CENTER);
            vacia.getStyleClass().addAll("tarjeta-punteada", "otro-par");
            secciones.getChildren().add(vacia);
        }
    }

    private boolean muestra(int tipo, int cantidad) {
        return cantidad > 0 && (alcance == 0 || alcance == tipo);
    }

    private void pintarAlcances(ResultadoGlobal resultado) {
        String[] textos = {"Todo · " + resultado.getTotal(), "Productos · " + resultado.getProductos().size(),
            "Pedidos · " + resultado.getPedidos().size(), "Clientes · " + resultado.getClientes().size()};
        alcances.getChildren().clear();
        for (int i = 0; i < textos.length; i++) {
            int elegido = i;
            ToggleButton chip = new ToggleButton(textos[i]);
            chip.getStyleClass().addAll("chip-perfil", "chip-admin");
            chip.setSelected(alcance == i);
            chip.setOnAction(e -> {
                alcance = elegido;
                buscar();
            });
            alcances.getChildren().add(chip);
        }
    }

    private interface CreadorFila<T> {

        GridPane crear(T dato, int posicion);
    }

    private <T> VBox seccion(String titulo, int tipo, ArrayList<T> datos, CreadorFila<T> creador) {
        Label nombre = new Label(titulo);
        nombre.getStyleClass().add("titulo-seccion-admin");
        Region espacio = new Region();
        HBox.setHgrow(espacio, Priority.ALWAYS);
        Label cuenta = new Label(datos.size() + (datos.size() == 1 ? " resultado" : " resultados"));
        cuenta.getStyleClass().add("texto-suave");
        HBox cabecera = new HBox(nombre, espacio, cuenta);
        cabecera.setAlignment(Pos.CENTER_LEFT);
        cabecera.getStyleClass().add("encabezado-admin");
        VBox tabla = new VBox(cabecera);
        tabla.getStyleClass().addAll("tarjeta", "tabla-admin");
        int visibles = completas[tipo] ? datos.size() : Math.min(FILAS_INICIALES, datos.size());
        for (int i = 0; i < visibles; i++) {
            tabla.getChildren().add(creador.crear(datos.get(i), i));
        }
        if (datos.size() > FILAS_INICIALES) {
            Hyperlink ver = new Hyperlink(completas[tipo] ? "Ver menos" : "Ver los " + datos.size());
            ver.getStyleClass().add("enlace-texto");
            ver.setOnAction(e -> {
                completas[tipo] = !completas[tipo];
                buscar();
            });
            HBox pie = new HBox(ver);
            pie.setAlignment(Pos.CENTER);
            pie.getStyleClass().add("pie-admin");
            tabla.getChildren().add(pie);
        } else {
            tabla.getChildren().get(visibles).getStyleClass().add("fila-admin-ultima");
        }
        return tabla;
    }

    private GridPane filaProducto(Zapato zapato, int posicion) {
        Label pares = texto(zapato.getStockTotal() + (zapato.getStockTotal() == 1 ? " par" : " pares"), "texto-suave-grande");
        if (zapato.estaAgotado()) {
            pares.getStyleClass().add("texto-alerta");
        }
        return fila(COLUMNAS_PRODUCTO, () -> editar(zapato), persona(new Miniatura(zapato, 44), zapato.getNombre(), zapato.getSku() + " · " + zapato.getMarca()),
                texto(zapato.getCategoria(), "texto-suave-grande"), pares, texto(zapato.getPrecioTexto(), "negrita"),
                PastillaAdmin.activo(zapato));
    }

    private GridPane filaPedido(Pedido pedido, int posicion) {
        String detalle = "Sin detalle";
        if (!pedido.getLineas().estaVacia()) {
            LineaPedido primera = pedido.getLineas().obtener(0);
            int otros = pedido.getTotales().getPares() - 1;
            detalle = primera.getNombre() + (otros > 0 ? " + " + otros : "");
        }
        return fila(COLUMNAS_PEDIDO, () -> Navegador.mostrar(Ventana.ADMIN_PEDIDOS), persona(null, "#" + pedido.getCodigo(), detalle),
                texto(pedido.getEnvio().getNombre(), "texto-suave-grande"),
                texto(Pedido.textoFechaCorta(pedido.getFecha().toLocalDate()), "texto-suave-grande"),
                texto(Zapato.formatearPrecio(pedido.getTotales().getTotal()), "negrita"), PastillaAdmin.de(pedido.getEstado()));
    }

    private GridPane filaCliente(ResumenCliente resumen, int posicion) {
        Cliente cliente = resumen.getCliente();
        return fila(COLUMNAS_CLIENTE, () -> verCliente(cliente), persona(new Avatar(cliente, 40, posicion), cliente.getNombre(), cliente.getCorreo()),
                texto(cliente.getCiudad().isEmpty() ? "—" : cliente.getCiudad(), "texto-suave-grande"),
                texto(resumen.getCompras() + (resumen.getCompras() == 1 ? " pedido" : " pedidos"), "texto-suave-grande"),
                PastillaAdmin.de(resumen.getEstado()));
    }

    private GridPane fila(double[] anchos, Runnable alElegir, Node... celdas) {
        GridPane fila = new GridPane();
        fila.getStyleClass().add("fila-admin");
        for (int i = 0; i < anchos.length; i++) {
            ColumnConstraints columna = new ColumnConstraints();
            columna.setPercentWidth(anchos[i]);
            columna.setMinWidth(0);
            fila.getColumnConstraints().add(columna);
            fila.add(celdas[i], i, 0);
        }
        fila.setOnMouseClicked(e -> alElegir.run());
        return fila;
    }

    private HBox persona(Node imagen, String titulo, String detalle) {
        Label suave = new Label(detalle);
        suave.getStyleClass().add("texto-suave");
        VBox datos = new VBox(texto(titulo, "negrita"), suave);
        datos.setAlignment(Pos.CENTER_LEFT);
        datos.setMinWidth(0);
        HBox caja = new HBox(12, datos);
        if (imagen != null) {
            caja.getChildren().add(0, imagen);
        }
        caja.setAlignment(Pos.CENTER_LEFT);
        return caja;
    }

    private Label texto(String valor, String estilo) {
        Label label = new Label(valor);
        label.getStyleClass().addAll("texto", estilo);
        return label;
    }

    private void editar(Zapato zapato) {
        AdminStockTallaController editor = Navegador.mostrar(Ventana.ADMIN_STOCK_TALLA);
        if (editor != null) {
            editor.mostrar(zapato);
        }
    }

    private void verCliente(Cliente cliente) {
        AdminClientesController clientes = Navegador.mostrar(Ventana.ADMIN_CLIENTES);
        if (clientes != null) {
            clientes.buscarCliente(cliente.getCorreo());
        }
    }

    @Override
    public String capturarEstado() {
        return campoBuscar.getText().trim();
    }

    @Override
    public void restaurarEstado(String estado) {
        campoBuscar.setText(estado);
    }
}
