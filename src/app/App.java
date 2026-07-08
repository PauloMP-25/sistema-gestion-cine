package app;

import model.Rol;
import model.SalaCine;
import model.SalaFactory;
import service.GestorSalas;
import service.SalaQuery;
import service.SalaService;
import service.interfaces.ISalaQuery;
import service.interfaces.ISalaService;
import service.interfaces.ISalaQuery;
import service.interfaces.ISalaService;
import view.LoginFrame;
import view.MainFrame;

import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import java.awt.Color;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public class App {

    public static void main(String[] args) {
        GestorSalas gestorSalas = new GestorSalas();
        cargarSalasDemo(gestorSalas);
        SwingUtilities.invokeLater(() -> iniciarSistema(gestorSalas));
    }

    // Salas disponibles por defecto para que el cajero pueda operar
    // sin necesidad de que el admin haya creado salas en la misma sesión.
    private static void cargarSalasDemo(GestorSalas gestorSalas) {
        gestorSalas.agregarSala(SalaFactory.crearSala("Sala 1 — Principal",  5 *  8,  5,  8));
        gestorSalas.agregarSala(SalaFactory.crearSala("Sala 2 — VIP",        4 *  6,  4,  6));
        gestorSalas.agregarSala(SalaFactory.crearSala("Sala 3 — Norte",      8 * 10,  8, 10));
    }

    private static void configurarUI() {
        Color bg    = new Color(22, 20, 55);
        Color texto = new Color(210, 210, 235);
        Color boton = new Color(51, 65, 105);
        UIManager.put("OptionPane.background",        bg);
        UIManager.put("Panel.background",             bg);
        UIManager.put("OptionPane.messageForeground", texto);
        UIManager.put("Button.background",            boton);
        UIManager.put("Button.foreground",            texto);
    }

    private static void iniciarSistema(GestorSalas gestorSalas) {
        configurarUI();

        LoginFrame login = new LoginFrame();
        login.setVisible(true);
        if (!login.isLoginExitoso()) System.exit(0);

        Rol rol = "admin".equals(login.getUsuarioActual()) ? Rol.ADMIN : Rol.CAJERO;

        abrirMainFrame(gestorSalas, rol);
    }

    private static void abrirMainFrame(GestorSalas gestorSalas, Rol rol) {
        MainFrame ventana = new MainFrame(gestorSalas, rol);

        ventana.addWindowListener(new WindowAdapter() {
            @Override public void windowClosed(WindowEvent e) {
                if (ventana.isLogout()) {
                    SwingUtilities.invokeLater(() -> iniciarSistema(gestorSalas));
                }
            }
        });

        ventana.setVisible(true);
    }
}
