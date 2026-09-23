package Apuntes_De_Ficheros;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

/*
 * ============================================================
 *  APUNTES 1 - LECTURA DE FICHEROS DE TEXTO
 * ============================================================
 *  Resumen de las formas de leer un fichero de texto:
 *
 *  | Método       | Clases usadas                 | Cómo lee           | ¿Cierra solo? |
 *  |--------------|-------------------------------|--------------------|---------------|
 *  | lectura1     | FileReader + BufferedReader   | línea a línea      | No            |
 *  | lectura2     | FileReader + BufferedReader   | línea a línea      | No            |
 *  | lectura2bis  | try-with-resources            | línea a línea      | Sí            |
 *  | lectura3     | File + Scanner                | línea a línea      | No            |
 *  | lectura4     | Path + Files.readAllLines     | todo → List        | Sí            |
 *  | lectura5     | Path + Files.readString       | todo → String      | Sí            |
 *
 *  ¿Cuál uso?
 *   - Fichero pequeño y quiero todo de golpe  → lectura4 o lectura5.
 *   - Fichero grande (no cabe cómodo en memoria) → lectura2bis (lee poco a poco).
 *   - Necesito trocear la línea en números, palabras... → Scanner (lectura3).
 *
 *  NOTA SOBRE LA RUTA:
 *   Una ruta relativa ("quijote.txt") se busca en la carpeta desde la que se
 *   ejecuta el programa. En Eclipse/IntelliJ es la RAÍZ DEL PROYECTO
 *   (al mismo nivel que la carpeta src, no dentro de ella).
 *   Crea ahí un quijote.txt con unas cuantas líneas para probar.
 */
public class Ficheros1_Lectura {

	// Constante con la ruta: si cambia, solo la tocamos aquí
	static final String RUTA = "quijote.txt";

	public static void main(String[] args) {
		lectura1();    // do-while clásico
		lectura2();    // while compacto
		lectura2bis(); // try-with-resources: se cierra solo
		lectura3();    // Scanner
		lectura4();    // todo el fichero a una lista
		lectura5();    // todo el fichero a un String
	}

	public static void lectura1() {
		System.out.println("===== lectura1 =====");
		// Tratar las excepciones es OBLIGATORIO al trabajar con ficheros:
		// son excepciones "checked" (el compilador te obliga a capturarlas
		// o a declararlas con throws).
		try {
			// FileReader representa al fichero. Recibe el nombre o la ruta.
			// Si el fichero no existe lanza FileNotFoundException.
			FileReader fichero = new FileReader(RUTA);

			// BufferedReader es como un dedo que señala la siguiente línea a leer.
			// Además usa un "buffer": lee del disco trozos grandes de golpe
			// y luego te los va dando, lo cual es mucho más rápido que ir
			// carácter a carácter al disco.
			BufferedReader lector = new BufferedReader(fichero);
			String linea;

			// do-while porque al menos hay que leer una vez
			do {
				// readLine() devuelve la línea SIN el salto de línea final (\n),
				// o null cuando ya no quedan líneas (fin de fichero).
				linea = lector.readLine();
				if (linea != null)
					System.out.println(linea);
			} while (linea != null); // null = hemos llegado al final

			// Cerrar libera el fichero para el sistema operativo.
			// Cerrar el BufferedReader cierra también el FileReader que lleva dentro.
			lector.close();

		// Excepciones concretas posibles: FileNotFoundException (no existe),
		// IOException (error al leer). Exception las captura todas.
		// getMessage() nos dice qué ha pasado exactamente.
		} catch (Exception e) {
			System.out.println("Error al leer: " + e.getMessage());
		}
	}

	public static void lectura2() {
		System.out.println("===== lectura2 =====");
		// Igual que lectura1 pero más compacto
		try {
			// Creamos los dos objetos en una sola línea
			BufferedReader lector = new BufferedReader(new FileReader(RUTA));
			String linea;

			// Truco muy habitual: leemos DENTRO de la condición.
			// (linea = lector.readLine()) asigna Y devuelve el valor asignado,
			// y ese valor es lo que se compara con null.
			while ((linea = lector.readLine()) != null) {
				System.out.println(linea);
			}
			lector.close();
			// PEGA: si readLine() lanza una excepción, saltamos al catch
			// y close() NUNCA se ejecuta. Por eso existe lectura2bis.
		} catch (Exception e) {
			System.out.println("Error al leer: " + e.getMessage());
		}
	}

	public static void lectura2bis() {
		System.out.println("===== lectura2bis =====");
		// TRY-WITH-RESOURCES: lo que se declara entre los paréntesis del try
		// se cierra AUTOMÁTICAMENTE al salir del try, tanto si todo va bien
		// como si salta una excepción. Es la forma recomendada.
		// (Funciona con cualquier clase que implemente AutoCloseable.)
		try (BufferedReader lector = new BufferedReader(new FileReader(RUTA))) {
			String linea;
			while ((linea = lector.readLine()) != null) {
				System.out.println(linea);
			}
		} catch (Exception e) {
			System.out.println("Error al leer: " + e.getMessage());
		}
	}

	public static void lectura3() {
		System.out.println("===== lectura3 =====");
		try {
			// Scanner: el mismo que usamos con el teclado (new Scanner(System.in)),
			// pero ahora le pasamos un File. File representa al fichero.
			File fichero = new File(RUTA);
			Scanner scanner = new Scanner(fichero);

			// hasNextLine() → true mientras queden líneas, false al final
			while (scanner.hasNextLine()) {
				// nextLine() lee una línea completa, también sin el \n
				String linea = scanner.nextLine();
				System.out.println(linea);
			}
			// VENTAJA de Scanner: tiene nextInt(), nextDouble(), next()...
			// muy útil si el fichero tiene datos que hay que interpretar.
			// DESVENTAJA: es más lento que BufferedReader en ficheros grandes.
			scanner.close();
		} catch (Exception e) {
			System.out.println("Error al leer: " + e.getMessage());
		}
	}

	public static void lectura4() {
		System.out.println("===== lectura4 =====");
		// Leemos el fichero ENTERO de una vez: cada elemento de la lista es una línea.
		// Path representa la ruta. Crearlo no toca el disco, así que no lanza
		// excepción y puede ir fuera del try.
		Path ruta = Path.of(RUTA);
		List<String> lineas = null;
		try {
			// readAllLines abre, lee todo, CIERRA el fichero y devuelve un List<String>.
			// Los saltos de línea se eliminan.
			lineas = Files.readAllLines(ruta);

			// ¿Y si quiero un ArrayList? NO hagas un cast
			// (ArrayList<String>) Files.readAllLines(ruta), porque Java no
			// garantiza que la lista devuelta sea un ArrayList. Lo seguro es:
			ArrayList<String> copia = new ArrayList<>(lineas);
			System.out.println("(El fichero tiene " + copia.size() + " líneas)");

			// List es una INTERFAZ (lo que se puede hacer: add, get, size...)
			// y ArrayList es una CLASE que la implementa. Por eso un ArrayList
			// "es" un List, pero un List no tiene por qué ser un ArrayList.
		} catch (Exception e) {
			System.out.println("Error al leer: " + e.getMessage());
		}

		// Procesamos la lista con un for-each
		if (lineas != null)
			for (String linea : lineas)
				System.out.println(linea);
	}

	public static void lectura5() {
		System.out.println("===== lectura5 =====");
		// Leemos TODO el fichero como un único String
		Path ruta = Path.of(RUTA);
		String contenido = null;
		try {
			// OJO: aquí los saltos de línea SÍ se conservan.
			// Si el fichero fuera:
			//   uno
			//   dos
			// contenido sería "uno\ndos\n"
			// \n es UN solo carácter (salto de línea), aunque lo escribamos con dos.
			contenido = Files.readString(ruta);

			// Si luego quisieras separarlo en líneas:
			// String[] partes = contenido.split("\n");
		} catch (Exception e) {
			System.out.println("Error al leer: " + e.getMessage());
		}
		// print (no println) porque el propio contenido ya lleva sus saltos
		if (contenido != null)
			System.out.print(contenido);
	}
}