package com.nuctua.estructuras;

import com.nuctua.modelo.ItemCarrito;
import com.nuctua.modelo.Zapato;

public class ListaCarrito extends ListaEnlazada<ItemCarrito> {

    public ItemCarrito buscar(String sku, int talla) {
        return sku == null ? null : buscar(item -> item.esMismo(sku, talla));
    }

    public void agregar(Zapato zapato, int talla, int cantidad) {
        if (zapato == null || cantidad < 1) {
            return;
        }
        ItemCarrito existente = buscar(zapato.getSku(), talla);
        if (existente == null) {
            insertarFinal(new ItemCarrito(zapato, talla, cantidad));
        } else {
            existente.sumar(cantidad);
        }
    }

    public boolean quitar(String sku, int talla) {
        return sku != null && eliminar(item -> item.esMismo(sku, talla));
    }

    public boolean cambiarCantidad(String sku, int talla, int cantidad) {
        ItemCarrito item = buscar(sku, talla);
        if (item == null || cantidad < 1) {
            return false;
        }
        item.setCantidad(cantidad);
        return true;
    }

    public boolean cambiarTalla(String sku, int talla, int nuevaTalla) {
        ItemCarrito item = buscar(sku, talla);
        if (item == null) {
            return false;
        }
        if (talla == nuevaTalla) {
            return true;
        }
        ItemCarrito destino = buscar(sku, nuevaTalla);
        if (destino == null) {
            return reemplazar(i -> i == item, new ItemCarrito(item.getZapato(), nuevaTalla, item.getCantidad()));
        }
        destino.sumar(item.getCantidad());
        return eliminar(i -> i == item);
    }

    public int totalPares() {
        int total = 0;
        for (ItemCarrito item : this) {
            total += item.getCantidad();
        }
        return total;
    }

    public int subtotal() {
        int total = 0;
        for (ItemCarrito item : this) {
            total += item.getSubtotal();
        }
        return total;
    }
}
