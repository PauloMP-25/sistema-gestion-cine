package view.dialog;

import view.*;

import java.awt.*;
import javax.swing.*;
import javax.swing.border.EmptyBorder;

/**
 * Diálogo modal con tema oscuro — reemplaza JOptionPane en todo el sistema.
 * Mantiene coherencia visual con DialogReserva.
 *
 * PARADIGMA: Orientado a Objetos — JDialog reutilizable con tipo configurable
 * SOLID: SRP — única responsabilidad: mostrar mensajes estilizados al usuario
 */
public class DialogMensaje extends JDialog {

    // -----------------------------------------------------------------------
    // TIPOS — coherentes con los colores de UIConstants
    // -----------------------------------------------------------------------
    public enum Tipo {
        EXITO("✔",  "Éxito",                  UIConstants.BTN_EXITO,   "🟢"),
        ERROR("✖",  "Error",                  UIConstants.BTN_PELIGRO, "🔴"),
        INFO ("📊", "Estadísticas de la sala", UIConstants.ACENTO,      "📊");

        final String icono;
        final String titulo;
        final Color  color;
        final String emoji;

        Tipo(String icono, String titulo, Color color, String emoji) {
            this.icono  = icono;
            this.titulo = titulo;
            this.color  = color;
            this.emoji  = emoji;
        }
    }

    // -----------------------------------------------------------------------
    // CONSTRUCTOR
    // -----------------------------------------------------------------------
    public DialogMensaje(Frame padre, Tipo tipo, String mensaje) {
        super(padre, tipo.titulo, true);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setResizable(false);
        inicializarComponentes(tipo, mensaje);
        setMinimumSize(new Dimension(460, 260));
        pack();
        setLocationRelativeTo(padre);
    }

    // -----------------------------------------------------------------------
    // UI — mismo patrón visual que DialogReserva, sin borde blanco
    // -----------------------------------------------------------------------
    private void inicializarComponentes(Tipo tipo, String mensaje) {
        // Eliminar completamente el borde blanco que Java añade por defecto
        getRootPane().setBorder(BorderFactory.createEmptyBorder());
        getContentPane().setBackground(UIConstants.BG_PANEL);

        JPanel root = new JPanel(new BorderLayout(0, 0));
        root.setBackground(UIConstants.BG_PANEL);
        root.setBorder(BorderFactory.createEmptyBorder());

        // --- ENCABEZADO (idéntico a DialogReserva) ---
        JPanel header = new JPanel(new FlowLayout(FlowLayout.LEFT, 14, 12)) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setColor(UIConstants.BG_HEADER);
                g2.fillRect(0, 0, getWidth(), getHeight());
                g2.setColor(tipo.color); // línea inferior con el color del tipo
                g2.fillRect(0, getHeight() - 2, getWidth(), 2);
                g2.dispose();
            }
        };
        header.setOpaque(true);
        header.setBackground(UIConstants.BG_HEADER);

        JLabel lblEmoji = new JLabel(tipo.emoji);
        lblEmoji.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 22));

        JLabel lblTitulo = new JLabel(tipo.titulo);
        lblTitulo.setFont(UIConstants.FUENTE_SUBTITULO);
        lblTitulo.setForeground(UIConstants.TEXTO_PRIMARIO);

        header.add(lblEmoji);
        header.add(lblTitulo);
        root.add(header, BorderLayout.NORTH);

        // --- CUERPO ---
        JPanel cuerpo = new JPanel(new BorderLayout(0, 12));
        cuerpo.setOpaque(true);
        cuerpo.setBackground(UIConstants.BG_PANEL);
        cuerpo.setBorder(new EmptyBorder(22, 30, 16, 30));

        JLabel lblMsg = new JLabel(
            "<html><center>" + mensaje + "</center></html>",
            SwingConstants.CENTER
        );
        lblMsg.setFont(UIConstants.FUENTE_CUERPO);
        lblMsg.setForeground(tipo.color);
        cuerpo.add(lblMsg, BorderLayout.CENTER);
        root.add(cuerpo, BorderLayout.CENTER);

        // --- PIE CON BOTÓN OK ---
        JPanel pie = new JPanel(new FlowLayout(FlowLayout.CENTER, 12, 12));
        pie.setOpaque(true);
        pie.setBackground(UIConstants.BG_HEADER);
        pie.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, UIConstants.BORDE));

        JButton btnOk;
        switch (tipo) {
            case EXITO:
                btnOk = ComponenteUI.botonExito("✔  Aceptar");
                break;
            case ERROR:
                btnOk = ComponenteUI.botonPeligro("✖  Cerrar");
                break;
            default:
                btnOk = ComponenteUI.botonPrimario("✔  Aceptar");
                break;
        }
        btnOk.setPreferredSize(new Dimension(140, 38));
        btnOk.addActionListener(e -> dispose());

        pie.add(btnOk);
        root.add(pie, BorderLayout.SOUTH);

        setContentPane(root);
    }

    // -----------------------------------------------------------------------
    // MÉTODO ESTÁTICO DE CONVENIENCIA
    // Uso: DialogMensaje.mostrar(padre, Tipo.EXITO, "Reserva correcta.");
    // -----------------------------------------------------------------------
    public static void mostrar(Frame padre, Tipo tipo, String mensaje) {
        new DialogMensaje(padre, tipo, mensaje).setVisible(true);
    }
}
