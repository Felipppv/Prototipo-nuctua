package com.nuctua.controlador;

import com.nuctua.servicio.CriterioOrden;
import com.nuctua.util.Iconos;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.Label;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;

public class PopupOrdenarCatalogoController extends PopupBase {

    @FXML
    private VBox listaOpciones;
    @FXML
    private Button botonAplicar;

    private final ToggleGroup grupo = new ToggleGroup();
    private CriterioOrden elegido;

    @FXML
    private void initialize() {
        botonAplicar.setGraphic(Iconos.flechaDerecha(Iconos.CREMA, 18));
        botonAplicar.setContentDisplay(ContentDisplay.RIGHT);
        for (CriterioOrden criterio : CriterioOrden.values()) {
            ToggleButton opcion = new ToggleButton();
            opcion.setUserData(criterio);
            opcion.setToggleGroup(grupo);
            opcion.getStyleClass().setAll("opcion-fila");
            opcion.setMaxWidth(Double.MAX_VALUE);
            opcion.setAlignment(Pos.CENTER_LEFT);
            opcion.setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
            Label texto = new Label(criterio.getEtiqueta() + "  ·  " + criterio.getMetodo());
            texto.getStyleClass().add("texto");
            Region espacio = new Region();
            HBox.setHgrow(espacio, Priority.ALWAYS);
            StackPane marca = new StackPane(punto(false));
            HBox fila = new HBox(texto, espacio, marca);
            fila.setAlignment(Pos.CENTER_LEFT);
            fila.setPrefWidth(440);
            opcion.setGraphic(fila);
            opcion.selectedProperty().addListener((obs, antes, ahora) -> {
                marca.getChildren().setAll(punto(ahora));
                texto.setStyle(ahora ? "-fx-font-weight: bold;" : "");
            });
            listaOpciones.getChildren().add(opcion);
        }
        grupo.selectedToggleProperty().addListener((obs, antes, ahora) -> {
            if (ahora == null && antes != null) {
                grupo.selectToggle(antes);
            }
        });
    }

    public void setSeleccion(CriterioOrden actual) {
        grupo.getToggles().forEach(t -> t.setSelected(t.getUserData() == actual));
    }

    public CriterioOrden getElegido() {
        return elegido;
    }

    @FXML
    private void aplicar() {
        if (grupo.getSelectedToggle() != null) {
            elegido = (CriterioOrden) grupo.getSelectedToggle().getUserData();
        }
        cerrar();
    }

    private Circle punto(boolean activo) {
        Circle circulo = new Circle(10, activo ? Iconos.OSCURO : Color.web("#FFFBF3"));
        circulo.setStroke(Iconos.OSCURO);
        circulo.setStrokeWidth(2);
        return circulo;
    }
}
