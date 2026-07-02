package exception;

/**
 * Excepción lanzada cuando se intenta acceder a una posición
 * de fila o columna que está fuera del rango válido de la sala.
 */
public class PosicionInvalidaException extends RuntimeException {

    /**
     * Crea la excepción indicando la posición inválida.
     * @param fila    Fila que causó el error.
     * @param columna Columna que causó el error.
     */
    public PosicionInvalidaException(int fila, int columna) {
        super("Posicion invalida: fila=" + fila + ", columna=" + columna
                + ". Debe estar dentro del rango permitido.");
    }
}
