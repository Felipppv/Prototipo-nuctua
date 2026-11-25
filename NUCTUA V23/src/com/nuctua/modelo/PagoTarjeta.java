package com.nuctua.modelo;

import com.nuctua.estructuras.ListaEnlazada;
import com.nuctua.util.Validaciones;
import java.time.LocalDate;

public class PagoTarjeta extends Pago {

    private final String numero;
    private final String titular;
    private final String vence;
    private final String cvv;

    public PagoTarjeta(String numero, String titular, String vence, String cvv) {
        super(MetodoPago.TARJETA);
        this.numero = Validaciones.digitos(numero);
        this.titular = Validaciones.limpiar(titular);
        this.vence = Validaciones.digitos(vence);
        this.cvv = Validaciones.digitos(cvv);
    }

    @Override
    public ListaEnlazada<ErrorCampo> validar(LocalDate hoy) {
        ListaEnlazada<ErrorCampo> errores = new ListaEnlazada<>();
        if (!Validaciones.esTarjeta(numero)) {
            errores.insertarFinal(new ErrorCampo(CampoCompra.NUMERO_TARJETA, numero.isEmpty()
                    ? "Escribe el número de la tarjeta." : "El número no es válido. Revisa los dígitos."));
        }
        if (!Validaciones.esNombreCompleto(titular)) {
            errores.insertarFinal(new ErrorCampo(CampoCompra.TITULAR, "Escribe nombre y apellido."));
        }
        if (!Validaciones.esMesValido(vence)) {
            errores.insertarFinal(new ErrorCampo(CampoCompra.VENCE, "Escribe mes y año."));
        } else if (!Validaciones.estaVigente(vence, hoy)) {
            errores.insertarFinal(new ErrorCampo(CampoCompra.VENCE, "Vencida o fecha errada."));
        }
        if (!Validaciones.esCvv(cvv, numero)) {
            errores.insertarFinal(new ErrorCampo(CampoCompra.CVV,
                    Validaciones.esAmex(numero) ? "Escribe 4 números." : "Escribe 3 números."));
        }
        return errores;
    }

    @Override
    public String getReferencia() {
        return numero.length() < 4 ? "" : numero.substring(numero.length() - 4);
    }
}
