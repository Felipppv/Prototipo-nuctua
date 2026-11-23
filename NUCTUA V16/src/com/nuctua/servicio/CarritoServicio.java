package com.nuctua.servicio;

import com.nuctua.estructuras.ListaEnlazada;
import com.nuctua.modelo.Cliente;
import com.nuctua.modelo.ItemCarrito;
import com.nuctua.modelo.SesionActual;
import com.nuctua.modelo.Zapato;
import com.nuctua.persistencia.ArchivoCarrito;

public class CarritoServicio {

    private final ArchivoCarrito archivo = new ArchivoCarrito();
    private final SeleccionServicio seleccion = new SeleccionServicio();

    public ListaEnlazada<ItemCarrito> lista() {
        SesionActual sesion = SesionActual.getInstancia();
        Cliente cliente = sesion.getCliente();
        if (cliente == null) {
            return new ListaEnlazada<>();
        }
        if (sesion.getCarrito() == null) {
            sesion.setCarrito(cargar(cliente.getCorreo()));
        }
        return sesion.getCarrito();
    }

    private ListaEnlazada<ItemCarrito> cargar(String correo) {
        ListaEnlazada<ItemCarrito> items = new ListaEnlazada<>();
        CatalogoServicio catalogo = new CatalogoServicio();
        for (String[] registro : archivo.cargar(correo)) {
            Zapato zapato = catalogo.buscarPorSku(registro[0]);
            int talla = Integer.parseInt(registro[1]);
            int cantidad = Integer.parseInt(registro[2]);
            if (zapato == null || cantidad < 1) {
                continue;
            }
            ItemCarrito existente = items.buscar(i -> i.esMismo(zapato.getSku(), talla));
            if (existente == null) {
                items.insertarFinal(new ItemCarrito(zapato, talla, cantidad));
            } else {
                existente.sumar(cantidad);
            }
        }
        return items;
    }

    public ItemCarrito buscar(String sku, int talla) {
        return sku == null ? null : lista().buscar(i -> i.esMismo(sku, talla));
    }

    public int cantidadDe(String sku, int talla) {
        ItemCarrito item = buscar(sku, talla);
        return item == null ? 0 : item.getCantidad();
    }

    public ResultadoSeleccion agregar(Zapato zapato, int talla, int cantidad) {
        Cliente cliente = SesionActual.getInstancia().getCliente();
        if (cliente == null || zapato == null) {
            return ResultadoSeleccion.SIN_PRODUCTO;
        }
        if (cantidad < 1) {
            return ResultadoSeleccion.CANTIDAD_INVALIDA;
        }
        Zapato vigente = new CatalogoServicio().buscarPorSku(zapato.getSku());
        ItemCarrito existente = buscar(zapato.getSku(), talla);
        int previa = existente == null ? 0 : existente.getCantidad();
        ResultadoSeleccion resultado = seleccion.validar(vigente, talla, previa + cantidad);
        if (!resultado.esValida()) {
            return resultado;
        }
        if (existente == null) {
            lista().insertarFinal(new ItemCarrito(vigente, talla, cantidad));
        } else {
            existente.sumar(cantidad);
        }
        archivo.guardar(cliente.getCorreo(), lista());
        return ResultadoSeleccion.VALIDA;
    }

    public int cantidadTotal() {
        int total = 0;
        for (ItemCarrito item : lista()) {
            total += item.getCantidad();
        }
        return total;
    }
}
