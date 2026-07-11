package model.reserva;

/**
 * Resultado de un intento de reserva individual dentro de una operación
 * de reserva múltiple. Registra si la reserva fue exitosa y, si no,
 * el motivo del fallo sin interrumpir las demás reservas del lote.
 */
// PARADIGMA: Orientado a Objetos — Objeto de valor inmutable para transferencia de datos
public class ResultadoReserva {

    private final int fila;
    private final int columna;
    private final boolean exitoso;
    private final String mensaje;

    /**
     * Crea el resultado de un intento de reserva.
     * @param fila     Índice de fila de la butaca intentada.
     * @param columna  Índice de columna de la butaca intentada.
     * @param exitoso  true si la reserva se completó sin errores.
     * @param mensaje  Descripción del resultado (éxito o motivo de fallo).
     */
    public ResultadoReserva(int fila, int columna, boolean exitoso, String mensaje) {
        this.fila    = fila;
        this.columna = columna;
        this.exitoso = exitoso;
        this.mensaje = mensaje;
    }

    /** @return Índice de fila de la butaca. */
    public int getFila() {
        return fila;
    }

    /** @return Índice de columna de la butaca. */
    public int getColumna() {
        return columna;
    }

    /** @return true si la reserva se realizó correctamente. */
    public boolean isExitoso() {
        return exitoso;
    }

    /** @return Descripción del resultado del intento de reserva. */
    public String getMensaje() {
        return mensaje;
    }
}
