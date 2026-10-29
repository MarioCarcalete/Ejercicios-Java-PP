# Apuntes JDBC – Ejercicios con varias consultas

## 1. Lo básico

| Qué quiero hacer | Método | Devuelve |
|---|---|---|
| `SELECT` | `executeQuery()` | `ResultSet` (las filas) |
| `UPDATE` / `INSERT` / `DELETE` | `executeUpdate()` | `int` (cuántas filas se han modificado) |

- **Usa siempre `PreparedStatement`** con `?` en vez de `Statement`. Es más seguro (evita la inyección SQL) y puedes reutilizarlo cambiando los parámetros.
- Los parámetros se numeran **desde 1**, en el orden en que aparecen los `?`:
  ```java
  PreparedStatement ps = conn.prepareStatement("UPDATE customers SET salesRepEmployeeNumber = ? WHERE customerName = ?");
  ps.setInt(1, 1056);                 // primer ?
  ps.setString(2, "Kelly's Gift Shop"); // segundo ?
  ps.executeUpdate();
  ```
- Los imports van **siempre de `java.sql`**: `Connection`, `PreparedStatement`, `ResultSet`, `SQLException`. Nunca de `com.mysql...`.

## 2. Sintaxis del UPDATE

```sql
UPDATE tabla
SET columna1 = valor1, columna2 = valor2
WHERE condicion;
```

⚠️ **Sin `WHERE` se modifican TODAS las filas.**

## 3. `if` o `while` con el ResultSet

- `rs.next()` avanza a la siguiente fila y devuelve `false` cuando ya no quedan.
- **Si esperas 1 fila** (buscas por código o por nombre único), usa `if`:
  ```java
  if (!rs.next()) {
      System.out.println("No existe");
      return;
  }
  ```
- **Si esperas varias filas**, usa `while`:
  ```java
  while (rs.next()) { ... }
  ```

## 4. Columnas que pueden ser NULL

`getInt()` devuelve `0` si la columna es NULL. Para saber si de verdad era NULL, llama a `rs.wasNull()` justo después:

```java
int emp = rs.getInt("salesRepEmployeeNumber");
boolean sinEmpleado = rs.wasNull();
```

## 5. Try-with-resources

Lo que declaras dentro de `try ( ... )` se cierra solo al terminar, aunque haya una excepción o un `return`:

```java
try (Connection conn = PoolConexiones2.getConnection();
     PreparedStatement ps = conn.prepareStatement(sql)) {
    ...
} catch (SQLException e) {
    System.out.println("Error: " + e.getMessage());
}
```

Con esto no hace falta escribir `conn.close()`.

## 6. Estructura recomendada con varias queries

### Caso A: SECUENCIAL (el más habitual)
Úsalo cuando cada query es **un paso o una comprobación**. Por ejemplo, el Ejercicio 2.

```
1. Comprobar dato 1     -> si falla, return
2. Comprobar dato 2     -> si falla, return
3. Mostrar información
4. UPDATE / INSERT / DELETE (al final, cuando todo está comprobado)
5. Mostrar el resultado
```

```java
try (Connection conn = ...;
     PreparedStatement ps1 = conn.prepareStatement(q1);
     PreparedStatement ps2 = conn.prepareStatement(q2);
     PreparedStatement ps3 = conn.prepareStatement(q3)) {

    // Paso 1
    ps1.setString(1, ...);
    try (ResultSet rs = ps1.executeQuery()) {
        if (!rs.next()) { System.out.println("Error"); return; }
        dato = rs.getInt(...);   // lo guardo en una variable
    }

    // Paso 2 (uso el dato del paso 1)
    ps2.setInt(1, dato);
    try (ResultSet rs = ps2.executeQuery()) { ... }

    // Paso 3: la acción
    ps3.setXxx(...);
    ps3.executeUpdate();
}
```

**Claves:**
- Haz **`return` en cuanto algo falle**, para no acabar con un `if` dentro de otro.
- **Guarda en variables** lo que necesites para los pasos siguientes.
- Haz **todas las comprobaciones ANTES del UPDATE**, para no modificar nada si hay un error.

### Caso B: ANIDADO
Úsalo solo cuando, **por cada fila** de la primera query, tengas que hacer otra query. Por ejemplo, el Ejercicio 1: "para cada empleado, mostrar quién le reporta".

```java
try (ResultSet rs1 = ps1.executeQuery()) {
    while (rs1.next()) {
        ps2.setInt(1, rs1.getInt("employeeNumber"));
        try (ResultSet rs2 = ps2.executeQuery()) {
            while (rs2.next()) { ... }
        }
    }
}
```

**Como máximo 2 niveles.** Si te sale un tercer nivel, seguramente algo debería ir secuencial o resolverse con un JOIN.

## 7. Tabla para decidir rápido

| Pregunta | Estructura |
|---|---|
| ¿Espero 1 fila? | `if (!rs.next())` |
| ¿Espero varias filas? | `while (rs.next())` |
| ¿La query 2 se ejecuta una vez? | Secuencial, después de la 1 |
| ¿La query 2 se ejecuta por cada fila de la 1? | Anidada dentro del `while` |
| ¿Hay comprobaciones de error? | Al principio, con `return` |
| ¿Hay UPDATE/INSERT/DELETE? | Al final |

## 8. Errores que cometí (para no repetirlos)

- Usar `executeQuery()` con un `UPDATE`: hay que usar `executeUpdate()`.
- Importar `Statement` de `com.mysql.cj.xdevapi`: el bueno es el de `java.sql`.
- Usar una variable (`resultado2`) sin haberla declarado.
- Meter `while` dentro de `while` cuando los pasos eran secuenciales.
- Usar un JOIN para comprobar si existe el cliente: el JOIN da 0 filas tanto si el cliente no existe como si no tiene empleado, así que **no se pueden distinguir los dos casos**. Hay que consultar primero la tabla `customers` sola.
- No cerrar la conexión: usa try-with-resources.

## 9. Truco para el examen

Antes de escribir código, **escribe los pasos como comentarios**:
```java
// 1. comprobar cliente
// 2. comprobar empleado
// 3. mostrar antiguo
// 4. update
// 5. mostrar nuevo
```
Luego rellena cada uno. Y **compila a menudo** para que los errores no se te acumulen.
