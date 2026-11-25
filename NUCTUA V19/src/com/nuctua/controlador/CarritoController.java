package com.nuctua.controlador;

import com.nuctua.modelo.ItemCarrito;
import com.nuctua.modelo.TallaStock;
import com.nuctua.modelo.TotalesCompra;
import com.nuctua.modelo.Zapato;
import com.nuctua.servicio.CarritoServicio;
import com.nuctua.servicio.ResultadoSeleccion;
import com.nuctua.servicio.SeleccionServicio;
import com.nuctua.util.Iconos;
import com.nuctua.vista.TarjetaCarrito;
import com.nuctua.vista.Ventana;
import java.util.ArrayList;
import javafx.fxml.FXML;
import javafx.geometry.Side;
import javafx.scene.control.Button;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

public class CarritoController {

    @FXML
    private BarraClienteController barraController;
    @FXML
    private Hyperlink enlaceSeguir;
    @FXML
    private Label sello;
    @FXML
    private HBox aviso;
    @FXML
    private Label textoAviso;
    @FXML
    private VBox productos;
    @FXML
    private VBox tarjetaVacia;
    @FXML
    private Button botonCatalogo;
    @FXML
    private Button botonDeseosCarrito;
    @FXML
    private ResumenCompraController resumenController;
    @FXML
    private Button botonPagar;
    @FXML
    private Label mensajePago;
    @FXML
    private StackPane iconoSeguridad;

    private final CarritoServicio carrito = new CarritoServicio();
    private final SeleccionServicio seleccion = new SeleccionServicio();
    private final ContextMenu menuTallas = new ContextMenu();

    @FXML
    private void initialize() {
        barraController.marcar(Ventana.CARRITO);
        enlaceSeguir.setGraphic(Iconos.flechaIzquierda(Iconos.OSCURO, 20));
        enlaceSeguir.setOnAction(e -> Navegador.mostrar(Ventana.CATALOGO));
        botonCatalogo.setGraphic(Iconos.flechaDerecha(Iconos.CREMA, 18));
        botonCatalogo.setContentDisplay(ContentDisplay.RIGHT);
        botonCatalogo.setOnAction(e -> Navegador.mostrar(Ventana.CATALOGO));
        botonDeseosCarrito.setGraphic(Iconos.corazon(Iconos.OSCURO, 20, false));
        botonDeseosCarrito.setOnAction(e -> Navegador.mostrar(Ventana.LISTA_DESEOS));
        botonPagar.setGraphic(Iconos.flechaDerecha(Iconos.CREMA, 22));
        botonPagar.setContentDisplay(ContentDisplay.RIGHT);
        botonPagar.setOnAction(e -> irAPagar());
        iconoSeguridad.getChildren().add(Iconos.candado(Iconos.OSCURO, 16));
        menuTallas.getStyleClass().add("menu-orden");
        ArrayList<String> cambios = carrito.sincronizar();
        barraController.actualizarContadores();
        pintar();
        if (!cambios.isEmpty()) {
            mostrarAviso(String.join(" ", cambios));
        }
    }

    private void pintar() {
        productos.getChildren().clear();
        for (ItemCarrito item : carrito.lista()) {
            productos.getChildren().add(crearTarjeta(item));
        }
        boolean vacio = carrito.lista().estaVacia();
        tarjetaVacia.setVisible(vacio);
        tarjetaVacia.setManaged(vacio);
        productos.setVisible(!vacio);
        productos.setManaged(!vacio);
        TotalesCompra totales = resumenController.pintar();
        sello.setText(vacio ? "Tu bolsa está vacía" : totales.getTextoPares() + " en tu bolsa");
        boolean listo = carrito.listoParaPagar();
        botonPagar.setDisable(!listo);
        mensajePago.setText(listo || vacio ? "" : "Quita o cambia los pares sin stock para poder pagar.");
        mensajePago.setVisible(!mensajePago.getText().isEmpty());
        mensajePago.setManaged(mensajePago.isVisible());
    }

    private TarjetaCarrito crearTarjeta(ItemCarrito item) {
        TarjetaCarrito tarjeta = new TarjetaCarrito(item);
        String sku = item.getZapato().getSku();
        int talla = item.getTalla();
        int stock = carrito.stockDe(item);
        ResultadoSeleccion estado = carrito.estadoDe(item);
        if (!estado.esValida()) {
            tarjeta.setEstado(estado.getMensaje(), true);
        } else if (item.getCantidad() >= stock) {
            tarjeta.setEstado("Es todo el stock de esta talla", false);
        } else if (stock <= 3) {
            tarjeta.setEstado(seleccion.describirStock(stock), false);
        }
        boolean disponible = item.getZapato().isActivo() && !item.getZapato().estaAgotado();
        tarjeta.getBotonTalla().setDisable(!disponible);
        tarjeta.getBotonMenos().setDisable(!estado.esValida() || item.getCantidad() <= 1);
        tarjeta.getBotonMas().setDisable(!estado.esValida() || item.getCantidad() >= stock);
        tarjeta.getBotonMenos().setOnAction(e -> cambiarCantidad(sku, talla, item.getCantidad() - 1));
        tarjeta.getBotonMas().setOnAction(e -> cambiarCantidad(sku, talla, item.getCantidad() + 1));
        tarjeta.getBotonTalla().setOnAction(e -> {
            menuTallas.getItems().setAll(opcionesDeTalla(item));
            menuTallas.show(tarjeta.getBotonTalla(), Side.BOTTOM, 0, 6);
        });
        tarjeta.getBotonQuitar().setOnAction(e -> {
            carrito.quitar(sku, talla);
            refrescar("Quitaste " + item.getZapato().getNombre() + " talla " + talla + " de tu carrito.");
        });
        tarjeta.getEnlaceDeseos().setOnAction(e -> {
            carrito.moverADeseos(sku, talla);
            refrescar(item.getZapato().getNombre() + " pasó a tu lista de deseos.");
        });
        tarjeta.getEnlaceNombre().setOnAction(e -> abrirDetalle(item.getZapato()));
        return tarjeta;
    }

    public ArrayList<MenuItem> opcionesDeTalla(ItemCarrito item) {
        ArrayList<MenuItem> opciones = new ArrayList<>();
        String sku = item.getZapato().getSku();
        for (TallaStock talla : item.getZapato().getTallas()) {
            if (talla.getStock() == 0) {
                continue;
            }
            boolean actual = talla.getTalla() == item.getTalla();
            int enCarrito = actual ? 0 : carrito.cantidadDe(sku, talla.getTalla());
            boolean alcanza = enCarrito + item.getCantidad() <= talla.getStock();
            String detalle = actual ? "Actual" : (alcanza ? seleccion.describirStock(talla.getStock()) : "No alcanza el stock");
            MenuItem opcion = new MenuItem("Talla " + talla.getTalla() + " · " + detalle);
            opcion.setDisable(actual || !alcanza);
            opcion.setOnAction(e -> cambiarTalla(sku, item.getTalla(), talla.getTalla()));
            opciones.add(opcion);
        }
        return opciones;
    }

    private void cambiarCantidad(String sku, int talla, int cantidad) {
        aplicar(carrito.cambiarCantidad(sku, talla, cantidad));
    }

    private void cambiarTalla(String sku, int talla, int nueva) {
        aplicar(carrito.cambiarTalla(sku, talla, nueva));
    }

    private void aplicar(ResultadoSeleccion resultado) {
        refrescar(resultado.esValida() ? "" : resultado.getMensaje());
    }

    private void refrescar(String mensaje) {
        pintar();
        BarraClienteController.refrescar();
        if (mensaje.isEmpty()) {
            aviso.setVisible(false);
            aviso.setManaged(false);
        } else {
            mostrarAviso(mensaje);
        }
    }

    private void mostrarAviso(String texto) {
        textoAviso.setText(texto);
        aviso.setVisible(true);
        aviso.setManaged(true);
    }

    private void irAPagar() {
        ArrayList<String> cambios = carrito.sincronizar();
        if (!cambios.isEmpty() || !carrito.listoParaPagar()) {
            refrescar(cambios.isEmpty() ? "Hay pares sin stock en tu carrito." : String.join(" ", cambios));
            return;
        }
        Navegador.mostrar(Ventana.PAGO);
    }

    private void abrirDetalle(Zapato zapato) {
        DetalleProductoController detalle = Navegador.mostrar(Ventana.DETALLE_PRODUCTO);
        if (detalle != null) {
            detalle.mostrar(zapato);
        }
    }
}
