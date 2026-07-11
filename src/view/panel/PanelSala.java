package view.panel;

import view.*;
import view.dialog.*;
import view.component.*;


import exception.AsientoNoReservadoException;
import exception.AsientoOcupadoException;
import exception.AsientoYaReservadoException;
import exception.PosicionInvalidaException;
import model.auth.Rol;
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
import java.util.function.Consumer;

/**
 * Panel visual que representa la matriz de butacas de la sala.
 * Genera dinámicamente los botones y sincroniza su color con el estado
 * del backend a través de las interfaces ISalaService e ISalaQuery.
 */
public class PanelSala extends JPanel {

    private final ISalaService    salaService;
    private final ISalaQuery      salaQuery;
    private final Rol             rol;
    private final BotonButaca[][] botones;
    private final int filas;
    private final int columnas;
    private Runnable alCambiarEstado;
    private Consumer<Integer> alSeleccionarButaca;

    /**
     * Crea el panel de sala, recibiendo las dependencias del backend
     * por constructor (Dependency Inversion: nunca se instancian aquí).
     * @param salaService servicio de operaciones de escritura.
     * @param salaQuery   servicio de consultas de solo lectura.
     */
    public PanelSala(ISalaService salaService, ISalaQuery salaQuery, Rol rol) {
        this.salaService = salaService;
        this.salaQuery   = salaQuery;
        this.rol         = rol;
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
        gbc.insets = new Insets(4, 5, 4, 5);
        add(new JLabel(""), gbc);

        for (int j = 0; j < columnas; j++) {
            gbc.gridx = j + 1;
            gbc.gridy = 0;
            
            int rightInset = 5;
            if (columnas == 6 && j == 2) rightInset = 35;
            else if (columnas >= 7 && (j == 1 || j == columnas - 3)) rightInset = 35;
            
            gbc.insets = new Insets(4, 5, 4, rightInset);
            
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
            gbc.insets = new Insets(4, 5, 4, 5);
            JLabel lblFila = new JLabel("F" + (i + 1), SwingConstants.CENTER);
            lblFila.setFont(new Font("Segoe UI", Font.BOLD, 11));
            lblFila.setForeground(UIConstants.TEXTO_SECUNDARIO);
            lblFila.setPreferredSize(new Dimension(28, UIConstants.ALTO_BOTON));
            add(lblFila, gbc);

            for (int j = 0; j < columnas; j++) {
                gbc.gridx = j + 1;
                gbc.gridy = i + 1;
                
                int rightInset = 5;
                if (columnas == 6 && j == 2) rightInset = 35;
                else if (columnas >= 7 && (j == 1 || j == columnas - 3)) rightInset = 35;
                
                gbc.insets = new Insets(4, 5, 4, rightInset);
                
                int numeroAsiento = (i * columnas) + j + 1;
                BotonButaca boton = crearBotonButaca(i, j, numeroAsiento);
                botones[i][j] = boton;
                add(boton, gbc);
            }
        }
    }
    // FIN RUTINA: Creación de la grilla de butacas

    // Factory Method (implícito): centraliza la creación de cada botón.
    private BotonButaca crearBotonButaca(int fila, int columna, int numeroAsiento) {
        BotonButaca boton = new BotonButaca(fila, columna, numeroAsiento);
        // PARADIGMA: Orientado a Eventos — ActionListener como Observer
        boton.addActionListener(e -> onButacaClick(boton.getFila(), boton.getColumna(), numeroAsiento));
        return boton;
    }

    // Separar la lógica de evento en un método privado con prefijo "on"
    private void onButacaClick(int fila, int columna, int numeroAsiento) {
        if (alSeleccionarButaca != null) {
            alSeleccionarButaca.accept(numeroAsiento);
        }
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

    // Determina la transición de estado al hacer clic
    private void ejecutarAccionSegunEstado(int fila, int columna) {
        BotonButaca boton = botones[fila][columna];
        switch (boton.getEstado()) {
            case LIBRE:
                java.awt.Frame padre = (java.awt.Frame) javax.swing.SwingUtilities.getWindowAncestor(this);
                int seatNum = (fila * columnas) + columna + 1;
                DialogReserva d = new DialogReserva(padre, fila, columna, seatNum);
                d.setVisible(true);
                if (d.isConfirmado()) {
                    salaService.reservar(fila, columna);
                    sincronizarBoton(fila, columna);
                    notificarCambio();
                    DialogMensaje.mostrar(padre, DialogMensaje.Tipo.EXITO, "Se ha reservado el asiento número " + seatNum + ".");
                }
                break;
            case RESERVADO:
                manejarButacaReservada(fila, columna);
                break;
            default:
                break;
        }
    }

    // Muestra opciones Comprar / Cancelar Reserva para butacas en estado RESERVADO
    private void manejarButacaReservada(int fila, int columna) {
        java.awt.Frame padre = (java.awt.Frame) javax.swing.SwingUtilities.getWindowAncestor(this);
        DialogOpcionesButaca dialogo = new DialogOpcionesButaca(padre, fila, columna);
        dialogo.setVisible(true);

        DialogOpcionesButaca.Opcion opcion = dialogo.getOpcionElegida();

        if (opcion == DialogOpcionesButaca.Opcion.COMPRAR) {
            DialogPagoQR pagoQR = new DialogPagoQR(padre, fila, columna);
            pagoQR.setVisible(true);
            if (pagoQR.isAceptado()) {
                int seatNum = (fila * columnas) + columna + 1;
                int confirm = JOptionPane.showConfirmDialog(padre, "¿Confirmar la ocupación para el asiento número " + seatNum + "?", "Confirmar Ocupación", JOptionPane.YES_NO_OPTION);
                if (confirm == JOptionPane.YES_OPTION) {
                    salaService.ocupar(fila, columna);
                    sincronizarBoton(fila, columna);
                    notificarCambio();
                    DialogMensaje.mostrar(padre, DialogMensaje.Tipo.EXITO, "Se ha ocupado el asiento número " + seatNum + ".");
                }
            }
        } else if (opcion == DialogOpcionesButaca.Opcion.CANCELAR) {
            salaService.cancelar(fila, columna);
            sincronizarBoton(fila, columna);
            notificarCambio();
            int seatNum = (fila * columnas) + columna + 1;
            DialogMensaje.mostrar(padre, DialogMensaje.Tipo.EXITO, "Se ha cancelado la reserva del asiento número " + seatNum + ".");
        }
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

    public void setAlSeleccionarButaca(Consumer<Integer> callback) {
        this.alSeleccionarButaca = callback;
    }

    private void notificarCambio() {
        if (alCambiarEstado != null) {
            alCambiarEstado.run();
        }
    }

    private void mostrarError(String mensaje) {
        java.awt.Frame padre = (java.awt.Frame) javax.swing.SwingUtilities.getWindowAncestor(this);
        DialogMensaje.mostrar(padre, DialogMensaje.Tipo.ERROR, mensaje);
    }
}
