package com.nuctua.servicio;

import com.nuctua.algoritmos.Busqueda;
import com.nuctua.algoritmos.ResultadoBusqueda;
import com.nuctua.modelo.Pedido;
import com.nuctua.modelo.ResumenCliente;
import com.nuctua.modelo.Zapato;
import java.util.ArrayList;

public class ResultadoGlobal {

    private final ResultadoBusqueda<Zapato> productos;
    private final ResultadoBusqueda<Pedido> pedidos;
    private final ResultadoBusqueda<ResumenCliente> clientes;

    public ResultadoGlobal(ResultadoBusqueda<Zapato> productos, ResultadoBusqueda<Pedido> pedidos,
            ResultadoBusqueda<ResumenCliente> clientes) {
        this.productos = productos;
        this.pedidos = pedidos;
        this.clientes = clientes;
    }

    public ArrayList<Zapato> getProductos() {
        return productos.getEncontrados();
    }

    public ArrayList<Pedido> getPedidos() {
        return pedidos.getEncontrados();
    }

    public ArrayList<ResumenCliente> getClientes() {
        return clientes.getEncontrados();
    }

    public int getTotal() {
        return getProductos().size() + getPedidos().size() + getClientes().size();
    }

    public String getDescripcion() {
        int comparaciones = productos.getComparaciones() + pedidos.getComparaciones() + clientes.getComparaciones();
        boolean binaria = pedidos.usoMetodo(Busqueda.BINARIA) || productos.usoMetodo(Busqueda.BINARIA);
        return (binaria ? "Búsqueda binaria + lineal" : "Búsqueda lineal") + " · " + comparaciones
                + (comparaciones == 1 ? " comparación" : " comparaciones");
    }
}
