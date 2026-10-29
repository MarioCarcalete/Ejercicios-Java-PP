# Docker + MySQL + pokemondb, paso a paso

Tu configuración: contenedor `mysql`, puerto **3307** de tu Mac → 3306 del contenedor, root/abc123, usuario `profe`/abc123.

## Cada día (lo normal)
1. Abre **Docker Desktop** (si no, `docker` no responde).
2. `docker ps -a` → ¿sale `mysql`? Si su STATUS es `Exited`: `docker start mysql`.
3. `docker ps` → tiene que salir `Up ...` y `0.0.0.0:3307->3306/tcp`.

## Solo la primera vez (si no existe el contenedor)
```
docker run --name mysql -p 3307:3306 -e MYSQL_ROOT_PASSWORD=abc123 -d mysql
docker exec -it mysql mysql -uroot -pabc123 -e "CREATE USER 'profe'@'%' IDENTIFIED BY 'abc123'; GRANT ALL ON *.* TO 'profe'@'%'; FLUSH PRIVILEGES;"
```
(el PDF del profe usa `-p 3306:3306` y el usuario `alumno@172.17.0.1`; tú usas 3307 y `profe`: lo que vale es lo que tengas creado)

## Cargar pokemondb (o classicmodels, naciones...)
Desde la carpeta donde está el .sql (aquí: `datos/`):
```
docker exec -i mysql mysql -uroot -pabc123 < pokemondb.sql
```
O como en el PDF (copiar y cargar dentro):
```
docker cp pokemondb.sql mysql:/root
docker exec -it mysql bash
mysql -uroot -pabc123 < /root/pokemondb.sql
exit
```
Comprobar: `docker exec -it mysql mysql -uprofe -pabc123 -e "SELECT COUNT(*) FROM pokemondb.pokemon;"` → 151

## Workbench
Nueva conexión: host `127.0.0.1`, puerto `3307`, usuario `profe` (o root), clave `abc123`.

## Eclipse
- Jars (en `lib/`): `mysql-connector-j`, y para el pool `commons-dbcp2`, `commons-pool2`, `commons-logging`.
- URL: `jdbc:mysql://localhost:3307/pokemondb` (cambia el final por la base que uses).
- `bbdd/PoolConexiones_pokemon.java` ya lo tiene.

## Errores típicos
| Error | Causa | Arreglo |
|---|---|---|
| `Communications link failure` | Docker parado o puerto mal | `docker start mysql`; puerto 3307 en la URL |
| `Access denied for user` | usuario/clave mal o sin permisos | revisa profe/abc123; el GRANT de arriba |
| `Unknown database 'pokemondb'` | no has cargado el .sql | paso "Cargar pokemondb" |
| `Table 'pokemondb.TIPO' doesn't exist` | en Docker (Linux) las tablas distinguen MAYÚSCULAS | escribe `tipo`, no `TIPO` (el INSERT del PDF va en minúsculas) |
| `No suitable driver` | falta el conector | Build Path → Add JARs → mysql-connector-j |
| `NoClassDefFoundError: ...BasicDataSource` / `LogFactory` | faltan jars del pool | añade dbcp2, pool2 y logging |
