package view.component;

import view.*;

import model.butaca.EstadoButaca;

import javax.swing.JButton;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.RoundRectangle2D;

/**
 * Botón personalizado que representa visualmente una butaca de la sala.
 * Mantiene su propia fila, columna y estado, y se pinta a sí mismo según
 * el color correspondiente definido en UIConstants.
 */
// PARADIGMA: Orientado a Objetos — BotonButaca extiende JButton (herencia)
public class BotonButaca extends JButton {

    private final int fila;
    private final int columna;
    private final int numeroAsiento;
    private EstadoButaca estado;
    private boolean hover;

    /**
     * Crea un botón de butaca para la posición indicada, en estado LIBRE.
     * @param fila    Índice de fila (0 a MAX_FILAS-1)
     * @param columna Índice de columna (0 a MAX_COLS-1)
     */
    public BotonButaca(int fila, int columna, int numeroAsiento) {
        this.fila = fila;
        this.columna = columna;
        this.numeroAsiento = numeroAsiento;
        this.estado = EstadoButaca.LIBRE;
        this.hover = false;
        configurarAspecto();
        configurarEventosHover();
    }

    private void configurarAspecto() {
        setPreferredSize(new java.awt.Dimension(UIConstants.TAMANO_BOTON, UIConstants.ALTO_BOTON));
        setFont(UIConstants.FUENTE_BOTON);
        setFocusPainted(false);
        setBorderPainted(false);
        setContentAreaFilled(false);
        setOpaque(false);
        setCursor(new Cursor(Cursor.HAND_CURSOR));
        actualizarTooltip();
    }

    // PARADIGMA: Orientado a Eventos — MouseListener reacciona a hover del usuario
    private void configurarEventosHover() {
        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                hover = true;
                repaint();
            }

            @Override
            public void mouseExited(MouseEvent e) {
                hover = false;
                repaint();
            }
        });
    }

    /**
     * Actualiza el color del botón según el nuevo estado de la butaca.
     * @param nuevoEstado El nuevo estado de la butaca (LIBRE, RESERVADO u OCUPADO).
     */
    public void actualizarColor(EstadoButaca nuevoEstado) {
        this.estado = nuevoEstado;
        actualizarTooltip();
        repaint();
    }

    private void actualizarTooltip() {
        setToolTipText(String.format("Fila %d - Columna %d (%s)",
                fila + 1, columna + 1, estado.name()));
    }

    /**
     * Devuelve la fila de esta butaca.
     * @return índice de fila en base 0.
     */
    public int getFila() {
        return fila;
    }

    /**
     * Devuelve la columna de esta butaca.
     * @return índice de columna en base 0.
     */
    public int getColumna() {
        return columna;
    }

    /**
     * Devuelve el estado visual actual representado por el botón.
     * @return estado actual.
     */
    public EstadoButaca getEstado() {
        return estado;
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        int w = getWidth();
        int h = getHeight();
        Color base = colorBase();
        Color actual = hover ? colorHover() : base;

        // Respaldo del asiento
        g2.setColor(actual.darker());
        g2.fill(new RoundRectangle2D.Float(3, 2, w - 6, 10, 10, 10));

        // Cuerpo del asiento
        g2.setColor(actual);
        g2.fill(new RoundRectangle2D.Float(3, 8, w - 6, h - 11, 10, 10));

        // Borde sutil
        g2.setColor(new Color(255, 255, 255, 40));
        g2.setStroke(new BasicStroke(1f));
        g2.draw(new RoundRectangle2D.Float(3, 8, w - 7, h - 12, 10, 10));

        // FIX-6: etiqueta más grande y legible
        String etiqueta;
        if (estado == EstadoButaca.OCUPADO) {
            g2.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 15));
            etiqueta = "👤";
        } else {
            g2.setFont(new Font("Segoe UI", Font.BOLD, 13));
            etiqueta = String.valueOf(numeroAsiento);
        }
        g2.setColor(new Color(255, 255, 255, 210));
        FontMetrics fm = g2.getFontMetrics();
        g2.drawString(etiqueta, (w - fm.stringWidth(etiqueta)) / 2, h / 2 + 5);

        g2.dispose();
    }

    private Color colorBase() {
        switch (estado) {
            case RESERVADO: return UIConstants.COLOR_RESERVADO;
            case OCUPADO:   return UIConstants.COLOR_OCUPADO;
            default:        return UIConstants.COLOR_LIBRE;
        }
    }

    private Color colorHover() {
        switch (estado) {
            case RESERVADO: return UIConstants.COLOR_RESERVADO_HOVER;
            case OCUPADO:   return UIConstants.COLOR_OCUPADO_HOVER;
            default:        return UIConstants.COLOR_LIBRE_HOVER;
        }
    }
}
