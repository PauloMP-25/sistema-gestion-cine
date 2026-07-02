package service;

import model.Butaca;
import model.EstadoButaca;
import model.SalaCine;
import service.interfaces.ISalaQuery;
import java.util.Arrays;

public class SalaQuery implements ISalaQuery {
    private final SalaCine manager;

    public SalaQuery(SalaCine manager) {
        this.manager = manager;
    }

    @Override
    public long contarLibres() {
        return Arrays.stream(manager.getButacas())
                .flatMap(Arrays::stream)
                .filter(b -> b.getEstado() == EstadoButaca.LIBRE)
                .count();
    }

    @Override
    public long contarReservadas() {
        return Arrays.stream(manager.getButacas())
                .flatMap(Arrays::stream)
                .filter(b -> b.getEstado() == EstadoButaca.RESERVADO)
                .count();
    }

    @Override
    public long contarOcupadas() {
        return Arrays.stream(manager.getButacas())
                .flatMap(Arrays::stream)
                .filter(b -> b.getEstado() == EstadoButaca.OCUPADO)
                .count();
    }

    @Override
    public int totalButacas() {
        return SalaCine.MAX_FILAS * SalaCine.MAX_COLS;
    }

    @Override
    public Butaca[][] obtenerMatriz() {
        return manager.getButacas();
    }

    @Override
    public Butaca obtenerButaca(int fila, int columna) {
        return manager.getButaca(fila, columna);
    }
}
