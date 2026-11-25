package com.nuctua.servicio;

import com.nuctua.estructuras.ListaEnlazada;
import com.nuctua.modelo.Cliente;
import com.nuctua.modelo.Cupon;
import com.nuctua.modelo.DatosEnvio;
import com.nuctua.modelo.ErrorCampo;
import com.nuctua.modelo.ItemCarrito;
import com.nuctua.modelo.LineaPedido;
import com.nuctua.modelo.Pago;
import com.nuctua.modelo.Pedido;
import com.nuctua.modelo.SesionActual;
import com.nuctua.modelo.TotalesCompra;
import com.nuctua.persistencia.ArchivoPedidos;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;

public class CompraServicio {

    private static final String[] BANCOS = {"Banco Agrario", "Banco de Bogotá", "Banco de Occidente", "Banco Popular",
        "Bancolombia", "BBVA Colombia", "Davivienda", "Scotiabank Colpatria"};

    private final ArchivoPedidos archivo = new ArchivoPedidos();
    private final CarritoServicio carrito = new CarritoServicio();
    private final CuponServicio cupones = new CuponServicio();
    private final EnvioServicio envios = new EnvioServicio();
    private final ClienteServicio clientes = new ClienteServicio();
    private final InventarioServicio inventario = new InventarioServicio();
    private final FacturaServicio facturas = new FacturaServicio();

    public String[] bancos() {
        return BANCOS.clone();
    }

    public ListaEnlazada<ErrorCampo> validar(DatosEnvio envio, Pago pago, LocalDate hoy) {
        ListaEnlazada<ErrorCampo> errores = envio.validar();
        for (ErrorCampo error : pago.validar(hoy)) {
            errores.insertarFinal(error);
        }
        return errores;
    }

    public ResultadoCompra confirmar(DatosEnvio envio, Pago pago, int totalMostrado, boolean guardarDatos) {
        Cliente cliente = SesionActual.getInstancia().getCliente();
        if (cliente == null || envio == null || pago == null) {
            return ResultadoCompra.error(ResultadoCompra.Tipo.SIN_SESION, "Inicia sesión para confirmar tu pedido.");
        }
        ArrayList<String> cambios = carrito.sincronizar();
        if (carrito.lista().estaVacia()) {
            return ResultadoCompra.error(ResultadoCompra.Tipo.CARRITO_VACIO, "Tu carrito está vacío.");
        }
        if (!cambios.isEmpty()) {
            return ResultadoCompra.error(ResultadoCompra.Tipo.CARRITO_CAMBIO,
                    String.join(" ", cambios) + " Revisa el resumen y confirma otra vez.");
        }
        if (!carrito.listoParaPagar()) {
            return ResultadoCompra.error(ResultadoCompra.Tipo.CARRITO_CAMBIO,
                    "Hay pares sin stock en tu carrito. Vuelve al carrito para quitarlos o cambiarlos.");
        }
        TotalesCompra totales = carrito.totales();
        if (totales.getTotal() != totalMostrado) {
            return ResultadoCompra.error(ResultadoCompra.Tipo.CARRITO_CAMBIO,
                    "El total de tu compra cambió. Revisa el resumen y confirma otra vez.");
        }
        LocalDateTime momento = LocalDateTime.now();
        ListaEnlazada<ErrorCampo> errores = validar(envio, pago, momento.toLocalDate());
        if (!errores.estaVacia()) {
            return ResultadoCompra.invalida(errores);
        }
        Pedido pedido;
        try {
            pedido = crear(cliente, envio, pago, totales, momento);
            LineaPedido sinStock = inventario.descontar(pedido.getLineas());
            if (sinStock != null) {
                return ResultadoCompra.error(ResultadoCompra.Tipo.CARRITO_CAMBIO, sinStock.getNombre() + " talla "
                        + sinStock.getTalla() + " ya no tiene pares suficientes. Vuelve al carrito para ajustarlo.");
            }
        } catch (IllegalStateException e) {
            return sinGuardar();
        }
        try {
            archivo.agregar(pedido);
        } catch (IllegalStateException e) {
            devolverStock(pedido);
            return sinGuardar();
        }
        facturas.emitir(pedido);
        carrito.vaciar();
        if (guardarDatos) {
            clientes.guardarDatosEnvio(envio);
        }
        SesionActual.getInstancia().setBorradorEnvio(null);
        return ResultadoCompra.exito(pedido);
    }

    private void devolverStock(Pedido pedido) {
        try {
            inventario.reponer(pedido.getLineas());
        } catch (IllegalStateException e) {
            System.err.println("No se pudo devolver el stock del pedido " + pedido.getCodigo());
        }
    }

    private ResultadoCompra sinGuardar() {
        return ResultadoCompra.error(ResultadoCompra.Tipo.SIN_GUARDAR, "No pudimos guardar tu pedido. Inténtalo de nuevo.");
    }

    private Pedido crear(Cliente cliente, DatosEnvio envio, Pago pago, TotalesCompra totales, LocalDateTime momento) {
        Pedido pedido = new Pedido(archivo.siguienteNumero(), momento, cliente.getCorreo(), envio, pago.getMetodo(),
                pago.getReferencia(), totales.getEnvio(), totales.getDescuento(), codigoUsado(totales));
        for (ItemCarrito item : carrito.lista()) {
            pedido.agregarLinea(LineaPedido.de(item));
        }
        return pedido;
    }

    private String codigoUsado(TotalesCompra totales) {
        Cupon cupon = cupones.aplicado();
        boolean ahorraEnvio = totales.getEnvio() < envios.costo(totales.getSubtotal());
        return cupon != null && (totales.getDescuento() > 0 || ahorraEnvio) ? cupon.getCodigo() : "";
    }
}
