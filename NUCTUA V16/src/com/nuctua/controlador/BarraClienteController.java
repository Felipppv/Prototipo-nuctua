package com.nuctua.controlador;

import com.nuctua.servicio.CarritoServicio;
import com.nuctua.servicio.DeseosServicio;
import com.nuctua.util.Iconos;
import com.nuctua.vista.Ventana;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Tooltip;

public class BarraClienteController {

    @FXML
    private Button enlaceCatalogo;
    @FXML
    private Button enlaceDeseos;
    @FXML
    private Button enlaceCarrito;
    @FXML
    private Button enlaceHistorial;
    @FXML
    private Button enlaceSalir;
    @FXML
    private Button botonBuscar;
    @FXML
    private Button botonDeseos;
    @FXML
    private Button botonCarrito;
    @FXML
    private Button botonPerfil;
    @FXML
    private Label contadorDeseos;
    @FXML
    private Label contadorCarrito;

    private static BarraClienteController visible;

    @FXML
    private void initialize() {
        botonBuscar.setGraphic(Iconos.buscar(Iconos.CREMA, 20));
        botonDeseos.setGraphic(Iconos.corazon(Iconos.CREMA, 20, false));
        botonCarrito.setGraphic(Iconos.carrito(Iconos.CREMA, 20));
        botonPerfil.setGraphic(Iconos.usuario(Iconos.CREMA, 20));
        botonBuscar.setTooltip(new Tooltip("Buscar zapatos"));
        botonDeseos.setTooltip(new Tooltip("Lista de deseos"));
        botonCarrito.setTooltip(new Tooltip("Carrito"));
        botonPerfil.setTooltip(new Tooltip("Mi perfil"));
        visible = this;
        actualizarContadores();
    }

    public static void refrescar() {
        if (visible != null) {
            visible.actualizarContadores();
        }
    }

    public void actualizarContadores() {
        setContadores(new DeseosServicio().cantidad(), new CarritoServicio().cantidadTotal());
    }

    public void marcar(Ventana ventana) {
        Button[] enlaces = {enlaceCatalogo, enlaceDeseos, enlaceCarrito, enlaceHistorial};
        Ventana[] destinos = {Ventana.CATALOGO, Ventana.LISTA_DESEOS, Ventana.CARRITO, Ventana.HISTORIAL_COMPRAS};
        for (int i = 0; i < enlaces.length; i++) {
            enlaces[i].getStyleClass().remove("enlace-activo");
            if (destinos[i] == ventana || (ventana == Ventana.DETALLE_PRODUCTO && i == 0) || (ventana == Ventana.PAGO && i == 2)) {
                enlaces[i].getStyleClass().add("enlace-activo");
            }
        }
        marcarIcono(botonBuscar, ventana == Ventana.BUSCAR_ZAPATOS || ventana == Ventana.CATALOGO);
        marcarIcono(botonPerfil, ventana == Ventana.PERFIL_USUARIO);
        boolean enDeseos = ventana == Ventana.LISTA_DESEOS;
        marcarIcono(botonDeseos, enDeseos);
        botonDeseos.setGraphic(Iconos.corazon(enDeseos ? Iconos.OSCURO : Iconos.CREMA, 20, enDeseos));
    }

    public void setContadores(int deseos, int carrito) {
        mostrarContador(contadorDeseos, deseos);
        mostrarContador(contadorCarrito, carrito);
    }

    private void mostrarContador(Label contador, int valor) {
        contador.setText(String.valueOf(valor));
        contador.setVisible(valor > 0);
    }

    private void marcarIcono(Button boton, boolean activo) {
        boton.getStyleClass().remove("boton-barra-activo");
        if (activo) {
            boton.getStyleClass().add("boton-barra-activo");
        }
    }

    @FXML
    private void irACatalogo() {
        Navegador.mostrar(Ventana.CATALOGO);
    }

    @FXML
    private void irADeseos() {
        Navegador.mostrar(Ventana.LISTA_DESEOS);
    }

    @FXML
    private void irACarrito() {
        Navegador.mostrar(Ventana.CARRITO);
    }

    @FXML
    private void irAHistorial() {
        Navegador.mostrar(Ventana.HISTORIAL_COMPRAS);
    }

    @FXML
    private void irABuscar() {
        Navegador.mostrar(Ventana.BUSCAR_ZAPATOS);
    }

    @FXML
    private void irAPerfil() {
        Navegador.mostrar(Ventana.PERFIL_USUARIO);
    }

    @FXML
    private void cerrarSesion() {
        Navegador.cerrarSesion();
    }
}
