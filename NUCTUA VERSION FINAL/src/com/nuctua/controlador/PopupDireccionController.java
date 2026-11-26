package com.nuctua.controlador;

import com.nuctua.modelo.CampoPerfil;
import com.nuctua.modelo.Direccion;
import com.nuctua.modelo.ErrorCampo;
import com.nuctua.modelo.SesionActual;
import com.nuctua.servicio.DireccionesServicio;
import com.nuctua.util.FormatoCampo;
import com.nuctua.util.Iconos;
import com.nuctua.vista.CampoFormulario;
import com.nuctua.vista.Ventana;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.Toggle;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.HBox;

public class PopupDireccionController extends PopupBase {

    private static final String[] ETIQUETAS = {"Casa", "Trabajo", "Otra"};

    @FXML
    private Label titulo;
    @FXML
    private HBox etiquetas;
    @FXML
    private CampoFormulario campoRecibe;
    @FXML
    private CampoFormulario campoDireccion;
    @FXML
    private CampoFormulario campoIndicaciones;
    @FXML
    private CampoFormulario campoCiudad;
    @FXML
    private CheckBox checkPrincipal;
    @FXML
    private Label mensaje;
    @FXML
    private Button botonEliminar;

    private final DireccionesServicio servicio = new DireccionesServicio();
    private final ToggleGroup grupo = new ToggleGroup();
    private int posicion = -1;
    private boolean cambio;

    @FXML
    private void initialize() {
        for (String etiqueta : ETIQUETAS) {
            ToggleButton boton = new ToggleButton(etiqueta);
            boton.getStyleClass().add("chip-perfil");
            boton.setToggleGroup(grupo);
            boton.setUserData(etiqueta);
            etiquetas.getChildren().add(boton);
        }
        FormatoCampo.texto(campoRecibe.getCampo(), 60);
        FormatoCampo.texto(campoDireccion.getCampo(), 100);
        FormatoCampo.texto(campoIndicaciones.getCampo(), 100);
        FormatoCampo.texto(campoCiudad.getCampo(), 60);
    }

    public static boolean mostrar(Direccion direccion, int posicion) {
        PopupDireccionController popup = Popup.abrir(Ventana.POPUP_DIRECCION);
        popup.setDireccion(direccion, posicion);
        popup.mostrarYEsperar();
        return popup.cambio;
    }

    public void setDireccion(Direccion direccion, int lugar) {
        posicion = direccion == null ? -1 : lugar;
        titulo.setText(direccion == null ? "AGREGA UNA DIRECCIÓN" : "EDITA TU DIRECCIÓN");
        botonEliminar.setVisible(direccion != null);
        botonEliminar.setManaged(direccion != null);
        if (direccion == null) {
            campoRecibe.setTexto(SesionActual.getInstancia().getUsuario().getNombre());
            elegirEtiqueta(servicio.lista().estaVacia() ? "Casa" : "Trabajo");
            checkPrincipal.setSelected(servicio.lista().estaVacia());
            checkPrincipal.setDisable(servicio.lista().estaVacia());
            return;
        }
        campoRecibe.setTexto(direccion.getRecibe());
        campoDireccion.setTexto(direccion.getDireccion());
        campoIndicaciones.setTexto(direccion.getIndicaciones());
        campoCiudad.setTexto(direccion.getCiudad());
        elegirEtiqueta(direccion.getEtiqueta());
        checkPrincipal.setSelected(direccion.isPrincipal());
        checkPrincipal.setDisable(direccion.isPrincipal());
    }

    public void elegirEtiqueta(String etiqueta) {
        for (Toggle opcion : grupo.getToggles()) {
            if (opcion.getUserData().equals(etiqueta)) {
                opcion.setSelected(true);
            }
        }
        if (grupo.getSelectedToggle() == null) {
            grupo.getToggles().get(ETIQUETAS.length - 1).setSelected(true);
        }
    }

    @FXML
    private void guardar() {
        String etiqueta = grupo.getSelectedToggle() == null ? ETIQUETAS[0] : (String) grupo.getSelectedToggle().getUserData();
        Direccion nueva = new Direccion(etiqueta, campoRecibe.getTexto(), campoDireccion.getTexto(),
                campoIndicaciones.getTexto(), campoCiudad.getTexto(), checkPrincipal.isSelected());
        CampoFormulario[] campos = {campoRecibe, campoDireccion, campoCiudad};
        for (CampoFormulario campo : campos) {
            campo.limpiarError();
        }
        for (ErrorCampo error : nueva.validar()) {
            campoDe((CampoPerfil) error.getCampo()).mostrarError(error.getMensaje());
        }
        if (!nueva.validar().estaVacia()) {
            return;
        }
        if (!servicio.guardar(posicion, nueva)) {
            avisar(posicion < 0 && servicio.lista().tamanio() >= DireccionesServicio.MAXIMO
                    ? "Ya tienes el máximo de " + DireccionesServicio.MAXIMO + " direcciones."
                    : "No pudimos guardar la dirección. Inténtalo de nuevo.");
            return;
        }
        cambio = true;
        cerrar();
    }

    @FXML
    private void eliminar() {
        Direccion actual = servicio.lista().obtener(posicion);
        boolean confirmado = PopupEliminarProductoController.confirmar("¿ELIMINAR ESTA DIRECCIÓN?",
                Iconos.basura(Iconos.OSCURO, 30), actual.getEtiqueta(), actual.getLinea() + ", " + actual.getCiudad(), "",
                "Se borrará de tu perfil. Tus pedidos anteriores no cambian.");
        if (!confirmado) {
            return;
        }
        if (!servicio.eliminar(posicion)) {
            avisar("No pudimos eliminar la dirección. Inténtalo de nuevo.");
            return;
        }
        cambio = true;
        cerrar();
    }

    private CampoFormulario campoDe(CampoPerfil campo) {
        return switch (campo) {
            case RECIBE -> campoRecibe;
            case CIUDAD -> campoCiudad;
            default -> campoDireccion;
        };
    }

    private void avisar(String texto) {
        mensaje.setText(texto);
        mensaje.setVisible(true);
        mensaje.setManaged(true);
    }
}
