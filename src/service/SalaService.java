package service;

import exception.AsientoNoReservadoException;
import exception.AsientoOcupadoException;
import exception.AsientoYaReservadoException;
import exception.PosicionInvalidaException;
import java.util.ArrayList;
import java.util.List;
import model.butaca.Butaca;
import model.butaca.EstadoButaca;
import model.reserva.ResultadoReserva;
import model.sala.SalaCine;
import service.interfaces.ISalaService;

/**
 * Implementación de ISalaService con la lógica de negocio para reservar,
 * cancelar y ocupar butacas. Actúa como Facade coordinando la validación,
 * el acceso a la matriz y el cambio de estado de cada butaca.
 * Cada instancia opera sobre la sala recibida en el constructor.
 */
// PARADIGMA: Orientado a Objetos — Implementación de interfaz, encapsulamiento y composición
public class SalaService implements ISalaService {

    private final SalaCine sala;

    /**
     * Crea el servicio de operaciones sobre la sala indicada.
     * @param sala La sala de cine sobre la que operará este servicio.
     */
    public SalaService(SalaCine sala) {
        this.sala = sala;
    }

    /**
     * Reserva la butaca en la posición indicada.
     * Solo puede reservarse si la butaca está en estado LIBRE.
     * @param fila    Índice de fila (0 a filas-1).
     * @param columna Índice de columna (0 a cols-1).
     * @throws PosicionInvalidaException   si la posición está fuera de rango.
     * @throws AsientoOcupadoException     si la butaca está OCUPADA.
     * @throws AsientoYaReservadoException si la butaca ya está RESERVADA.
     */
    // INICIO RUTINA: Reserva de butaca
    @Override
    public void reservar(int fila, int columna)
            throws PosicionInvalidaException, AsientoOcupadoException, AsientoYaReservadoException {
        validarPosicion(fila, columna);
        Butaca butaca = sala.getButaca(fila, columna);
        verificarEstadoParaReserva(butaca);
        cambiarEstado(butaca, EstadoButaca.RESERVADO);
    }
    // FIN RUTINA: Reserva de butaca

    /**
     * Cancela la reserva de la butaca en la posición indicada.
     * Solo puede cancelarse si la butaca está en estado RESERVADO.
     * @param fila    Índice de fila (0 a filas-1).
     * @param columna Índice de columna (0 a cols-1).
     * @throws PosicionInvalidaException   si la posición está fuera de rango.
     * @throws AsientoNoReservadoException si la butaca no está RESERVADA.
     */
    // INICIO RUTINA: Cancelación de reserva
    @Override
    public void cancelar(int fila, int columna)
            throws PosicionInvalidaException, AsientoNoReservadoException {
        validarPosicion(fila, columna);
        Butaca butaca = sala.getButaca(fila, columna);
        verificarEstadoParaCancelar(butaca);
        cambiarEstado(butaca, EstadoButaca.LIBRE);
    }
    // FIN RUTINA: Cancelación de reserva

    /**
     * Marca la butaca en la posición indicada como OCUPADO.
     * @param fila    Índice de fila (0 a filas-1).
     * @param columna Índice de columna (0 a cols-1).
     * @throws PosicionInvalidaException si la posición está fuera de rango.
     */
    @Override
    public void ocupar(int fila, int columna) throws PosicionInvalidaException {
        validarPosicion(fila, columna);
        Butaca butaca = sala.getButaca(fila, columna);
        cambiarEstado(butaca, EstadoButaca.OCUPADO);
    }

    /**
     * Intenta reservar una lista de butacas en una sola operación.
     * Cada posición se procesa de forma independiente sin detener el lote ante un error.
     * @param posiciones Lista de posiciones como int[]{fila, columna}.
     * @return Lista de ResultadoReserva con el detalle de cada intento, en el mismo orden.
     */
    // INICIO RUTINA: Reserva múltiple de butacas
    // PARADIGMA: Funcional — transformación de lista sin interrumpir ante errores parciales
    @Override
    public List<ResultadoReserva> reservarMultiple(List<int[]> posiciones) {
        List<ResultadoReserva> resultados = new ArrayList<>();
        for (int[] pos : posiciones) {
            resultados.add(intentarReservaIndividual(pos[0], pos[1]));
        }
        return resultados;
    }
    // FIN RUTINA: Reserva múltiple de butacas

    private ResultadoReserva intentarReservaIndividual(int fila, int columna) {
        try {
            reservar(fila, columna);
            return new ResultadoReserva(fila, columna, true,
                    "Reserva exitosa en F" + (fila + 1) + "-C" + (columna + 1) + ".");
        } catch (AsientoOcupadoException | AsientoYaReservadoException | PosicionInvalidaException ex) {
            return new ResultadoReserva(fila, columna, false, ex.getMessage());
        }
    }

    // PARADIGMA: Imperativo — Validación secuencial de rangos con condicionales
    // INICIO RUTINA: Validación de posición
    private void validarPosicion(int fila, int columna) throws PosicionInvalidaException {
        if (fila < 0 || fila >= sala.getFilas()
                || columna < 0 || columna >= sala.getCols()) {
            throw new PosicionInvalidaException(fila, columna);
        }
    }
    // FIN RUTINA: Validación de posición

    // INICIO RUTINA: Verificación de estado para reservar
    private void verificarEstadoParaReserva(Butaca butaca)
            throws AsientoOcupadoException, AsientoYaReservadoException {
        if (butaca.getEstado() == EstadoButaca.OCUPADO) {
            throw new AsientoOcupadoException(butaca.getFila(), butaca.getColumna());
        }
        if (butaca.getEstado() == EstadoButaca.RESERVADO) {
            throw new AsientoYaReservadoException(butaca.getFila(), butaca.getColumna());
        }
    }
    // FIN RUTINA: Verificación de estado para reservar

    // INICIO RUTINA: Verificación de estado para cancelar
    private void verificarEstadoParaCancelar(Butaca butaca) throws AsientoNoReservadoException {
        if (butaca.getEstado() != EstadoButaca.RESERVADO) {
            throw new AsientoNoReservadoException(butaca.getFila(), butaca.getColumna());
        }
    }
    // FIN RUTINA: Verificación de estado para cancelar

    @Override
    public void liberar(int fila, int columna) throws PosicionInvalidaException {
        validarPosicion(fila, columna);
        cambiarEstado(sala.getButaca(fila, columna), EstadoButaca.LIBRE);
    }

    // INICIO RUTINA: Limpieza total de la sala
    @Override
    public void limpiarSala() {
        for (Butaca[] fila : sala.getButacas()) {
            for (Butaca butaca : fila) {
                butaca.setEstado(EstadoButaca.LIBRE);
            }
        }
    }
    // FIN RUTINA: Limpieza total de la sala

    private void cambiarEstado(Butaca butaca, EstadoButaca nuevoEstado) {
        butaca.setEstado(nuevoEstado);
    }
}
