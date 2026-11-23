package com.nuctua.servicio;

import com.nuctua.estructuras.CursorDoble;
import com.nuctua.estructuras.ListaDobleDeseos;
import com.nuctua.modelo.Cliente;
import com.nuctua.modelo.SesionActual;
import com.nuctua.modelo.Zapato;
import com.nuctua.persistencia.ArchivoDeseos;
import java.util.ArrayList;

public class DeseosServicio {

    private final ArchivoDeseos archivo = new ArchivoDeseos();

    public ListaDobleDeseos lista() {
        SesionActual sesion = SesionActual.getInstancia();
        Cliente cliente = sesion.getCliente();
        if (cliente == null) {
            return new ListaDobleDeseos("");
        }
        ListaDobleDeseos actual = sesion.getDeseos();
        if (actual == null || !actual.getDueno().equalsIgnoreCase(cliente.getCorreo())) {
            sincronizar();
        }
        return sesion.getDeseos();
    }

    public ArrayList<String> sincronizar() {
        ArrayList<String> descartados = new ArrayList<>();
        SesionActual sesion = SesionActual.getInstancia();
        Cliente cliente = sesion.getCliente();
        if (cliente == null) {
            return descartados;
        }
        ListaDobleDeseos lista = new ListaDobleDeseos(cliente.getCorreo());
        CatalogoServicio catalogo = new CatalogoServicio();
        for (String sku : archivo.cargar(cliente.getCorreo())) {
            Zapato zapato = catalogo.buscarPorSku(sku);
            if (zapato == null) {
                descartados.add(sku);
            } else {
                lista.agregarAlFinal(zapato);
            }
        }
        sesion.setDeseos(lista);
        if (!descartados.isEmpty()) {
            guardar(lista);
        }
        return descartados;
    }

    public CursorDoble<Zapato> cursor() {
        return new CursorDoble<>(lista());
    }

    public boolean estaGuardado(Zapato zapato) {
        return zapato != null && lista().contiene(zapato.getSku());
    }

    public boolean alternar(Zapato zapato) {
        if (zapato == null || SesionActual.getInstancia().getCliente() == null) {
            return false;
        }
        ListaDobleDeseos lista = lista();
        boolean guardado = lista.alternar(zapato);
        guardar(lista);
        return guardado;
    }

    public int quitar(String sku) {
        ListaDobleDeseos lista = lista();
        int posicion = lista.posicionDe(sku);
        if (posicion >= 0 && lista.quitar(sku)) {
            guardar(lista);
        }
        return posicion;
    }

    public boolean restaurar(Zapato zapato, int posicion) {
        if (SesionActual.getInstancia().getCliente() == null) {
            return false;
        }
        ListaDobleDeseos lista = lista();
        boolean restaurado = lista.agregarEn(posicion, zapato);
        if (restaurado) {
            guardar(lista);
        }
        return restaurado;
    }

    public ResultadoSeleccion moverACarrito(String sku, int talla) {
        Zapato zapato = sku == null ? null : lista().buscar(z -> z.getSku().equalsIgnoreCase(sku));
        if (zapato == null) {
            return ResultadoSeleccion.SIN_PRODUCTO;
        }
        ResultadoSeleccion resultado = new CarritoServicio().agregar(zapato, talla, 1);
        if (resultado.esValida()) {
            quitar(sku);
        }
        return resultado;
    }

    public int cantidad() {
        return lista().tamanio();
    }

    public ArrayList<Zapato> ordenados(OrdenDeseos orden) {
        ListaDobleDeseos lista = lista();
        if (orden == null || orden == OrdenDeseos.MAS_RECIENTES) {
            return lista.masRecientes();
        }
        if (orden == OrdenDeseos.MAS_ANTIGUOS) {
            return lista.masAntiguos();
        }
        ArrayList<Zapato> copia = lista.masRecientes();
        new CatalogoServicio().ordenar(copia, orden.getCriterio());
        return copia;
    }

    private void guardar(ListaDobleDeseos lista) {
        archivo.guardar(lista.getDueno(), lista.skus());
    }
}
