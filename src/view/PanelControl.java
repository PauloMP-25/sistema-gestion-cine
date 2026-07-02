package view;

import exception.AsientoNoReservadoException;
import exception.AsientoOcupadoException;
import exception.AsientoYaReservadoException;
import exception.PosicionInvalidaException;
import service.interfaces.ISalaQuery;
import service.interfaces.ISalaService;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.JTextField;
import javax.swing.SpinnerNumberModel;
import javax.swing.border.EmptyBorder;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.RenderingHints;
import java.awt.geom.RoundRectangle2D;

/**
 * Panel con los botones de acción del sistema: Reservar, Cancelar Reserva
 * y Contar Libres. Cada botón tiene su propio ActionListener que delega
 * en ISalaService / ISalaQuery y captura las excepciones del backend.
 */
public class PanelControl extends JPanel {

    private final ISalaService salaService;
    private final ISalaQuery salaQuery;
    private final int filas;
    private final int columnas;
    private JSpinner spinFila;
    private JSpinner spinColumna;
    private JLabel lblEstadisticas;
    private Runnable alCambiarEstado;

    /**
     * Crea el panel de control recibiendo las dependencias del backend
     * por constructor.
     * @param salaService servicio de operaciones de escritura.
     * @param salaQuery   servicio de consultas de solo lectura.
     */
    public PanelControl(ISalaService salaService, ISalaQuery salaQuery) {
        this.salaService = salaService;
        this.salaQuery = salaQuery;
        var matriz = salaQuery.obtenerMatriz();
        this.filas = matriz.length;
        this.columnas = matriz.length > 0 ? matriz[0].length : 0;
        setOpaque(false);
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        inicializarComponentes();
        actualizarEstadisticas();
    }

    // Solo crea y posiciona componentes
    private void inicializarComponentes() {
        add(crearTitulo());
        add(Box.createVerticalStrut(12));
        add(crearPanelCoordenadas());
        add(Box.createVerticalStrut(12));
        add(crearPanelBotones());
        add(Box.createVerticalStrut(12));
        add(crearPanelEstadisticas());
    }

    private JPanel crearTitulo() {
        JPanel panel = new JPanel(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT, 0, 0));
        panel.setOpaque(false);
        JLabel lbl = new JLabel("⚙  Panel de Control");
        lbl.setFont(UIConstants.FUENTE_SUBTITULO);
        lbl.setForeground(UIConstants.TEXTO_PRIMARIO);
        panel.add(lbl);
        return panel;
    }

    private JPanel crearPanelCoordenadas() {
        JPanel panel = crearTarjeta();
        panel.setLayout(new java.awt.BorderLayout(0, 8));

        JLabel titulo = new JLabel("Seleccionar Butaca");
        titulo.setFont(UIConstants.FUENTE_NEGRITA);
        titulo.setForeground(UIConstants.TEXTO_PRIMARIO);
        panel.add(titulo, java.awt.BorderLayout.NORTH);

        JPanel campos = new JPanel(new GridLayout(1, 4, 8, 0));
        campos.setOpaque(false);

        JLabel lblFila = etiqueta("Fila:");
        spinFila = crearSpinner(filas);

        JLabel lblCol = etiqueta("Columna:");
        spinColumna = crearSpinner(columnas);

        campos.add(lblFila);
        campos.add(spinFila);
        campos.add(lblCol);
        campos.add(spinColumna);

        panel.add(campos, java.awt.BorderLayout.CENTER);
        return panel;
    }

    private JPanel crearPanelBotones() {
        JPanel panel = crearTarjeta();
        panel.setLayout(new java.awt.BorderLayout(0, 8));

        JLabel titulo = new JLabel("Operaciones");
        titulo.setFont(UIConstants.FUENTE_NEGRITA);
        titulo.setForeground(UIConstants.TEXTO_PRIMARIO);
        panel.add(titulo, java.awt.BorderLayout.NORTH);

        JPanel botones = new JPanel(new GridLayout(3, 1, 0, 8));
        botones.setOpaque(false);

        JButton btnReservar = crearBoton("Reservar Butaca", UIConstants.BTN_PRIMARIO);
        btnReservar.addActionListener(e -> onReservarClick());

        JButton btnCancelar = crearBoton("Cancelar Reserva", UIConstants.BTN_PELIGRO);
        btnCancelar.addActionListener(e -> onCancelarClick());

        JButton btnContar = crearBoton("Contar Libres", UIConstants.BTN_EXITO);
        btnContar.addActionListener(e -> onContarClick());

        botones.add(btnReservar);
        botones.add(btnCancelar);
        botones.add(btnContar);

        panel.add(botones, java.awt.BorderLayout.CENTER);
        return panel;
    }

    private JPanel crearPanelEstadisticas() {
        JPanel panel = crearTarjeta();
        panel.setLayout(new java.awt.BorderLayout(0, 6));

        JLabel titulo = new JLabel("Estadísticas");
        titulo.setFont(UIConstants.FUENTE_NEGRITA);
        titulo.setForeground(UIConstants.TEXTO_PRIMARIO);
        panel.add(titulo, java.awt.BorderLayout.NORTH);

        lblEstadisticas = new JLabel();
        lblEstadisticas.setFont(UIConstants.FUENTE_CUERPO);
        panel.add(lblEstadisticas, java.awt.BorderLayout.CENTER);
        return panel;
    }

    // -----------------------------------------------------------
    // HANDLERS — PARADIGMA: Orientado a Eventos
    // -----------------------------------------------------------

    private void onReservarClick() {
        int fila = filaSeleccionada();
        int columna = columnaSeleccionada();
        DialogReserva dialogo = new DialogReserva(ventanaPadre(), fila, columna);
        dialogo.setVisible(true);
        if (dialogo.isConfirmado()) {
            ejecutarReserva(fila, columna);
        }
    }

    private void ejecutarReserva(int fila, int columna) {
        try {
            salaService.reservar(fila, columna);
            notificarCambio();
        } catch (PosicionInvalidaException ex) {
            mostrarError("Posición inválida: " + ex.getMessage());
        } catch (AsientoOcupadoException ex) {
            mostrarError("Este asiento está ocupado.");
        } catch (AsientoYaReservadoException ex) {
            mostrarError("Este asiento ya fue reservado.");
        }
    }

    private void onCancelarClick() {
        int fila = filaSeleccionada();
        int columna = columnaSeleccionada();
        try {
            salaService.cancelar(fila, columna);
            notificarCambio();
            mostrarInfo("Reserva cancelada en F" + (fila + 1) + "-C" + (columna + 1) + ".");
        } catch (PosicionInvalidaException ex) {
            mostrarError("Posición inválida: " + ex.getMessage());
        } catch (AsientoNoReservadoException ex) {
            mostrarError("Este asiento no está reservado, no se puede cancelar.");
        }
    }

    private void onContarClick() {
        long libres = salaQuery.contarLibres();
        long reservadas = salaQuery.contarReservadas();
        long ocupadas = salaQuery.contarOcupadas();
        int total = salaQuery.totalButacas();

        String mensaje = String.format(
            "Butacas Libres: %d%nButacas Reservadas: %d%nButacas Ocupadas: %d%nTotal: %d",
            libres, reservadas, ocupadas, total);
        JOptionPane.showMessageDialog(this, mensaje, "Conteo de Butacas", JOptionPane.INFORMATION_MESSAGE);
    }

    // -----------------------------------------------------------
    // Utilidades internas
    // -----------------------------------------------------------

    private int filaSeleccionada() {
        return (int) spinFila.getValue() - 1;
    }

    private int columnaSeleccionada() {
        return (int) spinColumna.getValue() - 1;
    }

    /**
     * Refresca el texto de estadísticas con los valores actuales del backend.
     */
    public void actualizarEstadisticas() {
        long libres = salaQuery.contarLibres();
        long reservadas = salaQuery.contarReservadas();
        long ocupadas = salaQuery.contarOcupadas();
        int total = salaQuery.totalButacas();

        lblEstadisticas.setText(String.format(
            "<html>"
            + "<span style='color:#22c55e'>Libres: <b>%d</b></span><br>"
            + "<span style='color:#fbbf24'>Reservadas: <b>%d</b></span><br>"
            + "<span style='color:#ef4444'>Ocupadas: <b>%d</b></span><br>"
            + "<span style='color:#94a3b8'>Total: <b>%d</b></span>"
            + "</html>",
            libres, reservadas, ocupadas, total));
    }

    /**
     * Define el callback a invocar tras una operación exitosa, para que
     * MainFrame pueda refrescar la grilla visual de PanelSala.
     * @param callback acción a ejecutar tras cada cambio de estado.
     */
    public void setAlCambiarEstado(Runnable callback) {
        this.alCambiarEstado = callback;
    }

    private void notificarCambio() {
        actualizarEstadisticas();
        if (alCambiarEstado != null) {
            alCambiarEstado.run();
        }
    }

    private void mostrarError(String mensaje) {
        JOptionPane.showMessageDialog(this, mensaje, "Error", JOptionPane.ERROR_MESSAGE);
    }

    private void mostrarInfo(String mensaje) {
        JOptionPane.showMessageDialog(this, mensaje, "Información", JOptionPane.INFORMATION_MESSAGE);
    }

    private java.awt.Frame ventanaPadre() {
        return (java.awt.Frame) javax.swing.SwingUtilities.getWindowAncestor(this);
    }

    // -----------------------------------------------------------
    // Helpers de estilo visual
    // -----------------------------------------------------------

    private JPanel crearTarjeta() {
        JPanel panel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(UIConstants.BG_TARJETA);
                g2.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 14, 14));
                g2.setColor(UIConstants.BORDE);
                g2.setStroke(new BasicStroke(1f));
                g2.draw(new RoundRectangle2D.Float(0, 0, getWidth() - 1, getHeight() - 1, 14, 14));
                g2.dispose();
            }
        };
        panel.setOpaque(false);
        panel.setBorder(new EmptyBorder(14, 16, 14, 16));
        panel.setAlignmentX(java.awt.Component.LEFT_ALIGNMENT);
        return panel;
    }

    private JButton crearBoton(String texto, Color color) {
        JButton btn = new JButton(texto);
        btn.setFont(UIConstants.FUENTE_BOTON);
        btn.setBackground(color);
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setOpaque(true);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setPreferredSize(new Dimension(0, 38));
        return btn;
    }

    private JLabel etiqueta(String texto) {
        JLabel lbl = new JLabel(texto);
        lbl.setFont(UIConstants.FUENTE_CUERPO);
        lbl.setForeground(UIConstants.TEXTO_SECUNDARIO);
        return lbl;
    }

    private JSpinner crearSpinner(int max) {
        JSpinner spinner = new JSpinner(new SpinnerNumberModel(1, 1, max, 1));
        spinner.setFont(UIConstants.FUENTE_NEGRITA);
        JComponent editor = spinner.getEditor();
        if (editor instanceof JSpinner.DefaultEditor) {
            JTextField tf = ((JSpinner.DefaultEditor) editor).getTextField();
            tf.setBackground(UIConstants.BG_FONDO);
            tf.setForeground(UIConstants.TEXTO_PRIMARIO);
            tf.setCaretColor(UIConstants.TEXTO_PRIMARIO);
            tf.setBorder(BorderFactory.createLineBorder(UIConstants.BORDE));
        }
        return spinner;
    }
}
