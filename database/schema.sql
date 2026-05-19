--CREATE DATABASE InventarioUniversal;
--GO
----USE InventarioUniversal;
----GO

CREATE TABLE Usuarios (
id INT IDENTITY(1,1) PRIMARY KEY,
    -- IDENTITY(1,1) = el id empieza en 1 y se incrementa de 1 en 1 automáticamente
nombre NVARCHAR(100) NOT NULL,
   -- NVARCHAR admite caracteres especiales (tildes, ñ, etc.)
   -- NOT NULL = este campo es obligatorio
email NVARCHAR(150) NOT NULL UNIQUE,
   -- UNIQUE = no pueden existir dos usuarios con el mismo email
contrasena NVARCHAR(255) NOT NULL,
fecha_registro DATETIME DEFAULT GETDATE()
   -- DEFAULT GETDATE() = si no se especifica, usa la fecha/hora actual
);
GO

CREATE TABLE Categorias (
id INT IDENTITY(1,1) PRIMARY KEY,
usuario_id INT NOT NULL,
nombre NVARCHAR(100) NOT NULL,

CONSTRAINT FK_Categorias_Usuarios
FOREIGN KEY (usuario_id) REFERENCES Usuarios(id)
ON DELETE CASCADE 
    -- ON DELETE CASCADE: si se borra un usuario, se borran sus categorías también
);
GO

CREATE TABLE Productos (
    id                INT IDENTITY(1,1) PRIMARY KEY,
    usuario_id        INT NOT NULL,
    categoria_id      INT,
    nombre            NVARCHAR(200) NOT NULL,
    descripcion       NVARCHAR(500),
    unidad            NVARCHAR(50) NOT NULL DEFAULT 'unidad',
    stock_actual      DECIMAL(10,2) NOT NULL DEFAULT 0,
    stock_minimo      DECIMAL(10,2) NOT NULL DEFAULT 0,
    precio_referencia DECIMAL(10,2),
    activo            BIT NOT NULL DEFAULT 1,
    fecha_creacion    DATETIME DEFAULT GETDATE(),

    CONSTRAINT FK_Productos_Usuarios
        FOREIGN KEY (usuario_id) REFERENCES Usuarios(id)
        ON DELETE CASCADE,

    CONSTRAINT FK_Productos_Categorias
        FOREIGN KEY (categoria_id) REFERENCES Categorias(id)
        ON DELETE NO ACTION   -- ? el único cambio
);
GO

CREATE TABLE Movimientos (
id INT IDENTITY(1,1) PRIMARY KEY,
producto_id INT NOT NULL,
tipo NVARCHAR(10) NOT NULL,
    -- Solo dos valores válidos: 'ENTRADA' o 'SALIDA'
    -- Lo validamos con un CHECK
cantidad DECIMAL(10,2) NOT NULL,
nota NVARCHAR(500),
fecha DATETIME DEFAULT GETDATE(),

CONSTRAINT FK_Movimientos_Productos
FOREIGN KEY (producto_id) REFERENCES Productos(id),

CONSTRAINT CK_Tipo_Movimiento
CHECK (tipo IN ('ENTRADA', 'SALIDA')),
    -- CHECK garantiza que nadie escriba 'entrada' en minúsculas
    -- o un valor inválido como 'COMPRA'

CONSTRAINT CK_Cantidad_Positiva
CHECK (cantidad > 0)
    -- La cantidad siempre debe ser mayor que cero
);
GO

CREATE TABLE Alertas (
id INT IDENTITY(1,1) PRIMARY KEY,
producto_id INT NOT NULL,
mensaje NVARCHAR(300) NOT NULL,
leida BIT NOT NULL DEFAULT 0,
fecha DATETIME DEFAULT GETDATE(),

CONSTRAINT FK_Alertas_Productos
FOREIGN KEY (producto_id) REFERENCES Productos(id)
ON DELETE CASCADE
);
GO

CREATE INDEX IX_Produtos_UsuarioId  ON Productos(usuario_id);
CREATE INDEX IX_Movimientos_ProductoId  ON Movimientos(producto_id);
CREATE INDEX IX_Alertas_ProductoId  ON Alertas(producto_id);
