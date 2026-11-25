package com.nuctua.servicio;

import com.nuctua.algoritmos.Busqueda;
import com.nuctua.algoritmos.MetodoOrdenamiento;
import com.nuctua.algoritmos.ResultadoBusqueda;
import com.nuctua.modelo.EstadoCliente;
import com.nuctua.modelo.EstadoPedido;
import com.nuctua.modelo.LineaPedido;
import com.nuctua.modelo.Pedido;
import com.nuctua.modelo.ResumenCliente;
import com.nuctua.modelo.Zapato;
import com.nuctua.util.Validaciones;
import java.time.LocalDate;
import java.util.ArrayList;

public class BusquedaGlobalServicio {

    private static final String PATRON_PEDIDO = "#?(nct-?)?[0-9]{4,9}";

    private final CatalogoServicio catalogo = new CatalogoServicio();
    private final ArrayList<Pedido> pedidos = new PedidosServicio().todos().aArrayList();
    private final ArrayList<ResumenCliente> clientes = new ClientesServicio().todos();

    public BusquedaGlobalServicio() {
        MetodoOrdenamiento.INSERCION.ordenar(pedidos, (a, b) -> Integer.compare(a.getNumero(), b.getNumero()));
    }

    public ArrayList<String> categorias() {
        ArrayList<String> categorias = new ArrayList<>();
        for (Zapato zapato : catalogo.getCatalogo()) {
            if (!zapato.getCategoria().isBlank() && !categorias.contains(zapato.getCategoria())) {
                categorias.add(zapato.getCategoria());
            }
        }
        MetodoOrdenamiento.SELECCION.ordenar(categorias, (a, b) -> b.compareToIgnoreCase(a));
        return categorias;
    }

    public ResultadoGlobal buscar(FiltroGlobal filtro, LocalDate hoy) {
        String clave = CatalogoServicio.normalizar(filtro.getTexto());
        return new ResultadoGlobal(buscarProductos(filtro, clave), buscarPedidos(filtro, clave, hoy), buscarClientes(filtro, clave, hoy));
    }

    private ResultadoBusqueda<Zapato> buscarProductos(FiltroGlobal filtro, String clave) {
        ArrayList<Zapato> candidatos = catalogo.getCatalogo().aArrayList();
        boolean porSku = CatalogoServicio.esSku(clave);
        if (porSku) {
            candidatos.clear();
            Zapato exacto = catalogo.buscarPorSku(String.format("NCT-%03d", Integer.parseInt(Validaciones.digitos(clave))));
            if (exacto != null) {
                candidatos.add(exacto);
            }
        }
        ResultadoBusqueda<Zapato> resultado = Busqueda.lineal(candidatos, z -> (porSku || contiene(textoDe(z), clave)) && aceptaEstado(z, filtro.getEstado())
                && (filtro.getCategoria() == null || filtro.getCategoria().equalsIgnoreCase(z.getCategoria())));
        if (porSku) {
            resultado.agregarMetodo(Busqueda.BINARIA);
        }
        return resultado;
    }

    private ResultadoBusqueda<Pedido> buscarPedidos(FiltroGlobal filtro, String clave, LocalDate hoy) {
        ResultadoBusqueda<Pedido> resultado = new ResultadoBusqueda<>();
        ArrayList<Pedido> candidatos = pedidos;
        boolean porNumero = clave.matches(PATRON_PEDIDO);
        if (porNumero) {
            int numero = Integer.parseInt(Validaciones.digitos(clave));
            int[] comparaciones = new int[1];
            int posicion = Busqueda.binaria(pedidos, p -> Integer.compare(p.getNumero(), numero), comparaciones);
            resultado.agregarMetodo(Busqueda.BINARIA);
            resultado.contar(comparaciones[0]);
            candidatos = new ArrayList<>();
            if (posicion >= 0) {
                candidatos.add(pedidos.get(posicion));
            }
        }
        resultado.absorber(Busqueda.lineal(candidatos, p -> (porNumero || contiene(textoDe(p), clave))
                && aceptaEstado(p, filtro.getEstado())
                && (filtro.getFecha() == null || filtro.getFecha().incluye(p.getFecha().toLocalDate(), hoy))));
        MetodoOrdenamiento.QUICKSORT.ordenar(resultado.getEncontrados(), (a, b) -> Integer.compare(b.getNumero(), a.getNumero()));
        return resultado;
    }

    private ResultadoBusqueda<ResumenCliente> buscarClientes(FiltroGlobal filtro, String clave, LocalDate hoy) {
        return Busqueda.lineal(clientes, c -> ClientesServicio.coincide(c, clave) && aceptaEstado(c, filtro.getEstado())
                && (filtro.getFecha() == null || filtro.getFecha().incluye(c.getUltimaCompra() == null
                        ? c.getCliente().getRegistro() : c.getUltimaCompra(), hoy)));
    }

    private boolean contiene(String datos, String clave) {
        for (String palabra : clave.split(" +")) {
            if (!datos.contains(palabra)) {
                return false;
            }
        }
        return true;
    }

    private String textoDe(Zapato zapato) {
        String estado = zapato.estaAgotado() ? " agotado" : (zapato.tienePocoStock() ? " poco stock bajo" : "");
        return CatalogoServicio.normalizar(zapato.getNombre() + " " + zapato.getSku() + " " + zapato.getMarca() + " "
                + zapato.getColores() + " " + zapato.getCategoria() + estado);
    }

    private String textoDe(Pedido pedido) {
        StringBuilder texto = new StringBuilder("#" + pedido.getCodigo() + " " + pedido.getEnvio().getNombre() + " "
                + pedido.getCorreoCliente() + " " + pedido.getEnvio().getCiudad() + " " + pedido.getEstado().getTexto());
        for (LineaPedido linea : pedido.getLineas()) {
            texto.append(' ').append(linea.getNombre()).append(' ').append(linea.getSku());
        }
        return CatalogoServicio.normalizar(texto.toString());
    }

    private boolean aceptaEstado(Zapato zapato, FiltroGlobal.Estado estado) {
        if (estado == null) {
            return true;
        }
        return switch (estado) {
            case ACTIVO -> zapato.isActivo() && !zapato.estaAgotado();
            case AGOTADO -> zapato.estaAgotado();
            default -> false;
        };
    }

    private boolean aceptaEstado(Pedido pedido, FiltroGlobal.Estado estado) {
        if (estado == null) {
            return true;
        }
        return switch (estado) {
            case NUEVO -> pedido.getEstado() == EstadoPedido.NUEVO;
            case ENTREGADO -> pedido.getEstado() == EstadoPedido.ENTREGADO;
            default -> false;
        };
    }

    private boolean aceptaEstado(ResumenCliente cliente, FiltroGlobal.Estado estado) {
        return estado == null || (estado == FiltroGlobal.Estado.NUEVO && cliente.getEstado() == EstadoCliente.NUEVO);
    }
}
