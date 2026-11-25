package com.nuctua.controlador;

import com.nuctua.servicio.PedidosServicio;
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
        botonAvisos.setTooltip(new Tooltip(new PedidosServicio().describirCola()));
        botonPerfil.setTooltip(new Tooltip("Mi perfil"));
        botonSalir.setTooltip(new Tooltip("Cerrar sesión"));
    }

    public void marcar(Ventana ventana) {
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
