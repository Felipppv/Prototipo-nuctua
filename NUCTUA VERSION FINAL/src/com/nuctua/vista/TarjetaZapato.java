package com.nuctua.vista;

import com.nuctua.modelo.Zapato;
import com.nuctua.servicio.CatalogoServicio;
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
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;

public class TarjetaZapato extends VBox {

    public static final double ANCHO = 276;
    private static final double PROPORCION_FOTO = 0.8;

    private final Zapato zapato;
    private final Button botonAgregar;
    private final Button botonDeseo;
    private final StackPane foto;
    private final Rectangle recorte;
    private final TextFlow nombre;
    private final Label etiqueta;
    private final VBox datos;
    private boolean enDeseos;
    private double anchoActual;
    private String resaltado = "";

    public TarjetaZapato(Zapato zapato) {
        this.zapato = zapato;
        getStyleClass().addAll("tarjeta", "tarjeta-zapato");

        foto = new StackPane();
        foto.getStyleClass().add("zona-foto");
        foto.getChildren().add(new Region());
        recorte = new Rectangle();
        recorte.setArcWidth(34);
        recorte.setArcHeight(34);
        foto.setClip(recorte);

        etiqueta = new Label(zapato.estaAgotado() ? "Agotado" : etiquetaDe(zapato));
        etiqueta.getStyleClass().add(zapato.estaAgotado() ? "pastilla-naranja" : "pastilla");
        StackPane.setAlignment(etiqueta, Pos.TOP_LEFT);
        StackPane.setMargin(etiqueta, new Insets(12, 0, 0, 12));

        botonDeseo = new Button();
        botonDeseo.getStyleClass().add("boton-icono");
        botonDeseo.setTooltip(new Tooltip("Lista de deseos"));
        StackPane.setAlignment(botonDeseo, Pos.TOP_RIGHT);
        StackPane.setMargin(botonDeseo, new Insets(10, 10, 0, 0));
        foto.getChildren().addAll(etiqueta, botonDeseo);
        setEnDeseos(false);

        Region linea = new Region();
        linea.getStyleClass().add("separador");

        nombre = new TextFlow();
        nombre.getStyleClass().add("nombre-tarjeta-flujo");
        pintarNombre();

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
        stock.getStyleClass().add(zapato.estaAgotado() ? "estado-agotado"
                : (zapato.tienePocoStock() ? "estado-poco" : "estado-disponible"));

        Label precio = new Label(zapato.getPrecioTexto());
        precio.getStyleClass().add("precio-tarjeta");
        precio.setMinWidth(Region.USE_PREF_SIZE);
        Region espacio = new Region();
        HBox.setHgrow(espacio, Priority.ALWAYS);
        botonAgregar = new Button(zapato.estaAgotado() ? "AGOTADO" : "AGREGAR");
        botonAgregar.getStyleClass().add("boton-agregar");
        botonAgregar.setGraphic(Iconos.carrito(Iconos.CREMA, 15));
        botonAgregar.setContentDisplay(ContentDisplay.RIGHT);
        botonAgregar.setGraphicTextGap(10);
        botonAgregar.setDisable(zapato.estaAgotado());
        botonAgregar.setMinWidth(Region.USE_PREF_SIZE);
        HBox pie = new HBox(precio, espacio, botonAgregar);
        pie.setAlignment(Pos.CENTER_LEFT);

        datos = new VBox(8, nombre, colores, stock, pie);
        getChildren().addAll(foto, linea, datos);

        botonDeseo.addEventHandler(MouseEvent.MOUSE_CLICKED, Event::consume);
        botonAgregar.addEventHandler(MouseEvent.MOUSE_CLICKED, Event::consume);
        setOnMouseEntered(e -> setTranslateY(-4));
        setOnMouseExited(e -> setTranslateY(0));
        ajustarAncho(ANCHO);
    }

    public void setParaTi(boolean paraTi) {
        if (paraTi && !zapato.estaAgotado()) {
            etiqueta.setText("Para ti · " + etiquetaDe(zapato));
        }
    }

    public final void ajustarAncho(double ancho) {
        double nuevo = Math.max(240, Math.floor(ancho));
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
        double lado = nuevo < 264 ? 12 : 16;
        datos.setPadding(new Insets(14, lado, 16, lado));
    }

    public void setResaltado(String texto) {
        String nuevo = texto == null ? "" : texto.trim();
        if (!nuevo.equals(resaltado)) {
            resaltado = nuevo;
            pintarNombre();
        }
    }

    private void pintarNombre() {
        nombre.getChildren().clear();
        String completo = zapato.getNombre().toUpperCase();
        String normal = CatalogoServicio.normalizar(completo);
        boolean[] marcas = new boolean[completo.length()];
        for (String palabra : CatalogoServicio.normalizar(resaltado).split("\\s+")) {
            if (palabra.isEmpty()) {
                continue;
            }
            int desde = normal.indexOf(palabra);
            while (desde >= 0) {
                for (int i = desde; i < desde + palabra.length() && i < marcas.length; i++) {
                    marcas[i] = true;
                }
                desde = normal.indexOf(palabra, desde + palabra.length());
            }
        }
        int inicio = 0;
        while (inicio < completo.length()) {
            int fin = inicio;
            while (fin < completo.length() && marcas[fin] == marcas[inicio]) {
                fin++;
            }
            Text tramo = new Text(completo.substring(inicio, fin));
            tramo.getStyleClass().add(marcas[inicio] ? "nombre-tarjeta-resaltado" : "nombre-tarjeta-texto");
            nombre.getChildren().add(tramo);
            inicio = fin;
        }
    }

    public final void setEnDeseos(boolean valor) {
        enDeseos = valor;
        botonDeseo.setGraphic(Iconos.corazon(Iconos.OSCURO, 18, valor));
        botonDeseo.getTooltip().setText(valor ? "Quitar de tu lista de deseos" : "Guardar en tu lista de deseos");
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
            return total == 1 ? "¡Último par disponible!" : "¡Últimos " + total + " pares!";
        }
        return "En stock · " + total + " pares";
    }

    public static String etiquetaDe(Zapato zapato) {
        String marca = zapato.getMarca();
        if (marca.equalsIgnoreCase("Nike SB")) {
            return "SB";
        }
        if (marca.equalsIgnoreCase("Nike")) {
            return "Dunk";
        }
        return marca;
    }

    public static String[] coloresConocidos() {
        return new String[]{"Negro", "Rojo", "Blanco", "Gris", "Azul", "Verde", "Crema", "Arena", "Goma", "Moca", "Plata"};
    }

    public static Color colorDe(String nombre) {
        return switch (CatalogoServicio.normalizar(nombre)) {
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
