package app;

import javax.swing.SwingUtilities;

import model.SalaCine;
import service.SalaService;
import service.SalaQuery;
import service.interfaces.ISalaService;
import service.interfaces.ISalaQuery;
import view.DialogTamanoSala;
import view.MainFrame;

/**
 * Punto de entrada principal del sistema de gestión de butacas de cine.
 */
public class App {
    // INICIO RUTINA: Punto de entrada del sistema
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            // Paso 1: Diálogo de configuración del tamaño de sala
            DialogTamanoSala dialogo = new DialogTamanoSala();
            dialogo.setVisible(true);

            // Si el usuario canceló, salir del sistema
            if (!dialogo.isConfirmado()) {
                System.exit(0);
            }

            int filas = dialogo.getFilasSeleccionadas();
            int cols = dialogo.getColumnasSeleccionadas();

            // Paso 2: Inicializar sala con las dimensiones elegidas
            SalaCine.inicializar(filas, cols);
            SalaCine manager = SalaCine.getInstance();
            ISalaService servicio = new SalaService(manager);
            ISalaQuery consulta = new SalaQuery(manager);

            // Paso 3: Lanzar ventana principal
            MainFrame ventana = new MainFrame(servicio, consulta);
            ventana.setVisible(true);
        });
    }
    // FIN RUTINA: Punto de entrada del sistema
}
