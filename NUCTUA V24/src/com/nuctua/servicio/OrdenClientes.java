package com.nuctua.servicio;

import com.nuctua.algoritmos.MetodoOrdenamiento;
import com.nuctua.modelo.ResumenCliente;
import java.time.LocalDate;
import java.util.Comparator;

public enum OrdenClientes {
    MAS_RECIENTES("Más recientes", MetodoOrdenamiento.INSERCION, (a, b) -> fecha(b).compareTo(fecha(a))),
    NOMBRE("Nombre A – Z", MetodoOrdenamiento.SELECCION, (a, b) -> CatalogoServicio.normalizar(a.getCliente().getNombre())
            .compareTo(CatalogoServicio.normalizar(b.getCliente().getNombre()))),
    MAS_GASTO("Mayor gasto", MetodoOrdenamiento.QUICKSORT, (a, b) -> Integer.compare(b.getGastado(), a.getGastado())),
    MAS_PEDIDOS("Más pedidos", MetodoOrdenamiento.BURBUJA, (a, b) -> Integer.compare(b.getCompras(), a.getCompras()));

    private final String texto;
    private final MetodoOrdenamiento metodo;
    private final Comparator<ResumenCliente> comparador;

    OrdenClientes(String texto, MetodoOrdenamiento metodo, Comparator<ResumenCliente> comparador) {
        this.texto = texto;
        this.metodo = metodo;
        this.comparador = comparador;
    }

    private static LocalDate fecha(ResumenCliente resumen) {
        LocalDate fecha = resumen.getUltimaCompra() == null ? resumen.getCliente().getRegistro() : resumen.getUltimaCompra();
        return fecha == null ? LocalDate.MIN : fecha;
    }

    public String getTexto() {
        return texto;
    }

    public MetodoOrdenamiento getMetodo() {
        return metodo;
    }

    public Comparator<ResumenCliente> getComparador() {
        return comparador;
    }
}
