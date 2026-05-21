package com.universal.dao;

import com.universal.models.Producto;
import java.util.List;

public interface IProductoDAO {
    List<Producto> listarPorUsuario(int usuarioId);
    Producto obtenerPorId(int id);
    boolean crear(Producto producto);
    boolean actualizar(Producto producto);
    boolean eliminar(int id);
}
