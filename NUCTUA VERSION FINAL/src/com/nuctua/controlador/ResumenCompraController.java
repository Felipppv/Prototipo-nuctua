package com.nuctua.controlador;

import com.nuctua.modelo.Cupon;
import com.nuctua.modelo.TipoCupon;
import com.nuctua.modelo.TotalesCompra;
import com.nuctua.modelo.Zapato;
import com.nuctua.servicio.CarritoServicio;
import com.nuctua.servicio.CuponServicio;
import com.nuctua.servicio.EnvioServicio;
import com.nuctua.servicio.ResultadoCupon;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.TextFormatter;
import javafx.scene.layout.HBox;

public class ResumenCompraController {

    private static final int LARGO_CODIGO = 20;

    @FXML
    private Label etiquetaSubtotal;
    @FXML
    private Label valorSubtotal;
    @FXML
    private Label valorEnvio;
    @FXML
    private Label notaEnvio;
    @FXML
    private Label valorDescuento;
    @FXML
    private HBox cajaCupon;
    @FXML
    private TextField campoCupon;
    @FXML
    private Button botonCupon;
    @FXML
    private Label mensajeCupon;
    @FXML
    private Label valorTotal;

    private final CarritoServicio carrito = new CarritoServicio();
    private final CuponServicio cupones = new CuponServicio();
    private final EnvioServicio envio = new EnvioServicio();
    private int totalMostrado;

    @FXML
    private void initialize() {
        campoCupon.setTextFormatter(new TextFormatter<>(cambio -> {
            String limpio = cambio.getText().toUpperCase().replaceAll("[^A-Z0-9_-]", "");
            int sobrantes = cambio.getText().length() - limpio.length();
            cambio.setText(limpio);
            if (sobrantes > 0) {
                cambio.setCaretPosition(cambio.getCaretPosition() - sobrantes);
                cambio.setAnchor(cambio.getAnchor() - sobrantes);
            }
            return cambio.getControlNewText().length() > LARGO_CODIGO ? null : cambio;
        }));
        campoCupon.textProperty().addListener((obs, antes, ahora) -> {
            if (!campoCupon.isDisabled()) {
                mostrarMensaje("", false);
            }
        });
        campoCupon.setOnAction(e -> usarCupon());
        botonCupon.setOnAction(e -> usarCupon());
        pintar();
    }

    public TotalesCompra pintar() {
        TotalesCompra totales = carrito.totales();
        Cupon cupon = cupones.aplicado();
        etiquetaSubtotal.setText("Subtotal (" + totales.getTextoPares() + ")");
        valorSubtotal.setText(Zapato.formatearPrecio(totales.getSubtotal()));
        valorEnvio.setText(totales.getTextoEnvio());
        valorDescuento.setText(totales.getTextoDescuento());
        totalMostrado = totales.getTotal();
        valorTotal.setText(Zapato.formatearPrecio(totalMostrado));
        boolean pagaEnvio = totales.getEnvio() > 0;
        notaEnvio.setText(pagaEnvio ? "Te faltan " + Zapato.formatearPrecio(envio.faltaParaGratis(totales.getSubtotal()))
                + " para envío gratis." : "");
        notaEnvio.setVisible(pagaEnvio);
        notaEnvio.setManaged(pagaEnvio);
        pintarCupon(cupon, totales);
        return totales;
    }

    public int getTotalMostrado() {
        return totalMostrado;
    }

    private void pintarCupon(Cupon cupon, TotalesCompra totales) {
        boolean estabaAplicado = campoCupon.isDisabled();
        boolean hayCupon = cupon != null;
        campoCupon.setDisable(hayCupon || totales.getPares() == 0);
        botonCupon.setDisable(!hayCupon && totales.getPares() == 0);
        botonCupon.setText(hayCupon ? "QUITAR" : "APLICAR");
        cajaCupon.getStyleClass().remove("caja-cupon-activa");
        if (!hayCupon) {
            if (estabaAplicado) {
                campoCupon.clear();
                mostrarMensaje("", false);
            }
            return;
        }
        cajaCupon.getStyleClass().add("caja-cupon-activa");
        campoCupon.setText(cupon.getCodigo());
        int subtotal = totales.getSubtotal();
        if (!cupon.alcanza(subtotal)) {
            mostrarMensaje("Te faltan " + Zapato.formatearPrecio(cupon.getMinimo() - subtotal)
                    + " para usar este código.", true);
        } else if (cupon.getTipo() == TipoCupon.ENVIO && envio.costo(subtotal) == 0) {
            mostrarMensaje("Tu envío ya es gratis sin este código.", false);
        } else {
            mostrarMensaje(cupon.getDescripcion() + " aplicado.", false);
        }
    }

    private void usarCupon() {
        if (cupones.aplicado() != null) {
            cupones.quitar();
            pintar();
            return;
        }
        String codigo = campoCupon.getText();
        ResultadoCupon resultado = cupones.aplicar(codigo, carrito.subtotal());
        if (resultado.esValido()) {
            pintar();
            return;
        }
        String texto = resultado.getMensaje();
        if (resultado == ResultadoCupon.MINIMO_NO_ALCANZADO) {
            texto = "Ese código pide una compra mínima de "
                    + Zapato.formatearPrecio(cupones.buscar(codigo).getMinimo()) + ".";
        }
        mostrarMensaje(texto, true);
    }

    private void mostrarMensaje(String texto, boolean error) {
        mensajeCupon.setText(texto);
        mensajeCupon.getStyleClass().setAll("label", error ? "mensaje-error" : "mensaje-ok");
        mensajeCupon.setVisible(!texto.isEmpty());
        mensajeCupon.setManaged(!texto.isEmpty());
        cajaCupon.getStyleClass().remove("caja-cupon-error");
        if (error && !texto.isEmpty()) {
            cajaCupon.getStyleClass().add("caja-cupon-error");
        }
    }
}
