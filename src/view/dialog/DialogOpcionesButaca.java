package view.dialog;

import view.*;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

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

        JButton btnCancelar = ComponenteUI.botonPeligro("Cancelar Reserva");
        btnCancelar.setPreferredSize(new Dimension(155, 40));
        btnCancelar.addActionListener(e -> {
            opcionElegida = Opcion.CANCELAR;
            dispose();
        });

        JButton btnComprar = ComponenteUI.botonPrimario("Comprar");
        btnComprar.setPreferredSize(new Dimension(155, 40));
        btnComprar.addActionListener(e -> {
            opcionElegida = Opcion.COMPRAR;
            dispose();
        });

        panelBotones.add(btnCancelar);
        panelBotones.add(btnComprar);
        root.add(panelBotones, BorderLayout.SOUTH);

        setContentPane(root);
    }



    public Opcion getOpcionElegida() {
        return opcionElegida;
    }
}
