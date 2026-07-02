package view;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

/**
 * Diálogo de configuración inicial del tamaño de la sala.
 * Se muestra antes de abrir la ventana principal.
 */
public class DialogTamanoSala extends JDialog {

    private JSpinner spinFilas;
    private JSpinner spinColumnas;
    private boolean confirmado = false;

    public DialogTamanoSala() {
        super((Frame) null, "Configurar Tamaño de la Sala", true);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setResizable(false);
        inicializarComponentes();
        pack();
        setLocationRelativeTo(null);
    }

    private void inicializarComponentes() {
        JPanel root = new JPanel(new BorderLayout(0, 16));
        root.setBorder(new EmptyBorder(24, 28, 20, 28));
        root.setBackground(new Color(15, 12, 35));

        JLabel titulo = new JLabel("  Configurar Sala de Cine");
        titulo.setFont(new Font("Segoe UI", Font.BOLD, 16));
        titulo.setForeground(Color.WHITE);
        root.add(titulo, BorderLayout.NORTH);

        JPanel campos = new JPanel(new GridLayout(2, 2, 12, 10));
        campos.setOpaque(false);

        JLabel lblFilas = new JLabel("Numero de filas (1-15):");
        lblFilas.setForeground(new Color(148, 163, 184));
        lblFilas.setFont(new Font("Segoe UI", Font.PLAIN, 13));

        spinFilas = new JSpinner(new SpinnerNumberModel(5, 1, 15, 1));
        estilizarSpinner(spinFilas);

        JLabel lblCols = new JLabel("Numero de columnas (1-15):");
        lblCols.setForeground(new Color(148, 163, 184));
        lblCols.setFont(new Font("Segoe UI", Font.PLAIN, 13));

        spinColumnas = new JSpinner(new SpinnerNumberModel(6, 1, 15, 1));
        estilizarSpinner(spinColumnas);

        campos.add(lblFilas);
        campos.add(spinFilas);
        campos.add(lblCols);
        campos.add(spinColumnas);
        root.add(campos, BorderLayout.CENTER);

        JPanel panelBotones = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        panelBotones.setOpaque(false);

        JButton btnCancelar = crearBoton("Cancelar", new Color(71, 85, 105));
        btnCancelar.addActionListener(e -> dispose());

        JButton btnAceptar = crearBoton("Crear Sala", new Color(99, 102, 241));
        btnAceptar.addActionListener(e -> {
            confirmado = true;
            dispose();
        });

        panelBotones.add(btnCancelar);
        panelBotones.add(btnAceptar);
        root.add(panelBotones, BorderLayout.SOUTH);

        setContentPane(root);
    }

    private void estilizarSpinner(JSpinner spinner) {
        spinner.setFont(new Font("Segoe UI", Font.BOLD, 13));
        JComponent editor = spinner.getEditor();
        if (editor instanceof JSpinner.DefaultEditor) {
            JTextField tf = ((JSpinner.DefaultEditor) editor).getTextField();
            tf.setBackground(new Color(30, 27, 75));
            tf.setForeground(Color.WHITE);
            tf.setCaretColor(Color.WHITE);
        }
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
        btn.setPreferredSize(new Dimension(120, 36));
        return btn;
    }

    public boolean isConfirmado() {
        return confirmado;
    }

    public int getFilasSeleccionadas() {
        return (int) spinFilas.getValue();
    }

    public int getColumnasSeleccionadas() {
        return (int) spinColumnas.getValue();
    }
}
