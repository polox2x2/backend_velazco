-- Insertar roles - ACTUALIZADOS para coincidir con frontend
INSERT INTO roles (id, nombre) VALUES (1, 'Administrador');
INSERT INTO roles (id, nombre) VALUES (2, 'Cajero');
INSERT INTO roles (id, nombre) VALUES (3, 'Vendedor');
INSERT INTO roles (id, nombre) VALUES (4, 'Producción');
INSERT INTO roles (id, nombre) VALUES (5, 'Entregas');

-- Insertar usuarios - ACTUALIZADOS
INSERT INTO usuarios (id, nombre, correo, password, activo, rol_id) VALUES (1, 'Admin Principal', 'admin@test.com', '$2a$10$0KGE33tFXl3AWBzNSfRdX.BRUlrNlBfc/Idl/Sa0a.p8Pakfp7WNa', true, 1);
INSERT INTO usuarios (id, nombre, correo, password, activo, rol_id) VALUES (2, 'María Cajero', 'cajero@test.com', '$2a$10$0KGE33tFXl3AWBzNSfRdX.BRUlrNlBfc/Idl/Sa0a.p8Pakfp7WNa', true, 2);
INSERT INTO usuarios (id, nombre, correo, password, activo, rol_id) VALUES (3, 'Carlos Vendedor', 'vendedor@test.com', '$2a$10$0KGE33tFXl3AWBzNSfRdX.BRUlrNlBfc/Idl/Sa0a.p8Pakfp7WNa', true, 3);
INSERT INTO usuarios (id, nombre, correo, password, activo, rol_id) VALUES (4, 'Laura Producción', 'produccion@test.com', '$2a$10$0KGE33tFXl3AWBzNSfRdX.BRUlrNlBfc/Idl/Sa0a.p8Pakfp7WNa', true, 4);
INSERT INTO usuarios (id, nombre, correo, password, activo, rol_id) VALUES (5, 'Luis Entregas', 'entregas@test.com', '$2a$10$0KGE33tFXl3AWBzNSfRdX.BRUlrNlBfc/Idl/Sa0a.p8Pakfp7WNa', true, 5);

-- Insertar categorías de productos
INSERT INTO categorias (id, nombre, descripcion) VALUES
(1, 'Panadería', 'Productos de panadería fresca'),
(2, 'Pastelería', 'Pasteles y postres'),
(3, 'Bebidas', 'Bebidas calientes y frías'),
(4, 'Salados', 'Productos salados y snacks');

-- Insertar productos más realistas para panadería
INSERT INTO productos (id, nombre, precio, stock, activo, categoria_id, imagen) VALUES
(1, 'Pan Francés', 2.50, 50, true, 1, 'pan_frances.jpg'),
(2, 'Croissant', 3.00, 30, true, 1, 'croissant.jpg'),
(3, 'Bagette', 4.00, 25, true, 1, 'baguette.jpg'),
(4, 'Pastel de Chocolate', 25.00, 8, true, 2, 'pastel_chocolate.jpg'),
(5, 'Tarta de Manzana', 20.00, 6, true, 2, 'tarta_manzana.jpg'),
(6, 'Café Americano', 5.00, 100, true, 3, 'cafe_americano.jpg'),
(7, 'Capuchino', 7.50, 80, true, 3, 'capuchino.jpg'),
(8, 'Té Verde', 4.50, 60, true, 3, 'te_verde.jpg'),
(9, 'Empanada de Carne', 8.00, 15, true, 4, 'empanada_carne.jpg'),
(10, 'Sandwich Club', 12.00, 10, true, 4, 'sandwich_club.jpg'),
(11, 'Pan Integral', 3.50, 5, true, 1, 'pan_integral.jpg'),  -- Stock bajo
(12, 'Donas', 2.00, 3, true, 2, 'donas.jpg');  -- Stock bajo

-- Insertar órdenes de producción
INSERT INTO produccion (id, fecha_produccion, estado, asignado_por, asignado_a, comentarios) VALUES
(1, CURRENT_DATE, 'PENDIENTE', 1, 4, 'Preparar 50 unidades de Pan Francés y 30 Croissants para mañana'),
(2, CURRENT_DATE, 'EN_PROCESO', 1, 4, 'Producción de pasteles para pedidos del fin de semana'),
(3, CURRENT_DATE + 1, 'PENDIENTE', 1, 4, 'Preparar baguettes y pan integral');

-- Insertar detalles de producción
INSERT INTO detalle_produccion (produccion_id, producto_id, cantidad_solicitada, cantidad_producida, comentarios) VALUES
(1, 1, 50, 0, NULL),   -- Pan Francés
(1, 2, 30, 0, NULL),   -- Croissant
(2, 4, 5, 3, NULL),       -- Pastel de Chocolate
(2, 5, 4, 4, NULL),       -- Tarta de Manzana
(3, 3, 20, 0, NULL),   -- Baguette
(3, 11, 15, 0, NULL);  -- Pan Integral

-- Insertar pedidos de ejemplo
INSERT INTO pedidos (id, fecha_pedido, estado, nombre_cliente, usuario_atencion_id) VALUES
(1, CURRENT_TIMESTAMP, 'PENDIENTE', 'Juan Pérez', 3),
(2, CURRENT_TIMESTAMP, 'PAGADO', 'María García', 3),
(3, CURRENT_TIMESTAMP, 'PENDIENTE', 'Carlos López', 3),
(4, CURRENT_TIMESTAMP, 'ENTREGADO', 'Ana Martínez', 3),
(5, CURRENT_TIMESTAMP, 'ENTREGADO', 'Pedro Rodríguez', 3);

-- Insertar detalles de pedidos
INSERT INTO detalle_pedidos (pedido_id, producto_id, cantidad, precio_unitario) VALUES
(1, 1, 5, 2.50),   -- 5 Pan Francés
(1, 6, 2, 5.00),   -- 2 Café Americano
(1, 9, 3, 8.00),   -- 3 Empanadas
(2, 2, 4, 3.00),   -- 4 Croissants
(2, 7, 2, 7.50),   -- 2 Capuchinos
(2, 10, 1, 12.00), -- 1 Sandwich Club
(3, 4, 1, 25.00),  -- 1 Pastel de Chocolate
(3, 5, 1, 20.00),  -- 1 Tarta de Manzana
(3, 7, 3, 7.50),   -- 3 Capuchinos
(4, 1, 3, 2.50),  -- 3 Pan Francés
(4, 8, 2, 4.50),  -- 2 Té Verde
(4, 2, 2, 3.00),  -- 2 Croissants
(5, 6, 3, 5.00);  -- 3 Café Americano

-- Insertar ventas (para reportes)
INSERT INTO ventas (id, pedido_id, cajero_id, metodo_pago, fecha_venta, monto_total) VALUES
(1, 5, 2, 'EFECTIVO', CURRENT_TIMESTAMP, 15.00),
(2, 4, 2, 'TARJETA', CURRENT_TIMESTAMP, 28.75);

-- Insertar entregas
INSERT INTO entregas (id, pedido_id, usuario_entrega_id, fecha_entrega) VALUES
(1, 5, 5, CURRENT_TIMESTAMP),
(2, 4, 5, CURRENT_TIMESTAMP);