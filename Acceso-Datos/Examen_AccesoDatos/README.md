# Examen_AccesoDatos · un ejemplo de CADA cosa que entra

Todo con el mismo objeto (`Pokemon`: nombre, tipos, nivel) para que se vea que **solo cambia el formato**.
Cada clase tiene su propio `main` de prueba: botón derecho → Run As → Java Application.

| Paquete / clase | Qué tiene |
|---|---|
| `modelo/Pokemon` | La clase molde (Serializable, `List<String> tipos`, `tiposComoTexto` / `tiposDesdeTexto`) |
| `modelo/Pokedex` | La raíz del JSON (`{"pokedex": [...]}`) |
| `formatos/FicheroTexto` | TXT: leer, escribir (machaca), añadir al final (`FileWriter(ruta, true)`) |
| `formatos/FicheroBinario` | .dat de objetos: `writeObject` por cada uno, leer con `while(true)` + `EOFException` |
| `formatos/FicheroXML` | DOM: leer, escribir desde cero, crear si no existe, añadir, modificar nivel, añadir/modificar/**eliminar un tipo concreto**, eliminar pokémon, atributos |
| `formatos/FicheroJSON` | Gson: leer, escribir, crear si no existe, añadir (+ sobrecarga), modificar, añadir/modificar/**eliminar un tipo concreto**, eliminar |
| `formatos/BaseDatos` | JDBC: crear tabla, leer (SELECT), escribir (DELETE + INSERT en transacción), añadir, UPDATE, DELETE |
| `migraciones/Migraciones` | **Las 18 migraciones** (TXT, binario, XML, JSON, BBDD entre sí) y un `main` que las prueba todas |

## La idea de las migraciones
```
ORIGEN --leer--> List<Pokemon> --escribir--> DESTINO
```
Cada formato tiene `leer` y `escribir`. Migrar = el `leer` del origen + el `escribir` del destino.

## Antes de ejecutar
- Las rutas son relativas (`datos/...`): se ejecuta desde la raíz del proyecto (Eclipse lo hace solo).
- Los jars están en `lib/` (Gson y conector MySQL) y ya enlazados en `.classpath`.
- BBDD: Docker con MySQL en el **puerto 3307**, usuario `profe` / `abc123`, base `classicmodels`
  (los datos de tu `PoolConexiones_classic`). La tabla `pokemon` se crea sola; si no deja, `datos/pokemon.sql` como root.

## Subirlo a Git y abrirlo en el insti
Tu `.gitignore` ignora `.project` y `.classpath`. Para que en el insti se abra directamente, súbelos a la fuerza una vez:
```
git add Acceso-Datos/Examen_AccesoDatos
git add -f Acceso-Datos/Examen_AccesoDatos/.project Acceso-Datos/Examen_AccesoDatos/.classpath
git commit -m "Proyecto examen Acceso a Datos"
git push
```
En el insti: `git pull` → Eclipse → File → Import → General → Existing Projects into Workspace → elegir la carpeta `Examen_AccesoDatos`.
Si no subiste esos dos ficheros: File → New → Java Project, desmarca "Use default location", elige la carpeta,
y luego Build Path → Add JARs → los dos de `lib/`.

## Bases de datos (paquete `bbdd`)
Docker y cómo cargar `pokemondb.sql`: **DOCKER.md**. El .sql está en `datos/`.

| Clase | Base | Qué hace |
|---|---|---|
| `PoolConexiones_classic / _pokemon / _naciones` | | Pool (dbcp2) en el puerto 3307 |
| `Modelo1_EmpleadosCiudad` | classicmodels | empleados de una ciudad, con total (last + getRow) |
| `Modelo2_MoverEmpleados` | classicmodels | mover empleados de oficina (¡cambia la BBDD!) |
| `Modelo3_Evoluciones` | pokemondb | en qué evoluciona y de cuál viene |
| `Modelo4_IdiomasPais` / `Modelo5_HacerOficial` | naciones | ⚠ sin probar: tablas supuestas |
| `Modelo6_PokemonsTipo` | pokemondb | pokémon de un tipo con todos sus tipos (anidada) |
| `Boletin26_1/2/3` | classicmodels | reportan · cambiar empleado (wasNull) · poco stock (LEFT JOIN + COUNT) |
| `ConsultasPokemon` | pokemondb | 23 consultas de fácil a difícil (SELECT → JOIN → GROUP BY → subconsultas → INSERT/UPDATE/DELETE) |
