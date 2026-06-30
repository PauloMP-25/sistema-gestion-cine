package app;

import javax.swing.SwingUtilities;
import service.SalaManager;
import service.SalaService;
import service.SalaQuery;
import service.interfaces.ISalaService;
import service.interfaces.ISalaQuery;
import view.MainFrame;

/**
 * Punto de entrada principal del sistema de gestión de butacas de cine.
 * Responsabilidad del equipo QA para integración de capas.
 */
public class App {
    // INICIO RUTINA: Punto de entrada del sistema
    public static void main(String[] args) {
        // Única instanciación concreta del sistema
        SalaManager manager = SalaManager.getInstance();
        ISalaService servicio = new SalaService(manager);
        ISalaQuery consulta = new SalaQuery(manager);

        // Inyección de dependencias en el Frontend
        SwingUtilities.invokeLater(() -> {
            MainFrame ventana = new MainFrame(servicio, consulta);
            ventana.setVisible(true);
        });
    }
    // FIN RUTINA: Punto de entrada del sistema
}
