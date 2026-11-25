package com.nuctua.servicio;

import com.nuctua.algoritmos.Busqueda;
import com.nuctua.algoritmos.MetodoOrdenamiento;
import com.nuctua.estructuras.ColaPedidos;
import com.nuctua.estructuras.ListaEnlazada;
import com.nuctua.modelo.EstadoPedido;
import com.nuctua.modelo.LineaPedido;
import com.nuctua.modelo.Pedido;
import com.nuctua.modelo.SesionActual;
import com.nuctua.persistencia.ArchivoPedidos;
import com.nuctua.util.Validaciones;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;

public class PedidosServicio {

    private static final String PATRON_NUMERO = "#?(nct-?)?[0-9]{4,9}";

    private final ArchivoPedidos archivo = new ArchivoPedidos();
    private final InventarioServicio inventario = new InventarioServicio();
    private final ActividadServicio actividad = new ActividadServicio();

    public ListaEnlazada<Pedido> todos() {
        return archivo.cargar();
    }

    public ListaEnlazada<Pedido> delCliente(String correo) {
        return archivo.cargarDe(correo);
    }

    public ColaPedidos cola() {
        return new ColaPedidos(archivo.cargar());
    }

    public Pedido siguiente() {
        return cola().frente();
    }

    public int posicionDe(int numero) {
        return cola().posicionDe(numero);
    }

    public int entregadosEl(LocalDate dia) {
        int total = 0;
        for (Pedido pedido : archivo.cargar()) {
            if (pedido.getEstado() == EstadoPedido.ENTREGADO && pedido.getFechaEstado().toLocalDate().equals(dia)) {
                total++;
            }
        }
        return total;
    }

    public ArrayList<Pedido> buscar(String texto, FiltroPedidos filtro) {
        String clave = CatalogoServicio.normalizar(texto);
        ArrayList<Pedido> pedidos = archivo.cargar().aArrayList();
        ArrayList<Pedido> encontrados = new ArrayList<>();
        if (esNumero(clave)) {
            MetodoOrdenamiento.INSERCION.ordenar(pedidos, (a, b) -> Integer.compare(a.getNumero(), b.getNumero()));
            int numero = Integer.parseInt(Validaciones.digitos(clave));
            int posicion = Busqueda.binaria(pedidos, p -> Integer.compare(p.getNumero(), numero), new int[1]);
            if (posicion >= 0 && filtro.acepta(pedidos.get(posicion))) {
                encontrados.add(pedidos.get(posicion));
            }
            return encontrados;
        }
        encontrados = Busqueda.secuencial(pedidos, p -> filtro.acepta(p) && coincide(p, clave));
        MetodoOrdenamiento.QUICKSORT.ordenar(encontrados, (a, b) -> Integer.compare(b.getNumero(), a.getNumero()));
        return encontrados;
    }

    public static boolean esNumero(String clave) {
        return clave.matches(PATRON_NUMERO);
    }

    public static boolean coincide(Pedido pedido, String clave) {
        StringBuilder texto = new StringBuilder("#" + pedido.getCodigo() + " " + pedido.getEnvio().getNombre() + " "
                + pedido.getCorreoCliente() + " " + pedido.getEnvio().getCiudad() + " " + pedido.getEstado().getTexto());
        for (LineaPedido linea : pedido.getLineas()) {
            texto.append(' ').append(linea.getNombre()).append(' ').append(linea.getSku());
        }
        String datos = CatalogoServicio.normalizar(texto.toString());
        for (String palabra : clave.split(" +")) {
            if (!datos.contains(palabra)) {
                return false;
            }
        }
        return true;
    }

    public int cuantos(EstadoPedido estado) {
        int total = 0;
        for (Pedido pedido : archivo.cargar()) {
            if (pedido.getEstado() == estado) {
                total++;
            }
        }
        return total;
    }

    public String describirTurno(int numero) {
        int posicion;
        try {
            posicion = posicionDe(numero);
        } catch (IllegalStateException e) {
            return "";
        }
        if (posicion == 0) {
            return "";
        }
        if (posicion == 1) {
            return "Es el primero en la cola de preparación.";
        }
        return posicion == 2 ? "Hay 1 pedido antes en la cola de preparación."
                : "Hay " + (posicion - 1) + " pedidos antes en la cola de preparación.";
    }

    public String describirCola() {
        try {
            ColaPedidos cola = cola();
            if (cola.estaVacia()) {
                return "No hay pedidos nuevos";
            }
            return (cola.tamanio() == 1 ? "1 pedido nuevo" : cola.tamanio() + " pedidos nuevos") + " · sigue #" + cola.frente().getCodigo();
        } catch (IllegalStateException e) {
            return "Pedidos nuevos";
        }
    }

    public ResultadoEstado atenderSiguiente() {
        if (!esAdministrador()) {
            return sinPermiso();
        }
        ColaPedidos cola = cola();
        Pedido pedido = cola.desencolar();
        if (pedido == null) {
            return ResultadoEstado.error(ResultadoEstado.Tipo.COLA_VACIA, "No hay pedidos nuevos en la cola.");
        }
        return guardar(pedido, EstadoPedido.PREPARANDO);
    }

    public ResultadoEstado cambiarEstado(int numero, EstadoPedido destino) {
        if (!esAdministrador()) {
            return sinPermiso();
        }
        ListaEnlazada<Pedido> pedidos = archivo.cargar();
        Pedido pedido = pedidos.buscar(p -> p.getNumero() == numero);
        if (pedido == null) {
            return ResultadoEstado.error(ResultadoEstado.Tipo.NO_EXISTE, "Ese pedido ya no existe.");
        }
        if (!pedido.getEstado().puedePasarA(destino)) {
            return ResultadoEstado.error(ResultadoEstado.Tipo.NO_PERMITIDO, "Un pedido en estado "
                    + pedido.getEstado().getTexto() + " no puede pasar a " + (destino == null ? "otro" : destino.getTexto()) + ".");
        }
        ColaPedidos cola = new ColaPedidos(pedidos);
        if (destino == EstadoPedido.PREPARANDO && !cola.esSuTurno(numero)) {
            return ResultadoEstado.error(ResultadoEstado.Tipo.NO_ES_SU_TURNO, "Primero debes atender el pedido #"
                    + cola.frente().getCodigo() + ", que llegó antes.");
        }
        return guardar(pedido, destino);
    }

    private ResultadoEstado guardar(Pedido pedido, EstadoPedido destino) {
        EstadoPedido anterior = pedido.getEstado();
        LocalDateTime cambioAnterior = pedido.getFechaEstado();
        pedido.setEstado(destino, LocalDateTime.now());
        try {
            if (!archivo.actualizar(pedido)) {
                return ResultadoEstado.error(ResultadoEstado.Tipo.NO_EXISTE, "Ese pedido ya no existe.");
            }
        } catch (IllegalStateException e) {
            return sinGuardar();
        }
        if (destino == EstadoPedido.CANCELADO) {
            try {
                inventario.reponer(pedido.getLineas());
            } catch (IllegalStateException e) {
                pedido.setEstado(anterior, cambioAnterior);
                deshacer(pedido);
                return sinGuardar();
            }
        }
        actividad.registrar(describir(pedido));
        return ResultadoEstado.exito(pedido);
    }

    private String describir(Pedido pedido) {
        String codigo = " el pedido #" + pedido.getCodigo();
        return switch (pedido.getEstado()) {
            case PREPARANDO -> "Pasaste a preparación" + codigo;
            case ENVIADO -> "Marcaste como enviado" + codigo;
            case ENTREGADO -> "Marcaste como entregado" + codigo;
            case CANCELADO -> "Cancelaste" + codigo;
            case NUEVO -> "Reabriste" + codigo;
        };
    }

    private void deshacer(Pedido pedido) {
        try {
            archivo.actualizar(pedido);
        } catch (IllegalStateException e) {
            System.err.println("No se pudo deshacer el cambio del pedido " + pedido.getCodigo());
        }
    }

    private boolean esAdministrador() {
        return SesionActual.getInstancia().esAdministrador();
    }

    private ResultadoEstado sinPermiso() {
        return ResultadoEstado.error(ResultadoEstado.Tipo.SIN_PERMISO, "Solo un administrador puede cambiar pedidos.");
    }

    private ResultadoEstado sinGuardar() {
        return ResultadoEstado.error(ResultadoEstado.Tipo.SIN_GUARDAR, "No pudimos guardar el cambio. Inténtalo de nuevo.");
    }
}
