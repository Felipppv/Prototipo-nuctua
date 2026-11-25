package com.nuctua.servicio;

import com.nuctua.algoritmos.Busqueda;
import com.nuctua.algoritmos.MetodoOrdenamiento;
import com.nuctua.algoritmos.ResultadoBusqueda;
import com.nuctua.estructuras.ListaEnlazada;
import com.nuctua.modelo.Zapato;
import com.nuctua.persistencia.ArchivoProductos;
import java.text.Normalizer;
import java.util.ArrayList;
import java.util.function.Predicate;

public class CatalogoServicio {

    private static final String PATRON_SKU = "(nct-?)?\\d{1,3}";

    private final ListaEnlazada<Zapato> catalogo;
    private ArrayList<Zapato> indicePorSku;
    private ArrayList<Zapato> indicePorPrecio;

    public CatalogoServicio() {
        catalogo = new ArchivoProductos().cargar();
        reconstruirIndices();
    }

    public final void reconstruirIndices() {
        indicePorSku = catalogo.aArrayList();
        MetodoOrdenamiento.SELECCION.ordenar(indicePorSku, (a, b) -> a.getSku().compareToIgnoreCase(b.getSku()));
        indicePorPrecio = catalogo.aArrayList();
        MetodoOrdenamiento.QUICKSORT.ordenar(indicePorPrecio, (a, b) -> Integer.compare(a.getPrecio(), b.getPrecio()));
    }

    public ListaEnlazada<Zapato> getCatalogo() {
        return catalogo;
    }

    public ArrayList<Zapato> activos() {
        return Busqueda.secuencial(catalogo.aArrayList(), Zapato::isActivo);
    }

    public ArrayList<Zapato> filtrar(FiltroCatalogo filtro) {
        return buscar(filtro).getEncontrados();
    }

    public ResultadoBusqueda<Zapato> buscar(FiltroCatalogo filtro) {
        ResultadoBusqueda<Zapato> resultado = new ResultadoBusqueda<>();
        if (filtro == null) {
            resultado.absorber(Busqueda.lineal(activos(), z -> true));
            return resultado;
        }
        String texto = normalizar(filtro.getTexto());
        ArrayList<Zapato> candidatos;
        boolean porSku = esSku(texto);
        if (porSku) {
            candidatos = new ArrayList<>();
            int[] comparaciones = new int[1];
            String clave = claveSku(texto);
            int posicion = Busqueda.binaria(indicePorSku, z -> z.getSku().compareToIgnoreCase(clave), comparaciones);
            if (posicion >= 0) {
                candidatos.add(indicePorSku.get(posicion));
            }
            resultado.agregarMetodo(Busqueda.BINARIA);
            resultado.contar(comparaciones[0]);
        } else if (filtro.getPrecioMinimo() > 0 || filtro.getPrecioMaximo() != Integer.MAX_VALUE) {
            candidatos = rangoDePrecio(filtro.getPrecioMinimo(), filtro.getPrecioMaximo(), resultado);
        } else {
            candidatos = catalogo.aArrayList();
        }
        Predicate<Zapato> condicion = z -> z.isActivo()
                && (porSku || coincideTexto(z, texto))
                && (filtro.getGrupoChip() == null || perteneceAGrupo(z, filtro.getGrupoChip()))
                && coincideMarcados(z, filtro.getGruposMarcados())
                && (filtro.getTalla() == 0 || z.getStock(filtro.getTalla()) > 0)
                && (filtro.getColor() == null || normalizar(z.getColores()).contains(normalizar(filtro.getColor())))
                && z.getPrecio() >= filtro.getPrecioMinimo()
                && z.getPrecio() <= filtro.getPrecioMaximo();
        resultado.absorber(Busqueda.lineal(candidatos, condicion));
        return resultado;
    }

    public ArrayList<Zapato> rangoDePrecio(int minimo, int maximo, ResultadoBusqueda<Zapato> registro) {
        int[] comparaciones = new int[1];
        int desde = Busqueda.limiteInferior(indicePorPrecio, z -> Integer.compare(z.getPrecio(), minimo), comparaciones);
        int hasta = Busqueda.limiteSuperior(indicePorPrecio, z -> Integer.compare(z.getPrecio(), maximo), comparaciones);
        ArrayList<Zapato> rango = new ArrayList<>();
        for (int i = desde; i < hasta; i++) {
            rango.add(indicePorPrecio.get(i));
        }
        if (registro != null) {
            registro.agregarMetodo(Busqueda.BINARIA);
            registro.contar(comparaciones[0]);
        }
        return rango;
    }

    public int ordenar(ArrayList<Zapato> lista, CriterioOrden criterio) {
        if (lista == null || criterio == null) {
            return 0;
        }
        return criterio.getMetodoOrdenamiento().ordenar(lista, criterio.getComparador());
    }

    public Zapato buscarPorSku(String sku) {
        if (sku == null) {
            return null;
        }
        int posicion = Busqueda.binaria(indicePorSku, sku.trim(), Zapato::getSku);
        return posicion < 0 ? null : indicePorSku.get(posicion);
    }

    public boolean hayStockEnTalla(int talla) {
        return Busqueda.secuencialPrimero(activos(), z -> z.getStock(talla) > 0) >= 0;
    }

    public int precioMinimo() {
        int posicion = Busqueda.secuencialPrimero(indicePorPrecio, Zapato::isActivo);
        return posicion < 0 ? 0 : indicePorPrecio.get(posicion).getPrecio();
    }

    public static boolean esSku(String texto) {
        return texto != null && normalizar(texto).matches(PATRON_SKU);
    }

    private static String claveSku(String texto) {
        String digitos = texto.replaceAll("\\D", "");
        int numero = Integer.parseInt(digitos);
        return String.format("NCT-%03d", numero);
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
