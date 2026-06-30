package view;

import service.interfaces.ISalaQuery;
import service.interfaces.ISalaService;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.WindowConstants;
import javax.swing.border.EmptyBorder;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

/**
 * Ventana principal del sistema (JFrame raíz). Contiene el panel de la
 * sala, el panel de control y la leyenda de colores, organizados con
 * BorderLayout. Recibe las dependencias del backend por constructor:
 * nunca instancia SalaService ni SalaQuery directamente.
 */
public class MainFrame extends JFrame {

    private final ISalaService salaService;
    private final ISalaQuery salaQuery;
    private PanelSala panelSala;
    private PanelControl panelControl;
    private JLabel lblBarraEstado;

    /**
     * Crea la ventana principal inyectando las dependencias del backend.
     * @param salaService servicio de operaciones de escritura sobre la sala.
     * @param salaQuery   servicio de consultas de solo lectura sobre la sala.
     */
    // CORRECTO — depende de la abstracción, no de la implementación
    public MainFrame(ISalaService salaService, ISalaQuery salaQuery) {
        this.salaService = salaService;
        this.salaQuery = salaQuery;
        configurarVentana();
        inicializarComponentes();
        configurarEventos();
    }

    private void configurarVentana() {
        setTitle("🎬 Sistema de Gestión de Butacas de Cine");
        setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
        setSize(900, 650);
        setMinimumSize(new Dimension(820, 600));
        setLocationRelativeTo(null);
        getContentPane().setBackground(UIConstants.BG_FONDO);
        setLayout(new BorderLayout());
    }

    // Solo crea y posiciona componentes. Los listeners van en configurarEventos().
    private void inicializarComponentes() {
        add(crearHeader(), BorderLayout.NORTH);
        add(crearPanelCentral(), BorderLayout.CENTER);
        add(crearBarraEstado(), BorderLayout.SOUTH);
        actualizarBarraEstado();
    }

    private JPanel crearHeader() {
        JPanel header = new JPanel(new BorderLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                GradientPaint gp = new GradientPaint(0, 0, UIConstants.BG_HEADER,
                        getWidth(), 0, new Color(25, 20, 50));
                g2.setPaint(gp);
                g2.fillRect(0, 0, getWidth(), getHeight());
                g2.setColor(UIConstants.ACENTO);
                g2.fillRect(0, getHeight() - 2, getWidth(), 2);
                g2.dispose();
            }
        };
        header.setBorder(new EmptyBorder(14, 24, 14, 24));
        header.setPreferredSize(new Dimension(0, 70));

        JLabel titulo = new JLabel("🎬  Sistema de Gestión de Butacas de Cine");
        titulo.setFont(UIConstants.FUENTE_TITULO);
        titulo.setForeground(UIConstants.TEXTO_PRIMARIO);
        header.add(titulo, BorderLayout.WEST);

        var matriz = salaQuery.obtenerMatriz();
        int filas = matriz.length;
        int columnas = matriz.length > 0 ? matriz[0].length : 0;
        JLabel subtitulo = new JLabel("Sala: " + filas + " filas × " + columnas + " columnas  ");
        subtitulo.setFont(UIConstants.FUENTE_PEQUENA);
        subtitulo.setForeground(UIConstants.TEXTO_SECUNDARIO);
        header.add(subtitulo, BorderLayout.EAST);

        return header;
    }

    private JPanel crearPanelCentral() {
        JPanel central = new JPanel(new BorderLayout(15, 0));
        central.setOpaque(false);
        central.setBorder(new EmptyBorder(15, 15, 15, 15));

        JPanel izquierda = new JPanel(new BorderLayout(0, 12));
        izquierda.setOpaque(false);

        panelSala = new PanelSala(salaService, salaQuery);
        izquierda.add(panelSala, BorderLayout.CENTER);
        izquierda.add(new PanelLeyenda(), BorderLayout.SOUTH);

        panelControl = new PanelControl(salaService, salaQuery);
        panelControl.setPreferredSize(new Dimension(260, 0));

        // Sincronización entre paneles: cuando uno cambia, el otro se refresca.
        panelSala.setAlCambiarEstado(() -> {
            panelControl.actualizarEstadisticas();
            actualizarBarraEstado();
        });
        panelControl.setAlCambiarEstado(() -> {
            panelSala.refrescarTodo();
            actualizarBarraEstado();
        });

        central.add(izquierda, BorderLayout.CENTER);
        central.add(panelControl, BorderLayout.EAST);
        return central;
    }

    private JPanel crearBarraEstado() {
        JPanel barra = new JPanel(new FlowLayout(FlowLayout.LEFT, 16, 6));
        barra.setOpaque(false);
        barra.setBackground(UIConstants.BG_HEADER);
        barra.setPreferredSize(new Dimension(0, 30));
        lblBarraEstado = new JLabel();
        lblBarraEstado.setFont(UIConstants.FUENTE_PEQUENA);
        lblBarraEstado.setForeground(UIConstants.TEXTO_SECUNDARIO);
        barra.add(lblBarraEstado);
        return barra;
    }

    private void actualizarBarraEstado() {
        lblBarraEstado.setText(String.format(
            "🟢 Libres: %d   🟡 Reservadas: %d   🔴 Ocupadas: %d",
            salaQuery.contarLibres(), salaQuery.contarReservadas(), salaQuery.contarOcupadas()));
    }

    // Separación de listeners en su propio método
    private void configurarEventos() {
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                onCerrarVentana();
            }
        });
    }

    // PARADIGMA: Orientado a Eventos — WindowListener captura el cierre con la X
    private void onCerrarVentana() {
        int opcion = JOptionPane.showConfirmDialog(
            this,
            "¿Está seguro que desea salir del sistema?",
            "Confirmar salida",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.QUESTION_MESSAGE);
        if (opcion == JOptionPane.YES_OPTION) {
            dispose();
            System.exit(0);
        }
    }
}
