package com.nuctua.modelo;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class Actividad {

    private final LocalDateTime fecha;
    private final String texto;

    public Actividad(LocalDateTime fecha, String texto) {
        this.fecha = fecha;
        this.texto = texto;
    }

    public LocalDateTime getFecha() {
        return fecha;
    }

    public String getTexto() {
        return texto;
    }

    public String getCuando(LocalDate hoy) {
        return describirDia(fecha, hoy) + " · " + describirHora(fecha);
    }

    public static String describirDia(LocalDateTime momento, LocalDate hoy) {
        LocalDate dia = momento.toLocalDate();
        if (dia.equals(hoy)) {
            return "Hoy";
        }
        return dia.equals(hoy.minusDays(1)) ? "Ayer" : Pedido.textoFecha(momento);
    }

    public static String describirHora(LocalDateTime momento) {
        int hora = momento.getHour() % 12 == 0 ? 12 : momento.getHour() % 12;
        return hora + String.format(":%02d ", momento.getMinute()) + (momento.getHour() < 12 ? "a. m." : "p. m.");
    }
}
