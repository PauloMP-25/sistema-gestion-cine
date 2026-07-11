package model.sala;

/**
 * Contiene las estadísticas agregadas de todas las salas registradas en el sistema.
 * Pensado para alimentar el dashboard del panel lobby con los totales globales.
 */
// PARADIGMA: Orientado a Objetos — Objeto de valor inmutable para estadísticas globales
public class ResumenSalas {

    private final int totalSalas;
    private final long totalLibres;
    private final long totalReservadas;
    private final long totalOcupadas;

    /**
     * Crea el resumen con los conteos globales de todas las salas.
     * @param totalSalas      Número de salas actualmente en el sistema.
     * @param totalLibres     Suma de butacas LIBRE en todas las salas.
     * @param totalReservadas Suma de butacas RESERVADO en todas las salas.
     * @param totalOcupadas   Suma de butacas OCUPADO en todas las salas.
     */
    public ResumenSalas(int totalSalas, long totalLibres, long totalReservadas, long totalOcupadas) {
        this.totalSalas      = totalSalas;
        this.totalLibres     = totalLibres;
        this.totalReservadas = totalReservadas;
        this.totalOcupadas   = totalOcupadas;
    }

    /** @return Número de salas registradas en el sistema. */
    public int getTotalSalas() {
        return totalSalas;
    }

    /** @return Total de butacas libres sumando todas las salas. */
    public long getTotalLibres() {
        return totalLibres;
    }

    /** @return Total de butacas reservadas sumando todas las salas. */
    public long getTotalReservadas() {
        return totalReservadas;
    }

    /** @return Total de butacas ocupadas sumando todas las salas. */
    public long getTotalOcupadas() {
        return totalOcupadas;
    }

    /** @return Suma de todas las butacas (libres + reservadas + ocupadas). */
    public long getTotalButacas() {
        return totalLibres + totalReservadas + totalOcupadas;
    }
}
