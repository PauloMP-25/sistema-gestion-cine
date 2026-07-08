package view;

import model.SalaCine;
import model.SalaFactory;
import service.GestorSalas;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.RoundRectangle2D;
import java.util.List;

public class DialogGestionSalas extends JDialog {

    private final GestorSalas gestorSalas;
    private SalaCine salaSeleccionada = null;
    private boolean  confirmado       = false;
    private JButton  btnEntrar;
    private JPanel   listPanel;
    private JPanel   cardActiva = null;

    private static final Color BORDER_NORMAL   = new Color(45, 42, 80);
    private static final Color BORDER_HOVER    = new Color(70, 66, 110);
    private static final Color BORDER_SELECTED = new Color(99, 102, 241);

    public DialogGestionSalas(GestorSalas gestorSalas) {
        super((Frame) null, "Gestión de Salas", true);
        this.gestorSalas = gestorSalas;
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            @Override public void windowClosing(WindowEvent e) { dispose(); }
        });
        setSize(530, 590);
        setResizable(false);
        setLocationRelativeTo(null);
        inicializarComponentes();
    }

    // ── Layout ─────────────────────────────────────────────────────────────────

    private void inicializarComponentes() {
        JPanel root = new JPanel(new BorderLayout(0, 0)) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                        RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setPaint(new GradientPaint(0, 0, new Color(10, 8, 28),
                        0, getHeight(), new Color(20, 16, 50)));
                g2.fillRect(0, 0, getWidth(), getHeight());
                g2.dispose();
            }
        };
        root.setBorder(new EmptyBorder(28, 32, 24, 32));
        root.add(crearEncabezado(), BorderLayout.NORTH);
        root.add(crearCuerpo(),     BorderLayout.CENTER);
        root.add(crearPie(),        BorderLayout.SOUTH);
        setContentPane(root);
    }

    private JPanel crearEncabezado() {
        JPanel panel = new JPanel(new BorderLayout(0, 6));
        panel.setOpaque(false);
        panel.setBorder(new EmptyBorder(0, 0, 20, 0));

        JLabel titulo = new JLabel("Gestión de Salas");
        titulo.setFont(new Font("Segoe UI", Font.BOLD, 22));
        titulo.setForeground(Color.WHITE);

        JLabel subtitulo = new JLabel(
                "Crea, renombra o elimina salas  •  Solo Administrador");
        subtitulo.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        subtitulo.setForeground(new Color(100, 116, 139));

        JPanel linea = new JPanel() {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setPaint(new GradientPaint(0, 0, new Color(99, 102, 241),
                        200, 0, new Color(99, 102, 241, 0)));
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

    private JScrollPane crearCuerpo() {
        listPanel = new JPanel();
        listPanel.setLayout(new BoxLayout(listPanel, BoxLayout.Y_AXIS));
        listPanel.setOpaque(false);
        listPanel.setBorder(new EmptyBorder(4, 0, 4, 0));
        refreshLista();

        JScrollPane scroll = new JScrollPane(listPanel,
                JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
                JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scroll.setOpaque(false);
        scroll.getViewport().setOpaque(false);
        scroll.setBorder(null);
        scroll.getVerticalScrollBar().setUnitIncrement(16);
        estilizarScrollBar(scroll.getVerticalScrollBar());
        return scroll;
    }

    // ── Lista de salas ─────────────────────────────────────────────────────────

    private void refreshLista() {
        listPanel.removeAll();
        List<SalaCine> salas = gestorSalas.listarSalas();

        if (salas.isEmpty()) {
            JLabel vacio = new JLabel(
                    "No hay salas. Crea una con \"+ Nueva sala\".",
                    SwingConstants.CENTER);
            vacio.setFont(new Font("Segoe UI", Font.PLAIN, 13));
            vacio.setForeground(new Color(100, 116, 139));
            vacio.setAlignmentX(Component.CENTER_ALIGNMENT);
            vacio.setBorder(new EmptyBorder(50, 0, 0, 0));
            listPanel.add(vacio);
        } else {
            for (SalaCine sala : salas) {
                listPanel.add(crearTarjetaSala(sala));
                listPanel.add(Box.createVerticalStrut(10));
            }
        }

        // Si la sala seleccionada fue eliminada, resetear
        if (salaSeleccionada != null && !salas.contains(salaSeleccionada)) {
            salaSeleccionada = null;
            cardActiva       = null;
            if (btnEntrar != null) { btnEntrar.setEnabled(false); btnEntrar.repaint(); }
        }

        listPanel.revalidate();
        listPanel.repaint();
    }

    private JPanel crearTarjetaSala(SalaCine sala) {
        boolean[] selected  = {false};
        Color[]   borderCol = {BORDER_NORMAL};

        JPanel card = new JPanel(new BorderLayout(12, 0)) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                        RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(selected[0] ? new Color(35, 30, 75) : new Color(22, 20, 52));
                g2.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 12, 12));
                g2.setColor(borderCol[0]);
                g2.setStroke(new BasicStroke(selected[0] ? 2f : 1f));
                g2.draw(new RoundRectangle2D.Float(0.5f, 0.5f,
                        getWidth() - 1, getHeight() - 1, 12, 12));
                if (selected[0]) {
                    g2.setColor(new Color(99, 102, 241));
                    g2.fill(new RoundRectangle2D.Float(0, 10, 4, getHeight() - 20, 4, 4));
                }
                g2.dispose();
                super.paintComponent(g);
            }
        };
        card.setOpaque(false);
        card.setBorder(new EmptyBorder(10, 18, 10, 14));
        card.setAlignmentX(Component.LEFT_ALIGNMENT);
        card.setMaximumSize(new Dimension(Integer.MAX_VALUE, 92));
        card.setCursor(new Cursor(Cursor.HAND_CURSOR));
        card.putClientProperty("selected",  selected);
        card.putClientProperty("borderCol", borderCol);

        // Info (izquierda)
        JPanel info = new JPanel(new BorderLayout(0, 3));
        info.setOpaque(false);

        JLabel lblNombre = new JLabel(sala.getNombre());
        lblNombre.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lblNombre.setForeground(Color.WHITE);

        JLabel lblDims = new JLabel(sala.getFilas() + " filas × " + sala.getCols() +
                "  •  " + (sala.getFilas() * sala.getCols()) + " butacas");
        lblDims.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblDims.setForeground(new Color(100, 116, 139));

        int[] est = contarEstados(sala);
        JLabel lblStats = new JLabel(
            "<html><font color='#4ade80'>" + est[0] + " libre" + (est[0] != 1 ? "s" : "") + "</font>" +
            "&nbsp;·&nbsp;<font color='#fbbf24'>" + est[1] + " reservada" + (est[1] != 1 ? "s" : "") + "</font>" +
            "&nbsp;·&nbsp;<font color='#f87171'>" + est[2] + " ocupada" + (est[2] != 1 ? "s" : "") + "</font></html>");
        lblStats.setFont(new Font("Segoe UI", Font.PLAIN, 11));

        info.add(lblNombre, BorderLayout.NORTH);
        info.add(lblDims,   BorderLayout.CENTER);
        info.add(lblStats,  BorderLayout.SOUTH);

        // Botones acción (derecha)
        JPanel acciones = new JPanel(new GridLayout(1, 2, 6, 0));
        acciones.setOpaque(false);

        JButton btnRen = crearBotonAccion("✎", new Color(59, 130, 180));
        JButton btnDel = crearBotonAccion("✕", new Color(185, 50, 50));
        btnRen.setToolTipText("Renombrar sala");
        btnDel.setToolTipText("Eliminar sala");
        btnRen.addActionListener(e -> onRenombrar(sala, lblNombre));
        btnDel.addActionListener(e -> onEliminar(sala));
        acciones.add(btnRen);
        acciones.add(btnDel);

        card.add(info,     BorderLayout.CENTER);
        card.add(acciones, BorderLayout.EAST);

        // Selección al hacer clic en la tarjeta
        card.addMouseListener(new MouseAdapter() {
            @Override public void mouseClicked(MouseEvent e) {
                seleccionar(sala, card, selected, borderCol);
            }
            @Override public void mouseEntered(MouseEvent e) {
                if (!selected[0]) { borderCol[0] = BORDER_HOVER; card.repaint(); }
            }
            @Override public void mouseExited(MouseEvent e) {
                if (!selected[0]) { borderCol[0] = BORDER_NORMAL; card.repaint(); }
            }
        });

        return card;
    }

    // ── Acciones ───────────────────────────────────────────────────────────────

    private void onRenombrar(SalaCine sala, JLabel lblNombre) {
        String nuevo = JOptionPane.showInputDialog(
                this, "Nuevo nombre:", sala.getNombre());
        if (nuevo != null && !nuevo.trim().isEmpty()) {
            sala.setNombre(nuevo.trim());
            lblNombre.setText(sala.getNombre());
        }
    }

    private void onEliminar(SalaCine sala) {
        int op = JOptionPane.showConfirmDialog(this,
                "¿Eliminar \"" + sala.getNombre() + "\"?\nEsta acción no se puede deshacer.",
                "Eliminar sala", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
        if (op == JOptionPane.YES_OPTION) {
            gestorSalas.eliminarPorId(sala.getId());
            refreshLista();
        }
    }

    private void onCrearSala() {
        DialogTamanoSala d = new DialogTamanoSala();
        d.setVisible(true);
        if (d.isConfirmado()) {
            int f = d.getFilasSeleccionadas(), c = d.getColumnasSeleccionadas();
            gestorSalas.agregarSala(SalaFactory.crearSala(d.getNombreSala(), f * c, f, c));
            refreshLista();
        }
    }

    private static int[] contarEstados(SalaCine sala) {
        int libres = 0, reservadas = 0, ocupadas = 0;
        for (var fila : sala.getButacas()) {
            for (var b : fila) {
                switch (b.getEstado()) {
                    case LIBRE:     libres++;     break;
                    case RESERVADO: reservadas++; break;
                    case OCUPADO:   ocupadas++;   break;
                }
            }
        }
        return new int[]{libres, reservadas, ocupadas};
    }

    private void seleccionar(SalaCine sala, JPanel card,
                             boolean[] selected, Color[] borderCol) {
        if (cardActiva != null && cardActiva != card) {
            boolean[] ps = (boolean[]) cardActiva.getClientProperty("selected");
            Color[]   pb = (Color[])   cardActiva.getClientProperty("borderCol");
            if (ps != null) ps[0] = false;
            if (pb != null) pb[0] = BORDER_NORMAL;
            cardActiva.repaint();
        }
        selected[0]      = true;
        borderCol[0]     = BORDER_SELECTED;
        cardActiva       = card;
        salaSeleccionada = sala;
        card.repaint();
        btnEntrar.setEnabled(true);
        btnEntrar.repaint();
    }

    // ── Pie ────────────────────────────────────────────────────────────────────

    private JPanel crearPie() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setOpaque(false);
        panel.setBorder(new EmptyBorder(16, 0, 0, 0));

        JSeparator sep = new JSeparator();
        sep.setForeground(new Color(30, 28, 60));
        panel.add(sep, BorderLayout.NORTH);

        JPanel botones = new JPanel(new BorderLayout());
        botones.setOpaque(false);
        botones.setBorder(new EmptyBorder(12, 0, 0, 0));

        JButton btnCrear = crearBotonSecundario("+ Nueva sala");
        btnCrear.addActionListener(e -> onCrearSala());

        btnEntrar = crearBotonPrimario("Entrar  →");
        btnEntrar.setEnabled(false);
        btnEntrar.addActionListener(e -> {
            if (salaSeleccionada != null) { confirmado = true; dispose(); }
        });

        JPanel izq = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        izq.setOpaque(false);
        izq.add(btnCrear);

        JPanel der = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
        der.setOpaque(false);
        der.add(btnEntrar);

        botones.add(izq, BorderLayout.WEST);
        botones.add(der, BorderLayout.EAST);
        panel.add(botones, BorderLayout.CENTER);
        return panel;
    }

    // ── Helpers visuales ───────────────────────────────────────────────────────

    private JButton crearBotonAccion(String texto, Color color) {
        boolean[] hov = {false};
        JButton btn = new JButton(texto) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                        RenderingHints.VALUE_ANTIALIAS_ON);
                int alpha = hov[0] ? 230 : 160;
                g2.setColor(new Color(
                        color.getRed(), color.getGreen(), color.getBlue(), alpha));
                g2.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 6, 6));
                g2.dispose();
                super.paintComponent(g);
            }
        };
        btn.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btn.setForeground(Color.WHITE);
        btn.setContentAreaFilled(false);
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setOpaque(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setPreferredSize(new Dimension(32, 30));
        btn.addMouseListener(new MouseAdapter() {
            @Override public void mouseEntered(MouseEvent e) { hov[0] = true;  btn.repaint(); }
            @Override public void mouseExited (MouseEvent e) { hov[0] = false; btn.repaint(); }
        });
        return btn;
    }

    private JButton crearBotonPrimario(String texto) {
        boolean[] hov = {false};
        JButton btn = new JButton(texto) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                        RenderingHints.VALUE_ANTIALIAS_ON);
                if (!isEnabled()) {
                    g2.setColor(new Color(40, 38, 68));
                    g2.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 8, 8));
                } else {
                    Color c1 = hov[0] ? new Color(109, 112, 255) : new Color(99, 102, 241);
                    Color c2 = hov[0] ? new Color(79,  72,  215) : new Color(67,  56,  202);
                    g2.setPaint(new GradientPaint(0, 0, c1, getWidth(), 0, c2));
                    g2.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 8, 8));
                }
                g2.dispose();
                super.paintComponent(g);
            }
        };
        estiloBoton(btn, Color.WHITE);
        btn.addMouseListener(new MouseAdapter() {
            @Override public void mouseEntered(MouseEvent e) { hov[0] = true;  btn.repaint(); }
            @Override public void mouseExited (MouseEvent e) { hov[0] = false; btn.repaint(); }
        });
        return btn;
    }

    private JButton crearBotonSecundario(String texto) {
        boolean[] hov = {false};
        JButton btn = new JButton(texto) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                        RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(hov[0] ? new Color(38, 35, 72) : new Color(28, 25, 58));
                g2.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 8, 8));
                g2.setColor(new Color(70, 66, 110));
                g2.setStroke(new BasicStroke(1f));
                g2.draw(new RoundRectangle2D.Float(0, 0, getWidth() - 1, getHeight() - 1, 8, 8));
                g2.dispose();
                super.paintComponent(g);
            }
        };
        estiloBoton(btn, new Color(148, 163, 184));
        btn.addMouseListener(new MouseAdapter() {
            @Override public void mouseEntered(MouseEvent e) { hov[0] = true;  btn.repaint(); }
            @Override public void mouseExited (MouseEvent e) { hov[0] = false; btn.repaint(); }
        });
        return btn;
    }

    private void estiloBoton(JButton btn, Color fg) {
        btn.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btn.setForeground(fg);
        btn.setContentAreaFilled(false);
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setOpaque(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setPreferredSize(new Dimension(140, 40));
    }

    private static void estilizarScrollBar(JScrollBar sb) {
        sb.setBackground(new Color(14, 12, 40));
        sb.setUI(new javax.swing.plaf.basic.BasicScrollBarUI() {
            @Override protected void configureScrollBarColors() {
                thumbColor = new Color(70, 66, 110);
                trackColor = new Color(14, 12, 40);
            }
            @Override protected JButton createDecreaseButton(int o) { return btn0(); }
            @Override protected JButton createIncreaseButton(int o) { return btn0(); }
            private JButton btn0() {
                JButton b = new JButton();
                b.setPreferredSize(new Dimension(0, 0));
                b.setMinimumSize(new Dimension(0, 0));
                b.setMaximumSize(new Dimension(0, 0));
                return b;
            }
            @Override protected void paintThumb(Graphics g, JComponent c, Rectangle r) {
                if (r.isEmpty()) return;
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                        RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(isDragging
                        ? new Color(139, 92, 246)
                        : new Color(70, 66, 110));
                g2.fill(new RoundRectangle2D.Float(
                        r.x + 2, r.y + 2, r.width - 4, r.height - 4, 6, 6));
                g2.dispose();
            }
            @Override protected void paintTrack(Graphics g, JComponent c, Rectangle r) {
                g.setColor(new Color(14, 12, 40));
                g.fillRect(r.x, r.y, r.width, r.height);
            }
        });
    }

    // ── Getters ────────────────────────────────────────────────────────────────

    public boolean  isConfirmado()        { return confirmado; }
    public SalaCine getSalaSeleccionada() { return salaSeleccionada; }
}
