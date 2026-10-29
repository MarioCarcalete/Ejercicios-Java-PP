package Pokemon_y_Demás_Serializables;

import java.io.EOFException;            // aviso de "fin de fichero" en binario
import java.io.FileInputStream;         // abre el fichero para leer bytes
import java.io.FileOutputStream;        // abre/crea el fichero para escribir bytes
import java.io.IOException;             // errores de entrada/salida
import java.io.ObjectInputStream;       // lee objetos enteros
import java.io.ObjectOutputStream;      // escribe objetos enteros
import java.util.ArrayList;
import java.util.List;

public class Escritura_Y_Lectura_Binario {
    public static void main(String[] args) {

        // Creo la lista AQUÍ, porque esta clase no ve la del otro main
        ArrayList<Ejercicio_Pokemon> Lpokemon = new ArrayList<>();
        Lpokemon.addAll(List.of(
            new Ejercicio_Pokemon("pikachu", "Eléctrico", 25),
            new Ejercicio_Pokemon("Charizard", "Fuego y Volador", 50),
            new Ejercicio_Pokemon("Miwutu", "psiquico", 75)));

        // ===== ESCRIBIR: lista → pokemons.dat =====
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream("pokemons.dat"))) {
            for (Ejercicio_Pokemon p : Lpokemon) {   // por cada pokémon...
                oos.writeObject(p);                   // ...lo escribo entero en binario
            }
        } catch (IOException e) {
            System.out.println("Error al escribir: " + e.getMessage());
        }

        // ===== LEER: pokemons.dat → lista =====
        ArrayList<Ejercicio_Pokemon> desdeDat = new ArrayList<>();   // aquí guardo lo leído

        // MISMO nombre de fichero que al escribir
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream("pokemons.dat"))) {
            while (true) {                                           // leo sin parar...
                Ejercicio_Pokemon p = (Ejercicio_Pokemon) ois.readObject();  // leo un objeto y lo convierto
                desdeDat.add(p);                                     // lo añado a la lista
            }
        } catch (EOFException e) {
            // ...hasta que se acaba el fichero. NO es un error: no hago nada
        } catch (IOException | ClassNotFoundException e) {           // errores de verdad (va DESPUÉS de EOF)
            System.out.println("Error al leer: " + e.getMessage());
        }

        for (Ejercicio_Pokemon p : desdeDat) {                       // imprimo lo leído
            System.out.println(p);
        }
    }
}