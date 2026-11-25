package com.nuctua.controlador;

import com.nuctua.estructuras.ListaEnlazada;
import com.nuctua.modelo.CambioStock;
import com.nuctua.modelo.ResumenInventario;
import com.nuctua.modelo.SesionActual;
import com.nuctua.modelo.TallaStock;
import com.nuctua.modelo.Zapato;
import com.nuctua.servicio.FiltroInventario;
import com.nuctua.servicio.InventarioServicio;
import com.nuctua.servicio.ResultadoInventario;
import com.nuctua.util.Iconos;
import com.nuctua.vista.TarjetaInventario;
import com.nuctua.vista.Ventana;
import java.util.ArrayList;
import javafx.fxml.FXML;
import javafx.geometry.Side;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

public class AdminInventarioController {

    @FXML
    private BarraAdminController barraController;
    @FXML
    private Hyperlink enlaceVolver;
    @FXML
    private Label sello;
    @FXML
    private HBox aviso;
    @FXML
    private Label textoAviso;
    @FXML
    private StackPane iconoBuscar;
    @FXML
    private TextField campoBuscar;
    @FXML
    private HBox filtros;
    @FXML
    private VBox lista;
    @FXML
    private VBox listaVacia;
    @FXML
    private Label cifraPares;
    @FXML
    private Label cifraActivos;
    @FXML
    private Label cifraPoco;
    @FXML
    private Label cifraAgotados;
    @FXML
    private Label cifraCambios;
    @FXML
    private Button botonGuardar;
    @FXML
    private Hyperlink enlaceDescartar;
    @FXML
    private StackPane iconoSeguridad;
    @FXML
    private Button botonNuevo;

    private final InventarioServicio inventario = new InventarioServicio();
    private final ListaEnlazada<CambioStock> cambios = SesionActual.getInstancia().getCambiosStock();
    private final ContextMenu menuTallas = new ContextMenu();
    private final ToggleGroup grupo = new ToggleGroup();
    private FiltroInventario filtro = FiltroInventario.TODOS;

    @FXML
    private void initialize() {
        barraController.marcar(Ventana.ADMIN_INVENTARIO);
        enlaceVolver.setGraphic(Iconos.flechaIzquierda(Iconos.OSCURO, 18));
        enlaceVolver.setTooltip(new Tooltip("Cierra tu sesión de administrador y va al ingreso de la tienda"));
        enlaceVolver.setOnAction(e -> volverATienda());
        iconoBuscar.getChildren().add(Iconos.buscar(Iconos.OSCURO, 18));
        iconoSeguridad.getChildren().add(Iconos.candado(Iconos.OSCURO, 14));
        botonNuevo.setGraphic(Iconos.mas(Iconos.OSCURO, 16));
        botonNuevo.setOnAction(e -> Navegador.mostrar(Ventana.ADMIN_REGISTRAR_PRODUCTO));
        menuTallas.getStyleClass().add("menu-orden");
        for (FiltroInventario opcion : FiltroInventario.values()) {
            ToggleButton boton = new ToggleButton(opcion.getTexto());
            boton.getStyleClass().addAll("chip-perfil", "chip-admin");
            boton.setToggleGroup(grupo);
            boton.setSelected(opcion == filtro);
            boton.setOnAction(e -> {
                boton.setSelected(true);
                filtro = opcion;
                pintar();
            });
            filtros.getChildren().add(boton);
        }
        campoBuscar.textProperty().addListener((obs, antes, ahora) -> pintar());
        botonGuardar.setOnAction(e -> guardar());
        enlaceDescartar.setOnAction(e -> descartar());
        pintar();
    }

    private void pintar() {
        ResumenInventario resumen = inventario.resumen();
        sello.setText(resumen.getPares() + (resumen.getPares() == 1 ? " par en bodega" : " pares en bodega"));
        cifraPares.setText(String.valueOf(resumen.getPares()));
        cifraActivos.setText(String.valueOf(resumen.getActivos()));
        cifraPoco.setText(String.valueOf(resumen.getPocoStock()));
        cifraAgotados.setText(String.valueOf(resumen.getAgotados()));
        ArrayList<Zapato> productos = inventario.buscar(campoBuscar.getText(), filtro);
        lista.getChildren().clear();
        for (Zapato zapato : productos) {
            lista.getChildren().add(crearTarjeta(zapato));
        }
        alternar(listaVacia, productos.isEmpty());
        pintarCambios();
    }

    private TarjetaInventario crearTarjeta(Zapato zapato) {
        TarjetaInventario tarjeta = new TarjetaInventario(zapato);
        actualizar(tarjeta);
        tarjeta.getSelector().getMas().setOnAction(e -> elegirTalla(tarjeta, tarjeta.getSelector().getMas(), 1));
        tarjeta.getSelector().getMenos().setOnAction(e -> elegirTalla(tarjeta, tarjeta.getSelector().getMenos(), -1));
        tarjeta.getBotonEditar().setOnAction(e -> editar(zapato));
        tarjeta.getBotonEliminar().setOnAction(e -> eliminar(zapato));
        return tarjeta;
    }

    private void actualizar(TarjetaInventario tarjeta) {
        Zapato zapato = tarjeta.getZapato();
        int total = zapato.getStockTotal();
        boolean cambiado = false;
        for (CambioStock cambio : cambios) {
            if (cambio.getSku().equalsIgnoreCase(zapato.getSku())) {
                total += cambio.getDiferencia();
                cambiado = true;
            }
        }
        tarjeta.mostrarStock(Math.max(0, total), cambiado);
    }

    private void elegirTalla(TarjetaInventario tarjeta, Node ancla, int cantidad) {
        ArrayList<MenuItem> opciones = opcionesDeTalla(tarjeta, cantidad);
        if (opciones.size() == 1) {
            opciones.get(0).fire();
            return;
        }
        menuTallas.getItems().setAll(opciones);
        menuTallas.show(ancla, Side.BOTTOM, 0, 6);
    }

    public ArrayList<MenuItem> opcionesDeTalla(TarjetaInventario tarjeta, int cantidad) {
        ArrayList<MenuItem> opciones = new ArrayList<>();
        Zapato zapato = tarjeta.getZapato();
        for (TallaStock talla : zapato.getTallas()) {
            CambioStock pendiente = cambios.buscar(c -> c.esDe(zapato.getSku(), talla.getTalla()));
            int stock = talla.getStock() + (pendiente == null ? 0 : pendiente.getDiferencia());
            int nuevo = stock + cantidad;
            if (nuevo < 0 || nuevo > InventarioServicio.MAXIMO_POR_TALLA) {
                continue;
            }
            MenuItem opcion = new MenuItem("Talla " + talla.getTalla() + " · " + stock + " → " + nuevo);
            opcion.setOnAction(e -> anotar(tarjeta, talla.getTalla(), cantidad));
            opciones.add(opcion);
        }
        return opciones;
    }

    private void anotar(TarjetaInventario tarjeta, int talla, int cantidad) {
        CambioStock.anotar(cambios, tarjeta.getZapato().getSku(), talla, cantidad);
        alternar(aviso, false);
        actualizar(tarjeta);
        pintarCambios();
    }

    private void pintarCambios() {
        int total = cambios.tamanio();
        cifraCambios.setText(String.valueOf(total));
        botonGuardar.setDisable(total == 0);
        enlaceDescartar.setDisable(total == 0);
    }

    private void guardar() {
        int total = cambios.tamanio();
        String sku = cambios.estaVacia() ? "" : cambios.obtener(0).getSku();
        boolean unoSolo = !cambios.contiene(c -> !c.getSku().equalsIgnoreCase(sku));
        ResultadoInventario resultado = inventario.guardarCambios(cambios);
        if (!resultado.esExito()) {
            pintar();
            mostrarAviso(resultado.getMensaje());
            return;
        }
        cambios.vaciar();
        alternar(aviso, false);
        pintar();
        barraController.actualizarAvisos();
        ArrayList<Zapato> cambiados = inventario.buscar(sku, FiltroInventario.TODOS);
        if (unoSolo && !cambiados.isEmpty()) {
            PopupCambiosGuardadosController.mostrar(cambiados.get(0), resultado.getMensaje());
        } else {
            PopupCambiosGuardadosController.mostrar(Iconos.check(Iconos.OSCURO, 34), total + " tallas actualizadas",
                    cifraPares.getText() + " pares en bodega", resultado.getMensaje());
        }
    }

    private void descartar() {
        cambios.vaciar();
        alternar(aviso, false);
        pintar();
    }

    private void editar(Zapato zapato) {
        AdminStockTallaController editor = Navegador.mostrar(Ventana.ADMIN_STOCK_TALLA);
        if (editor != null) {
            editor.mostrar(zapato);
        }
    }

    private void eliminar(Zapato zapato) {
        if (!PopupEliminarProductoController.confirmar(zapato)) {
            return;
        }
        ResultadoInventario resultado = inventario.eliminar(zapato.getSku());
        if (resultado.esExito()) {
            CambioStock.olvidar(cambios, zapato.getSku());
            barraController.actualizarAvisos();
        }
        pintar();
        mostrarAviso(resultado.getMensaje());
    }

    private void volverATienda() {
        if (!cambios.estaVacia()) {
            mostrarAviso("Guarda o descarta tus cambios antes de salir.");
            return;
        }
        Navegador.cerrarSesion();
    }

    private void mostrarAviso(String texto) {
        textoAviso.setText(texto);
        alternar(aviso, true);
    }

    private void alternar(Node nodo, boolean visible) {
        nodo.setVisible(visible);
        nodo.setManaged(visible);
    }
}
