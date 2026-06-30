package service.interfaces;

import model.Butaca;

public interface ISalaQuery {
    long contarLibres();
    long contarReservadas();
    long contarOcupadas();
    int totalButacas();
    Butaca[][] obtenerMatriz();
    Butaca obtenerButaca(int fila, int columna);
}
