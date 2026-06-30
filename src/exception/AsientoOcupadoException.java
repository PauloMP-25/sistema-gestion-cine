package exception;

/**
 * Excepción lanzada cuando se intenta reservar una butaca
 * que ya se encuentra en estado OCUPADO.
 */
// PARADIGMA: Orientado a Objetos — Herencia de RuntimeException
public class AsientoOcupadoException extends RuntimeException {

    /**
     * Crea la excepción indicando el asiento ocupado.
     * @param fila    Fila de la butaca ocupada.
     * @param columna Columna de la butaca ocupada.
     */
    public AsientoOcupadoException(int fila, int columna) {
        super("La butaca [" + fila + "][" + columna + "] esta OCUPADA y no puede reservarse.");
    }
}
