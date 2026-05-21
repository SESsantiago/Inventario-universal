package com.universal;

import com.universal.dao.ProductoDAOImpl;
import com.universal.models.Producto;
import com.universal.utils.ConexionDB;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

public class MainApp {

    public static void main(String[] args) {

        System.out.println("Iniciando Sistema de Inventario Universal...");

        try {

            Connection conexion = ConexionDB.obtenerConexion();

            System.out.println("¡Conexión exitosa! Base de datos lista.");
            System.out.println("Catálogo: " + conexion.getCatalog());

            ProductoDAOImpl productoDAO = new ProductoDAOImpl();

            List<Producto> productos = productoDAO.listarPorUsuario(1);

            System.out.println("\n--- Productos de Carlos ---");
            for (Producto p : productos) {
                System.out.println(p);  // usa el toString() que definimos
                if (p.estaBajoMinimo()) {
                    System.out.println("  ⚠ ALERTA: stock bajo mínimo");
                }
            }

        } catch (SQLException e) {
            // Si algo falla, mostramos el error completo para diagnosticarlo
            System.err.println("Error de conexión: " + e.getMessage());
        }
    }
}