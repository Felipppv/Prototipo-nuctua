package com.nuctua.servicio;

import com.nuctua.modelo.Cliente;
import com.nuctua.modelo.Cupon;
import com.nuctua.modelo.SesionActual;
import com.nuctua.modelo.TipoCupon;
import com.nuctua.persistencia.ArchivoCupones;

public class CuponServicio {

    private final ArchivoCupones archivo = new ArchivoCupones();
    private final EnvioServicio envio = new EnvioServicio();

    public Cupon buscar(String codigo) {
        if (codigo == null || codigo.isBlank()) {
            return null;
        }
        String clave = codigo.trim();
        return archivo.cargar().buscar(c -> c.getCodigo().equalsIgnoreCase(clave));
    }

    public Cupon aplicado() {
        Cliente cliente = SesionActual.getInstancia().getCliente();
        if (cliente == null) {
            return null;
        }
        Cupon cupon = buscar(archivo.codigoAplicado(cliente.getCorreo()));
        return cupon != null && cupon.isActivo() ? cupon : null;
    }

    public boolean descartarSiNoVale() {
        Cliente cliente = SesionActual.getInstancia().getCliente();
        if (cliente == null || archivo.codigoAplicado(cliente.getCorreo()).isEmpty() || aplicado() != null) {
            return false;
        }
        quitar();
        return true;
    }

    public ResultadoCupon aplicar(String codigo, int subtotal) {
        Cliente cliente = SesionActual.getInstancia().getCliente();
        if (cliente == null || subtotal <= 0) {
            return ResultadoCupon.CARRITO_VACIO;
        }
        if (codigo == null || codigo.isBlank()) {
            return ResultadoCupon.VACIO;
        }
        Cupon cupon = buscar(codigo);
        if (cupon == null) {
            return ResultadoCupon.INEXISTENTE;
        }
        if (!cupon.isActivo()) {
            return ResultadoCupon.INACTIVO;
        }
        if (!cupon.alcanza(subtotal)) {
            return ResultadoCupon.MINIMO_NO_ALCANZADO;
        }
        if (cupon.getTipo() == TipoCupon.ENVIO && envio.costo(subtotal) == 0) {
            return ResultadoCupon.ENVIO_YA_GRATIS;
        }
        archivo.guardarAplicado(cliente.getCorreo(), cupon.getCodigo());
        return ResultadoCupon.APLICADO;
    }

    public void quitar() {
        Cliente cliente = SesionActual.getInstancia().getCliente();
        if (cliente != null) {
            archivo.guardarAplicado(cliente.getCorreo(), "");
        }
    }
}
