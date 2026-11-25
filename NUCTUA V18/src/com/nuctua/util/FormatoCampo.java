package com.nuctua.util;

import java.util.function.UnaryOperator;
import javafx.scene.control.TextFormatter;
import javafx.scene.control.TextInputControl;

public final class FormatoCampo {

    private FormatoCampo() {
    }

    public static void telefono(TextInputControl campo) {
        numerico(campo, 10, d -> d.length() > 10 && d.startsWith("57") ? d.substring(2) : d, FormatoCampo::agruparTelefono);
    }

    public static void tarjeta(TextInputControl campo) {
        numerico(campo, 16, d -> d, FormatoCampo::agruparTarjeta);
    }

    public static void vence(TextInputControl campo) {
        numerico(campo, 4, d -> !d.isEmpty() && d.charAt(0) > '1' ? "0" + d : d, FormatoCampo::agruparVence);
    }

    public static void soloDigitos(TextInputControl campo, int maximo) {
        numerico(campo, maximo, d -> d, d -> d);
    }

    public static void texto(TextInputControl campo, int maximo) {
        campo.setTextFormatter(new TextFormatter<>(cambio -> {
            int sobran = cambio.getControlNewText().length() - maximo;
            if (!cambio.isContentChange() || sobran <= 0) {
                return cambio;
            }
            String agregado = cambio.getText();
            if (agregado.length() <= sobran) {
                return null;
            }
            cambio.setText(agregado.substring(0, agregado.length() - sobran));
            int fin = cambio.getRangeStart() + cambio.getText().length();
            cambio.selectRange(fin, fin);
            return cambio;
        }));
    }

    public static String agruparTelefono(String digitos) {
        if (digitos.length() <= 3) {
            return digitos;
        }
        if (digitos.length() <= 6) {
            return digitos.substring(0, 3) + " " + digitos.substring(3);
        }
        return digitos.substring(0, 3) + " " + digitos.substring(3, 6) + " " + digitos.substring(6);
    }

    public static String agruparTarjeta(String digitos) {
        StringBuilder texto = new StringBuilder();
        for (int i = 0; i < digitos.length(); i++) {
            if (i > 0 && i % 4 == 0) {
                texto.append(' ');
            }
            texto.append(digitos.charAt(i));
        }
        return texto.toString();
    }

    public static String agruparVence(String digitos) {
        return digitos.length() <= 2 ? digitos : digitos.substring(0, 2) + " / " + digitos.substring(2);
    }

    private static void numerico(TextInputControl campo, int maximo, UnaryOperator<String> limpiar, UnaryOperator<String> agrupar) {
        campo.setTextFormatter(new TextFormatter<>(cambio -> {
            if (!cambio.isContentChange()) {
                return cambio;
            }
            String escrito = cambio.getControlNewText();
            String crudos = Validaciones.digitos(escrito);
            String digitos = limpiar.apply(crudos);
            boolean lleno = Validaciones.digitos(cambio.getControlText()).length() == maximo;
            if (digitos.length() > maximo && lleno && !cambio.isDeleted()) {
                return null;
            }
            int cursor = Math.min(cambio.getCaretPosition(), escrito.length());
            int antes = Validaciones.digitos(escrito.substring(0, cursor)).length() + digitos.length() - crudos.length();
            if (digitos.length() > maximo) {
                digitos = digitos.substring(0, maximo);
            }
            String texto = agrupar.apply(digitos);
            int posicion = posicionTras(texto, Math.max(0, Math.min(antes, digitos.length())));
            cambio.setRange(0, cambio.getControlText().length());
            cambio.setText(texto);
            cambio.selectRange(posicion, posicion);
            return cambio;
        }));
    }

    private static int posicionTras(String texto, int digitos) {
        int vistos = 0;
        int i = 0;
        while (i < texto.length() && vistos < digitos) {
            if (Character.isDigit(texto.charAt(i))) {
                vistos++;
            }
            i++;
        }
        return i;
    }
}
