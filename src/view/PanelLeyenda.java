package view;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.RoundRectangle2D;

/**
 * Panel que muestra la leyenda de colores de la sala
 * (Verde = Libre, Amarillo = Reservado, Rojo = Ocupado).
 * Debe estar siempre visible en la ventana principal.
 */
public class PanelLeyenda extends JPanel {

    /**
     * Construye el panel de leyenda con los tres indicadores de color.
     */
    public PanelLeyenda() {
        setOpaque(false);
        setLayout(new FlowLayout(FlowLayout.LEFT, 18, 6));
        setBorder(new EmptyBorder(8, 12, 8, 12));
        inicializarComponentes();
    }

    // Separación de la inicialización de componentes (sin listeners aquí)
    private void inicializarComponentes() {
        add(crearItem("Libre", UIConstants.COLOR_LIBRE));
        add(crearItem("Reservado", UIConstants.COLOR_RESERVADO));
        add(crearItem("Ocupado", UIConstants.COLOR_OCUPADO));
    }

    private JPanel crearItem(String texto, Color color) {
        JPanel item = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 0));
        item.setOpaque(false);

        JPanel cuadro = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(color);
                g2.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 5, 5));
                g2.dispose();
            }
        };
        cuadro.setOpaque(false);
        cuadro.setPreferredSize(new java.awt.Dimension(16, 16));

        JLabel lbl = new JLabel(texto);
        lbl.setFont(UIConstants.FUENTE_CUERPO);
        lbl.setForeground(UIConstants.TEXTO_SECUNDARIO);

        item.add(cuadro);
        item.add(lbl);
        return item;
    }
}


