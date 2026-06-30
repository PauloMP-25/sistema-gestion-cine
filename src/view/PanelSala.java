package view;

import exception.AsientoNoReservadoException;
import exception.AsientoOcupadoException;
import exception.AsientoYaReservadoException;
import exception.PosicionInvalidaException;
import service.interfaces.ISalaQuery;
import service.interfaces.ISalaService;

import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

/**
 * Panel visual que representa la matriz de butacas de la sala.
 * Genera dinámicamente los botones y sincroniza su color con el estado
 * del backend a través de las interfaces ISalaService e ISalaQuery.
 */
public class PanelSala extends JPanel {

    private final ISalaService salaService;
    private final ISalaQuery salaQuery;
    private final BotonButaca[][] botones;
    private final int filas;
    private final int columnas;
    private Runnable alCambiarEstado;

    /**
     * Crea el panel de sala, recibiendo las dependencias del backend
     * por constructor (Dependency Inversion: nunca se instancian aquí).
     * @param salaService servicio de operaciones de escritura.
     * @param salaQuery   servicio de consultas de solo lectura.
     */
    public PanelSala(ISalaService salaService, ISalaQuery salaQuery) {
        this.salaService = salaService;
        this.salaQuery = salaQuery;
        var matriz = salaQuery.obtenerMatriz();
        this.filas = matriz.length;
        this.columnas = matriz.length > 0 ? matriz[0].length : 0;
        this.botones = new BotonButaca[filas][columnas];
        setOpaque(false);
        inicializarComponentes();
    }

    // Solo crea y posiciona componentes. No agrega listeners aquí.
    private void inicializarComponentes() {
        setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(4, 5, 4, 5);

        agregarEncabezados(gbc);
        crearGrillaButacas(gbc);
    }

    private void agregarEncabezados(GridBagConstraints gbc) {
        gbc.gridx = 0;
        gbc.gridy = 0;
        add(new JLabel(""), gbc);

        for (int j = 0; j < columnas; j++) {
            gbc.gridx = j + 1;
            gbc.gridy = 0;
            JLabel lblCol = new JLabel("C" + (j + 1), SwingConstants.CENTER);
            lblCol.setFont(new Font("Segoe UI", Font.BOLD, 11));
            lblCol.setForeground(UIConstants.TEXTO_SECUNDARIO);
            lblCol.setPreferredSize(new Dimension(UIConstants.TAMANO_BOTON, 20));
            add(lblCol, gbc);
        }
    }

    // INICIO RUTINA: Creación de la grilla de butacas
    // PARADIGMA: Imperativo — Bucles anidados para construir la matriz visual
    private void crearGrillaButacas(GridBagConstraints gbc) {
        for (int i = 0; i < filas; i++) {
            gbc.gridx = 0;
            gbc.gridy = i + 1;
            JLabel lblFila = new JLabel("F" + (i + 1), SwingConstants.CENTER);
            lblFila.setFont(new Font("Segoe UI", Font.BOLD, 11));
            lblFila.setForeground(UIConstants.TEXTO_SECUNDARIO);
            lblFila.setPreferredSize(new Dimension(28, UIConstants.ALTO_BOTON));
            add(lblFila, gbc);

            for (int j = 0; j < columnas; j++) {
                gbc.gridx = j + 1;
                gbc.gridy = i + 1;
                BotonButaca boton = crearBotonButaca(i, j);
                botones[i][j] = boton;
                add(boton, gbc);
            }
        }
    }
    // FIN RUTINA: Creación de la grilla de butacas

    // Factory Method (implícito): centraliza la creación de cada botón.
    private BotonButaca crearBotonButaca(int fila, int columna) {
        BotonButaca boton = new BotonButaca(fila, columna);
        // PARADIGMA: Orientado a Eventos — ActionListener como Observer
        boton.addActionListener(e -> onButacaClick(boton.getFila(), boton.getColumna()));
        return boton;
    }

    // Separar la lógica de evento en un método privado con prefijo "on"
    private void onButacaClick(int fila, int columna) {
        try {
            ejecutarAccionSegunEstado(fila, columna);
        } catch (PosicionInvalidaException ex) {
            mostrarError("Posición inválida: " + ex.getMessage());
        } catch (AsientoOcupadoException ex) {
            mostrarError("Este asiento está ocupado.");
        } catch (AsientoYaReservadoException ex) {
            mostrarError("Este asiento ya fue reservado.");
        } catch (AsientoNoReservadoException ex) {
            mostrarError("Este asiento no tiene una reserva activa.");
        }
    }

    // Determina la transición de estado al hacer clic: LIBRE -> RESERVADO -> OCUPADO -> LIBRE
    private void ejecutarAccionSegunEstado(int fila, int columna) {
        BotonButaca boton = botones[fila][columna];
        switch (boton.getEstado()) {
            case LIBRE:
                salaService.reservar(fila, columna);
                break;
            case RESERVADO:
                salaService.ocupar(fila, columna);
                break;
            default:
                salaService.cancelar(fila, columna);
                break;
        }
        sincronizarBoton(fila, columna);
        notificarCambio();
    }

    private void sincronizarBoton(int fila, int columna) {
        var butaca = salaQuery.obtenerButaca(fila, columna);
        botones[fila][columna].actualizarColor(butaca.getEstado());
    }

    /**
     * Refresca todos los botones de la grilla según el estado actual del backend.
     * Útil tras operaciones ejecutadas desde otros paneles (ej. PanelControl).
     */
    public void refrescarTodo() {
        for (int i = 0; i < filas; i++) {
            for (int j = 0; j < columnas; j++) {
                sincronizarBoton(i, j);
            }
        }
    }

    /**
     * Define un callback a ejecutar cada vez que cambia el estado de una butaca.
     * @param callback acción a ejecutar (por ejemplo, refrescar contadores).
     */
    public void setAlCambiarEstado(Runnable callback) {
        this.alCambiarEstado = callback;
    }

    private void notificarCambio() {
        if (alCambiarEstado != null) {
            alCambiarEstado.run();
        }
    }

    private void mostrarError(String mensaje) {
        JOptionPane.showMessageDialog(this, mensaje, "Error", JOptionPane.ERROR_MESSAGE);
    }
}
