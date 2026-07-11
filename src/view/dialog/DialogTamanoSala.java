package view.dialog;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.geom.RoundRectangle2D;

/**
 * Diálogo de configuración inicial del tamaño de la sala.
 * Incluye preview en vivo, sliders, presets y contador dinámico de butacas.
 */
public class DialogTamanoSala extends JDialog {

    private JTextField txtNombre;
    private JSlider    sliderFilas;
    private JSlider    sliderColumnas;
    private JLabel     lblValorFilas;
    private JLabel     lblValorColumnas;
    private JLabel     lblTotal;
    private PanelPreview panelPreview;
    private boolean    confirmado = false;

    public DialogTamanoSala() {
        super((Frame) null, "Crear Nueva Sala", true);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setResizable(false);
        setSize(560, 600);
        setLocationRelativeTo(null);
        inicializarComponentes();
    }

    private void inicializarComponentes() {
        // Panel raíz con fondo degradado
        JPanel root = new JPanel(new BorderLayout(0, 0)) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                GradientPaint gp = new GradientPaint(0, 0, new Color(10, 8, 28),
                        0, getHeight(), new Color(20, 16, 50));
                g2.setPaint(gp);
                g2.fillRect(0, 0, getWidth(), getHeight());
                g2.dispose();
            }
        };
        root.setBorder(new EmptyBorder(28, 32, 24, 32));

        root.add(crearEncabezado(),  BorderLayout.NORTH);
        root.add(crearCuerpo(),      BorderLayout.CENTER);
        root.add(crearPieBotones(),  BorderLayout.SOUTH);

        setContentPane(root);
    }

    // ── ENCABEZADO ──────────────────────────────────────────────────────────

    private JPanel crearEncabezado() {
        JPanel panel = new JPanel(new BorderLayout(0, 6));
        panel.setOpaque(false);
        panel.setBorder(new EmptyBorder(0, 0, 20, 0));

        JLabel titulo = new JLabel("Crear Nueva Sala");
        titulo.setFont(new Font("Segoe UI", Font.BOLD, 22));
        titulo.setForeground(Color.WHITE);

        JLabel subtitulo = new JLabel("Elige las dimensiones y previsualiza tu sala en tiempo real");
        subtitulo.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        subtitulo.setForeground(new Color(100, 116, 139));

        // Línea decorativa acento
        JPanel linea = new JPanel() {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                GradientPaint gp = new GradientPaint(0, 0, new Color(99, 102, 241),
                        200, 0, new Color(139, 92, 246, 0));
                g2.setPaint(gp);
                g2.fillRect(0, 0, getWidth(), getHeight());
                g2.dispose();
            }
        };
        linea.setPreferredSize(new Dimension(0, 2));
        linea.setOpaque(false);

        panel.add(titulo,    BorderLayout.NORTH);
        panel.add(subtitulo, BorderLayout.CENTER);
        panel.add(linea,     BorderLayout.SOUTH);
        return panel;
    }

    // ── CUERPO ───────────────────────────────────────────────────────────────

    private JPanel crearCuerpo() {
        JPanel panel = new JPanel(new BorderLayout(0, 18));
        panel.setOpaque(false);

        JPanel superior = new JPanel(new BorderLayout(0, 14));
        superior.setOpaque(false);
        superior.add(crearCampoNombre(), BorderLayout.NORTH);
        superior.add(crearPresets(),     BorderLayout.CENTER);

        panel.add(superior,       BorderLayout.NORTH);
        panel.add(crearSliders(), BorderLayout.CENTER);
        panel.add(crearPreview(), BorderLayout.SOUTH);
        return panel;
    }

    // ── CAMPO NOMBRE ─────────────────────────────────────────────────────────

    private JPanel crearCampoNombre() {
        JPanel panel = new JPanel(new BorderLayout(0, 6));
        panel.setOpaque(false);

        JLabel lbl = new JLabel("Nombre de la sala");
        lbl.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lbl.setForeground(new Color(148, 163, 184));
        panel.add(lbl, BorderLayout.NORTH);

        txtNombre = new JTextField("Sala Principal");
        txtNombre.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        txtNombre.setForeground(Color.WHITE);
        txtNombre.setBackground(new Color(22, 20, 52));
        txtNombre.setCaretColor(new Color(139, 92, 246));
        txtNombre.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(50, 46, 90), 1, true),
                new EmptyBorder(8, 12, 8, 12)));
        txtNombre.setOpaque(true);

        Color normalB = new Color(50, 46, 90);
        Color focusB  = new Color(99, 102, 241);
        txtNombre.addFocusListener(new FocusAdapter() {
            @Override public void focusGained(FocusEvent e) {
                txtNombre.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(focusB, 2, true),
                        new EmptyBorder(7, 11, 7, 11)));
            }
            @Override public void focusLost(FocusEvent e) {
                txtNombre.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(normalB, 1, true),
                        new EmptyBorder(8, 12, 8, 12)));
            }
        });

        panel.add(txtNombre, BorderLayout.CENTER);
        return panel;
    }

    // ── PRESETS ──────────────────────────────────────────────────────────────

    private JPanel crearPresets() {
        JPanel panel = new JPanel(new BorderLayout(0, 10));
        panel.setOpaque(false);

        JLabel lbl = new JLabel("Configuraciones rápidas");
        lbl.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lbl.setForeground(new Color(148, 163, 184));
        panel.add(lbl, BorderLayout.NORTH);

        JPanel botones = new JPanel(new GridLayout(1, 3, 10, 0));
        botones.setOpaque(false);

        botones.add(crearBotonPreset("Pequeña",  "5 × 6",  5,  6, new Color(34, 197, 94)));
        botones.add(crearBotonPreset("Mediana",  "8 × 10", 8, 10, new Color(99, 102, 241)));
        botones.add(crearBotonPreset("Grande",  "12 × 15",12, 15, new Color(239, 68, 68)));

        panel.add(botones, BorderLayout.CENTER);
        return panel;
    }

    private JPanel crearBotonPreset(String nombre, String dim, int filas, int cols, Color color) {
        JPanel card = new JPanel(new BorderLayout(0, 4)) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(color.getRed(), color.getGreen(), color.getBlue(), 20));
                g2.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 10, 10));
                g2.setColor(new Color(color.getRed(), color.getGreen(), color.getBlue(), 80));
                g2.setStroke(new BasicStroke(1f));
                g2.draw(new RoundRectangle2D.Float(0, 0, getWidth()-1, getHeight()-1, 10, 10));
                g2.dispose();
                super.paintComponent(g);
            }
        };
        card.setOpaque(false);
        card.setBorder(new EmptyBorder(10, 12, 10, 12));
        card.setCursor(new Cursor(Cursor.HAND_CURSOR));

        JLabel lblNombre = new JLabel(nombre, SwingConstants.CENTER);
        lblNombre.setFont(new Font("Segoe UI", Font.BOLD, 13));
        lblNombre.setForeground(color);

        JLabel lblDim = new JLabel(dim, SwingConstants.CENTER);
        lblDim.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblDim.setForeground(new Color(148, 163, 184));

        card.add(lblNombre, BorderLayout.NORTH);
        card.add(lblDim,    BorderLayout.SOUTH);

        card.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override public void mouseClicked(java.awt.event.MouseEvent e) {
                sliderFilas.setValue(filas);
                sliderColumnas.setValue(cols);
            }
        });

        return card;
    }

    // ── SLIDERS ──────────────────────────────────────────────────────────────

    private JPanel crearSliders() {
        JPanel panel = new JPanel(new GridLayout(2, 1, 0, 14));
        panel.setOpaque(false);

        sliderFilas    = crearSlider(5);
        sliderColumnas = crearSlider(6);
        lblValorFilas    = crearLabelValor("5");
        lblValorColumnas = crearLabelValor("6");

        ChangeListener onChange = e -> actualizarPreview();
        sliderFilas.addChangeListener(onChange);
        sliderColumnas.addChangeListener(onChange);

        panel.add(crearFilaSlider("Filas",    sliderFilas,    lblValorFilas));
        panel.add(crearFilaSlider("Columnas", sliderColumnas, lblValorColumnas));
        return panel;
    }

    private JPanel crearFilaSlider(String nombre, JSlider slider, JLabel lblValor) {
        JPanel panel = new JPanel(new BorderLayout(10, 4));
        panel.setOpaque(false);

        JLabel lbl = new JLabel(nombre);
        lbl.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lbl.setForeground(new Color(148, 163, 184));
        lbl.setPreferredSize(new Dimension(65, 20));

        JPanel fila = new JPanel(new BorderLayout(10, 0));
        fila.setOpaque(false);
        fila.add(lbl,     BorderLayout.WEST);
        fila.add(slider,  BorderLayout.CENTER);
        fila.add(lblValor, BorderLayout.EAST);

        panel.add(fila, BorderLayout.CENTER);
        return panel;
    }

    private JSlider crearSlider(int valorInicial) {
        JSlider slider = new JSlider(1, 15, valorInicial);
        slider.setOpaque(false);
        slider.setForeground(new Color(99, 102, 241));
        slider.setBackground(new Color(10, 8, 28));
        slider.setPaintTicks(true);
        slider.setMajorTickSpacing(7);
        slider.setMinorTickSpacing(1);
        slider.setPaintLabels(true);
        slider.setSnapToTicks(true);
        slider.setUI(new javax.swing.plaf.basic.BasicSliderUI(slider) {
            @Override public void paintTrack(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                Rectangle t = trackRect;
                // Track fondo
                g2.setColor(new Color(51, 65, 85));
                g2.fill(new RoundRectangle2D.Float(t.x, t.y + t.height/2 - 3, t.width, 6, 6, 6));
                // Track relleno hasta el thumb
                int filled = thumbRect.x + thumbRect.width / 2 - t.x;
                GradientPaint gp = new GradientPaint(t.x, 0, new Color(99,102,241),
                        t.x + filled, 0, new Color(139,92,246));
                g2.setPaint(gp);
                g2.fill(new RoundRectangle2D.Float(t.x, t.y + t.height/2 - 3, filled, 6, 6, 6));
                g2.dispose();
            }
            @Override public void paintThumb(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                int cx = thumbRect.x + thumbRect.width / 2;
                int cy = thumbRect.y + thumbRect.height / 2;
                g2.setColor(new Color(139, 92, 246));
                g2.fillOval(cx - 9, cy - 9, 18, 18);
                g2.setColor(Color.WHITE);
                g2.fillOval(cx - 5, cy - 5, 10, 10);
                g2.dispose();
            }
        });
        // Estilo labels del slider
        slider.setFont(new Font("Segoe UI", Font.PLAIN, 10));
        slider.setForeground(new Color(100, 116, 139));
        return slider;
    }

    private JLabel crearLabelValor(String val) {
        JLabel lbl = new JLabel(val, SwingConstants.CENTER) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                GradientPaint gp = new GradientPaint(0, 0, new Color(99, 102, 241),
                        getWidth(), 0, new Color(139, 92, 246));
                g2.setPaint(gp);
                g2.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 8, 8));
                g2.dispose();
                super.paintComponent(g);
            }
        };
        lbl.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lbl.setForeground(Color.WHITE);
        lbl.setOpaque(false);
        lbl.setPreferredSize(new Dimension(40, 32));
        return lbl;
    }

    // ── PREVIEW ──────────────────────────────────────────────────────────────

    private JPanel crearPreview() {
        JPanel wrapper = new JPanel(new BorderLayout(0, 10));
        wrapper.setOpaque(false);

        JPanel encabezado = new JPanel(new BorderLayout());
        encabezado.setOpaque(false);

        JLabel lblPreview = new JLabel("Vista previa de la sala");
        lblPreview.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lblPreview.setForeground(new Color(148, 163, 184));

        lblTotal = new JLabel("30 butacas en total", SwingConstants.RIGHT);
        lblTotal.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lblTotal.setForeground(new Color(99, 102, 241));

        encabezado.add(lblPreview, BorderLayout.WEST);
        encabezado.add(lblTotal,   BorderLayout.EAST);

        panelPreview = new PanelPreview(5, 6);
        panelPreview.setPreferredSize(new Dimension(0, 160));

        wrapper.add(encabezado,   BorderLayout.NORTH);
        wrapper.add(panelPreview, BorderLayout.CENTER);
        return wrapper;
    }

    private void actualizarPreview() {
        int f = sliderFilas.getValue();
        int c = sliderColumnas.getValue();
        lblValorFilas.setText(String.valueOf(f));
        lblValorColumnas.setText(String.valueOf(c));
        lblTotal.setText(f * c + " butacas en total");
        panelPreview.setDimensiones(f, c);
    }

    // ── PIE / BOTONES ────────────────────────────────────────────────────────

    private JPanel crearPieBotones() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setOpaque(false);
        panel.setBorder(new EmptyBorder(20, 0, 0, 0));

        JSeparator sep = new JSeparator();
        sep.setForeground(new Color(30, 28, 60));
        panel.add(sep, BorderLayout.NORTH);

        JPanel botones = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 12));
        botones.setOpaque(false);

        JButton btnCancelar = crearBoton("Cancelar", false);
        btnCancelar.addActionListener(e -> dispose());

        JButton btnCrear = crearBoton("Crear Sala  →", true);
        btnCrear.addActionListener(e -> { confirmado = true; dispose(); });

        botones.add(btnCancelar);
        botones.add(btnCrear);
        panel.add(botones, BorderLayout.CENTER);
        return panel;
    }

    private JButton crearBoton(String texto, boolean primario) {
        JButton btn = new JButton(texto) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                if (primario) {
                    GradientPaint gp = new GradientPaint(0, 0, new Color(99, 102, 241),
                            getWidth(), 0, new Color(139, 92, 246));
                    g2.setPaint(gp);
                    g2.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 8, 8));
                } else {
                    g2.setColor(new Color(30, 28, 60));
                    g2.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 8, 8));
                    g2.setColor(new Color(51, 65, 85));
                    g2.setStroke(new BasicStroke(1f));
                    g2.draw(new RoundRectangle2D.Float(0, 0, getWidth()-1, getHeight()-1, 8, 8));
                }
                g2.dispose();
                super.paintComponent(g);
            }
        };
        btn.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btn.setForeground(primario ? Color.WHITE : new Color(148, 163, 184));
        btn.setContentAreaFilled(false);
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setOpaque(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setPreferredSize(new Dimension(primario ? 140 : 100, 40));
        return btn;
    }

    // ── PANEL PREVIEW INTERNO ─────────────────────────────────────────────────

    private static class PanelPreview extends JPanel {
        private int filas;
        private int cols;

        PanelPreview(int filas, int cols) {
            this.filas = filas;
            this.cols  = cols;
            setOpaque(false);
        }

        void setDimensiones(int filas, int cols) {
            this.filas = filas;
            this.cols  = cols;
            repaint();
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            // Fondo de la tarjeta
            g2.setColor(new Color(18, 15, 45));
            g2.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 12, 12));
            g2.setColor(new Color(40, 36, 80));
            g2.setStroke(new BasicStroke(1f));
            g2.draw(new RoundRectangle2D.Float(0, 0, getWidth()-1, getHeight()-1, 12, 12));

            int margen  = 16;
            int gap     = 3;
            int areaW   = getWidth()  - margen * 2;
            int areaH   = getHeight() - margen * 2 - 20; // reservar espacio para pantalla
            int celW    = Math.min(22, (areaW - gap * (cols - 1)) / cols);
            int celH    = Math.min(16, (areaH - gap * (filas - 1)) / filas);
            int totalW  = cols  * celW + (cols  - 1) * gap;
            int totalH  = filas * celH + (filas - 1) * gap;
            int startX  = (getWidth()  - totalW) / 2;
            int startY  = (getHeight() - totalH) / 2 + 10;

            // Pantalla
            int pantallaW = Math.min(totalW, 120);
            int pantallaX = (getWidth() - pantallaW) / 2;
            GradientPaint gpPantalla = new GradientPaint(pantallaX, 0,
                    new Color(99, 102, 241, 180), pantallaX + pantallaW, 0,
                    new Color(139, 92, 246, 180));
            g2.setPaint(gpPantalla);
            g2.fill(new RoundRectangle2D.Float(pantallaX, startY - 18, pantallaW, 6, 4, 4));
            g2.setFont(new Font("Segoe UI", Font.PLAIN, 9));
            g2.setColor(new Color(148, 163, 184));
            String txtPantalla = "PANTALLA";
            FontMetrics fm = g2.getFontMetrics();
            g2.drawString(txtPantalla, (getWidth() - fm.stringWidth(txtPantalla)) / 2, startY - 22);

            // Butacas
            for (int i = 0; i < filas; i++) {
                for (int j = 0; j < cols; j++) {
                    int x = startX + j * (celW + gap);
                    int y = startY + i * (celH + gap);

                    // Sombra
                    g2.setColor(new Color(0, 0, 0, 40));
                    g2.fill(new RoundRectangle2D.Float(x + 1, y + 2, celW, celH, 4, 4));

                    // Respaldo
                    g2.setColor(new Color(34, 197, 94).darker());
                    g2.fill(new RoundRectangle2D.Float(x, y, celW, 4, 3, 3));

                    // Asiento
                    g2.setColor(new Color(34, 197, 94));
                    g2.fill(new RoundRectangle2D.Float(x, y + 3, celW, celH - 3, 4, 4));
                }
            }

            g2.dispose();
        }
    }

    // ── GETTERS ───────────────────────────────────────────────────────────────

    public boolean isConfirmado()         { return confirmado; }
    public int getFilasSeleccionadas()    { return sliderFilas.getValue(); }
    public int getColumnasSeleccionadas() { return sliderColumnas.getValue(); }
    public String getNombreSala() {
        String n = txtNombre != null ? txtNombre.getText().trim() : "";
        return n.isEmpty() ? "Sala Principal" : n;
    }
}
