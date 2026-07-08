package model;

public enum Rol {
    ADMIN("Administrador"),
    CAJERO("Cajero");

    private final String nombre;

    Rol(String nombre) { this.nombre = nombre; }

    public String getNombre() { return nombre; }
}
