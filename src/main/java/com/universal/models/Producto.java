package com.universal.models;

import java.math.BigDecimal;

public class Producto extends EntidadBase {
    // Atrivutos propios de Producto (los que no hereda de EntidadBase)
    private int usuarioId;
    private Integer categoriaId;

    private String nombre;
    private String descripcion;
    private String unidad;

    // Usamos BigDecimal para manejar precios y cantidades con precisión, debido a que double tiene errores de precisión.
    private BigDecimal stockActual;

    private BigDecimal stockMinimo;
    private BigDecimal precioReferencia;
    private boolean activo;

    // Constructor vacío
    public Producto() {
        super(); // Llama al constructor de EntidadBase
    }

    // Constructor completo (util para crear productos nuevos)
    public Producto(int usuarioId, String nombre, String unidad, BigDecimal stockMinimo) {
        super(); // hereda el comportamiento de EntidadBase
        this.usuarioId = usuarioId;
        this.nombre = nombre;
        this.unidad = unidad;
        this.stockActual = BigDecimal.ZERO;
        this.stockMinimo = stockMinimo;
        this.activo = true;
    }

    public boolean estaBajoMinimo() {
        return stockActual.compareTo(stockMinimo) <= 0; // compareTo en BigDecimal es como <=
    }

    // Metodos de entrafa y salida de stock
    public void registrarEntrada(BigDecimal cantidad) {
        if (cantidad.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("La cantidad debe ser positiva.");
        }
        this.stockActual = this.stockActual.add(cantidad);
    }

    public void registrarSalida(BigDecimal cantidad) {
        if (cantidad.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("La cantidad debe ser positiva.");
        }
        if (cantidad.compareTo(this.stockActual) > 0) {
            throw new IllegalArgumentException("Stock insuficiente");
        }
        this.stockActual = this.stockActual.subtract(cantidad);
    }

    // Getters y Setters
    public int getUsuarioId() { return usuarioId; }
    public void setUsuarioId(int usuarioId) { this.usuarioId = usuarioId; }

    public Integer getCategoriaId() { return categoriaId; }
    public void setCategoriaId(Integer categoriaId) { this.categoriaId = categoriaId; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }

    public String getUnidad() { return unidad; }
    public void setUnidad(String unidad) { this.unidad = unidad; }

    public BigDecimal getStockActual() { return stockActual; }
    public void setStockActual(BigDecimal stockActual) { this.stockActual = stockActual; }

    public BigDecimal getStockMinimo() { return stockMinimo; }
    public void setStockMinimo(BigDecimal stockMinimo) { this.stockMinimo = stockMinimo; }

    public BigDecimal getPrecioReferencia() { return precioReferencia; }
    public void setPrecioReferencia(BigDecimal precioReferencia) {
        this.precioReferencia = precioReferencia;
    }

    public boolean isActivo() { return activo; }
    public void setActivo(boolean activo) {this.activo = activo; }

    @Override
    public String toString() {
        return "Producto{id=" + id +
                ", nombre='" + nombre + "'" +
                ", stockActual=" + stockActual +
                "/" + stockMinimo +
                ", unidad='" + unidad + "'" +
                ", bajoMinimo=" + estaBajoMinimo() + "}";
    }
}
