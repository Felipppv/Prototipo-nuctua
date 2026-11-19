package com.nuctua;

import com.nuctua.controlador.Navegador;
import javafx.application.Application;
import javafx.stage.Stage;

public class Main extends Application {

    @Override
    public void start(Stage escenario) {
        Navegador.iniciar(escenario);
    }

    public static void main(String[] args) {
        launch(args);
    }
}
