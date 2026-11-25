package com.nuctua.util;

import java.time.LocalDate;
import java.time.YearMonth;

public final class Validaciones {

    private static final int ANIOS_MAXIMOS_TARJETA = 15;

    private Validaciones() {
    }

    public static String limpiar(String texto) {
        return texto == null ? "" : texto.strip().replaceAll("[ \t\r\n]+", " ");
    }

    public static String digitos(String texto) {
        return texto == null ? "" : texto.replaceAll("[^0-9]", "");
    }

    public static boolean esNombreCompleto(String texto) {
        String limpio = limpiar(texto);
        return limpio.matches("[\\p{L}.'-]+( [\\p{L}.'-]+)+") && contarLetras(limpio) >= 4;
    }

    public static boolean esTelefono(String texto) {
        return digitos(texto).matches("3[0-9]{9}|60[0-9]{8}");
    }

    public static boolean esCelular(String texto) {
        return digitos(texto).matches("3[0-9]{9}");
    }

    public static boolean esCorreo(String texto) {
        return limpiar(texto).matches("[^@ ]+@[^@ .]+([.][^@ .]+)+");
    }

    public static boolean esCiudad(String texto) {
        String limpio = limpiar(texto);
        return limpio.matches("[\\p{L} .,'()-]+") && contarLetras(limpio) >= 3;
    }

    public static boolean esDireccion(String texto) {
        String limpio = limpiar(texto);
        return limpio.length() >= 6 && contarLetras(limpio) >= 2 && !digitos(limpio).isEmpty();
    }

    public static boolean esDocumento(String texto) {
        return digitos(texto).matches("[1-9][0-9]{5,9}");
    }

    public static boolean esAmex(String numero) {
        return digitos(numero).matches("3[47][0-9]*");
    }

    public static boolean esTarjeta(String texto) {
        String numero = digitos(texto);
        if (numero.length() != (esAmex(numero) ? 15 : 16) || numero.matches("([0-9])\\1+")) {
            return false;
        }
        int suma = 0;
        boolean doble = false;
        for (int i = numero.length() - 1; i >= 0; i--) {
            int valor = numero.charAt(i) - '0';
            if (doble) {
                valor *= 2;
                if (valor > 9) {
                    valor -= 9;
                }
            }
            suma += valor;
            doble = !doble;
        }
        return suma % 10 == 0;
    }

    public static boolean esMesValido(String vence) {
        String valor = digitos(vence);
        if (valor.length() != 4) {
            return false;
        }
        int mes = Integer.parseInt(valor.substring(0, 2));
        return mes >= 1 && mes <= 12;
    }

    public static boolean estaVigente(String vence, LocalDate hoy) {
        if (!esMesValido(vence)) {
            return false;
        }
        String valor = digitos(vence);
        YearMonth limite = YearMonth.of(2000 + Integer.parseInt(valor.substring(2)), Integer.parseInt(valor.substring(0, 2)));
        YearMonth actual = YearMonth.from(hoy);
        return !limite.isBefore(actual) && !limite.isAfter(actual.plusYears(ANIOS_MAXIMOS_TARJETA));
    }

    public static boolean esCvv(String cvv, String numeroTarjeta) {
        return digitos(cvv).length() == (esAmex(numeroTarjeta) ? 4 : 3);
    }

    private static int contarLetras(String texto) {
        int letras = 0;
        for (int i = 0; i < texto.length(); i++) {
            if (Character.isLetter(texto.charAt(i))) {
                letras++;
            }
        }
        return letras;
    }
}
