package service;

import model.Butaca;
import model.EstadoButaca;

/**
 * Singleton encargado de administrar la estructura principal de la sala.
 * Inicializa y posee la matriz de butacas.
 */
public class SalaManager {
    
    /** Cantidad máxima de filas en la sala. */
    public static final int MAX_FILAS = 5;
    
    /** Cantidad máxima de columnas en la sala. */
    public static final int MAX_COLS = 6;

    private static SalaManager instancia;
    private final Butaca[][] butacas;

    // PARADIGMA: Imperativo — Inicialización y recorrido de la matriz de butacas
    private SalaManager() {
        butacas = new Butaca[MAX_FILAS][MAX_COLS];
        inicializarSala();
    }

    /**
     * Obtiene la instancia única de SalaManager (patrón Singleton).
     * @return La instancia única de SalaManager.
     */
    public static synchronized SalaManager getInstance() {
        if (instancia == null) {
            instancia = new SalaManager();
        }
        return instancia;
    }

    // INICIO RUTINA: Inicialización de la matriz de butacas
    /**
     * Inicializa la matriz de butacas con objetos Butaca en estado LIBRE.
     */
    private void inicializarSala() {
        for (int i = 0; i < MAX_FILAS; i++) {
            for (int j = 0; j < MAX_COLS; j++) {
                butacas[i][j] = new Butaca(i, j, EstadoButaca.LIBRE);
            }
        }
    }
    // FIN RUTINA: Inicialización de la matriz de butacas

    /**
     * Obtiene la matriz completa de butacas de la sala.
     * @return Arreglo bidimensional de butacas.
     */
    public Butaca[][] getButacas() {
        return butacas;
    }

    /**
     * Obtiene una butaca en una posición específica de la sala.
     * @param fila    Índice de la fila.
     * @param columna Índice de la columna.
     * @return La butaca en la posición especificada, o null si los índices están fuera de rango.
     */
    public Butaca getButaca(int fila, int columna) {
        if (fila >= 0 && fila < MAX_FILAS && columna >= 0 && columna < MAX_COLS) {
            return butacas[fila][columna];
        }
        return null;
    }
}
