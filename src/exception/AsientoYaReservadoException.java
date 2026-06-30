package exception;

/**
 * Excepción lanzada cuando se intenta reservar una butaca
 * que ya se encuentra en estado RESERVADO.
 */
// PARADIGMA: Orientado a Objetos — Herencia de RuntimeException
public class AsientoYaReservadoException extends RuntimeException {

    /**
     * Crea la excepción indicando el asiento ya reservado.
     * @param fila    Fila de la butaca reservada.
     * @param columna Columna de la butaca reservada.
     */
    public AsientoYaReservadoException(int fila, int columna) {
        super("La butaca [" + fila + "][" + columna + "] ya esta RESERVADA.");
    }
}
