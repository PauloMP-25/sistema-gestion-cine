package service.interfaces;

import exception.AsientoNoReservadoException;
import exception.AsientoOcupadoException;
import exception.AsientoYaReservadoException;
import exception.PosicionInvalidaException;
import model.reserva.ResultadoReserva;

import java.util.List;

/**
 * Contrato de operaciones de escritura sobre una sala de cine.
 * Define las acciones que modifican el estado de las butacas.
 * El equipo Frontend depende únicamente de esta interfaz, nunca de la implementación concreta.
 */
// PARADIGMA: Orientado a Objetos — Interfaz como contrato de abstracción (SOLID: DIP, ISP)
public interface ISalaService {

    /**
     * Reserva la butaca en la posición indicada.
     * Solo puede reservarse si la butaca está en estado LIBRE.
     * @param fila    Índice de fila (0 a filas-1).
     * @param columna Índice de columna (0 a cols-1).
     * @throws PosicionInvalidaException   si fila o columna están fuera de rango.
     * @throws AsientoOcupadoException     si la butaca está en estado OCUPADO.
     * @throws AsientoYaReservadoException si la butaca ya está en estado RESERVADO.
     */
    void reservar(int fila, int columna)
            throws PosicionInvalidaException, AsientoOcupadoException, AsientoYaReservadoException;

    /**
     * Cancela la reserva de la butaca en la posición indicada.
     * Solo puede cancelarse si la butaca está en estado RESERVADO.
     * @param fila    Índice de fila (0 a filas-1).
     * @param columna Índice de columna (0 a cols-1).
     * @throws PosicionInvalidaException   si fila o columna están fuera de rango.
     * @throws AsientoNoReservadoException si la butaca no está en estado RESERVADO.
     */
    void cancelar(int fila, int columna)
            throws PosicionInvalidaException, AsientoNoReservadoException;

    /**
     * Marca la butaca en la posición indicada como OCUPADO, sin validación de estado previo.
     * Útil para simular una butaca vendida directamente en taquilla.
     * @param fila    Índice de fila (0 a filas-1).
     * @param columna Índice de columna (0 a cols-1).
     * @throws PosicionInvalidaException si fila o columna están fuera de rango.
     */
    void ocupar(int fila, int columna) throws PosicionInvalidaException;

    /**
     * Intenta reservar varias butacas en una sola operación.
     * Cada posición se procesa de forma independiente: un fallo en una
     * no detiene las demás. El resultado indica cuáles tuvieron éxito y cuáles no.
     * @param posiciones Lista de posiciones, cada una como int[]{fila, columna}.
     * @return Lista de ResultadoReserva, uno por cada posición recibida, en el mismo orden.
     */
    List<ResultadoReserva> reservarMultiple(List<int[]> posiciones);

    /**
     * Fuerza la butaca indicada al estado LIBRE sin importar su estado actual.
     * @param fila    Índice de fila (0 a filas-1).
     * @param columna Índice de columna (0 a cols-1).
     * @throws PosicionInvalidaException si la posición está fuera de rango.
     */
    void liberar(int fila, int columna) throws PosicionInvalidaException;

    /**
     * Restablece todas las butacas de la sala al estado LIBRE.
     */
    void limpiarSala();
}
