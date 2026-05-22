package com.universal.controllers;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonSerializer;
import com.universal.dao.ProductoDAOImpl;
import com.universal.models.Producto;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

import static spark.Spark.*;

public class ProductoController {

    // Gson convierte objetos Java ↔ JSON
    // Necesita configuración especial para LocalDateTime

    private static final Gson gson = new GsonBuilder()
            .registerTypeAdapter(LocalDateTime.class,(JsonSerializer<LocalDateTime>) (src, type, ctx) ->
                    ctx.serialize(src.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)))
            .registerTypeAdapter(LocalDateTime.class,
                    (JsonDeserializer<LocalDateTime>) (json, type, ctx) ->
                        LocalDateTime.parse(json.getAsString(),
                                DateTimeFormatter.ISO_LOCAL_DATE_TIME))
            .create();

    private static final ProductoDAOImpl productoDAO =new ProductoDAOImpl();

    public static void registrar() {

        // Lista todos los productos de un usuario
        get("/api/productos/usuario/:usuarioId", (req, res) -> {

            int usuarioId = Integer.parseInt(req.params("usuarioId"));

            List<Producto> productos = productoDAO.listarPorUsuario(usuarioId);

            Map<String, Object> respuesta = new HashMap<>();
            respuesta.put("ok", true);
            respuesta.put("total", productos.size());
            respuesta.put("datos", productos);

            res.status(200);
            return gson.toJson(respuesta);
        });

        // Obtiene un producto específico por su ID
        get("/api/productos/:id", (req, res) -> {

            int id = Integer.parseInt(req.params("id"));
            Producto producto = productoDAO.obtenerPorId(id);

            if  (producto == null) {
                res.status(404); // = No encontrado
                return gson.toJson(Map.of("ok", false, "mensaje", "Producto no encontrado"));

            }

            res.status(200);
            return gson.toJson(Map.of("ok", true, "datos", producto));
        });

        // Crea un nuevo producto
        post("/api/productos", (req, res) -> {

            Producto nuevo = gson.fromJson(req.body(), Producto.class);

            if (nuevo.getNombre() == null || nuevo.getNombre().isBlank()) {
                res.status(400); // 400 = Bad Request
                   return gson.toJson(Map.of("ok", false, "mensaje", "El nombre es obligatorio"));
            }

            boolean creado = productoDAO.crear(nuevo);

            if (creado) {
                res.status(201);
                return gson.toJson(Map.of("ok", true, "mensaje", "Producto creado correctamente"));
            } else {
                res.status(500);
                return gson.toJson(Map.of("ok", false, "mensaje", "Error al crear el producto"));
            }
        });

        // Actualiza un producto existente
        put("/api/productos/:id", (req, res) -> {

            int id = Integer.parseInt(req.params("id"));
            Producto actualizado = gson.fromJson(req.body(), Producto.class);
            actualizado.setId(id);

            boolean ok = productoDAO.actualizar(actualizado);

            if (ok) {
                res.status(200);
                return gson.toJson(Map.of("ok", true, "mensaje", "Producto actualizado"));
            } else {
                res.status(404);
                return gson.toJson(Map.of("ok", false, "mensaje", "Producto no encontrado"));
            }
        });

        // Borrado lógico — marca activo = 0
        delete("/api/productos/:id", (req, res) -> {

            int id = Integer.parseInt(req.params("id"));
            boolean ok = productoDAO.eliminar(id);

            if (ok) {
                res.status(200);
                return gson.toJson(Map.of("ok", true, "mensaje", "Producto desactivado"));
            } else {
                res.status(404);
                return gson.toJson(Map.of("ok", false, "mensaje", "Producto no encontrado"));
            }
        });

    }

}
