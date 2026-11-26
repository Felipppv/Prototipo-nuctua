package com.nuctua.servicio;

import com.nuctua.algoritmos.MetodoOrdenamiento;
import com.nuctua.modelo.Zapato;
import java.util.Comparator;

public enum CriterioOrden {
    PRECIO_MENOR("Precio: menor a mayor", "Precio menor a mayor", MetodoOrdenamiento.QUICKSORT),
    PRECIO_MAYOR("Precio: mayor a menor", "Precio mayor a menor", MetodoOrdenamiento.INSERCION),
    NOMBRE_AZ("Nombre: A – Z", "Nombre A – Z", MetodoOrdenamiento.SELECCION),
    NOMBRE_ZA("Nombre: Z – A", "Nombre Z – A", MetodoOrdenamiento.BURBUJA),
    MAS_NUEVOS("Más nuevos primero", "Más nuevos", MetodoOrdenamiento.INTERCAMBIO);

    private final String etiqueta;
    private final String etiquetaCorta;
    private final MetodoOrdenamiento metodo;

    CriterioOrden(String etiqueta, String etiquetaCorta, MetodoOrdenamiento metodo) {
        this.etiqueta = etiqueta;
        this.etiquetaCorta = etiquetaCorta;
        this.metodo = metodo;
    }

    public Comparator<Zapato> getComparador() {
        Comparator<Zapato> porNombre = (a, b) -> CatalogoServicio.normalizar(a.getNombre())
                .compareTo(CatalogoServicio.normalizar(b.getNombre()));
        return switch (this) {
            case PRECIO_MENOR -> (a, b) -> Integer.compare(a.getPrecio(), b.getPrecio());
            case PRECIO_MAYOR -> (a, b) -> Integer.compare(b.getPrecio(), a.getPrecio());
            case NOMBRE_AZ -> porNombre;
            case NOMBRE_ZA -> porNombre.reversed();
            case MAS_NUEVOS -> (a, b) -> Integer.compare(b.getNumeroSku(), a.getNumeroSku());
        };
    }

    public String getEtiqueta() {
        return etiqueta;
    }

    public String getEtiquetaCorta() {
        return etiquetaCorta;
    }

    public MetodoOrdenamiento getMetodoOrdenamiento() {
        return metodo;
    }

    public String getMetodo() {
        return metodo.getNombre();
    }
}
