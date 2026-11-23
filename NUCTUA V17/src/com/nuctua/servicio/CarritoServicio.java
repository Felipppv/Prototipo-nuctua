package com.nuctua.servicio;

import com.nuctua.estructuras.ListaCarrito;
import com.nuctua.modelo.Cliente;
import com.nuctua.modelo.ItemCarrito;
import com.nuctua.modelo.SesionActual;
import com.nuctua.modelo.TallaStock;
import com.nuctua.modelo.Zapato;
import com.nuctua.persistencia.ArchivoCarrito;
import java.util.ArrayList;

public class CarritoServicio {

    private final ArchivoCarrito archivo = new ArchivoCarrito();
    private final SeleccionServicio seleccion = new SeleccionServicio();

    public ListaCarrito lista() {
        SesionActual sesion = SesionActual.getInstancia();
        if (sesion.getCliente() == null) {
            return new ListaCarrito();
        }
        if (sesion.getCarrito() == null) {
            sincronizar();
        }
        return sesion.getCarrito();
    }

    public ArrayList<String> sincronizar() {
        ArrayList<String> cambios = new ArrayList<>();
        SesionActual sesion = SesionActual.getInstancia();
        Cliente cliente = sesion.getCliente();
        if (cliente == null) {
            return cambios;
        }
        ListaCarrito items = new ListaCarrito();
        boolean modificado = false;
        CatalogoServicio catalogo = new CatalogoServicio();
        for (String[] registro : archivo.cargar(cliente.getCorreo())) {
            Zapato zapato = catalogo.buscarPorSku(registro[0]);
            if (zapato == null) {
                modificado = true;
                cambios.add("Un par ya no existe en el catálogo y salió de tu carrito.");
                continue;
            }
            items.agregar(zapato, Integer.parseInt(registro[1]), Integer.parseInt(registro[2]));
        }
        for (ItemCarrito item : items) {
            TallaStock talla = item.getZapato().getTallaStock(item.getTalla());
            int stock = talla == null ? 0 : talla.getStock();
            if (item.getCantidad() > Math.max(1, stock)) {
                item.setCantidad(Math.max(1, stock));
                modificado = true;
                if (stock > 0) {
                    cambios.add("Ajustamos " + item.getZapato().getNombre() + " talla " + item.getTalla()
                        + " al stock disponible.");
                }
            }
        }
        sesion.setCarrito(items);
        if (modificado) {
            guardar();
        }
        return cambios;
    }

    public int cantidadDe(String sku, int talla) {
        ItemCarrito item = lista().buscar(sku, talla);
        return item == null ? 0 : item.getCantidad();
    }

    public ResultadoSeleccion agregar(Zapato zapato, int talla, int cantidad) {
        if (SesionActual.getInstancia().getCliente() == null || zapato == null) {
            return ResultadoSeleccion.SIN_PRODUCTO;
        }
        if (cantidad < 1) {
            return ResultadoSeleccion.CANTIDAD_INVALIDA;
        }
        Zapato vigente = vigente(zapato.getSku());
        ResultadoSeleccion resultado = seleccion.validar(vigente, talla, cantidadDe(zapato.getSku(), talla) + cantidad);
        if (resultado.esValida()) {
            lista().agregar(vigente, talla, cantidad);
            guardar();
        }
        return resultado;
    }

    public ResultadoSeleccion cambiarCantidad(String sku, int talla, int cantidad) {
        if (lista().buscar(sku, talla) == null) {
            return ResultadoSeleccion.SIN_PRODUCTO;
        }
        ResultadoSeleccion resultado = seleccion.validar(vigente(sku), talla, cantidad);
        if (resultado.esValida()) {
            lista().cambiarCantidad(sku, talla, cantidad);
            guardar();
        }
        return resultado;
    }

    public ResultadoSeleccion cambiarTalla(String sku, int talla, int nuevaTalla) {
        ItemCarrito item = lista().buscar(sku, talla);
        if (item == null) {
            return ResultadoSeleccion.SIN_PRODUCTO;
        }
        if (talla == nuevaTalla) {
            return ResultadoSeleccion.VALIDA;
        }
        int total = item.getCantidad() + cantidadDe(sku, nuevaTalla);
        ResultadoSeleccion resultado = seleccion.validar(vigente(sku), nuevaTalla, total);
        if (resultado.esValida()) {
            lista().cambiarTalla(sku, talla, nuevaTalla);
            guardar();
        }
        return resultado;
    }

    public boolean quitar(String sku, int talla) {
        boolean quitado = lista().quitar(sku, talla);
        if (quitado) {
            guardar();
        }
        return quitado;
    }

    public boolean moverADeseos(String sku, int talla) {
        ItemCarrito item = lista().buscar(sku, talla);
        if (item == null) {
            return false;
        }
        new DeseosServicio().agregar(item.getZapato());
        return quitar(sku, talla);
    }

    public ResultadoSeleccion estadoDe(ItemCarrito item) {
        return item == null ? ResultadoSeleccion.SIN_PRODUCTO
                : seleccion.validar(item.getZapato(), item.getTalla(), item.getCantidad());
    }

    public int stockDe(ItemCarrito item) {
        return item == null ? 0 : item.getZapato().getStock(item.getTalla());
    }

    public boolean listoParaPagar() {
        ListaCarrito lista = lista();
        for (ItemCarrito item : lista) {
            if (!estadoDe(item).esValida()) {
                return false;
            }
        }
        return !lista.estaVacia();
    }

    public int cantidadTotal() {
        return lista().totalPares();
    }

    public int subtotal() {
        return lista().subtotal();
    }

    private Zapato vigente(String sku) {
        return new CatalogoServicio().buscarPorSku(sku);
    }

    private void guardar() {
        Cliente cliente = SesionActual.getInstancia().getCliente();
        if (cliente != null) {
            archivo.guardar(cliente.getCorreo(), lista());
        }
    }
}
