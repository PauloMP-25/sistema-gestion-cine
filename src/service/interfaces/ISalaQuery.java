package service.interfaces;

import model.butaca.Butaca;

/**
 * Contrato de consultas de solo lectura sobre una sala de cine.
 * Ningún método definido aquí puede modificar el estado de las butacas.
 * Separado de ISalaService para aplicar CQRS y el principio de Segregación de Interfaces.
 */
// PARADIGMA: Orientado a Objetos — Interfaz de consulta (SOLID: ISP, CQRS implícito)
public interface ISalaQuery {

    /**
     * Cuenta las butacas actualmente en estado LIBRE.
     * @return Número de butacas libres.
     */
    long contarLibres();

    /**
     * Cuenta las butacas actualmente en estado RESERVADO.
     * @return Número de butacas reservadas.
     */
    long contarReservadas();

    /**
     * Cuenta las butacas actualmente en estado OCUPADO.
     * @return Número de butacas ocupadas.
     */
    long contarOcupadas();

    /**
     * Devuelve la capacidad total de la sala (filas × columnas).
     * @return Total de butacas de la sala.
     */
    int totalButacas();

    /**
     * Devuelve la referencia a la matriz interna de butacas.
     * Solo debe usarse para lectura; modificar la matriz externamente rompe la arquitectura.
     * @return Arreglo bidimensional de butacas de la sala.
     */
    Butaca[][] obtenerMatriz();

    /**
     * Devuelve la butaca en la posición indicada para consulta de su estado.
     * @param fila    Índice de fila.
     * @param columna Índice de columna.
     * @return La butaca en esa posición, o null si la posición es inválida.
     */
    Butaca obtenerButaca(int fila, int columna);
}
