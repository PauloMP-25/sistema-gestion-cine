package view;

import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.RoundRectangle2D;
import java.util.List;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import model.ResumenSalas;
import model.Rol;
import model.SalaCine;
import model.SalaFactory;
import service.GestorSalas;
import service.SalaQuery;
import service.SalaService;
import service.interfaces.ISalaQuery;
import service.interfaces.ISalaService;

/**
 * Panel lobby — vista principal del sistema. Lista todas las salas,
 * muestra el dashboard global y permite crear/abrir/eliminar salas.
 * Vive dentro de MainFrame (RF6: única ventana).
 *
 * PARADIGMA: Orientado a Objetos — componente reutilizable con callback
 * SOLID: DIP — depende de GestorSalas (abstracción), no de SalaCine directamente
 */
public class PanelLobby extends JPanel {

    private final GestorSalas gestorSalas;
    private final Rol rol;
    private AbrirSalaCallback alAbrirSala;
    private Runnable alCrearSala;

    private PanelDashboard panelDashboard;
    private JPanel panelListaSalas;
    private JLabel lblSinSalas;

    /**
     * Callback que el lobby invoca cuando el usuario elige abrir una sala.
     * MainFrame escucha esto para cambiar el panel visible.
     */
    public interface AbrirSalaCallback {
        void abrir(ISalaService servicio, ISalaQuery consulta, String nombreSala);
    }

    public PanelLobby(GestorSalas gestorSalas, Rol rol) {
        this.gestorSalas = gestorSalas;
        this.rol = rol;
        setOpaque(false);
        setLayout(new BorderLayout(0, 16));
        setBorder(new EmptyBorder(20, 24, 20, 24));
        inicializarComponentes();
    }

    public void setAlAbrirSala(AbrirSalaCallback cb) {
        this.alAbrirSala = cb;
    }

    public void setAlCrearSala(Runnable cb) {
        this.alCrearSala = cb;
    }

    private void inicializarComponentes() {
        add(crearEncabezadoLobby(), BorderLayout.NORTH);

        JPanel centro = new JPanel(new BorderLayout(0, 16));
        centro.setOpaque(false);

        panelDashboard = new PanelDashboard();
        centro.add(panelDashboard, BorderLayout.NORTH);
        centro.add(crearSeccionSalas(), BorderLayout.CENTER);

        JScrollPane scroll = new JScrollPane(centro);
        scroll.setOpaque(false);
        scroll.getViewport().setOpaque(false);
        scroll.setBorder(BorderFactory.createEmptyBorder());
        scroll.getVerticalScrollBar().setUnitIncrement(12);
        add(scroll, BorderLayout.CENTER);
    }

    private JPanel crearEncabezadoLobby() {
        JPanel enc = new JPanel(new BorderLayout());
        enc.setOpaque(false);
        enc.setBorder(new EmptyBorder(0, 0, 4, 0));

        JPanel izq = new JPanel(new GridLayout(2, 1, 0, 3));
        izq.setOpaque(false);
        JLabel lblTitulo = new JLabel("🎬  Mis Salas de Cine");
        lblTitulo.setFont(UIConstants.FUENTE_TITULO_LG);
        lblTitulo.setForeground(UIConstants.TEXTO_PRIMARIO);
        JLabel lblSub = ComponenteUI.cuerpo("Gestiona tus salas, reservas y ocupación en un solo lugar.");
        izq.add(lblTitulo);
        izq.add(lblSub);

        enc.add(izq, BorderLayout.CENTER);

        if (rol == Rol.ADMIN) {
            JButton btnNuevaSala = ComponenteUI.botonPrimario("✚  Nueva Sala");
            btnNuevaSala.setPreferredSize(new Dimension(150, 40));
            btnNuevaSala.addActionListener(e -> onNuevaSala());
            enc.add(btnNuevaSala, BorderLayout.EAST);
        }
        
        return enc;
    }

    private JPanel crearSeccionSalas() {
        JPanel seccion = new JPanel(new BorderLayout(0, 10));
        seccion.setOpaque(false);

        JLabel lblTitulo = ComponenteUI.subtitulo("Salas disponibles");
        seccion.add(lblTitulo, BorderLayout.NORTH);

        panelListaSalas = new JPanel();
        panelListaSalas.setOpaque(false);
        panelListaSalas.setLayout(new BoxLayout(panelListaSalas, BoxLayout.Y_AXIS));

        lblSinSalas = new JLabel("No hay salas creadas. Crea la primera sala con el botón \"✚ Nueva Sala\".");
        lblSinSalas.setFont(UIConstants.FUENTE_CUERPO);
        lblSinSalas.setForeground(UIConstants.TEXTO_TENUE);
        lblSinSalas.setAlignmentX(Component.LEFT_ALIGNMENT);
        lblSinSalas.setBorder(new EmptyBorder(20, 0, 0, 0));

        seccion.add(panelListaSalas, BorderLayout.CENTER);
        return seccion;
    }

    /**
     * Refresca la lista de salas y el dashboard. Llamar siempre al volver al lobby.
     * RF7: refleja cambios hechos dentro de una sala.
     */
    public void refrescar() {
        // Dashboard global
        ResumenSalas resumen = gestorSalas.obtenerResumenGlobal();
        panelDashboard.actualizar(resumen);

        // Lista de salas
        panelListaSalas.removeAll();
        List<SalaCine> salas = gestorSalas.listarSalas();

        if (salas.isEmpty()) {
            panelListaSalas.add(lblSinSalas);
        } else {
            for (SalaCine sala : salas) {
                panelListaSalas.add(crearFilaSala(sala));
                panelListaSalas.add(Box.createVerticalStrut(8));
            }
        }

        panelListaSalas.revalidate();
        panelListaSalas.repaint();
    }

    /**
     * Crea la tarjeta visual de una sala en la lista del lobby.
     */
    private JPanel crearFilaSala(SalaCine sala) {
        // Calcular estadísticas de esta sala
        ISalaQuery q = new SalaQuery(sala);
        long libres     = q.contarLibres();
        long reservadas = q.contarReservadas();
        long ocupadas   = q.contarOcupadas();
        int  total      = q.totalButacas();
        double pctOcup  = total > 0 ? (double)(total - libres) / total * 100 : 0;

        // Tarjeta base con hover
        JPanel card = new JPanel(new BorderLayout(14, 0)) {
            private boolean hover = false;
            {
                addMouseListener(new MouseAdapter() {
                    @Override public void mouseEntered(MouseEvent e) { hover = true;  repaint(); }
                    @Override public void mouseExited (MouseEvent e) { hover = false; repaint(); }
                });
            }
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                Color bg = hover ? UIConstants.BG_TARJETA_HOVER : UIConstants.BG_TARJETA;
                g2.setColor(bg);
                g2.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 14, 14));
                g2.setColor(UIConstants.BORDE);
                g2.setStroke(new java.awt.BasicStroke(1f));
                g2.draw(new RoundRectangle2D.Float(0, 0, getWidth() - 1, getHeight() - 1, 14, 14));
                g2.dispose();
            }
        };
        card.setOpaque(false);
        card.setBorder(new EmptyBorder(14, 18, 14, 18));
        card.setAlignmentX(Component.LEFT_ALIGNMENT);
        card.setMaximumSize(new Dimension(Integer.MAX_VALUE, 82));

        // Izquierda: ícono + info
        JPanel izq = new JPanel(new BorderLayout(10, 0));
        izq.setOpaque(false);

        // Ícono de sala
        JLabel iconoSala = new JLabel("🎭");
        iconoSala.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 28));
        iconoSala.setVerticalAlignment(SwingConstants.CENTER);

        // Info de la sala
        JPanel info = new JPanel(new GridLayout(2, 1, 0, 2));
        info.setOpaque(false);
        JLabel lblNombre = new JLabel(sala.getNombre());
        lblNombre.setFont(UIConstants.FUENTE_NEGRITA);
        lblNombre.setForeground(UIConstants.TEXTO_PRIMARIO);

        // Badges de estado con los mismos colores que los botones
        JPanel badges = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 0));
        badges.setOpaque(false);
        badges.add(ComponenteUI.pequena(sala.getFilas() + "×" + sala.getCols() + " · " + total + " butacas  "));
        badges.add(ComponenteUI.badge(libres + " libres",     UIConstants.COLOR_LIBRE));
        if (reservadas > 0) badges.add(ComponenteUI.badge(reservadas + " reservadas", UIConstants.COLOR_RESERVADO));
        if (ocupadas > 0)   badges.add(ComponenteUI.badge(ocupadas   + " ocupadas",   UIConstants.COLOR_OCUPADO));

        info.add(lblNombre);
        info.add(badges);
        izq.add(iconoSala, BorderLayout.WEST);
        izq.add(info, BorderLayout.CENTER);

        // Derecha: mini barra de ocupación + botones
        JPanel der = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        der.setOpaque(false);

        // Mini barra de ocupación
        JPanel barraWrap = new JPanel(new BorderLayout(0, 2));
        barraWrap.setOpaque(false);
        JProgressBar barra = new JProgressBar(0, 100);
        barra.setValue((int) pctOcup);
        barra.setStringPainted(false);
        barra.setBorderPainted(false);
        barra.setForeground(pctOcup > 80 ? UIConstants.COLOR_OCUPADO
                : pctOcup > 40 ? UIConstants.COLOR_RESERVADO : UIConstants.COLOR_LIBRE);
        barra.setBackground(UIConstants.BG_FONDO);
        barra.setPreferredSize(new Dimension(80, 6));
        JLabel lblPct = ComponenteUI.pequena(String.format("%.0f%% ocupado", pctOcup));
        barraWrap.add(lblPct, BorderLayout.NORTH);
        barraWrap.add(barra, BorderLayout.CENTER);

        // Botón Abrir (azul primario)
        JButton btnAbrir = ComponenteUI.botonPrimario("Abrir →");
        btnAbrir.setPreferredSize(new Dimension(90, 34));
        btnAbrir.addActionListener(e -> onAbrirSala(sala));

        der.add(barraWrap);
        der.add(btnAbrir);

        if (rol == Rol.ADMIN) {
            // Botón Eliminar (rojo peligro — mismo que COLOR_OCUPADO)
            JButton btnEliminar = ComponenteUI.botonPeligro("🗑");
            btnEliminar.setPreferredSize(new Dimension(36, 34));
            btnEliminar.setToolTipText("Eliminar sala");
            btnEliminar.addActionListener(e -> onEliminarSala(sala));
            der.add(btnEliminar);
        }

        card.add(izq,  BorderLayout.CENTER);
        card.add(der, BorderLayout.EAST);
        return card;
    }

    // =========================================================================
    // HANDLERS — PARADIGMA: Orientado a Eventos
    // =========================================================================

    private void onNuevaSala() {
        if (alCrearSala != null) {
            alCrearSala.run();
        }
    }

    private void onAbrirSala(SalaCine sala) {
        if (alAbrirSala != null) {
            ISalaService srv = new SalaService(sala);
            ISalaQuery   qry = new SalaQuery(sala);
            alAbrirSala.abrir(srv, qry, sala.getNombre());
        }
    }

    // RF5: advertir si hay butacas no libres antes de eliminar
    private void onEliminarSala(SalaCine sala) {
        Frame padre = (Frame) SwingUtilities.getWindowAncestor(this);
        String msg;
        if (gestorSalas.tieneButacasNoLibres(sala.getId())) {
            msg = "<html><b>" + sala.getNombre() + "</b> tiene butacas reservadas u ocupadas.<br>"
                + "¿Desea eliminarla de todas formas?</html>";
        } else {
            msg = "<html>¿Eliminar la sala <b>" + sala.getNombre() + "</b>?<br>"
                + "Esta acción no se puede deshacer.</html>";
        }
        int res = JOptionPane.showConfirmDialog(padre, msg, "Eliminar sala",
                JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
        if (res == JOptionPane.YES_OPTION) {
            gestorSalas.eliminarPorId(sala.getId());
            refrescar();
        }
    }
}
