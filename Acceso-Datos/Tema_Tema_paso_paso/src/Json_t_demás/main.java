package Json_t_demás;

import java.io.File;                          // para ver en qué carpeta busca el fichero
import java.io.FileReader;                    // abre el JSON para LEER texto
import java.io.FileWriter;                    // abre (o crea) el JSON para ESCRIBIR texto
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;

import com.google.gson.Gson;                  // la librería que pasa JSON <-> objetos
import com.google.gson.GsonBuilder;           // para crear un Gson "bonito" (con sangría)

public class main {

    public static void main(String[] args) {

        final String ruta = "pokemons.json";              // el JSON está en la raíz del proyecto

        leerPokemon(ruta);                                // 1) muestro lo que hay

        File directorioActual = new File(".");            // "." = la carpeta desde la que se ejecuta
        System.out.println(directorioActual.getAbsolutePath());   // ahí tiene que estar pokemons.json

        // 2) añado uno nuevo: el new pokemon lleva nombre, tipos (lista) y nivel
        //    new ArrayList<>(List.of(...)) porque List.of sola no deja hacer add después
        nuevoPokemon(new pokemon("Pichi", new ArrayList<>(List.of("Cerveza", "Fuego")), 99), ruta);

        // 3) cambio el nivel de Pikachu a 99
        modificar_nivel("Pikachu", 99, ruta);

        // 4) cambio el tipo de la posición 0 de Pichi (aquí SÍ hace falta posición: hay varios tipos)
        modificar_tipo("Pichi", 0, "Agua", ruta);

        leerPokemon(ruta);
        
        // 5) muestro cómo ha quedado
        
        
        eliminarPokemon("pichi",ruta);
        
        leerPokemon(ruta);
    }

    // ---------------------------------------------------------------
    // MOSTRAR todos los pokémon
    // ---------------------------------------------------------------
    public static void leerPokemon(String fichero) {
        System.out.println("======Pokedex======");
        List<pokemon> lista = cargarListaPokemon(fichero);   // leo el JSON a una lista
        if (lista != null) {                                 // null = no se pudo leer
            for (pokemon c : lista) {
                System.out.println(c);                       // usa el toString de pokemon
            }
            System.out.println();
        }
    }

    // ---------------------------------------------------------------
    // CARGAR: JSON -> List<pokemon>   (la usan todas las demás)
    // ---------------------------------------------------------------
    public static List<pokemon> cargarListaPokemon(String ruta) {
        List<pokemon> lista = null;                          // si falla, devuelvo null

        try (Reader lc = new FileReader(ruta)) {             // abro el fichero (se cierra solo)
            Gson gson = new Gson();
            Pokedex pokedex = gson.fromJson(lc, Pokedex.class);   // JSON -> objeto raíz Pokedex
            lista = pokedex.getPokedex();                    // saco la lista de dentro
        } catch (Exception e) {
            System.out.println("Error : " + e);
        }
        return lista;
    }

    // ---------------------------------------------------------------
    // GUARDAR: List<pokemon> -> JSON   (machaca el fichero entero)
    // ---------------------------------------------------------------
    public static void guardarPokedex(List<pokemon> lista, String ruta) {
        Pokedex pokedex = new Pokedex();                     // creo la raíz...
        pokedex.setPokedex(lista);                           // ...y le meto la lista

        try (FileWriter ec = new FileWriter(ruta)) {         // abro el fichero para escribir
            // setPrettyPrinting: cada dato en su línea (con new Gson() sale todo en una línea)
            Gson gson = new GsonBuilder().setPrettyPrinting().create();
            gson.toJson(pokedex, ec);                        // objeto -> JSON dentro del fichero
        } catch (Exception e) {
            System.out.println("Error  :  " + e.getMessage());
        }
    }

    // ---------------------------------------------------------------
    // AÑADIR UN Pokemon NUEVO (si no existe ya)
    // ---------------------------------------------------------------
    public static void nuevoPokemon(pokemon nuevoP, String ruta) {
        List<pokemon> lista = cargarListaPokemon(ruta);      // 1) cargo
        if (lista == null) {
            System.out.println("No se ha podido cargar la pokedex");
            return;                                          // salgo de la función
        }

        boolean encontrado = false;                          // 2) busco si ya está
        for (int i = 0; i < lista.size() && !encontrado; i++) {   // !encontrado: paro al encontrarlo
            String nombre = lista.get(i).getNombre().trim();
            if (nombre.equalsIgnoreCase(nuevoP.getNombre().trim())) {
                encontrado = true;
            }
        }

        if (encontrado) {
            System.out.println("Ya existe un pokemon con ese nombre");
        } else {
            lista.add(nuevoP);                               // 3) lo añado (con sus tipos y nivel)
            guardarPokedex(lista, ruta);                     // 4) guardo la lista entera
            System.out.println("Pokemon " + nuevoP.getNombre() + " añadido");
        }
    }

    // ---------------------------------------------------------------
    // MODIFICAR EL NIVEL (nivel es un int: no hay posición)
    // ---------------------------------------------------------------
    public static void modificar_nivel(String nombreN, int nivelN, String ruta) {
        List<pokemon> lista = cargarListaPokemon(ruta);      // 1) cargo
        if (lista == null) {
            System.out.println("No se ha podido cargar la lista");
            return;
        }

        boolean encontrado = false;
        for (int i = 0; i < lista.size() && !encontrado; i++) {   // 2) busco por nombre
            pokemon p = lista.get(i);
            if (p.getNombre().trim().equalsIgnoreCase(nombreN.trim())) {
                encontrado = true;
                p.setNivel(nivelN);                          // 3) cambio el nivel del objeto
                guardarPokedex(lista, ruta);                 // 4) p está DENTRO de la lista: guardo la lista
                System.out.println("Nivel de " + nombreN + " cambiado a " + nivelN);
            }
        }
        if (!encontrado) {
            System.out.println("No se encontró ningún " + nombreN);
        }
    }

    // ---------------------------------------------------------------
    // MODIFICAR UN TIPO concreto (tipos es una lista: aquí SÍ va la posición)
    // ---------------------------------------------------------------
    public static void modificar_tipo(String nombreN, int posicion, String tipoN, String ruta) {
        List<pokemon> lista = cargarListaPokemon(ruta);      // 1) cargo
        if (lista == null) {
            System.out.println("No se ha podido cargar la lista");
            return;
        }

        boolean encontrado = false;
        for (int i = 0; i < lista.size() && !encontrado; i++) {   // 2) busco por nombre
            pokemon p = lista.get(i);
            if (p.getNombre().trim().equalsIgnoreCase(nombreN.trim())) {
                encontrado = true;
                List<String> tipos = p.getTipo();           // la lista de tipos de ESE pokémon
                if (posicion >= 0 && posicion < tipos.size()) {   // ¿existe esa posición?
                    tipos.set(posicion, tipoN);              // 3) sustituyo el tipo de esa posición
                    guardarPokedex(lista, ruta);             // 4) guardo
                    System.out.println("Tipo " + (posicion + 1) + " de " + nombreN + " cambiado a " + tipoN);
                } else {
                    System.out.println(nombreN + " no tiene tipo en la posición " + posicion);
                }
            }
        }
        if (!encontrado) {
            System.out.println("No se encontró ningún " + nombreN);
        }
    }
    
    public static void eliminarPokemon(String nombreE, String ruta) {
        List<pokemon> lista = cargarListaPokemon(ruta);          // 1) cargo la lista del JSON
        if (lista == null) {
            System.out.println("Error al cargar lista");
            return;                                              //    sin lista no sigo
        }

        boolean encontrado = false;
        for (int i = 0; i < lista.size() && !encontrado; i++) {  // 2) recorro hasta encontrarlo
            pokemon p = lista.get(i);
            if (p.getNombre().trim().equalsIgnoreCase(nombreE.trim())) {   // ← CON llaves
                encontrado = true;                               //    así el for para en la siguiente vuelta
                lista.remove(p);                                 // 3) quito el OBJETO de la lista
                guardarPokedex(lista, ruta);                     // 4) guardo la lista sin él
                System.out.println("Pokemon " + nombreE + " eliminado");
            }
        }
        if (!encontrado) {                                       // 5) no estaba
            System.out.println("No se encontró ningún " + nombreE);
        }
    }
    	
    	
    	
    
}
