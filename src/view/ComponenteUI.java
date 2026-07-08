package view;

import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.RoundRectangle2D;
import javax.swing.*;
import javax.swing.border.EmptyBorder;

/**
 * Fábrica de componentes UI reutilizables con el tema oscuro del sistema.
 * Centraliza la creación de tarjetas, botones, badges y separadores para
 * mantener coherencia visual en todo el frontend.
 *
 * PARADIGMA: Orientado a Objetos — clase utilitaria de fábrica (Factory Method)
 * SOLID: SRP — responsabilidad única: construir componentes visuales
 * estilizados.
 */
public final class ComponenteUI {

    private ComponenteUI() {
    }

    // =========================================================================
    // TARJETAS (paneles redondeados con fondo y borde)
    // =========================================================================
    /**
     * Crea una tarjeta estándar con fondo oscuro, borde sutil y radio de
     * esquinas.
     */
    public static JPanel tarjeta() {
        return tarjeta(UIConstants.BG_TARJETA, UIConstants.BORDE, UIConstants.RADIO_TARJETA);
    }

    /**
     * Crea una tarjeta con color de fondo, borde y radio personalizados.
     */
    public static JPanel tarjeta(Color fondo, Color borde, int radio) {
        JPanel panel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(fondo);
                g2.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), radio, radio));
                g2.setColor(borde);
                g2.setStroke(new java.awt.BasicStroke(1f));
                g2.draw(new RoundRectangle2D.Float(0, 0, getWidth() - 1, getHeight() - 1, radio, radio));
                g2.dispose();
            }
        };
        panel.setOpaque(false);
        panel.setBorder(new EmptyBorder(14, 16, 14, 16));
        return panel;
    }

    /**
     * Tarjeta con borde de acento violeta (resaltada).
     */
    public static JPanel tarjetaAcento() {
        return tarjeta(UIConstants.BG_TARJETA, UIConstants.ACENTO, UIConstants.RADIO_TARJETA);
    }

    // =========================================================================
    // BOTONES
    // =========================================================================
    /**
     * Crea un botón estilizado con color de fondo, efecto hover y cursor mano.
     */
    public static JButton boton(String texto, Color colorBase, Color colorHover) {
        JButton btn = new JButton(texto) {
            private Color colorActual = colorBase;

            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(colorActual);
                g2.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(),
                        UIConstants.RADIO_BOTON, UIConstants.RADIO_BOTON));
                g2.dispose();
                super.paintComponent(g);
            }

            { // bloque init
                addMouseListener(new MouseAdapter() {
                    @Override
                    public void mouseEntered(MouseEvent e) {
                        colorActual = colorHover;
                        repaint();
                    }

                    @Override
                    public void mouseExited(MouseEvent e) {
                        colorActual = colorBase;
                        repaint();
                    }
                });
            }
        };
        btn.setFont(UIConstants.FUENTE_BOTON);
        btn.setForeground(Color.WHITE);
        btn.setBackground(colorBase);
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setContentAreaFilled(false);
        btn.setOpaque(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setPreferredSize(new Dimension(0, 40));
        return btn;
    }

    /**
     * Botón primario (violeta).
     */
    public static JButton botonPrimario(String texto) {
        return boton(texto, UIConstants.BTN_PRIMARIO, UIConstants.BTN_PRIMARIO_HOVER);
    }

    /**
     * Botón de éxito/reserva (verde — mismo que COLOR_LIBRE).
     */
    public static JButton botonExito(String texto) {
        return boton(texto, UIConstants.BTN_EXITO, UIConstants.BTN_EXITO_HOVER);
    }

    /**
     * Botón de alerta/cancelar reserva (ámbar — mismo que COLOR_RESERVADO).
     */
    public static JButton botonAlerta(String texto) {
        return boton(texto, UIConstants.BTN_ALERTA, UIConstants.BTN_ALERTA_HOVER);
    }

    /**
     * Botón de peligro/eliminar (rojo — mismo que COLOR_OCUPADO).
     */
    public static JButton botonPeligro(String texto) {
        return boton(texto, UIConstants.BTN_PELIGRO, UIConstants.BTN_PELIGRO_HOVER);
    }

    /**
     * Botón secundario/neutro (pizarra gris).
     */
    public static JButton botonSecundario(String texto) {
        return boton(texto, UIConstants.BTN_SECUNDARIO, UIConstants.BTN_SECUNDARIO_HOVER);
    }

    // =========================================================================
    // BADGES (etiquetas de conteo redondeadas)
    // =========================================================================
    /**
     * Crea un badge pill (cápsula) con número y color.
     */
    public static JLabel badge(String numero, Color color) {
        JLabel lbl = new JLabel(numero, SwingConstants.CENTER) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(color.getRed(), color.getGreen(), color.getBlue(), 30));
                g2.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), getHeight(), getHeight()));
                g2.setColor(color);
                g2.setStroke(new java.awt.BasicStroke(1.2f));
                g2.draw(new RoundRectangle2D.Float(0, 0, getWidth() - 1, getHeight() - 1, getHeight(), getHeight()));
                g2.dispose();
                super.paintComponent(g);
            }
        };
        lbl.setFont(UIConstants.FUENTE_BADGE);
        lbl.setForeground(color);
        lbl.setOpaque(false);
        lbl.setBorder(new EmptyBorder(2, 10, 2, 10));
        lbl.setPreferredSize(null);   // deja que el layout calcule el ancho real
        return lbl;
    }

    // =========================================================================
    // SEPARADORES
    // =========================================================================
    /**
     * Separador horizontal fino del color de borde del tema.
     */
    public static JSeparator separador() {
        JSeparator sep = new JSeparator();
        sep.setForeground(UIConstants.BORDE);
        sep.setBackground(UIConstants.BG_TARJETA);
        return sep;
    }

    // =========================================================================
    // ETIQUETAS
    // =========================================================================
    /**
     * Etiqueta de título (blanco, negrita grande).
     */
    public static JLabel titulo(String texto) {
        JLabel lbl = new JLabel(texto);
        lbl.setFont(UIConstants.FUENTE_TITULO);
        lbl.setForeground(UIConstants.TEXTO_PRIMARIO);
        return lbl;
    }

    /**
     * Etiqueta de subtítulo de sección.
     */
    public static JLabel subtitulo(String texto) {
        JLabel lbl = new JLabel(texto);
        lbl.setFont(UIConstants.FUENTE_SUBTITULO);
        lbl.setForeground(UIConstants.TEXTO_PRIMARIO);
        return lbl;
    }

    /**
     * Etiqueta de cuerpo (gris secundario).
     */
    public static JLabel cuerpo(String texto) {
        JLabel lbl = new JLabel(texto);
        lbl.setFont(UIConstants.FUENTE_CUERPO);
        lbl.setForeground(UIConstants.TEXTO_SECUNDARIO);
        return lbl;
    }

    /**
     * Etiqueta pequeña tenue.
     */
    public static JLabel pequena(String texto) {
        JLabel lbl = new JLabel(texto);
        lbl.setFont(UIConstants.FUENTE_PEQUENA);
        lbl.setForeground(UIConstants.TEXTO_TENUE);
        return lbl;
    }

    // =========================================================================
    // SPINNERS
    // =========================================================================
    /**
     * Spinner estilizado con tema oscuro.
     */
    public static JSpinner spinner(int min, int max, int valorInicial) {
        JSpinner sp = new JSpinner(new SpinnerNumberModel(valorInicial, min, max, 1));
        sp.setFont(UIConstants.FUENTE_NEGRITA);
        JComponent editor = sp.getEditor();
        if (editor instanceof JSpinner.DefaultEditor de) {
            de.getTextField().setBackground(UIConstants.BG_INPUT);
            de.getTextField().setForeground(UIConstants.TEXTO_PRIMARIO);
            de.getTextField().setCaretColor(UIConstants.TEXTO_PRIMARIO);
            de.getTextField().setHorizontalAlignment(JTextField.CENTER);
            de.getTextField().setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(UIConstants.BORDE),
                    new EmptyBorder(4, 8, 4, 8)
            ));
        }
        sp.setBorder(BorderFactory.createLineBorder(UIConstants.BORDE));
        return sp;
    }

    // =========================================================================
    // CAMPOS DE TEXTO
    // =========================================================================
    /**
     * Campo de texto estilizado con tema oscuro.
     */
    public static JTextField campo(String placeholder) {
        JTextField tf = new JTextField();
        tf.setFont(UIConstants.FUENTE_CUERPO);
        tf.setBackground(UIConstants.BG_INPUT);
        tf.setForeground(UIConstants.TEXTO_PRIMARIO);
        tf.setCaretColor(UIConstants.TEXTO_PRIMARIO);
        tf.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(UIConstants.BORDE),
                new EmptyBorder(6, 10, 6, 10)
        ));
        tf.setPreferredSize(new Dimension(0, 38));
        // Focus highlight
        tf.addFocusListener(new java.awt.event.FocusAdapter() {
            @Override
            public void focusGained(java.awt.event.FocusEvent e) {
                tf.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(UIConstants.BORDE_FOCUS, 2),
                        new EmptyBorder(5, 9, 5, 9)));
            }

            @Override
            public void focusLost(java.awt.event.FocusEvent e) {
                tf.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(UIConstants.BORDE),
                        new EmptyBorder(6, 10, 6, 10)));
            }
        });
        return tf;
    }
}
