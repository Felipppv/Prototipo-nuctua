package com.nuctua.modelo;

import com.nuctua.estructuras.ListaEnlazada;
import com.nuctua.util.Validaciones;
import java.time.LocalDate;

public class PagoNequi extends Pago {

    private final String celular;

    public PagoNequi(String celular) {
        super(MetodoPago.NEQUI);
        this.celular = Validaciones.digitos(celular);
    }

    @Override
    public ListaEnlazada<ErrorCampo> validar(LocalDate hoy) {
        ListaEnlazada<ErrorCampo> errores = new ListaEnlazada<>();
        if (!Validaciones.esCelular(celular)) {
            errores.insertarFinal(new ErrorCampo(CampoCompra.CELULAR_NEQUI,
                    "Escribe tu celular Nequi: 10 números que empiezan por 3."));
        }
        return errores;
    }

    @Override
    public String getReferencia() {
        return celular.length() < 4 ? "" : celular.substring(celular.length() - 4);
    }
}
