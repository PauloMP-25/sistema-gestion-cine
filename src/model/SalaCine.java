package model;

/**
 * Representa una sala de cine con su propia matriz de butacas.
 * Cada instancia es completamente independiente: valida posiciones
 * contra su propio tamaño y no comparte estado con otras salas.
 */
// PARADIGMA: Orientado a Objetos — Encapsulamiento y responsabilidad única
public class SalaCine {

    private static int siguienteId = 1;

    private final int id;
    private String nombre;
    private final int filas;
    private final int cols;
    private final Butaca[][] butacas;

    /**
     * Crea una nueva sala de cine con el nombre y dimensiones indicados.
     * Todas las butacas se inicializan en estado LIBRE.
     * @param nombre Nombre descriptivo de la sala (ej. "Sala 1").
     * @param filas  Número de filas de la sala.
     * @param cols   Número de columnas de la sala.
     */
    public SalaCine(String nombre, int filas, int cols) {
        this.id     = siguienteId++;
        this.nombre = nombre;
        this.filas  = filas;
        this.cols   = cols;
        this.butacas = new Butaca[filas][cols];
        inicializarSala();
    }

    // INICIO RUTINA: Inicialización de la matriz de butacas
    // PARADIGMA: Imperativo — Recorrido secuencial con bucles anidados
    private void inicializarSala() {
        for (int i = 0; i < filas; i++) {
            for (int j = 0; j < cols; j++) {
                butacas[i][j] = new Butaca(i, j, EstadoButaca.LIBRE);
            }
        }
    }
    // FIN RUTINA: Inicialización de la matriz de butacas

    /** @return Identificador único de la sala. */
    public int getId() {
        return id;
    }

    /** @return Nombre descriptivo de la sala. */
    public String getNombre() { return nombre; }

    /** Actualiza el nombre descriptivo de la sala. */
    public void setNombre(String nombre) {
        if (nombre != null && !nombre.isBlank()) this.nombre = nombre.trim();
    }

    /** @return Número de filas de la sala. */
    public int getFilas() {
        return filas;
    }

    /** @return Número de columnas de la sala. */
    public int getCols() {
        return cols;
    }

    /**
     * Obtiene la matriz completa de butacas de la sala.
     * @return Arreglo bidimensional de butacas.
     */
    public Butaca[][] getButacas() {
        return butacas;
    }

    /**
     * Obtiene la butaca en la posición indicada, o null si está fuera de rango.
     * @param fila    Índice de fila.
     * @param columna Índice de columna.
     * @return La butaca en esa posición, o null si los índices son inválidos.
     */
    public Butaca getButaca(int fila, int columna) {
        if (fila >= 0 && fila < filas && columna >= 0 && columna < cols) {
            return butacas[fila][columna];
        }
        return null;
    }
}
