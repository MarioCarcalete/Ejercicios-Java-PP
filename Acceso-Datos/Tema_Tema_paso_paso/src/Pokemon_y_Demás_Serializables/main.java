package Pokemon_y_Demás_Serializables;     // paquete donde está la clase

import java.io.BufferedReader;              // para leer texto línea a línea
import java.io.BufferedWriter;              // para escribir texto de forma eficiente
import java.io.FileReader;                  // abre un fichero para leerlo
import java.io.FileWriter;                  // abre (o crea) un fichero para escribir
import java.io.IOException;                 // error de entrada/salida (fichero no existe, etc.)
import java.util.ArrayList;                 // la lista que usamos
import java.util.List;                      // necesario para List.of

public class main {

    public static void main(String[] args) {               // aquí empieza el programa

        ArrayList<Ejercicio_Pokemon> Lpokemon = new ArrayList<>();  // lista con los pokémon originales
        ArrayList<Ejercicio_Pokemon> Leer = new ArrayList<>();      // lista para los que leamos del fichero

        // Creo 3 objetos llamando al constructor (nombre, tipo, nivel)
        Ejercicio_Pokemon pi  = new Ejercicio_Pokemon("pikachu", "Eléctrico", 25);
        Ejercicio_Pokemon pi2 = new Ejercicio_Pokemon("Charizard", "Fuego y Volador", 50);
        Ejercicio_Pokemon pi3 = new Ejercicio_Pokemon("Miwutu", "psiquico", 75);

        Lpokemon.addAll(List.of(pi, pi2, pi3));             // meto los 3 en la lista de golpe

        for (Ejercicio_Pokemon c : Lpokemon) {              // recorro la lista
            System.out.println(c);                          // println usa el toString del pokémon
        }

        // ===== ESCRITURA: lista → fichero de texto =====
        // try-with-resources: el BufferedWriter se cierra solo al acabar el try
        // FileWriter crea pokemons.txt (si ya existe, lo machaca)
        try (BufferedWriter ec = new BufferedWriter(new FileWriter("pokemons.txt"))) {
            for (Ejercicio_Pokemon c : Lpokemon) {          // por cada pokémon de la lista...
                // ...escribo sus datos separados por comas: pikachu,Eléctrico,25
                ec.write(c.getNombre() + "," + c.getTipo() + "," + c.getNivel());
                ec.newLine();                               // salto de línea: uno por línea
            }
        } catch (IOException e) {                           // si no se puede escribir...
            System.out.println("Error :" + e.getMessage()); // ...muestro el motivo
        }

        // ===== LECTURA: fichero de texto → lista =====
        // Abro el mismo fichero para leerlo (también se cierra solo)
        try (BufferedReader lc = new BufferedReader(new FileReader("pokemons.txt"))) {
            String linea;                                   // aquí guardo cada línea leída

            // readLine lee una línea; cuando no quedan devuelve null y el while para
            while ((linea = lc.readLine()) != null) {
                String[] partes = linea.split(",");         // "pikachu,Eléctrico,25" → ["pikachu","Eléctrico","25"]

                String nombre = partes[0];                  // posición 0: el nombre
                String tipo = partes[1];                    // posición 1: el tipo
                int nivel = Integer.parseInt(partes[2].trim()); // posición 2: texto → int (trim quita espacios)
//                Si en el examen te piden los dos tipos por separado, 
                //después haz String[] tipos = partes[4].split(", ");. 
                //Te queda tipos[0] = "Normal" y tipos[1] = "Volador".
                Leer.add(new Ejercicio_Pokemon(nombre, tipo, nivel)); // creo el pokémon y lo añado
            }

            for (Ejercicio_Pokemon c : Leer) {              // recorro la lista leída
                System.out.println(c);                      // y la imprimo para comprobar
            }
            

        } catch (IOException e) {                           // el fichero no existe o no se puede leer
            System.out.println("Error : " + e.getMessage());
        } catch (NumberFormatException e) {                 // el nivel del fichero no era un número
            System.out.println("Nivel de control malo ");
        }
    }   // fin del main
}       // fin de la clase