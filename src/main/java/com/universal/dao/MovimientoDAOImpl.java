package com.universal.dao;

import com.universal.models.Movimiento;
import com.universal.models.Movimiento.TipoMovimiento;
import com.universal.utils.ConexionDB;

import java.math.BigDecimal;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class MovimientoDAOImpl {

    public boolean registrar(Movimiento movimiento){

        Connection con = null;

        try {
            con = ConexionDB.obtenerConexion();
                // Desactivamos el autocommit — tomamos control manual
                // Por defecto cada SQL se confirma solo. Con false,
                // nada se confirma hasta que llamemos commit()
            con.setAutoCommit(false);

            // Insertar el movimiento
            String sqlMovimiento = """
                    INSERT INTO Movimientos (producto_id, tipo, cantidad, nota)
                    VALUES (?, ?, ?, ?)
                    """;

            PreparedStatement psMovimiento = con.prepareStatement(sqlMovimiento);
            psMovimiento.setInt(1, movimiento.getProductoId());
            psMovimiento.setString(2, movimiento.getTipo().name());
            // .name() convierte el enum ENTRADA → "ENTRADA" (String)
            psMovimiento.setBigDecimal(3, movimiento.getCantidad());
            psMovimiento.setString(4, movimiento.getNota());
            psMovimiento.executeUpdate();

            // Actualizar el stock del producto
            String sqlStock;
            if (movimiento.getTipo() == TipoMovimiento.ENTRADA) {
                sqlStock = "UPDATE Productos SET stock_actual = stock_actual + ? WHERE id = ?";
            } else {
                sqlStock = "UPDATE Productos SET stock_actual = stock_actual - ? WHERE id = ?";
            }

            PreparedStatement psStock = con.prepareStatement(sqlStock);
            psStock.setBigDecimal(1, movimiento.getCantidad());
            psStock.setInt(2, movimiento.getProductoId());
            psStock.executeUpdate();

            // Verficar si el stock quedo bajo minimo
            String sqlVerificar = """
                    SELECT stock_actual, stock_minimo, nombre
                    FROM Productos
                    WHERE id = ?
                    """;

            PreparedStatement psVerificar = con.prepareStatement(sqlVerificar);
            psVerificar.setInt(1, movimiento.getProductoId());
            ResultSet rs = psVerificar.executeQuery();

            if (rs.next()) {
                BigDecimal stockActual = rs.getBigDecimal("stock_actual");
                BigDecimal stockMinimo = rs.getBigDecimal("stock_minimo");
                String nombreProducto = rs.getString("nombre");

                if (stockActual.compareTo(stockMinimo) <= 0) {
                    String sqlAlerta = """
                            INSERT INTO Alertas (producto_id, mensaje)
                            VALUES (?, ?)
                            """;
                    String mensaje = String.format(
                            "Stock bajo: %s tiene %.2f %s (minimo: %.2f)",
                            nombreProducto, stockActual, "unidades", stockMinimo
                    );

                    PreparedStatement psAlerta = con.prepareStatement(sqlAlerta);
                    psAlerta.setInt(1, movimiento.getProductoId());
                    psAlerta.setString(2, mensaje);
                    psAlerta.executeUpdate();
                }
            }
            // confirmamos los 3 pasos juntos
            con.commit();
            return true;

        } catch (SQLException e) {
            // Algo falló: revertimos TODO — como si nada hubiera pasado
            System.err.println("Error al registrar movimiento: " + e.getMessage());
            try {
                if (con != null) con.rollback();
                // rollback = "deshacer todo desde el último commit"
            } catch (SQLException ex) {
                System.err.println("Error al hacer rollback: " + ex.getMessage());
            }
            return false;

        } finally {
            // finally siempre se ejecuta — restauramos el autocommit
            try {
                if (con != null) con.setAutoCommit(true);
            } catch (SQLException e) {
                System.err.println("Error al restaurar autocommit: " + e.getMessage());
            }
        }
    }

    //Lista el historial de movimientos de un producto.

    public List<Movimiento> listarPorProducto(int productoId) {

        List<Movimiento> lista = new ArrayList<>();
        String sql = """
                SELECT id, producto_id, tipo, cantidad, nota, fecha
                FROM Movimientos
                WHERE producto_id = ?
                ORDER BY fecha DESC
                """;

        try {
            Connection con = ConexionDB.obtenerConexion();
            PreparedStatement ps = con.prepareStatement(sql);
            ps.setInt(1, productoId);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                Movimiento m = new Movimiento();
                m.setId(rs.getInt("id"));
                m.setProductoId(rs.getInt("producto_id"));
                m.setTipo(TipoMovimiento.valueOf(rs.getString("tipo")));
                // valueOf convierte "ENTRADA" (String) → TipoMovimiento.ENTRADA (enum)
                m.setCantidad(rs.getBigDecimal("cantidad"));
                m.setNota(rs.getString("nota"));
                Timestamp ts = rs.getTimestamp("fecha");
                if (ts != null) m.setFechaCreacion(ts.toLocalDateTime());
                lista.add(m);
            }

        }  catch (SQLException e) {
            System.err.println("Error al listar movimientos: " + e.getMessage());
        }
        return lista;
    }
}
