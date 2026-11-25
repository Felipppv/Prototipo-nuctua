package com.nuctua.vista;

import com.nuctua.modelo.Zapato;
import com.nuctua.util.Iconos;
import com.nuctua.util.Recursos;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
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

public class TarjetaInventario extends HBox {

    private static final double LADO = 84;

    private final Zapato zapato;
    private final SelectorCantidad selector = new SelectorCantidad();
    private final StackPane cajaEstado = new StackPane();
    private final Button botonEditar = new Button("Editar");
    private final Button botonEliminar = new Button();

    public TarjetaInventario(Zapato zapato) {
        super(16);
        this.zapato = zapato;
        getStyleClass().addAll("tarjeta", "tarjeta-inventario");
        setAlignment(Pos.CENTER_LEFT);

        StackPane imagen = new StackPane(Recursos.vistaCubierta(Recursos.imagenProducto(zapato), LADO, LADO));
        Rectangle recorte = new Rectangle(LADO, LADO);
        recorte.setArcWidth(20);
        recorte.setArcHeight(20);
        imagen.setClip(recorte);
        StackPane foto = new StackPane(imagen);
        foto.getStyleClass().add("foto-resumen");
        foto.setMinSize(LADO + 4, LADO + 4);
        foto.setMaxSize(LADO + 4, LADO + 4);

        Label nombre = new Label(zapato.getNombre().toUpperCase());
        nombre.getStyleClass().add("nombre-inventario");
        nombre.setWrapText(true);
        nombre.setMinHeight(Region.USE_PREF_SIZE);
        HBox colores = new HBox(6);
        colores.setAlignment(Pos.CENTER_LEFT);
        for (String color : zapato.getColores().split("·")) {
            Circle punto = new Circle(8, TarjetaZapato.colorDe(color.trim()));
            punto.setStroke(Iconos.OSCURO);
            punto.setStrokeWidth(1.5);
            colores.getChildren().add(punto);
        }
        Label textoColores = new Label(zapato.getColores());
        textoColores.getStyleClass().add("texto-suave");
        colores.getChildren().add(textoColores);
        Label sku = new Label("SKU " + zapato.getSku() + " · " + zapato.getRangoTallas());
        sku.getStyleClass().add("texto-suave");
        VBox textos = new VBox(5, nombre, colores, sku);
        textos.setAlignment(Pos.CENTER_LEFT);
        textos.setMinWidth(150);
        textos.setPrefWidth(300);
        HBox.setHgrow(textos, Priority.ALWAYS);

        Label precio = new Label(zapato.getPrecioTexto());
        precio.getStyleClass().add("precio-pedido");
        precio.setMinWidth(Region.USE_PREF_SIZE);
        botonEditar.getStyleClass().addAll("boton-secundario", "boton-editar");
        botonEditar.setGraphic(Iconos.lapiz(Iconos.OSCURO, 14));
        botonEditar.setMinWidth(Region.USE_PREF_SIZE);
        botonEliminar.getStyleClass().addAll("boton-icono", "boton-corazon-grande");
        botonEliminar.setGraphic(Iconos.basura(Iconos.OSCURO, 18));
        botonEliminar.setTooltip(new Tooltip("Eliminar del inventario"));
        FlowPane acciones = new FlowPane(12, 12, precio, selector, cajaEstado, botonEditar, botonEliminar);
        acciones.setAlignment(Pos.CENTER_RIGHT);
        acciones.setPrefWrapLength(450);
        acciones.setMinWidth(260);
        getChildren().addAll(foto, textos, acciones);
        mostrarStock(zapato.getStockTotal(), false);
    }

    public final void mostrarStock(int total, boolean cambiado) {
        selector.mostrar(total, cambiado);
        selector.getMenos().setDisable(total <= 0);
        cajaEstado.getChildren().setAll(PastillaAdmin.de(zapato, total));
    }

    public Zapato getZapato() {
        return zapato;
    }

    public SelectorCantidad getSelector() {
        return selector;
    }

    public Button getBotonEditar() {
        return botonEditar;
    }

    public Button getBotonEliminar() {
        return botonEliminar;
    }
}
