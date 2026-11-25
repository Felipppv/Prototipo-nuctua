package com.nuctua.vista;

import com.nuctua.modelo.Zapato;
import com.nuctua.util.Iconos;
import com.nuctua.util.Recursos;
import javafx.event.Event;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.Label;
import javafx.scene.control.Tooltip;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;

public class TarjetaDeseo extends VBox {

    private static final double PROPORCION_FOTO = 0.82;

    private final Zapato zapato;
    private final StackPane foto;
    private final Rectangle recorte;
    private final Button botonCorazon;
    private final Button botonCarrito;
    private final Button botonQuitar;
    private double anchoActual;

    public TarjetaDeseo(Zapato zapato) {
        this.zapato = zapato;
        getStyleClass().addAll("tarjeta", "tarjeta-zapato", "tarjeta-deseo");

        foto = new StackPane();
        foto.getStyleClass().add("zona-foto");
        foto.getChildren().add(new Region());
        recorte = new Rectangle();
        recorte.setArcWidth(34);
        recorte.setArcHeight(34);
        foto.setClip(recorte);

        boolean disponible = zapato.isActivo() && !zapato.estaAgotado();
        Label sello = new Label(disponible ? "Guardado" : (zapato.isActivo() ? "Agotado" : "No disponible"));
        sello.getStyleClass().add(disponible ? "pastilla" : "pastilla-naranja");
        sello.setRotate(-4);
        StackPane.setAlignment(sello, Pos.TOP_LEFT);
        StackPane.setMargin(sello, new Insets(18, 0, 0, 18));

        botonCorazon = new Button();
        botonCorazon.getStyleClass().addAll("boton-icono", "boton-icono-activo", "boton-corazon-grande");
        botonCorazon.setGraphic(Iconos.corazon(Iconos.OSCURO, 20, true));
        botonCorazon.setTooltip(new Tooltip("Quitar de tu lista de deseos"));
        StackPane.setAlignment(botonCorazon, Pos.TOP_RIGHT);
        StackPane.setMargin(botonCorazon, new Insets(16, 16, 0, 0));
        foto.getChildren().addAll(sello, botonCorazon);

        Region linea = new Region();
        linea.getStyleClass().add("separador");

        Label nombre = new Label(zapato.getNombre().toUpperCase());
        nombre.getStyleClass().add("nombre-deseo");
        nombre.setWrapText(true);
        nombre.setMinHeight(Region.USE_PREF_SIZE);

        HBox colores = new HBox(8);
        colores.setAlignment(Pos.CENTER_LEFT);
        for (String color : zapato.getColores().split("·")) {
            Circle punto = new Circle(9, TarjetaZapato.colorDe(color.trim()));
            punto.setStroke(Iconos.OSCURO);
            punto.setStrokeWidth(2);
            colores.getChildren().add(punto);
        }
        Label textoColores = new Label(zapato.getColores());
        textoColores.getStyleClass().add("texto");
        colores.getChildren().add(textoColores);

        Label precio = new Label(zapato.getPrecioTexto());
        precio.getStyleClass().add("precio-deseo");

        botonCarrito = new Button(disponible ? "AGREGAR AL CARRITO" : (zapato.isActivo() ? "AGOTADO" : "NO DISPONIBLE"));
        botonCarrito.getStyleClass().addAll("boton-agregar", "boton-agregar-deseo");
        botonCarrito.setGraphic(Iconos.carrito(Iconos.CREMA, 18));
        botonCarrito.setContentDisplay(ContentDisplay.RIGHT);
        botonCarrito.setGraphicTextGap(10);
        botonCarrito.setMaxWidth(Double.MAX_VALUE);
        botonCarrito.setDisable(!disponible);
        botonCarrito.setTooltip(new Tooltip("Elige la talla y pasa el par al carrito"));
        HBox.setHgrow(botonCarrito, Priority.ALWAYS);

        botonQuitar = new Button();
        botonQuitar.getStyleClass().add("boton-quitar");
        botonQuitar.setGraphic(Iconos.basura(Iconos.OSCURO, 22));
        botonQuitar.setTooltip(new Tooltip("Quitar de la lista"));

        HBox acciones = new HBox(12, botonCarrito, botonQuitar);
        acciones.setAlignment(Pos.CENTER_LEFT);

        VBox datos = new VBox(10, nombre, colores, precio, acciones);
        datos.setPadding(new Insets(20));
        getChildren().addAll(foto, linea, datos);

        for (Button boton : new Button[]{botonCorazon, botonCarrito, botonQuitar}) {
            boton.addEventHandler(MouseEvent.MOUSE_CLICKED, Event::consume);
        }
        setOnMouseEntered(e -> setTranslateY(-4));
        setOnMouseExited(e -> setTranslateY(0));
        ajustarAncho(380);
    }

    public final void ajustarAncho(double ancho) {
        double nuevo = Math.max(260, Math.floor(ancho));
        if (Math.abs(nuevo - anchoActual) < 1) {
            return;
        }
        anchoActual = nuevo;
        setMinWidth(nuevo);
        setPrefWidth(nuevo);
        setMaxWidth(nuevo);
        double alto = Math.round(nuevo * PROPORCION_FOTO);
        foto.setMinHeight(alto);
        foto.setMaxHeight(alto);
        foto.getChildren().set(0, Recursos.vistaCubierta(Recursos.imagenProducto(zapato), nuevo - 5, alto));
        recorte.setWidth(nuevo - 5);
        recorte.setHeight(alto);
    }

    public void setActual(boolean actual) {
        getStyleClass().remove("tarjeta-deseo-actual");
        if (actual) {
            getStyleClass().add("tarjeta-deseo-actual");
        }
    }

    public Zapato getZapato() {
        return zapato;
    }

    public Button getBotonCorazon() {
        return botonCorazon;
    }

    public Button getBotonCarrito() {
        return botonCarrito;
    }

    public Button getBotonQuitar() {
        return botonQuitar;
    }
}
