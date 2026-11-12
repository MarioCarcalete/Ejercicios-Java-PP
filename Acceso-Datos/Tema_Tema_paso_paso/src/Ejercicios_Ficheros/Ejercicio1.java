package Ejercicios_Ficheros;

import java.io.File;                       // para comprobar si votos.dat existe
import java.io.FileInputStream;            // abre el fichero para LEER bytes
import java.io.FileOutputStream;           // abre (o crea) el fichero para ESCRIBIR bytes
import java.io.IOException;                // error de entrada/salida
import java.io.ObjectInputStream;          // lee OBJETOS enteros (aquí: el Map)
import java.io.ObjectOutputStream;         // escribe OBJETOS enteros
import java.util.ArrayList;                // para la lista de partidos que ordeno
import java.util.List;
import java.util.Map;                      // el tipo: clave -> valor
import java.util.TreeMap;                  // Map ordenado por la clave: PA, PB, PC, PD

// Ficheros 01 · ejercicio 5 (elecciones): votos guardados en un Map dentro de votos.dat
public class Ejercicio1 {

    public static void main(String[] args) {
        // El main SOLO llama a las funciones (como en la agenda del profe)
        anyadirVotos("PC", 200);           // suma 200 votos al PC
        anyadirVotos("PA", 300);
        anyadirVotos("PB", 300);
        anyadirVotos("PD", 300);
        anyadirVotos("PA", 200);           // el PA ya tenía 300 -> ahora 500
        mostrarResultados(1736);          // 1736 = censo (personas que pueden votar)
    }

    // Suma los votos a un partido, guarda el fichero y muestra cómo va el recuento
    public static void anyadirVotos(String nombreP, int votos) {

        // 1) LEER: leerVotos ya mira si existe (si no, me da los 4 partidos a 0)
        Map<String, Integer> votos2 = leerVotos();

        // 2) SUMAR: get = los votos que tenía, + votos = los nuevos.
        //    put con una clave que ya existe SUSTITUYE el valor viejo
        votos2.put(nombreP, votos2.getOrDefault(nombreP,0) + votos);

        // 3) ESCRIBIR: guardo el Map entero de una vez (machaca el fichero viejo; la 1ª vez lo crea)
        try (ObjectOutputStream ous = new ObjectOutputStream(new FileOutputStream("votos.dat"))) {
            ous.writeObject(votos2);                          // TreeMap ya es Serializable
        } catch (IOException e) {                             // por si falla el disco o la ruta
            System.out.println("Error : " + e.getMessage());
        }

        // 4) MOSTRAR, con el formato del enunciado
        System.out.println("Nuevos votos para el partido " + nombreP + ": " + votos + ".");
        System.out.println("Votos hasta el momento:");
        for (String v : votos2.keySet()) {                    // keySet = todas las claves (PA, PB...)
            System.out.println("Partido " + v + ": " + votos2.get(v) + " votos");   // get(v) = sus votos
        }
    }

    // Muestra los partidos ordenados de más a menos votos y el % de escrutinio
    public static void mostrarResultados(int censo) {
        Map<String, Integer> votos2 = leerVotos();                 // 1) leo el fichero

        int total = 0;
        for (String p : votos2.keySet()) {
        	total += votos2.get(p);   // 2) sumo los votos de todos
        }

        int escrutinio = total * 100 / censo;                       // 3) % : multiplico ANTES de dividir
                                                                    //    (con int, 868/1736 daría 0)

        List<String> partidos = new ArrayList<>(votos2.keySet());   // 4) copio las claves a una lista...
        partidos.sort((a, b) -> votos2.get(b) - votos2.get(a));     //    ...y la ordeno: b - a = de mayor a menor

        System.out.println("Resultados con un " + escrutinio + "% de escrutinio:");   // 5) muestro
        for (String p : partidos) {
            System.out.println("Partido " + p + ": " + votos2.get(p) + " votos");
        }
    }

    // Lee el Map de votos.dat. Si no existe, devuelve uno con los 4 partidos a 0.
    // La usan las dos funciones de arriba (igual que leerXML en el XML del profe)
    @SuppressWarnings("unchecked")                                 // quita el aviso amarillo del cast
    public static Map<String, Integer> leerVotos() {
        File archivo = new File("votos.dat");                      // el fichero (exista o no)
        Map<String, Integer> votos = new TreeMap<>();              // Map vacío, ordenado por clave

        if (!archivo.exists()) {                                   // PRIMERA VEZ: no hay fichero
            votos.put("PA", 0);                                    // empiezo los 4 partidos a 0
            votos.put("PB", 0);
            votos.put("PC", 0);
            votos.put("PD", 0);
        } else {                                                   // YA EXISTE: lo leo
            try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(archivo))) {
                votos = (Map<String, Integer>) ois.readObject();   // el Map entero de una vez (sin while)
            } catch (IOException | ClassNotFoundException e) {     // fallo al leer / clase desconocida
                System.out.println("Error : " + e.getMessage());
            }
        }
        return votos;                                              // devuelvo el Map para usarlo fuera
    }
}
