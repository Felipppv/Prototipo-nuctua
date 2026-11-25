package com.nuctua.servicio;

import com.nuctua.algoritmos.Busqueda;
import com.nuctua.estructuras.ListaEnlazada;
import com.nuctua.estructuras.Pila;
import com.nuctua.modelo.CampoPerfil;
import com.nuctua.modelo.Cliente;
import com.nuctua.modelo.ErrorCampo;
import com.nuctua.modelo.Pedido;
import com.nuctua.modelo.ResumenCliente;
import com.nuctua.modelo.SesionActual;
import com.nuctua.modelo.Usuario;
import com.nuctua.modelo.Zapato;
import com.nuctua.persistencia.ArchivoDeseos;
import com.nuctua.persistencia.ArchivoPedidos;
import com.nuctua.persistencia.ArchivoUsuarios;
import com.nuctua.util.Validaciones;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.UUID;

public class ClientesServicio {

    private final ArchivoUsuarios archivo = new ArchivoUsuarios();
    private final ArchivoDeseos deseos = new ArchivoDeseos();

    public ArrayList<ResumenCliente> todos() {
        ArrayList<ResumenCliente> resumenes = new ArrayList<>();
        ListaEnlazada<Pedido> pedidos = new ArchivoPedidos().cargar();
        for (Usuario usuario : archivo.cargar()) {
            if (usuario instanceof Cliente cliente) {
                ListaEnlazada<Pedido> propios = new ListaEnlazada<>();
                for (Pedido pedido : pedidos) {
                    if (pedido.getCorreoCliente().equalsIgnoreCase(cliente.getCorreo())) {
                        propios.insertarFinal(pedido);
                    }
                }
                resumenes.add(new ResumenCliente(cliente, propios, deseos.cargar(cliente.getCorreo()).size(), LocalDate.now()));
            }
        }
        return resumenes;
    }

    public ArrayList<ResumenCliente> buscar(ArrayList<ResumenCliente> clientes, String texto, FiltroClientes filtro, OrdenClientes orden) {
        ArrayList<ResumenCliente> encontrados = Busqueda.secuencial(clientes,
                c -> filtro.acepta(c) && coincide(c, CatalogoServicio.normalizar(texto)));
        orden.getMetodo().ordenar(encontrados, orden.getComparador());
        return encontrados;
    }

    public static boolean coincide(ResumenCliente resumen, String clave) {
        Cliente cliente = resumen.getCliente();
        String datos = CatalogoServicio.normalizar(cliente.getNombre() + " " + cliente.getCorreo() + " " + cliente.getCiudad()
                + " " + cliente.getTelefono());
        for (String palabra : clave.split(" +")) {
            if (!datos.contains(palabra)) {
                return false;
            }
        }
        return true;
    }

    public ResumenCliente porCorreo(String correo) {
        ArrayList<ResumenCliente> clientes = todos();
        int posicion = Busqueda.secuencialPrimero(clientes, c -> c.getCliente().getCorreo().equalsIgnoreCase(correo));
        return posicion < 0 ? null : clientes.get(posicion);
    }

    public int nuevosEsteMes(ArrayList<ResumenCliente> clientes) {
        LocalDate hoy = LocalDate.now();
        return Busqueda.secuencial(clientes, c -> c.getCliente().getRegistro() != null
                && c.getCliente().getRegistro().getYear() == hoy.getYear()
                && c.getCliente().getRegistro().getMonth() == hoy.getMonth()).size();
    }

    public int recurrentes(ArrayList<ResumenCliente> clientes) {
        return Busqueda.secuencial(clientes, c -> c.getCompras() > 1).size();
    }

    public int conFavoritos(ArrayList<ResumenCliente> clientes) {
        return Busqueda.secuencial(clientes, c -> c.getDeseos() > 0).size();
    }

    public ListaEnlazada<Pedido> ultimosPedidos(ResumenCliente resumen, int maximo) {
        Pila<Pedido> pila = new Pila<>();
        for (Pedido pedido : resumen.getPedidos()) {
            pila.apilar(pedido);
        }
        ListaEnlazada<Pedido> ultimos = new ListaEnlazada<>();
        while (!pila.estaVacia() && ultimos.tamanio() < maximo) {
            ultimos.insertarFinal(pila.desapilar());
        }
        return ultimos;
    }

    public ListaEnlazada<Zapato> favoritos(ResumenCliente resumen, int maximo) {
        CatalogoServicio catalogo = new CatalogoServicio();
        ListaEnlazada<Zapato> favoritos = new ListaEnlazada<>();
        for (String sku : deseos.cargar(resumen.getCliente().getCorreo())) {
            Zapato zapato = catalogo.buscarPorSku(sku);
            if (zapato != null && favoritos.tamanio() < maximo) {
                favoritos.insertarFinal(zapato);
            }
        }
        return favoritos;
    }

    public ListaEnlazada<ErrorCampo> validar(String nombre, String correo, String telefono, String ciudad) {
        ListaEnlazada<ErrorCampo> errores = new ListaEnlazada<>();
        if (!Validaciones.esNombreCompleto(nombre)) {
            errores.insertarFinal(new ErrorCampo(CampoPerfil.NOMBRE, "Escribe nombre y apellido, solo letras."));
        }
        String limpio = Validaciones.limpiar(correo);
        if (!Validaciones.esCorreo(limpio) || !AutenticacionServicio.esCorreoValido(limpio)) {
            errores.insertarFinal(new ErrorCampo(CampoPerfil.CORREO, "Escribe un correo válido."));
        } else if (archivo.cargar().contiene(u -> u.getCorreo().equalsIgnoreCase(limpio))) {
            errores.insertarFinal(new ErrorCampo(CampoPerfil.CORREO, "Ya existe una cuenta con ese correo."));
        }
        if (!Validaciones.digitos(telefono).isEmpty() && !Validaciones.esTelefono(telefono)) {
            errores.insertarFinal(new ErrorCampo(CampoPerfil.TELEFONO, "Escribe 10 números: celular (3…) o fijo (60…)."));
        }
        if (!Validaciones.limpiar(ciudad).isEmpty() && !Validaciones.esCiudad(ciudad)) {
            errores.insertarFinal(new ErrorCampo(CampoPerfil.CIUDAD, "Escribe una ciudad válida."));
        }
        return errores;
    }

    public Cliente agregar(String nombre, String correo, String telefono, String ciudad) {
        if (!SesionActual.getInstancia().esAdministrador() || !validar(nombre, correo, telefono, ciudad).estaVacia()) {
            return null;
        }
        try {
            ListaEnlazada<Usuario> usuarios = archivo.cargar();
            Cliente cliente = new Cliente(Validaciones.limpiar(correo).toLowerCase(), UUID.randomUUID().toString(),
                    Validaciones.limpiar(nombre), Validaciones.digitos(telefono), "", Validaciones.limpiar(ciudad));
            cliente.setRegistro(LocalDate.now());
            usuarios.insertarFinal(cliente);
            archivo.guardar(usuarios);
            new ActividadServicio().registrar("Agregaste al cliente " + cliente.getNombre());
            return cliente;
        } catch (IllegalStateException e) {
            return null;
        }
    }
}
