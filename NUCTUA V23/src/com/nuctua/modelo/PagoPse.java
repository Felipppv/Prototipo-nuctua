package com.nuctua.modelo;

import com.nuctua.estructuras.ListaEnlazada;
import com.nuctua.util.Validaciones;
import java.time.LocalDate;

public class PagoPse extends Pago {

    private final String banco;
    private final String documento;

    public PagoPse(String banco, String documento) {
        super(MetodoPago.PSE);
        this.banco = Validaciones.limpiar(banco);
        this.documento = Validaciones.digitos(documento);
    }

    @Override
    public ListaEnlazada<ErrorCampo> validar(LocalDate hoy) {
        ListaEnlazada<ErrorCampo> errores = new ListaEnlazada<>();
        if (banco.isEmpty()) {
            errores.insertarFinal(new ErrorCampo(CampoCompra.BANCO, "Elige tu banco."));
        }
        if (!Validaciones.esDocumento(documento)) {
            errores.insertarFinal(new ErrorCampo(CampoCompra.DOCUMENTO, "Escribe tu documento, de 6 a 10 números."));
        }
        return errores;
    }

    @Override
    public String getReferencia() {
        return banco;
    }
}
