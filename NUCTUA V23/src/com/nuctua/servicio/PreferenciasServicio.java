package com.nuctua.servicio;

import com.nuctua.modelo.SesionActual;
import com.nuctua.modelo.Usuario;
import com.nuctua.modelo.Zapato;
import com.nuctua.persistencia.GestorArchivos;
import java.util.ArrayList;

public class PreferenciasServicio {

    public static final String TALLA = "TALLA";
    public static final String INTERESES = "INTERESES";
    public static final String NOVEDADES = "NOVEDADES";
    public static final String OFERTAS = "OFERTAS";
    public static final String AVISO_PEDIDOS = "AVISO_PEDIDOS";
    public static final String AVISO_STOCK = "AVISO_STOCK";
    public static final String AVISO_CLIENTES = "AVISO_CLIENTES";

    private static final String NOMBRE = "preferencias.txt";
    private static final String[] OPCIONES = {"Bajos", "Altos", "SB", "Dunk", "Jordan"};

    public String[] opcionesDeInteres() {
        return OPCIONES.clone();
    }

    public int getTalla() {
        String valor = leer(TALLA);
        return valor.matches("[0-9]{2}") ? Integer.parseInt(valor) : 0;
    }

    public boolean setTalla(int talla) {
        return escribir(TALLA, talla <= 0 ? "" : String.valueOf(talla));
    }

    public ArrayList<String> getIntereses() {
        ArrayList<String> intereses = new ArrayList<>();
        for (String interes : leer(INTERESES).split(",")) {
            for (String opcion : OPCIONES) {
                if (opcion.equals(interes.trim()) && !intereses.contains(opcion)) {
                    intereses.add(opcion);
                }
            }
        }
        return intereses;
    }

    public boolean setIntereses(ArrayList<String> intereses) {
        return escribir(INTERESES, String.join(",", intereses));
    }

    public boolean isActiva(String clave, boolean porDefecto) {
        String valor = leer(clave);
        return valor.isEmpty() ? porDefecto : Boolean.parseBoolean(valor);
    }

    public boolean setActiva(String clave, boolean activa) {
        return escribir(clave, String.valueOf(activa));
    }

    public static boolean coincide(Zapato zapato, ArrayList<String> intereses) {
        String texto = " " + CatalogoServicio.normalizar(zapato.getNombre() + " " + zapato.getMarca()) + " ";
        for (String interes : intereses) {
            boolean coincide = switch (interes) {
                case "Bajos" -> "Bajo".equalsIgnoreCase(zapato.getCategoria());
                case "Altos" -> "Alto".equalsIgnoreCase(zapato.getCategoria());
                default -> texto.contains(" " + CatalogoServicio.normalizar(interes) + " ");
            };
            if (coincide) {
                return true;
            }
        }
        return false;
    }

    private String leer(String clave) {
        Usuario usuario = SesionActual.getInstancia().getUsuario();
        if (usuario == null) {
            return "";
        }
        for (String linea : GestorArchivos.leerLineas(NOMBRE)) {
            String[] campos = linea.split(GestorArchivos.SEPARADOR_REGEX, -1);
            if (campos.length > 2 && campos[0].equalsIgnoreCase(usuario.getCorreo()) && campos[1].equals(clave)) {
                return campos[2].trim();
            }
        }
        return "";
    }

    private boolean escribir(String clave, String valor) {
        Usuario usuario = SesionActual.getInstancia().getUsuario();
        if (usuario == null) {
            return false;
        }
        try {
            ArrayList<String> propias = new ArrayList<>();
            for (String linea : GestorArchivos.leerLineas(NOMBRE)) {
                String[] campos = linea.split(GestorArchivos.SEPARADOR_REGEX, -1);
                if (campos.length > 2 && campos[0].equalsIgnoreCase(usuario.getCorreo()) && !campos[1].equals(clave)) {
                    propias.add(linea);
                }
            }
            propias.add(GestorArchivos.limpiar(usuario.getCorreo()) + GestorArchivos.SEPARADOR + clave
                    + GestorArchivos.SEPARADOR + GestorArchivos.limpiar(valor));
            GestorArchivos.reemplazarDe(NOMBRE, usuario.getCorreo(), propias);
            return true;
        } catch (IllegalStateException e) {
            return false;
        }
    }
}
