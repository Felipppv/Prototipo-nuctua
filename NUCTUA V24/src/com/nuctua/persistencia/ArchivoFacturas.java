package com.nuctua.persistencia;

import com.nuctua.modelo.Factura;
import java.io.File;

public class ArchivoFacturas {

    private static final String CARPETA = "facturas";

    public File ruta(Factura factura) {
        return new File(new File(GestorArchivos.getCarpetaDatos(), CARPETA), nombre(factura));
    }

    public String nombre(Factura factura) {
        return "factura_" + factura.getPedido().getCodigo() + ".txt";
    }

    public void guardar(Factura factura, File destino) {
        GestorArchivos.escribirEn(destino, factura.aTexto());
    }
}
