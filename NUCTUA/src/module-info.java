module com.nuctua {
    requires javafx.controls;
    requires javafx.fxml;

    opens com.nuctua.controlador to javafx.fxml;

    exports com.nuctua;
    exports com.nuctua.controlador;
}
