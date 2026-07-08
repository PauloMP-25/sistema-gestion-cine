package view;

import model.Rol;
import service.GestorSalas;
import service.interfaces.ISalaQuery;
import service.interfaces.ISalaService;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.geom.RoundRectangle2D;

/**
 * Ventana principal única del sistema (RF6).
 * Usa CardLayout para cambiar entre PanelLobby y PanelSala+PanelControl
 * sin abrir ventanas nuevas. Es el único JFrame de toda la ejecución.
 *
 * PARADIGMA: Orientado a Objetos — JFrame raíz con responsabilidad de navegación
 * SOLID: DIP — depende de GestorSalas (abstracción), no de implementaciones directas
 */
public class MainFrame extends JFrame {

    // Claves del CardLayout
    private static final String CARD_LOBBY = "LOBBY";
    private static final String CARD_SALA  = "SALA";

    private final GestorSalas gestorSalas;
    private final Rol rol;
    private boolean logout = false;

    private CardLayout cardLayout;
    private JPanel     panelContenido;
    private JPanel     panelBarraEstado;
    private JLabel     lblBreadcrumb;   // indicador de ubicación en el header
    private JLabel     lblBarraEstado;

    // Paneles (se crean una vez y se reutilizan)
    private PanelLobby  panelLobby;
    private JPanel      panelSalaWrapper;   // envuelve PanelSala + PanelControl
    private PanelSala   panelSalaActual;
    private PanelControl panelControlActual;
    private ISalaQuery   queryActual;

    /**
     * Constructor para uso con GestorSalas (RF6: ventana única, lobby primero).
     */
    public MainFrame(GestorSalas gestorSalas, Rol rol) {
        this.gestorSalas = gestorSalas;
        this.rol = rol;
        configurarVentana();
        inicializarComponentes();
        configurarEventos();
        mostrarLobby();
    }

    /**
     * Constructor de compatibilidad — mantiene funcionamiento del código existente
     * mientras el equipo migra al flujo de lobby.
     */
    public MainFrame(ISalaService salaService, ISalaQuery salaQuery, Rol rol) {
        this.gestorSalas = null;
        this.rol = rol;
        configurarVentana();
        inicializarComponentes();
        configurarEventos();
        // Modo legacy: abre directamente la sala
        mostrarSala(salaService, salaQuery, "Sala Principal");
    }

    // =========================================================================
    // CONFIGURACIÓN DE LA VENTANA
    // =========================================================================

    private void configurarVentana() {
        setTitle("🎬 Sistema de Gestión de Butacas de Cine");
        setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
        setSize(1060, 700);
        setMinimumSize(new Dimension(860, 600));
        setLocationRelativeTo(null);
        getContentPane().setBackground(UIConstants.BG_FONDO);
        setLayout(new BorderLayout());
    }

    private void inicializarComponentes() {
        add(crearHeader(), BorderLayout.NORTH);

        cardLayout      = new CardLayout();
        panelContenido  = new JPanel(cardLayout);
        panelContenido.setOpaque(false);

        // Lobby
        if (gestorSalas != null) {
            panelLobby = new PanelLobby(gestorSalas, rol);
            panelLobby.setAlAbrirSala(this::mostrarSala);
            panelContenido.add(panelLobby, CARD_LOBBY);
        }

        // Wrapper de sala (se rellena en mostrarSala)
        panelSalaWrapper = new JPanel(new BorderLayout(0, 0));
        panelSalaWrapper.setOpaque(false);
        panelContenido.add(panelSalaWrapper, CARD_SALA);

        add(panelContenido, BorderLayout.CENTER);
        add(crearBarraEstado(), BorderLayout.SOUTH);
    }

    // =========================================================================
    // HEADER
    // =========================================================================

    private JPanel crearHeader() {
        JPanel header = new JPanel(new BorderLayout()) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                GradientPaint gp = new GradientPaint(0, 0, UIConstants.BG_HEADER,
                        getWidth(), 0, new Color(20, 16, 45));
                g2.setPaint(gp);
                g2.fillRect(0, 0, getWidth(), getHeight());
                // Línea de acento inferior
                g2.setColor(UIConstants.ACENTO);
                g2.fillRect(0, getHeight() - 2, getWidth(), 2);
                g2.dispose();
            }
        };
        header.setBorder(new EmptyBorder(12, 24, 12, 24));
        header.setPreferredSize(new Dimension(0, 66));

        // Logo + título
        JPanel izq = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        izq.setOpaque(false);
        JLabel lblEmoji = new JLabel("🎬");
        lblEmoji.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 26));
        JLabel lblTitulo = new JLabel("Sistema de Gestión de Butacas de Cine");
        lblTitulo.setFont(UIConstants.FUENTE_TITULO);
        lblTitulo.setForeground(UIConstants.TEXTO_PRIMARIO);
        izq.add(lblEmoji);
        izq.add(lblTitulo);

        // Breadcrumb / navegación
        lblBreadcrumb = new JLabel("Inicio");
        lblBreadcrumb.setFont(UIConstants.FUENTE_PEQUENA);
        lblBreadcrumb.setForeground(UIConstants.TEXTO_TENUE);

        // Botón "Volver a mis salas" (solo visible en vista sala)
        JButton btnVolver = crearBotonVolver();
        
        // Botón Logout
        JButton btnLogout = crearBotonLogout();

        JPanel der = new JPanel(new FlowLayout(FlowLayout.RIGHT, 12, 0));
        der.setOpaque(false);
        der.add(lblBreadcrumb);
        der.add(btnVolver);
        der.add(btnLogout);

        header.add(izq,  BorderLayout.WEST);
        header.add(der, BorderLayout.EAST);
        return header;
    }

    private JButton crearBotonVolver() {
        JButton btn = new JButton("← Volver a mis salas") {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                Color bg = getModel().isRollover()
                        ? UIConstants.ACENTO_TENUE.brighter()
                        : UIConstants.ACENTO_TENUE;
                g2.setColor(bg);
                g2.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 8, 8));
                g2.setColor(UIConstants.ACENTO);
                g2.setStroke(new java.awt.BasicStroke(1f));
                g2.draw(new RoundRectangle2D.Float(0, 0, getWidth()-1, getHeight()-1, 8, 8));
                g2.dispose();
                super.paintComponent(g);
            }
        };
        btn.setFont(UIConstants.FUENTE_PEQUENA);
        btn.setForeground(UIConstants.TEXTO_ACENTO);
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setContentAreaFilled(false);
        btn.setOpaque(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setPreferredSize(new Dimension(160, 30));
        btn.setVisible(false);  // solo aparece en vista sala
        btn.setName("btnVolver");
        btn.addActionListener(e -> mostrarLobby());
        return btn;
    }

    private JButton crearBotonLogout() {
        JButton btn = new JButton("← Salir") {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                Color bg = getModel().isRollover()
                        ? new Color(220, 38, 38, 50)
                        : new Color(220, 38, 38, 20);
                g2.setColor(bg);
                g2.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 8, 8));
                g2.setColor(new Color(239, 68, 68));
                g2.setStroke(new java.awt.BasicStroke(1f));
                g2.draw(new RoundRectangle2D.Float(0, 0, getWidth()-1, getHeight()-1, 8, 8));
                g2.dispose();
                super.paintComponent(g);
            }
        };
        btn.setFont(UIConstants.FUENTE_PEQUENA);
        btn.setForeground(new Color(239, 68, 68));
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setContentAreaFilled(false);
        btn.setOpaque(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setPreferredSize(new Dimension(80, 30));
        btn.addActionListener(e -> cerrarSesion());
        return btn;
    }

    private void cerrarSesion() {
        int op = JOptionPane.showConfirmDialog(this,
            "¿Desea cerrar sesión y volver al inicio?",
            "Cerrar sesión", JOptionPane.YES_NO_OPTION);
        if (op == JOptionPane.YES_OPTION) { logout = true; dispose(); }
    }

    public boolean isLogout() { return logout; }

    // =========================================================================
    // BARRA DE ESTADO INFERIOR
    // =========================================================================

    private JPanel crearBarraEstado() {
        panelBarraEstado = new JPanel(new FlowLayout(FlowLayout.LEFT, 16, 4));
        panelBarraEstado.setBackground(UIConstants.BG_HEADER);
        panelBarraEstado.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, UIConstants.BORDE));
        panelBarraEstado.setPreferredSize(new Dimension(0, 36));
        lblBarraEstado = new JLabel("🎬  Bienvenido al sistema de gestión de butacas");
        lblBarraEstado.setFont(UIConstants.FUENTE_PEQUENA);
        lblBarraEstado.setForeground(UIConstants.TEXTO_TENUE);
        panelBarraEstado.add(lblBarraEstado);
        return panelBarraEstado;
    }

    private void actualizarBarraEstado(String texto) {
        lblBarraEstado.setText(texto);
    }

    // =========================================================================
    // NAVEGACIÓN (RF6: todo dentro del mismo JFrame con CardLayout)
    // =========================================================================

    /**
     * Muestra el panel lobby y refresca sus datos (RF7).
     */
    public void mostrarLobby() {
        if (panelLobby != null) {
            panelLobby.refrescar();
        }
        cardLayout.show(panelContenido, CARD_LOBBY);
        lblBreadcrumb.setText("Inicio  /  Mis Salas");
        setBtnVolverVisible(false);
        
        panelBarraEstado.removeAll();
        panelBarraEstado.add(lblBarraEstado);
        actualizarBarraEstado("🎬  Selecciona o crea una sala para comenzar");
        panelBarraEstado.revalidate();
        panelBarraEstado.repaint();
        
        setTitle("🎬 Sistema de Gestión de Butacas de Cine");
    }

    /**
     * Muestra la vista de una sala específica dentro del mismo JFrame.
     * Crea los paneles de sala y control con las dependencias inyectadas.
     */
    private void mostrarSala(ISalaService salaService, ISalaQuery salaQuery, String nombreSala) {
        this.queryActual = salaQuery;

        // Reconstruir el wrapper de sala con los nuevos servicios
        panelSalaWrapper.removeAll();

        JPanel contenedorSala = new JPanel(new BorderLayout(0, 12));
        contenedorSala.setOpaque(false);
        contenedorSala.setBorder(new EmptyBorder(16, 20, 16, 20));

        // Panel principal: grilla + leyenda
        JPanel izquierda = new JPanel(new BorderLayout(0, 10));
        izquierda.setOpaque(false);

        // Sub-encabezado de la sala (pantalla + nombre) solo en el lado izquierdo
        izquierda.add(crearHeaderSala(nombreSala, salaQuery), BorderLayout.NORTH);
        panelSalaActual = new PanelSala(salaService, salaQuery, rol);
        JScrollPane scrollSala = new JScrollPane(panelSalaActual);
        scrollSala.setOpaque(false);
        scrollSala.getViewport().setOpaque(false);
        scrollSala.setBorder(BorderFactory.createEmptyBorder());
        izquierda.add(scrollSala, BorderLayout.CENTER);
        // La leyenda ya no se muestra debajo de los asientos

        // Panel de control lateral
        panelControlActual = new PanelControl(salaService, salaQuery, rol);
        panelControlActual.setPreferredSize(new Dimension(270, 0));

        // Sincronización entre paneles (PARADIGMA FUNCIONAL: lambdas como callbacks)
        panelSalaActual.setAlCambiarEstado(() -> {
            panelControlActual.actualizarEstadisticas();
            actualizarBarraEstadoSala(salaQuery);
        });
        panelSalaActual.setAlSeleccionarButaca(numeroAsiento -> {
            panelControlActual.seleccionarAsiento(numeroAsiento);
        });
        panelControlActual.setAlCambiarEstado(() -> {
            panelSalaActual.refrescarTodo();
            actualizarBarraEstadoSala(salaQuery);
        });

        contenedorSala.add(izquierda, BorderLayout.CENTER);
        contenedorSala.add(panelControlActual, BorderLayout.EAST);

        panelSalaWrapper.add(contenedorSala, BorderLayout.CENTER);
        panelSalaWrapper.revalidate();
        panelSalaWrapper.repaint();

        cardLayout.show(panelContenido, CARD_SALA);
        lblBreadcrumb.setText("Inicio  /  Mis Salas  /  " + nombreSala);
        setBtnVolverVisible(gestorSalas != null);
        actualizarBarraEstadoSala(salaQuery);
        setTitle("🎬  " + nombreSala + " — Gestión de Butacas");
    }

    /**
     * Sub-encabezado visual de la sala: pantalla + título.
     */
    private JPanel crearHeaderSala(String nombreSala, ISalaQuery salaQuery) {
        JPanel enc = new JPanel(new BorderLayout(0, 6));
        enc.setOpaque(false);

        // Barra de pantalla
        JPanel pantallaWrap = new JPanel(new BorderLayout());
        pantallaWrap.setOpaque(false);
        pantallaWrap.setBorder(new EmptyBorder(0, 40, 0, 40));
        JPanel pantalla = new JPanel(new BorderLayout()) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                GradientPaint gp = new GradientPaint(0, 0, new Color(80, 80, 120),
                        getWidth(), 0, new Color(50, 50, 90));
                g2.setPaint(gp);
                g2.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 6, 6));
                g2.dispose();
            }
        };
        pantalla.setOpaque(false);
        pantalla.setPreferredSize(new Dimension(0, 24));
        JLabel lblPantalla = new JLabel("▬▬▬  P A N T A L L A  ▬▬▬", SwingConstants.CENTER);
        lblPantalla.setFont(new Font("Consolas", Font.BOLD, 11));
        lblPantalla.setForeground(new Color(200, 205, 230));
        pantalla.add(lblPantalla, BorderLayout.CENTER);
        pantallaWrap.add(pantalla);

        enc.add(pantallaWrap, BorderLayout.NORTH);
        return enc;
    }

    private void actualizarBarraEstadoSala(ISalaQuery q) {
        panelBarraEstado.removeAll();
        panelBarraEstado.add(new PanelLeyenda());
        panelBarraEstado.revalidate();
        panelBarraEstado.repaint();
    }

    private void setBtnVolverVisible(boolean visible) {
        for (Component c : ((JPanel)((BorderLayout)getContentPane().getLayout())
                .getLayoutComponent(BorderLayout.NORTH)).getComponents()) {
            buscarYToggleBtnVolver(c, visible);
        }
    }

    private void buscarYToggleBtnVolver(Component c, boolean visible) {
        if (c instanceof JButton && "btnVolver".equals(c.getName())) {
            c.setVisible(visible);
        } else if (c instanceof Container) {
            for (Component hijo : ((Container) c).getComponents()) {
                buscarYToggleBtnVolver(hijo, visible);
            }
        }
    }

    // =========================================================================
    // EVENTOS
    // =========================================================================

    private void configurarEventos() {
        addWindowListener(new WindowAdapter() {
            @Override public void windowClosing(WindowEvent e) { onCerrarVentana(); }
        });
    }

    private void onCerrarVentana() {
        int op = JOptionPane.showConfirmDialog(this,
                "¿Está seguro que desea salir del sistema?",
                "Confirmar salida", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
        if (op == JOptionPane.YES_OPTION) { dispose(); System.exit(0); }
    }
}
