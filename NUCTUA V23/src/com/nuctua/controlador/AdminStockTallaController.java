package com.nuctua.controlador;

import com.nuctua.estructuras.ListaEnlazada;
import com.nuctua.modelo.CambioStock;
import com.nuctua.modelo.SesionActual;
import com.nuctua.modelo.TallaStock;
import com.nuctua.modelo.Zapato;
import com.nuctua.servicio.CatalogoServicio;
import com.nuctua.servicio.InventarioServicio;
import com.nuctua.servicio.ResultadoInventario;
import com.nuctua.util.Iconos;
import com.nuctua.util.Recursos;
import com.nuctua.vista.Interruptor;
import com.nuctua.vista.PastillaAdmin;
import com.nuctua.vista.TarjetaTalla;
import com.nuctua.vista.Ventana;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.StackPane;
import javafx.scene.shape.Rectangle;

public class AdminStockTallaController implements ConEstadoNavegacion {

    @FXML
    private BarraAdminController barraController;
    @FXML
    private Hyperlink enlaceVolver;
    @FXML
    private Label sello;
    @FXML
    private Label titulo;
    @FXML
    private Label textoTotal;
    @FXML
    private FlowPane tallas;
    @FXML
    private Interruptor interruptorVisible;
    @FXML
    private Label textoVisible;
    @FXML
    private StackPane foto;
    @FXML
    private StackPane cajaEstado;
    @FXML
    private Label visibilidad;
    @FXML
    private Label precio;
    @FXML
    private Label total;
    @FXML
    private Label etiquetaPares;
    @FXML
    private Label mensaje;
    @FXML
    private Button botonGuardar;
    @FXML
    private Hyperlink enlaceCancelar;

    private final InventarioServicio inventario = new InventarioServicio();
    private final ListaEnlazada<CambioStock> cambios = new ListaEnlazada<>();
    private Zapato zapato;

    @FXML
    private void initialize() {
        barraController.marcar(Ventana.ADMIN_STOCK_TALLA);
        enlaceVolver.setGraphic(Iconos.flechaIzquierda(Iconos.OSCURO, 18));
        enlaceVolver.setText(Navegador.textoVolver());
        enlaceVolver.setOnAction(e -> Navegador.volver());
        enlaceCancelar.setOnAction(e -> Navegador.volver());
        botonGuardar.setOnAction(e -> guardar());
        interruptorVisible.setOnAction(e -> pintarResumen());
        interruptorVisible.setDisable(true);
        botonGuardar.setDisable(true);
        avisar("");
    }

    public void mostrar(Zapato elegido) {
        Zapato vigente = elegido == null ? null : new CatalogoServicio().buscarPorSku(elegido.getSku());
        if (vigente == null) {
            avisar("Ese par ya no existe en el inventario.");
            return;
        }
        zapato = vigente;
        cambios.vaciar();
        for (CambioStock pendiente : SesionActual.getInstancia().getCambiosStock()) {
            if (pendiente.getSku().equalsIgnoreCase(zapato.getSku())) {
                cambios.insertarFinal(new CambioStock(pendiente.getSku(), pendiente.getTalla(), pendiente.getDiferencia()));
            }
        }
        sello.setText("Editando · SKU " + zapato.getSku());
        titulo.setText(zapato.getNombre().toUpperCase() + ".");
        interruptorVisible.setDisable(false);
        interruptorVisible.setSelected(zapato.isActivo());
        StackPane imagen = new StackPane(Recursos.vistaCubierta(Recursos.imagenProducto(zapato), 156, 116));
        Rectangle recorte = new Rectangle(156, 116);
        recorte.setArcWidth(24);
        recorte.setArcHeight(24);
        imagen.setClip(recorte);
        foto.getChildren().setAll(imagen);
        precio.setText(zapato.getPrecioTexto());
        pintarTallas();
        pintarResumen();
    }

    private void pintarTallas() {
        tallas.getChildren().clear();
        for (TallaStock talla : zapato.getTallas()) {
            TarjetaTalla tarjeta = new TarjetaTalla(talla.getTalla());
            tarjeta.getSelector().getMas().setOnAction(e -> cambiar(tarjeta, 1));
            tarjeta.getSelector().getMenos().setOnAction(e -> cambiar(tarjeta, -1));
            actualizar(tarjeta);
            tallas.getChildren().add(tarjeta);
        }
    }

    private void cambiar(TarjetaTalla tarjeta, int cantidad) {
        CambioStock.anotar(cambios, zapato.getSku(), tarjeta.getTalla(), cantidad);
        actualizar(tarjeta);
        pintarResumen();
    }

    private void actualizar(TarjetaTalla tarjeta) {
        CambioStock cambio = cambios.buscar(c -> c.esDe(zapato.getSku(), tarjeta.getTalla()));
        int stock = zapato.getStock(tarjeta.getTalla()) + (cambio == null ? 0 : cambio.getDiferencia());
        tarjeta.mostrar(Math.max(0, stock), cambio != null, InventarioServicio.MAXIMO_POR_TALLA);
    }

    private void pintarResumen() {
        if (zapato == null) {
            return;
        }
        int pares = zapato.getStockTotal();
        for (CambioStock cambio : cambios) {
            pares += cambio.getDiferencia();
        }
        pares = Math.max(0, pares);
        boolean visible = interruptorVisible.isSelected();
        textoTotal.setText(pares + (pares == 1 ? " par" : " pares"));
        total.setText(String.valueOf(pares));
        etiquetaPares.setText(pares == 1 ? "PAR" : "PARES");
        textoVisible.setText(visible ? "Visible" : "Oculto");
        visibilidad.setText(visible ? "Visible" : "Oculto");
        cajaEstado.getChildren().setAll(PastillaAdmin.porStock(pares));
        botonGuardar.setDisable(cambios.estaVacia() && visible == zapato.isActivo());
        avisar("");
    }

    private void guardar() {
        ResultadoInventario resultado = inventario.guardar(cambios, zapato.getSku(), interruptorVisible.isSelected());
        if (!resultado.esExito()) {
            mostrar(zapato);
            avisar(resultado.getMensaje());
            return;
        }
        CambioStock.olvidar(SesionActual.getInstancia().getCambiosStock(), zapato.getSku());
        mostrar(zapato);
        barraController.actualizarAvisos();
        PopupCambiosGuardadosController.mostrar(zapato, zapato.isActivo() ? resultado.getMensaje()
                : "Los cambios quedaron guardados. El par está oculto en el catálogo.");
        Navegador.volver();
    }

    private void avisar(String texto) {
        mensaje.setText(texto);
        mensaje.setVisible(!texto.isEmpty());
        mensaje.setManaged(!texto.isEmpty());
    }

    @Override
    public String capturarEstado() {
        return zapato == null ? "" : zapato.getSku();
    }

    @Override
    public void restaurarEstado(String estado) {
        mostrar(new CatalogoServicio().buscarPorSku(estado));
    }
}
