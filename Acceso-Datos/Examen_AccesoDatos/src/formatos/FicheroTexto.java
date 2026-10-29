package formatos;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import modelo.Pokemon;

/*
 * FICHERO DE TEXTO  (formato de cada línea:  Pikachu;Eléctrico,Volador;25)
 *
 *   leer(ruta)            -> List<Pokemon>     (BufferedReader + split)
 *   escribir(lista, ruta) -> machaca el fichero con la lista entera
 *   anyadirAlFinal(p, ruta) -> FileWriter(ruta, TRUE): añade sin borrar lo que había
 */
public class FicheroTexto {

    public static final String SEPARADOR = ";";      // separa nombre ; tipos ; nivel

    // ------------------------------------------------------------ LEER
    public static List<Pokemon> leer(String ruta) {
        List<Pokemon> lista = new ArrayList<>();      // aquí voy metiendo los pokémon
        File f = new File(ruta);
        if (!f.exists()) {                            // caso raro: el fichero no existe
            System.out.println("No existe " + ruta);
            return lista;                             // devuelvo la lista vacía
        }
        try (BufferedReader br = new BufferedReader(new FileReader(f))) {   // se cierra solo
            String linea;
            while ((linea = br.readLine()) != null) {          // leo hasta el final (null)
                if (linea.isBlank()) continue;                  // me salto líneas vacías
                String[] partes = linea.split(SEPARADOR);       // "Pikachu" | "Eléctrico,Volador" | "25"
                if (partes.length != 3) {                       // línea mal formada
                    System.out.println("Línea incorrecta: " + linea);
                    continue;
                }
                String nombre = partes[0].trim();
                List<String> tipos = Pokemon.tiposDesdeTexto(partes[1]);   // "A,B" -> [A, B]
                int nivel = Integer.parseInt(partes[2].trim());           // "25" -> 25
                lista.add(new Pokemon(nombre, tipos, nivel));            // creo el objeto y lo guardo
            }
        } catch (IOException e) {
            System.out.println("Error al leer " + ruta + ": " + e.getMessage());
        } catch (NumberFormatException e) {                    // el nivel no era un número
            System.out.println("Nivel no válido: " + e.getMessage());
        }
        return lista;
    }

    // ------------------------------------------------------------ ESCRIBIR (machaca)
    public static void escribir(List<Pokemon> lista, String ruta) {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(ruta))) {   // FileWriter(ruta) BORRA lo anterior
            for (Pokemon p : lista) {
                // monto la línea con el mismo formato que leo
                bw.write(p.getNombre() + SEPARADOR + p.tiposComoTexto() + SEPARADOR + p.getNivel());
                bw.newLine();                          // salto de línea (sin esto sale todo en una línea)
            }
            System.out.println("TXT guardado: " + ruta + " (" + lista.size() + " pokémon)");
        } catch (IOException e) {
            System.out.println("Error al escribir " + ruta + ": " + e.getMessage());
        }
    }

    // ------------------------------------------------------------ AÑADIR AL FINAL
    public static void anyadirAlFinal(Pokemon p, String ruta) {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(ruta, true))) {   // TRUE = añadir
            bw.write(p.getNombre() + SEPARADOR + p.tiposComoTexto() + SEPARADOR + p.getNivel());
            bw.newLine();
        } catch (IOException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    // Prueba rápida: botón derecho -> Run As -> Java Application
    public static void main(String[] args) {
        List<Pokemon> lista = leer("datos/pokemons.txt");
        for (Pokemon p : lista) System.out.println(p);
        escribir(lista, "datos/copia.txt");
    }
}
