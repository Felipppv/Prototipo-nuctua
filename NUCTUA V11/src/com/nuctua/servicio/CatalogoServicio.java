package com.nuctua.servicio;

import com.nuctua.algoritmos.Busqueda;
import com.nuctua.algoritmos.Ordenamiento;
import com.nuctua.estructuras.ListaEnlazada;
import com.nuctua.modelo.Zapato;
import com.nuctua.persistencia.ArchivoProductos;
import java.text.Normalizer;
import java.util.ArrayList;

public class CatalogoServicio {

    private final ListaEnlazada<Zapato> catalogo;

    public CatalogoServicio() {
        catalogo = new ArchivoProductos().cargar();
    }

    public ListaEnlazada<Zapato> getCatalogo() {
        return catalogo;
    }

    public ArrayList<Zapato> activos() {
        return Busqueda.secuencial(catalogo.aArrayList(), Zapato::isActivo);
    }

    public ArrayList<Zapato> filtrar(FiltroCatalogo filtro) {
        String texto = normalizar(filtro.getTexto());
        return Busqueda.secuencial(activos(), z -> coincideTexto(z, texto)
                && (filtro.getGrupoChip() == null || perteneceAGrupo(z, filtro.getGrupoChip()))
                && coincideMarcados(z, filtro.getGruposMarcados())
                && (filtro.getTalla() == 0 || z.getStock(filtro.getTalla()) > 0)
                && (filtro.getColor() == null || normalizar(z.getColores()).contains(normalizar(filtro.getColor())))
                && z.getPrecio() >= filtro.getPrecioMinimo()
                && z.getPrecio() <= filtro.getPrecioMaximo());
    }

    public int ordenar(ArrayList<Zapato> lista, CriterioOrden criterio) {
        if (lista == null || criterio == null) {
            return 0;
        }
        return criterio.getMetodoOrdenamiento().ordenar(lista, criterio.getComparador());
    }

    public Zapato buscarPorSku(String sku) {
        ArrayList<Zapato> porSku = catalogo.aArrayList();
        Ordenamiento.seleccion(porSku, (a, b) -> a.getSku().compareToIgnoreCase(b.getSku()));
        int posicion = Busqueda.binaria(porSku, sku, Zapato::getSku);
        return posicion < 0 ? null : porSku.get(posicion);
    }

    public boolean hayStockEnTalla(int talla) {
        return Busqueda.secuencialPrimero(activos(), z -> z.getStock(talla) > 0) >= 0;
    }

    public int precioMinimo() {
        int menor = Integer.MAX_VALUE;
        for (Zapato zapato : activos()) {
            menor = Math.min(menor, zapato.getPrecio());
        }
        return menor == Integer.MAX_VALUE ? 0 : menor;
    }

    public static boolean perteneceAGrupo(Zapato zapato, String grupo) {
        String marca = zapato.getMarca().toUpperCase();
        return switch (grupo) {
            case FiltroCatalogo.NIKE -> marca.startsWith("NIKE");
            case FiltroCatalogo.YEEZY_OFF_WHITE -> marca.equals(FiltroCatalogo.YEEZY) || marca.equals(FiltroCatalogo.OFF_WHITE);
            default -> marca.equals(grupo);
        };
    }

    private boolean coincideMarcados(Zapato zapato, ArrayList<String> grupos) {
        if (grupos.isEmpty()) {
            return true;
        }
        for (String grupo : grupos) {
            if (perteneceAGrupo(zapato, grupo)) {
                return true;
            }
        }
        return false;
    }

    private boolean coincideTexto(Zapato zapato, String texto) {
        if (texto.isEmpty()) {
            return true;
        }
        String contenido = normalizar(zapato.getNombre() + " " + zapato.getMarca() + " " + zapato.getColores()
                + " " + zapato.getCategoria() + " " + zapato.getSku());
        for (String palabra : texto.split("\\s+")) {
            if (!contenido.contains(palabra)) {
                return false;
            }
        }
        return true;
    }

    public static String normalizar(String texto) {
        if (texto == null) {
            return "";
        }
        String sinTildes = Normalizer.normalize(texto, Normalizer.Form.NFD).replaceAll("\\p{M}", "");
        return sinTildes.toLowerCase().trim();
    }
}
