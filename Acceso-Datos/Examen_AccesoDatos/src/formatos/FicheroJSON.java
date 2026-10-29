package formatos;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.Reader;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import modelo.Pokedex;
import modelo.Pokemon;

/*
 * JSON CON GSON.  Formato:
 * {
 *   "pokedex": [
 *     { "nombre": "Gyarados", "tipos": ["Agua", "Volador"], "nivel": 40 }
 *   ]
 * }
 *  { }  -> una clase        (de fuera: Pokedex; de dentro: Pokemon)
 *  [ ]  -> una List         (List<Pokemon>, List<String>)
 *  Las CLAVES se llaman igual que los ATRIBUTOS (si no -> @SerializedName("clave")).
 *
 * REGLA DE ORO del CRUD (igual que el profe):
 *     List<Pokemon> lista = leer(ruta);   1) cargar la lista
 *     Pokemon p = buscar(lista, nombre);  2) buscar el objeto
 *     ...add / set / remove...            3) cambiar la LISTA (o el objeto, que está dentro)
 *     escribir(lista, ruta);              4) guardar la lista ENTERA
 */
public class FicheroJSON {

    // ================================================================ BÁSICAS
    // JSON -> List<Pokemon>
    public static List<Pokemon> leer(String ruta) {
        if (!new File(ruta).exists()) {                          // no existe -> lista vacía
            System.out.println("No existe " + ruta);
            return new ArrayList<>();
        }
        try (Reader lector = new FileReader(ruta)) {
            Pokedex raiz = new Gson().fromJson(lector, Pokedex.class);   // JSON -> objeto raíz
            if (raiz == null || raiz.getPokedex() == null) return new ArrayList<>();   // JSON vacío
            return raiz.getPokedex();                            // saco la lista de dentro
        } catch (Exception e) {
            System.out.println("Error al leer JSON: " + e.getMessage());
            return new ArrayList<>();
        }
    }

    // List<Pokemon> -> JSON (machaca el fichero; si no existe, lo CREA)
    public static void escribir(List<Pokemon> lista, String ruta) {
        Pokedex raiz = new Pokedex(lista);                       // meto la lista en la raíz
        Gson gson = new GsonBuilder().setPrettyPrinting().create();   // bonito: una cosa por línea
        try (Writer escritor = new FileWriter(ruta)) {
            gson.toJson(raiz, escritor);                         // objeto -> JSON en el fichero
            System.out.println("JSON guardado: " + ruta + " (" + lista.size() + " pokémon)");
        } catch (Exception e) {
            System.out.println("Error al escribir JSON: " + e.getMessage());
        }
    }

    // Busca en la LISTA (no en el fichero). Devuelve el objeto o null.
    public static Pokemon buscar(List<Pokemon> lista, String nombre) {
        for (Pokemon p : lista) {
            if (p.getNombre().equalsIgnoreCase(nombre.trim())) return p;
        }
        return null;
    }

    // ================================================================ CRUD
    // CREAR un JSON vacío ({"pokedex": []}) si no existe
    public static void crearSiNoExiste(String ruta) {
        if (!new File(ruta).exists()) escribir(new ArrayList<>(), ruta);
    }

    public static void mostrar(String ruta) {
        System.out.println("=== " + ruta + " ===");
        for (Pokemon p : leer(ruta)) System.out.println(p);
    }

    // AÑADIR un pokémon (si no existe)
    public static void anyadir(Pokemon nuevo, String ruta) {
        crearSiNoExiste(ruta);
        List<Pokemon> lista = leer(ruta);                        // 1) cargar
        if (buscar(lista, nuevo.getNombre()) != null) {          // 2) ¿ya está?
            System.out.println("Ya existe " + nuevo.getNombre());
            return;
        }
        lista.add(nuevo);                                        // 3) añadir a la lista
        escribir(lista, ruta);                                   // 4) guardar
        System.out.println("Añadido " + nuevo.getNombre());
    }

    // SOBRECARGA: lo mismo pero con los datos sueltos (como el "alta" del ejercicio 5)
    public static void anyadir(String nombre, List<String> tipos, int nivel, String ruta) {
        anyadir(new Pokemon(nombre, tipos, nivel), ruta);        // creo el objeto y llamo a la de arriba
    }

    // MODIFICAR un dato simple (nivel)
    public static void modificarNivel(String nombre, int nivelNuevo, String ruta) {
        List<Pokemon> lista = leer(ruta);
        Pokemon p = buscar(lista, nombre);
        if (p == null) { System.out.println("No existe " + nombre); return; }
        p.setNivel(nivelNuevo);                                  // p está DENTRO de la lista
        escribir(lista, ruta);                                   // -> guardo la lista
        System.out.println("Nivel de " + nombre + " -> " + nivelNuevo);
    }

    // AÑADIR un valor a una lista interna (un tipo más / un teléfono más)
    public static void anyadirTipo(String nombre, String tipoNuevo, String ruta) {
        List<Pokemon> lista = leer(ruta);
        Pokemon p = buscar(lista, nombre);
        if (p == null) { System.out.println("No existe " + nombre); return; }
        if (p.getTipos().contains(tipoNuevo)) { System.out.println(nombre + " ya es " + tipoNuevo); return; }
        p.getTipos().add(tipoNuevo);                             // add a SU lista de tipos
        escribir(lista, ruta);
        System.out.println("Tipo " + tipoNuevo + " añadido a " + nombre);
    }

    // MODIFICAR un valor concreto de la lista interna (por posición: 0 = primero)
    public static void modificarTipo(String nombre, int posicion, String tipoNuevo, String ruta) {
        List<Pokemon> lista = leer(ruta);
        Pokemon p = buscar(lista, nombre);
        if (p == null) { System.out.println("No existe " + nombre); return; }
        if (posicion < 0 || posicion >= p.getTipos().size()) {   // ¿existe esa posición?
            System.out.println(nombre + " no tiene tipo en la posición " + posicion);
            return;
        }
        p.getTipos().set(posicion, tipoNuevo);                   // set = sustituir
        escribir(lista, ruta);
        System.out.println("Tipo " + (posicion + 1) + " de " + nombre + " -> " + tipoNuevo);
    }

    // ELIMINAR algo CONCRETO de dentro (quitar el tipo "Volador")
    public static void eliminarTipo(String nombre, String tipoBorrar, String ruta) {
        List<Pokemon> lista = leer(ruta);
        Pokemon p = buscar(lista, nombre);
        if (p == null) { System.out.println("No existe " + nombre); return; }
        if (p.getTipos().remove(tipoBorrar)) {                   // remove(String) devuelve true si lo quitó
            escribir(lista, ruta);
            System.out.println("Tipo " + tipoBorrar + " quitado de " + nombre);
        } else {
            System.out.println(nombre + " no tiene el tipo " + tipoBorrar);
        }
    }

    // ELIMINAR un pokémon entero
    public static void eliminar(String nombre, String ruta) {
        List<Pokemon> lista = leer(ruta);
        Pokemon p = buscar(lista, nombre);
        if (p == null) { System.out.println("No existe " + nombre); return; }
        lista.remove(p);                                         // quito el OBJETO (no el String)
        escribir(lista, ruta);
        System.out.println("Eliminado " + nombre);
    }

    public static void main(String[] args) {
        String ruta = "datos/pruebaJSON.json";
        escribir(FicheroTexto.leer("datos/pokemons.txt"), ruta);   // creo el JSON desde el TXT
        anyadir("Lapras", List.of("Agua", "Hielo"), 30, ruta);
        anyadir("Lapras", List.of("Agua"), 30, ruta);              // -> ya existe
        modificarNivel("Pikachu", 50, ruta);
        anyadirTipo("Pikachu", "Acero", ruta);
        modificarTipo("Pikachu", 1, "Volador", ruta);
        eliminarTipo("Gyarados", "Volador", ruta);
        eliminar("Bulbasaur", ruta);
        eliminar("Mewtwo", ruta);                                  // -> no existe
        mostrar(ruta);
    }
}
