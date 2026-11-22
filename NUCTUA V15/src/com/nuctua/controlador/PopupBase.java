package com.nuctua.controlador;

import javafx.fxml.FXML;
import javafx.stage.Stage;

public abstract class PopupBase {

    private Stage escenario;

    void setEscenario(Stage escenario) {
        this.escenario = escenario;
    }

    public void mostrarYEsperar() {
        escenario.showAndWait();
    }

    @FXML
    protected void cerrar() {
        if (escenario != null) {
            escenario.close();
        }
    }
}
