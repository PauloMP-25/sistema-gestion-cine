package view.panel;

import view.*;
import view.frame.*;
import view.panel.*;
import view.dialog.*;
import view.component.*;
import model.sala.*;
import model.butaca.*;
import model.reserva.*;
import model.auth.*;


import exception.AsientoNoReservadoException;
import exception.AsientoOcupadoException;
import exception.AsientoYaReservadoException;
import exception.PosicionInvalidaException;
import model.auth.Rol;
import service.interfaces.ISalaQuery;
import service.interfaces.ISalaService;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.RoundRectangle2D;

public class PanelControl extends JPanel {

    private final ISalaService salaService;
    private final ISalaQuery   salaQuery;
    private final Rol          rol;
    private final int filas, columnas;

    private JSpinner    spinNumeroAsiento;
    private JLabel      lblSeatDisplay;
    private JLabel      lblOcupacion;
    private PanelBarras panelBarras;
    private Runnable    alCambiarEstado;

    public PanelControl(ISalaService salaService, ISalaQuery salaQuery, Rol rol) {
        this.salaService = salaService;
        this.salaQuery   = salaQuery;
        this.rol         = rol;
        var m = salaQuery.obtenerMatriz();
        this.filas    = m.length;
        this.columnas = m.length > 0 ? m[0].length : 0;
        setOpaque(false);
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        inicializarComponentes();
        actualizarEstadisticas();
    }

    private void inicializarComponentes() {
        add(crearTitulo());
        add(Box.createVerticalStrut(12));
        add(crearPanelCoordenadas());
        add(Box.createVerticalStrut(12));
        add(crearPanelBotones());
        add(Box.createVerticalStrut(12));
        add(crearPanelEstadisticas());
    }

    // ── TÍTULO ───────────────────────────────────────────────────────────────

    private JPanel crearTitulo() {
        JPanel p = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 2)) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                // Gradient underline (purple → transparent)
                g2.setPaint(new GradientPaint(0, 0, new Color(99, 102, 241),
                        getWidth() * 0.65f, 0, new Color(99, 102, 241, 0)));
                g2.fillRect(0, getHeight() - 2, getWidth(), 2);
                g2.dispose();
            }
        };
        p.setOpaque(false);
        p.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel lbl = new JLabel("Panel de Control");
        lbl.setFont(UIConstants.FUENTE_SUBTITULO);
        lbl.setForeground(UIConstants.TEXTO_PRIMARIO);
        p.add(lbl);
        return p;
    }

    // ── SELECTOR DE BUTACA ───────────────────────────────────────────────────

    private JPanel crearPanelCoordenadas() {
        JPanel panel = crearTarjeta(new Color(59, 130, 246)); // blue accent
        panel.setLayout(new BorderLayout(0, 10));

        JLabel titulo = new JLabel("Seleccionar Butaca");
        titulo.setFont(UIConstants.FUENTE_NEGRITA);
        titulo.setForeground(new Color(147, 197, 253)); // light blue
        panel.add(titulo, BorderLayout.NORTH);

        JPanel centro = new JPanel(new BorderLayout(0, 8));
        centro.setOpaque(false);

        // Dynamic seat display box
        JPanel displayPanel = new JPanel(new GridBagLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(14, 12, 38));
                g2.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 10, 10));
                g2.setColor(new Color(38, 48, 88));
                g2.setStroke(new BasicStroke(1f));
                g2.draw(new RoundRectangle2D.Float(0, 0, getWidth() - 1, getHeight() - 1, 10, 10));
                g2.dispose();
                super.paintComponent(g);
            }
        };
        displayPanel.setOpaque(false);
        displayPanel.setPreferredSize(new Dimension(0, 46));

        lblSeatDisplay = new JLabel("Asiento 1");
        lblSeatDisplay.setFont(new Font("Segoe UI", Font.BOLD, 18));
        lblSeatDisplay.setForeground(new Color(139, 92, 246));
        displayPanel.add(lblSeatDisplay);

        // Spinner row
        JPanel spRow = new JPanel(new GridLayout(1, 2, 8, 0));
        spRow.setOpaque(false);
        spRow.add(etiqueta("Nº Asiento:"));
        spinNumeroAsiento = crearSpinner(filas * columnas);
        spRow.add(spinNumeroAsiento);

        // Live update of display
        ChangeListener refresh = e -> actualizarDisplayButaca();
        spinNumeroAsiento.addChangeListener(refresh);

        centro.add(displayPanel, BorderLayout.NORTH);
        centro.add(spRow,        BorderLayout.CENTER);
        panel.add(centro, BorderLayout.CENTER);
        return panel;
    }

    private void actualizarDisplayButaca() {
        int num = (int) spinNumeroAsiento.getValue();
        lblSeatDisplay.setText("Asiento " + num);
    }
    
    public void seleccionarAsiento(int numeroAsiento) {
        if (numeroAsiento >= 1 && numeroAsiento <= filas * columnas) {
            spinNumeroAsiento.setValue(numeroAsiento);
        }
    }

    // ── BOTONES ──────────────────────────────────────────────────────────────

    private JPanel crearPanelBotones() {
        JPanel panel = crearTarjeta(new Color(139, 92, 246)); // purple accent
        panel.setLayout(new BorderLayout(0, 10));

        JLabel titulo = new JLabel("Operaciones");
        titulo.setFont(UIConstants.FUENTE_NEGRITA);
        titulo.setForeground(new Color(196, 181, 253)); // light purple
        panel.add(titulo, BorderLayout.NORTH);

        JPanel botones = new JPanel(new GridLayout(4, 1, 0, 8));
        botones.setOpaque(false);

        JButton btnReservar = crearBoton("Reservar Butaca",
                UIConstants.COLOR_LIBRE, UIConstants.COLOR_LIBRE_DARK);
        btnReservar.addActionListener(e -> onReservarClick());

        JButton btnOcupar = crearBoton("Ocupar Butaca",
                UIConstants.COLOR_OCUPADO, UIConstants.COLOR_OCUPADO_DARK);
        btnOcupar.addActionListener(e -> onOcuparClick());

        JButton btnCancelar = crearBoton("Cancelar Reserva",
                UIConstants.COLOR_RESERVADO, UIConstants.COLOR_RESERVADO_DARK);
        btnCancelar.addActionListener(e -> onCancelarClick());

        boolean puedeAdmin = rol == Rol.ADMIN;
        JButton btnLimpiar = crearBoton(
                puedeAdmin ? "Limpiar Sala" : "Limpiar Sala  [Admin]",
                puedeAdmin ? new Color(160, 110, 210) : new Color(42, 46, 52),
                puedeAdmin ? new Color(180, 130, 230) : new Color(30, 33, 38));
        btnLimpiar.addActionListener(e -> onLimpiarClick());

        botones.add(btnReservar);
        botones.add(btnOcupar);
        botones.add(btnCancelar);
        botones.add(btnLimpiar);

        panel.add(botones, BorderLayout.CENTER);
        return panel;
    }

    // ── ESTADÍSTICAS VISUALES ────────────────────────────────────────────────

    private JPanel crearPanelEstadisticas() {
        JPanel panel = crearTarjeta(new Color(34, 197, 94)); // green accent
        panel.setLayout(new BorderLayout(0, 10));

        // Header: title + live occupancy %
        JPanel headerRow = new JPanel(new BorderLayout());
        headerRow.setOpaque(false);

        JLabel titulo = new JLabel("Estadísticas");
        titulo.setFont(UIConstants.FUENTE_NEGRITA);
        titulo.setForeground(UIConstants.TEXTO_PRIMARIO);

        lblOcupacion = new JLabel("100% libre");
        lblOcupacion.setFont(new Font("Segoe UI", Font.BOLD, 11));
        lblOcupacion.setForeground(UIConstants.COLOR_LIBRE);

        headerRow.add(titulo,       BorderLayout.WEST);
        headerRow.add(lblOcupacion, BorderLayout.EAST);
        panel.add(headerRow, BorderLayout.NORTH);

        panelBarras = new PanelBarras();
        panelBarras.setPreferredSize(new Dimension(0, 96));
        panel.add(panelBarras, BorderLayout.CENTER);
        return panel;
    }

    // ── PANEL DE BARRAS ──────────────────────────────────────────────────────

    private static class PanelBarras extends JPanel {
        private long libres = 0, reservadas = 0, ocupadas = 0;
        private int  total  = 1;

        PanelBarras() { setOpaque(false); }

        void actualizar(long l, long r, long o, int t) {
            libres = l; reservadas = r; ocupadas = o;
            total  = t > 0 ? t : 1;
            repaint();
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            int barH = 7, gap = 26, startY = 4, maxW = getWidth() - 62;
            dibujarBarra(g2, "Libres",     libres,     UIConstants.COLOR_LIBRE,     maxW, startY,           barH);
            dibujarBarra(g2, "Reservadas", reservadas, UIConstants.COLOR_RESERVADO, maxW, startY + gap,     barH);
            dibujarBarra(g2, "Ocupadas",   ocupadas,   UIConstants.COLOR_OCUPADO,   maxW, startY + gap * 2, barH);

            // Total label
            g2.setFont(new Font("Segoe UI", Font.PLAIN, 10));
            g2.setColor(UIConstants.TEXTO_TENUE);
            g2.drawString("Total: " + total + " butacas", 0, startY + gap * 3 + 2);
            g2.dispose();
        }

        private void dibujarBarra(Graphics2D g2, String label, long valor, Color color,
                                   int maxW, int y, int barH) {
            int pct    = (int)(100.0 * valor / total);
            int filled = (int)((double) valor / total * maxW);

            g2.setFont(new Font("Segoe UI", Font.PLAIN, 11));
            g2.setColor(UIConstants.TEXTO_SECUNDARIO);
            g2.drawString(label, 0, y + barH);

            int barY = y + barH + 4;

            // Background track
            g2.setColor(new Color(40, 38, 75));
            g2.fill(new RoundRectangle2D.Float(0, barY, maxW, barH, barH, barH));

            // Filled segment
            if (filled > 0) {
                g2.setPaint(new GradientPaint(0, 0, color, filled, 0, color.brighter()));
                g2.fill(new RoundRectangle2D.Float(0, barY, filled, barH, barH, barH));
            }

            // Percentage label
            g2.setFont(new Font("Segoe UI", Font.BOLD, 10));
            g2.setColor(color);
            g2.drawString(valor + " (" + pct + "%)", maxW + 5, barY + barH);
        }
    }

    // ── HANDLERS ─────────────────────────────────────────────────────────────

    private void onReservarClick() {
        int fila = filaSeleccionada(), col = columnaSeleccionada();
        int num = (fila * columnas) + col + 1;
        DialogReserva d = new DialogReserva(ventanaPadre(), fila, col, num);
        d.setVisible(true);
        if (d.isConfirmado()) ejecutarReserva(fila, col, num);
    }

    private void ejecutarReserva(int fila, int col, int num) {
        try {
            salaService.reservar(fila, col);
            notificarCambio();
            DialogMensaje.mostrar(ventanaPadre(), DialogMensaje.Tipo.EXITO, "Se ha reservado el asiento número " + num + ".");
        } catch (PosicionInvalidaException ex)     { mostrarError("Posición inválida."); }
          catch (AsientoOcupadoException ex)       { mostrarError("Este asiento está ocupado."); }
          catch (AsientoYaReservadoException ex)   { mostrarError("Este asiento ya fue reservado."); }
    }

    private void onCancelarClick() {
        int fila = filaSeleccionada(), col = columnaSeleccionada();
        int num = (fila * columnas) + col + 1;
        try {
            salaService.cancelar(fila, col);
            notificarCambio();
            DialogMensaje.mostrar(ventanaPadre(), DialogMensaje.Tipo.EXITO, "Se ha cancelado la reserva del asiento número " + num + ".");
        } catch (PosicionInvalidaException ex)  { mostrarError("Posición inválida."); }
          catch (AsientoNoReservadoException ex) { mostrarError("El asiento no está reservado."); }
    }

    private void onLimpiarClick() {
        if (rol == Rol.CAJERO) {
            mostrarError("Sin permisos. Solo el administrador puede limpiar la sala.");
            return;
        }
        int c = JOptionPane.showConfirmDialog(ventanaPadre(),
            "¿Desea limpiar todos los estados de la sala?",
            "Limpiar Sala", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
        if (c == JOptionPane.YES_OPTION) {
            salaService.limpiarSala();
            notificarCambio();
            mostrarInfo("Sala limpiada. Todas las butacas están libres.");
        }
    }

    private void onOcuparClick() {
        int fila = filaSeleccionada(), col = columnaSeleccionada();
        int num = (fila * columnas) + col + 1;
        int confirm = JOptionPane.showConfirmDialog(ventanaPadre(), "¿Confirmar la ocupación para el asiento número " + num + "?", "Confirmar Ocupación", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            try {
                salaService.ocupar(fila, col);
                notificarCambio();
                DialogMensaje.mostrar(ventanaPadre(), DialogMensaje.Tipo.EXITO, "Se ha ocupado el asiento número " + num + ".");
            } catch (PosicionInvalidaException ex) { 
                mostrarError("Posición inválida."); 
            }
        }
    }

    // ── ACTUALIZACIÓN ────────────────────────────────────────────────────────

    public void actualizarEstadisticas() {
        long l = salaQuery.contarLibres();
        long r = salaQuery.contarReservadas();
        long o = salaQuery.contarOcupadas();
        int  t = salaQuery.totalButacas();
        panelBarras.actualizar(l, r, o, t);

        // Live occupancy label
        if (lblOcupacion != null && t > 0) {
            int pctLibre = (int)(100.0 * l / t);
            lblOcupacion.setText(pctLibre + "% libre");
            Color color = pctLibre > 60 ? UIConstants.COLOR_LIBRE
                        : pctLibre > 30 ? UIConstants.COLOR_RESERVADO
                        : UIConstants.COLOR_OCUPADO;
            lblOcupacion.setForeground(color);
        }
    }

    public void setAlCambiarEstado(Runnable cb) { this.alCambiarEstado = cb; }

    private void notificarCambio() {
        actualizarEstadisticas();
        if (alCambiarEstado != null) alCambiarEstado.run();
    }

    // ── UTILIDADES ───────────────────────────────────────────────────────────

    private int filaSeleccionada() { 
        int num = (int) spinNumeroAsiento.getValue();
        return (num - 1) / columnas;
    }
    
    private int columnaSeleccionada() {
        int num = (int) spinNumeroAsiento.getValue();
        return (num - 1) % columnas;
    }

    private void mostrarError(String m) {
        DialogMensaje.mostrar(ventanaPadre(), DialogMensaje.Tipo.ERROR, m);
    }
    private void mostrarInfo(String m) {
        DialogMensaje.mostrar(ventanaPadre(), DialogMensaje.Tipo.INFO, m);
    }
    private Frame ventanaPadre() {
        return (Frame) SwingUtilities.getWindowAncestor(this);
    }

    // ── HELPERS VISUALES ─────────────────────────────────────────────────────

    private JPanel crearTarjeta(Color accentColor) {
        JPanel p = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                // Background
                g2.setColor(UIConstants.BG_TARJETA);
                g2.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 14, 14));
                // Border + top accent
                if (accentColor != null) {
                    int ar = accentColor.getRed(), ag = accentColor.getGreen(), ab = accentColor.getBlue();
                    // Colored border (low opacity)
                    g2.setColor(new Color(ar, ag, ab, 55));
                    g2.setStroke(new BasicStroke(1f));
                    g2.draw(new RoundRectangle2D.Float(0, 0, getWidth() - 1, getHeight() - 1, 14, 14));
                    // Top gradient accent strip
                    g2.setPaint(new GradientPaint(0, 0, new Color(ar, ag, ab, 190),
                            getWidth() * 0.7f, 0, new Color(ar, ag, ab, 0)));
                    g2.fillRoundRect(0, 0, getWidth(), 3, 14, 14);
                } else {
                    g2.setColor(UIConstants.BORDE);
                    g2.setStroke(new BasicStroke(1f));
                    g2.draw(new RoundRectangle2D.Float(0, 0, getWidth() - 1, getHeight() - 1, 14, 14));
                }
                g2.dispose();
            }
        };
        p.setOpaque(false);
        p.setBorder(new EmptyBorder(12, 14, 12, 14));
        p.setAlignmentX(Component.LEFT_ALIGNMENT);
        return p;
    }

    private JButton crearBoton(String texto, Color c1, Color c2) {
        JButton btn = new JButton(texto) {
            boolean hovered = false;
            {
                addMouseListener(new MouseAdapter() {
                    public void mouseEntered(MouseEvent e) { hovered = true;  repaint(); }
                    public void mouseExited (MouseEvent e) { hovered = false; repaint(); }
                });
            }
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                Color a = hovered ? c1.brighter() : c1;
                Color b = hovered ? c2.brighter() : c2;
                g2.setPaint(new GradientPaint(0, 0, a, getWidth(), 0, b));
                g2.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 8, 8));
                // Subtle dot indicator on left
                g2.setColor(new Color(255, 255, 255, 65));
                g2.fillOval(10, getHeight() / 2 - 3, 6, 6);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        btn.setFont(UIConstants.FUENTE_BOTON);
        btn.setForeground(Color.WHITE);
        btn.setContentAreaFilled(false);
        btn.setFocusPainted(false);
        btn.setFocusable(false);
        btn.setBorderPainted(false);
        btn.setOpaque(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setHorizontalAlignment(SwingConstants.LEFT);
        btn.setBorder(new EmptyBorder(0, 26, 0, 8));
        btn.setPreferredSize(new Dimension(0, 42));
        return btn;
    }

    private JLabel etiqueta(String t) {
        JLabel l = new JLabel(t);
        l.setFont(UIConstants.FUENTE_CUERPO);
        l.setForeground(UIConstants.TEXTO_SECUNDARIO);
        return l;
    }

    private JSpinner crearSpinner(int max) {
        JSpinner sp = new JSpinner(new SpinnerNumberModel(1, 1, max, 1));
        sp.setFont(UIConstants.FUENTE_NEGRITA);
        sp.setBackground(new Color(14, 12, 38));
        JComponent ed = sp.getEditor();
        if (ed instanceof JSpinner.DefaultEditor) {
            JTextField tf = ((JSpinner.DefaultEditor) ed).getTextField();
            tf.setBackground(new Color(14, 12, 38));
            tf.setForeground(new Color(139, 92, 246));
            tf.setCaretColor(new Color(139, 92, 246));
            tf.setFont(new Font("Segoe UI", Font.BOLD, 13));
            tf.setHorizontalAlignment(JTextField.CENTER);
            tf.setBorder(new EmptyBorder(4, 2, 4, 2));
        }
        sp.setBorder(BorderFactory.createLineBorder(new Color(40, 38, 80), 1, true));
        // FIX-4: oscurecer botones de flechas del spinner
        for (Component c : sp.getComponents()) {
            if (c instanceof JButton) {
                JButton btn = (JButton) c;
                btn.setBackground(new Color(30, 28, 70));
                btn.setForeground(new Color(139, 92, 246));
                btn.setOpaque(true);
            }
        }
        return sp;
    }
}
