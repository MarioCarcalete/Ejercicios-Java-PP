# Ejercicios de Java · Acceso a Datos (2º DAM)

![Java](https://img.shields.io/badge/Java-ED8B00?style=flat&logo=openjdk&logoColor=white)
![MySQL](https://img.shields.io/badge/MySQL-4479A1?style=flat&logo=mysql&logoColor=white)
![Docker](https://img.shields.io/badge/Docker-2496ED?style=flat&logo=docker&logoColor=white)

Ejercicios de la asignatura **Acceso a Datos** del 2º curso de DAM y algún proyecto personal, organizados por temas. Todo en **Java**, sobre todo con el ejemplo de una Pokédex para ver cómo cambia el mismo dato según el formato.

## 📚 Qué se trabaja

- **Ficheros de texto**: leer y escribir línea a línea, añadir al final.
- **Ficheros binarios**: `DataOutputStream`, objetos serializables con `ObjectOutputStream` y acceso aleatorio con `RandomAccessFile`.
- **XML con DOM**: crear, leer, modificar y borrar nodos y atributos.
- **JSON con Gson**: leer y escribir objetos y listas.
- **Bases de datos relacionales con JDBC**: `PreparedStatement`, transacciones y pool de conexiones con Apache Commons DBCP sobre MySQL en Docker.
- **Migraciones** entre todos los formatos (TXT, binario, XML, JSON y base de datos).

## 🗂️ Estructura

| Carpeta | Contenido |
|---|---|
| [`Tema_Tema_paso_paso`](Tema_Tema_paso_paso) | Primeros ejercicios de ficheros, serialización, XML DOM y JSON |
| [`Acceso-Datos/Acceso_Datos_DAM`](Acceso-Datos/Acceso_Datos_DAM) | Ficheros secuenciales y de acceso aleatorio, agenda en XML, ejercicios de boletín |
| [`Acceso-Datos/Acceso_Datos_Dam2`](Acceso-Datos/Acceso_Datos_Dam2) | Más ejercicios del boletín 1 |
| [`Acceso-Datos/Acceso_datos_JSON`](Acceso-Datos/Acceso_datos_JSON) | JSON con Gson y migración de XML a JSON |
| [`Acceso-Datos/Acceso_BBDD_relacionales`](Acceso-Datos/Acceso_BBDD_relacionales) | JDBC, pool de conexiones y [apuntes de JDBC](Acceso-Datos/Acceso_BBDD_relacionales/Apuntes_JDBC.md) |
| [`Acceso-Datos/Examen_AccesoDatos`](Acceso-Datos/Examen_AccesoDatos) | ⭐ Resumen completo: un ejemplo de cada operación y las 18 migraciones entre formatos ([ver README](Acceso-Datos/Examen_AccesoDatos/README.md)) |

## ▶️ Cómo ejecutarlo

1. Clona el repo: `git clone https://github.com/MarioCarcalete/Ejercicios-Java-PP.git`
2. En Eclipse: **File → Import → Existing Projects into Workspace** y elige el proyecto que quieras.
3. Las librerías (Gson, conector MySQL, Commons DBCP) están en la carpeta `lib/` de cada proyecto.
4. Para los ejercicios de base de datos hace falta MySQL. En [`DOCKER.md`](Acceso-Datos/Examen_AccesoDatos/DOCKER.md) está explicado cómo levantarlo con Docker.

---

Hecho por [Mario Carcalete](https://github.com/MarioCarcalete) durante el 2º curso de DAM.
