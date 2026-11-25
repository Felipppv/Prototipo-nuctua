package com.nuctua.util;

import com.nuctua.modelo.Zapato;
import com.nuctua.persistencia.GestorArchivos;
import java.io.File;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import javafx.geometry.Rectangle2D;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.text.Font;

public final class Recursos {

    public static final String PREFIJO_IMAGEN_LOCAL = "datos/imagenes/";
    private static final String BASE = "/com/nuctua/";
    private static final String[] FUENTES = {
        "Anton-Regular.ttf", "SpaceGrotesk-Regular.ttf", "SpaceGrotesk-Medium.ttf", "SpaceGrotesk-Bold.ttf"
    };
    private static final HashMap<String, Image> CACHE = new HashMap<>();

    private Recursos() {
    }

    public static ArrayList<String> cargarFuentes() {
        ArrayList<String> familias = new ArrayList<>();
        for (String fuente : FUENTES) {
            try (InputStream entrada = Recursos.class.getResourceAsStream(BASE + "fuentes/" + fuente)) {
                Font cargada = entrada == null ? null : Font.loadFont(entrada, 14);
                if (cargada == null) {
                    System.err.println("No se pudo cargar la fuente " + fuente);
                } else {
                    familias.add(cargada.getName());
                }
            } catch (Exception e) {
                System.err.println("No se pudo cargar la fuente " + fuente);
            }
        }
        return familias;
    }

    public static String hojaDeEstilos() {
        return url("estilos/nuctua.css").toExternalForm();
    }

    public static URL vista(String nombre) {
        return url("vista/" + nombre + ".fxml");
    }

    public static URL url(String relativa) {
        URL url = Recursos.class.getResource(BASE + relativa);
        if (url == null) {
            throw new IllegalStateException("Recurso no encontrado: " + BASE + relativa);
        }
        return url;
    }

    public static Image imagenProducto(Zapato zapato) {
        return imagenProducto(zapato.getImagen());
    }

    public static Image imagenProducto(String nombre) {
        if (nombre == null || nombre.isBlank()) {
            return null;
        }
        Image guardada = CACHE.get(nombre);
        if (guardada != null) {
            return guardada;
        }
        String ruta = rutaImagen(nombre);
        if (ruta == null) {
            return null;
        }
        Image imagen = new Image(ruta, 900, 0, true, true, true);
        CACHE.put(nombre, imagen);
        return imagen;
    }

    public static ImageView vistaCubierta(Image imagen, double ancho, double alto) {
        ImageView vista = new ImageView();
        vista.setFitWidth(ancho);
        vista.setFitHeight(alto);
        vista.setSmooth(true);
        if (imagen == null) {
            return vista;
        }
        vista.setImage(imagen);
        if (imagen.getProgress() >= 1.0) {
            recortar(vista, imagen, ancho, alto);
        } else {
            imagen.progressProperty().addListener((obs, antes, ahora) -> {
                if (ahora.doubleValue() >= 1.0) {
                    recortar(vista, imagen, ancho, alto);
                }
            });
        }
        return vista;
    }

    public static void olvidarImagen(String nombre) {
        CACHE.remove(nombre);
    }

    public static boolean existeImagen(String nombre) {
        return nombre != null && !nombre.isBlank() && rutaImagen(nombre) != null;
    }

    public static ArrayList<String> imagenesFaltantes(Iterable<Zapato> productos) {
        ArrayList<String> faltantes = new ArrayList<>();
        for (Zapato zapato : productos) {
            if (!existeImagen(zapato.getImagen())) {
                faltantes.add(zapato.getSku() + " → " + zapato.getImagen());
            }
        }
        return faltantes;
    }

    private static void recortar(ImageView vista, Image imagen, double ancho, double alto) {
        double iw = imagen.getWidth();
        double ih = imagen.getHeight();
        if (iw <= 0 || ih <= 0) {
            return;
        }
        double escala = Math.max(ancho / iw, alto / ih);
        double w = ancho / escala;
        double h = alto / escala;
        vista.setViewport(new Rectangle2D((iw - w) / 2, (ih - h) / 2, w, h));
    }

    private static String rutaImagen(String nombre) {
        if (nombre.startsWith(PREFIJO_IMAGEN_LOCAL)) {
            File archivo = new File(GestorArchivos.getCarpetaDatos().getParentFile(), nombre);
            return archivo.exists() ? archivo.toURI().toString() : null;
        }
        URL url = Recursos.class.getResource(BASE + "imagenes/" + nombre);
        return url == null ? null : url.toExternalForm();
    }
}
