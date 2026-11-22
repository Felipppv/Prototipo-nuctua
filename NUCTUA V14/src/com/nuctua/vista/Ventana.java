package com.nuctua.vista;

import java.util.ArrayList;

public enum Ventana {
    LOGIN_CLIENTE("01", "Login cliente", Area.ACCESO, "Login", "12:2455", 1440, 900),
    REGISTRO_USUARIO("02", "Registro de usuario", Area.ACCESO, "Registro", "100:1958", 1440, 1120),
    LOGIN_ADMINISTRADOR("03", "Login administrador", Area.ACCESO, "LoginAdmin", "27:807", 684, 852),
    CATALOGO("C1", "Catálogo", Area.CLIENTE, "Catalogo", "100:847", 1440, 2732),
    DETALLE_PRODUCTO("C2", "Detalle de producto", Area.CLIENTE, "DetalleProducto", "149:224", 1440, 1218),
    CARRITO("C3", "Carrito", Area.CLIENTE, "Carrito", "12:2227", 1440, 1409),
    PAGO("C4", "Pago", Area.CLIENTE, "Pago", "102:1034", 1440, 1521),
    HISTORIAL_COMPRAS("C5", "Historial de compras", Area.CLIENTE, "Historial", "23:349", 1440, 1431),
    LISTA_DESEOS("C6", "Lista de deseos", Area.CLIENTE, "ListaDeseos", "12:2099", 1440, 1189),
    PERFIL_USUARIO("C7", "Perfil de usuario", Area.CLIENTE, "PerfilUsuario", "104:1034", 1440, 2003),
    BUSCAR_ZAPATOS("C8", "Buscar zapatos", Area.CLIENTE, "BuscarZapatos", "96:360", 1440, 1353),
    ADMIN_INVENTARIO("A1", "Inventario", Area.ADMINISTRADOR, "AdminInventario", "12:2637", 1440, 1258),
    ADMIN_STOCK_TALLA("A2", "Stock por talla", Area.ADMINISTRADOR, "AdminStockTalla", "12:2953", 1440, 1015),
    ADMIN_PRODUCTOS("A3", "Productos", Area.ADMINISTRADOR, "AdminProductos", "105:1034", 1440, 1317),
    ADMIN_REGISTRAR_PRODUCTO("A4", "Registrar producto", Area.ADMINISTRADOR, "AdminRegistrarProducto", "106:1034", 1440, 1747),
    ADMIN_PEDIDOS("A5", "Pedidos", Area.ADMINISTRADOR, "AdminPedidos", "92:173", 1440, 1492),
    ADMIN_CLIENTES("A6", "Clientes", Area.ADMINISTRADOR, "AdminClientes", "92:456", 1440, 1334),
    ADMIN_BUSCAR("A7", "Buscar", Area.ADMINISTRADOR, "AdminBuscar", "107:1348", 1440, 1304),
    ADMIN_PERFIL("A8", "Perfil del administrador", Area.ADMINISTRADOR, "AdminPerfil", "107:1034", 1440, 2126),
    POPUP_ORDENAR_CATALOGO("P1", "Ordenar catálogo", Area.POPUP, "PopupOrdenarCatalogo", "157:609", 720, 610),
    POPUP_AGREGADO_CARRITO("P2", "Agregado al carrito", Area.POPUP, "PopupAgregadoCarrito", "157:635", 720, 592),
    POPUP_COMPRA_CONFIRMADA("P3", "Compra confirmada", Area.POPUP, "PopupCompraConfirmada", "157:655", 720, 552),
    POPUP_DATOS_INCORRECTOS("P4", "Datos incorrectos", Area.POPUP, "PopupDatosIncorrectos", "157:671", 720, 500),
    POPUP_ELIMINAR_PRODUCTO("P5", "Eliminar producto", Area.POPUP, "PopupEliminarProducto", "157:684", 720, 616),
    POPUP_ESTADO_PEDIDO("P6", "Cambiar estado del pedido", Area.POPUP, "PopupEstadoPedido", "157:704", 720, 647),
    POPUP_CUENTA_CREADA("P7", "Cuenta creada", Area.POPUP, "PopupCuentaCreada", "157:731", 720, 500),
    POPUP_CAMBIOS_GUARDADOS("P8", "Cambios guardados", Area.POPUP, "PopupCambiosGuardados", "157:743", 720, 616),
    POPUP_RECUPERAR_CONTRASENA("P9", "Recuperar contraseña", Area.POPUP, "PopupRecuperarContrasena", "", 720, 700);

    private final String codigo;
    private final String titulo;
    private final Area area;
    private final String archivo;
    private final String nodoFigma;
    private final int anchoFigma;
    private final int altoFigma;

    Ventana(String codigo, String titulo, Area area, String archivo, String nodoFigma, int anchoFigma, int altoFigma) {
        this.codigo = codigo;
        this.titulo = titulo;
        this.area = area;
        this.archivo = archivo;
        this.nodoFigma = nodoFigma;
        this.anchoFigma = anchoFigma;
        this.altoFigma = altoFigma;
    }

    public static ArrayList<Ventana> delArea(Area area) {
        ArrayList<Ventana> resultado = new ArrayList<>();
        for (Ventana ventana : values()) {
            if (ventana.area == area) {
                resultado.add(ventana);
            }
        }
        return resultado;
    }

    public boolean esPopup() {
        return area == Area.POPUP;
    }

    public String getRutaFxml() {
        return "/com/nuctua/vista/" + archivo + ".fxml";
    }

    public String getCodigo() {
        return codigo;
    }

    public String getTitulo() {
        return titulo;
    }

    public Area getArea() {
        return area;
    }

    public String getArchivo() {
        return archivo;
    }

    public String getNodoFigma() {
        return nodoFigma;
    }

    public int getAnchoFigma() {
        return anchoFigma;
    }

    public int getAltoFigma() {
        return altoFigma;
    }
}
