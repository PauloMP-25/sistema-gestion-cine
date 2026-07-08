package view.dialog;

import view.*;
import view.frame.*;
import view.panel.*;
import view.dialog.*;
import view.component.*;
import model.sala.*;
import model.butaca.*;
import model.reserva.*;
import model.auth.*;


import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.Graphics;

/**
 * Ventana modal de confirmación que se muestra al confirmar una reserva.
 * Informa al usuario la fila y columna seleccionadas y solicita confirmación
 * antes de ejecutar la operación contra el backend.
 */
public class DialogReserva extends JDialog {

    private boolean confirmado;

    /**
     * Crea el diálogo modal de confirmación de reserva.
     * @param padre   Ventana padre sobre la cual se centra el diálogo.
     * @param fila    Fila de la butaca (base 0, se muestra en base 1).
     * @param columna Columna de la butaca (base 0, se muestra en base 1).
     */
    public DialogReserva(Frame padre, int fila, int columna, int numeroAsiento) {
        super(padre, "Confirmar Reserva", true);
        this.confirmado = false;
        inicializarComponentes(fila, columna, numeroAsiento);
        configurarEventos();
        setSize(360, 200);
        setLocationRelativeTo(padre);
        setResizable(false);
    }

    // Solo crea y posiciona componentes; sin listeners aquí.
    private void inicializarComponentes(int fila, int columna, int numeroAsiento) {
        getContentPane().setBackground(UIConstants.BG_PANEL);
        setLayout(new BorderLayout(0, 0));

        JPanel cuerpo = new JPanel(new BorderLayout(0, 14));
        cuerpo.setOpaque(false);
        cuerpo.setBorder(new EmptyBorder(20, 24, 10, 24));

        JLabel mensaje = new JLabel(
            "<html><center>¿Confirmar la reserva para el<br>"
            + "<b style='color:#fbbf24'>Asiento " + numeroAsiento + "</b>?</center></html>",
            javax.swing.SwingConstants.CENTER);
        mensaje.setFont(UIConstants.FUENTE_CUERPO);
        mensaje.setForeground(UIConstants.TEXTO_PRIMARIO);

        cuerpo.add(mensaje, BorderLayout.CENTER);

        add(cuerpo, BorderLayout.CENTER);
        add(crearPanelBotones(), BorderLayout.SOUTH);
    }

    private JPanel crearPanelBotones() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 14));
        panel.setOpaque(false);

        JButton btnConfirmar = ComponenteUI.botonExito("Confirmar");
        btnConfirmar.setPreferredSize(new java.awt.Dimension(120, 36));
        btnConfirmar.addActionListener(e -> onConfirmarClick());

        JButton btnCancelar = ComponenteUI.botonPeligro("Cancelar");
        btnCancelar.setPreferredSize(new java.awt.Dimension(120, 36));
        btnCancelar.addActionListener(e -> onCancelarClick());

        panel.add(btnConfirmar);
        panel.add(btnCancelar);
        return panel;
    }

    // Separar configuración de eventos del resto (cierre con la X)
    private void configurarEventos() {
        setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
    }

    // PARADIGMA: Orientado a Eventos — handler con prefijo "on"
    private void onConfirmarClick() {
        confirmado = true;
        dispose();
    }

    private void onCancelarClick() {
        confirmado = false;
        dispose();
    }



    /**
     * Indica si el usuario confirmó la reserva.
     * @return true si presionó "Confirmar", false en caso contrario.
     */
    public boolean isConfirmado() {
        return confirmado;
    }

    @Override
    public void paint(Graphics g) {
        super.paint(g);
    }
}
