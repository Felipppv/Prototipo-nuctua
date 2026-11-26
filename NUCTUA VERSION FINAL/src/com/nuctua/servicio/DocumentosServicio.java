package com.nuctua.servicio;

import com.nuctua.modelo.Documento;
import com.nuctua.persistencia.ArchivoDocumentos;
import java.io.File;

public class DocumentosServicio {

    private final ArchivoDocumentos archivo = new ArchivoDocumentos();

    public boolean guardar(Documento documento) {
        return documento != null && exportar(documento, archivo.ruta(documento));
    }

    public boolean asegurar(Documento documento) {
        return archivo.ruta(documento).isFile() || guardar(documento);
    }

    public boolean exportar(Documento documento, File destino) {
        if (documento == null || destino == null) {
            return false;
        }
        try {
            archivo.guardar(documento, destino);
            return true;
        } catch (IllegalStateException e) {
            return false;
        }
    }

    public String rutaCorta(Documento documento) {
        File ruta = archivo.ruta(documento);
        File carpeta = ruta.getParentFile();
        return carpeta.getParentFile().getName() + "/" + carpeta.getName() + "/" + ruta.getName();
    }
}
