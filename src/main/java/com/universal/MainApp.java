package com.universal;

import com.universal.utils.ConexionDB;
import java.sql.Connection;
import java.sql.SQLException;

public class MainApp {

    public static void main(String[] args) {

        System.out.println("Iniciando Sistema de Inventario Universal...");

        try {

            Connection conexion = ConexionDB.obtenerConexion();

            System.out.println("¡Conexión exitosa! Base de datos lista.");
            System.out.println("Catálogo: " + conexion.getCatalog());

        } catch (SQLException e) {
            // Si algo falla, mostramos el error completo para diagnosticarlo
            System.err.println("Error de conexión: " + e.getMessage());
        }
    }
}