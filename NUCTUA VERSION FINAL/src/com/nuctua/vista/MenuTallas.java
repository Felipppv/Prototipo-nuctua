package com.nuctua.vista;

import com.nuctua.modelo.TallaStock;
import com.nuctua.modelo.Zapato;
import com.nuctua.servicio.CarritoServicio;
import com.nuctua.servicio.SeleccionServicio;
import java.util.ArrayList;
import java.util.function.IntConsumer;
import javafx.geometry.Side;
import javafx.scene.Node;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;

public class MenuTallas extends ContextMenu {

    private final SeleccionServicio seleccion = new SeleccionServicio();

    public MenuTallas() {
        getStyleClass().add("menu-orden");
    }

    public ArrayList<MenuItem> opciones(Zapato zapato, IntConsumer alElegir) {
        ArrayList<MenuItem> opciones = new ArrayList<>();
        CarritoServicio carrito = new CarritoServicio();
        for (TallaStock talla : zapato.getTallas()) {
            if (talla.getStock() == 0) {
                continue;
            }
            int libres = talla.getStock() - carrito.cantidadDe(zapato.getSku(), talla.getTalla());
            MenuItem opcion = new MenuItem("Talla " + talla.getTalla() + " · "
                    + (libres > 0 ? seleccion.describirStock(libres) : "Ya están en tu carrito"));
            opcion.setDisable(libres <= 0);
            opcion.setOnAction(e -> alElegir.accept(talla.getTalla()));
            opciones.add(opcion);
        }
        return opciones;
    }

    public void mostrar(Node ancla, Zapato zapato, IntConsumer alElegir) {
        getItems().setAll(opciones(zapato, alElegir));
        show(ancla, Side.BOTTOM, 0, 6);
    }
}
