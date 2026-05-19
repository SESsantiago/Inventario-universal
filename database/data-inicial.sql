INSERT INTO Usuarios (nombre, email, contrasena)
VALUES
('Carlos Mendoza', 'carlos@mail.com', 'password123'),
('Laura Ríos', 'laura@mail.com', 'password123');
GO

INSERT INTO Categorias (usuario_id, nombre)
VALUES
	(1, 'Bebidas'),
	(1, 'Alimentos'),
	(1, 'Insumos'),

	(2, 'Papeleria'),
	(2, 'Electronica');
GO

INSERT INTO Productos
(usuario_id, categoria_id, nombre, unidad, stock_actual, stock_minimo, precio_referencia)
VALUES
	(1, 1, 'Cafe Molido', 'Kg', 15.00, 5.00, 25000.00),
	(1, 1, 'Jugo de Naranja',  'litro',  8.00, 3.00,   4500.00),
	(1, 2, 'Arroz Diana',      'kg',    50.00, 10.00,  2800.00),
	(1, 3, 'Bolsas Plásticas', 'paquete', 4.00, 5.00,  1200.00),
  -- Este producto YA está bajo el mínimo → debe generar alerta

  -- Productos de Laura (usuario_id=2)
	(2, 4, 'Resma Papel',      'unidad', 12.00, 3.00,  11000.00),
	(2, 5, 'Cable USB-C',      'unidad',  2.00, 5.00,   8500.00);
  -- Este también está bajo el mínimo
GO

INSERT INTO Movimientos (producto_id, tipo, cantidad, nota)
VALUES
  -- Historial del Café Molido (producto_id=1)
  (1, 'ENTRADA', 20.00, 'Compra inicial a proveedor'),
  (1, 'SALIDA',   3.00, 'Consumo interno semana 1'),
  (1, 'SALIDA',   2.00, 'Consumo interno semana 2'),

  -- Historial del Arroz (producto_id=3)
  (3, 'ENTRADA', 50.00, 'Compra inicial'),

  -- Historial de Bolsas (producto_id=4) — quedó bajo mínimo
  (4, 'ENTRADA', 10.00, 'Compra inicial'),
  (4, 'SALIDA',   6.00, 'Distribución a tienda'),

  -- Historial de Cable USB-C (producto_id=6) — también bajo mínimo
  (6, 'ENTRADA',  5.00, 'Compra inicial'),
  (6, 'SALIDA',   3.00, 'Ventas del mes');
GO

INSERT INTO Alertas (producto_id, mensaje)
VALUES
  (4, 'Stock bajo: Bolsas Plásticas tiene 4 paquetes (mínimo: 5)'),
  (6, 'Stock bajo: Cable USB-C tiene 2 unidades (mínimo: 5)');
GO

-- ============================================================
-- VERIFICACIÓN — ejecuta estas consultas para confirmar que
-- todo quedó bien insertado
-- ============================================================

-- Ver todos los productos con su estado de stock
SELECT
  u.nombre        AS usuario,
  c.nombre        AS categoria,
  p.nombre        AS producto,
  p.stock_actual,
  p.stock_minimo,
  CASE
    WHEN p.stock_actual <= p.stock_minimo THEN '⚠ BAJO MÍNIMO'
    ELSE '✓ OK'
  END             AS estado_stock
FROM Productos p
JOIN Usuarios   u ON p.usuario_id   = u.id
JOIN Categorias c ON p.categoria_id = c.id
WHERE p.activo = 1
ORDER BY u.nombre, estado_stock;

-- Ver alertas pendientes
SELECT
  p.nombre AS producto,
  a.mensaje,
  a.fecha
FROM Alertas a
JOIN Productos p ON a.producto_id = p.id
WHERE a.leida = 0;