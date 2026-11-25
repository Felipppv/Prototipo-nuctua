package com.nuctua.servicio;

public enum OrdenDeseos {
    MAS_RECIENTES("Más recientes", null),
    MAS_ANTIGUOS("Más antiguos", null),
    PRECIO_MENOR("Precio menor a mayor", CriterioOrden.PRECIO_MENOR),
    PRECIO_MAYOR("Precio mayor a menor", CriterioOrden.PRECIO_MAYOR);

    private final String etiqueta;
    private final CriterioOrden criterio;

    OrdenDeseos(String etiqueta, CriterioOrden criterio) {
        this.etiqueta = etiqueta;
        this.criterio = criterio;
    }

    public String getEtiqueta() {
        return etiqueta;
    }

    public CriterioOrden getCriterio() {
        return criterio;
    }

    public boolean esRecorrido() {
        return criterio == null;
    }

    public String getRecorrido() {
        return switch (this) {
            case MAS_RECIENTES -> "Lista doble · de la cabeza a la cola";
            case MAS_ANTIGUOS -> "Lista doble · de la cola a la cabeza";
            default -> "Ordenado con " + criterio.getMetodo();
        };
    }
}
