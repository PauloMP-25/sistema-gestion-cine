package service.interfaces;

import exception.AsientoNoReservadoException;
import exception.AsientoOcupadoException;
import exception.AsientoYaReservadoException;
import exception.PosicionInvalidaException;

/**
 * Contrato de operaciones de escritura sobre la sala de cine.
 * Define las acciones que modifican el estado de las butacas.
 */
// PARADIGMA: Orientado a Objetos — Interfaz como contrato (abstracción)
public interface ISalaService {

    /**
     * Reserva la butaca en la posición indicada.
     * @param fila    Índice de fila (0 a MAX_FILAS-1).
     * @param columna Índice de columna (0 a MAX_COLS-1).
     * @throws PosicionInvalidaException   si fila o columna están fuera de rango.
     * @throws AsientoOcupadoException     si la butaca está en estado OCUPADO.
     * @throws AsientoYaReservadoException si la butaca ya está en estado RESERVADO.
     */
    void reservar(int fila, int columna)
            throws PosicionInvalidaException, AsientoOcupadoException, AsientoYaReservadoException;

    /**
     * Cancela la reserva de la butaca en la posición indicada.
     * @param fila    Índice de fila (0 a MAX_FILAS-1).
     * @param columna Índice de columna (0 a MAX_COLS-1).
     * @throws PosicionInvalidaException   si fila o columna están fuera de rango.
     * @throws AsientoNoReservadoException si la butaca no está en estado RESERVADO.
     */
    void cancelar(int fila, int columna)
            throws PosicionInvalidaException, AsientoNoReservadoException;

    void ocupar(int fila, int columna) throws PosicionInvalidaException;
}
