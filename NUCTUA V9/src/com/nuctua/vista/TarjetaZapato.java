package com.nuctua.vista;

import com.nuctua.modelo.Zapato;
import com.nuctua.util.Iconos;
import com.nuctua.util.Recursos;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;

public class TarjetaZapato extends VBox {

    public static final double ANCHO = 276;
    private static final double ALTO_FOTO = 184;

    private final Zapato zapato;
    private final Button botonAgregar;
    private final Button botonDeseo;
    private boolean enDeseos;

    public TarjetaZapato(Zapato zapato) {
        this.zapato = zapato;
        getStyleClass().addAll("tarjeta", "tarjeta-zapato");
        setPrefWidth(ANCHO);
        setMinWidth(ANCHO);
        setMaxWidth(ANCHO);

        StackPane foto = new StackPane();
        foto.getStyleClass().add("zona-foto");
        foto.setMinHeight(ALTO_FOTO);
        foto.setMaxHeight(ALTO_FOTO);
        foto.getChildren().add(Recursos.vistaCubierta(Recursos.imagenProducto(zapato), ANCHO - 5, ALTO_FOTO));
        Rectangle recorte = new Rectangle(ANCHO - 5, ALTO_FOTO);
        recorte.setArcWidth(34);
        recorte.setArcHeight(34);
        foto.setClip(recorte);

        Label etiqueta = new Label(zapato.estaAgotado() ? "Agotado" : zapato.getMarca());
        etiqueta.getStyleClass().add(zapato.estaAgotado() ? "pastilla-naranja" : "pastilla");
        StackPane.setAlignment(etiqueta, Pos.TOP_LEFT);
        StackPane.setMargin(etiqueta, new Insets(12, 0, 0, 12));

        botonDeseo = new Button();
        botonDeseo.getStyleClass().add("boton-icono");
        StackPane.setAlignment(botonDeseo, Pos.TOP_RIGHT);
        StackPane.setMargin(botonDeseo, new Insets(10, 10, 0, 0));
        foto.getChildren().addAll(etiqueta, botonDeseo);
        setEnDeseos(false);

        Region linea = new Region();
        linea.getStyleClass().add("separador");

        Label nombre = new Label(zapato.getNombre().toUpperCase());
        nombre.getStyleClass().add("nombre-tarjeta");
        nombre.setWrapText(true);
        nombre.setMinHeight(Region.USE_PREF_SIZE);

        HBox colores = new HBox(6);
        colores.setAlignment(Pos.CENTER_LEFT);
        for (String color : zapato.getColores().split("·")) {
            Circle punto = new Circle(7, colorDe(color.trim()));
            punto.setStroke(Iconos.OSCURO);
            punto.setStrokeWidth(1.5);
            colores.getChildren().add(punto);
        }
        Label detalle = new Label(zapato.getColores() + " · " + zapato.getRangoTallas());
        detalle.getStyleClass().add("texto-suave");
        detalle.setWrapText(true);
        detalle.setMinHeight(Region.USE_PREF_SIZE);
        HBox.setHgrow(detalle, Priority.ALWAYS);
        colores.getChildren().add(detalle);

        Label stock = new Label(textoStock());
        stock.getStyleClass().add(zapato.estaAgotado() ? "estado-agotado" : (zapato.tienePocoStock() ? "estado-poco" : "estado-disponible"));

        Label precio = new Label(zapato.getPrecioTexto());
        precio.getStyleClass().add("precio");
        Region espacio = new Region();
        HBox.setHgrow(espacio, Priority.ALWAYS);
        botonAgregar = new Button("AGREGAR");
        botonAgregar.getStyleClass().add("boton-chico");
        botonAgregar.setGraphic(Iconos.carrito(Iconos.CREMA, 15));
        botonAgregar.setContentDisplay(ContentDisplay.RIGHT);
        botonAgregar.setDisable(zapato.estaAgotado());
        HBox pie = new HBox(precio, espacio, botonAgregar);
        pie.setAlignment(Pos.CENTER_LEFT);

        VBox datos = new VBox(8, nombre, colores, stock, pie);
        datos.setPadding(new Insets(14, 16, 16, 16));
        getChildren().addAll(foto, linea, datos);
    }

    public final void setEnDeseos(boolean valor) {
        enDeseos = valor;
        botonDeseo.setGraphic(Iconos.corazon(valor ? Iconos.CREMA : Iconos.OSCURO, 18, valor));
        botonDeseo.getStyleClass().remove("boton-icono-activo");
        if (valor) {
            botonDeseo.getStyleClass().add("boton-icono-activo");
        }
    }

    public boolean isEnDeseos() {
        return enDeseos;
    }

    public Zapato getZapato() {
        return zapato;
    }

    public Button getBotonAgregar() {
        return botonAgregar;
    }

    public Button getBotonDeseo() {
        return botonDeseo;
    }

    private String textoStock() {
        int total = zapato.getStockTotal();
        if (total == 0) {
            return "Agotado en todas las tallas";
        }
        if (zapato.tienePocoStock()) {
            return total == 1 ? "¡Queda 1 par!" : "¡Quedan " + total + " pares!";
        }
        return total + " pares disponibles";
    }

    public static Color colorDe(String nombre) {
        return switch (nombre.toLowerCase()) {
            case "negro" -> Color.web("#1B1410");
            case "rojo" -> Color.web("#C8202A");
            case "blanco" -> Color.web("#FFFFFF");
            case "gris" -> Color.web("#9E9E9E");
            case "goma" -> Color.web("#C9A06B");
            case "arena" -> Color.web("#D2BD9A");
            case "moca" -> Color.web("#8C6B4C");
            case "plata" -> Color.web("#C9CBD1");
            case "azul" -> Color.web("#6FA7E6");
            case "crema" -> Color.web("#EFE3C8");
            case "verde" -> Color.web("#5FC56B");
            default -> Color.web("#E8DCCB");
        };
    }
}
