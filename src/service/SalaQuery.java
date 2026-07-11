package service;

import model.butaca.Butaca;
import model.butaca.EstadoButaca;
import model.sala.SalaCine;
import service.interfaces.ISalaQuery;

import java.util.Arrays;

/**
 * Implementación de ISalaQuery que realiza consultas de solo lectura sobre
 * una sala de cine usando la API de Streams de Java. Ningún método modifica
 * el estado de ninguna butaca; solo lee y agrega datos.
 */
// PARADIGMA: Funcional — Consultas declarativas con Streams sin efectos secundarios
public class SalaQuery implements ISalaQuery {

    private final SalaCine sala;

    /**
     * Crea el servicio de consultas sobre la sala indicada.
     * @param sala La sala de cine que será consultada.
     */
    public SalaQuery(SalaCine sala) {
        this.sala = sala;
    }

    /**
     * Cuenta las butacas actualmente en estado LIBRE.
     * @return Número de butacas libres.
     */
    // PARADIGMA: Funcional — Pipeline de Stream con evaluación perezosa
    @Override
    public long contarLibres() {
        return contarPorEstado(EstadoButaca.LIBRE);
    }

    /**
     * Cuenta las butacas actualmente en estado RESERVADO.
     * @return Número de butacas reservadas.
     */
    @Override
    public long contarReservadas() {
        return contarPorEstado(EstadoButaca.RESERVADO);
    }

    /**
     * Cuenta las butacas actualmente en estado OCUPADO.
     * @return Número de butacas ocupadas.
     */
    @Override
    public long contarOcupadas() {
        return contarPorEstado(EstadoButaca.OCUPADO);
    }

    /**
     * Devuelve la capacidad total de la sala (filas × columnas).
     * @return Total de butacas de la sala.
     */
    @Override
    public int totalButacas() {
        return sala.getFilas() * sala.getCols();
    }

    /**
     * Devuelve la referencia a la matriz interna de butacas.
     * @return Arreglo bidimensional de butacas de la sala.
     */
    @Override
    public Butaca[][] obtenerMatriz() {
        return sala.getButacas();
    }

    /**
     * Devuelve la butaca en la posición indicada para consultar su estado.
     * @param fila    Índice de fila.
     * @param columna Índice de columna.
     * @return La butaca en esa posición, o null si la posición es inválida.
     */
    @Override
    public Butaca obtenerButaca(int fila, int columna) {
        return sala.getButaca(fila, columna);
    }

    // PARADIGMA: Funcional — Método auxiliar reutilizable para contar por estado
    private long contarPorEstado(EstadoButaca estado) {
        return Arrays.stream(sala.getButacas())
                .flatMap(Arrays::stream)
                .filter(b -> b.getEstado() == estado)
                .count();
    }
}
