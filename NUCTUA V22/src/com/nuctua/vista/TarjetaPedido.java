package com.nuctua.vista;

import com.nuctua.modelo.LineaPedido;
import com.nuctua.modelo.Pedido;
import com.nuctua.modelo.TotalesCompra;
import com.nuctua.modelo.Zapato;
import com.nuctua.servicio.HistorialServicio;
import com.nuctua.servicio.ResultadoSeleccion;
import com.nuctua.util.Recursos;
import java.util.function.Consumer;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Rectangle;

public class TarjetaPedido extends VBox {

    private static final double ANCHO_FOTO = 86;
    private static final double ALTO_FOTO = 71;
    private static final double ANCHO_BOTON = 172;

    private final Pedido pedido;
    private final Hyperlink enlaceSeguimiento = new Hyperlink();
    private final Hyperlink enlaceFactura = new Hyperlink("Ver factura");
    private final VBox seguimiento;

    public TarjetaPedido(Pedido pedido, HistorialServicio historial, Consumer<LineaPedido> alRepetir) {
        this.pedido = pedido;
        getStyleClass().addAll("tarjeta", "tarjeta-pedido");
        TotalesCompra totales = pedido.getTotales();

        Label codigo = new Label("PEDIDO #" + pedido.getCodigo());
        codigo.getStyleClass().add("codigo-pedido");
        Label datos = new Label("• " + pedido.getFechaTexto() + " · " + totales.getTextoPares());
        datos.getStyleClass().addAll("texto-suave", "datos-pedido");
        HBox titulo = new HBox(10, codigo, datos);
        titulo.setAlignment(Pos.BASELINE_LEFT);
        Region espacio = new Region();
        HBox.setHgrow(espacio, Priority.ALWAYS);
        HBox encabezado = new HBox(titulo, espacio, new PastillaEstado(pedido.getEstado()));
        encabezado.setAlignment(Pos.CENTER_LEFT);
        getChildren().addAll(encabezado, separador());

        for (LineaPedido linea : pedido.getLineas()) {
            getChildren().add(crearLinea(linea, historial, alRepetir));
        }
        if (pedido.getLineas().estaVacia()) {
            Label vacio = new Label("No encontramos el detalle de este pedido.");
            vacio.getStyleClass().add("texto-suave");
            getChildren().add(vacio);
        }

        Label texto = new Label(historial.seguimiento(pedido));
        texto.getStyleClass().add("texto");
        texto.setWrapText(true);
        texto.setMinHeight(Region.USE_PREF_SIZE);
        seguimiento = new VBox(10, new SeguimientoPedido(pedido.getEstado()), texto);
        seguimiento.getStyleClass().add("caja-seguimiento");
        Label total = new Label("Total " + Zapato.formatearPrecio(totales.getTotal()));
        total.getStyleClass().addAll("texto", "negrita");
        Label pago = new Label("· " + pedido.getTextoMetodo());
        pago.getStyleClass().add("texto-suave");
        Region hueco = new Region();
        HBox.setHgrow(hueco, Priority.ALWAYS);
        enlaceSeguimiento.getStyleClass().add("enlace-texto");
        enlaceSeguimiento.setOnAction(e -> mostrarSeguimiento(!seguimiento.isVisible()));
        enlaceFactura.getStyleClass().add("enlace-texto");
        HBox pie = new HBox(10, total, pago, hueco, enlaceSeguimiento, enlaceFactura);
        pie.setAlignment(Pos.CENTER_LEFT);
        getChildren().addAll(seguimiento, separador(), pie);
        mostrarSeguimiento(!pedido.getEstado().esFinal());
    }

    private Node crearLinea(LineaPedido linea, HistorialServicio historial, Consumer<LineaPedido> alRepetir) {
        Zapato zapato = historial.producto(linea);
        StackPane imagen = new StackPane();
        if (zapato != null) {
            imagen.getChildren().add(Recursos.vistaCubierta(Recursos.imagenProducto(zapato), ANCHO_FOTO, ALTO_FOTO));
        }
        Rectangle recorte = new Rectangle(ANCHO_FOTO, ALTO_FOTO);
        recorte.setArcWidth(20);
        recorte.setArcHeight(20);
        imagen.setClip(recorte);
        StackPane foto = new StackPane(imagen);
        foto.getStyleClass().add("foto-resumen");
        foto.setMinSize(ANCHO_FOTO + 4, ALTO_FOTO + 4);
        foto.setMaxSize(ANCHO_FOTO + 4, ALTO_FOTO + 4);

        Label nombre = new Label(linea.getNombre().toUpperCase());
        nombre.getStyleClass().add("nombre-pedido");
        nombre.setWrapText(true);
        nombre.setMinHeight(Region.USE_PREF_SIZE);
        String pares = linea.getCantidad() == 1 ? "" : " · " + linea.getCantidad() + " pares";
        String colores = zapato == null ? "" : " • " + zapato.getColores().replace("·", "–");
        Label detalle = new Label("Talla " + linea.getTalla() + pares + colores);
        detalle.getStyleClass().add("texto-suave");
        detalle.setWrapText(true);
        VBox textos = new VBox(4, nombre, detalle);
        textos.setAlignment(Pos.CENTER_LEFT);
        textos.setMinWidth(0);
        HBox.setHgrow(textos, Priority.ALWAYS);

        Label precio = new Label(Zapato.formatearPrecio(linea.getSubtotal()));
        precio.getStyleClass().add("precio-pedido");
        precio.setMinWidth(Region.USE_PREF_SIZE);
        ResultadoSeleccion disponible = historial.disponibilidad(linea);
        Button repetir = new Button(textoRepetir(disponible));
        repetir.getStyleClass().addAll("boton-agregar", "boton-repetir");
        repetir.setMinWidth(ANCHO_BOTON);
        repetir.setDisable(!disponible.esValida());
        repetir.setOnAction(e -> alRepetir.accept(linea));

        HBox fila = new HBox(16, foto, textos, precio, repetir);
        fila.setAlignment(Pos.CENTER_LEFT);
        HBox.setMargin(repetir, new Insets(0, 0, 0, 16));
        return fila;
    }

    private String textoRepetir(ResultadoSeleccion disponible) {
        return switch (disponible) {
            case VALIDA -> "VOLVER A COMPRAR →";
            case SIN_PRODUCTO, PRODUCTO_INACTIVO -> "NO DISPONIBLE";
            default -> "AGOTADO";
        };
    }

    private Region separador() {
        Region linea = new Region();
        linea.getStyleClass().add("separador-punteado");
        return linea;
    }

    public final void mostrarSeguimiento(boolean visible) {
        seguimiento.setVisible(visible);
        seguimiento.setManaged(visible);
        enlaceSeguimiento.setText(visible ? "Ocultar seguimiento" : "Ver seguimiento");
    }

    public boolean muestraSeguimiento() {
        return seguimiento.isVisible();
    }

    public Pedido getPedido() {
        return pedido;
    }

    public Hyperlink getEnlaceSeguimiento() {
        return enlaceSeguimiento;
    }

    public Hyperlink getEnlaceFactura() {
        return enlaceFactura;
    }
}
