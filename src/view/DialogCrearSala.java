package view;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

/**
 * Diálogo modal para crear una nueva sala de cine.
 * Solicita: nombre, filas, columnas. Calcula y muestra el total automáticamente.
 * Valida que todos los campos sean correctos antes de confirmar.
 *
 * PARADIGMA: Orientado a Objetos — JDialog modal con validación inline
 * SOLID: SRP — responsabilidad única: recoger y validar datos de creación de sala
 */
public class DialogCrearSala extends JDialog {

    private JTextField campNombre;
    private JSpinner spinFilas;
    private JSpinner spinColumnas;
    private JLabel lblTotalCalc;
    private JLabel lblError;
    private boolean confirmado = false;

    public DialogCrearSala(Frame padre) {
        super(padre, "Nueva Sala de Cine", true);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setResizable(false);
        inicializarComponentes();
        pack();
        setLocationRelativeTo(padre);
    }

    private void inicializarComponentes() {
        JPanel root = new JPanel(new BorderLayout(0, 0));
        root.setBackground(UIConstants.BG_PANEL);
        root.setBorder(new EmptyBorder(0, 0, 0, 0));

        root.add(crearEncabezado(), BorderLayout.NORTH);
        root.add(crearCuerpo(), BorderLayout.CENTER);
        root.add(crearPieBotones(), BorderLayout.SOUTH);

        setContentPane(root);
        setPreferredSize(new Dimension(440, 500));
    }

    private JPanel crearEncabezado() {
        JPanel header = new JPanel(new BorderLayout()) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(UIConstants.BG_HEADER);
                g2.fillRect(0, 0, getWidth(), getHeight());
                g2.setColor(UIConstants.ACENTO);
                g2.fillRect(0, getHeight() - 2, getWidth(), 2);
                g2.dispose();
            }
        };
        header.setBorder(new EmptyBorder(18, 24, 18, 24));
        header.setPreferredSize(new Dimension(0, 78));

        JLabel lblTitulo = new JLabel("🎭  Nueva Sala de Cine");
        lblTitulo.setFont(UIConstants.FUENTE_TITULO);
        lblTitulo.setForeground(UIConstants.TEXTO_PRIMARIO);
        JLabel lblSub = new JLabel("Complete los datos para crear la sala");
        lblSub.setFont(UIConstants.FUENTE_PEQUENA);
        lblSub.setForeground(UIConstants.TEXTO_TENUE);

        JPanel textos = new JPanel(new GridLayout(2, 1, 0, 3));
        textos.setOpaque(false);
        textos.add(lblTitulo);
        textos.add(lblSub);
        header.add(textos, BorderLayout.CENTER);
        return header;
    }

    private JPanel crearCuerpo() {
        JPanel body = new JPanel();
        body.setBackground(UIConstants.BG_PANEL);
        body.setLayout(new BoxLayout(body, BoxLayout.Y_AXIS));
        body.setBorder(new EmptyBorder(20, 24, 10, 24));

        // Campo nombre
        body.add(crearEtiquetaCampo("Nombre de la sala"));
        body.add(Box.createVerticalStrut(6));
        campNombre = ComponenteUI.campo("Ej: Sala 1, Sala VIP...");
        campNombre.setMaximumSize(new Dimension(Integer.MAX_VALUE, 38));
        campNombre.setAlignmentX(Component.LEFT_ALIGNMENT);
        body.add(campNombre);
        body.add(Box.createVerticalStrut(18));

        // Filas y Columnas en grid 2 columnas
        JPanel gridDim = new JPanel(new GridLayout(1, 2, 14, 0));
        gridDim.setOpaque(false);
        gridDim.setAlignmentX(Component.LEFT_ALIGNMENT);
        gridDim.setMaximumSize(new Dimension(Integer.MAX_VALUE, 70));

        JPanel panelFilas = new JPanel(new BorderLayout(0, 6));
        panelFilas.setOpaque(false);
        panelFilas.add(crearEtiquetaCampo("Filas  (1 – 15)"), BorderLayout.NORTH);
        spinFilas = ComponenteUI.spinner(1, 15, 5);
        panelFilas.add(spinFilas, BorderLayout.CENTER);

        JPanel panelCols = new JPanel(new BorderLayout(0, 6));
        panelCols.setOpaque(false);
        panelCols.add(crearEtiquetaCampo("Columnas  (1 – 15)"), BorderLayout.NORTH);
        spinColumnas = ComponenteUI.spinner(1, 15, 6);
        panelCols.add(spinColumnas, BorderLayout.CENTER);

        gridDim.add(panelFilas);
        gridDim.add(panelCols);
        body.add(gridDim);
        body.add(Box.createVerticalStrut(16));

        // Card de total calculado automáticamente (RF3: se calcula, no se edita)
        JPanel cardTotal = ComponenteUI.tarjetaAcento();
        cardTotal.setLayout(new FlowLayout(FlowLayout.LEFT, 10, 0));
        cardTotal.setAlignmentX(Component.LEFT_ALIGNMENT);
        cardTotal.setMaximumSize(new Dimension(Integer.MAX_VALUE, 52));

        JLabel lblIcono = new JLabel("🪑");
        lblIcono.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 18));
        JLabel lblTexto = ComponenteUI.cuerpo("Total de butacas:");
        lblTotalCalc = new JLabel("30");
        lblTotalCalc.setFont(UIConstants.FUENTE_TITULO);
        lblTotalCalc.setForeground(UIConstants.TEXTO_ACENTO);
        JLabel lblAuto = ComponenteUI.pequena("  (calculado automáticamente)");

        cardTotal.add(lblIcono);
        cardTotal.add(lblTexto);
        cardTotal.add(lblTotalCalc);
        cardTotal.add(lblAuto);
        body.add(cardTotal);
        body.add(Box.createVerticalStrut(14));

        // Label de error
        lblError = new JLabel(" ");
        lblError.setFont(UIConstants.FUENTE_PEQUENA);
        lblError.setForeground(UIConstants.BTN_PELIGRO);
        lblError.setAlignmentX(Component.LEFT_ALIGNMENT);
        body.add(lblError);

        // Listeners para recalcular total (PARADIGMA FUNCIONAL: lambdas)
        spinFilas.addChangeListener(e -> recalcularTotal());
        spinColumnas.addChangeListener(e -> recalcularTotal());
        recalcularTotal();

        return body;
    }

    private JPanel crearPieBotones() {
        JPanel pie = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 12));
        pie.setBackground(UIConstants.BG_HEADER);
        pie.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, UIConstants.BORDE));

        JButton btnCancelar = ComponenteUI.botonSecundario("Cancelar");
        btnCancelar.setPreferredSize(new Dimension(110, 38));
        btnCancelar.addActionListener(e -> dispose());

        JButton btnCrear = ComponenteUI.botonPrimario("✚  Crear Sala");
        btnCrear.setPreferredSize(new Dimension(130, 38));
        btnCrear.addActionListener(e -> onConfirmar());

        pie.add(btnCancelar);
        pie.add(btnCrear);
        return pie;
    }

    private void recalcularTotal() {
        int f = (int) spinFilas.getValue();
        int c = (int) spinColumnas.getValue();
        lblTotalCalc.setText(String.valueOf(f * c));
        lblError.setText(" ");
    }

    private void onConfirmar() {
        String nombre = campNombre.getText().trim();
        if (nombre.isEmpty()) {
            mostrarError("El nombre de la sala no puede estar vacío.");
            campNombre.requestFocus();
            return;
        }
        if (nombre.length() > 40) {
            mostrarError("El nombre no puede superar los 40 caracteres.");
            return;
        }
        confirmado = true;
        dispose();
    }

    private void mostrarError(String msg) {
        lblError.setText("⚠  " + msg);
    }

    private JLabel crearEtiquetaCampo(String texto) {
        JLabel lbl = new JLabel(texto);
        lbl.setFont(UIConstants.FUENTE_NEGRITA);
        lbl.setForeground(UIConstants.TEXTO_SECUNDARIO);
        lbl.setAlignmentX(Component.LEFT_ALIGNMENT);
        return lbl;
    }

    // =========================================================================
    // GETTERS
    // =========================================================================

    public boolean isConfirmado()       { return confirmado; }
    public String  getNombreSala()      { return campNombre.getText().trim(); }
    public int     getFilas()           { return (int) spinFilas.getValue(); }
    public int     getColumnas()        { return (int) spinColumnas.getValue(); }
    public int     getTotalAsientos()   { return getFilas() * getColumnas(); }
}
