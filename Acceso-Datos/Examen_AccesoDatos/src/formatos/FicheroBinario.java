package formatos;

import java.io.EOFException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.List;

import modelo.Pokemon;

/*
 * FICHERO BINARIO DE OBJETOS (.dat)
 *   - La clase Pokemon tiene que ser "implements Serializable".
 *   - escribir: un writeObject POR CADA pokémon.
 *   - leer: readObject en un while(true) hasta que salta EOFException (= se acabó el fichero).
 *   (Otra forma: writeObject(lista) de una vez y leer con (List<Pokemon>) readObject(), sin while.)
 */
public class FicheroBinario {

    // ------------------------------------------------------------ ESCRIBIR
    public static void escribir(List<Pokemon> lista, String ruta) {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(ruta))) {
            for (Pokemon p : lista) {
                oos.writeObject(p);                    // guardo el objeto ENTERO
            }
            System.out.println("DAT guardado: " + ruta + " (" + lista.size() + " pokémon)");
        } catch (IOException e) {
            System.out.println("Error al escribir " + ruta + ": " + e.getMessage());
        }
    }

    // ------------------------------------------------------------ LEER
    public static List<Pokemon> leer(String ruta) {
        List<Pokemon> lista = new ArrayList<>();
        if (!new File(ruta).exists()) {                // si no existe, lista vacía
            System.out.println("No existe " + ruta);
            return lista;
        }
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(ruta))) {
            while (true) {                             // no sé cuántos hay: leo hasta que se acabe
                Pokemon p = (Pokemon) ois.readObject();   // readObject devuelve Object -> cast
                lista.add(p);
            }
        } catch (EOFException e) {
            // FIN DEL FICHERO: es lo normal, no es un error. Va ANTES que IOException.
        } catch (IOException | ClassNotFoundException e) {
            System.out.println("Error al leer " + ruta + ": " + e.getMessage());
        }
        return lista;
    }

    public static void main(String[] args) {
        escribir(FicheroTexto.leer("datos/pokemons.txt"), "datos/pokemons.dat");
        for (Pokemon p : leer("datos/pokemons.dat")) System.out.println(p);
    }
}
