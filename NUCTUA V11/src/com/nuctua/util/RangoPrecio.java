package com.nuctua.util;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.scene.Cursor;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.scene.shape.StrokeLineCap;

public class RangoPrecio extends Pane {

    private static final double RADIO = 10;
    private static final double ALTO = 28;

    private final double limiteInferior;
    private final double limiteSuperior;
    private final double paso;
    private final DoubleProperty minimo;
    private final DoubleProperty maximo;
    private final Line pista = new Line();
    private final Line tramo = new Line();
    private final Circle manijaMinima = manija();
    private final Circle manijaMaxima = manija();

    public RangoPrecio() {
        this(120000, 250000, 10000);
    }

    public RangoPrecio(double limiteInferior, double limiteSuperior, double paso) {
        this.limiteInferior = limiteInferior;
        this.limiteSuperior = limiteSuperior;
        this.paso = paso;
        this.minimo = new SimpleDoubleProperty(limiteInferior);
        this.maximo = new SimpleDoubleProperty(limiteSuperior);
        setMinHeight(ALTO);
        setPrefHeight(ALTO);
        pista.setStroke(Iconos.OSCURO);
        pista.setStrokeWidth(4);
        pista.setStrokeLineCap(StrokeLineCap.ROUND);
        tramo.setStroke(Iconos.NARANJA);
        tramo.setStrokeWidth(6);
        tramo.setStrokeLineCap(StrokeLineCap.ROUND);
        getChildren().addAll(pista, tramo, manijaMinima, manijaMaxima);
        manijaMinima.setOnMouseDragged(e -> {
            double valor = valorEn(e.getX());
            setMinimo(Math.min(valor, getMaximo() - paso));
        });
        manijaMaxima.setOnMouseDragged(e -> {
            double valor = valorEn(e.getX());
            setMaximo(Math.max(valor, getMinimo() + paso));
        });
        minimo.addListener((obs, antes, ahora) -> requestLayout());
        maximo.addListener((obs, antes, ahora) -> requestLayout());
    }

    @Override
    protected void layoutChildren() {
        double y = getHeight() / 2;
        double inicio = RADIO + 2;
        double fin = getWidth() - RADIO - 2;
        pista.setStartX(inicio);
        pista.setEndX(fin);
        pista.setStartY(y);
        pista.setEndY(y);
        double xMinima = posicionDe(getMinimo());
        double xMaxima = posicionDe(getMaximo());
        tramo.setStartX(xMinima);
        tramo.setEndX(xMaxima);
        tramo.setStartY(y);
        tramo.setEndY(y);
        manijaMinima.setCenterX(xMinima);
        manijaMinima.setCenterY(y);
        manijaMaxima.setCenterX(xMaxima);
        manijaMaxima.setCenterY(y);
    }

    private double posicionDe(double valor) {
        double inicio = RADIO + 2;
        double ancho = Math.max(1, getWidth() - 2 * (RADIO + 2));
        return inicio + (valor - limiteInferior) / (limiteSuperior - limiteInferior) * ancho;
    }

    private double valorEn(double x) {
        double inicio = RADIO + 2;
        double ancho = Math.max(1, getWidth() - 2 * (RADIO + 2));
        double proporcion = Math.max(0, Math.min(1, (x - inicio) / ancho));
        double valor = limiteInferior + proporcion * (limiteSuperior - limiteInferior);
        return Math.round(valor / paso) * paso;
    }

    private Circle manija() {
        Circle circulo = new Circle(RADIO, Color.web("#FFFBF3"));
        circulo.setStroke(Iconos.OSCURO);
        circulo.setStrokeWidth(2.5);
        circulo.setCursor(Cursor.HAND);
        return circulo;
    }

    public void reiniciar() {
        setMinimo(limiteInferior);
        setMaximo(limiteSuperior);
    }

    public double getLimiteInferior() {
        return limiteInferior;
    }

    public double getLimiteSuperior() {
        return limiteSuperior;
    }

    public double getMinimo() {
        return minimo.get();
    }

    public void setMinimo(double valor) {
        minimo.set(Math.max(limiteInferior, Math.min(valor, getMaximo() - paso)));
    }

    public DoubleProperty minimoProperty() {
        return minimo;
    }

    public double getMaximo() {
        return maximo.get();
    }

    public void setMaximo(double valor) {
        maximo.set(Math.min(limiteSuperior, Math.max(valor, getMinimo() + paso)));
    }

    public DoubleProperty maximoProperty() {
        return maximo;
    }
}
