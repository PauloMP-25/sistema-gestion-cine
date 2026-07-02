package service;

import exception.AsientoNoReservadoException;
import exception.AsientoOcupadoException;
import exception.AsientoYaReservadoException;
import exception.PosicionInvalidaException;
import model.Butaca;
import model.EstadoButaca;
import model.SalaCine;
import service.interfaces.ISalaService;

/**
 * Implementación de ISalaService con la lógica de negocio para reservar
 * y cancelar butacas. Actúa como Facade coordinando la validación,
 * el acceso a la matriz (SalaManager) y el cambio de estado (Butaca).
 */
// PARADIGMA: Orientado a Objetos — Implementación de interfaz, encapsulamiento y composición
public class SalaService implements ISalaService {

    private final SalaCine manager;

    /**
     * Crea una instancia de SalaService usando el gestor de sala dado.
     * @param manager El SalaManager que contiene la matriz de butacas.
     */
    public SalaService(SalaCine manager) {
        this.manager = manager;
    }

    /**
     * Reserva la butaca en la posición indicada.
     * Solo puede reservarse si la butaca está en estado LIBRE.
     * @param fila    Índice de fila (0 a MAX_FILAS-1).
     * @param columna Índice de columna (0 a MAX_COLS-1).
     * @throws PosicionInvalidaException   si la posición está fuera de rango.
     * @throws AsientoOcupadoException     si la butaca está OCUPADA.
     * @throws AsientoYaReservadoException si la butaca ya está RESERVADA.
     */
    // INICIO rutina: Reserva de butaca
    @Override
    public void reservar(int fila, int columna)
            throws PosicionInvalidaException, AsientoOcupadoException, AsientoYaReservadoException {
        validarPosicion(fila, columna);
        Butaca butaca = manager.getButaca(fila, columna);
        verificarEstadoParaReserva(butaca);
        cambiarEstado(butaca, EstadoButaca.RESERVADO);
    }
    // FIN rutina: Reserva de butaca

    /**
     * Cancela la reserva de la butaca en la posición indicada.
     * Solo puede cancelarse si la butaca está en estado RESERVADO.
     * @param fila    Índice de fila (0 a MAX_FILAS-1).
     * @param columna Índice de columna (0 a MAX_COLS-1).
     * @throws PosicionInvalidaException   si la posición está fuera de rango.
     * @throws AsientoNoReservadoException si la butaca no está RESERVADA.
     */
    // inicio rutina: Cancelación de reserva
    @Override
    public void cancelar(int fila, int columna)
            throws PosicionInvalidaException, AsientoNoReservadoException {
        validarPosicion(fila, columna);
        Butaca butaca = manager.getButaca(fila, columna);
        verificarEstadoParaCancelar(butaca);
        cambiarEstado(butaca, EstadoButaca.LIBRE);
    }
    // fin rutina: Cancelación de reserva

    @Override
    public void ocupar(int fila, int columna) throws PosicionInvalidaException {
        validarPosicion(fila, columna);
        Butaca butaca = manager.getButaca(fila, columna);
        cambiarEstado(butaca, EstadoButaca.OCUPADO);
    }

    // paradigma: Imperativo — Validación secuencial de rangos con condicionales
    // inicio rutina: Validación de posición
    private void validarPosicion(int fila, int columna) throws PosicionInvalidaException {
        if (fila < 0 || fila >= SalaCine.MAX_FILAS
                || columna < 0 || columna >= SalaCine.MAX_COLS) {
            throw new PosicionInvalidaException(fila, columna);
        }
    }
    // fin rutina: Validación de posición

    // inicio rutina: Verificación de estado para reservar
    private void verificarEstadoParaReserva(Butaca butaca)
            throws AsientoOcupadoException, AsientoYaReservadoException {
        if (butaca.getEstado() == EstadoButaca.OCUPADO) {
            throw new AsientoOcupadoException(butaca.getFila(), butaca.getColumna());
        }
        if (butaca.getEstado() == EstadoButaca.RESERVADO) {
            throw new AsientoYaReservadoException(butaca.getFila(), butaca.getColumna());
        }
    }
    // fin rutina: Verificación de estado para reservar

    // inicio rutina: Verificación de estado para cancelar
    private void verificarEstadoParaCancelar(Butaca butaca) throws AsientoNoReservadoException {
        if (butaca.getEstado() != EstadoButaca.RESERVADO) {
            throw new AsientoNoReservadoException(butaca.getFila(), butaca.getColumna());
        }
    }
    // fin rutina: Verificación de estado para cancelar

    private void cambiarEstado(Butaca butaca, EstadoButaca nuevoEstado) {
        butaca.setEstado(nuevoEstado);
    }
}
