package com.universal.models;

public class Categoria extends EntidadBase {

    private int usuarioId;
    private String nombre;

    public Categoria() { super(); }

    public Categoria(int usuarioId, String nombre) {
        super();
        this.usuarioId = usuarioId;
        this.nombre = nombre;
    }

    public int getUsuarioId() { return usuarioId; }
    public void setUsuarioId(int usuarioId) { this.usuarioId = usuarioId; }
    public String getNombre() { return nombre; }
    public void setNombre(String nombre) {this.nombre = nombre; }

    @Override
    public String toString() {
        return "Categoria{id=" + id + ", nombre='" + nombre + "'}";
    }
}
