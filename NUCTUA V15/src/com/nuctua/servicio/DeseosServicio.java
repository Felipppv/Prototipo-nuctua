package com.nuctua.servicio;

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
            actual = cargar(cliente.getCorreo());
            sesion.setDeseos(actual);
        }
        return actual;
    }

    private ListaDobleDeseos cargar(String correo) {
        ListaDobleDeseos lista = new ListaDobleDeseos(correo);
        CatalogoServicio catalogo = new CatalogoServicio();
        for (String sku : archivo.cargar(correo)) {
            Zapato zapato = catalogo.buscarPorSku(sku);
            if (zapato != null && zapato.isActivo()) {
                lista.agregarAlFinal(zapato);
            }
        }
        return lista;
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

    public boolean agregar(Zapato zapato) {
        ListaDobleDeseos lista = lista();
        boolean agregado = SesionActual.getInstancia().getCliente() != null && lista.agregar(zapato);
        if (agregado) {
            guardar(lista);
        }
        return agregado;
    }

    public boolean quitar(String sku) {
        ListaDobleDeseos lista = lista();
        boolean quitado = lista.quitar(sku);
        if (quitado) {
            guardar(lista);
        }
        return quitado;
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
