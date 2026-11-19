package com.nuctua.controlador;

import com.nuctua.modelo.SesionActual;
import com.nuctua.persistencia.ArchivoProductos;
import com.nuctua.util.Recursos;
import com.nuctua.vista.Area;
import com.nuctua.vista.Ventana;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public final class Navegador {

    private static Stage escenario;
    private static Scene escena;
    private static Ventana actual;

    private Navegador() {
    }

    public static void iniciar(Stage principal) {
        escenario = principal;
        Recursos.cargarFuentes();
        ArrayList<String> faltantes = Recursos.imagenesFaltantes(new ArchivoProductos().cargar());
        for (String faltante : faltantes) {
            System.err.println("Imagen no encontrada: " + faltante);
        }
        escenario.setTitle("Nuctua · Calzado exclusivo");
        escenario.setMinWidth(1100);
        escenario.setMinHeight(700);
        mostrar(Ventana.LOGIN_CLIENTE);
        escenario.show();
    }

    public static <T> T mostrar(Ventana ventana) {
        if (ventana.esPopup()) {
            throw new IllegalArgumentException("Los popups se abren con Popup.abrir");
        }
        SesionActual sesion = SesionActual.getInstancia();
        if (ventana.getArea() == Area.CLIENTE && (!sesion.haySesion() || sesion.esAdministrador())) {
            ventana = sesion.esAdministrador() ? Ventana.ADMIN_INVENTARIO : Ventana.LOGIN_CLIENTE;
        } else if (ventana.getArea() == Area.ADMINISTRADOR && !sesion.esAdministrador()) {
            ventana = sesion.haySesion() ? Ventana.CATALOGO : Ventana.LOGIN_ADMINISTRADOR;
        }
        URL ruta = Navegador.class.getResource(ventana.getRutaFxml());
        FXMLLoader cargador = null;
        Parent raiz;
        if (ruta == null) {
            raiz = VistaPendiente.crear(ventana);
        } else {
            cargador = new FXMLLoader(ruta);
            try {
                raiz = cargador.load();
            } catch (IOException e) {
                throw new IllegalStateException("No se pudo cargar la vista " + ventana.getTitulo(), e);
            }
        }
        if (escena == null) {
            escena = new Scene(raiz, 1360, 860);
            escena.getStylesheets().add(Recursos.hojaDeEstilos());
            escenario.setScene(escena);
        } else {
            escena.setRoot(raiz);
        }
        actual = ventana;
        escenario.setTitle("Nuctua · " + ventana.getTitulo());
        return cargador == null ? null : cargador.getController();
    }

    public static Node crearBarra(Ventana ventana) {
        boolean admin = ventana.getArea() == Area.ADMINISTRADOR;
        FXMLLoader cargador = new FXMLLoader(Navegador.class.getResource(
                admin ? "/com/nuctua/vista/BarraAdmin.fxml" : "/com/nuctua/vista/BarraCliente.fxml"));
        try {
            Node barra = cargador.load();
            if (admin) {
                ((BarraAdminController) cargador.getController()).marcar(ventana);
            } else {
                ((BarraClienteController) cargador.getController()).marcar(ventana);
            }
            return barra;
        } catch (IOException e) {
            throw new IllegalStateException("No se pudo cargar la barra de navegación", e);
        }
    }

    public static void irAInicioSegunRol() {
        if (SesionActual.getInstancia().esAdministrador()) {
            mostrar(Ventana.ADMIN_INVENTARIO);
        } else {
            mostrar(Ventana.CATALOGO);
        }
    }

    public static void cerrarSesion() {
        SesionActual.getInstancia().cerrar();
        mostrar(Ventana.LOGIN_CLIENTE);
    }

    public static Ventana getActual() {
        return actual;
    }

    public static Stage getEscenario() {
        return escenario;
    }
}
