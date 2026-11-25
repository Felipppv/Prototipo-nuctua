package com.nuctua.controlador;

import com.nuctua.modelo.GuiaEnvio;
import com.nuctua.servicio.DocumentosServicio;
import com.nuctua.vista.Ventana;
import java.io.File;
import javafx.fxml.FXML;
import javafx.print.PrinterJob;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;

public class PopupGuiaController extends PopupBase {

    @FXML
    private Label numero;
    @FXML
    private TextArea contenido;
    @FXML
    private Label mensaje;

    private final DocumentosServicio servicio = new DocumentosServicio();
    private GuiaEnvio guia;

    public static void mostrar(GuiaEnvio guia) {
        PopupGuiaController popup = Popup.abrir(Ventana.POPUP_GUIA);
        popup.setGuia(guia);
        popup.mostrarYEsperar();
    }

    public void setGuia(GuiaEnvio nueva) {
        guia = nueva;
        numero.setText("N.º " + guia.getNumero());
        contenido.setText(String.join("\n", guia.aTexto()));
        contenido.setScrollTop(0);
        if (servicio.guardar(guia)) {
            avisar("Guardada en " + servicio.rutaCorta(guia), false);
        } else {
            avisar("No pudimos guardar el archivo. Usa Guardar copia.", true);
        }
    }

    @FXML
    private void imprimir() {
        PrinterJob trabajo = PrinterJob.createPrinterJob();
        if (trabajo == null) {
            avisar("No hay impresoras instaladas en este equipo.", true);
            return;
        }
        if (!trabajo.showPrintDialog(contenido.getScene().getWindow())) {
            trabajo.cancelJob();
            return;
        }
        Text hoja = new Text(contenido.getText());
        hoja.setFont(Font.font("Consolas", 10));
        boolean impresa = trabajo.printPage(hoja) && trabajo.endJob();
        avisar(impresa ? "Guía enviada a la impresora." : "No pudimos imprimir la guía.", !impresa);
    }

    @FXML
    private void guardarCopia() {
        FileChooser selector = new FileChooser();
        selector.setTitle("Guardar guía de envío");
        selector.setInitialFileName(guia.getNombreArchivo());
        selector.getExtensionFilters().add(new FileChooser.ExtensionFilter("Archivo de texto", "*.txt"));
        guardarEn(selector.showSaveDialog(contenido.getScene().getWindow()));
    }

    public void guardarEn(File destino) {
        if (destino == null) {
            return;
        }
        boolean guardada = servicio.exportar(guia, destino);
        avisar(guardada ? "Copia guardada como " + destino.getName() : "No pudimos guardar la copia en esa carpeta.", !guardada);
    }

    private void avisar(String texto, boolean error) {
        mensaje.setText(texto);
        mensaje.getStyleClass().setAll("label", error ? "mensaje-error" : "mensaje-ok");
    }
}
