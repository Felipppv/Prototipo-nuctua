package com.nuctua.servicio;

import com.nuctua.algoritmos.Busqueda;
import com.nuctua.estructuras.ListaEnlazada;
import com.nuctua.modelo.CambioStock;
import com.nuctua.modelo.LineaPedido;
import com.nuctua.modelo.ResumenInventario;
import com.nuctua.modelo.SesionActual;
import com.nuctua.modelo.TallaStock;
import com.nuctua.modelo.Zapato;
import com.nuctua.persistencia.ArchivoImagenes;
import com.nuctua.persistencia.ArchivoProductos;
import java.util.ArrayList;

public class InventarioServicio {

    public static final int MAXIMO_POR_TALLA = 999;

    private final ArchivoProductos archivo = new ArchivoProductos();
    private final ActividadServicio actividad = new ActividadServicio();

    public LineaPedido descontar(Iterable<LineaPedido> lineas) {
        ListaEnlazada<Zapato> productos = archivo.cargar();
        for (LineaPedido linea : lineas) {
            Zapato zapato = buscar(productos, linea.getSku());
            TallaStock talla = zapato == null || !zapato.isActivo() ? null : zapato.getTallaStock(linea.getTalla());
            if (talla == null || !talla.descontar(linea.getCantidad())) {
                return linea;
            }
        }
        archivo.guardar(productos);
        return null;
    }

    public void reponer(Iterable<LineaPedido> lineas) {
        ListaEnlazada<Zapato> productos = archivo.cargar();
        for (LineaPedido linea : lineas) {
            Zapato zapato = buscar(productos, linea.getSku());
            if (zapato != null) {
                zapato.agregarTalla(linea.getTalla(), zapato.getStock(linea.getTalla()) + linea.getCantidad());
            }
        }
        archivo.guardar(productos);
    }

    public ResumenInventario resumen() {
        return new ResumenInventario(archivo.cargar());
    }

    public ArrayList<Zapato> buscar(String texto, FiltroInventario filtro) {
        String clave = CatalogoServicio.normalizar(texto);
        return Busqueda.secuencial(archivo.cargar().aArrayList(),
                zapato -> filtro.acepta(zapato) && contiene(zapato, clave));
    }

    private boolean contiene(Zapato zapato, String clave) {
        String datos = CatalogoServicio.normalizar(zapato.getNombre() + " " + zapato.getSku() + " " + zapato.getMarca()
                + " " + zapato.getColores());
        for (String palabra : clave.split(" +")) {
            if (!datos.contains(palabra)) {
                return false;
            }
        }
        return true;
    }

    public ResultadoInventario guardarCambios(Iterable<CambioStock> cambios) {
        return guardar(cambios, null, true);
    }

    public ResultadoInventario guardar(Iterable<CambioStock> cambios, String skuVisible, boolean visible) {
        if (!SesionActual.getInstancia().esAdministrador()) {
            return ResultadoInventario.error("Solo un administrador puede cambiar el inventario.");
        }
        try {
            ListaEnlazada<Zapato> productos = archivo.cargar();
            ArrayList<String> nombres = new ArrayList<>();
            for (CambioStock cambio : cambios) {
                Zapato zapato = buscar(productos, cambio.getSku());
                TallaStock talla = zapato == null ? null : zapato.getTallaStock(cambio.getTalla());
                if (talla == null) {
                    return ResultadoInventario.error("Un par o una talla ya no existe en el inventario.");
                }
                int nuevo = talla.getStock() + cambio.getDiferencia();
                if (nuevo < 0 || nuevo > MAXIMO_POR_TALLA) {
                    return ResultadoInventario.error(zapato.getNombre() + " talla " + cambio.getTalla() + " ahora tiene "
                            + talla.getStock() + (talla.getStock() == 1 ? " par" : " pares") + " y no admite ese cambio.");
                }
                talla.setStock(nuevo);
                if (cambio.getDiferencia() != 0 && !nombres.contains(zapato.getNombre())) {
                    nombres.add(zapato.getNombre());
                }
            }
            Zapato cambiado = skuVisible == null ? null : buscar(productos, skuVisible);
            boolean cambiaVisible = cambiado != null && cambiado.isActivo() != visible;
            if (skuVisible != null && cambiado == null) {
                return ResultadoInventario.error("Ese par ya no existe en el inventario.");
            }
            if (cambiaVisible) {
                cambiado.setActivo(visible);
            }
            archivo.guardar(productos);
            for (String nombre : nombres) {
                actividad.registrar("Actualizaste el stock de " + nombre);
            }
            if (cambiaVisible) {
                actividad.registrar((visible ? "Mostraste " : "Ocultaste ") + cambiado.getNombre() + " en la tienda");
            }
            return ResultadoInventario.exito("El nuevo stock quedó guardado en el archivo y ya se ve en el catálogo.");
        } catch (IllegalStateException e) {
            return ResultadoInventario.error("No pudimos guardar el inventario. Inténtalo de nuevo.");
        }
    }

    public ResultadoInventario eliminar(String sku) {
        if (!SesionActual.getInstancia().esAdministrador()) {
            return ResultadoInventario.error("Solo un administrador puede eliminar productos.");
        }
        try {
            ListaEnlazada<Zapato> productos = archivo.cargar();
            Zapato zapato = buscar(productos, sku);
            if (zapato == null) {
                return ResultadoInventario.error("Ese par ya no existe en el inventario.");
            }
            productos.eliminar(z -> z == zapato);
            archivo.guardar(productos);
            new ArchivoImagenes().borrarDe(zapato.getSku());
            actividad.registrar("Eliminaste el producto " + zapato.getNombre());
            return ResultadoInventario.exito("Eliminaste " + zapato.getNombre() + " del inventario.");
        } catch (IllegalStateException e) {
            return ResultadoInventario.error("No pudimos eliminar el producto. Inténtalo de nuevo.");
        }
    }

    private Zapato buscar(ListaEnlazada<Zapato> productos, String sku) {
        return productos.buscar(z -> z.getSku().equalsIgnoreCase(sku));
    }
}
