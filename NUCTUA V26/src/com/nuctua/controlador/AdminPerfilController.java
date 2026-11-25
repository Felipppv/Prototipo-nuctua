package com.nuctua.controlador;

import com.nuctua.estructuras.ListaEnlazada;
import com.nuctua.modelo.Actividad;
import com.nuctua.modelo.Administrador;
import com.nuctua.modelo.CampoPerfil;
import com.nuctua.modelo.ErrorCampo;
import com.nuctua.modelo.EstadoPedido;
import com.nuctua.modelo.Pedido;
import com.nuctua.modelo.SesionActual;
import com.nuctua.servicio.AccesosServicio;
import com.nuctua.servicio.ActividadServicio;
import com.nuctua.servicio.AutenticacionServicio;
import com.nuctua.servicio.CatalogoServicio;
import com.nuctua.servicio.PedidosServicio;
import com.nuctua.servicio.PerfilServicio;
import com.nuctua.servicio.PreferenciasServicio;
import com.nuctua.servicio.ResultadoAcceso;
import com.nuctua.util.CampoContrasena;
import com.nuctua.util.FormatoCampo;
import com.nuctua.util.Iconos;
import com.nuctua.vista.Avatar;
import com.nuctua.vista.CampoFormulario;
import com.nuctua.vista.Interruptor;
import com.nuctua.vista.TarjetaMetrica;
import com.nuctua.vista.Ventana;
import java.time.LocalDate;
import java.time.LocalDateTime;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

public class AdminPerfilController {

    private static final int MAXIMO_ACTIVIDAD = 6;

    @FXML
    private BarraAdminController barraController;
    @FXML
    private Hyperlink enlaceVolver;
    @FXML
    private HBox metricas;
    @FXML
    private StackPane avatar;
    @FXML
    private Label nombrePerfil;
    @FXML
    private Label correoPerfil;
    @FXML
    private Label rolPerfil;
    @FXML
    private Label tiendaPerfil;
    @FXML
    private VBox cajaMiembro;
    @FXML
    private Label miembroPerfil;
    @FXML
    private CampoFormulario campoNombre;
    @FXML
    private CampoFormulario campoCorreo;
    @FXML
    private CampoFormulario campoTelefono;
    @FXML
    private CampoFormulario campoCiudad;
    @FXML
    private Label mensajeDatos;
    @FXML
    private CampoContrasena campoActual;
    @FXML
    private CampoContrasena campoNueva;
    @FXML
    private Label mensajeSeguridad;
    @FXML
    private Interruptor interruptorPedidos;
    @FXML
    private Interruptor interruptorStock;
    @FXML
    private Interruptor interruptorClientes;
    @FXML
    private Label mensajeAvisos;
    @FXML
    private VBox actividad;

    private final PerfilServicio perfil = new PerfilServicio();
    private final PreferenciasServicio preferencias = new PreferenciasServicio();
    private final ActividadServicio registro = new ActividadServicio();

    @FXML
    private void initialize() {
        barraController.marcar(Ventana.ADMIN_PERFIL);
        enlaceVolver.setText(Navegador.textoVolver());
        enlaceVolver.setGraphic(Iconos.flechaIzquierda(Iconos.OSCURO, 18));
        enlaceVolver.setOnAction(e -> Navegador.volver());
        FormatoCampo.texto(campoNombre.getCampo(), 60);
        FormatoCampo.telefono(campoTelefono.getCampo());
        FormatoCampo.texto(campoCiudad.getCampo(), 60);
        campoCorreo.getCampo().setDisable(true);
        for (CampoFormulario campo : new CampoFormulario[]{campoNombre, campoTelefono, campoCiudad}) {
            campo.getCampo().setOnAction(e -> guardar());
        }
        campoNueva.getCampo().setOnAction(e -> cambiarContrasena());
        Administrador admin = administrador();
        if (admin == null) {
            return;
        }
        pintarMetricas(admin);
        pintarCuenta(admin);
        cargarDatos(admin);
        conectar(interruptorPedidos, PreferenciasServicio.AVISO_PEDIDOS, true);
        conectar(interruptorStock, PreferenciasServicio.AVISO_STOCK, true);
        conectar(interruptorClientes, PreferenciasServicio.AVISO_CLIENTES, false);
        pintarActividad();
    }

    private Administrador administrador() {
        return SesionActual.getInstancia().getUsuario() instanceof Administrador admin ? admin : null;
    }

    private void pintarMetricas(Administrador admin) {
        PedidosServicio pedidos = new PedidosServicio();
        TarjetaMetrica gestionados = new TarjetaMetrica("PEDIDOS GESTIONADOS", true);
        gestionados.mostrar(pedidos.todos().tamanio() - pedidos.cuantos(EstadoPedido.NUEVO), "Desde que abriste la tienda");
        TarjetaMetrica productos = new TarjetaMetrica("PRODUCTOS CREADOS", false);
        productos.mostrar(new CatalogoServicio().getCatalogo().tamanio(), "En el catálogo actual");
        TarjetaMetrica clientes = new TarjetaMetrica("CLIENTES ATENDIDOS", false);
        clientes.mostrar(new AutenticacionServicio().contarClientes(), "Con cuenta en la tienda");
        TarjetaMetrica acceso = new TarjetaMetrica("ÚLTIMO ACCESO", false);
        LocalDateTime anterior = new AccesosServicio().anterior(admin);
        String ciudad = admin.getCiudad().isEmpty() ? "" : " · " + admin.getCiudad();
        if (anterior == null) {
            acceso.mostrar("Hoy", "Tu primer ingreso registrado");
        } else {
            acceso.mostrar(Actividad.describirDia(anterior, LocalDate.now()), Actividad.describirHora(anterior) + ciudad);
        }
        metricas.getChildren().setAll(gestionados, productos, clientes, acceso);
    }

    private void pintarCuenta(Administrador admin) {
        avatar.getChildren().setAll(new Avatar(admin, 76, 0));
        nombrePerfil.setText(admin.getNombre());
        correoPerfil.setText(admin.getCorreo());
        rolPerfil.setText(admin.getCargo().isBlank() ? "Administrador" : admin.getCargo());
        tiendaPerfil.setText(admin.getCiudad().isEmpty() ? "Nuctua" : "Nuctua · " + admin.getCiudad());
        boolean conFecha = admin.getRegistro() != null;
        miembroPerfil.setText(conFecha ? Pedido.textoFecha(admin.getRegistro().atStartOfDay()) : "");
        cajaMiembro.setVisible(conFecha);
        cajaMiembro.setManaged(conFecha);
    }

    private void cargarDatos(Administrador admin) {
        campoNombre.setTexto(admin.getNombre());
        campoCorreo.setTexto(admin.getCorreo());
        campoTelefono.setTexto(admin.getTelefono());
        campoCiudad.setTexto(admin.getCiudad());
    }

    @FXML
    private void guardar() {
        limpiarDatos();
        ListaEnlazada<ErrorCampo> errores = perfil.validarAdministrador(campoNombre.getTexto(), campoTelefono.getTexto(),
                campoCiudad.getTexto());
        for (ErrorCampo error : errores) {
            campoDe((CampoPerfil) error.getCampo()).mostrarError(error.getMensaje());
        }
        if (!errores.estaVacia()) {
            return;
        }
        if (!perfil.guardarAdministrador(campoNombre.getTexto(), campoTelefono.getTexto(), campoCiudad.getTexto())) {
            avisar(mensajeDatos, "No pudimos guardar tus datos. Inténtalo de nuevo.", true);
            return;
        }
        registro.registrar("Actualizaste los datos de tu cuenta");
        Administrador admin = administrador();
        pintarCuenta(admin);
        cargarDatos(admin);
        pintarActividad();
        PopupCambiosGuardadosController.mostrar(new Avatar(admin, 58, 0), admin.getNombre(), admin.getCorreo(),
                "Los datos de tu cuenta quedaron guardados.");
    }

    @FXML
    private void cancelar() {
        limpiarDatos();
        Administrador admin = administrador();
        if (admin != null) {
            cargarDatos(admin);
        }
    }

    private void limpiarDatos() {
        for (CampoFormulario campo : new CampoFormulario[]{campoNombre, campoTelefono, campoCiudad}) {
            campo.limpiarError();
        }
        avisar(mensajeDatos, "", false);
    }

    private CampoFormulario campoDe(CampoPerfil campo) {
        return switch (campo) {
            case TELEFONO -> campoTelefono;
            case CIUDAD -> campoCiudad;
            default -> campoNombre;
        };
    }

    @FXML
    private void cambiarContrasena() {
        ResultadoAcceso resultado;
        try {
            resultado = new AutenticacionServicio().cambiarContrasena(campoActual.getTexto(), campoNueva.getTexto(),
                    campoNueva.getTexto());
        } catch (IllegalStateException e) {
            avisar(mensajeSeguridad, "No pudimos guardar el cambio. Inténtalo de nuevo.", true);
            return;
        }
        if (!resultado.esExito()) {
            avisar(mensajeSeguridad, resultado.getMensaje(), true);
            return;
        }
        campoActual.setTexto("");
        campoNueva.setTexto("");
        registro.registrar("Cambiaste tu contraseña");
        pintarActividad();
        avisar(mensajeSeguridad, "Contraseña actualizada.", false);
    }

    private void conectar(Interruptor interruptor, String clave, boolean porDefecto) {
        interruptor.setSelected(preferencias.isActiva(clave, porDefecto));
        interruptor.setOnAction(e -> {
            boolean guardada = preferencias.setActiva(clave, interruptor.isSelected());
            avisar(mensajeAvisos, guardada ? "Aviso actualizado en la campana." : "No pudimos guardar el cambio.", !guardada);
            barraController.actualizarAvisos();
        });
    }

    private void pintarActividad() {
        actividad.getChildren().clear();
        for (Actividad hecho : registro.recientes(MAXIMO_ACTIVIDAD)) {
            Region punto = new Region();
            punto.getStyleClass().add("punto-actividad");
            if (actividad.getChildren().isEmpty()) {
                punto.getStyleClass().add("punto-actividad-reciente");
            }
            Label texto = new Label(hecho.getTexto());
            texto.getStyleClass().addAll("texto", "negrita");
            texto.setWrapText(true);
            Region espacio = new Region();
            HBox.setHgrow(espacio, Priority.ALWAYS);
            Label cuando = new Label(hecho.getCuando(LocalDate.now()));
            cuando.getStyleClass().add("texto-suave");
            cuando.setMinWidth(Region.USE_PREF_SIZE);
            HBox fila = new HBox(14, punto, texto, espacio, cuando);
            fila.setAlignment(Pos.CENTER_LEFT);
            fila.getStyleClass().add("fila-actividad");
            actividad.getChildren().add(fila);
        }
        if (actividad.getChildren().isEmpty()) {
            Label vacia = new Label("Aún no hay actividad. Aquí verás los cambios que hagas.");
            vacia.getStyleClass().add("texto-suave");
            vacia.setWrapText(true);
            actividad.getChildren().add(vacia);
        }
    }

    @FXML
    private void cerrarSesion() {
        Navegador.cerrarSesion();
    }

    private void avisar(Label mensaje, String texto, boolean error) {
        mensaje.setText(texto);
        mensaje.getStyleClass().setAll("label", error ? "mensaje-error" : "mensaje-ok");
        alternar(mensaje, !texto.isEmpty());
    }

    private void alternar(Node nodo, boolean visible) {
        nodo.setVisible(visible);
        nodo.setManaged(visible);
    }
}
