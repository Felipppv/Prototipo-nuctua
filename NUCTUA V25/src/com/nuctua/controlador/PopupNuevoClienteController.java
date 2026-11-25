package com.nuctua.controlador;

import com.nuctua.estructuras.ListaEnlazada;
import com.nuctua.modelo.CampoPerfil;
import com.nuctua.modelo.Cliente;
import com.nuctua.modelo.ErrorCampo;
import com.nuctua.servicio.ClientesServicio;
import com.nuctua.util.FormatoCampo;
import com.nuctua.vista.CampoFormulario;
import com.nuctua.vista.Ventana;
import javafx.fxml.FXML;
import javafx.scene.control.Label;

public class PopupNuevoClienteController extends PopupBase {

    @FXML
    private CampoFormulario campoNombre;
    @FXML
    private CampoFormulario campoCorreo;
    @FXML
    private CampoFormulario campoTelefono;
    @FXML
    private CampoFormulario campoCiudad;
    @FXML
    private Label mensaje;

    private final ClientesServicio servicio = new ClientesServicio();
    private Cliente creado;

    @FXML
    private void initialize() {
        FormatoCampo.texto(campoNombre.getCampo(), 60);
        FormatoCampo.texto(campoCorreo.getCampo(), 100);
        FormatoCampo.telefono(campoTelefono.getCampo());
        FormatoCampo.texto(campoCiudad.getCampo(), 60);
    }

    public static Cliente mostrar() {
        PopupNuevoClienteController popup = Popup.abrir(Ventana.POPUP_NUEVO_CLIENTE);
        popup.mostrarYEsperar();
        return popup.creado;
    }

    @FXML
    private void agregar() {
        for (CampoFormulario campo : new CampoFormulario[]{campoNombre, campoCorreo, campoTelefono, campoCiudad}) {
            campo.limpiarError();
        }
        ListaEnlazada<ErrorCampo> errores = servicio.validar(campoNombre.getTexto(), campoCorreo.getTexto(),
                campoTelefono.getTexto(), campoCiudad.getTexto());
        for (ErrorCampo error : errores) {
            campoDe((CampoPerfil) error.getCampo()).mostrarError(error.getMensaje());
        }
        if (!errores.estaVacia()) {
            return;
        }
        creado = servicio.agregar(campoNombre.getTexto(), campoCorreo.getTexto(), campoTelefono.getTexto(), campoCiudad.getTexto());
        if (creado == null) {
            mensaje.setText("No pudimos agregar al cliente. Inténtalo de nuevo.");
            mensaje.setVisible(true);
            mensaje.setManaged(true);
            return;
        }
        cerrar();
    }

    private CampoFormulario campoDe(CampoPerfil campo) {
        return switch (campo) {
            case CORREO -> campoCorreo;
            case TELEFONO -> campoTelefono;
            case CIUDAD -> campoCiudad;
            default -> campoNombre;
        };
    }
}
