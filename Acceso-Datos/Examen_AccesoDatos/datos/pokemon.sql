-- Tabla para BaseDatos.java (el programa también la crea solo con CREATE TABLE IF NOT EXISTS).
-- Si "profe" no tiene permiso para crear tablas, ejecuta esto en Workbench como root.
USE classicmodels;
CREATE TABLE IF NOT EXISTS pokemon (
    nombre VARCHAR(30) PRIMARY KEY,
    tipos  VARCHAR(100),          -- "Agua,Volador"
    nivel  INT
);
