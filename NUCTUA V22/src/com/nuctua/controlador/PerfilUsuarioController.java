package com.nuctua.controlador;

import com.nuctua.estructuras.ListaEnlazada;
import com.nuctua.modelo.CampoPerfil;
import com.nuctua.modelo.Cliente;
import com.nuctua.modelo.Direccion;
import com.nuctua.modelo.ErrorCampo;
import com.nuctua.modelo.EstadoCliente;
import com.nuctua.modelo.ResumenCliente;
import com.nuctua.modelo.SesionActual;
import com.nuctua.servicio.DireccionesServicio;
import com.nuctua.servicio.PerfilServicio;
import com.nuctua.servicio.PreferenciasServicio;
import com.nuctua.util.FormatoCampo;
import com.nuctua.vista.Avatar;
import com.nuctua.vista.CampoFormulario;
import com.nuctua.vista.Interruptor;
import com.nuctua.vista.TarjetaDireccion;
import com.nuctua.vista.Ventana;
import java.time.LocalDate;
import java.util.ArrayList;
import javafx.fxml.FXML;
import javafx.geometry.Bounds;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

public class PerfilUsuarioController {

    private static final int TALLA_MINIMA = 38;
    private static final int TALLA_MAXIMA = 44;

    @FXML
    private BarraClienteController barraController;
    @FXML
    private ScrollPane scroll;
    @FXML
    private Hyperlink enlaceVolver;
    @FXML
    private Label saludo;
    @FXML
    private StackPane avatar;
    @FXML
    private Label nombrePerfil;
    @FXML
    private Label correoPerfil;
    @FXML
    private Label estadoPerfil;
    @FXML
    private Label cifraPedidos;
    @FXML
    private Label cifraPares;
    @FXML
    private Label cifraDeseos;
    @FXML
    private Button menuDatos;
    @FXML
    private Button menuDirecciones;
    @FXML
    private Button menuPedidos;
    @FXML
    private Button menuDeseos;
    @FXML
    private VBox tarjetaDatos;
    @FXML
    private VBox tarjetaDirecciones;
    @FXML
    private CampoFormulario campoNombre;
    @FXML
    private CampoFormulario campoApellido;
    @FXML
    private CampoFormulario campoCorreo;
    @FXML
    private CampoFormulario campoTelefono;
    @FXML
    private CampoFormulario campoCumpleanos;
    @FXML
    private CampoFormulario campoDocumento;
    @FXML
    private Label mensajeDatos;
    @FXML
    private GridPane direcciones;
    @FXML
    private Label sinDirecciones;
    @FXML
    private Button botonAgregarDireccion;
    @FXML
    private FlowPane tallas;
    @FXML
    private FlowPane intereses;
    @FXML
    private Interruptor interruptorNovedades;
    @FXML
    private Interruptor interruptorOfertas;
    @FXML
    private Label mensajePreferencias;

    private final PerfilServicio perfil = new PerfilServicio();
    private final DireccionesServicio servicioDirecciones = new DireccionesServicio();
    private final PreferenciasServicio preferencias = new PreferenciasServicio();

    @FXML
    private void initialize() {
        barraController.marcar(Ventana.PERFIL_USUARIO);
        enlaceVolver.setText("← " + Navegador.textoVolver());
        enlaceVolver.setOnAction(e -> Navegador.volver());
        for (Button menu : new Button[]{menuDatos, menuDirecciones, menuPedidos, menuDeseos}) {
            decorarMenu(menu);
        }
        FormatoCampo.texto(campoNombre.getCampo(), 30);
        FormatoCampo.texto(campoApellido.getCampo(), 40);
        FormatoCampo.telefono(campoTelefono.getCampo());
        FormatoCampo.fecha(campoCumpleanos.getCampo());
        FormatoCampo.documento(campoDocumento.getCampo());
        campoCorreo.getCampo().setDisable(true);
        Tooltip.install(campoCorreo, new Tooltip("Tu correo es tu usuario y no se puede cambiar."));
        for (CampoFormulario campo : editables()) {
            campo.getCampo().setOnAction(e -> guardar());
            campo.getCampo().textProperty().addListener((obs, antes, ahora) -> avisarDatos("", false));
        }
        if (SesionActual.getInstancia().getCliente() != null) {
            pintarCuenta();
            cargarDatos();
            pintarDirecciones();
            pintarPreferencias();
        }
    }

    private CampoFormulario[] editables() {
        return new CampoFormulario[]{campoNombre, campoApellido, campoTelefono, campoCumpleanos, campoDocumento};
    }

    private void decorarMenu(Button boton) {
        Label texto = new Label(boton.getText());
        Region espacio = new Region();
        HBox.setHgrow(espacio, Priority.ALWAYS);
        HBox fila = new HBox(texto, espacio, new Label("→"));
        fila.setAlignment(Pos.CENTER_LEFT);
        fila.prefWidthProperty().bind(boton.widthProperty().subtract(40));
        boton.setAccessibleText(boton.getText());
        boton.setGraphic(fila);
        boton.setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
    }

    private void pintarCuenta() {
        Cliente cliente = SesionActual.getInstancia().getCliente();
        ResumenCliente resumen = perfil.resumen(cliente);
        saludo.setText("Hola, " + cliente.getPrimerNombre());
        avatar.getChildren().setAll(new Avatar(cliente, 84, 0));
        nombrePerfil.setText(cliente.getNombre());
        correoPerfil.setText(cliente.getCorreo());
        estadoPerfil.setText(resumen.getEstado() == EstadoCliente.INACTIVO ? "CLIENTE NUCTUA"
                : "CLIENTE " + resumen.getEstado().getTexto().toUpperCase());
        cifraPedidos.setText(String.valueOf(resumen.getCompras()));
        cifraPares.setText(String.valueOf(resumen.getPares()));
        cifraDeseos.setText(String.valueOf(resumen.getDeseos()));
    }

    private void cargarDatos() {
        Cliente cliente = SesionActual.getInstancia().getCliente();
        campoNombre.setTexto(cliente.getPrimerNombre());
        campoApellido.setTexto(cliente.getApellido());
        campoCorreo.setTexto(cliente.getCorreo());
        campoTelefono.setTexto(cliente.getTelefono());
        campoCumpleanos.setTexto(cliente.getCumpleanos());
        campoDocumento.setTexto(cliente.getDocumento());
    }

    @FXML
    private void guardar() {
        for (CampoFormulario campo : editables()) {
            campo.limpiarError();
        }
        ListaEnlazada<ErrorCampo> errores = perfil.validarCliente(campoNombre.getTexto(), campoApellido.getTexto(),
                campoTelefono.getTexto(), campoCumpleanos.getTexto(), campoDocumento.getTexto(), LocalDate.now());
        for (ErrorCampo error : errores) {
            campoDe((CampoPerfil) error.getCampo()).mostrarError(error.getMensaje());
        }
        if (!errores.estaVacia()) {
            campoDe((CampoPerfil) errores.obtener(0).getCampo()).getControl().requestFocus();
            avisarDatos(errores.tamanio() == 1 ? "Revisa el campo marcado."
                    : "Revisa los " + errores.tamanio() + " campos marcados.", true);
            return;
        }
        if (!perfil.guardarCliente(campoNombre.getTexto(), campoApellido.getTexto(), campoTelefono.getTexto(),
                campoCumpleanos.getTexto(), campoDocumento.getTexto())) {
            avisarDatos("No pudimos guardar tus datos. Inténtalo de nuevo.", true);
            return;
        }
        pintarCuenta();
        cargarDatos();
        Cliente cliente = SesionActual.getInstancia().getCliente();
        PopupCambiosGuardadosController.mostrar(new Avatar(cliente, 58, 0), cliente.getNombre(), cliente.getCorreo(),
                "Tus datos quedaron guardados y se usarán en tu próxima compra.");
    }

    private CampoFormulario campoDe(CampoPerfil campo) {
        return switch (campo) {
            case APELLIDO -> campoApellido;
            case TELEFONO -> campoTelefono;
            case CUMPLEANOS -> campoCumpleanos;
            case DOCUMENTO -> campoDocumento;
            default -> campoNombre;
        };
    }

    @FXML
    private void cambiarContrasena() {
        if (PopupCambiarContrasenaController.mostrar()) {
            avisarDatos("Contraseña actualizada.", false);
        }
    }

    private void pintarDirecciones() {
        direcciones.getChildren().clear();
        ListaEnlazada<Direccion> lista = servicioDirecciones.lista();
        int posicion = 0;
        for (Direccion direccion : lista) {
            int lugar = posicion;
            TarjetaDireccion tarjeta = new TarjetaDireccion(direccion);
            tarjeta.getEnlaceEditar().setOnAction(e -> abrirDireccion(direccion, lugar));
            direcciones.add(tarjeta, posicion % 2, posicion / 2);
            posicion++;
        }
        alternar(direcciones, posicion > 0);
        alternar(sinDirecciones, posicion == 0);
        boolean lleno = posicion >= DireccionesServicio.MAXIMO;
        botonAgregarDireccion.setDisable(lleno);
        botonAgregarDireccion.setText(lleno ? "Llegaste al máximo de " + DireccionesServicio.MAXIMO + " direcciones" : "+ Agregar dirección");
    }

    @FXML
    private void agregarDireccion() {
        abrirDireccion(null, -1);
    }

    private void abrirDireccion(Direccion direccion, int posicion) {
        if (PopupDireccionController.mostrar(direccion, posicion)) {
            pintarDirecciones();
        }
    }

    private void pintarPreferencias() {
        int habitual = preferencias.getTalla();
        for (int talla = TALLA_MINIMA; talla <= TALLA_MAXIMA; talla++) {
            int valor = talla;
            ToggleButton boton = chip(String.valueOf(talla));
            boton.setSelected(talla == habitual);
            boton.setOnAction(e -> elegirTalla(boton, valor));
            tallas.getChildren().add(boton);
        }
        ArrayList<String> elegidos = preferencias.getIntereses();
        for (String opcion : preferencias.opcionesDeInteres()) {
            ToggleButton boton = chip(opcion);
            boton.setSelected(elegidos.contains(opcion));
            boton.setOnAction(e -> guardarIntereses());
            intereses.getChildren().add(boton);
        }
        interruptorNovedades.setSelected(preferencias.isActiva(PreferenciasServicio.NOVEDADES, true));
        interruptorOfertas.setSelected(preferencias.isActiva(PreferenciasServicio.OFERTAS, false));
        interruptorNovedades.setOnAction(e -> avisarPreferencia(
                preferencias.setActiva(PreferenciasServicio.NOVEDADES, interruptorNovedades.isSelected())));
        interruptorOfertas.setOnAction(e -> avisarPreferencia(
                preferencias.setActiva(PreferenciasServicio.OFERTAS, interruptorOfertas.isSelected())));
    }

    private ToggleButton chip(String texto) {
        ToggleButton boton = new ToggleButton(texto);
        boton.getStyleClass().add("chip-perfil");
        return boton;
    }

    private void elegirTalla(ToggleButton elegido, int talla) {
        for (Node nodo : tallas.getChildren()) {
            if (nodo != elegido) {
                ((ToggleButton) nodo).setSelected(false);
            }
        }
        avisarPreferencia(preferencias.setTalla(elegido.isSelected() ? talla : 0));
    }

    private void guardarIntereses() {
        ArrayList<String> elegidos = new ArrayList<>();
        for (Node nodo : intereses.getChildren()) {
            ToggleButton boton = (ToggleButton) nodo;
            if (boton.isSelected()) {
                elegidos.add(boton.getText());
            }
        }
        avisarPreferencia(preferencias.setIntereses(elegidos));
    }

    private void avisarPreferencia(boolean guardada) {
        mensajePreferencias.setText(guardada ? "Preferencia guardada." : "No pudimos guardar tu preferencia.");
        mensajePreferencias.getStyleClass().setAll("label", guardada ? "mensaje-ok" : "mensaje-error");
        alternar(mensajePreferencias, true);
    }

    private void avisarDatos(String texto, boolean error) {
        mensajeDatos.setText(texto);
        mensajeDatos.getStyleClass().setAll("label", error ? "mensaje-error" : "mensaje-ok");
        alternar(mensajeDatos, !texto.isEmpty());
    }

    @FXML
    private void irADatos() {
        activarMenu(menuDatos, tarjetaDatos);
    }

    @FXML
    private void irADirecciones() {
        activarMenu(menuDirecciones, tarjetaDirecciones);
    }

    @FXML
    private void irAPedidos() {
        Navegador.mostrar(Ventana.HISTORIAL_COMPRAS);
    }

    @FXML
    private void irADeseos() {
        Navegador.mostrar(Ventana.LISTA_DESEOS);
    }

    @FXML
    private void cerrarSesion() {
        Navegador.cerrarSesion();
    }

    private void activarMenu(Button menu, Node destino) {
        menuDatos.getStyleClass().remove("boton-menu-activo");
        menuDirecciones.getStyleClass().remove("boton-menu-activo");
        menu.getStyleClass().add("boton-menu-activo");
        Node contenido = scroll.getContent();
        double recorrido = contenido.getBoundsInLocal().getHeight() - scroll.getViewportBounds().getHeight();
        Bounds posicion = contenido.sceneToLocal(destino.localToScene(destino.getBoundsInLocal()));
        if (posicion != null && recorrido > 0) {
            scroll.setVvalue(Math.max(0, Math.min(1, (posicion.getMinY() - 30) / recorrido)));
        }
    }

    private void alternar(Node nodo, boolean visible) {
        nodo.setVisible(visible);
        nodo.setManaged(visible);
    }
}
