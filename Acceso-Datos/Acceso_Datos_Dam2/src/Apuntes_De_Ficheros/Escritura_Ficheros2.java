package Apuntes_De_Ficheros;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.List;

/*
 * ============================================================
 *  APUNTES 2 - ESCRITURA DE FICHEROS DE TEXTO
 * ============================================================
 *  Dos modos de escribir:
 *   - SOBRESCRIBIR: si el fichero existe se BORRA su contenido y se escribe
 *     desde cero. Si no existe, se crea.
 *   - AÑADIR (append): se escribe al final de lo que ya había.
 *
 *  | Método       | Clase           | Sobrescribir                  | Añadir                          |
 *  |--------------|-----------------|-------------------------------|---------------------------------|
 *  | escritura1   | FileWriter      | new FileWriter(ruta)          | new FileWriter(ruta, true)      |
 *  | escritura2   | BufferedWriter  | new BufferedWriter(new FW..)  | ...new FileWriter(ruta, true)   |
 *  | escritura3   | PrintWriter     | new PrintWriter(ruta, UTF_8)  | new PrintWriter(new FW(.., true))|
 *  | escritura4   | Files.write     | Files.write(ruta, lista)      | + CREATE, APPEND                |
 *  | escritura5   | Files.writeString| Files.writeString(ruta, txt) | + CREATE, APPEND                |
 *
 *  REGLA DE ORO: cierra SIEMPRE lo que abras (o usa try-with-resources).
 *  Al escribir es aún más importante que al leer: los datos se guardan
 *  primero en memoria (buffer) y NO llegan al disco hasta que se hace
 *  flush() o close(). Si no cierras, puedes perder lo escrito.
 */
public class Escritura_Ficheros2 {

	static final String RUTA = "java.txt";

	public static void main(String[] args) {
		// Cada método sobrescribe el fichero, así que después de ejecutar
		// el main solo verás lo que dejó escritura5.
		// Para ver el resultado de uno concreto, comenta los demás.
		escritura1();
		escritura1bis();
		escritura2();
		escritura3();
		escritura4();
		escritura5();
	}

	public static void escritura1() {
		// --- SOBRESCRIBIR ---
		try {
			// Si no existe se crea. Si existe, se vacía antes de escribir.
			FileWriter escritor = new FileWriter(RUTA);
			// write() NO añade salto de línea: hay que ponerlo con \n
			escritor.write("Hola, mundo con FileWriter!\n");
			escritor.write("Escribiendo una segunda línea.");
			escritor.close(); // aquí es cuando se asegura que llega al disco
			System.out.println("Archivo escrito correctamente.");
		} catch (Exception e) {
			// System.err es la salida de ERRORES (en Eclipse sale en rojo)
			System.err.println("Error al escribir el archivo: " + e.getMessage());
		}

		// --- AÑADIR ---
		try {
			// El segundo parámetro true = append: se escribe al final.
			// Si no existe, se crea igualmente.
			FileWriter escritor = new FileWriter(RUTA, true);
			escritor.write("\nEsta línea se añade al final.");
			// IMPORTANTE: en los apuntes originales faltaba este close().
			// Sin él, esta línea podía no llegar a escribirse nunca.
			escritor.close();
		} catch (Exception e) {
			System.err.println("Error al añadir al archivo: " + e.getMessage());
		}
	}

	public static void escritura1bis() {
		// Igual que escritura1 pero con try-with-resources:
		// el fichero se cierra solo al salir del try (aunque haya error).
		try (FileWriter escritor = new FileWriter(RUTA)) {
			escritor.write("Hola, mundo con FileWriter!\n");
			escritor.write("Escribiendo una segunda línea.");
			// write también acepta un char suelto: escritor.write('A');
			// o un array de char: escritor.write(new char[]{'h','o','l','a'});
			System.out.println("Archivo escrito correctamente.");
		} catch (Exception e) {
			System.err.println("Error al escribir el archivo: " + e.getMessage());
		}

		// Añadir, también con try-with-resources
		try (FileWriter escritor = new FileWriter(RUTA, true)) {
			escritor.write("\nEsta línea se añade al final.");
		} catch (Exception e) {
			System.err.println("Error al añadir al archivo: " + e.getMessage());
		}
	}

	public static void escritura2() {
		// BufferedWriter "envuelve" a un FileWriter y le añade un buffer:
		// acumula lo que escribes en memoria y lo manda al disco en bloques.
		// Es mucho más eficiente si escribes muchas líneas (por ejemplo en un bucle).
		// Precisamente por el buffer, NO cerrar = perder datos. try-with-resources.
		try (BufferedWriter escritor = new BufferedWriter(new FileWriter(RUTA))) {
			escritor.write("Primera línea con Buffer.");
			// newLine() escribe el salto de línea propio del sistema operativo
			// (Windows usa \r\n, Linux y Mac usan \n). Es más portable que "\n".
			escritor.newLine();
			escritor.write("Segunda línea. Es más eficiente.");
			escritor.newLine();
			System.out.println("Archivo escrito eficientemente.");
		} catch (Exception e) {
			System.err.println("Error: " + e.getMessage());
		}

		// Añadir: el true va en el FileWriter, no en el BufferedWriter
		try (BufferedWriter escritor = new BufferedWriter(new FileWriter(RUTA, true))) {
			escritor.write("Última línea.");
			System.out.println("Añadido.");
		} catch (Exception e) {
			System.err.println("Error: " + e.getMessage());
		}
	}

	public static void escritura3() {
		// PrintWriter tiene los mismos métodos que System.out:
		// print, println y printf. Es el más cómodo para escribir con formato.
		// Podemos indicar el juego de caracteres (UTF-8 para tildes y ñ).
		try (PrintWriter escritor = new PrintWriter(RUTA, StandardCharsets.UTF_8)) {
			escritor.println("Línea 1: Hola con PrintWriter."); // con salto
			escritor.print("Línea 2: Esto no tiene salto de línea. "); // sin salto
			escritor.println("Pero esto sí lo añade.");

			// printf: igual que con la consola
			//   %s = String, %d = entero, %.2f = decimal con 2 decimales, %n = salto de línea
			String nombre = "Ana";
			int edad = 30;
			double altura = 1.7589;
			escritor.printf("Usuario: %s, Edad: %d, Altura: %.2f m%n", nombre, edad, altura);
			// Resultado: Usuario: Ana, Edad: 30, Altura: 1,76 m
			// (la coma o el punto decimal depende del idioma del sistema)

			System.out.println("Archivo escrito con formato correctamente.");

			// PARTICULARIDAD: PrintWriter NO lanza excepciones al escribir.
			// Si algo falla, se guarda internamente y lo comprobamos con checkError().
			// (Sí puede lanzar excepción al ABRIR el fichero, por eso sigue el catch.)
			if (escritor.checkError()) {
				System.err.println("Ocurrió un error durante la escritura.");
			}
		} catch (Exception e) {
			System.err.println("Error al abrir/crear el archivo: " + e.getMessage());
		}

		// Para AÑADIR, PrintWriter no tiene constructor con "true" directo,
		// así que envolvemos un FileWriter en modo append.
		try (PrintWriter escritor = new PrintWriter(
				new FileWriter(RUTA, StandardCharsets.UTF_8, true))) {
			escritor.println("Esta línea se añade al final.");
			escritor.printf("Número de línea: %d%n", 42);
			System.out.println("Texto añadido correctamente con PrintWriter");
		} catch (Exception e) {
			System.err.println("Error: " + e.getMessage());
		}
	}

	public static void escritura4() {
		// Escribir una LISTA de una vez: cada elemento es una línea del fichero.
		// Es la operación inversa a Files.readAllLines (Ficheros1.lectura4).
		// Path.of() y Paths.get() hacen lo mismo; Path.of() es la forma moderna.
		Path rutaArchivo = Path.of(RUTA);
		List<String> lineas = new ArrayList<>(List.of("Primera línea", "Segunda línea", "Tercera línea"));
		try {
			// Abre, escribe (una línea por elemento, con su salto) y cierra.
			// Por defecto SOBRESCRIBE.
			Files.write(rutaArchivo, lineas, StandardCharsets.UTF_8);
			System.out.println("Lista escrita en archivo.");
		} catch (Exception e) {
			System.err.println("Error: " + e.getMessage());
		}

		// Para AÑADIR se pasan opciones de apertura (StandardOpenOption):
		//   APPEND → escribe al final
		//   CREATE → crea el fichero si no existe (sin CREATE, APPEND sobre un
		//            fichero inexistente lanza NoSuchFileException)
		List<String> nuevasLineas = new ArrayList<>(List.of("Cuarta línea", "Quinta línea"));
		try {
			Files.write(
					rutaArchivo,
					nuevasLineas,
					StandardCharsets.UTF_8,
					StandardOpenOption.CREATE,
					StandardOpenOption.APPEND);
			System.out.println("Líneas añadidas correctamente");
		} catch (Exception e) {
			System.err.println("Error: " + e.getMessage());
		}
	}

	public static void escritura5() {
		// Igual que escritura4 pero con un String en vez de una lista.
		// Inversa de Files.readString (Ficheros1.lectura5).
		Path rutaArchivo = Path.of(RUTA);
		// Aquí los saltos de línea los ponemos nosotros con \n
		String contenido = "Este es el contenido a escribir.\nSegunda línea.";
		try {
			// Abre, escribe el String y cierra. Sobrescribe.
			Files.writeString(rutaArchivo, contenido, StandardCharsets.UTF_8);
			System.out.println("Archivo escrito con Files.writeString");

			// Añadir: mismas opciones que con listas
			String masContenido = "\nAñadiendo más texto.";
			Files.writeString(rutaArchivo, masContenido, StandardCharsets.UTF_8,
					StandardOpenOption.CREATE, StandardOpenOption.APPEND);
			System.out.println("Texto añadido.");
		} catch (Exception e) {
			System.err.println("Error: " + e.getMessage());
		}
	}
}