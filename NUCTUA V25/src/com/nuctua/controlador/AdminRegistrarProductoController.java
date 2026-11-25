package com.nuctua.controlador;

import com.nuctua.estructuras.ListaEnlazada;
import com.nuctua.modelo.CampoProducto;
import com.nuctua.modelo.DatosProducto;
import com.nuctua.modelo.ErrorCampo;
import com.nuctua.modelo.FotoPar;
import com.nuctua.modelo.TallaStock;
import com.nuctua.modelo.Zapato;
import com.nuctua.persistencia.ArchivoImagenes;
import com.nuctua.servicio.CatalogoServicio;
import com.nuctua.servicio.ProductosServicio;
import com.nuctua.servicio.ResultadoProducto;
import com.nuctua.util.FormatoCampo;
import com.nuctua.util.FotoProducto;
import com.nuctua.util.Iconos;
import com.nuctua.util.Recursos;
import com.nuctua.util.Validaciones;
import com.nuctua.vista.CampoFormulario;
import com.nuctua.vista.ContadorTalla;
import com.nuctua.vista.Interruptor;
import com.nuctua.vista.TarjetaZapato;
import com.nuctua.vista.Ventana;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import javafx.fxml.FXML;
import javafx.geometry.Bounds;
import javafx.geometry.Pos;
import javafx.geometry.Side;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextFormatter;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.Tooltip;
import javafx.scene.image.Image;
import javafx.scene.input.DragEvent;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.stage.FileChooser;

public class AdminRegistrarProductoController implements ConEstadoNavegacion {

    private static final String OTRA_MARCA = "Otra marca";
    private static final int COLORES_INICIALES = 5;
    private static final int LARGO_DESCRIPCION = 300;

    @FXML
    private BarraAdminController barraController;
    @FXML
    private ScrollPane scroll;
    @FXML
    private Hyperlink enlaceVolver;
    @FXML
    private Label sello;
    @FXML
    private Label titulo;
    @FXML
    private CampoFormulario campoNombre;
    @FXML
    private CampoFormulario campoSku;
    @FXML
    private CampoFormulario campoMarca;
    @FXML
    private CampoFormulario campoOtraMarca;
    @FXML
    private CampoFormulario campoCategoria;
    @FXML
    private CampoFormulario campoEstado;
    @FXML
    private CampoFormulario campoPrecio;
    @FXML
    private CampoFormulario campoStock;
    @FXML
    private TextArea areaDescripcion;
    @FXML
    private Label errorDescripcion;
    @FXML
    private FlowPane colores;
    @FXML
    private Label errorColores;
    @FXML
    private FlowPane tallas;
    @FXML
    private Label errorTallas;
    @FXML
    private VBox zonaFoto;
    @FXML
    private Button botonFoto;
    @FXML
    private HBox miniaturas;
    @FXML
    private Label errorImagen;
    @FXML
    private StackPane fotoPrevia;
    @FXML
    private Label nombrePrevio;
    @FXML
    private Label coloresPrevios;
    @FXML
    private Label precioPrevio;
    @FXML
    private Interruptor interruptorVisible;
    @FXML
    private Label mensaje;
    @FXML
    private Button botonGuardar;
    @FXML
    private Hyperlink enlaceCancelar;

    private final ProductosServicio servicio = new ProductosServicio();
    private final ArrayList<String> coloresElegidos = new ArrayList<>();
    private final ListaEnlazada<FotoPar> fotos = new ListaEnlazada<>();
    private final Button botonColor = new Button("+ Color");
    private final ContextMenu menuColores = new ContextMenu();
    private Zapato original;

    @FXML
    private void initialize() {
        barraController.marcar(Ventana.ADMIN_REGISTRAR_PRODUCTO);
        enlaceVolver.setGraphic(Iconos.flechaIzquierda(Iconos.OSCURO, 18));
        enlaceVolver.setText(Navegador.textoVolver());
        enlaceVolver.setOnAction(e -> Navegador.volver());
        enlaceCancelar.setOnAction(e -> Navegador.volver());
        botonGuardar.setOnAction(e -> guardar());
        botonFoto.setOnAction(e -> elegirFoto());
        prepararCampos();
        prepararColores();
        prepararTallas();
        prepararZona();
        pintarFotos();
        pintarPrevia();
    }

    private void prepararCampos() {
        FormatoCampo.texto(campoNombre.getCampo(), 50);
        FormatoCampo.texto(campoSku.getCampo(), 7);
        FormatoCampo.precio(campoPrecio.getCampo());
        FormatoCampo.texto(campoOtraMarca.getCampo(), 20);
        campoSku.setTexto(servicio.siguienteSku());
        ArrayList<String> marcas = servicio.marcas();
        marcas.add(OTRA_MARCA);
        campoMarca.usarLista(marcas.toArray(new String[0]));
        campoMarca.getLista().valueProperty().addListener((obs, antes, ahora) -> {
            alternar(campoOtraMarca, OTRA_MARCA.equals(ahora));
            mostrarMensaje("");
        });
        alternar(campoOtraMarca, false);
        campoCategoria.usarLista(servicio.categorias().toArray(new String[0]));
        campoCategoria.getLista().getSelectionModel().selectFirst();
        campoEstado.usarLista("Activo", "Oculto");
        campoEstado.getLista().setValue("Activo");
        interruptorVisible.setSelected(true);
        campoEstado.getLista().valueProperty().addListener((obs, antes, ahora) -> interruptorVisible.setSelected("Activo".equals(ahora)));
        interruptorVisible.setOnAction(e -> campoEstado.getLista().setValue(interruptorVisible.isSelected() ? "Activo" : "Oculto"));
        campoStock.getCampo().setEditable(false);
        campoStock.getCampo().setFocusTraversable(false);
        for (CampoFormulario campo : new CampoFormulario[]{campoNombre, campoSku, campoPrecio, campoOtraMarca}) {
            campo.getCampo().textProperty().addListener((obs, antes, ahora) -> {
                pintarPrevia();
                mostrarMensaje("");
            });
        }
        areaDescripcion.setTextFormatter(new TextFormatter<>(cambio ->
                cambio.getControlNewText().length() > LARGO_DESCRIPCION ? null : cambio));
        areaDescripcion.textProperty().addListener((obs, antes, ahora) -> {
            marcarError(errorDescripcion, "");
            mostrarMensaje("");
        });
    }

    private void prepararColores() {
        menuColores.getStyleClass().add("menu-orden");
        botonColor.getStyleClass().add("boton-color");
        botonColor.setOnAction(e -> menuColores.show(botonColor, Side.BOTTOM, 0, 6));
        colores.getChildren().add(botonColor);
        String[] conocidos = TarjetaZapato.coloresConocidos();
        for (int i = 0; i < conocidos.length; i++) {
            String color = conocidos[i];
            if (i < COLORES_INICIALES) {
                agregarMuestra(color);
                continue;
            }
            Circle punto = new Circle(8, TarjetaZapato.colorDe(color));
            punto.setStroke(Iconos.OSCURO);
            MenuItem item = new MenuItem(color, punto);
            item.setOnAction(e -> {
                menuColores.getItems().remove(item);
                agregarMuestra(color).fire();
                alternar(botonColor, !menuColores.getItems().isEmpty());
            });
            menuColores.getItems().add(item);
        }
    }

    public void editar(Zapato elegido) {
        Zapato vigente = elegido == null ? null : new CatalogoServicio().buscarPorSku(elegido.getSku());
        if (vigente == null) {
            mostrarMensaje("Ese par ya no existe en el archivo de productos.");
            botonGuardar.setDisable(true);
            return;
        }
        original = vigente;
        sello.setText("Editando · SKU " + original.getSku());
        titulo.setText("EDITAR PRODUCTO.");
        botonGuardar.setText("GUARDAR CAMBIOS →");
        campoSku.setTexto(original.getSku());
        campoSku.getCampo().setDisable(true);
        campoNombre.setTexto(original.getNombre());
        campoMarca.getLista().setValue(original.getMarca());
        campoCategoria.getLista().setValue(original.getCategoria());
        campoEstado.getLista().setValue(original.isActivo() ? "Activo" : "Oculto");
        campoPrecio.setTexto(String.valueOf(original.getPrecio()));
        areaDescripcion.setText(original.getDescripcion());
        for (Node nodo : tallas.getChildren()) {
            ContadorTalla contador = (ContadorTalla) nodo;
            contador.setValor(original.getStock(contador.getTalla()));
        }
        marcarColores();
        fotos.vaciar();
        for (String ruta : Recursos.fotosDe(original)) {
            fotos.insertarFinal(FotoPar.guardada(ruta));
        }
        pintarFotos();
        pintarPrevia();
        mostrarMensaje("");
    }

    private void marcarColores() {
        coloresElegidos.clear();
        for (String parte : original.getColores().split("·")) {
            String color = parte.trim();
            if (!color.isEmpty()) {
                muestraDe(color).setSelected(true);
                coloresElegidos.add(color);
            }
        }
    }

    private ToggleButton muestraDe(String color) {
        for (Node nodo : colores.getChildren()) {
            if (nodo instanceof ToggleButton muestra && color.equals(muestra.getUserData())) {
                return muestra;
            }
        }
        menuColores.getItems().removeIf(item -> item.getText().equals(color));
        alternar(botonColor, !menuColores.getItems().isEmpty());
        return agregarMuestra(color);
    }

    private ToggleButton agregarMuestra(String color) {
        Circle circulo = new Circle(17, TarjetaZapato.colorDe(color));
        circulo.setStroke(Iconos.OSCURO);
        circulo.setStrokeWidth(2);
        ToggleButton muestra = new ToggleButton();
        muestra.getStyleClass().add("muestra-color");
        muestra.setGraphic(circulo);
        muestra.setTooltip(new Tooltip(color));
        muestra.setAccessibleText(color);
        muestra.setUserData(color);
        muestra.setOnAction(e -> elegirColor(muestra, color));
        colores.getChildren().add(colores.getChildren().size() - 1, muestra);
        return muestra;
    }

    private void elegirColor(ToggleButton muestra, String color) {
        if (muestra.isSelected() && coloresElegidos.size() >= ProductosServicio.MAXIMO_COLORES) {
            muestra.setSelected(false);
            marcarError(errorColores, "Puedes elegir hasta " + ProductosServicio.MAXIMO_COLORES + " colores.");
            return;
        }
        if (muestra.isSelected()) {
            coloresElegidos.add(color);
        } else {
            coloresElegidos.remove(color);
        }
        marcarError(errorColores, "");
        pintarPrevia();
        mostrarMensaje("");
    }

    private void prepararTallas() {
        for (int talla = ProductosServicio.TALLA_MINIMA; talla <= ProductosServicio.TALLA_MAXIMA; talla++) {
            ContadorTalla contador = new ContadorTalla(talla);
            contador.getCampo().textProperty().addListener((obs, antes, ahora) -> {
                marcarError(errorTallas, "");
                pintarPrevia();
                mostrarMensaje("");
            });
            tallas.getChildren().add(contador);
        }
    }

    private void prepararZona() {
        zonaFoto.setOnDragOver(this::alArrastrar);
        zonaFoto.setOnDragDropped(e -> {
            agregarFotos(e.getDragboard().getFiles());
            e.setDropCompleted(true);
            e.consume();
        });
    }

    private void alArrastrar(DragEvent evento) {
        if (evento.getDragboard().hasFiles()) {
            evento.acceptTransferModes(TransferMode.COPY);
        }
        evento.consume();
    }

    private void elegirFoto() {
        FileChooser selector = new FileChooser();
        selector.setTitle("Elige la foto del par");
        selector.getExtensionFilters().add(new FileChooser.ExtensionFilter("Fotos PNG o JPG", "*.png", "*.jpg", "*.jpeg"));
        List<File> elegidas = selector.showOpenMultipleDialog(Navegador.getEscenario());
        if (elegidas != null) {
            agregarFotos(elegidas);
        }
    }

    public void agregarFotos(List<File> elegidas) {
        marcarError(errorImagen, "");
        for (File foto : elegidas) {
            if (fotos.tamanio() >= ArchivoImagenes.MAXIMO) {
                marcarError(errorImagen, "Puedes subir hasta " + ArchivoImagenes.MAXIMO + " fotos.");
                break;
            }
            FotoPar nueva = FotoPar.nueva(foto);
            if (fotos.contiene(f -> f.esMisma(nueva))) {
                continue;
            }
            String error = FotoProducto.revisar(foto);
            if (error.isEmpty()) {
                fotos.insertarFinal(nueva);
            } else {
                marcarError(errorImagen, error);
            }
        }
        pintarFotos();
        mostrarMensaje("");
    }

    private void quitarFoto(FotoPar foto) {
        fotos.eliminar(f -> f == foto);
        marcarError(errorImagen, "");
        pintarFotos();
    }

    private void pintarFotos() {
        miniaturas.getChildren().clear();
        for (FotoPar foto : fotos) {
            Button quitar = new Button();
            quitar.getStyleClass().add("boton-quitar-foto");
            quitar.setGraphic(Iconos.cerrar(Iconos.OSCURO, 12));
            quitar.setTooltip(new Tooltip("Quitar foto"));
            quitar.setOnAction(e -> quitarFoto(foto));
            StackPane caja = new StackPane(recortada(foto, 116, 96, 20), quitar);
            StackPane.setAlignment(quitar, Pos.TOP_RIGHT);
            caja.getStyleClass().add("foto-resumen");
            caja.setMinSize(120, 100);
            caja.setMaxSize(120, 100);
            miniaturas.getChildren().add(caja);
        }
        for (int i = fotos.tamanio(); i < ArchivoImagenes.MAXIMO; i++) {
            Button vacio = new Button("+");
            vacio.getStyleClass().add("boton-foto-vacia");
            vacio.setTooltip(new Tooltip("Agregar otra foto"));
            vacio.setOnAction(e -> elegirFoto());
            miniaturas.getChildren().add(vacio);
        }
        fotoPrevia.getChildren().clear();
        if (fotos.estaVacia()) {
            Label sinFoto = new Label("Aún no hay foto");
            sinFoto.getStyleClass().add("texto-suave");
            fotoPrevia.getChildren().add(sinFoto);
        } else {
            fotoPrevia.getChildren().add(recortada(fotos.obtener(0), 244, 186, 24));
        }
    }

    private StackPane recortada(FotoPar foto, double ancho, double alto, double arco) {
        Image cargada = foto.esNueva() ? FotoProducto.cargar(foto.getArchivo()) : Recursos.imagenProducto(foto.getRuta());
        StackPane imagen = new StackPane(Recursos.vistaCubierta(cargada, ancho, alto));
        Rectangle recorte = new Rectangle(ancho, alto);
        recorte.setArcWidth(arco);
        recorte.setArcHeight(arco);
        imagen.setClip(recorte);
        imagen.setMaxSize(ancho, alto);
        return imagen;
    }

    private void pintarPrevia() {
        DatosProducto datos = datos();
        nombrePrevio.setText(datos.getNombre().isEmpty() ? "NOMBRE DEL MODELO" : datos.getNombre().toUpperCase());
        coloresPrevios.setText(datos.getColores().isEmpty() ? "Elige los colores" : datos.getTextoColores());
        precioPrevio.setText(Zapato.formatearPrecio(datos.getPrecio()));
        campoStock.setTexto(datos.getPares() + (datos.getPares() == 1 ? " par" : " pares"));
    }

    private DatosProducto datos() {
        ArrayList<TallaStock> stock = new ArrayList<>();
        for (Node nodo : tallas.getChildren()) {
            ContadorTalla contador = (ContadorTalla) nodo;
            stock.add(new TallaStock(contador.getTalla(), contador.getValor()));
        }
        String marca = campoMarca.getTexto();
        String digitos = Validaciones.digitos(campoPrecio.getTexto());
        return new DatosProducto(campoSku.getTexto(), campoNombre.getTexto(),
                OTRA_MARCA.equals(marca) ? campoOtraMarca.getTexto() : marca, campoCategoria.getTexto(),
                digitos.isEmpty() ? 0 : Integer.parseInt(digitos), areaDescripcion.getText(), interruptorVisible.isSelected(),
                coloresElegidos, stock);
    }

    private void guardar() {
        limpiarErrores();
        DatosProducto datos = datos();
        ListaEnlazada<ErrorCampo> errores = servicio.validar(datos, !fotos.estaVacia(), original == null);
        Node primero = null;
        for (ErrorCampo error : errores) {
            Node nodo = mostrarError((CampoProducto) error.getCampo(), error.getMensaje());
            primero = primero == null ? nodo : primero;
        }
        if (primero != null) {
            desplazarA(primero);
            mostrarMensaje(errores.tamanio() == 1 ? "Revisa el dato marcado." : "Revisa los " + errores.tamanio() + " datos marcados.");
            return;
        }
        ResultadoProducto resultado = original == null ? servicio.registrar(datos, fotos) : servicio.editar(datos, original, fotos);
        if (!resultado.esExito()) {
            mostrarMensaje(resultado.getMensaje());
            return;
        }
        barraController.actualizarAvisos();
        PopupCambiosGuardadosController.mostrar(resultado.getZapato(), resultado.getMensaje());
        if (original != null) {
            Navegador.volver();
            return;
        }
        AdminProductosController productos = Navegador.reemplazar(Ventana.ADMIN_PRODUCTOS);
        if (productos != null) {
            productos.elegir(resultado.getZapato().getSku());
        }
    }

    private Node mostrarError(CampoProducto campo, String texto) {
        return switch (campo) {
            case NOMBRE -> enCampo(campoNombre, texto);
            case SKU -> enCampo(campoSku, texto);
            case MARCA -> enCampo(OTRA_MARCA.equals(campoMarca.getTexto()) ? campoOtraMarca : campoMarca, texto);
            case PRECIO -> enCampo(campoPrecio, texto);
            case DESCRIPCION -> enEtiqueta(errorDescripcion, texto);
            case COLORES -> enEtiqueta(errorColores, texto);
            case TALLAS -> enEtiqueta(errorTallas, texto);
            case IMAGEN -> enEtiqueta(errorImagen, texto);
        };
    }

    private Node enCampo(CampoFormulario campo, String texto) {
        campo.mostrarError(texto);
        return campo;
    }

    private Node enEtiqueta(Label etiqueta, String texto) {
        marcarError(etiqueta, texto);
        return etiqueta;
    }

    private void limpiarErrores() {
        for (CampoFormulario campo : new CampoFormulario[]{campoNombre, campoSku, campoMarca, campoOtraMarca, campoPrecio}) {
            campo.limpiarError();
        }
        for (Label etiqueta : new Label[]{errorDescripcion, errorColores, errorTallas, errorImagen}) {
            marcarError(etiqueta, "");
        }
    }

    private void marcarError(Label etiqueta, String texto) {
        etiqueta.setText(texto);
        alternar(etiqueta, !texto.isEmpty());
    }

    private void mostrarMensaje(String texto) {
        marcarError(mensaje, texto);
    }

    private void desplazarA(Node nodo) {
        scroll.layout();
        Node contenido = scroll.getContent();
        double recorrido = contenido.getBoundsInLocal().getHeight() - scroll.getViewportBounds().getHeight();
        Bounds posicion = contenido.sceneToLocal(nodo.localToScene(nodo.getBoundsInLocal()));
        if (posicion != null && recorrido > 0) {
            scroll.setVvalue(Math.max(0, Math.min(1, (posicion.getMinY() - 120) / recorrido)));
        }
    }

    private void alternar(Node nodo, boolean visible) {
        nodo.setVisible(visible);
        nodo.setManaged(visible);
    }

    @Override
    public String capturarEstado() {
        return original == null ? "" : original.getSku();
    }

    @Override
    public void restaurarEstado(String estado) {
        editar(new CatalogoServicio().buscarPorSku(estado));
    }
}
