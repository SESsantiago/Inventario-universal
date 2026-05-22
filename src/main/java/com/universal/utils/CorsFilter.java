package com.universal.utils;

import static spark.Spark.*;

public class CorsFilter {

    public static void aplicar() {
            // "before" se ejecuta ANTES de cada petición
        before((request, response) -> {

            // Permite peticiones desde cualquier origen (en producción
            // reemplazarías "*" por tu dominio específico)
            response.header("Access-Control-Allow-Origin", "*");
            response.header("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS");
            response.header("Access-Control-Allow-Headers", "Content-Type, Authorization");
            response.type("application/json");
        });

        options("/*", (req, res) -> {
            res.status(200);
            return "OK";
        });
    }
}
