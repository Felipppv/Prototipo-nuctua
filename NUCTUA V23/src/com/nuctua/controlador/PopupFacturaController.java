package com.nuctua.controlador;

import com.nuctua.modelo.DatosEnvio;
import com.nuctua.modelo.Factura;
import com.nuctua.modelo.LineaPedido;
import com.nuctua.modelo.Pedido;
import com.nuctua.modelo.TotalesCompra;
import com.nuctua.modelo.Zapato;
import com.nuctua.servicio.FacturaServicio;
import com.nuctua.util.FormatoCampo;
import com.nuctua.vista.Ventana;
import java.io.File;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;

public class PopupFacturaController extends PopupBase {

    @FXML
    private Label numero;
    @FXML
    private Label fecha;
    @FXML
    private Label cliente;
    @FXML
    private Label contacto;
    @FXML
    private Label direccion;
    @FXML
    private Label indicaciones;
    @FXML
    private Label metodo;
    @FXML
    private VBox lineas;
    @FXML
    private VBox cuentas;
    @FXML
    private Label total;
    @FXML
    private Label mensaje;
    @FXML
    private VBox raizModal;

    private final FacturaServicio servicio = new FacturaServicio();
    private Factura factura;

    public static void mostrar(Pedido pedido) {
        PopupFacturaController popup = Popup.abrir(Ventana.POPUP_FACTURA);
        popup.setPedido(pedido);
        popup.mostrarYEsperar();
    }

    public void setPedido(Pedido pedido) {
        factura = servicio.crear(pedido);
        DatosEnvio envio = pedido.getEnvio();
        TotalesCompra totales = pedido.getTotales();
        numero.setText("N.º " + factura.getNumero());
        fecha.setText("Pedido #" + pedido.getCodigo() + " · " + pedido.getFechaTexto() + ", " + pedido.getHoraTexto());
        cliente.setText(envio.getNombre());
        contacto.setText(envio.getCorreo() + " · " + FormatoCampo.agruparTelefono(envio.getTelefono()));
        direccion.setText(envio.getDireccion() + ", " + envio.getCiudad());
        indicaciones.setText(envio.getIndicaciones());
        indicaciones.setVisible(!envio.getIndicaciones().isEmpty());
        indicaciones.setManaged(indicaciones.isVisible());
        metodo.setText(pedido.getTextoMetodo() + " · " + pedido.getEstadoPago());
        lineas.getChildren().clear();
        for (LineaPedido linea : pedido.getLineas()) {
            lineas.getChildren().add(fila(linea.getNombre(), linea.getSku() + " · Talla " + linea.getTalla() + " · "
                    + linea.getCantidad() + " x " + Zapato.formatearPrecio(linea.getPrecio()), linea.getSubtotal()));
        }
        cuentas.getChildren().setAll(
                cuenta("Subtotal (" + totales.getTextoPares() + ")", Zapato.formatearPrecio(totales.getSubtotal())),
                cuenta(factura.getEtiquetaDescuento(), totales.getTextoDescuento()),
                cuenta("Base gravable", Zapato.formatearPrecio(factura.getBaseGravable())),
                cuenta("IVA incluido (" + Factura.PORCENTAJE_IVA + "%)", Zapato.formatearPrecio(factura.getIva())),
                cuenta("Envío", totales.getTextoEnvio()));
        total.setText(Zapato.formatearPrecio(totales.getTotal()));
        if (servicio.asegurar(factura)) {
            avisar("Guardada en " + servicio.rutaCorta(factura), false);
        } else {
            avisar("No pudimos guardar el archivo. Usa Guardar copia.", true);
        }
    }

    @FXML
    private void guardarCopia() {
        FileChooser selector = new FileChooser();
        selector.setTitle("Guardar factura");
        selector.setInitialFileName(servicio.nombreSugerido(factura));
        selector.getExtensionFilters().add(new FileChooser.ExtensionFilter("Archivo de texto", "*.txt"));
        guardarEn(selector.showSaveDialog(raizModal.getScene().getWindow()));
    }

    public void guardarEn(File destino) {
        if (destino == null) {
            return;
        }
        boolean guardada = servicio.exportar(factura, destino);
        avisar(guardada ? "Copia guardada como " + destino.getName() : "No pudimos guardar la copia en esa carpeta.", !guardada);
    }

    private void avisar(String texto, boolean error) {
        mensaje.setText(texto);
        mensaje.getStyleClass().setAll("label", error ? "mensaje-error" : "mensaje-ok");
    }

    private HBox fila(String titulo, String detalle, int valor) {
        Label nombre = new Label(titulo);
        nombre.getStyleClass().addAll("texto", "negrita");
        nombre.setWrapText(true);
        Label datos = new Label(detalle);
        datos.getStyleClass().add("texto-suave");
        VBox textos = new VBox(2, nombre, datos);
        textos.setMinWidth(0);
        HBox.setHgrow(textos, Priority.ALWAYS);
        Label precio = new Label(Zapato.formatearPrecio(valor));
        precio.getStyleClass().addAll("texto", "negrita");
        precio.setMinWidth(Region.USE_PREF_SIZE);
        HBox fila = new HBox(14, textos, precio);
        fila.setAlignment(Pos.CENTER_LEFT);
        return fila;
    }

    private HBox cuenta(String titulo, String valor) {
        Label etiqueta = new Label(titulo);
        etiqueta.getStyleClass().add("texto");
        Region espacio = new Region();
        HBox.setHgrow(espacio, Priority.ALWAYS);
        Label cifra = new Label(valor);
        cifra.getStyleClass().addAll("texto", "negrita");
        return new HBox(etiqueta, espacio, cifra);
    }
}
