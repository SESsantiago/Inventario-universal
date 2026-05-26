package com.universal;

import com.universal.controllers.ProductoController;
import com.universal.dao.MovimientoDAOImpl;
import com.universal.dao.ProductoDAOImpl;
import com.universal.models.Movimiento;
import com.universal.models.Producto;
import com.universal.utils.ConexionDB;
import com.universal.utils.CorsFilter;

import java.math.BigDecimal;
import java.sql.SQLException;
import static spark.Spark.*;

public class MainApp {

    public static void main(String[] args) {

        // Puerto donde escuchará el servidor
        // Tu API estará en: http://localhost:4567
        port(4567);

        // Verificamos la conexión a la BD antes de arrancar
        try {
            ConexionDB.obtenerConexion();
            System.out.println("✓ Base de datos conectada.");
        } catch (SQLException e) {
            System.err.println("✗ Error de BD: " + e.getMessage());
            System.exit(1); // detenemos todo si no hay BD
        }

        // Aplicamos CORS para que el frontend pueda llamar a la API
        CorsFilter.aplicar();

        // Registramos los endpoints de cada controlador
        ProductoController.registrar();

        System.out.println("✓ Servidor corriendo en http://localhost:4567");
        System.out.println("  Prueba: http://localhost:4567/api/productos/usuario/1");
    }
}