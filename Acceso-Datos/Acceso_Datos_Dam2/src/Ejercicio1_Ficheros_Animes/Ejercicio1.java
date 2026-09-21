package Ejercicio1_Ficheros_Animes;

import java.io.BufferedReader;   // Para leer texto de un archivo línea a línea, de forma eficiente (con buffer)
import java.io.File;             // Para representar rutas y archivos del sistema
import java.io.FileReader;       // Para abrir un archivo de texto en modo lectura
import java.io.IOException;      // Excepción que hay que controlar al leer/escribir archivos
import java.util.ArrayList;      // Implementación de lista dinámica (crece según se añaden elementos)
import java.util.HashMap;        // Implementación de diccionario (clave -> valor) basada en tabla hash
import java.util.List;           // Interfaz de lista, para declarar variables de forma genérica
import java.util.Map;            // Interfaz de diccionario, para declarar variables de forma genérica

public class Ejercicio1 {

    // Ruta al fichero de animes, construida con File.separator para que funcione
    // igual en Windows ("\") como en Linux/Mac ("/"), sea cual sea el sistema operativo.
    final static String DIR_ANIME = "DAM2" + File.separator + "josemaria" + File.separator + "animes.txt";

    // Ruta al fichero de personajes, construida de la misma forma.
    final static String DIR_PERSONAJES = "DAM2" + File.separator + "josemaria" + File.separator + "personajes.txt";

    public static void main(String[] args) {

        // "." representa el directorio actual desde el que se ejecuta el programa
        // (en Eclipse, normalmente la raíz del proyecto).
        File directAct = new File(".");

        // Imprime la ruta absoluta de ese directorio actual, solo para comprobar dónde está trabajando el programa.
        System.out.println(directAct.getAbsolutePath());

        // Creamos objetos File apuntando a animes.txt y personajes.txt.
        // Esto NO abre ni lee nada todavía, solo representa esas rutas en memoria.
        File dirA = new File(DIR_ANIME);
        File dirP = new File(DIR_PERSONAJES);

        // Diccionario donde guardaremos los animes: clave = código (Integer), valor = título (String).
        // Se declara con la interfaz Map, pero se instancia con la implementación concreta HashMap.
        Map<Integer, String> animes = new HashMap<>();

        // Llamada de PRUEBA a la función buscarPersonajes, antes de conectarla con el bucle del Map.
        // dirP.getPath() convierte el File en el String de ruta que espera la función (aunque aquí
        // también valdría pasar directamente DIR_PERSONAJES, que ya es un String).
        List<String> prueba = buscarPersonajes(3, dirP.getPath()); // esto de dirP.getPath es para pasar de File a String
        System.out.println(prueba); // Imprime la lista de personajes con código 3, para comprobar que la función funciona

        // try-with-resources: abre el fichero de animes para lectura y lo CIERRA automáticamente
        // al salir del bloque (tanto si todo va bien como si salta una excepción).
        try (BufferedReader lector = new BufferedReader(new FileReader(dirA))) {

            String linea; // Aquí guardaremos cada línea leída del fichero

            // Bucle que lee línea a línea hasta que readLine() devuelve null (fin del fichero)
            while ((linea = lector.readLine()) != null) {

                // Separamos la línea en 2 trozos como máximo, cortando solo por el PRIMER espacio.
                // Así, si el título tiene varias palabras, no se corta también por esos espacios.
                String[] partes = linea.split(" ", 2);  // esto hace que como limite separe 2 trozos para que no separe tambien el nombre aqui separa el numero gracias al espacio y los personajes

                // Solo a modo informativo: imprime el código y el título de cada línea leída
                System.out.println("Código: " + partes[0] + " - Título: " + partes[1]);

                // Convertimos el código de texto (String) a número entero (int)
                int codigo = Integer.parseInt(partes[0]);
                // El título se queda tal cual, es el resto de la línea
                String titulo = partes[1];

                // Guardamos el par código-título en el diccionario "animes"
                animes.put(codigo, titulo);
            }

            // Recorremos el Map ya completo con un for-each sobre entrySet(),
            // que nos da parejas clave-valor (Map.Entry) en cada vuelta.
            for (Map.Entry<Integer, String> entry : animes.entrySet()) { // ESto es para leer un Hasmap clave valor
                Integer codigo = entry.getKey();    // getKey() -> la clave de esa entrada (el código)
                String titulo = entry.getValue();   // getValue() -> el valor de esa entrada (el título)
                System.out.println(codigo + " = " + titulo); // Imprime cada pareja, para comprobar que el Map se llenó bien
            }

        } catch (Exception e) {
            // Si algo falla al leer el fichero de animes (ruta incorrecta, error de formato, etc.),
            // se captura aquí en vez de que el programa se detenga bruscamente.
            System.out.println("Error : " + e.getMessage());
        }

        // Recorremos el Map de animes con forEach + lambda: por cada pareja (codigo, titulo)...
        animes.forEach((codigo, titulo) -> {

            // ...usamos la función ya probada para buscar los personajes de ESE código concreto
            List<String> personajes = buscarPersonajes(codigo, dirP.getPath()); // o pones DIR_PERSONAJES

            // Imprimimos el título del anime como cabecera
            System.out.println(titulo);

            if (personajes.isEmpty()) {
                // Si la lista de personajes está vacía, avisamos de que no hay ninguno (caso Hunter x Hunter)
                System.out.println("No tiene Personajes");
            } else {
                // Si hay personajes, los recorremos uno a uno y los imprimimos con un guion delante
                for (String nombre : personajes) {
                    System.out.println("- " + nombre);
                }
            }
        });
        
        System.out.println("Personajes sin anime");

        try (BufferedReader lector = new BufferedReader(new FileReader(dirP))) {
            String linea;
            while ((linea = lector.readLine()) != null) {
                String[] partes = linea.split(" ", 2);
                int codigo = Integer.parseInt(partes[0]);
                String nombre = partes[1];

                if (!animes.containsKey(codigo)) {
                    System.out.println("- " + nombre); // considero que si dentro de animes no hay ningun  codigo que coincida con el personaje de personajes quiere decir que no tiene anime
                }
            }
        } catch (IOException e) {
            System.out.println("Error : " + e.getMessage());
        }

    }

    // Función que busca, dentro de un fichero de personajes, todos los que tengan un código concreto.
    // Recibe el código a buscar y la ruta del fichero como parámetros, así es independiente y reutilizable.
    public static List<String> buscarPersonajes(int codigoBuscado, String rutaFichero) {

        // Lista donde iremos acumulando los nombres de personajes que coincidan con el código buscado
        List<String> PersonajesEncontrados = new ArrayList<>();

        // Abrimos el fichero de personajes para lectura (se cierra solo al salir del bloque)
        try (BufferedReader lector = new BufferedReader(new FileReader(rutaFichero))) {

            String linea; // Aquí guardaremos cada línea leída

            // Leemos línea a línea hasta que no queden más (readLine() devuelve null)
            while ((linea = lector.readLine()) != null) {

                // Igual que antes: separamos en máximo 2 trozos, cortando por el primer espacio
                String[] partes = linea.split(" ", 2);

                // Convertimos el código de esta línea de texto a número
                int codigo = Integer.parseInt(partes[0]);
                // El nombre del personaje es el resto de la línea
                String personajes = partes[1];

                // Si el código de esta línea coincide con el que estamos buscando...
                if (codigoBuscado == codigo) {
                    // ...añadimos el nombre del personaje a la lista de resultados
                    PersonajesEncontrados.add(personajes);
                }
            }

        } catch (IOException e) {
            // Si falla la lectura del fichero de personajes, lo capturamos aquí
            System.out.println("Error : " + e.getMessage());
        }

        // Devolvemos la lista con todos los personajes encontrados para ese código
        // (puede estar vacía si no hay ninguno)
        return PersonajesEncontrados;
    }

}


/*
 *
 * 2. Usar el método forEach (Java 8+)
Una alternativa moderna y concisa utilizando expresiones lambda:

mapa.forEach((clave, valor) -> System.out.println(clave + " = " + valor));

3. Iterar solo sobre Claves
Si solo necesitas las claves, usa keySet():

for (String clave : mapa.keySet()) {
    System.out.println("Clave: " + clave);
}

4. Iterar solo sobre Valores
Para acceder únicamente a los valores, utiliza values():

for (Integer valor : mapa.values()) {
    System.out.println("Valor: " + valor);
}
 *
 *
 *
 *
 *
 *
 *
 *
 * */