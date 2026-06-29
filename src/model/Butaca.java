package model;

public class Butaca {
    
    private final int fila;
    private final int columna;
    private EstadoButaca estado;

    /**
     * Crea una nueva butaca en la posición especificada con estado inicial LIBRE.
     * @param fila    Índice de fila de la butaca.
     * @param columna Índice de columna de la butaca.
     */
    
    public Butaca(int fila, int columna) {
        this.fila = fila;
        this.columna = columna;
        this.estado = EstadoButaca.LIBRE;
    }

    /**
     * Crea una nueva butaca en la posición y estado especificados.
     * @param fila    Índice de fila de la butaca.
     * @param columna Índice de columna de la butaca.
     * @param estado  Estado inicial de la butaca.
     */
    public Butaca(int fila, int columna, EstadoButaca estado) {
        this.fila = fila;
        this.columna = columna;
        this.estado = estado;
    }

    public int getFila() {
        return fila;
    }

    public int getColumna() {
        return columna;
    }

    public EstadoButaca getEstado() {
        return estado;
    }

    public void setEstado(EstadoButaca estado) {
        this.estado = estado;
    }
}
