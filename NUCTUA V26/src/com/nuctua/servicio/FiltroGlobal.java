package com.nuctua.servicio;

import java.time.LocalDate;

public class FiltroGlobal {

    public enum Estado {
        ACTIVO("Activo"),
        AGOTADO("Agotado"),
        NUEVO("Nuevo"),
        ENTREGADO("Entregado");

        private final String texto;

        Estado(String texto) {
            this.texto = texto;
        }

        public String getTexto() {
            return texto;
        }
    }

    public enum Fecha {
        HOY("Hoy", 0),
        SEMANA("Esta semana", 6),
        MES("Este mes", 29);

        private final String texto;
        private final int dias;

        Fecha(String texto, int dias) {
            this.texto = texto;
            this.dias = dias;
        }

        public String getTexto() {
            return texto;
        }

        public boolean incluye(LocalDate fecha, LocalDate hoy) {
            return fecha != null && !fecha.isAfter(hoy) && !fecha.isBefore(hoy.minusDays(dias));
        }
    }

    private String texto = "";
    private Estado estado;
    private Fecha fecha;
    private String categoria;

    public String getTexto() {
        return texto;
    }

    public void setTexto(String texto) {
        this.texto = texto == null ? "" : texto.trim();
    }

    public Estado getEstado() {
        return estado;
    }

    public void setEstado(Estado estado) {
        this.estado = estado;
    }

    public Fecha getFecha() {
        return fecha;
    }

    public void setFecha(Fecha fecha) {
        this.fecha = fecha;
    }

    public String getCategoria() {
        return categoria;
    }

    public void setCategoria(String categoria) {
        this.categoria = categoria;
    }

    public void limpiar() {
        estado = null;
        fecha = null;
        categoria = null;
    }
}
