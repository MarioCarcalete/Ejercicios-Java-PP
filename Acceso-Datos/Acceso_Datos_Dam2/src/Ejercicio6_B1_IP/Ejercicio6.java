package Ejercicio6_B1_IP;

// Importaciones necesarias para leer ficheros
import java.io.BufferedReader;   // Lee texto línea a línea de forma eficiente (usa un buffer)
import java.io.File;             // Representa un fichero o ruta del sistema
import java.io.FileReader;       // Abre un fichero para leer caracteres
import java.io.IOException;      // Excepción que se lanza si falla la lectura

// Importaciones de colecciones
import java.util.ArrayList;      // Lista dinámica (implementación de List)
import java.util.List;           // Interfaz de lista
import java.util.Map;            // Interfaz de mapa (clave -> valor)
import java.util.Scanner;        // Para leer datos por teclado
import java.util.TreeMap;        // Mapa que mantiene las claves ordenadas

/**
 * Ejercicio 6 - Cortafuegos básico.
 *
 * Lee un fichero con un historial de IPs (una por línea) y:
 *  - Cuenta cuántas veces aparece cada IP.
 *  - Si una IP aparece 2 veces o menos, tiene acceso permitido.
 *  - Si aparece más de 2 veces, se considera ataque por fuerza bruta y se bloquea.
 *    Además se muestran los accesos que hizo por encima de lo permitido.
 */
public class Ejercicio6 {

    // Número máximo de accesos permitidos antes de bloquear una IP.
    // Lo ponemos como constante para no tener un "2" suelto por el código
    // y poder cambiarlo fácilmente en un solo sitio.
    static final int MAX_ACCESOS = 2;

    public static void main(String[] args) {
        // Creamos el Scanner para leer lo que escribe el usuario por teclado
        Scanner sc = new Scanner(System.in);

        // Pedimos el nombre del fichero (por ejemplo: IP.txt)
        System.out.print("Introduce el nombre del fichero: ");

        // Leemos la línea completa y quitamos espacios al principio y al final
        String nombreFichero = sc.nextLine().trim();

        // Cerramos el Scanner porque ya no lo vamos a usar más
        sc.close();

        // Leemos el fichero y obtenemos un mapa con cada IP y sus apariciones.
        // Ejemplo: {10.0.0.5=2, 172.16.15.15=1, 192.168.1.1=4}
        Map<String, Integer> contador = leerFichero(nombreFichero);

        // Si hubo algún problema (no existe o está vacío), leerFichero devuelve null
        // y ya habrá mostrado el aviso. Solo seguimos si el mapa es válido.
        if (contador != null) {
            mostrarResultados(contador);
        }
    }

    /**
     * Lee el fichero indicado y cuenta cuántas veces aparece cada IP.
     *
     * @param ruta nombre o ruta del fichero a leer
     * @return mapa ordenado con IP -> número de apariciones,
     *         o null si el fichero no existe, está vacío o hay error de lectura
     */
    public static Map<String, Integer> leerFichero(String ruta) {
        // Creamos el objeto File con la ruta que nos han pasado
        File fichero = new File(ruta);

        // CASO 1: el fichero no existe -> avisamos y devolvemos null
        if (!fichero.exists()) {
            System.out.println("Aviso: el fichero \"" + ruta + "\" no existe.");
            return null;
        }

        // Mapa donde guardaremos cada IP (clave) y cuántas veces aparece (valor).
        // Usamos TreeMap porque ordena automáticamente las claves.
        Map<String, Integer> contadorRepeticiones = new TreeMap<>();

        // try-with-resources: el BufferedReader se cierra solo al terminar,
        // aunque ocurra un error. No hace falta llamar a close().
        try (BufferedReader lc = new BufferedReader(new FileReader(fichero))) {

            String linea; // Aquí guardaremos cada línea leída

            // readLine() devuelve la siguiente línea o null cuando se acaba el fichero.
            // Asignamos y comparamos a la vez: mientras haya líneas, seguimos.
            while ((linea = lc.readLine()) != null) {

                // Quitamos espacios al principio y al final de la línea.
                // Así "10.0.0.5 " y "10.0.0.5" cuentan como la misma IP.
                String ip = linea.trim();

                // Si la línea está en blanco, la ignoramos y pasamos a la siguiente
                if (ip.isEmpty()) {
                    continue;
                }

                // ¿Ya habíamos visto esta IP antes?
                if (contadorRepeticiones.containsKey(ip)) {
                    // Sí -> obtenemos cuántas veces lleva...
                    int valor = contadorRepeticiones.get(ip);
                    // ...y guardamos una más. Al hacer put con una clave que ya existe,
                    // se sobrescribe el valor anterior (no se duplica la clave).
                    contadorRepeticiones.put(ip, valor + 1);
                } else {
                    // No -> es la primera vez que aparece, la guardamos con 1
                    contadorRepeticiones.put(ip, 1);
                }
            }

        } catch (IOException e) {
            // Si algo falla al leer (permisos, fichero dañado...), avisamos
            System.out.println("Error al leer el fichero: " + e.getMessage());
            return null;
        }

        // CASO 2: el fichero existe pero no tenía ninguna IP -> avisamos y devolvemos null
        if (contadorRepeticiones.isEmpty()) {
            System.out.println("Aviso: el fichero \"" + ruta + "\" está vacío.");
            return null;
        }

        // Todo correcto: devolvemos el mapa con los recuentos
        return contadorRepeticiones;
    }

    /**
     * Clasifica las IPs en permitidas o bloqueadas y las muestra por consola.
     *
     * @param contador mapa con cada IP y su número de apariciones
     */
    public static void mostrarResultados(Map<String, Integer> contador) {

        // Lista con las IPs que NO superan el límite.
        // Como vamos a recorrer un TreeMap (ya ordenado), las añadiremos en orden
        // y la lista mantendrá ese orden.
        List<String> permitidas = new ArrayList<>();

        // Mapa con las IPs bloqueadas: IP -> número de accesos por encima del límite.
        // Usamos TreeMap para que también salgan ordenadas.
        Map<String, Integer> bloqueadas = new TreeMap<>();

        // Recorremos cada pareja (IP, apariciones) del mapa.
        // entrySet() nos da todas las entradas; cada una tiene getKey() y getValue().
        for (Map.Entry<String, Integer> entrada : contador.entrySet()) {
            String ip = entrada.getKey();      // La IP
            int veces = entrada.getValue();    // Cuántas veces ha aparecido

            if (veces <= MAX_ACCESOS) {
                // No supera el límite -> acceso permitido
                permitidas.add(ip);
            } else {
                // Supera el límite -> se bloquea.
                // Los accesos "maliciosos" son los que superan el máximo.
                // Ejemplo: 4 apariciones - 2 permitidas = 2 accesos de más.
                bloqueadas.put(ip, veces - MAX_ACCESOS);
            }
        }

        // Solo mostramos el bloque de permitidas si hay alguna
        if (!permitidas.isEmpty()) {
            System.out.println("IP con acceso permitido:");
            // for-each: recorre la lista IP por IP
            for (String ip : permitidas) {
                System.out.println("- " + ip);
            }
        }

        // Solo mostramos el bloque de bloqueadas si hay alguna
        if (!bloqueadas.isEmpty()) {
            System.out.println("IPs con acceso restringido:");
            // Recorremos el mapa de bloqueadas para mostrar IP y accesos de más
            for (Map.Entry<String, Integer> entrada : bloqueadas.entrySet()) {
                System.out.println("- " + entrada.getKey()
                        + ". Accesos superiores a lo permitido: " + entrada.getValue());
            }
        }
    }
}