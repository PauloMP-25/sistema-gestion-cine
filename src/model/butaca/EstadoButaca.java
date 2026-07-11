package model.butaca;

/**
 * Define los tres estados posibles de una butaca dentro de la sala.
 * Usa tipado fuerte para evitar valores inválidos en tiempo de compilación.
 */
// PARADIGMA: Orientado a Objetos — Encapsulamiento de dominio cerrado mediante enum
public enum EstadoButaca {
    LIBRE,
    RESERVADO,
    OCUPADO
}
