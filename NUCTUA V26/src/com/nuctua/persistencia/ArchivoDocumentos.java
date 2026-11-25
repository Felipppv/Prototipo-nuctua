package com.nuctua.persistencia;

import com.nuctua.modelo.Documento;
import java.io.File;

public class ArchivoDocumentos {

    public File ruta(Documento documento) {
        return new File(new File(GestorArchivos.getCarpetaDatos(), documento.getCarpeta()), documento.getNombreArchivo());
    }

    public void guardar(Documento documento, File destino) {
        GestorArchivos.escribirEn(destino, documento.aTexto());
    }
}
