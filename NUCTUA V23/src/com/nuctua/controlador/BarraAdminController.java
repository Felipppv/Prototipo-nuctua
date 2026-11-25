package com.nuctua.controlador;

import com.nuctua.servicio.AvisosServicio;
import com.nuctua.util.Iconos;
import com.nuctua.vista.Ventana;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Tooltip;

public class BarraAdminController {

    @FXML
    private Button enlaceInventario;
    @FXML
    private Button enlacePedidos;
    @FXML
    private Button enlaceProductos;
    @FXML
    private Button enlaceClientes;
    @FXML
    private Button botonBuscar;
    @FXML
    private Button botonAvisos;
    @FXML
    private Button botonPerfil;
    @FXML
    private Button botonSalir;

    @FXML
    private void initialize() {
        botonBuscar.setGraphic(Iconos.buscar(Iconos.CREMA, 20));
        botonAvisos.setGraphic(Iconos.campana(Iconos.OSCURO, 20));
        botonPerfil.setGraphic(Iconos.usuario(Iconos.CREMA, 20));
        botonSalir.setGraphic(Iconos.salir(Iconos.CREMA, 20));
        botonBuscar.setTooltip(new Tooltip("Buscar"));
        actualizarAvisos();
        botonPerfil.setTooltip(new Tooltip("Mi perfil"));
        botonSalir.setTooltip(new Tooltip("Cerrar sesión"));
    }

    public void actualizarAvisos() {
        botonAvisos.setTooltip(new Tooltip(new AvisosServicio().texto()));
    }

    public void marcar(Ventana ventana) {
        marcarIcono(botonBuscar, ventana == Ventana.ADMIN_BUSCAR);
        botonBuscar.setGraphic(Iconos.buscar(ventana == Ventana.ADMIN_BUSCAR ? Iconos.OSCURO : Iconos.CREMA, 20));
        marcarIcono(botonPerfil, ventana == Ventana.ADMIN_PERFIL);
        botonPerfil.setGraphic(Iconos.usuario(ventana == Ventana.ADMIN_PERFIL ? Iconos.OSCURO : Iconos.CREMA, 20));
        boolean campana = ventana != Ventana.ADMIN_BUSCAR && ventana != Ventana.ADMIN_PERFIL;
        marcarIcono(botonAvisos, campana);
        botonAvisos.setGraphic(Iconos.campana(campana ? Iconos.OSCURO : Iconos.CREMA, 20));
        Button[] enlaces = {enlaceInventario, enlacePedidos, enlaceProductos, enlaceClientes};
        Ventana[] destinos = {Ventana.ADMIN_INVENTARIO, Ventana.ADMIN_PEDIDOS, Ventana.ADMIN_PRODUCTOS, Ventana.ADMIN_CLIENTES};
        for (int i = 0; i < enlaces.length; i++) {
            enlaces[i].getStyleClass().remove("enlace-activo");
            boolean activo = destinos[i] == ventana
                    || (ventana == Ventana.ADMIN_STOCK_TALLA && i == 0)
                    || (ventana == Ventana.ADMIN_REGISTRAR_PRODUCTO && i == 2);
            if (activo) {
                enlaces[i].getStyleClass().add("enlace-activo");
            }
        }
    }

    private void marcarIcono(Button boton, boolean activo) {
        boton.getStyleClass().remove("boton-barra-activo");
        if (activo) {
            boton.getStyleClass().add("boton-barra-activo");
        }
    }

    @FXML
    private void irAInventario() {
        Navegador.mostrar(Ventana.ADMIN_INVENTARIO);
    }

    @FXML
    private void irAPedidos() {
        Navegador.mostrar(Ventana.ADMIN_PEDIDOS);
    }

    @FXML
    private void irAProductos() {
        Navegador.mostrar(Ventana.ADMIN_PRODUCTOS);
    }

    @FXML
    private void irAClientes() {
        Navegador.mostrar(Ventana.ADMIN_CLIENTES);
    }

    @FXML
    private void irABuscar() {
        Navegador.mostrar(Ventana.ADMIN_BUSCAR);
    }

    @FXML
    private void irAPerfil() {
        Navegador.mostrar(Ventana.ADMIN_PERFIL);
    }

    @FXML
    private void cerrarSesion() {
        Navegador.cerrarSesion();
    }
}
