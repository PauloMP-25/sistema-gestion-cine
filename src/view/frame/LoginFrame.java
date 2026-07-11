package view.frame;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.RoundRectangle2D;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class LoginFrame extends JDialog {

    private static final Map<String, String> USUARIOS = Map.of(
            "admin",  "admin123",
            "cajero", "cine2024"
    );

    private JTextField     txtUsuario;
    private JPasswordField txtPassword;
    private JLabel         lblError;
    private boolean        loginExitoso  = false;
    private String         usuarioActual = "";

    // Animation state — accessed by anonymous inner panels
    float glowPulse = 0f;
    private Timer animTimer;

    // Particles: {x, y, speed, size, opacity}
    private final List<float[]> particulas = new ArrayList<>();

    public LoginFrame() {
        super((Frame) null, "Sistema de Gestión de Cine — Iniciar Sesión", true);
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            @Override public void windowClosing(WindowEvent e) { System.exit(0); }
        });
        setSize(480, 590);
        setResizable(false);
        setLocationRelativeTo(null);

        for (int i = 0; i < 50; i++) {
            particulas.add(new float[]{
                (float)(Math.random() * 480),
                (float)(Math.random() * 590),
                (float)(0.2f + Math.random() * 0.5f),
                (float)(1.0f + Math.random() * 2.5f),
                (float)(0.1f + Math.random() * 0.4f)
            });
        }

        inicializarComponentes();
    }

    @Override
    public void setVisible(boolean b) {
        if (b && !isVisible()) {
            try {
                setOpacity(0f);
                super.setVisible(true);
                float[] alpha = {0f};
                Timer fadeIn = new Timer(16, null);
                fadeIn.addActionListener(e -> {
                    alpha[0] = Math.min(1f, alpha[0] + 0.07f);
                    try { setOpacity(alpha[0]); } catch (Exception ignored) {}
                    if (alpha[0] >= 1f) fadeIn.stop();
                });
                fadeIn.start();
            } catch (Exception ignored) {
                super.setVisible(true);
            }
        } else {
            super.setVisible(b);
        }
    }

    @Override
    public void dispose() {
        if (animTimer != null) animTimer.stop();
        super.dispose();
    }

    public boolean isLoginExitoso()   { return loginExitoso; }
    public String  getUsuarioActual() { return usuarioActual; }

    // ── Layout ─────────────────────────────────────────────────────────────────

    private void inicializarComponentes() {
        JPanel cp = crearContentPane();
        setContentPane(cp);

        animTimer = new Timer(33, e -> {
            glowPulse += 0.04f;
            for (float[] p : particulas) {
                p[1] -= p[2];
                if (p[1] < -5) p[1] = 596;
            }
            cp.repaint();
        });
        animTimer.start();

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx   = 0;
        gbc.fill    = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;

        gbc.gridy = 0; gbc.insets = new Insets(36, 60, 0, 60);
        cp.add(crearPanelLogo(), gbc);

        gbc.gridy = 1; gbc.insets = new Insets(14, 60, 2, 60);
        JLabel lblTitulo = new JLabel("Bienvenido", SwingConstants.CENTER);
        lblTitulo.setFont(new Font("Segoe UI", Font.BOLD, 26));
        lblTitulo.setForeground(Color.WHITE);
        cp.add(lblTitulo, gbc);

        gbc.gridy = 2; gbc.insets = new Insets(0, 60, 0, 60);
        JLabel lblSub = new JLabel("Inicia sesión para continuar", SwingConstants.CENTER);
        lblSub.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        lblSub.setForeground(new Color(100, 116, 139));
        cp.add(lblSub, gbc);

        gbc.gridy = 3; gbc.insets = new Insets(18, 60, 16, 60);
        JSeparator sep = new JSeparator();
        sep.setForeground(new Color(40, 38, 68));
        cp.add(sep, gbc);

        gbc.gridy = 4; gbc.insets = new Insets(0, 60, 12, 60);
        cp.add(crearCampoEtiquetado("Usuario", false), gbc);

        gbc.gridy = 5; gbc.insets = new Insets(0, 60, 10, 60);
        cp.add(crearCampoEtiquetado("Contraseña", true), gbc);

        gbc.gridy = 6; gbc.insets = new Insets(0, 60, 6, 60);
        lblError = new JLabel(" ", SwingConstants.CENTER);
        lblError.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblError.setForeground(new Color(239, 68, 68));
        cp.add(lblError, gbc);

        JButton btnLogin = crearBotonLogin();
        gbc.gridy = 7; gbc.insets = new Insets(0, 60, 14, 60);
        cp.add(btnLogin, gbc);

        gbc.gridy = 8; gbc.insets = new Insets(0, 60, 8, 60);
        JLabel lblHint = new JLabel(
                "admin / admin123   •   cajero / cine2024",
                SwingConstants.CENTER);
        lblHint.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        lblHint.setForeground(new Color(71, 85, 105));
        cp.add(lblHint, gbc);

        gbc.gridy = 9; gbc.insets = new Insets(0, 60, 12, 60);
        JLabel lblFooter = new JLabel(
                "Sistema Cinema v1.0  •  2025",
                SwingConstants.CENTER);
        lblFooter.setFont(new Font("Segoe UI", Font.PLAIN, 10));
        lblFooter.setForeground(new Color(50, 50, 72));
        cp.add(lblFooter, gbc);

        KeyAdapter enter = new KeyAdapter() {
            @Override public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) btnLogin.doClick();
            }
        };
        txtUsuario.addKeyListener(enter);
        txtPassword.addKeyListener(enter);
    }

    // ── Custom painted panels ───────────────────────────────────────────────────

    private JPanel crearContentPane() {
        return new JPanel(new GridBagLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                        RenderingHints.VALUE_ANTIALIAS_ON);
                int w = getWidth(), h = getHeight();

                // Dark gradient background
                g2.setPaint(new GradientPaint(0, 0, new Color(10, 8, 28),
                        0, h, new Color(16, 12, 46)));
                g2.fillRect(0, 0, w, h);

                // Film strips on left and right edges
                dibujarFilmStrip(g2, 0,      h, true);
                dibujarFilmStrip(g2, w - 18, h, false);

                // Static dot grid between the strips
                g2.setColor(new Color(255, 255, 255, 6));
                for (int x = 22; x < w - 18; x += 26)
                    for (int y = 6; y < h; y += 26)
                        g2.fillOval(x, y, 2, 2);

                // Floating violet particles
                for (float[] p : particulas) {
                    int al = (int)(p[4] * 255);
                    g2.setColor(new Color(139, 92, 246, al));
                    g2.fillOval((int)p[0], (int)p[1], (int)p[3], (int)p[3]);
                }

                // Ambient pulsing glow behind the logo
                float pulse = (float)(Math.sin(glowPulse) * 0.5 + 0.5);
                int ambR = 72 + (int)(pulse * 16);
                g2.setColor(new Color(99, 102, 241, 10 + (int)(pulse * 12)));
                g2.fillOval(w / 2 - ambR, 18, ambR * 2, ambR * 2);

                // Top accent gradient line
                g2.setPaint(new GradientPaint(0, 0, new Color(99, 102, 241, 0),
                        w * 0.5f, 0, new Color(99, 102, 241, 115)));
                g2.fillRect(0, 0, w / 2, 3);
                g2.setPaint(new GradientPaint(w * 0.5f, 0, new Color(139, 92, 246, 115),
                        (float)w, 0, new Color(139, 92, 246, 0)));
                g2.fillRect(w / 2, 0, w / 2, 3);

                g2.dispose();
            }

            private void dibujarFilmStrip(Graphics2D g2, int x, int h, boolean isLeft) {
                g2.setColor(new Color(255, 255, 255, 12));
                g2.fillRect(x, 0, 18, h);
                g2.setColor(new Color(80, 70, 140, 55));
                g2.drawLine(isLeft ? x + 17 : x, 0,
                            isLeft ? x + 17 : x, h);
                g2.setColor(new Color(10, 8, 28));
                for (int y = 12; y < h - 8; y += 24)
                    g2.fillRoundRect(x + 3, y, 12, 14, 4, 4);
            }
        };
    }

    private JPanel crearPanelLogo() {
        JPanel p = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                        RenderingHints.VALUE_ANTIALIAS_ON);

                int cx = getWidth() / 2;
                int cy = getHeight() / 2 + 2;
                int r  = 38;

                // Pulsing outer and middle glow rings
                float pulse = (float)(Math.sin(glowPulse) * 0.5 + 0.5);
                int ex = (int)(pulse * 8);

                g2.setColor(new Color(99, 102, 241, 13 + (int)(pulse * 14)));
                g2.fillOval(cx - r - 22 - ex, cy - r - 22 - ex,
                        (r + 22 + ex) * 2, (r + 22 + ex) * 2);

                g2.setColor(new Color(99, 102, 241, 28 + (int)(pulse * 18)));
                g2.fillOval(cx - r - 12, cy - r - 12,
                        (r + 12) * 2, (r + 12) * 2);

                // Main circle
                g2.setPaint(new GradientPaint(
                        cx - r, cy - r, new Color(99, 102, 241),
                        cx + r, cy + r, new Color(67, 56, 202)));
                g2.fillOval(cx - r, cy - r, r * 2, r * 2);

                // Top-left highlight
                g2.setColor(new Color(255, 255, 255, 30));
                g2.fillOval(cx - r + 4, cy - r + 4, r - 4, r - 4);

                // Cinema screen
                g2.setColor(new Color(255, 255, 255, 220));
                g2.fillRoundRect(cx - 18, cy - 22, 36, 22, 4, 4);

                // Screen content lines
                g2.setColor(new Color(99, 102, 241));
                g2.fillRect(cx - 13, cy - 18, 26, 2);
                g2.fillRect(cx - 13, cy - 13, 26, 2);
                g2.fillRect(cx - 13, cy -  8, 16, 2);

                // Front seat row
                int[] rowA = {cx - 14, cx - 5, cx + 4, cx + 13};
                for (int sx : rowA) {
                    g2.setColor(new Color(255, 255, 255, 200));
                    g2.fillRoundRect(sx, cy + 4, 7, 8, 3, 3);
                    g2.setColor(new Color(255, 255, 255, 130));
                    g2.fillRoundRect(sx, cy + 2, 7, 4, 2, 2);
                }
                // Back seat row
                int[] rowB = {cx - 10, cx - 2, cx + 7};
                for (int sx : rowB) {
                    g2.setColor(new Color(255, 255, 255, 155));
                    g2.fillRoundRect(sx, cy + 15, 7, 7, 3, 3);
                    g2.setColor(new Color(255, 255, 255, 95));
                    g2.fillRoundRect(sx, cy + 13, 7, 3, 2, 2);
                }

                g2.dispose();
            }

            @Override
            public Dimension getPreferredSize() { return new Dimension(0, 100); }
        };
        p.setOpaque(false);
        return p;
    }

    // ── Form components ─────────────────────────────────────────────────────────

    private JPanel crearCampoEtiquetado(String label, boolean esPassword) {
        JPanel p = new JPanel(new BorderLayout(0, 4));
        p.setOpaque(false);

        JLabel lbl = new JLabel(label);
        lbl.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lbl.setForeground(new Color(148, 163, 184));
        p.add(lbl, BorderLayout.NORTH);

        JComponent campo;
        Color caretColor = new Color(139, 92, 246);
        if (esPassword) {
            txtPassword = new JPasswordField();
            txtPassword.setCaretColor(caretColor);
            campo = txtPassword;
        } else {
            txtUsuario = new JTextField();
            txtUsuario.setCaretColor(caretColor);
            campo = txtUsuario;
        }

        campo.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        campo.setForeground(Color.WHITE);
        campo.setBackground(new Color(28, 25, 60));
        campo.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(50, 46, 90), 1, true),
                new EmptyBorder(10, 14, 10, 14)));
        campo.setOpaque(true);
        campo.setPreferredSize(new Dimension(0, 44));

        Color normalBorder = new Color(50, 46, 90);
        Color focusBorder  = new Color(99, 102, 241);
        float[] anim  = {0f};
        Timer[] timer = {null};

        campo.addFocusListener(new FocusAdapter() {
            @Override public void focusGained(FocusEvent e) {
                animateBorder(campo, anim, timer, normalBorder, focusBorder, true);
            }
            @Override public void focusLost(FocusEvent e) {
                animateBorder(campo, anim, timer, normalBorder, focusBorder, false);
            }
        });

        p.add(campo, BorderLayout.CENTER);
        return p;
    }

    private void animateBorder(JComponent c, float[] anim, Timer[] timer,
                               Color from, Color to, boolean toFocus) {
        if (timer[0] != null) timer[0].stop();
        timer[0] = new Timer(12, null);
        timer[0].addActionListener(e -> {
            anim[0] = toFocus
                    ? Math.min(1f, anim[0] + 0.12f)
                    : Math.max(0f, anim[0] - 0.12f);
            int bw = anim[0] > 0.1f ? 2 : 1;
            int pad = 10 - (bw - 1);
            c.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(blend(from, to, anim[0]), bw, true),
                    new EmptyBorder(pad, 14 - (bw - 1), pad, 14 - (bw - 1))));
            if (anim[0] <= 0f || anim[0] >= 1f) timer[0].stop();
        });
        timer[0].start();
    }

    private Color blend(Color a, Color b, float t) {
        return new Color(
                clamp((int)(a.getRed()   + (b.getRed()   - a.getRed())   * t)),
                clamp((int)(a.getGreen() + (b.getGreen() - a.getGreen()) * t)),
                clamp((int)(a.getBlue()  + (b.getBlue()  - a.getBlue())  * t)));
    }

    private int clamp(int v) { return Math.max(0, Math.min(255, v)); }

    private JButton crearBotonLogin() {
        boolean[] hovered = {false};
        boolean[] pressed = {false};

        JButton btn = new JButton("Iniciar Sesión") {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                        RenderingHints.VALUE_ANTIALIAS_ON);
                int w = getWidth(), h = getHeight();

                Color c1, c2;
                if (pressed[0]) {
                    c1 = new Color(55, 48, 163); c2 = new Color(67, 56, 202);
                } else if (hovered[0]) {
                    c1 = new Color(109, 112, 255); c2 = new Color(79, 72, 215);
                } else {
                    c1 = new Color(99, 102, 241); c2 = new Color(67, 56, 202);
                }

                g2.setPaint(new GradientPaint(0, 0, c1, 0, h, c2));
                g2.fill(new RoundRectangle2D.Float(0, 0, w, h, 10, 10));

                if (!pressed[0]) {
                    g2.setColor(new Color(255, 255, 255, 22));
                    g2.fill(new RoundRectangle2D.Float(2, 2, w - 4, h / 2f, 8, 8));
                }

                if (hovered[0] && !pressed[0]) {
                    g2.setPaint(new GradientPaint(
                            w * 0.55f, 0, new Color(255, 255, 255, 0),
                            w * 0.88f, 0, new Color(255, 255, 255, 20)));
                    g2.fill(new RoundRectangle2D.Float(0, 0, w, h, 10, 10));
                }

                g2.setFont(new Font("Segoe UI", Font.BOLD, 14));
                g2.setColor(Color.WHITE);
                FontMetrics fm = g2.getFontMetrics();
                String txt = getText();
                g2.drawString(txt,
                        (w - fm.stringWidth(txt)) / 2,
                        (h - fm.getHeight()) / 2 + fm.getAscent());

                g2.dispose();
            }
        };

        btn.setPreferredSize(new Dimension(0, 46));
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setContentAreaFilled(false);
        btn.setOpaque(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));

        btn.addMouseListener(new MouseAdapter() {
            @Override public void mouseEntered(MouseEvent e)  { hovered[0] = true;  btn.repaint(); }
            @Override public void mouseExited(MouseEvent e)   { hovered[0] = false; btn.repaint(); }
            @Override public void mousePressed(MouseEvent e)  { pressed[0] = true;  btn.repaint(); }
            @Override public void mouseReleased(MouseEvent e) { pressed[0] = false; btn.repaint(); }
        });

        btn.addActionListener(e -> intentarLogin());
        return btn;
    }

    // ── Logic ───────────────────────────────────────────────────────────────────

    private void intentarLogin() {
        String usuario  = txtUsuario.getText().trim();
        String password = new String(txtPassword.getPassword());

        if (usuario.isEmpty() || password.isEmpty()) {
            lblError.setText("Completa todos los campos.");
            shakeWindow();
            return;
        }

        String esperado = USUARIOS.get(usuario);
        if (esperado != null && esperado.equals(password)) {
            loginExitoso  = true;
            usuarioActual = usuario;
            if (animTimer != null) animTimer.stop();
            dispose();
        } else {
            lblError.setText("Usuario o contraseña incorrectos.");
            txtPassword.setText("");
            txtPassword.requestFocus();
            shakeWindow();
        }
    }

    private void shakeWindow() {
        Point origin = getLocation();
        int[] offsets = {-10, 10, -8, 8, -5, 5, -2, 2, 0};
        int[] step    = {0};
        Timer t = new Timer(35, null);
        t.addActionListener(e -> {
            if (step[0] >= offsets.length) {
                setLocation(origin);
                t.stop();
            } else {
                setLocation(origin.x + offsets[step[0]], origin.y);
                step[0]++;
            }
        });
        t.start();
    }
}
