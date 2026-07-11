package service;

import model.butaca.EstadoButaca;
import model.sala.ResumenSalas;
import model.sala.SalaCine;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Gestiona el conjunto de salas de cine activas en memoria durante la sesión.
 * Permite agregar, listar, consultar y eliminar salas, además de calcular
 * el resumen global de ocupación para el dashboard del lobby.
 */
// PARADIGMA: Orientado a Objetos — Clase gestora con responsabilidad única
public class GestorSalas {

    // LinkedHashMap para mantener el orden de inserción de las salas
    private final Map<Integer, SalaCine> salas;

    /**
     * Crea un gestor de salas vacío, listo para registrar nuevas salas.
     */
    public GestorSalas() {
        this.salas = new LinkedHashMap<>();
    }

    /**
     * Registra una nueva sala en el gestor.
     * @param sala Sala a agregar. No puede ser null.
     */
    public void agregarSala(SalaCine sala) {
        if (sala == null) {
            throw new IllegalArgumentException("La sala no puede ser null.");
        }
        salas.put(sala.getId(), sala);
    }

    /**
     * Devuelve la lista de todas las salas registradas, en orden de creación.
     * @return Lista de salas (puede estar vacía, nunca null).
     */
    public List<SalaCine> listarSalas() {
        return new ArrayList<>(salas.values());
    }

    /**
     * Busca y devuelve la sala con el id indicado.
     * @param id Identificador único de la sala.
     * @return La sala encontrada, o null si no existe ninguna con ese id.
     */
    public SalaCine obtenerPorId(int id) {
        return salas.get(id);
    }

    /**
     * Elimina la sala con el id indicado del gestor.
     * @param id Identificador único de la sala a eliminar.
     * @return true si la sala existía y fue eliminada; false si no se encontró.
     */
    public boolean eliminarPorId(int id) {
        return salas.remove(id) != null;
    }

    /**
     * Indica si la sala con el id indicado tiene al menos una butaca
     * en estado RESERVADO u OCUPADO. Útil para mostrar una advertencia
     * antes de confirmar la eliminación de la sala.
     * @param id Identificador de la sala a revisar.
     * @return true si hay butacas no libres; false si todas están libres o la sala no existe.
     */
    // INICIO RUTINA: Verificación de butacas no libres antes de eliminar
    // PARADIGMA: Funcional — Stream con anyMatch para búsqueda de estado
    public boolean tieneButacasNoLibres(int id) {
        SalaCine sala = salas.get(id);
        if (sala == null) {
            return false;
        }
        return Arrays.stream(sala.getButacas())
                .flatMap(Arrays::stream)
                .anyMatch(b -> b.getEstado() != EstadoButaca.LIBRE);
    }
    // FIN RUTINA: Verificación de butacas no libres antes de eliminar

    /**
     * Calcula el resumen global de ocupación sumando todas las salas registradas.
     * Recorre las butacas una sola vez por sala para obtener los tres conteos.
     * @return Objeto con el total de salas, butacas libres, reservadas y ocupadas.
     */
    // INICIO RUTINA: Cálculo del resumen global del dashboard
    // PARADIGMA: Funcional — Streams anidados para agregación de estadísticas globales
    public ResumenSalas obtenerResumenGlobal() {
        long libres = contarEstadoGlobal(EstadoButaca.LIBRE);
        long reservadas = contarEstadoGlobal(EstadoButaca.RESERVADO);
        long ocupadas = contarEstadoGlobal(EstadoButaca.OCUPADO);
        return new ResumenSalas(salas.size(), libres, reservadas, ocupadas);
    }
    // FIN RUTINA: Cálculo del resumen global del dashboard

    private long contarEstadoGlobal(EstadoButaca estado) {
        return salas.values().stream()
                .mapToLong(sala -> contarEstadoEnSala(sala, estado))
                .sum();
    }

    private long contarEstadoEnSala(SalaCine sala, EstadoButaca estado) {
        return Arrays.stream(sala.getButacas())
                .flatMap(Arrays::stream)
                .filter(b -> b.getEstado() == estado)
                .count();
    }
}
