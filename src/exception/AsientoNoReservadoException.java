package exception;

/**
 * Excepción lanzada cuando se intenta cancelar la reserva de una butaca
 * que no se encuentra en estado RESERVADO.
 */
// PARADIGMA: Orientado a Objetos — Herencia de RuntimeException
public class AsientoNoReservadoException extends RuntimeException {

    /**
     * Crea la excepción indicando el asiento sin reserva activa.
     * @param fila    Fila de la butaca.
     * @param columna Columna de la butaca.
     */
    public AsientoNoReservadoException(int fila, int columna) {
        super("La butaca [" + fila + "][" + columna + "] no tiene reserva activa. No se puede cancelar.");
    }
}
