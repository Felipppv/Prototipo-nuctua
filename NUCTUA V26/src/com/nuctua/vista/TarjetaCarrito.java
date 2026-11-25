package com.nuctua.vista;

import com.nuctua.modelo.ItemCarrito;
import com.nuctua.modelo.Zapato;
import com.nuctua.util.Iconos;
import com.nuctua.util.Recursos;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;

public class TarjetaCarrito extends HBox {

    private static final double LADO_FOTO = 164;

    private final ItemCarrito item;
    private final Button botonTalla;
    private final Button botonMenos;
    private final Button botonMas;
    private final Button botonQuitar;
    private final Hyperlink enlaceDeseos;
    private final Hyperlink enlaceNombre;
    private final Label aviso;

    public TarjetaCarrito(ItemCarrito item) {
        super(24);
        this.item = item;
        Zapato zapato = item.getZapato();
        getStyleClass().addAll("tarjeta", "tarjeta-carrito");

        StackPane imagen = new StackPane(Recursos.vistaCubierta(Recursos.imagenProducto(zapato), LADO_FOTO, LADO_FOTO));
        Rectangle recorte = new Rectangle(LADO_FOTO, LADO_FOTO);
        recorte.setArcWidth(26);
        recorte.setArcHeight(26);
        imagen.setClip(recorte);
        StackPane foto = new StackPane(imagen);
        foto.getStyleClass().add("foto-carrito");
        foto.setMinSize(LADO_FOTO + 4, LADO_FOTO + 4);
        foto.setMaxSize(LADO_FOTO + 4, LADO_FOTO + 4);

        enlaceNombre = new Hyperlink(zapato.getNombre().toUpperCase());
        enlaceNombre.getStyleClass().add("nombre-carrito");
        enlaceNombre.setWrapText(true);
        enlaceNombre.setMinHeight(Region.USE_PREF_SIZE);

        HBox colores = new HBox(6);
        colores.setAlignment(Pos.CENTER_LEFT);
        for (String color : zapato.getColores().split("·")) {
            Circle punto = new Circle(8, TarjetaZapato.colorDe(color.trim()));
            punto.setStroke(Iconos.OSCURO);
            punto.setStrokeWidth(2);
            colores.getChildren().add(punto);
        }
        Label textoColores = new Label(zapato.getColores());
        textoColores.getStyleClass().add("texto-suave");
        colores.getChildren().add(textoColores);

        botonTalla = new Button("Talla " + item.getTalla());
        botonTalla.getStyleClass().add("selector-talla");
        botonTalla.setGraphic(Iconos.flechaAbajo(Iconos.OSCURO, 16));
        botonTalla.setContentDisplay(ContentDisplay.RIGHT);
        botonTalla.setGraphicTextGap(18);

        botonMenos = new Button();
        botonMenos.getStyleClass().add("boton-cantidad");
        botonMenos.setGraphic(Iconos.menos(Iconos.OSCURO, 16));
        botonMenos.setTooltip(new Tooltip("Quitar un par"));
        Label cantidad = new Label(String.valueOf(item.getCantidad()));
        cantidad.getStyleClass().add("numero-cantidad");
        botonMas = new Button();
        botonMas.getStyleClass().addAll("boton-cantidad", "boton-cantidad-mas");
        botonMas.setGraphic(Iconos.mas(Iconos.CREMA, 16));
        botonMas.setTooltip(new Tooltip("Agregar un par"));
        HBox selectorCantidad = new HBox(botonMenos, cantidad, botonMas);
        selectorCantidad.getStyleClass().add("selector-cantidad");

        HBox controles = new HBox(14, botonTalla, selectorCantidad);
        controles.setAlignment(Pos.CENTER_LEFT);

        enlaceDeseos = new Hyperlink("Mover a deseos");
        enlaceDeseos.getStyleClass().add("enlace-texto");
        enlaceDeseos.setGraphic(Iconos.corazon(Iconos.OSCURO, 18, false));
        enlaceDeseos.setGraphicTextGap(8);
        aviso = new Label();
        aviso.getStyleClass().add("aviso-carrito");
        FlowPane pie = new FlowPane(18, 4, enlaceDeseos, aviso);
        pie.setAlignment(Pos.CENTER_LEFT);
        pie.setPrefWrapLength(260);

        VBox datos = new VBox(10, enlaceNombre, colores, controles, pie);
        datos.setAlignment(Pos.CENTER_LEFT);
        datos.setMinWidth(0);
        HBox.setHgrow(datos, Priority.ALWAYS);

        Label precio = new Label(Zapato.formatearPrecio(item.getSubtotal()));
        precio.getStyleClass().add("precio-carrito");
        precio.setMinWidth(Region.USE_PREF_SIZE);
        Region espacio = new Region();
        VBox.setVgrow(espacio, Priority.ALWAYS);
        botonQuitar = new Button();
        botonQuitar.getStyleClass().addAll("boton-icono", "boton-corazon-grande");
        botonQuitar.setGraphic(Iconos.basura(Iconos.OSCURO, 20));
        botonQuitar.setTooltip(new Tooltip("Quitar del carrito"));
        VBox derecha = new VBox(precio, espacio, botonQuitar);
        derecha.setAlignment(Pos.TOP_RIGHT);

        getChildren().addAll(foto, datos, derecha);
    }

    public void setEstado(String mensaje, boolean problema) {
        aviso.setText(mensaje);
        aviso.getStyleClass().remove("aviso-carrito-problema");
        if (problema) {
            aviso.getStyleClass().add("aviso-carrito-problema");
        }
    }

    public String getEstado() {
        return aviso.getText();
    }

    public ItemCarrito getItem() {
        return item;
    }

    public Button getBotonTalla() {
        return botonTalla;
    }

    public Button getBotonMenos() {
        return botonMenos;
    }

    public Button getBotonMas() {
        return botonMas;
    }

    public Button getBotonQuitar() {
        return botonQuitar;
    }

    public Hyperlink getEnlaceDeseos() {
        return enlaceDeseos;
    }

    public Hyperlink getEnlaceNombre() {
        return enlaceNombre;
    }
}
