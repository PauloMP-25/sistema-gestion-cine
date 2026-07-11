package model.sala;

/**
 * Fábrica responsable de crear instancias de SalaCine con validación previa.
 * Verifica que las dimensiones estén dentro del rango permitido y que el total
 * de asientos ingresado coincida exactamente con filas × columnas.
 */
// PARADIGMA: Orientado a Objetos — Factory Method con métodos estáticos de validación
public class SalaFactory {

    private static final int MIN_DIMENSION = 1;
    private static final int MAX_DIMENSION = 15;

    private SalaFactory() {
        // Clase utilitaria — no se instancia
    }

    /**
     * Crea una sala de cine validando las dimensiones y el total de asientos.
     * Si el total ingresado no coincide con filas × columnas, se lanza una excepción
     * con el valor esperado para que el usuario pueda corregir el formulario.
     * @param nombre        Nombre de la sala.
     * @param totalAsientos Total de asientos ingresado por el usuario para verificación.
     * @param filas         Número de filas.
     * @param cols          Número de columnas.
     * @return Nueva instancia de SalaCine lista para usarse.
     * @throws IllegalArgumentException si alguna dimensión está fuera de rango o
     *                                  el total no coincide con filas × columnas.
     */
    // INICIO RUTINA: Creación y validación de sala
    public static SalaCine crearSala(String nombre, int totalAsientos, int filas, int cols) {
        validarNombre(nombre);
        validarDimension(filas, "filas");
        validarDimension(cols, "columnas");
        validarTotalAsientos(totalAsientos, filas, cols);
        return new SalaCine(nombre, filas, cols);
    }
    // FIN RUTINA: Creación y validación de sala

    // PARADIGMA: Imperativo — Validaciones secuenciales con cláusulas de guarda
    private static void validarNombre(String nombre) {
        if (nombre == null || nombre.isBlank()) {
            throw new IllegalArgumentException("El nombre de la sala no puede estar vacío.");
        }
    }

    private static void validarDimension(int valor, String campo) {
        if (valor < MIN_DIMENSION || valor > MAX_DIMENSION) {
            throw new IllegalArgumentException(
                "El número de " + campo + " debe estar entre "
                + MIN_DIMENSION + " y " + MAX_DIMENSION
                + ". Valor ingresado: " + valor + ".");
        }
    }

    private static void validarTotalAsientos(int totalAsientos, int filas, int cols) {
        int esperado = filas * cols;
        if (totalAsientos != esperado) {
            throw new IllegalArgumentException(
                "El total de asientos ingresado (" + totalAsientos
                + ") no coincide con filas × columnas ("
                + filas + " × " + cols + " = " + esperado + ").");
        }
    }
}
