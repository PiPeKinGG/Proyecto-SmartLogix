-- init-databases.sql
CREATE DATABASE smartlogix_users;
CREATE DATABASE smartlogix_auth;
CREATE DATABASE smartlogix_orders;
CREATE DATABASE smartlogix_inventory;
CREATE DATABASE smartlogix_shipping;
CREATE DATABASE smartlogix_notifications;

\c smartlogix_users;

CREATE TABLE IF NOT EXISTS users (
    id BIGSERIAL PRIMARY KEY,
    email VARCHAR(255) NOT NULL UNIQUE,
    nombre VARCHAR(255) NOT NULL,
    password_hash VARCHAR(255) NOT NULL,
    pyme_id BIGINT NOT NULL,
    role VARCHAR(255) NOT NULL
);

CREATE TABLE IF NOT EXISTS users (
    id BIGSERIAL PRIMARY KEY,
    email VARCHAR(255) NOT NULL UNIQUE,
    nombre VARCHAR(255) NOT NULL,
    password_hash VARCHAR(255) NOT NULL,
    pyme_id BIGINT NOT NULL,
    role VARCHAR(255) NOT NULL,
    is_active BOOLEAN DEFAULT TRUE
);

-- La contraseña hasheada corresponde a 'password' para todos los usuarios semilla
INSERT INTO users (email, nombre, password_hash, pyme_id, role) VALUES
('danilo.celis@smartlogix.com', 'Danilo Celis', '$2b$10$iavWCa5MF6A1R/Xar3OzKurgUbsmP33OOWoZh7U9oipf2mgI/LrX6', 50, 'ADMIN'),
('felipe.quezada@smartlogix.com', 'Felipe Quezada', '$2b$10$iavWCa5MF6A1R/Xar3OzKurgUbsmP33OOWoZh7U9oipf2mgI/LrX6', 50, 'ADMIN'),
('matias.guzman@smartlogix.com', 'Matías Guzmán', '$2b$10$iavWCa5MF6A1R/Xar3OzKurgUbsmP33OOWoZh7U9oipf2mgI/LrX6', 50, 'ADMIN')
ON CONFLICT (email) DO NOTHING;


\c smartlogix_inventory;

CREATE TABLE IF NOT EXISTS products (
    id BIGSERIAL PRIMARY KEY,
    available_quantity INTEGER NOT NULL,
    name VARCHAR(255) NOT NULL,
    pyme_id BIGINT NOT NULL,
    reserved_quantity INTEGER NOT NULL,
    total_quantity INTEGER NOT NULL
);

INSERT INTO products (available_quantity, name, pyme_id, reserved_quantity, total_quantity) VALUES
(100, 'Teclado Mecánico', 50, 0, 100),
(50, 'Mouse Inalámbrico', 50, 0, 50),
(30, 'Monitor 27 Pulgadas', 50, 0, 30),
(200, 'Cable HDMI', 50, 0, 200);