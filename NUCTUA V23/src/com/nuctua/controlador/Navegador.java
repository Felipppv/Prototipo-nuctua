package com.nuctua.controlador;

import com.nuctua.estructuras.PilaHistorial;
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
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;

public final class Navegador {

    private static final int MAXIMO_HISTORIAL = 20;
    private static final PilaHistorial<EntradaNavegacion> HISTORIAL =
            new PilaHistorial<>(MAXIMO_HISTORIAL, EntradaNavegacion::esIgualA);

    private static Stage escenario;
    private static Scene escena;
    private static Ventana actual;
    private static Object controladorActual;

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
        return navegar(ventana, true);
    }

    public static <T> T reemplazar(Ventana ventana) {
        return navegar(ventana, false);
    }

    @SuppressWarnings("unchecked")
    private static <T> T navegar(Ventana destino, boolean recordar) {
        if (destino.esPopup()) {
            throw new IllegalArgumentException("Los popups se abren con Popup.abrir");
        }
        Ventana ventana = permitida(destino);
        if (recordar) {
            recordarActual(ventana);
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
            escena.addEventFilter(KeyEvent.KEY_PRESSED, evento -> {
                if (evento.isAltDown() && evento.getCode() == KeyCode.LEFT && puedeVolverConAtajo()) {
                    evento.consume();
                    volver();
                }
            });
            escena.addEventFilter(MouseEvent.MOUSE_PRESSED, evento -> {
                if (evento.getButton() == MouseButton.BACK && puedeVolverConAtajo()) {
                    evento.consume();
                    volver();
                }
            });
            escenario.setScene(escena);
        } else {
            escena.setRoot(raiz);
        }
        if (ventana.getArea() == Area.ACCESO) {
            HISTORIAL.vaciar();
        }
        actual = ventana;
        controladorActual = cargador == null ? null : cargador.getController();
        escenario.setTitle("Nuctua · " + ventana.getTitulo());
        return (T) controladorActual;
    }

    private static Ventana permitida(Ventana ventana) {
        SesionActual sesion = SesionActual.getInstancia();
        if (ventana.getArea() == Area.CLIENTE && (!sesion.haySesion() || sesion.esAdministrador())) {
            return sesion.esAdministrador() ? Ventana.ADMIN_INVENTARIO : Ventana.LOGIN_CLIENTE;
        }
        if (ventana.getArea() == Area.ADMINISTRADOR && !sesion.esAdministrador()) {
            return sesion.haySesion() ? Ventana.CATALOGO : Ventana.LOGIN_ADMINISTRADOR;
        }
        return ventana;
    }

    private static void recordarActual(Ventana destino) {
        if (actual == null || actual == destino || actual.getArea() == Area.ACCESO || destino.getArea() == Area.ACCESO) {
            return;
        }
        HISTORIAL.visitar(entradaActual());
    }

    public static void recordar(EntradaNavegacion entrada) {
        if (entrada != null && entrada.getVentana().getArea() != Area.ACCESO) {
            HISTORIAL.visitar(entrada);
        }
    }

    public static EntradaNavegacion entradaActual() {
        if (actual == null) {
            return null;
        }
        String estado = controladorActual instanceof ConEstadoNavegacion vista ? vista.capturarEstado() : "";
        return new EntradaNavegacion(actual, estado);
    }

    public static void volver() {
        EntradaNavegacion presente = entradaActual();
        EntradaNavegacion previa = HISTORIAL.retroceder();
        while (previa != null && previa.esIgualA(presente)) {
            previa = HISTORIAL.retroceder();
        }
        if (previa == null) {
            Ventana inicio = inicioSegunRol();
            if (actual != inicio) {
                navegar(inicio, false);
            }
            return;
        }
        Object controlador = navegar(previa.getVentana(), false);
        if (controlador instanceof ConEstadoNavegacion vista && !previa.getEstado().isEmpty()) {
            vista.restaurarEstado(previa.getEstado());
        }
    }

    public static Ventana destinoVolver() {
        EntradaNavegacion previa = HISTORIAL.cima();
        return previa == null ? inicioSegunRol() : previa.getVentana();
    }

    public static String textoVolver() {
        return switch (destinoVolver()) {
            case CATALOGO -> "Volver al catálogo";
            case BUSCAR_ZAPATOS -> "Volver a la búsqueda";
            case DETALLE_PRODUCTO -> "Volver al par anterior";
            case LISTA_DESEOS -> "Volver a tu lista de deseos";
            case CARRITO -> "Volver al carrito";
            case PAGO -> "Volver al pago";
            case HISTORIAL_COMPRAS -> "Volver a tu historial";
            case PERFIL_USUARIO -> "Volver a tu perfil";
            case ADMIN_INVENTARIO -> "Volver al inventario";
            case ADMIN_PERFIL -> "Volver a tu perfil";
            case ADMIN_BUSCAR -> "Volver a la búsqueda";
            case ADMIN_REGISTRAR_PRODUCTO -> "Volver al registro";
            default -> "Volver a " + destinoVolver().getTitulo().toLowerCase();
        };
    }

    public static PilaHistorial<EntradaNavegacion> getHistorial() {
        return HISTORIAL;
    }

    private static boolean puedeVolverConAtajo() {
        return actual != null && actual.getArea() != Area.ACCESO;
    }

    private static Ventana inicioSegunRol() {
        return SesionActual.getInstancia().esAdministrador() ? Ventana.ADMIN_INVENTARIO : Ventana.CATALOGO;
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
        mostrar(inicioSegunRol());
    }

    public static void cerrarSesion() {
        SesionActual.getInstancia().cerrar();
        HISTORIAL.vaciar();
        mostrar(Ventana.LOGIN_CLIENTE);
    }

    public static Ventana getActual() {
        return actual;
    }

    public static Stage getEscenario() {
        return escenario;
    }
}
