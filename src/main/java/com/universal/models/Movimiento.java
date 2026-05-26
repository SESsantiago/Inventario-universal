package com.universal.models;

import java.math.BigDecimal;

public class Movimiento extends EntidadBase {

    // Enum: define los únicos valores válidos para tipo
    // Es como el CHECK constraint de SQL, pero en Java
    public enum TipoMovimiento {
        ENTRADA, SALIDA
    }

    private int productoId;
    private TipoMovimiento tipo;
    private BigDecimal cantidad;
    private String nota;

    public Movimiento() {
        super();}

    public Movimiento(int productoId, TipoMovimiento tipo, BigDecimal cantidad, String nota) {
        super();
        this.productoId = productoId;
        this.tipo = tipo;
        this.cantidad = cantidad;
        this.nota = nota;
    }

    public int getProductoId() { return productoId; }
    public void setProductoId(int productoId) { this.productoId = productoId; }
    public TipoMovimiento getTipo() { return tipo; }
    public void setTipo(TipoMovimiento tipo) { this.tipo = tipo; }
    public BigDecimal getCantidad() { return cantidad; }
    public void setCantidad(BigDecimal cantidad) { this.cantidad = cantidad; }
    public String getNota() { return nota; }
    public void setNota(String nota) { this.nota = nota; }

    @Override
    public String toString() {
        return "Movimiento{Id=" + productoId + ", tipo=" + tipo + ", cantidad=" + cantidad + "}";
    }

}
