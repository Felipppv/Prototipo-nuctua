package com.nuctua.servicio;

import com.nuctua.algoritmos.Busqueda;
import com.nuctua.estructuras.ListaEnlazada;
import com.nuctua.modelo.CampoProducto;
import com.nuctua.modelo.DatosProducto;
import com.nuctua.modelo.ErrorCampo;
import com.nuctua.modelo.LineaPedido;
import com.nuctua.modelo.Pedido;
import com.nuctua.modelo.SesionActual;
import com.nuctua.modelo.Zapato;
import com.nuctua.persistencia.ArchivoImagenes;
import com.nuctua.persistencia.ArchivoPedidos;
import com.nuctua.persistencia.ArchivoProductos;
import com.nuctua.util.Validaciones;
import java.io.File;
import java.util.ArrayList;

public class ProductosServicio {

    public static final int PRECIO_MINIMO = 120000;
    public static final int PRECIO_MAXIMO = 250000;
    public static final int MAXIMO_COLORES = 3;
    public static final int TALLA_MINIMA = 37;
    public static final int TALLA_MAXIMA = 45;

    private static final String PATRON_SKU = "NCT-[0-9]{3}";
    private static final int ULTIMO_SKU = 999;

    private final ArchivoProductos archivo = new ArchivoProductos();
    private final ArchivoImagenes imagenes = new ArchivoImagenes();

    public String siguienteSku() {
        int mayor = 0;
        for (Zapato zapato : archivo.cargar()) {
            mayor = Math.max(mayor, zapato.getNumeroSku());
        }
        for (Pedido pedido : new ArchivoPedidos().cargar()) {
            for (LineaPedido linea : pedido.getLineas()) {
                String digitos = Validaciones.digitos(linea.getSku());
                if (digitos.length() == 3) {
                    mayor = Math.max(mayor, Integer.parseInt(digitos));
                }
            }
        }
        return mayor >= ULTIMO_SKU ? "" : String.format("NCT-%03d", mayor + 1);
    }

    public ArrayList<String> marcas() {
        ArrayList<String> marcas = new ArrayList<>();
        for (Zapato zapato : archivo.cargar()) {
            if (Busqueda.secuencialPrimero(marcas, m -> m.equalsIgnoreCase(zapato.getMarca())) < 0) {
                marcas.add(zapato.getMarca());
            }
        }
        return marcas;
    }

    public ArrayList<String> categorias() {
        return new BusquedaGlobalServicio().categorias();
    }

    public ListaEnlazada<ErrorCampo> validar(DatosProducto datos, boolean hayImagen) {
        ListaEnlazada<ErrorCampo> errores = new ListaEnlazada<>();
        ArrayList<Zapato> productos = archivo.cargar().aArrayList();
        if (datos.getNombre().length() < 4 || datos.getNombre().length() > 50) {
            errores.insertarFinal(new ErrorCampo(CampoProducto.NOMBRE, "Escribe el nombre del modelo, de 4 a 50 letras."));
        } else if (Busqueda.secuencialPrimero(productos, z -> CatalogoServicio.normalizar(z.getNombre())
                .equals(CatalogoServicio.normalizar(datos.getNombre()))) >= 0) {
            errores.insertarFinal(new ErrorCampo(CampoProducto.NOMBRE, "Ya existe un par con ese nombre."));
        }
        validarSku(datos.getSku(), productos, errores);
        if (datos.getMarca().isEmpty()) {
            errores.insertarFinal(new ErrorCampo(CampoProducto.MARCA, "Elige o escribe la marca."));
        }
        if (datos.getPrecio() < PRECIO_MINIMO || datos.getPrecio() > PRECIO_MAXIMO) {
            errores.insertarFinal(new ErrorCampo(CampoProducto.PRECIO, "El precio va de " + Zapato.formatearPrecio(PRECIO_MINIMO)
                    + " a " + Zapato.formatearPrecio(PRECIO_MAXIMO) + "."));
        }
        if (datos.getDescripcion().length() < 20 || datos.getDescripcion().length() > 300) {
            errores.insertarFinal(new ErrorCampo(CampoProducto.DESCRIPCION, "Describe el par con 20 a 300 letras."));
        }
        if (datos.getColores().isEmpty() || datos.getColores().size() > MAXIMO_COLORES) {
            errores.insertarFinal(new ErrorCampo(CampoProducto.COLORES, "Elige de 1 a " + MAXIMO_COLORES + " colores."));
        }
        if (datos.getPares() == 0) {
            errores.insertarFinal(new ErrorCampo(CampoProducto.TALLAS, "Agrega al menos un par en alguna talla."));
        }
        if (!hayImagen) {
            errores.insertarFinal(new ErrorCampo(CampoProducto.IMAGEN, "Elige la foto del par."));
        }
        return errores;
    }

    private void validarSku(String sku, ArrayList<Zapato> productos, ListaEnlazada<ErrorCampo> errores) {
        if (!sku.matches(PATRON_SKU)) {
            errores.insertarFinal(new ErrorCampo(CampoProducto.SKU, "El SKU se escribe como NCT-013."));
        } else if (Busqueda.secuencialPrimero(productos, z -> z.getSku().equalsIgnoreCase(sku)) >= 0) {
            errores.insertarFinal(new ErrorCampo(CampoProducto.SKU, "Ese SKU ya existe."));
        } else if (vendidoAntes(sku)) {
            errores.insertarFinal(new ErrorCampo(CampoProducto.SKU, "Ese SKU ya está en pedidos anteriores. Usa otro."));
        }
    }

    private boolean vendidoAntes(String sku) {
        for (Pedido pedido : new ArchivoPedidos().cargar()) {
            if (pedido.getLineas().contiene(l -> l.getSku().equalsIgnoreCase(sku))) {
                return true;
            }
        }
        return false;
    }

    public ResultadoProducto registrar(DatosProducto datos, ListaEnlazada<File> fotos) {
        if (!SesionActual.getInstancia().esAdministrador()) {
            return ResultadoProducto.error("Solo un administrador puede registrar productos.");
        }
        if (!validar(datos, !fotos.estaVacia()).estaVacia()) {
            return ResultadoProducto.error("Revisa los campos marcados.");
        }
        try {
            imagenes.borrarDe(datos.getSku());
            String principal = null;
            int numero = 1;
            for (File foto : fotos) {
                String ruta = imagenes.copiar(foto, datos.getSku(), numero);
                principal = principal == null ? ruta : principal;
                numero++;
            }
            Zapato zapato = datos.crear(principal);
            ListaEnlazada<Zapato> productos = archivo.cargar();
            productos.insertarFinal(zapato);
            archivo.guardar(productos);
            new ActividadServicio().registrar("Creaste el producto " + zapato.getNombre());
            return ResultadoProducto.exito(zapato, zapato.isActivo()
                    ? "El par quedó escrito en el archivo de productos y ya aparece en el catálogo."
                    : "El par quedó escrito en el archivo de productos. Está oculto en el catálogo.");
        } catch (IllegalStateException e) {
            imagenes.borrarDe(datos.getSku());
            return ResultadoProducto.error("No pudimos guardar el producto. Inténtalo de nuevo.");
        }
    }
}
