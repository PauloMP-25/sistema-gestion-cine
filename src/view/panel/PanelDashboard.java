package view.panel;

import view.*;
import model.sala.ResumenSalas;

import javax.swing.*;
import java.awt.*;

/**
 * Panel de estadísticas globales del lobby (dashboard).
 * Muestra el total de salas, butacas libres, reservadas y ocupadas
 * usando tarjetas KPI con barras de progreso y colores coherentes.
 *
 * PARADIGMA: Orientado a Objetos — componente visual autocontenido y actualizable
 * SOLID: SRP — responsabilidad única: visualizar el ResumenSalas
 */
public class PanelDashboard extends JPanel {

    private JLabel lblTotalSalas;
    private JLabel lblTotalButacas;
    private JLabel lblLibres;
    private JLabel lblReservadas;
    private JLabel lblOcupadas;
    private JProgressBar barraLibres;
    private JProgressBar barraReservadas;
    private JProgressBar barraOcupadas;

    public PanelDashboard() {
        setOpaque(false);
        setLayout(new BorderLayout(0, 12));
        inicializarComponentes();
    }

    private void inicializarComponentes() {
        // Encabezado del panel
        JPanel encabezado = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        encabezado.setOpaque(false);
        JLabel icono = new JLabel("📊");
        icono.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 18));
        JLabel titulo = ComponenteUI.subtitulo("  Resumen Global");
        encabezado.add(icono);
        encabezado.add(titulo);
        add(encabezado, BorderLayout.NORTH);

        // Grid de KPI cards
        JPanel grid = new JPanel(new GridLayout(1, 4, 10, 0));
        grid.setOpaque(false);

        // KPI: Salas activas
        JPanel cardSalas = crearKpiCard("Salas Activas", "0", UIConstants.ACENTO, "🎭");
        lblTotalSalas = (JLabel) cardSalas.getClientProperty("lblNumero");

        // KPI: Butacas libres (verde)
        JPanel cardLibres = crearKpiCard("Libres", "0", UIConstants.COLOR_LIBRE, "🟢");
        lblLibres = (JLabel) cardLibres.getClientProperty("lblNumero");
        barraLibres = (JProgressBar) cardLibres.getClientProperty("barra");

        // KPI: Reservadas (ámbar)
        JPanel cardReservadas = crearKpiCard("Reservadas", "0", UIConstants.COLOR_RESERVADO, "🟡");
        lblReservadas = (JLabel) cardReservadas.getClientProperty("lblNumero");
        barraReservadas = (JProgressBar) cardReservadas.getClientProperty("barra");

        // KPI: Ocupadas (rojo)
        JPanel cardOcupadas = crearKpiCard("Ocupadas", "0", UIConstants.COLOR_OCUPADO, "🔴");
        lblOcupadas = (JLabel) cardOcupadas.getClientProperty("lblNumero");
        barraOcupadas = (JProgressBar) cardOcupadas.getClientProperty("barra");

        grid.add(cardSalas);
        grid.add(cardLibres);
        grid.add(cardReservadas);
        grid.add(cardOcupadas);
        add(grid, BorderLayout.CENTER);

        // Barra de totales
        JPanel barraTotales = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        barraTotales.setOpaque(false);
        lblTotalButacas = ComponenteUI.pequena("Total de butacas: 0");
        barraTotales.add(lblTotalButacas);
        add(barraTotales, BorderLayout.SOUTH);
    }

    /**
     * Crea una tarjeta KPI con número grande, etiqueta, barra de progreso e ícono.
     * Almacena referencias en clientProperty para poder actualizar los valores.
     */
    private JPanel crearKpiCard(String etiqueta, String numero, Color color, String emoji) {
        JPanel card = ComponenteUI.tarjeta();
        card.setLayout(new BorderLayout(0, 6));

        // Encabezado: icono + etiqueta
        JPanel top = new JPanel(new FlowLayout(FlowLayout.LEFT, 4, 0));
        top.setOpaque(false);
        JLabel lblEmoji = new JLabel(emoji);
        lblEmoji.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 13));
        JLabel lblEti = new JLabel(etiqueta);
        lblEti.setFont(UIConstants.FUENTE_PEQUENA);
        lblEti.setForeground(UIConstants.TEXTO_SECUNDARIO);
        top.add(lblEmoji);
        top.add(lblEti);

        // Número grande
        JLabel lblNum = new JLabel(numero, SwingConstants.LEFT);
        lblNum.setFont(UIConstants.FUENTE_STAT_NUM);
        lblNum.setForeground(color);

        // Barra de progreso (solo si no es "Salas Activas")
        JProgressBar barra = new JProgressBar(0, 100);
        barra.setValue(0);
        barra.setBackground(UIConstants.BG_FONDO);
        barra.setForeground(color);
        barra.setBorderPainted(false);
        barra.setPreferredSize(new Dimension(0, 5));
        barra.setOpaque(true);

        card.add(top, BorderLayout.NORTH);
        card.add(lblNum, BorderLayout.CENTER);
        card.add(barra, BorderLayout.SOUTH);

        // Guardar referencias para actualización posterior
        card.putClientProperty("lblNumero", lblNum);
        card.putClientProperty("barra", barra);
        return card;
    }

    /**
     * Actualiza todos los valores del dashboard con los datos del resumen.
     * @param resumen Resumen global calculado por GestorSalas.
     */
    public void actualizar(ResumenSalas resumen) {
        long total = resumen.getTotalButacas();

        lblTotalSalas.setText(String.valueOf(resumen.getTotalSalas()));
        lblLibres.setText(String.valueOf(resumen.getTotalLibres()));
        lblReservadas.setText(String.valueOf(resumen.getTotalReservadas()));
        lblOcupadas.setText(String.valueOf(resumen.getTotalOcupadas()));
        lblTotalButacas.setText("Total de butacas en el sistema: " + total);

        if (total > 0) {
            barraLibres.setValue((int)(resumen.getTotalLibres() * 100 / total));
            barraReservadas.setValue((int)(resumen.getTotalReservadas() * 100 / total));
            barraOcupadas.setValue((int)(resumen.getTotalOcupadas() * 100 / total));
        }
        repaint();
    }
}
