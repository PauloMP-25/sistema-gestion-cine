package view;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.geom.RoundRectangle2D;

/**
 * Diálogo que aparece al hacer clic en una butaca RESERVADA.
 * Presenta dos opciones: Comprar (→ OCUPADO) o Cancelar Reserva (→ LIBRE).
 */
public class DialogOpcionesButaca extends JDialog {

    public enum Opcion { COMPRAR, CANCELAR, NINGUNA }

    private Opcion opcionElegida = Opcion.NINGUNA;

    public DialogOpcionesButaca(Frame padre, int fila, int columna) {
        super(padre, "Opciones de Butaca", true);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setResizable(false);
        inicializarComponentes(fila, columna);
        pack();
        setLocationRelativeTo(padre);
    }

    private void inicializarComponentes(int fila, int columna) {
        JPanel root = new JPanel(new BorderLayout(0, 16));
        root.setBorder(new EmptyBorder(24, 32, 22, 32));
        root.setBackground(new Color(15, 12, 35));

        // Encabezado con badge de estado
        JPanel encabezado = new JPanel(new BorderLayout(0, 8));
        encabezado.setOpaque(false);

        JLabel titulo = new JLabel("Fila " + (fila + 1) + "  —  Columna " + (columna + 1));
        titulo.setFont(new Font("Segoe UI", Font.BOLD, 15));
        titulo.setForeground(Color.WHITE);
        titulo.setHorizontalAlignment(SwingConstants.CENTER);

        JLabel badge = new JLabel("RESERVADA") {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(251, 191, 36, 40));
                g2.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 20, 20));
                g2.setColor(new Color(251, 191, 36));
                g2.setStroke(new java.awt.BasicStroke(1f));
                g2.draw(new RoundRectangle2D.Float(0, 0, getWidth() - 1, getHeight() - 1, 20, 20));
                g2.dispose();
                super.paintComponent(g);
            }
        };
        badge.setFont(new Font("Segoe UI", Font.BOLD, 11));
        badge.setForeground(new Color(251, 191, 36));
        badge.setHorizontalAlignment(SwingConstants.CENTER);
        badge.setOpaque(false);
        badge.setBorder(new EmptyBorder(4, 14, 4, 14));

        JPanel badgePanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 0));
        badgePanel.setOpaque(false);
        badgePanel.add(badge);

        encabezado.add(titulo, BorderLayout.NORTH);
        encabezado.add(badgePanel, BorderLayout.SOUTH);
        root.add(encabezado, BorderLayout.NORTH);

        // Descripción
        JLabel desc = new JLabel("<html><center>Esta butaca tiene una reserva activa.<br>¿Qué desea hacer?</center></html>");
        desc.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        desc.setForeground(new Color(148, 163, 184));
        desc.setHorizontalAlignment(SwingConstants.CENTER);
        root.add(desc, BorderLayout.CENTER);

        // Botones
        JPanel panelBotones = new JPanel(new GridLayout(1, 2, 12, 0));
        panelBotones.setOpaque(false);
        panelBotones.setBorder(new EmptyBorder(4, 0, 0, 0));

        JButton btnCancelar = crearBoton("Cancelar Reserva", new Color(239, 68, 68));
        btnCancelar.addActionListener(e -> {
            opcionElegida = Opcion.CANCELAR;
            dispose();
        });

        JButton btnComprar = crearBoton("Comprar", new Color(99, 102, 241));
        btnComprar.addActionListener(e -> {
            opcionElegida = Opcion.COMPRAR;
            dispose();
        });

        panelBotones.add(btnCancelar);
        panelBotones.add(btnComprar);
        root.add(panelBotones, BorderLayout.SOUTH);

        setContentPane(root);
    }

    private JButton crearBoton(String texto, Color color) {
        JButton btn = new JButton(texto);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btn.setBackground(color);
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setOpaque(true);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setPreferredSize(new Dimension(155, 40));
        return btn;
    }

    public Opcion getOpcionElegida() {
        return opcionElegida;
    }
}
