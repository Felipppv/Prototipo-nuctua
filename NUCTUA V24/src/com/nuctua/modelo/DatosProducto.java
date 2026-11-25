package com.nuctua.modelo;

import com.nuctua.util.Validaciones;
import java.util.ArrayList;

public class DatosProducto {

    private final String sku;
    private final String nombre;
    private final String marca;
    private final String categoria;
    private final int precio;
    private final String descripcion;
    private final boolean visible;
    private final ArrayList<String> colores;
    private final ArrayList<TallaStock> tallas;

    public DatosProducto(String sku, String nombre, String marca, String categoria, int precio, String descripcion,
            boolean visible, ArrayList<String> colores, ArrayList<TallaStock> tallas) {
        this.sku = Validaciones.limpiar(sku).toUpperCase();
        this.nombre = Validaciones.limpiar(nombre);
        this.marca = Validaciones.limpiar(marca);
        this.categoria = Validaciones.limpiar(categoria);
        this.precio = precio;
        this.descripcion = Validaciones.limpiar(descripcion);
        this.visible = visible;
        this.colores = colores;
        this.tallas = tallas;
    }

    public Zapato crear(String imagen) {
        Zapato zapato = new Zapato(sku, nombre, marca, categoria, getTextoColores(), precio, imagen, visible, descripcion);
        int primera = -1;
        int ultima = -1;
        for (int i = 0; i < tallas.size(); i++) {
            if (tallas.get(i).getStock() > 0) {
                primera = primera < 0 ? i : primera;
                ultima = i;
            }
        }
        for (int i = primera; primera >= 0 && i <= ultima; i++) {
            zapato.agregarTalla(tallas.get(i).getTalla(), tallas.get(i).getStock());
        }
        return zapato;
    }

    public int getPares() {
        int total = 0;
        for (TallaStock talla : tallas) {
            total += talla.getStock();
        }
        return total;
    }

    public String getTextoColores() {
        return String.join(" · ", colores);
    }

    public String getSku() {
        return sku;
    }

    public String getNombre() {
        return nombre;
    }

    public String getMarca() {
        return marca;
    }

    public String getCategoria() {
        return categoria;
    }

    public int getPrecio() {
        return precio;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public boolean isVisible() {
        return visible;
    }

    public ArrayList<String> getColores() {
        return colores;
    }

    public ArrayList<TallaStock> getTallas() {
        return tallas;
    }
}
