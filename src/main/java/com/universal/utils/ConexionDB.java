package com.universal.utils;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ConexionDB {

    private static final String SERVIDOR = "LAPTOP-SG9R4HBL\\SQLEXPRESS";
    private static final String BASE_DE_DATOS = "InventarioUniversal";
    private static final String USUARIO = "sa";
    private static final String CONTRASENA = "Se1033178592";


    private static final String URL =
            "jdbc:sqlserver://" + SERVIDOR + ";databaseName=" + BASE_DE_DATOS
                    + ";user=" + USUARIO + ";password=" + CONTRASENA
                    + ";trustServerCertificate=true";

    private static Connection instancia = null;

    // Constructor privado — nadie puede hacer "new ConexionDB()" desde afuera
    // Esto es la clave del patrón Singleton
    private ConexionDB() {}

    /**
     * Devuelve la conexión existente, o crea una nueva si no existe.
     */
    public static Connection obtenerConexion() throws SQLException {

        // Si no hay conexión activa (o se cerró), creamos una nueva
        if (instancia == null || instancia.isClosed()) {
            instancia = DriverManager.getConnection(URL);
            System.out.println("✓ Conexión a SQL Server establecida.");
        }
        return instancia;
    }

    /**
     * Cierra la conexión limpiamente cuando se apaga el servidor.
     */
    public static void cerrarConexion() {
        try {
            if (instancia != null && !instancia.isClosed()) {
                instancia.close();
                System.out.println("✓ Conexión cerrada correctamente.");
            }
        } catch (SQLException e) {
            System.err.println("Error al cerrar la conexión: " + e.getMessage());
        }
    }
}