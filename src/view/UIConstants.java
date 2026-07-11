package view;

import java.awt.Color;
import java.awt.Font;

/**
 * Define todos los colores, fuentes y dimensiones usadas en la interfaz.
 * NINGÚN otro archivo de view/ usa valores mágicos: todo se referencia aquí.
 *
 * SISTEMA DE COLOR COHERENTE:
 *   - COLOR_LIBRE / BTN_EXITO       → mismo verde  (operación positiva / butaca disponible)
 *   - COLOR_RESERVADO / BTN_ALERTA  → mismo ámbar  (estado intermedio / acción neutra)
 *   - COLOR_OCUPADO / BTN_PELIGRO   → mismo rojo   (estado bloqueante / acción destructiva)
 *   - BTN_PRIMARIO                  → violeta       (acción principal del sistema)
 *   - BTN_SECUNDARIO                → azul pizarra  (acción secundaria / cancelar)
 */
public final class UIConstants {

    // =========================================================================
    // COLORES DE ESTADO DE BUTACA  (usados en BotonButaca y PanelLeyenda)
    // =========================================================================
    public static final Color COLOR_LIBRE           = new Color(34, 197, 94);   // verde
    public static final Color COLOR_LIBRE_HOVER     = new Color(74, 222, 128);
    public static final Color COLOR_LIBRE_DARK      = new Color(20, 120, 55);   // fondo oscuro de butaca

    public static final Color COLOR_RESERVADO       = new Color(251, 191, 36);  // ámbar
    public static final Color COLOR_RESERVADO_HOVER = new Color(252, 211, 77);
    public static final Color COLOR_RESERVADO_DARK  = new Color(140, 100, 10);

    public static final Color COLOR_OCUPADO         = new Color(239, 68, 68);   // rojo
    public static final Color COLOR_OCUPADO_HOVER   = new Color(248, 113, 113);
    public static final Color COLOR_OCUPADO_DARK    = new Color(130, 25, 25);

    // =========================================================================
    // COLORES DE BOTONES DE ACCIÓN  (coherentes con los estados de butaca)
    //   BTN_EXITO   == mismo verde que COLOR_LIBRE
    //   BTN_ALERTA  == mismo ámbar que COLOR_RESERVADO
    //   BTN_PELIGRO == mismo rojo  que COLOR_OCUPADO
    // =========================================================================
    public static final Color BTN_PRIMARIO          = new Color(139, 92, 246);  // violeta
    public static final Color BTN_PRIMARIO_HOVER    = new Color(109, 60, 210);
    public static final Color BTN_SECUNDARIO        = new Color(71, 85, 105);   // pizarra
    public static final Color BTN_SECUNDARIO_HOVER  = new Color(55, 65, 81);
    public static final Color BTN_EXITO             = COLOR_LIBRE;              // verde   ← mismo que libre
    public static final Color BTN_EXITO_HOVER       = new Color(22, 163, 74);
    public static final Color BTN_ALERTA            = COLOR_RESERVADO;          // ámbar   ← mismo que reservado
    public static final Color BTN_ALERTA_HOVER      = new Color(202, 138, 4);
    public static final Color BTN_PELIGRO           = COLOR_OCUPADO;            // rojo    ← mismo que ocupado
    public static final Color BTN_PELIGRO_HOVER     = new Color(185, 28, 28);

    // =========================================================================
    // COLORES DE INTERFAZ GENERAL  (tema oscuro)
    // =========================================================================
    public static final Color BG_FONDO          = new Color(8, 8, 18);
    public static final Color BG_PANEL          = new Color(15, 15, 30);
    public static final Color BG_TARJETA        = new Color(22, 22, 42);
    public static final Color BG_TARJETA_HOVER  = new Color(30, 30, 55);
    public static final Color BG_HEADER         = new Color(12, 12, 25);
    public static final Color BG_INPUT          = new Color(18, 18, 38);
    public static final Color BORDE             = new Color(45, 45, 75);
    public static final Color BORDE_FOCUS       = new Color(139, 92, 246);
    public static final Color ACENTO            = new Color(139, 92, 246);      // violeta acento
    public static final Color ACENTO_AZUL       = new Color(59, 130, 246);
    public static final Color ACENTO_TENUE      = new Color(139, 92, 246, 40); // violeta transparente

    // =========================================================================
    // COLORES DE DASHBOARD
    // =========================================================================
    public static final Color BADGE_SALA_BG     = new Color(30, 30, 55);
    public static final Color BADGE_SALA_BORDE  = new Color(60, 60, 95);

    // =========================================================================
    // TEXTO
    // =========================================================================
    public static final Color TEXTO_PRIMARIO    = new Color(240, 242, 250);
    public static final Color TEXTO_SECUNDARIO  = new Color(148, 163, 184);
    public static final Color TEXTO_TENUE       = new Color(71, 85, 105);
    public static final Color TEXTO_ACENTO      = new Color(167, 139, 250);    // violeta claro

    // =========================================================================
    // FUENTES
    // =========================================================================
    public static final Font FUENTE_TITULO      = new Font("Segoe UI", Font.BOLD, 22);
    public static final Font FUENTE_TITULO_LG   = new Font("Segoe UI", Font.BOLD, 26);
    public static final Font FUENTE_SUBTITULO   = new Font("Segoe UI", Font.BOLD, 15);
    public static final Font FUENTE_CUERPO      = new Font("Segoe UI", Font.PLAIN, 13);
    public static final Font FUENTE_PEQUENA     = new Font("Segoe UI", Font.PLAIN, 11);
    public static final Font FUENTE_NEGRITA     = new Font("Segoe UI", Font.BOLD, 13);
    public static final Font FUENTE_BOTON       = new Font("Segoe UI", Font.BOLD, 13);
    public static final Font FUENTE_MONO        = new Font("Consolas", Font.PLAIN, 13);
    public static final Font FUENTE_STAT_NUM    = new Font("Segoe UI", Font.BOLD, 28);
    public static final Font FUENTE_STAT_LBL    = new Font("Segoe UI", Font.PLAIN, 11);
    public static final Font FUENTE_BADGE       = new Font("Segoe UI", Font.BOLD, 11);

    // =========================================================================
    // DIMENSIONES
    // =========================================================================
    public static final int TAMANO_BOTON        = 58;
    public static final int ALTO_BOTON          = 50;
    public static final int RADIO_TARJETA       = 14;
    public static final int RADIO_BOTON         = 8;

    private UIConstants() {}
}
