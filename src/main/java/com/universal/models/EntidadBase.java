package com.universal.models;

import java.time.LocalDateTime;

public abstract class EntidadBase {
    protected int id;
    protected LocalDateTime fechaCreacion;

    public EntidadBase() {}

    public EntidadBase(int id) {
        this.id = id;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id;}

    public LocalDateTime getFechaCreacion() { return fechaCreacion; }
    public void setFechaCreacion(LocalDateTime fechaCreacion) {
        this.fechaCreacion = fechaCreacion;
    }
}
