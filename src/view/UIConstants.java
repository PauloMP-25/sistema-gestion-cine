package view;

import java.awt.Color;
import java.awt.Font;

/**
 * Define todos los colores, fuentes y dimensiones usadas en la interfaz
 * gráfica. Ningún otro archivo de la capa view/ debe usar valores
 * "mágicos" de color, fuente o tamaño: todos se referencian desde aquí.
 */
public final class UIConstants {

    // ---------------------------------------------------------
    // Colores de estado de butaca
    // ---------------------------------------------------------
    public static final Color COLOR_LIBRE       = new Color(34, 197, 94);
    public static final Color COLOR_LIBRE_HOVER = new Color(74, 222, 128);
    public static final Color COLOR_RESERVADO       = new Color(251, 191, 36);
    public static final Color COLOR_RESERVADO_HOVER = new Color(252, 211, 77);
    public static final Color COLOR_OCUPADO       = new Color(239, 68, 68);
    public static final Color COLOR_OCUPADO_HOVER = new Color(248, 113, 113);

    // ---------------------------------------------------------
    // Colores generales de la interfaz (tema oscuro moderno)
    // ---------------------------------------------------------
    public static final Color BG_FONDO        = new Color(10, 10, 20);
    public static final Color BG_PANEL        = new Color(18, 18, 35);
    public static final Color BG_TARJETA      = new Color(28, 28, 50);
    public static final Color BG_HEADER       = new Color(15, 15, 30);
    public static final Color BORDE           = new Color(51, 51, 80);
    public static final Color ACENTO          = new Color(139, 92, 246);
    public static final Color ACENTO_AZUL     = new Color(59, 130, 246);

    // ---------------------------------------------------------
    // Texto
    // ---------------------------------------------------------
    public static final Color TEXTO_PRIMARIO   = new Color(248, 250, 252);
    public static final Color TEXTO_SECUNDARIO = new Color(148, 163, 184);
    public static final Color TEXTO_TENUE      = new Color(71, 85, 105);

    // ---------------------------------------------------------
    // Botones de acción
    // ---------------------------------------------------------
    public static final Color BTN_PRIMARIO        = new Color(139, 92, 246);
    public static final Color BTN_PRIMARIO_HOVER  = new Color(124, 58, 237);
    public static final Color BTN_PELIGRO         = new Color(239, 68, 68);
    public static final Color BTN_PELIGRO_HOVER   = new Color(220, 38, 38);
    public static final Color BTN_EXITO           = new Color(34, 197, 94);
    public static final Color BTN_EXITO_HOVER     = new Color(22, 163, 74);

    // ---------------------------------------------------------
    // Fuentes
    // ---------------------------------------------------------
    public static final Font FUENTE_TITULO    = new Font("Segoe UI", Font.BOLD, 22);
    public static final Font FUENTE_SUBTITULO = new Font("Segoe UI", Font.BOLD, 15);
    public static final Font FUENTE_CUERPO    = new Font("Segoe UI", Font.PLAIN, 13);
    public static final Font FUENTE_PEQUENA   = new Font("Segoe UI", Font.PLAIN, 11);
    public static final Font FUENTE_NEGRITA   = new Font("Segoe UI", Font.BOLD, 13);
    public static final Font FUENTE_BOTON     = new Font("Segoe UI", Font.BOLD, 12);
    public static final Font FUENTE_MONO      = new Font("Consolas", Font.PLAIN, 13);

    // ---------------------------------------------------------
    // Dimensiones
    // ---------------------------------------------------------
    public static final int TAMANO_BOTON = 58;
    public static final int ALTO_BOTON   = 48;

    // Clase utilitaria: no instanciable
    private UIConstants() {
    }
}
