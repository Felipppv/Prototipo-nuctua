package com.nuctua.modelo;

import java.util.ArrayList;

public class Zapato {

    private String sku;
    private String nombre;
    private String marca;
    private String categoria;
    private String colores;
    private int precio;
    private String imagen;
    private boolean activo;
    private String descripcion;
    private final ArrayList<TallaStock> tallas;

    public Zapato(String sku, String nombre, String marca, String categoria, String colores,
            int precio, String imagen, boolean activo, String descripcion) {
        this.sku = sku;
        this.nombre = nombre;
        this.marca = marca;
        this.categoria = categoria;
        this.colores = colores;
        this.precio = precio;
        this.imagen = imagen;
        this.activo = activo;
        this.descripcion = descripcion;
        this.tallas = new ArrayList<>();
    }

    public static String formatearPrecio(int valor) {
        String digitos = String.valueOf(Math.abs(valor));
        StringBuilder texto = new StringBuilder();
        int contador = 0;
        for (int i = digitos.length() - 1; i >= 0; i--) {
            texto.insert(0, digitos.charAt(i));
            contador++;
            if (contador % 3 == 0 && i > 0) {
                texto.insert(0, '.');
            }
        }
        return (valor < 0 ? "-$" : "$") + texto;
    }

    public String getPrecioTexto() {
        return formatearPrecio(precio);
    }

    public void agregarTalla(int talla, int stock) {
        TallaStock existente = getTallaStock(talla);
        if (existente != null) {
            existente.setStock(stock);
        } else {
            int posicion = 0;
            while (posicion < tallas.size() && tallas.get(posicion).getTalla() < talla) {
                posicion++;
            }
            tallas.add(posicion, new TallaStock(talla, stock));
        }
    }

    public TallaStock getTallaStock(int talla) {
        for (TallaStock t : tallas) {
            if (t.getTalla() == talla) {
                return t;
            }
        }
        return null;
    }

    public int getStock(int talla) {
        TallaStock t = getTallaStock(talla);
        return t == null ? 0 : t.getStock();
    }

    public int getStockTotal() {
        int total = 0;
        for (TallaStock t : tallas) {
            total += t.getStock();
        }
        return total;
    }

    public boolean estaAgotado() {
        return getStockTotal() == 0;
    }

    public boolean tienePocoStock() {
        int total = getStockTotal();
        return total > 0 && total <= 5;
    }

    public String getRangoTallas() {
        if (tallas.isEmpty()) {
            return "Sin tallas";
        }
        return "Tallas " + tallas.get(0).getTalla() + "–" + tallas.get(tallas.size() - 1).getTalla();
    }

    public int getNumeroSku() {
        String digitos = sku.replaceAll("\\D", "");
        return digitos.isEmpty() ? 0 : Integer.parseInt(digitos);
    }

    public String getSku() {
        return sku;
    }

    public void setSku(String sku) {
        this.sku = sku;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getMarca() {
        return marca;
    }

    public void setMarca(String marca) {
        this.marca = marca;
    }

    public String getCategoria() {
        return categoria;
    }

    public void setCategoria(String categoria) {
        this.categoria = categoria;
    }

    public String getColores() {
        return colores;
    }

    public void setColores(String colores) {
        this.colores = colores;
    }

    public int getPrecio() {
        return precio;
    }

    public void setPrecio(int precio) {
        this.precio = precio;
    }

    public String getImagen() {
        return imagen;
    }

    public void setImagen(String imagen) {
        this.imagen = imagen;
    }

    public boolean isActivo() {
        return activo;
    }

    public void setActivo(boolean activo) {
        this.activo = activo;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public ArrayList<TallaStock> getTallas() {
        return tallas;
    }
}
