package Apuntes_De_Ficheros;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.List;

/*
 * ============================================================
 *  APUNTES 3 - FICHEROS BINARIOS Y PERSISTENCIA DE OBJETOS
 * ============================================================
 *  Texto vs binario:
 *   - TEXTO: lo abres con un editor y lo lees. Todo se guarda como caracteres.
 *     El número 1234567 ocupa 7 caracteres.
 *   - BINARIO: se guardan los bytes tal cual están en memoria. No es legible
 *     con un editor. Un int ocupa SIEMPRE 4 bytes, sea 1 o 1234567.
 *
 *  Ventajas del binario: ocupa menos, es más rápido y permite guardar
 *  objetos completos (serialización) muy fácilmente.
 *
 *  Dos familias de clases:
 *   - DataOutputStream / DataInputStream     → tipos primitivos (int, double...)
 *   - ObjectOutputStream / ObjectInputStream → objetos completos
 *
 *  ¿Por qué OUTPUT para escribir e INPUT para leer?
 *  Piensa desde el punto de vista del PROGRAMA:
 *   - Escribir: los datos SALEN del programa hacia el fichero → Output
 *   - Leer:     los datos ENTRAN al programa desde el fichero → Input
 */
public class Ficheros3_Binarios_Persistencia_Objetos {

	static final String RUTA = "binario.bin";

	public static void main(String[] args) {
		// 1. Tipos primitivos
		escribirFichero(RUTA);
		leerFichero(RUTA);

		// 2. Persistencia de UN objeto
		Pokemon pokemon = new Pokemon(6, "Charizard", "Fuego", "Volador");
		guardarPokemon(pokemon, RUTA);
		Pokemon pokemonRecuperado = recuperarPokemon(RUTA);
		if (pokemonRecuperado != null)
			pokemonRecuperado.mostrar();

		// 3. Guardar y recuperar una LISTA de objetos
		Pokemon p1 = new Pokemon(1, "Bulbasaur", "Planta");
		Pokemon p2 = new Pokemon(6, "Charizard", "Fuego", "Volador");
		Pokemon p3 = new Pokemon(2, "Ivysaur", "Planta");
		Pokemon p4 = new Pokemon(25, "Pikachu", "Eléctrico");
		Pokemon p5 = new Pokemon(11, "Metapod", "Bicho");
		Pokemon p6 = new Pokemon(7, "Squirtle", "Agua");
		ArrayList<Pokemon> listaPokemons = new ArrayList<>(List.of(p1, p2, p3, p4, p5, p6));
		guardarListaPokemons(listaPokemons, RUTA);

		ArrayList<Pokemon> listaRecuperada = recuperarListaPokemons(RUTA);
		if (listaRecuperada != null)
			for (Pokemon poke : listaRecuperada)
				poke.mostrar();

		// 4. AÑADIR un objeto a un fichero que ya existe.
		// Con ObjectOutputStream NO se puede hacer append directamente
		// (el fichero lleva una cabecera al principio y se corrompería).
		// La técnica es: recuperar la lista → añadir → volver a guardarla entera.
		Pokemon p7 = new Pokemon(131, "Lapras", "Agua", "Hielo");
		listaRecuperada = recuperarListaPokemons(RUTA);
		if (listaRecuperada != null) {
			listaRecuperada.add(p7);
			guardarListaPokemons(listaRecuperada, RUTA);
		}

		listaRecuperada = recuperarListaPokemons(RUTA);
		if (listaRecuperada != null)
			for (Pokemon poke : listaRecuperada)
				poke.mostrar();
	}

	// ---------------------------------------------------------
	// TIPOS PRIMITIVOS
	// ---------------------------------------------------------
	public static void escribirFichero(String fichero) {
		// DataOutputStream envuelve a un FileOutputStream (el fichero en sí)
		try (DataOutputStream binario = new DataOutputStream(new FileOutputStream(fichero))) {
			// En binario SÍ importa el tipo: hay un método para cada uno.
			// Entre paréntesis, lo que ocupa cada dato en el fichero:
			binario.writeInt(42);           // 4 bytes
			binario.writeDouble(3.14159);   // 8 bytes
			binario.writeBoolean(true);     // 1 byte
			binario.writeUTF("Hola Mundo"); // 2 bytes con la longitud + los caracteres
			binario.writeChar('A');         // 2 bytes
			// Existen también writeLong, writeFloat, writeByte, writeShort...

			System.out.println("Datos escritos correctamente en " + fichero);
		} catch (Exception e) {
			System.err.println("Error: " + e.getMessage());
		}
	}

	public static void leerFichero(String fichero) {
		try (DataInputStream binario = new DataInputStream(new FileInputStream(fichero))) {
			// MUY IMPORTANTE: hay que leer en el MISMO ORDEN y con los MISMOS
			// TIPOS que se escribió. El fichero son solo bytes seguidos: no
			// sabe dónde acaba un dato y empieza otro.
			// Prueba a intercambiar readInt y readDouble: leerás basura,
			// porque estarás interpretando bytes de un tipo como si fueran de otro.
			int entero = binario.readInt();
			double decimal = binario.readDouble();
			boolean bool = binario.readBoolean();
			String texto = binario.readUTF();
			char caracter = binario.readChar();
			// Si intentas leer más allá del final → EOFException (End Of File)

			System.out.println("Datos leídos del fichero:");
			System.out.println("Entero: " + entero);
			System.out.println("Double: " + decimal);
			System.out.println("Booleano: " + bool);
			System.out.println("String: " + texto);
			System.out.println("Char: " + caracter);
		} catch (Exception e) {
			System.err.println("Error: " + e.getMessage());
		}
	}

	// ---------------------------------------------------------
	// OBJETOS (SERIALIZACIÓN)
	// ---------------------------------------------------------
	// SERIALIZAR = convertir un objeto en una secuencia de bytes para guardarlo.
	// DESERIALIZAR = reconstruir el objeto a partir de esos bytes.
	// Requisito: la clase debe implementar Serializable (ver Pokemon.java).
	// Si un atributo es a su vez un objeto (p.ej. evolucion), también tiene
	// que ser Serializable, o dará NotSerializableException.

	public static void guardarPokemon(Pokemon pokemon, String fichero) {
		try (ObjectOutputStream binario = new ObjectOutputStream(new FileOutputStream(fichero))) {
			// Siempre el mismo método, sea el objeto que sea
			binario.writeObject(pokemon);
			System.out.println("Pokemon guardado correctamente en " + fichero);
		} catch (Exception e) {
			System.err.println("Error: " + e.getMessage());
		}
	}

	public static Pokemon recuperarPokemon(String fichero) {
		Pokemon pokemon = null;
		try (ObjectInputStream binario = new ObjectInputStream(new FileInputStream(fichero))) {
			// readObject() devuelve un Object genérico, así que el CAST es obligatorio.
			// Si el fichero contiene otro tipo de objeto → ClassCastException.
			// Si la clase ya no existe o cambió → ClassNotFoundException / InvalidClassException.
			pokemon = (Pokemon) binario.readObject();
			System.out.println("Pokemon recuperado correctamente");
		} catch (Exception e) {
			System.err.println("Error: " + e.getMessage());
		}
		return pokemon;
	}

	public static void guardarListaPokemons(ArrayList<Pokemon> lista, String fichero) {
		// Truco práctico: guardar una LISTA de objetos. La lista se trata como
		// UN SOLO objeto, tenga 1 o 1000 elementos, así que no tenemos que
		// saber cuántos hay al leer. (ArrayList ya es Serializable.)
		try (ObjectOutputStream binario = new ObjectOutputStream(new FileOutputStream(fichero))) {
			binario.writeObject(lista);
			System.out.println("Lista de Pokemons guardada correctamente en " + fichero);
		} catch (Exception e) {
			System.err.println("Error: " + e.getMessage());
		}
	}

	// @SuppressWarnings("unchecked") quita el aviso amarillo de Eclipse en el cast.
	// El aviso sale porque Java no puede comprobar en ejecución que la lista sea
	// realmente de Pokemon (solo sabe que es un ArrayList). Como sabemos lo que
	// guardamos, lo silenciamos.
	@SuppressWarnings("unchecked")
	public static ArrayList<Pokemon> recuperarListaPokemons(String fichero) {
		ArrayList<Pokemon> lista = null;
		try (ObjectInputStream binario = new ObjectInputStream(new FileInputStream(fichero))) {
			// Un solo readObject para toda la lista, y el cast también hace falta
			lista = (ArrayList<Pokemon>) binario.readObject();
			System.out.println("Lista de Pokemons recuperada correctamente");
		} catch (Exception e) {
			System.err.println("Error: " + e.getMessage());
		}
		return lista;
	}
}