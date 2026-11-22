package com.nuctua.util;

import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Ellipse;
import javafx.scene.shape.Line;
import javafx.scene.shape.SVGPath;
import javafx.scene.shape.StrokeLineCap;
import javafx.scene.shape.StrokeLineJoin;

public final class Iconos {

    public static final Color OSCURO = Color.web("#1B1410");
    public static final Color CREMA = Color.web("#FFF4E4");
    public static final Color NARANJA = Color.web("#FF6A2B");
    public static final Color AMARILLO = Color.web("#FFCC33");

    private static final String BUSCAR = "M10.5 3.5a7 7 0 1 0 0 14a7 7 0 1 0 0-14zM15.6 15.6L21 21";
    private static final String CORAZON = "M12 20.5S3.5 15.2 3.5 9.2A4.7 4.7 0 0 1 12 6.4a4.7 4.7 0 0 1 8.5 2.8c0 6-8.5 11.3-8.5 11.3z";
    private static final String CARRITO = "M2.5 3.5h2.6l2.3 10.6a1.6 1.6 0 0 0 1.6 1.3h8.3a1.6 1.6 0 0 0 1.5-1.1L21 7.5H6.2M9.5 20.2h.01M17 20.2h.01";
    private static final String USUARIO = "M12 3.5a4 4 0 1 0 0 8a4 4 0 1 0 0-8zM4 20.5c1.2-3.8 4.3-5.8 8-5.8s6.8 2 8 5.8";
    private static final String OJO = "M2 12s3.6-6.5 10-6.5S22 12 22 12s-3.6 6.5-10 6.5S2 12 2 12zM12 9a3 3 0 1 0 0 6a3 3 0 1 0 0-6z";
    private static final String CANDADO = "M6 11h12v9H6zM8.5 11V8a3.5 3.5 0 0 1 7 0v3";
    private static final String AYUDA = "M12 3a9 9 0 1 0 0 18a9 9 0 1 0 0-18zM9.5 9.3a2.6 2.6 0 0 1 5 .9c0 1.7-2.5 2.2-2.5 3.8M12 16.8v.2";
    private static final String FLECHA_DERECHA = "M5 12h14M13 6l6 6-6 6";
    private static final String FLECHA_IZQUIERDA = "M19 12H5M11 6l-6 6 6 6";
    private static final String BASURA = "M4.5 6.5h15M9.5 6.5V4h5v2.5M6.5 6.5l1 13.5h9l1-13.5M10 10v6.5M14 10v6.5";
    private static final String CHECK = "M5 12.5l4.5 4.5L19 7.5";
    private static final String ALERTA = "M12 3L22 20H2zM12 9.5v5M12 17.2v.3";
    private static final String MAS = "M12 5v14M5 12h14";
    private static final String MENOS = "M5 12h14";
    private static final String LAPIZ = "M4 20h4L19 9l-4-4L4 16zM14 6l4 4";
    private static final String CAMPANA = "M6 16V11a6 6 0 0 1 12 0v5l1.5 2h-15zM10 20.5a2 2 0 0 0 4 0";
    private static final String SALIR = "M14 4h5v16h-5M10 8l-4 4 4 4M6 12h10";

    private Iconos() {
    }

    public static Node buscar(Paint color, double tamano) {
        return trazo(BUSCAR, color, tamano);
    }

    public static Node corazon(Paint color, double tamano, boolean relleno) {
        SVGPath ruta = base(CORAZON, tamano);
        ruta.setStroke(color);
        ruta.setStrokeWidth(2);
        ruta.setFill(relleno ? color : Color.TRANSPARENT);
        return envolver(ruta, tamano);
    }

    public static Node carrito(Paint color, double tamano) {
        return trazo(CARRITO, color, tamano);
    }

    public static Node usuario(Paint color, double tamano) {
        return trazo(USUARIO, color, tamano);
    }

    public static Node ojo(Paint color, double tamano) {
        return trazo(OJO, color, tamano);
    }

    public static Node candado(Paint color, double tamano) {
        return trazo(CANDADO, color, tamano);
    }

    public static Node ayuda(Paint color, double tamano) {
        return trazo(AYUDA, color, tamano);
    }

    public static Node flechaDerecha(Paint color, double tamano) {
        return trazo(FLECHA_DERECHA, color, tamano);
    }

    public static Node flechaIzquierda(Paint color, double tamano) {
        return trazo(FLECHA_IZQUIERDA, color, tamano);
    }

    public static Node basura(Paint color, double tamano) {
        return trazo(BASURA, color, tamano);
    }

    public static Node check(Paint color, double tamano) {
        return trazo(CHECK, color, tamano);
    }

    public static Node alerta(Paint color, double tamano) {
        return trazo(ALERTA, color, tamano);
    }

    public static Node mas(Paint color, double tamano) {
        return trazo(MAS, color, tamano);
    }

    public static Node menos(Paint color, double tamano) {
        return trazo(MENOS, color, tamano);
    }

    public static Node lapiz(Paint color, double tamano) {
        return trazo(LAPIZ, color, tamano);
    }

    public static Node campana(Paint color, double tamano) {
        return trazo(CAMPANA, color, tamano);
    }

    public static Node huellaPequena(Color color, double rotacion) {
        Ellipse planta = new Ellipse(18, 0, 18, 12);
        planta.setFill(color);
        Ellipse talon = new Ellipse(-12, 0, 10, 9);
        talon.setFill(color);
        Group grupo = new Group(talon, planta);
        for (int i = 0; i < 4; i++) {
            Line raya = new Line(8 + i * 6, -8, 12 + i * 6, 8);
            raya.setStroke(NARANJA);
            raya.setStrokeWidth(2.4);
            raya.setStrokeLineCap(StrokeLineCap.ROUND);
            grupo.getChildren().add(raya);
        }
        grupo.setRotate(rotacion);
        StackPane caja = new StackPane(new Group(grupo));
        caja.setMouseTransparent(true);
        return caja;
    }

    public static Node salir(Paint color, double tamano) {
        return trazo(SALIR, color, tamano);
    }

    public static Node okGrande(double tamano) {
        SVGPath circulo = new SVGPath();
        circulo.setContent("M48 4a44 44 0 1 0 0.01 0z");
        circulo.setFill(NARANJA);
        circulo.setStroke(OSCURO);
        circulo.setStrokeWidth(4);
        SVGPath check = new SVGPath();
        check.setContent("M28 49l13 13 27-28");
        check.setFill(Color.TRANSPARENT);
        check.setStroke(OSCURO);
        check.setStrokeWidth(8);
        check.setStrokeLineCap(StrokeLineCap.ROUND);
        check.setStrokeLineJoin(StrokeLineJoin.ROUND);
        Group grupo = new Group(circulo, check);
        double escala = tamano / 96.0;
        grupo.setScaleX(escala);
        grupo.setScaleY(escala);
        StackPane caja = new StackPane(new Group(grupo));
        caja.setMinSize(tamano, tamano);
        caja.setMaxSize(tamano, tamano);
        caja.setMouseTransparent(true);
        return caja;
    }

    public static Node alertaGrande(double tamano) {
        SVGPath triangulo = new SVGPath();
        triangulo.setContent("M48 8L92 86H4Z");
        triangulo.setFill(AMARILLO);
        triangulo.setStroke(OSCURO);
        triangulo.setStrokeWidth(5);
        triangulo.setStrokeLineJoin(StrokeLineJoin.ROUND);
        SVGPath signo = new SVGPath();
        signo.setContent("M48 36v22M48 71v1");
        signo.setStroke(OSCURO);
        signo.setStrokeWidth(8);
        signo.setStrokeLineCap(StrokeLineCap.ROUND);
        Group grupo = new Group(triangulo, signo);
        double escala = tamano / 96.0;
        grupo.setScaleX(escala);
        grupo.setScaleY(escala);
        StackPane caja = new StackPane(new Group(grupo));
        caja.setMinSize(tamano, tamano);
        caja.setMaxSize(tamano, tamano);
        caja.setMouseTransparent(true);
        return caja;
    }

    private static Node trazo(String contenido, Paint color, double tamano) {
        SVGPath ruta = base(contenido, tamano);
        ruta.setStroke(color);
        ruta.setStrokeWidth(2.2);
        ruta.setFill(Color.TRANSPARENT);
        return envolver(ruta, tamano);
    }

    private static SVGPath base(String contenido, double tamano) {
        SVGPath ruta = new SVGPath();
        ruta.setContent(contenido);
        ruta.setStrokeLineCap(StrokeLineCap.ROUND);
        ruta.setStrokeLineJoin(StrokeLineJoin.ROUND);
        double escala = tamano / 24.0;
        ruta.setScaleX(escala);
        ruta.setScaleY(escala);
        return ruta;
    }

    private static Node envolver(SVGPath ruta, double tamano) {
        StackPane caja = new StackPane(new Group(ruta));
        caja.setMinSize(tamano, tamano);
        caja.setPrefSize(tamano, tamano);
        caja.setMaxSize(tamano, tamano);
        caja.setMouseTransparent(true);
        return caja;
    }
}
