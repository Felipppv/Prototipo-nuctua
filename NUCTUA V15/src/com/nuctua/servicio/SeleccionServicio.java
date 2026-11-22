package com.nuctua.servicio;

import com.nuctua.modelo.TallaStock;
import com.nuctua.modelo.Zapato;
import java.util.ArrayList;

public class SeleccionServicio {

    public ResultadoSeleccion validar(Zapato zapato, int talla, int cantidad) {
        if (zapato == null) {
            return ResultadoSeleccion.SIN_PRODUCTO;
        }
        if (!zapato.isActivo()) {
            return ResultadoSeleccion.PRODUCTO_INACTIVO;
        }
        if (zapato.estaAgotado()) {
            return ResultadoSeleccion.PRODUCTO_AGOTADO;
        }
        if (talla <= 0) {
            return ResultadoSeleccion.SIN_TALLA;
        }
        TallaStock elegida = zapato.getTallaStock(talla);
        if (elegida == null) {
            return ResultadoSeleccion.TALLA_INEXISTENTE;
        }
        if (elegida.getStock() == 0) {
            return ResultadoSeleccion.TALLA_AGOTADA;
        }
        if (cantidad < 1) {
            return ResultadoSeleccion.CANTIDAD_INVALIDA;
        }
        if (cantidad > elegida.getStock()) {
            return ResultadoSeleccion.STOCK_INSUFICIENTE;
        }
        return ResultadoSeleccion.VALIDA;
    }

    public ArrayList<Integer> tallasAgotadas(Zapato zapato) {
        ArrayList<Integer> agotadas = new ArrayList<>();
        if (zapato == null) {
            return agotadas;
        }
        for (TallaStock talla : zapato.getTallas()) {
            if (talla.getStock() == 0) {
                agotadas.add(talla.getTalla());
            }
        }
        return agotadas;
    }

    public String describirAgotadas(Zapato zapato) {
        ArrayList<Integer> agotadas = tallasAgotadas(zapato);
        if (agotadas.isEmpty()) {
            return "Todas las tallas tienen stock.";
        }
        if (agotadas.size() == 1) {
            return "La talla " + agotadas.get(0) + " está agotada.";
        }
        StringBuilder texto = new StringBuilder("Las tallas ");
        for (int i = 0; i < agotadas.size(); i++) {
            if (i > 0) {
                texto.append(i == agotadas.size() - 1 ? " y " : ", ");
            }
            texto.append(agotadas.get(i));
        }
        return texto.append(" están agotadas.").toString();
    }

    public String describirStock(int stock) {
        if (stock <= 0) {
            return "Agotada";
        }
        return stock == 1 ? "Queda 1 par" : "Quedan " + stock + " pares";
    }

    public static String describirCategoria(Zapato zapato) {
        String categoria = zapato == null ? "" : zapato.getCategoria();
        if ("Alto".equalsIgnoreCase(categoria)) {
            return "CAÑA ALTA";
        }
        if ("Bajo".equalsIgnoreCase(categoria)) {
            return "CAÑA BAJA";
        }
        return categoria == null ? "" : categoria.toUpperCase();
    }
}
