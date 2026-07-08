package view;

import model.Rol;
import model.SalaCine;
import service.GestorSalas;
import service.interfaces.ISalaQuery;
import service.interfaces.ISalaService;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.plaf.basic.BasicScrollBarUI;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.RoundRectangle2D;

public class MainFrame extends JFrame {

    private final ISalaService salaService;
    private final ISalaQuery   salaQuery;
    private final Rol          rol;

    private PanelSala    panelSala;
    private PanelControl panelControl;

    private JLabel  lblCardLibres, lblCardReservadas, lblCardOcupadas;
    private JButton btnMaximizar;                              // botón maximize/restore

    private int    statLibres = 0, statReservadas = 0, statOcupadas = 0;
    private JPanel barraOcupacion;
    private boolean  logout     = false;
    private boolean  cambioSala = false;
    private SalaCine nuevaSala  = null;

    private final String      nombreSala;
    private final GestorSalas gestorSalas;

    public MainFrame(ISalaService salaService, ISalaQuery salaQuery, Rol rol,
                     String nombreSala, GestorSalas gestorSalas) {
        this.salaService  = salaService;
        this.salaQuery    = salaQuery;
        this.rol          = rol;
        this.nombreSala   = nombreSala;
        this.gestorSalas  = gestorSalas;
        configurarVentana();
        inicializarComponentes();
        configurarEventos();
    }

    // ── CONFIGURACIÓN ────────────────────────────────────────────────────────

    private void configurarVentana() {
        setTitle("Sistema de Gestión de Butacas de Cine");
        setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
        setUndecorated(true);
        setSize(980, 700);
        setMinimumSize(new Dimension(860, 580));
        setLocationRelativeTo(null);
        getContentPane().setBackground(UIConstants.BG_FONDO);
        setLayout(new BorderLayout());
        getRootPane().setBorder(BorderFactory.createLineBorder(new Color(50, 46, 90), 1));
    }

    private void inicializarComponentes() {
        add(crearHeader(),         BorderLayout.NORTH);
        add(crearPanelCentral(),   BorderLayout.CENTER);
        add(crearBarraOcupacion(), BorderLayout.SOUTH);
        actualizarStats();
    }

    // ── HEADER ──────────────────────────────────────────────────────────────

    private JPanel crearHeader() {
        JPanel header = new JPanel(new BorderLayout(20, 0)) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setPaint(new GradientPaint(0, 0, new Color(12, 10, 32),
                        getWidth(), 0, new Color(28, 22, 58)));
                g2.fillRect(0, 0, getWidth(), getHeight());
                g2.setPaint(new GradientPaint(0, 0, new Color(99, 102, 241),
                        getWidth() / 2f, 0, new Color(139, 92, 246)));
                g2.fillRect(0, getHeight() - 2, getWidth(), 2);
                g2.dispose();
            }
        };
        header.setBorder(new EmptyBorder(8, 22, 8, 14));
        header.setPreferredSize(new Dimension(0, 100));

        // Izquierda: título + subtítulo + badge de rol
        JPanel izq = new JPanel();
        izq.setLayout(new BoxLayout(izq, BoxLayout.Y_AXIS));
        izq.setOpaque(false);
        JLabel titulo = new JLabel("Sistema de Gestión de Butacas");
        titulo.setFont(UIConstants.FUENTE_TITULO);
        titulo.setForeground(UIConstants.TEXTO_PRIMARIO);
        titulo.setAlignmentX(Component.LEFT_ALIGNMENT);
        var mat = salaQuery.obtenerMatriz();
        int f = mat.length, c = mat.length > 0 ? mat[0].length : 0;
        JLabel subtitulo = new JLabel(
            nombreSala + "  •  " + f + " filas × " + c + " columnas  •  " + (f * c) + " butacas");
        subtitulo.setFont(UIConstants.FUENTE_PEQUENA);
        subtitulo.setForeground(UIConstants.TEXTO_TENUE);
        subtitulo.setAlignmentX(Component.LEFT_ALIGNMENT);
        izq.add(titulo);
        izq.add(Box.createVerticalStrut(3));
        izq.add(subtitulo);
        izq.add(Box.createVerticalStrut(5));
        izq.add(crearBadgeRol());
        header.add(izq, BorderLayout.WEST);

        // Derecha: controles ventana (arriba) + stat cards (abajo)
        JPanel derecha = new JPanel(new BorderLayout(0, 6));
        derecha.setOpaque(false);
        derecha.add(crearControlesVentana(), BorderLayout.NORTH);

        JPanel cards = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        cards.setOpaque(false);
        lblCardLibres     = new JLabel("0");
        lblCardReservadas = new JLabel("0");
        lblCardOcupadas   = new JLabel("0");
        cards.add(crearStatCard("Libres",     lblCardLibres,     UIConstants.COLOR_LIBRE));
        cards.add(crearStatCard("Reservadas", lblCardReservadas, UIConstants.COLOR_RESERVADO));
        cards.add(crearStatCard("Ocupadas",   lblCardOcupadas,   UIConstants.COLOR_OCUPADO));
        derecha.add(cards, BorderLayout.CENTER);
        header.add(derecha, BorderLayout.EAST);

        // Arrastrar desde el header para mover la ventana
        Point[] dragStart = {null};
        header.addMouseListener(new MouseAdapter() {
            @Override public void mousePressed (MouseEvent e) { dragStart[0] = e.getPoint(); }
            @Override public void mouseReleased(MouseEvent e) { dragStart[0] = null; }
        });
        header.addMouseMotionListener(new MouseMotionAdapter() {
            @Override public void mouseDragged(MouseEvent e) {
                if (dragStart[0] != null
                        && (getExtendedState() & JFrame.MAXIMIZED_BOTH) == 0) {
                    Point loc = MainFrame.this.getLocationOnScreen();
                    MainFrame.this.setLocation(
                        loc.x + e.getX() - dragStart[0].x,
                        loc.y + e.getY() - dragStart[0].y);
                }
            }
        });

        return header;
    }

    // ── BADGE DE ROL ─────────────────────────────────────────────────────────

    private JLabel crearBadgeRol() {
        boolean isAdmin = rol == Rol.ADMIN;
        Color textColor = isAdmin ? new Color(251, 191, 36) : new Color(147, 197, 253);
        Color bgColor   = isAdmin ? new Color(92, 68, 8, 130) : new Color(22, 56, 100, 130);
        Color bdColor   = isAdmin ? new Color(180, 130, 20, 90) : new Color(59, 130, 180, 90);
        String texto    = isAdmin ? "★ Administrador" : "● Cajero";

        JLabel badge = new JLabel(texto) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                        RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(bgColor);
                g2.fill(new RoundRectangle2D.Float(0, 1, getWidth() - 1, getHeight() - 2, 8, 8));
                g2.setColor(bdColor);
                g2.setStroke(new BasicStroke(1f));
                g2.draw(new RoundRectangle2D.Float(0, 1, getWidth() - 1, getHeight() - 2, 8, 8));
                g2.dispose();
                super.paintComponent(g);
            }
        };
        badge.setFont(new Font("Segoe UI", Font.BOLD, 11));
        badge.setForeground(textColor);
        badge.setBorder(new EmptyBorder(2, 8, 2, 8));
        badge.setOpaque(false);
        badge.setAlignmentX(Component.LEFT_ALIGNMENT);
        return badge;
    }

    // ── CONTROLES DE VENTANA ─────────────────────────────────────────────────

    private JPanel crearControlesVentana() {
        JPanel p = new JPanel(new FlowLayout(FlowLayout.RIGHT, 5, 0));
        p.setOpaque(false);
        // Cambiar sala
        p.add(crearBotonCambiarSala());
        // Cerrar sesión
        p.add(crearBotonLogout());
        // Separador visual
        JLabel sep = new JLabel("|");
        sep.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        sep.setForeground(new Color(50, 46, 80));
        p.add(sep);
        // Minimizar
        p.add(crearBotonVentana(new Color(251, 191, 36), "–",
                () -> setState(Frame.ICONIFIED)));
        // Maximizar / Restaurar
        btnMaximizar = crearBotonVentana(new Color(52, 211, 153), "□",
                this::toggleMaximizar);
        p.add(btnMaximizar);
        // Cerrar
        p.add(crearBotonVentana(new Color(239, 68, 68), "×",
                this::cerrarConConfirmacion));
        return p;
    }

    private JButton crearBotonLogout() {
        boolean[] hov = {false};
        JButton btn = new JButton("← Sesión") {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                        RenderingHints.VALUE_ANTIALIAS_ON);
                if (hov[0]) {
                    g2.setColor(new Color(40, 36, 68));
                    g2.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 6, 6));
                }
                g2.dispose();
                super.paintComponent(g);
            }
        };
        btn.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        btn.setForeground(new Color(100, 116, 139));
        btn.setContentAreaFilled(false);
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setOpaque(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setPreferredSize(new Dimension(72, 18));
        btn.addMouseListener(new MouseAdapter() {
            @Override public void mouseEntered(MouseEvent e) {
                hov[0] = true;
                btn.setForeground(new Color(148, 163, 184));
                btn.repaint();
            }
            @Override public void mouseExited(MouseEvent e) {
                hov[0] = false;
                btn.setForeground(new Color(100, 116, 139));
                btn.repaint();
            }
        });
        btn.addActionListener(e -> cerrarSesion());
        return btn;
    }

    private JButton crearBotonVentana(Color color, String icono, Runnable accion) {
        JButton btn = new JButton(icono) {
            boolean hov = false;
            {
                addMouseListener(new MouseAdapter() {
                    @Override public void mouseEntered(MouseEvent e) { hov = true;  repaint(); }
                    @Override public void mouseExited (MouseEvent e) { hov = false; repaint(); }
                });
            }
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(hov ? color : color.darker());
                g2.fillOval(1, 1, getWidth() - 2, getHeight() - 2);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        btn.setFont(new Font("Segoe UI", Font.BOLD, 10));
        btn.setForeground(new Color(0, 0, 0, 160));
        btn.setContentAreaFilled(false);
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setOpaque(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setPreferredSize(new Dimension(18, 18));
        btn.setMargin(new Insets(0, 0, 0, 0));
        btn.addActionListener(e -> accion.run());
        return btn;
    }

    private void toggleMaximizar() {
        boolean max = (getExtendedState() & JFrame.MAXIMIZED_BOTH) != 0;
        setExtendedState(max ? JFrame.NORMAL : JFrame.MAXIMIZED_BOTH);
    }

    private void actualizarIconoMaximizar() {
        if (btnMaximizar == null) return;
        boolean max = (getExtendedState() & JFrame.MAXIMIZED_BOTH) != 0;
        btnMaximizar.setText(max ? "▣" : "□");
    }

    private void cerrarConConfirmacion() {
        int op = JOptionPane.showConfirmDialog(this,
            "¿Está seguro que desea salir del sistema?",
            "Confirmar salida", JOptionPane.YES_NO_OPTION);
        if (op == JOptionPane.YES_OPTION) { dispose(); System.exit(0); }
    }

    private void cerrarSesion() {
        int op = JOptionPane.showConfirmDialog(this,
            "¿Desea cerrar sesión y volver al inicio?",
            "Cerrar sesión", JOptionPane.YES_NO_OPTION);
        if (op == JOptionPane.YES_OPTION) { logout = true; dispose(); }
    }

    public boolean  isLogout()    { return logout; }
    public boolean  isCambioSala(){ return cambioSala; }
    public SalaCine getNuevaSala() { return nuevaSala; }

    private JButton crearBotonCambiarSala() {
        boolean[] hov = {false};
        JButton btn = new JButton("⇄ Sala") {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                        RenderingHints.VALUE_ANTIALIAS_ON);
                if (hov[0]) {
                    g2.setColor(new Color(40, 36, 68));
                    g2.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 6, 6));
                }
                g2.dispose();
                super.paintComponent(g);
            }
        };
        btn.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        btn.setForeground(new Color(100, 116, 139));
        btn.setContentAreaFilled(false);
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setOpaque(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setPreferredSize(new Dimension(62, 18));
        btn.addMouseListener(new MouseAdapter() {
            @Override public void mouseEntered(MouseEvent e) {
                hov[0] = true;
                btn.setForeground(new Color(148, 163, 184));
                btn.repaint();
            }
            @Override public void mouseExited(MouseEvent e) {
                hov[0] = false;
                btn.setForeground(new Color(100, 116, 139));
                btn.repaint();
            }
        });
        btn.addActionListener(e -> cambiarSala());
        return btn;
    }

    private void cambiarSala() {
        SalaCine seleccionada;
        if (rol == Rol.ADMIN) {
            DialogGestionSalas d = new DialogGestionSalas(gestorSalas);
            d.setVisible(true);
            seleccionada = d.isConfirmado() ? d.getSalaSeleccionada() : null;
        } else {
            if (gestorSalas.listarSalas().isEmpty()) {
                JOptionPane.showMessageDialog(this,
                    "No hay salas disponibles.", "Sin salas", JOptionPane.WARNING_MESSAGE);
                return;
            }
            DialogSeleccionarSala d = new DialogSeleccionarSala(gestorSalas);
            d.setVisible(true);
            seleccionada = d.isConfirmado() ? d.getSalaSeleccionada() : null;
        }
        if (seleccionada != null) {
            nuevaSala  = seleccionada;
            cambioSala = true;
            dispose();
        }
    }

    private JPanel crearStatCard(String etiqueta, JLabel lblNum, Color color) {
        JPanel card = new JPanel(new BorderLayout(0, 2)) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(color.getRed(), color.getGreen(), color.getBlue(), 18));
                g2.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 10, 10));
                g2.setColor(new Color(color.getRed(), color.getGreen(), color.getBlue(), 70));
                g2.setStroke(new BasicStroke(1f));
                g2.draw(new RoundRectangle2D.Float(0, 0, getWidth()-1, getHeight()-1, 10, 10));
                g2.setColor(color);
                g2.fillRoundRect(0, 0, getWidth(), 3, 2, 2);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        card.setOpaque(false);
        card.setBorder(new EmptyBorder(7, 12, 7, 12));
        card.setPreferredSize(new Dimension(108, 58));

        lblNum.setFont(new Font("Segoe UI", Font.BOLD, 20));
        lblNum.setForeground(Color.WHITE);
        lblNum.setHorizontalAlignment(SwingConstants.CENTER);

        JLabel lbl = new JLabel(etiqueta, SwingConstants.CENTER);
        lbl.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        lbl.setForeground(color);

        card.add(lblNum, BorderLayout.CENTER);
        card.add(lbl,    BorderLayout.SOUTH);
        return card;
    }

    // ── PANEL CENTRAL ────────────────────────────────────────────────────────

    private JPanel crearPanelCentral() {
        JPanel central = new JPanel(new BorderLayout(14, 0));
        central.setOpaque(false);
        central.setBorder(new EmptyBorder(14, 14, 14, 14));

        JPanel izquierda = new JPanel(new BorderLayout(0, 10));
        izquierda.setOpaque(false);

        panelSala = new PanelSala(salaService, salaQuery, rol);

        JScrollPane scrollSala = new JScrollPane(panelSala,
            JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
            JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        scrollSala.setOpaque(false);
        scrollSala.getViewport().setOpaque(false);
        scrollSala.setBorder(null);
        scrollSala.getVerticalScrollBar().setUnitIncrement(16);
        scrollSala.getHorizontalScrollBar().setUnitIncrement(16);
        darkenScrollBar(scrollSala.getVerticalScrollBar());
        darkenScrollBar(scrollSala.getHorizontalScrollBar());

        JPanel tarjeta = crearTarjetaSala();
        tarjeta.add(scrollSala, BorderLayout.CENTER);

        JPanel salaConPantalla = new JPanel(new BorderLayout(0, 6));
        salaConPantalla.setOpaque(false);
        salaConPantalla.add(crearBannerPantalla(), BorderLayout.NORTH);
        salaConPantalla.add(tarjeta,               BorderLayout.CENTER);

        izquierda.add(salaConPantalla,  BorderLayout.CENTER);
        izquierda.add(new PanelLeyenda(), BorderLayout.SOUTH);

        panelControl = new PanelControl(salaService, salaQuery, rol);
        panelControl.setPreferredSize(new Dimension(268, 0));

        panelSala.setAlCambiarEstado(() -> {
            panelControl.actualizarEstadisticas();
            actualizarStats();
        });
        panelControl.setAlCambiarEstado(() -> {
            panelSala.refrescarTodo();
            actualizarStats();
        });

        central.add(izquierda,   BorderLayout.CENTER);
        central.add(panelControl, BorderLayout.EAST);
        return central;
    }

    private JPanel crearBannerPantalla() {
        JPanel banner = new JPanel(new BorderLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                int w = getWidth(), h = getHeight();
                g2.setPaint(new GradientPaint(w * 0.1f, 0, new Color(99, 102, 241, 0),
                        w * 0.5f, 0, new Color(139, 92, 246, 160)));
                g2.fill(new RoundRectangle2D.Float(0, 0, w / 2f, h, 6, 6));
                g2.setPaint(new GradientPaint(w * 0.5f, 0, new Color(139, 92, 246, 160),
                        w * 0.9f, 0, new Color(99, 102, 241, 0)));
                g2.fill(new RoundRectangle2D.Float(w / 2f, 0, w / 2f, h, 6, 6));
                g2.setColor(new Color(255, 255, 255, 30));
                g2.fillRect(0, h - 1, w, 1);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        banner.setOpaque(false);
        banner.setPreferredSize(new Dimension(0, 26));
        JLabel lbl = new JLabel("◄  P A N T A L L A  ►", SwingConstants.CENTER);
        lbl.setFont(new Font("Segoe UI", Font.BOLD, 11));
        lbl.setForeground(new Color(220, 220, 255, 200));
        banner.add(lbl, BorderLayout.CENTER);
        return banner;
    }

    private JPanel crearTarjetaSala() {
        JPanel tarjeta = new JPanel(new BorderLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(18, 16, 42));
                g2.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 16, 16));
                g2.setColor(new Color(50, 46, 90));
                g2.setStroke(new BasicStroke(1f));
                g2.draw(new RoundRectangle2D.Float(0, 0, getWidth()-1, getHeight()-1, 16, 16));
                g2.dispose();
                super.paintComponent(g);
            }
        };
        tarjeta.setOpaque(false);
        tarjeta.setBorder(new EmptyBorder(12, 12, 12, 12));
        return tarjeta;
    }

    // ── SCROLLBAR OSCURA ─────────────────────────────────────────────────────

    private static void darkenScrollBar(JScrollBar sb) {
        sb.setBackground(UIConstants.BG_PANEL);
        sb.setUI(new BasicScrollBarUI() {
            @Override protected void configureScrollBarColors() {
                thumbColor = new Color(70, 66, 110);
                trackColor = new Color(18, 16, 42);
            }
            @Override protected JButton createDecreaseButton(int o) { return makeZeroBtn(); }
            @Override protected JButton createIncreaseButton(int o) { return makeZeroBtn(); }
            private JButton makeZeroBtn() {
                JButton b = new JButton();
                b.setPreferredSize(new Dimension(0, 0));
                b.setMinimumSize(new Dimension(0, 0));
                b.setMaximumSize(new Dimension(0, 0));
                return b;
            }
            @Override protected void paintThumb(Graphics g, JComponent c, Rectangle r) {
                if (r.isEmpty()) return;
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(isDragging ? new Color(139, 92, 246) : new Color(70, 66, 110));
                g2.fill(new RoundRectangle2D.Float(r.x+2, r.y+2, r.width-4, r.height-4, 6, 6));
                g2.dispose();
            }
            @Override protected void paintTrack(Graphics g, JComponent c, Rectangle r) {
                g.setColor(new Color(18, 16, 42));
                g.fillRect(r.x, r.y, r.width, r.height);
            }
        });
    }

    // ── BARRA DE OCUPACIÓN ───────────────────────────────────────────────────

    private JPanel crearBarraOcupacion() {
        barraOcupacion = new JPanel(new BorderLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                int w = getWidth(), h = getHeight();
                g2.setColor(new Color(12, 10, 30));
                g2.fillRect(0, 0, w, h);
                g2.setColor(new Color(40, 38, 75));
                g2.drawLine(0, 0, w, 0);

                int total = statLibres + statReservadas + statOcupadas;
                if (total == 0) { g2.dispose(); super.paintComponent(g); return; }

                int barH = 6, barY = 8, margen = 16;
                int barW = w - margen * 2;
                int wL = (int)((double) statLibres     / total * barW);
                int wR = (int)((double) statReservadas / total * barW);
                int wO = barW - wL - wR;

                g2.setColor(new Color(30, 28, 60));
                g2.fill(new RoundRectangle2D.Float(margen, barY, barW, barH, barH, barH));
                if (wL > 0) {
                    g2.setColor(UIConstants.COLOR_LIBRE);
                    g2.fill(new RoundRectangle2D.Float(margen, barY, wL, barH, barH, barH));
                }
                if (wR > 0) {
                    g2.setColor(UIConstants.COLOR_RESERVADO);
                    g2.fillRect(margen + wL, barY, wR, barH);
                }
                if (wO > 0) {
                    g2.setColor(UIConstants.COLOR_OCUPADO);
                    g2.fill(new RoundRectangle2D.Float(margen+wL+wR, barY, wO, barH, barH, barH));
                }

                g2.setFont(new Font("Segoe UI", Font.PLAIN, 11));
                int ty   = barY + barH + 14;
                int secW = (w - margen * 2) / 3;
                int pctL = (int)(100.0 * statLibres     / total);
                int pctR = (int)(100.0 * statReservadas / total);
                int pctO = 100 - pctL - pctR;

                drawStatText(g2, UIConstants.COLOR_LIBRE,
                    "Libres: "     + statLibres     + " (" + pctL + "%)", margen,           ty);
                drawStatText(g2, UIConstants.COLOR_RESERVADO,
                    "Reservadas: " + statReservadas + " (" + pctR + "%)", margen + secW,     ty);
                drawStatText(g2, UIConstants.COLOR_OCUPADO,
                    "Ocupadas: "   + statOcupadas   + " (" + pctO + "%)", margen + secW * 2, ty);

                g2.dispose();
                super.paintComponent(g);
            }

            private void drawStatText(Graphics2D g2, Color color, String text, int x, int y) {
                g2.setColor(color);
                g2.fillOval(x, y - 8, 8, 8);
                g2.setColor(UIConstants.TEXTO_SECUNDARIO);
                g2.drawString(text, x + 12, y);
            }
        };
        barraOcupacion.setOpaque(false);
        barraOcupacion.setPreferredSize(new Dimension(0, 40));
        return barraOcupacion;
    }

    // ── ESTADÍSTICAS ─────────────────────────────────────────────────────────

    private void actualizarStats() {
        statLibres     = (int) salaQuery.contarLibres();
        statReservadas = (int) salaQuery.contarReservadas();
        statOcupadas   = (int) salaQuery.contarOcupadas();
        lblCardLibres.setText(String.valueOf(statLibres));
        lblCardReservadas.setText(String.valueOf(statReservadas));
        lblCardOcupadas.setText(String.valueOf(statOcupadas));
        if (barraOcupacion != null) barraOcupacion.repaint();
    }

    // ── REDIMENSIÓN POR ARRASTRE ──────────────────────────────────────────────

    private void habilitarRedimension() {
        final int BDR = 6;
        Component glass = getGlassPane();
        glass.setVisible(true);

        MouseAdapter m = new MouseAdapter() {
            int       dir       = Cursor.DEFAULT_CURSOR;
            boolean   resizing  = false;
            Component dragTarget = null;
            Rectangle origBounds;
            Point     origMouse;
            Component lastEntered = null;

            private int calcDir(Point p) {
                if ((getExtendedState() & JFrame.MAXIMIZED_BOTH) != 0)
                    return Cursor.DEFAULT_CURSOR;
                int w = getWidth(), h = getHeight();
                boolean L = p.x < BDR, R = p.x > w - BDR,
                        T = p.y < BDR, B = p.y > h - BDR;
                if (T && L) return Cursor.NW_RESIZE_CURSOR;
                if (T && R) return Cursor.NE_RESIZE_CURSOR;
                if (B && L) return Cursor.SW_RESIZE_CURSOR;
                if (B && R) return Cursor.SE_RESIZE_CURSOR;
                if (T)      return Cursor.N_RESIZE_CURSOR;
                if (B)      return Cursor.S_RESIZE_CURSOR;
                if (L)      return Cursor.W_RESIZE_CURSOR;
                if (R)      return Cursor.E_RESIZE_CURSOR;
                return Cursor.DEFAULT_CURSOR;
            }

            private Component destino(MouseEvent e) {
                Container cp = MainFrame.this.getContentPane();
                Point pt = SwingUtilities.convertPoint(glass, e.getPoint(), cp);
                return SwingUtilities.getDeepestComponentAt(cp, pt.x, pt.y);
            }

            private void redispatch(MouseEvent e, Component dest) {
                if (dest != null)
                    dest.dispatchEvent(SwingUtilities.convertMouseEvent(glass, e, dest));
            }

            // Genera ENTERED/EXITED automáticamente para mantener hover effects
            private void updateHover(Component dest, MouseEvent e) {
                if (dest == lastEntered) return;
                if (lastEntered != null)
                    lastEntered.dispatchEvent(new MouseEvent(lastEntered,
                        MouseEvent.MOUSE_EXITED, e.getWhen(), 0, 0, 0, 0, false));
                if (dest != null) {
                    Point pt = SwingUtilities.convertPoint(glass, e.getPoint(), dest);
                    dest.dispatchEvent(new MouseEvent(dest,
                        MouseEvent.MOUSE_ENTERED, e.getWhen(), 0, pt.x, pt.y, 0, false));
                }
                lastEntered = dest;
            }

            @Override public void mouseMoved(MouseEvent e) {
                dir = calcDir(e.getPoint());
                glass.setCursor(dir == Cursor.DEFAULT_CURSOR
                    ? Cursor.getDefaultCursor()
                    : Cursor.getPredefinedCursor(dir));
                if (dir == Cursor.DEFAULT_CURSOR) {
                    Component dest = destino(e);
                    updateHover(dest, e);
                    redispatch(e, dest);
                }
            }

            @Override public void mousePressed(MouseEvent e) {
                dir = calcDir(e.getPoint());
                if (dir != Cursor.DEFAULT_CURSOR) {
                    resizing = true;
                    origBounds = MainFrame.this.getBounds();
                    origMouse  = e.getLocationOnScreen();
                    dragTarget = null;
                } else {
                    resizing   = false;
                    dragTarget = destino(e);
                    redispatch(e, dragTarget);
                }
            }

            @Override public void mouseDragged(MouseEvent e) {
                if (!resizing) {
                    redispatch(e, dragTarget);
                    return;
                }
                Point cur = e.getLocationOnScreen();
                int dx = cur.x - origMouse.x, dy = cur.y - origMouse.y;
                int nx = origBounds.x, ny = origBounds.y,
                    nw = origBounds.width, nh = origBounds.height;
                if (dir==Cursor.W_RESIZE_CURSOR||dir==Cursor.NW_RESIZE_CURSOR||dir==Cursor.SW_RESIZE_CURSOR){nx+=dx;nw-=dx;}
                if (dir==Cursor.E_RESIZE_CURSOR||dir==Cursor.NE_RESIZE_CURSOR||dir==Cursor.SE_RESIZE_CURSOR) nw+=dx;
                if (dir==Cursor.N_RESIZE_CURSOR||dir==Cursor.NW_RESIZE_CURSOR||dir==Cursor.NE_RESIZE_CURSOR){ny+=dy;nh-=dy;}
                if (dir==Cursor.S_RESIZE_CURSOR||dir==Cursor.SW_RESIZE_CURSOR||dir==Cursor.SE_RESIZE_CURSOR) nh+=dy;
                Dimension mn = MainFrame.this.getMinimumSize();
                if (nw >= mn.width && nh >= mn.height) {
                    MainFrame.this.setBounds(nx, ny, nw, nh);
                    revalidate();
                }
            }

            @Override public void mouseReleased(MouseEvent e) {
                if (!resizing) redispatch(e, dragTarget);
                resizing = false; origBounds = null; dragTarget = null;
                dir = Cursor.DEFAULT_CURSOR;
                glass.setCursor(Cursor.getDefaultCursor());
            }

            @Override public void mouseClicked(MouseEvent e) {
                if (!resizing) redispatch(e, destino(e));
            }
        };

        glass.addMouseListener(m);
        glass.addMouseMotionListener(m);
    }

    // ── EVENTOS ──────────────────────────────────────────────────────────────

    private void configurarEventos() {
        WindowAdapter wa = new WindowAdapter() {
            @Override public void windowClosing(WindowEvent e)      { cerrarConConfirmacion(); }
            @Override public void windowStateChanged(WindowEvent e) { actualizarIconoMaximizar(); }
        };
        addWindowListener(wa);
        addWindowStateListener(wa);         // necesario para que windowStateChanged() dispare
        habilitarRedimension();
    }
}
