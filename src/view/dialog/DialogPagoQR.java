package view.dialog;

import view.*;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.geom.RoundRectangle2D;

/**
 * Modal de pago simulado con código QR generado gráficamente.
 * Se muestra cuando el usuario elige "Comprar" en DialogOpcionesButaca.
 */
public class DialogPagoQR extends JDialog {

    private boolean aceptado = false;

    private static final int MODULO = 9;
    private static final int MARGEN = 12;

    // Patrón QR simulado (21x21 módulos) con finder patterns correctos
    private static final int[][] MODULOS = {
        {1,1,1,1,1,1,1,0,1,0,1,0,1,0,1,1,1,1,1,1,1},
        {1,0,0,0,0,0,1,0,0,1,0,1,0,0,1,0,0,0,0,0,1},
        {1,0,1,1,1,0,1,0,1,0,1,0,1,0,1,0,1,1,1,0,1},
        {1,0,1,1,1,0,1,0,0,1,1,0,0,0,1,0,1,1,1,0,1},
        {1,0,1,1,1,0,1,0,1,1,0,1,1,0,1,0,1,1,1,0,1},
        {1,0,0,0,0,0,1,0,0,0,1,0,0,0,1,0,0,0,0,0,1},
        {1,1,1,1,1,1,1,0,1,0,1,0,1,0,1,1,1,1,1,1,1},
        {0,0,0,0,0,0,0,0,1,1,0,1,1,0,0,0,0,0,0,0,0},
        {1,1,0,1,1,0,1,1,0,1,0,1,1,1,0,1,0,1,1,0,1},
        {0,1,1,0,0,1,0,0,1,0,1,1,0,0,1,0,1,1,0,0,0},
        {1,0,1,1,0,0,1,0,1,1,0,1,1,0,1,1,0,0,1,0,1},
        {0,1,0,0,1,0,0,1,1,0,1,0,0,1,1,0,1,0,0,1,0},
        {1,1,1,0,1,0,1,0,0,1,0,1,0,0,1,1,0,1,1,0,1},
        {0,0,0,0,0,0,0,0,1,0,1,1,0,1,0,0,1,0,0,1,0},
        {1,1,1,1,1,1,1,0,0,1,1,0,1,0,1,0,0,0,0,0,0},
        {1,0,0,0,0,0,1,0,1,0,0,1,0,1,1,0,0,0,0,0,0},
        {1,0,1,1,1,0,1,0,0,1,1,0,1,0,1,1,1,0,0,0,0},
        {1,0,1,1,1,0,1,0,1,1,0,1,0,1,1,0,0,1,0,1,1},
        {1,0,1,1,1,0,1,0,0,0,1,0,1,0,0,1,0,0,0,0,1},
        {1,0,0,0,0,0,1,0,1,0,0,1,0,1,0,0,1,0,1,0,0},
        {1,1,1,1,1,1,1,0,1,1,0,0,1,0,0,1,0,1,0,1,1}
    };

    public DialogPagoQR(Frame padre, int fila, int columna) {
        super(padre, "Confirmar Pago", true);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setResizable(false);
        inicializarComponentes(fila, columna);
        pack();
        setLocationRelativeTo(padre);
    }

    private void inicializarComponentes(int fila, int columna) {
        JPanel root = new JPanel(new BorderLayout(0, 14));
        root.setBorder(new EmptyBorder(20, 28, 20, 28));
        root.setBackground(new Color(15, 12, 35));

        // Encabezado
        JPanel encabezado = new JPanel(new BorderLayout(0, 4));
        encabezado.setOpaque(false);

        JLabel titulo = new JLabel("Escanea para completar el pago");
        titulo.setFont(new Font("Segoe UI", Font.BOLD, 14));
        titulo.setForeground(Color.WHITE);
        titulo.setHorizontalAlignment(SwingConstants.CENTER);

        JLabel subtitulo = new JLabel("Butaca F" + (fila + 1) + " - Columna " + (columna + 1));
        subtitulo.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        subtitulo.setForeground(new Color(148, 163, 184));
        subtitulo.setHorizontalAlignment(SwingConstants.CENTER);

        encabezado.add(titulo, BorderLayout.NORTH);
        encabezado.add(subtitulo, BorderLayout.SOUTH);
        root.add(encabezado, BorderLayout.NORTH);

        // Panel QR con borde redondeado
        int qrContentSize = 21 * MODULO;
        int qrPanelSize = qrContentSize + MARGEN * 2;

        JPanel panelQR = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                int px = (getWidth() - qrPanelSize) / 2;
                int py = (getHeight() - qrPanelSize) / 2;

                // Sombra sutil
                g2.setColor(new Color(0, 0, 0, 60));
                g2.fill(new RoundRectangle2D.Float(px + 3, py + 3, qrPanelSize, qrPanelSize, 12, 12));

                // Fondo blanco del QR
                g2.setColor(Color.WHITE);
                g2.fill(new RoundRectangle2D.Float(px, py, qrPanelSize, qrPanelSize, 12, 12));

                // Módulos
                int ox = px + MARGEN;
                int oy = py + MARGEN;
                for (int row = 0; row < MODULOS.length; row++) {
                    for (int col = 0; col < MODULOS[row].length; col++) {
                        if (MODULOS[row][col] == 1) {
                            g2.setColor(new Color(10, 8, 28));
                            g2.fillRect(ox + col * MODULO, oy + row * MODULO, MODULO, MODULO);
                        }
                    }
                }
                g2.dispose();
            }
        };
        panelQR.setPreferredSize(new Dimension(qrPanelSize + 10, qrPanelSize + 10));
        panelQR.setOpaque(false);
        root.add(panelQR, BorderLayout.CENTER);

        // Panel inferior: precio + divisor + botón
        JPanel panelInferior = new JPanel(new BorderLayout(0, 10));
        panelInferior.setOpaque(false);

        // Precio
        JLabel lblPrecio = new JLabel("S/ 15.00");
        lblPrecio.setFont(new Font("Segoe UI", Font.BOLD, 18));
        lblPrecio.setForeground(new Color(34, 197, 94));
        lblPrecio.setHorizontalAlignment(SwingConstants.CENTER);

        JLabel lblDesc = new JLabel("Precio de entrada");
        lblDesc.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        lblDesc.setForeground(new Color(100, 116, 139));
        lblDesc.setHorizontalAlignment(SwingConstants.CENTER);

        JPanel precioPanel = new JPanel(new BorderLayout(0, 2));
        precioPanel.setOpaque(false);
        precioPanel.add(lblPrecio, BorderLayout.NORTH);
        precioPanel.add(lblDesc, BorderLayout.SOUTH);

        // Divisor
        JSeparator sep = new JSeparator();
        sep.setForeground(new Color(51, 65, 85));
        sep.setBackground(new Color(51, 65, 85));

        // Botón
        JButton btnAceptar = ComponenteUI.botonExito("Confirmar Pago");
        btnAceptar.addActionListener(e -> {
            aceptado = true;
            dispose();
        });

        panelInferior.add(precioPanel, BorderLayout.NORTH);
        panelInferior.add(sep, BorderLayout.CENTER);
        panelInferior.add(btnAceptar, BorderLayout.SOUTH);
        root.add(panelInferior, BorderLayout.SOUTH);

        setContentPane(root);
    }

    public boolean isAceptado() {
        return aceptado;
    }
}
