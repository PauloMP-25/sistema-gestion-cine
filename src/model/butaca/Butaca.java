package model.butaca;

/**
 * Representa un asiento individual de la sala de cine.
 * Su posición (fila, columna) es inmutable; su estado puede cambiar
 * a lo largo de la sesión mediante el setter correspondiente.
 */
// PARADIGMA: Orientado a Objetos — Encapsulamiento de estado con identidad inmutable
public class Butaca {

    private final int fila;
    private final int columna;
    private EstadoButaca estado;

    /**
     * Crea una butaca en la posición indicada con estado LIBRE por defecto.
     * @param fila    Índice de fila de la butaca.
     * @param columna Índice de columna de la butaca.
     */
    public Butaca(int fila, int columna) {
        this(fila, columna, EstadoButaca.LIBRE);
    }

    /**
     * Crea una butaca en la posición y estado indicados.
     * @param fila    Índice de fila de la butaca.
     * @param columna Índice de columna de la butaca.
     * @param estado  Estado inicial de la butaca.
     */
    public Butaca(int fila, int columna, EstadoButaca estado) {
        this.fila    = fila;
        this.columna = columna;
        this.estado  = estado;
    }

    /** @return Índice de fila de esta butaca. */
    public int getFila() {
        return fila;
    }

    /** @return Índice de columna de esta butaca. */
    public int getColumna() {
        return columna;
    }

    /** @return Estado actual de la butaca. */
    public EstadoButaca getEstado() {
        return estado;
    }

    /**
     * Cambia el estado de la butaca.
     * La validación de la transición es responsabilidad de la capa de servicio.
     * @param estado Nuevo estado a asignar.
     */
    public void setEstado(EstadoButaca estado) {
        this.estado = estado;
    }
}
