package com.universal.dao;

import com.universal.models.Producto;
import com.universal.utils.ConexionDB;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ProductoDAOImpl implements IProductoDAO {

    //* Lista todos los productos activos de un usuario específico.
    //* Equivale a: SELECT * FROM Productos WHERE usuario_id = ? AND activo = 1

    @Override
    public List<Producto> listarPorUsuario(int usuarioId) {
        List<Producto> productos = new ArrayList<>();

        String sql = """
                SELECT id, usuario_id, categoria_id, nombre, descripcion,
                       unidad, stock_actual, stock_minimo, precio_referencia,
                       activo, fecha_creacion
                FROM Productos
                WHERE usuario_id = ? AND activo = 1
                ORDER BY nombre
                """;


                try {
                    Connection con = ConexionDB.obtenerConexion();
                        // PreparedStatement previene SQL Injection y mejora rendimiento
                    PreparedStatement ps = con.prepareStatement(sql);
                        // el "1" reemplaza el primer "?"
                    ps.setInt(1, usuarioId);
                        // ResultSet es como un cursor que recorre las filas del resultado
                    ResultSet rs = ps.executeQuery();
                        // rs.next() avanza al siguiente registro — false cuando no hay más
                    while (rs.next()) {
                        // Convertimos cada fila en un objeto Producto
                        Producto p = mapearProducto(rs);
                        productos.add(p);
                    }
                } catch (SQLException e) {
                    System.err.println("Error al listar productos: " + e.getMessage());
                }

        return productos;
    }
    //* Obtiene un producto específico por su ID.

    @Override
    public Producto obtenerPorId(int id) {
        String sql = """
                SELECT id, usuario_id, categoria_id, nombre, descripcion,
                                             unidad, stock_actual, stock_minimo, precio_referencia,
                                             activo, fecha_creacion
                                      FROM Productos
                                      WHERE id = ? AND activo = 1
                """;
        try {
            Connection con = ConexionDB.obtenerConexion();
            PreparedStatement ps = con.prepareStatement(sql);
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                return mapearProducto(rs);
            }
        } catch (SQLException e) {
            System.err.println("Error al obtener producto por: " + e.getMessage());
        }

        return null; // No se encontró el producto
    }

    //* Inserta un nuevo producto en la base de datos.
    //* Devuelve true si se insertó correctamente.

    @Override
    public boolean crear(Producto p) {
        String sql = """
                    INSERT INTO Productos
                       (usuario_id, categoria_id, nombre, descripcion,
                       unidad, stock_actual, stock_minimo, precio_referencia)
                       VALUES (?, ?, ?, ?, ?, ?, ?, ?)
                """;
        try {
            Connection con = ConexionDB.obtenerConexion();
            PreparedStatement ps = con.prepareStatement(sql);

            ps.setInt(1, p.getUsuarioId());

            if (p.getCategoriaId() != null) {
                ps.setInt(2, p.getCategoriaId());
            } else {
                ps.setNull(2, Types.INTEGER);
            }

            ps.setString(3, p.getNombre());
            ps.setString(4, p.getDescripcion());
            ps.setString(5, p.getUnidad());
            ps.setBigDecimal(6, p.getStockActual());
            ps.setBigDecimal(7, p.getStockMinimo());
            ps.setBigDecimal(8, p.getPrecioReferencia());
            // executeUpdate devuelve cuántas filas fueron afectadas
            int filasAfectadas = ps.executeUpdate();
            return filasAfectadas > 0; // true si se insertó al menos una fila

        } catch (SQLException e) {
            System.err.println("Error al crear producto: " + e.getMessage());
            return false;
        }
    }

    //* Actualiza los datos de un producto existente.

    @Override
    public boolean actualizar(Producto p) {
        String sql = """
                UPDATE Productos
                SET nombre = ?, descripcion = ?, unidad = ?,
                    stock_minimo = ?, precio_referencia = ?,
                    categoria_id = ?
                WHERE id = ? AND activo = 1
                """;
        try {
            Connection con = ConexionDB.obtenerConexion();
            PreparedStatement ps = con.prepareStatement(sql);

            ps.setString(1, p.getNombre());
            ps.setString(2, p.getDescripcion());
            ps.setString(3, p.getUnidad());
            ps.setBigDecimal(4, p.getStockMinimo());
            ps.setBigDecimal(5, p.getPrecioReferencia());

            if (p.getCategoriaId() != null) {
                ps.setInt(6, p.getCategoriaId());
            } else {
                ps.setNull(6, Types.INTEGER);
            }

            ps.setInt(7, p.getId());

            int filasAfectadas = ps.executeUpdate();
            return filasAfectadas > 0;

        } catch (SQLException e) {
            System.err.println("Error al actualizar producto: " + e.getMessage());
            return false;
        }
    }

    //* Borrado lógico: marca el producto como inactivo.
    //* NUNCA borramos físicamente — conservamos el historial.

    @Override
    public boolean eliminar(int id) {
        String sql = "UPDATE Productos SET activo = 0 WHERE id = ?";

        try {
            Connection con = ConexionDB.obtenerConexion();
            PreparedStatement ps = con.prepareStatement(sql);
            ps.setInt(1, id);

            int filasAfectadas = ps.executeUpdate();
            return filasAfectadas > 0;

        } catch (SQLException e) {
            System.err.println("Error al eliminar producto: " + e.getMessage());
            return false;
        }
    }

    /**
     * Método privado auxiliar: convierte una fila del ResultSet en Producto.
     * CONCEPTO: extracción de método — evita repetir este bloque
     * en listarPorUsuario() y en obtenerPorId().
     */

    private Producto mapearProducto(ResultSet rs) throws SQLException {
        Producto p = new Producto();

        p.setId(rs.getInt("id"));
        p.setUsuarioId(rs.getInt("usuario_id"));

        // getInt devuelve 0 si el valor es NULL — verificamos con wasNull()

        int catId = rs.getInt("categoria_id");
        p.setCategoriaId(rs.wasNull() ? null : catId);

        p.setNombre(rs.getString("nombre"));
        p.setDescripcion(rs.getString("descripcion"));
        p.setUnidad(rs.getString("unidad"));
        p.setStockActual(rs.getBigDecimal("stock_actual"));
        p.setStockMinimo(rs.getBigDecimal("stock_minimo"));
        p.setPrecioReferencia(rs.getBigDecimal("precio_referencia"));
        p.setActivo(rs.getBoolean("activo"));

        // Convertimos Timestamp de SQL a LocalDateTime de Java
        Timestamp ts = rs.getTimestamp("fecha_creacion");
        if (ts != null) {
            p.setFechaCreacion(ts.toLocalDateTime());
        }

        return p;
    }
}
