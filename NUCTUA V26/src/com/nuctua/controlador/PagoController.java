package com.nuctua.controlador;

import com.nuctua.modelo.CampoCompra;
import com.nuctua.modelo.Cliente;
import com.nuctua.modelo.DatosEnvio;
import com.nuctua.modelo.ErrorCampo;
import com.nuctua.modelo.ItemCarrito;
import com.nuctua.modelo.MetodoPago;
import com.nuctua.modelo.Pago;
import com.nuctua.modelo.PagoContraEntrega;
import com.nuctua.modelo.PagoNequi;
import com.nuctua.modelo.PagoPse;
import com.nuctua.modelo.PagoTarjeta;
import com.nuctua.modelo.SesionActual;
import com.nuctua.servicio.CarritoServicio;
import com.nuctua.servicio.CompraServicio;
import com.nuctua.servicio.ResultadoCompra;
import com.nuctua.util.FormatoCampo;
import com.nuctua.util.Iconos;
import com.nuctua.vista.CampoFormulario;
import com.nuctua.vista.FilaResumen;
import com.nuctua.vista.OpcionPago;
import com.nuctua.vista.Ventana;
import java.util.ArrayList;
import javafx.fxml.FXML;
import javafx.geometry.Bounds;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

public class PagoController {

    private static final int LARGO_CORTO = 60;
    private static final int LARGO_LARGO = 100;

    @FXML
    private BarraClienteController barraController;
    @FXML
    private ScrollPane scroll;
    @FXML
    private Hyperlink enlaceVolver;
    @FXML
    private HBox aviso;
    @FXML
    private Label textoAviso;
    @FXML
    private VBox formularios;
    @FXML
    private VBox tarjetaVacia;
    @FXML
    private Button botonCatalogo;
    @FXML
    private CampoFormulario campoNombre;
    @FXML
    private CampoFormulario campoTelefono;
    @FXML
    private CampoFormulario campoCorreo;
    @FXML
    private CampoFormulario campoCiudad;
    @FXML
    private CampoFormulario campoDireccion;
    @FXML
    private CampoFormulario campoIndicaciones;
    @FXML
    private CheckBox checkGuardar;
    @FXML
    private GridPane opciones;
    @FXML
    private VBox datosTarjeta;
    @FXML
    private HBox datosPse;
    @FXML
    private VBox datosNequi;
    @FXML
    private Label datosContraEntrega;
    @FXML
    private CampoFormulario campoNumero;
    @FXML
    private CampoFormulario campoTitular;
    @FXML
    private CampoFormulario campoVence;
    @FXML
    private CampoFormulario campoCvv;
    @FXML
    private CampoFormulario campoBanco;
    @FXML
    private CampoFormulario campoDocumento;
    @FXML
    private CampoFormulario campoCelular;
    @FXML
    private VBox miniaturas;
    @FXML
    private ResumenCompraController resumenController;
    @FXML
    private Button botonConfirmar;
    @FXML
    private Label mensaje;
    @FXML
    private StackPane iconoSeguridad;

    private final CarritoServicio carrito = new CarritoServicio();
    private final CompraServicio compra = new CompraServicio();
    private final ToggleGroup grupo = new ToggleGroup();

    @FXML
    private void initialize() {
        barraController.marcar(Ventana.PAGO);
        enlaceVolver.setGraphic(Iconos.flechaIzquierda(Iconos.OSCURO, 20));
        enlaceVolver.setOnAction(e -> volverAlCarrito());
        botonCatalogo.setGraphic(Iconos.flechaDerecha(Iconos.CREMA, 18));
        botonCatalogo.setContentDisplay(ContentDisplay.RIGHT);
        botonCatalogo.setOnAction(e -> Navegador.mostrar(Ventana.CATALOGO));
        botonConfirmar.setGraphic(Iconos.flechaDerecha(Iconos.CREMA, 22));
        botonConfirmar.setContentDisplay(ContentDisplay.RIGHT);
        botonConfirmar.setOnAction(e -> confirmar());
        iconoSeguridad.getChildren().add(Iconos.candado(Iconos.OSCURO, 16));
        prepararCampos();
        prepararOpciones();
        ArrayList<String> cambios = carrito.sincronizar();
        barraController.actualizarContadores();
        pintarResumen();
        if (!cambios.isEmpty()) {
            mostrarAviso(String.join(" ", cambios));
        }
    }

    private void prepararCampos() {
        FormatoCampo.texto(campoNombre.getCampo(), LARGO_CORTO);
        FormatoCampo.telefono(campoTelefono.getCampo());
        FormatoCampo.texto(campoCorreo.getCampo(), LARGO_LARGO);
        FormatoCampo.texto(campoCiudad.getCampo(), LARGO_CORTO);
        FormatoCampo.texto(campoDireccion.getCampo(), LARGO_LARGO);
        FormatoCampo.texto(campoIndicaciones.getCampo(), LARGO_LARGO);
        FormatoCampo.tarjeta(campoNumero.getCampo());
        FormatoCampo.texto(campoTitular.getCampo(), LARGO_CORTO);
        FormatoCampo.vence(campoVence.getCampo());
        FormatoCampo.soloDigitos(campoCvv.getCampo(), 4);
        FormatoCampo.soloDigitos(campoDocumento.getCampo(), 10);
        FormatoCampo.telefono(campoCelular.getCampo());
        campoBanco.usarLista(compra.bancos());
        precargarEnvio();
        CampoFormulario[] deEnvio = {campoNombre, campoTelefono, campoCorreo, campoCiudad, campoDireccion, campoIndicaciones};
        for (CampoFormulario campo : deEnvio) {
            campo.getCampo().textProperty().addListener((obs, antes, ahora) ->
                    SesionActual.getInstancia().setBorradorEnvio(datosEnvio()));
        }
        CampoFormulario[] todos = {campoNombre, campoTelefono, campoCorreo, campoCiudad, campoDireccion, campoIndicaciones,
            campoNumero, campoTitular, campoVence, campoCvv, campoDocumento, campoCelular};
        for (CampoFormulario campo : todos) {
            campo.getCampo().setOnAction(e -> confirmar());
            campo.getCampo().textProperty().addListener((obs, antes, ahora) -> mostrarMensaje(""));
        }
        campoBanco.getLista().valueProperty().addListener((obs, antes, ahora) -> mostrarMensaje(""));
    }

    private void precargarEnvio() {
        SesionActual sesion = SesionActual.getInstancia();
        Cliente cliente = sesion.getCliente();
        DatosEnvio datos = sesion.getBorradorEnvio();
        if (datos == null && cliente != null) {
            datos = DatosEnvio.de(cliente);
        }
        if (datos == null) {
            return;
        }
        campoNombre.setTexto(datos.getNombre());
        campoTelefono.setTexto(datos.getTelefono());
        campoCorreo.setTexto(datos.getCorreo());
        campoCiudad.setTexto(datos.getCiudad());
        campoDireccion.setTexto(datos.getDireccion());
        campoIndicaciones.setTexto(datos.getIndicaciones());
    }

    private void prepararOpciones() {
        MetodoPago[] metodos = MetodoPago.values();
        for (int i = 0; i < metodos.length; i++) {
            OpcionPago opcion = new OpcionPago(metodos[i]);
            opcion.setToggleGroup(grupo);
            opciones.add(opcion, i % 2, i / 2);
            if (i == 0) {
                opcion.setSelected(true);
            }
        }
        grupo.selectedToggleProperty().addListener((obs, antes, ahora) -> mostrarDatosDelMetodo());
        mostrarDatosDelMetodo();
    }

    private void mostrarDatosDelMetodo() {
        MetodoPago metodo = metodoElegido();
        alternar(datosTarjeta, metodo == MetodoPago.TARJETA);
        alternar(datosPse, metodo == MetodoPago.PSE);
        alternar(datosNequi, metodo == MetodoPago.NEQUI);
        alternar(datosContraEntrega, metodo == MetodoPago.CONTRA_ENTREGA);
        mostrarMensaje("");
    }

    public MetodoPago metodoElegido() {
        return grupo.getSelectedToggle() instanceof OpcionPago opcion ? opcion.getMetodo() : MetodoPago.TARJETA;
    }

    private void pintarResumen() {
        miniaturas.getChildren().clear();
        for (ItemCarrito item : carrito.lista()) {
            miniaturas.getChildren().add(new FilaResumen(item));
        }
        boolean vacio = carrito.lista().estaVacia();
        alternar(formularios, !vacio);
        alternar(tarjetaVacia, vacio);
        alternar(miniaturas, !vacio);
        botonConfirmar.setDisable(vacio);
        resumenController.pintar();
    }

    private DatosEnvio datosEnvio() {
        return new DatosEnvio(campoNombre.getTexto(), campoTelefono.getTexto(), campoCorreo.getTexto(),
                campoCiudad.getTexto(), campoDireccion.getTexto(), campoIndicaciones.getTexto());
    }

    private Pago pagoElegido() {
        return switch (metodoElegido()) {
            case TARJETA -> new PagoTarjeta(campoNumero.getTexto(), campoTitular.getTexto(),
                    campoVence.getTexto(), campoCvv.getTexto());
            case PSE -> new PagoPse(campoBanco.getTexto(), campoDocumento.getTexto());
            case NEQUI -> new PagoNequi(campoCelular.getTexto());
            case CONTRA_ENTREGA -> new PagoContraEntrega();
        };
    }

    private void confirmar() {
        if (botonConfirmar.isDisabled()) {
            return;
        }
        limpiarErrores();
        ResultadoCompra resultado = compra.confirmar(datosEnvio(), pagoElegido(), resumenController.getTotalMostrado(),
                checkGuardar.isSelected());
        BarraClienteController.refrescar();
        if (resultado.esExito()) {
            terminar(resultado);
            return;
        }
        if (resultado.getTipo() == ResultadoCompra.Tipo.DATOS_INVALIDOS) {
            marcarErrores(resultado);
        } else {
            pintarResumen();
        }
        mostrarMensaje(resultado.getMensaje());
    }

    private void terminar(ResultadoCompra resultado) {
        botonConfirmar.setDisable(true);
        campoNumero.setTexto("");
        campoCvv.setTexto("");
        boolean verHistorial = PopupCompraConfirmadaController.mostrar(resultado.getPedido());
        Navegador.reemplazar(verHistorial ? Ventana.HISTORIAL_COMPRAS : Ventana.CATALOGO);
    }

    private void marcarErrores(ResultadoCompra resultado) {
        CampoFormulario primero = null;
        for (ErrorCampo error : resultado.getErrores()) {
            CampoFormulario campo = campoDe((CampoCompra) error.getCampo());
            campo.mostrarError(error.getMensaje());
            if (primero == null) {
                primero = campo;
            }
        }
        if (primero != null) {
            primero.getControl().requestFocus();
            desplazarA(primero);
        }
    }

    private void limpiarErrores() {
        for (CampoCompra campo : CampoCompra.values()) {
            campoDe(campo).limpiarError();
        }
    }

    private CampoFormulario campoDe(CampoCompra campo) {
        return switch (campo) {
            case NOMBRE -> campoNombre;
            case TELEFONO -> campoTelefono;
            case CORREO -> campoCorreo;
            case CIUDAD -> campoCiudad;
            case DIRECCION -> campoDireccion;
            case NUMERO_TARJETA -> campoNumero;
            case TITULAR -> campoTitular;
            case VENCE -> campoVence;
            case CVV -> campoCvv;
            case BANCO -> campoBanco;
            case DOCUMENTO -> campoDocumento;
            case CELULAR_NEQUI -> campoCelular;
        };
    }

    private void desplazarA(Node nodo) {
        Node contenido = scroll.getContent();
        scroll.layout();
        Bounds posicion = contenido.sceneToLocal(nodo.localToScene(nodo.getBoundsInLocal()));
        double visible = scroll.getViewportBounds().getHeight();
        double recorrido = contenido.getBoundsInLocal().getHeight() - visible;
        if (posicion == null || recorrido <= 0) {
            return;
        }
        double actual = scroll.getVvalue() * recorrido;
        if (posicion.getMinY() < actual || posicion.getMaxY() > actual + visible) {
            scroll.setVvalue(Math.max(0, Math.min(1, (posicion.getMinY() - 40) / recorrido)));
        }
    }

    private void volverAlCarrito() {
        if (Navegador.destinoVolver() == Ventana.CARRITO) {
            Navegador.volver();
        } else {
            Navegador.mostrar(Ventana.CARRITO);
        }
    }

    private void mostrarMensaje(String texto) {
        mensaje.setText(texto);
        alternar(mensaje, !texto.isEmpty());
    }

    private void mostrarAviso(String texto) {
        textoAviso.setText(texto);
        alternar(aviso, true);
    }

    private void alternar(Node nodo, boolean visible) {
        nodo.setVisible(visible);
        nodo.setManaged(visible);
    }
}
