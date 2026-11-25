package com.nuctua.servicio;

import com.nuctua.modelo.Factura;
import com.nuctua.modelo.Pedido;
import com.nuctua.persistencia.ArchivoFacturas;
import java.io.File;

public class FacturaServicio {

    private final ArchivoFacturas archivo = new ArchivoFacturas();

    public Factura crear(Pedido pedido) {
        return pedido == null ? null : new Factura(pedido);
    }

    public boolean emitir(Pedido pedido) {
        Factura factura = crear(pedido);
        return factura != null && exportar(factura, archivo.ruta(factura));
    }

    public boolean asegurar(Factura factura) {
        return archivo.ruta(factura).isFile() || exportar(factura, archivo.ruta(factura));
    }

    public boolean exportar(Factura factura, File destino) {
        if (factura == null || destino == null) {
            return false;
        }
        try {
            archivo.guardar(factura, destino);
            return true;
        } catch (IllegalStateException e) {
            return false;
        }
    }

    public String rutaCorta(Factura factura) {
        File ruta = archivo.ruta(factura);
        File carpeta = ruta.getParentFile();
        return carpeta.getParentFile().getName() + "/" + carpeta.getName() + "/" + ruta.getName();
    }

    public String nombreSugerido(Factura factura) {
        return archivo.nombre(factura);
    }
}
