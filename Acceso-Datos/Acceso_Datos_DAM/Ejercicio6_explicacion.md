# Ejercicio 6 – Cortafuegos básico (explicación)

## La idea general

Te dan una lista de IPs en un fichero:

```
192.168.1.1
10.0.0.5
192.168.1.1
172.16.15.15
192.168.1.1
10.0.0.5
192.168.1.1
```

Una persona a mano haría tres cosas:

1. **Contar** cuántas veces sale cada IP.
2. **Separarlas**: las que salen 2 veces o menos están bien; las que salen más, bloqueadas.
3. **Escribir** el resultado.

El programa hace exactamente eso, y cada parte está en un sitio:

| Parte | Dónde está | Qué hace |
|---|---|---|
| 0. Pedir el fichero | `main` | Pregunta el nombre con `Scanner` |
| 1. Contar | `leerFichero` | Lee línea a línea y cuenta |
| 2 y 3. Separar y mostrar | `mostrarResultados` | Reparte y escribe |

---

## Parte 0: pedir el fichero (`main`)

```java
Scanner sc = new Scanner(System.in);
System.out.print("Introduce el nombre del fichero: ");
String nombreFichero = sc.nextLine().trim();
sc.close();

Map<String, Integer> contador = leerFichero(nombreFichero);

if (contador != null) {
    mostrarResultados(contador);
}
```

- Se pide el nombre del fichero por teclado.
- Se llama a `leerFichero`, que devuelve el mapa con los recuentos.
- Si devuelve `null` es que algo fue mal (no existe o está vacío), así que no seguimos.

---

## Parte 1: contar (`leerFichero`)

Imagina el mapa como una **libreta con dos columnas**: IP y veces.

El programa lee línea por línea y en cada una se pregunta: *¿esta IP ya está en la libreta?*

- **No** → la apunta con un 1.
- **Sí** → mira cuántas llevaba y le suma 1.

```java
String ip = linea.trim();

if (contadorRepeticiones.containsKey(ip)) {
    int valor = contadorRepeticiones.get(ip);   // cuántas lleva
    contadorRepeticiones.put(ip, valor + 1);    // guardo una más
} else {
    contadorRepeticiones.put(ip, 1);            // primera vez
}
```

Con el ejemplo, la libreta va cambiando así:

| Línea leída | ¿Estaba? | Libreta después |
|---|---|---|
| 192.168.1.1 | No | 192.168.1.1 = 1 |
| 10.0.0.5 | No | …, 10.0.0.5 = 1 |
| 192.168.1.1 | Sí | 192.168.1.1 = **2** |
| 172.16.15.15 | No | …, 172.16.15.15 = 1 |
| 192.168.1.1 | Sí | 192.168.1.1 = **3** |
| 10.0.0.5 | Sí | 10.0.0.5 = **2** |
| 192.168.1.1 | Sí | 192.168.1.1 = **4** |

Al terminar, la libreta queda:

```
{10.0.0.5=2, 172.16.15.15=1, 192.168.1.1=4}
```

### Comprobaciones que pide el enunciado

- Si el fichero **no existe** → avisa y devuelve `null`.
- Si al acabar la libreta está **vacía** → avisa y devuelve `null`.

Devolver `null` significa "algo ha ido mal, no sigas". Por eso en el `main` está el `if (contador != null)`.

### Detalles importantes

- **`trim()`** quita espacios al principio y al final. Sin él, `"10.0.0.5 "` y `"10.0.0.5"` serían IPs distintas.
- **`put` con una clave que ya existe** sobrescribe el valor; no duplica la clave.
- **try-with-resources** (`try (BufferedReader lc = ...)`) cierra el fichero solo al terminar.

---

## Parte 2: separar (`mostrarResultados`)

Con la libreta completa, preparamos **dos cajas**:

- `permitidas` → una **lista**, porque solo hace falta guardar la IP.
- `bloqueadas` → un **mapa**, porque además de la IP hay que guardar los accesos de más.

Recorremos la libreta fila a fila:

```java
for (Map.Entry<String, Integer> entrada : contador.entrySet()) {
    String ip = entrada.getKey();     // la IP
    int veces = entrada.getValue();   // cuántas veces salió

    if (veces <= MAX_ACCESOS) {
        permitidas.add(ip);
    } else {
        bloqueadas.put(ip, veces - MAX_ACCESOS);
    }
}
```

- `entrySet()` → todas las filas de la libreta.
- `getKey()` → la IP.
- `getValue()` → las veces.

| IP | Veces | ¿≤ 2? | Va a… |
|---|---|---|---|
| 10.0.0.5 | 2 | Sí | permitidas |
| 172.16.15.15 | 1 | Sí | permitidas |
| 192.168.1.1 | 4 | No | bloqueadas, con 4 − 2 = **2** de más |

`MAX_ACCESOS` es una constante que vale 2. Así no hay un "2" suelto por el código y se cambia en un solo sitio.

---

## Parte 3: mostrar

```java
if (!permitidas.isEmpty()) {
    System.out.println("IP con acceso permitido:");
    for (String ip : permitidas) {
        System.out.println("- " + ip);
    }
}

if (!bloqueadas.isEmpty()) {
    System.out.println("IPs con acceso restringido:");
    for (Map.Entry<String, Integer> entrada : bloqueadas.entrySet()) {
        System.out.println("- " + entrada.getKey()
                + ". Accesos superiores a lo permitido: " + entrada.getValue());
    }
}
```

El `if (!caja.isEmpty())` hace que, si una caja está vacía, su título ni siquiera aparezca (lo pide el enunciado).

### Salida esperada

```
IP con acceso permitido:
- 10.0.0.5
- 172.16.15.15
IPs con acceso restringido:
- 192.168.1.1. Accesos superiores a lo permitido: 2
```

---

## ¿Por qué sale ordenado?

Porque usamos **`TreeMap`**, que guarda las claves siempre ordenadas. Al recorrerlo, las IPs salen en orden y se meten en las cajas en ese mismo orden.

> Ojo: ordena como texto (alfabéticamente), no numéricamente. Para este ejercicio es suficiente.

---

## Errores típicos (los que fuimos corrigiendo)

| Error | Por qué falla |
|---|---|
| Crear el mapa **dentro** del `while` | Se crea vacío en cada línea y no acumula nada |
| `containsKey(fichero)` en vez de `containsKey(ip)` | Busca un `File`, no la IP; siempre da `false` |
| `put(linea, +1)` | `+1` es solo el número 1; no suma nada |
| Hacer `trim()` **fuera** del bucle | Solo se aplica una vez, no a cada línea |
| Clasificar **dentro** del `while` | Los contadores aún no son definitivos |
| `"texto" + get(ip) - 2` | Java une texto y número primero y luego no puede restar a un `String`; hay que poner paréntesis |

---

## Consejo para Eclipse

Si te dice que el fichero no existe, escribe solo `IP.txt` y comprueba que está en la **raíz del proyecto** (al mismo nivel que `src`, no dentro).
