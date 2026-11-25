package com.nuctua.modelo;

import com.nuctua.estructuras.ListaEnlazada;
import java.time.LocalDate;

public class PagoContraEntrega extends Pago {

    public PagoContraEntrega() {
        super(MetodoPago.CONTRA_ENTREGA);
    }

    @Override
    public ListaEnlazada<ErrorCampo> validar(LocalDate hoy) {
        return new ListaEnlazada<>();
    }

    @Override
    public String getReferencia() {
        return "";
    }
}
