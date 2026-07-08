package view;

import model.SalaCine;
import model.SalaFactory;
import service.GestorSalas;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.geom.RoundRectangle2D;

/**
 * Vista completa para crear una sala con vista previa de pasillos en tiempo real.
 */
public class PanelCrearSala extends JPanel {

    private final GestorSalas gestorSalas;
    private Runnable alTerminar;

    private JTextField campNombre;
    private JSpinner spinFilas;
    private JSpinner spinColumnas;
    private JLabel lblTotalCalc;
    private JLabel lblError;
    private JPanel panelPreviewGrid;

    public PanelCrearSala(GestorSalas gestorSalas) {
        this.gestorSalas = gestorSalas;
        setOpaque(false);
        setLayout(new GridBagLayout());
        setBorder(new EmptyBorder(20, 24, 20, 24));
        inicializarComponentes();
    }

    public void setAlTerminar(Runnable cb) {
        this.alTerminar = cb;
    }

    private void inicializarComponentes() {
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.BOTH;
        gbc.weighty = 1.0;

        // Formulario (lado izquierdo) - 40%
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 0.4;
        gbc.insets = new Insets(0, 0, 0, 16);
        add(crearPanelFormulario(), gbc);

        // Vista previa (lado derecho) - 60%
        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.weightx = 0.6;
        gbc.insets = new Insets(0, 16, 0, 0);
        add(crearPanelPreview(), gbc);
    }

    private JPanel crearPanelFormulario() {
        JPanel form = ComponenteUI.tarjeta();
        form.setLayout(new BoxLayout(form, BoxLayout.Y_AXIS));

        JLabel lblTitulo = ComponenteUI.subtitulo("Configuración de Sala");
        lblTitulo.setAlignmentX(Component.LEFT_ALIGNMENT);
        form.add(lblTitulo);
        form.add(Box.createVerticalStrut(16));

        form.add(crearEtiquetaCampo("Nombre de la sala"));
        form.add(Box.createVerticalStrut(6));
        campNombre = ComponenteUI.campo("Ej: Sala 1, Sala VIP...");
        campNombre.setMaximumSize(new Dimension(Integer.MAX_VALUE, 38));
        campNombre.setAlignmentX(Component.LEFT_ALIGNMENT);
        form.add(campNombre);
        form.add(Box.createVerticalStrut(18));

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
        form.add(gridDim);
        form.add(Box.createVerticalStrut(18));

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

        cardTotal.add(lblIcono);
        cardTotal.add(lblTexto);
        cardTotal.add(lblTotalCalc);
        form.add(cardTotal);
        form.add(Box.createVerticalStrut(14));

        lblError = new JLabel(" ");
        lblError.setFont(UIConstants.FUENTE_PEQUENA);
        lblError.setForeground(UIConstants.BTN_PELIGRO);
        lblError.setAlignmentX(Component.LEFT_ALIGNMENT);
        form.add(lblError);
        form.add(Box.createVerticalGlue());

        JPanel pie = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        pie.setOpaque(false);
        pie.setAlignmentX(Component.LEFT_ALIGNMENT);
        pie.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));

        JButton btnCancelar = ComponenteUI.botonSecundario("Cancelar");
        btnCancelar.setPreferredSize(new Dimension(100, 38));
        btnCancelar.addActionListener(e -> cancelar());

        JButton btnCrear = ComponenteUI.botonPrimario("✚ Crear Sala");
        btnCrear.setPreferredSize(new Dimension(120, 38));
        btnCrear.addActionListener(e -> crearSala());

        pie.add(btnCancelar);
        pie.add(btnCrear);
        form.add(pie);

        spinFilas.addChangeListener(e -> recalcularYActualizar());
        spinColumnas.addChangeListener(e -> recalcularYActualizar());

        return form;
    }

    private JPanel crearPanelPreview() {
        JPanel wrapper = ComponenteUI.tarjeta();
        wrapper.setLayout(new BorderLayout(0, 12));

        JLabel lblTitulo = ComponenteUI.subtitulo("Vista Previa de la Distribución");
        wrapper.add(lblTitulo, BorderLayout.NORTH);

        JPanel pantalla = new JPanel(new BorderLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                GradientPaint gp = new GradientPaint(0, 0, new Color(80, 80, 120),
                        getWidth(), 0, new Color(50, 50, 90));
                g2.setPaint(gp);
                g2.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 4, 4));
                g2.dispose();
            }
        };
        pantalla.setOpaque(false);
        pantalla.setPreferredSize(new Dimension(0, 20));
        JLabel lblPantalla = new JLabel("P A N T A L L A", SwingConstants.CENTER);
        lblPantalla.setFont(new Font("Consolas", Font.BOLD, 10));
        lblPantalla.setForeground(new Color(200, 205, 230));
        pantalla.add(lblPantalla, BorderLayout.CENTER);

        JPanel content = new JPanel(new BorderLayout(0, 10));
        content.setOpaque(false);
        content.add(pantalla, BorderLayout.NORTH);

        panelPreviewGrid = new JPanel(new GridBagLayout());
        panelPreviewGrid.setOpaque(false);
        
        JScrollPane scroll = new JScrollPane(panelPreviewGrid);
        scroll.setOpaque(false);
        scroll.getViewport().setOpaque(false);
        scroll.setBorder(BorderFactory.createEmptyBorder());

        content.add(scroll, BorderLayout.CENTER);
        wrapper.add(content, BorderLayout.CENTER);

        recalcularYActualizar();
        return wrapper;
    }

    private void recalcularYActualizar() {
        int f = (int) spinFilas.getValue();
        int c = (int) spinColumnas.getValue();
        lblTotalCalc.setText(String.valueOf(f * c));
        lblError.setText(" ");
        actualizarVistaPrevia(f, c);
    }

    private void actualizarVistaPrevia(int filas, int columnas) {
        panelPreviewGrid.removeAll();
        GridBagConstraints gbc = new GridBagConstraints();

        gbc.gridy = 0;
        gbc.insets = new Insets(2, 3, 2, 3);
        gbc.gridx = 0;
        panelPreviewGrid.add(new JLabel(""), gbc);

        for (int j = 0; j < columnas; j++) {
            gbc.gridx = j + 1;
            int rightInset = 3;
            if (columnas == 6 && j == 2) rightInset = 20;
            else if (columnas >= 7 && (j == 1 || j == columnas - 3)) rightInset = 20;
            
            gbc.insets = new Insets(2, 3, 2, rightInset);
            JLabel lblCol = new JLabel("C" + (j + 1), SwingConstants.CENTER);
            lblCol.setFont(new Font("Segoe UI", Font.BOLD, 9));
            gbc.insets = new Insets(2, 3, 2, rightInset); // reuse inset
            lblCol.setForeground(UIConstants.TEXTO_TENUE);
            lblCol.setPreferredSize(new Dimension(32, 14));
            panelPreviewGrid.add(lblCol, gbc);
        }

        for (int i = 0; i < filas; i++) {
            gbc.gridx = 0;
            gbc.gridy = i + 1;
            gbc.insets = new Insets(2, 3, 2, 3);
            JLabel lblFila = new JLabel("F" + (i + 1), SwingConstants.CENTER);
            lblFila.setFont(new Font("Segoe UI", Font.BOLD, 9));
            lblFila.setForeground(UIConstants.TEXTO_TENUE);
            lblFila.setPreferredSize(new Dimension(16, 32));
            panelPreviewGrid.add(lblFila, gbc);

            for (int j = 0; j < columnas; j++) {
                gbc.gridx = j + 1;
                int rightInset = 3;
                if (columnas == 6 && j == 2) rightInset = 20;
                else if (columnas >= 7 && (j == 1 || j == columnas - 3)) rightInset = 20;

                gbc.insets = new Insets(2, 3, 2, rightInset);
                int numeroAsiento = (i * columnas) + j + 1;
                panelPreviewGrid.add(crearMiniButaca(numeroAsiento), gbc);
            }
        }

        panelPreviewGrid.revalidate();
        panelPreviewGrid.repaint();
    }

    private JComponent crearMiniButaca(int numero) {
        JPanel btn = new JPanel(new BorderLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(UIConstants.COLOR_LIBRE);
                g2.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 6, 6));
                g2.dispose();
            }
        };
        btn.setOpaque(false);
        btn.setPreferredSize(new Dimension(32, 32));
        
        JLabel lbl = new JLabel(String.valueOf(numero), SwingConstants.CENTER);
        lbl.setFont(new Font("Segoe UI", Font.BOLD, 10));
        lbl.setForeground(Color.WHITE);
        btn.add(lbl, BorderLayout.CENTER);
        return btn;
    }

    private void crearSala() {
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

        int f = (int) spinFilas.getValue();
        int c = (int) spinColumnas.getValue();

        SalaCine nuevaSala = SalaFactory.crearSala(nombre, f * c, f, c);
        gestorSalas.agregarSala(nuevaSala);

        limpiarCampos();
        if (alTerminar != null) alTerminar.run();
    }

    private void cancelar() {
        limpiarCampos();
        if (alTerminar != null) alTerminar.run();
    }

    public void limpiarCampos() {
        campNombre.setText("");
        spinFilas.setValue(5);
        spinColumnas.setValue(6);
        lblError.setText(" ");
        recalcularYActualizar();
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
}
