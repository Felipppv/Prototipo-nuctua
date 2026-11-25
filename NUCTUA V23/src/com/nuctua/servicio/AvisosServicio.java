package com.nuctua.servicio;

import com.nuctua.modelo.Cliente;
import com.nuctua.modelo.Usuario;
import com.nuctua.modelo.Zapato;
import com.nuctua.persistencia.ArchivoUsuarios;
import java.time.LocalDate;
import java.util.ArrayList;

public class AvisosServicio {

    private static final int POCAS_UNIDADES = 3;
    private static final int DIAS_CLIENTE_NUEVO = 7;

    private final PreferenciasServicio preferencias = new PreferenciasServicio();

    public String texto() {
        ArrayList<String> avisos = new ArrayList<>();
        try {
            if (preferencias.isActiva(PreferenciasServicio.AVISO_PEDIDOS, true)) {
                avisos.add(new PedidosServicio().describirCola());
            }
            if (preferencias.isActiva(PreferenciasServicio.AVISO_STOCK, true)) {
                int bajos = modelosConPocoStock();
                if (bajos > 0) {
                    avisos.add(bajos + (bajos == 1 ? " modelo" : " modelos") + " con menos de " + POCAS_UNIDADES + " pares");
                }
            }
            if (preferencias.isActiva(PreferenciasServicio.AVISO_CLIENTES, false)) {
                int nuevos = clientesNuevos();
                avisos.add(nuevos + (nuevos == 1 ? " cliente nuevo" : " clientes nuevos") + " esta semana");
            }
        } catch (IllegalStateException e) {
            return "Pedidos";
        }
        return avisos.isEmpty() ? "Avisos desactivados" : String.join("\n", avisos);
    }

    public int modelosConPocoStock() {
        int total = 0;
        for (Zapato zapato : new CatalogoServicio().activos()) {
            if (zapato.getStockTotal() < POCAS_UNIDADES) {
                total++;
            }
        }
        return total;
    }

    public int clientesNuevos() {
        int total = 0;
        LocalDate desde = LocalDate.now().minusDays(DIAS_CLIENTE_NUEVO);
        for (Usuario usuario : new ArchivoUsuarios().cargar()) {
            if (usuario instanceof Cliente cliente && cliente.getRegistro() != null && !cliente.getRegistro().isBefore(desde)) {
                total++;
            }
        }
        return total;
    }
}
