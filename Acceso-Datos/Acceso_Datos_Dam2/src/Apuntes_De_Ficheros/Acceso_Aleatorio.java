package Apuntes_De_Ficheros;

import java.io.RandomAccessFile;
import java.util.LinkedHashMap;

/*
 * ============================================================
 *  APUNTES 4 - ACCESO ALEATORIO (RandomAccessFile)
 * ============================================================
 *  Acceso SECUENCIAL (Ficheros 1-3): para llegar al dato 500 hay que leer
 *  los 499 anteriores, como una cinta de cassette.
 *  Acceso ALEATORIO (directo): saltamos directamente a la posición que
 *  queremos, como elegir una canción en un CD.
 *
 *  CLAVE: para poder calcular dónde está cada dato, todos los REGISTROS
 *  deben tener el MISMO TAMAÑO en bytes.
 *
 *  Nuestro registro:  [ nombre: 20 chars = 40 bytes ][ edad: int = 4 bytes ]  = 44 bytes
 *
 *     Registro 1        Registro 2        Registro 3
 *  |--------44--------|--------44--------|--------44--------|
 *  0                  44                 88                132   ← posición (byte)
 *
 *  Posición del registro N = (N - 1) * TAMANYO_REGISTRO
 *
 *  Métodos clave de RandomAccessFile:
 *   - seek(pos)       → mueve el "cursor" al byte pos (contando desde 0)
 *   - getFilePointer()→ dice en qué byte está el cursor ahora
 *   - length()        → tamaño total del fichero en bytes
 *   - setLength(n)    → cambia el tamaño (setLength(0) lo vacía)
 *   - readInt/writeInt, readChar/writeChar... → como DataInput/OutputStream
 *   Cada lectura o escritura AVANZA el cursor lo que ocupe el dato.
 *
 *  Modos de apertura:
 *   - "r"   → solo lectura. Excepción si el fichero no existe.
 *   - "rw"  → lectura y escritura. Crea el fichero si no existe.
 *             OJO: NO lo vacía si ya existe (distinto de FileWriter).
 *   - "rws"/"rwd" → como rw pero escribe directamente a disco, sin caché.
 *   (No existe modo "w".)
 */
public class Acceso_Aleatorio {

	// Las variables globales se evitan, pero las CONSTANTES globales son muy útiles:
	// si cambia un tamaño, se toca solo aquí.
	// static → pertenece a la clase, no a cada objeto
	// final  → no se puede modificar (constante). Por convenio, en MAYÚSCULAS.
	static final int TAMANYO_NOMBRE = 20; // caracteres. Cada char ocupa 2 bytes
	static final int TAMANYO_EDAD = 4;    // bytes. Un int ocupa 4 bytes
	static final int TAMANYO_REGISTRO = TAMANYO_NOMBRE * 2 + TAMANYO_EDAD; // 44 bytes

	public static void main(String[] args) {
		// Pequeña agenda: nombre → edad
		String fichero = "registros.dat";

		// LinkedHashMap en vez de HashMap: HashMap NO respeta el orden de inserción,
		// así que no sabríamos qué persona es "el registro 2".
		// LinkedHashMap sí mantiene el orden en que se añadieron.
		LinkedHashMap<String, Integer> agenda = new LinkedHashMap<>();
		agenda.put("Isabel", 35);     // registro 1
		agenda.put("Marcos", 51);     // registro 2
		agenda.put("José María", 57); // registro 3
		agenda.put("Luis", 23);       // registro 4

		// Todas las excepciones se capturan AQUÍ (ver explicación más abajo)
		try {
			crearRegistro(fichero, agenda);

			leerRegistro(fichero, 2);                        // Marcos
			modificarRegistro(fichero, 2, "José Miguel", 56);
			modificarRegistro(fichero, 200, "Luis Miguel", 31); // no existe
			leerRegistro(fichero, 2);                        // ahora José Miguel
			leerRegistro(fichero, 500);                      // no existe

			leerTodosLosRegistros(fichero);

			anyadeRegistro(fichero, "Armando", 35);          // pasa a ser el registro 5
			leerRegistro(fichero, 5);

			borrarRegistro(fichero, 3);
			// Intentamos operar con un registro marcado como borrado
			borrarRegistro(fichero, 3);
			leerRegistro(fichero, 3);
			modificarRegistro(fichero, 3, "José Miguel", 56);

			leerTodosLosRegistros(fichero); // el 3 ya no aparece
		} catch (Exception e) {
			System.out.println("Error: " + e.getMessage());
		}
	}

	/*
	 * OTRA FORMA DE TRATAR EXCEPCIONES: throws
	 * En vez de poner try-catch en cada método, añadimos "throws Exception"
	 * a la cabecera. Si ocurre un error, el método se interrumpe y la excepción
	 * "sube" al método que lo llamó (aquí, el main), donde está el catch.
	 * Nota: aquí seguimos usando try(...) pero SIN catch, solo para que el
	 * fichero se cierre solo.
	 */
	public static void crearRegistro(String fichero, LinkedHashMap<String, Integer> agenda) throws Exception {
		try (RandomAccessFile raf = new RandomAccessFile(fichero, "rw")) {
			// "rw" no vacía el fichero, así que si ejecutas el programa dos veces
			// quedarían registros viejos (incluidos los borrados). Lo vaciamos:
			raf.setLength(0);

			// keySet() devuelve las claves (nombres) del mapa
			for (String nombre : agenda.keySet()) {
				escribirNombre(raf, nombre);       // 40 bytes
				raf.writeInt(agenda.get(nombre));  // 4 bytes
			}
			System.out.println("Archivo creado con " + agenda.size() + " registros");
			// length() nos sirve de comprobación: 4 registros * 44 = 176 bytes
			System.out.println("Tamaño total del archivo: " + raf.length() + " bytes");
		}
	}

	// Escribe un nombre SIEMPRE con TAMANYO_NOMBRE caracteres:
	// si es más corto se rellena con espacios, si es más largo se CORTA.
	// "Luis" → "Luis                " (4 letras + 16 espacios)
	private static void escribirNombre(RandomAccessFile raf, String nombre) throws Exception {
		char[] chars = new char[TAMANYO_NOMBRE];
		for (int i = 0; i < TAMANYO_NOMBRE; i++) {
			if (i < nombre.length())
				chars[i] = nombre.charAt(i);
			else
				chars[i] = ' '; // relleno
		}
		// Escribimos cada carácter (2 bytes cada uno)
		for (char c : chars)
			raf.writeChar(c);
		// Alternativa más corta con el mismo efecto:
		// raf.writeChars(String.format("%-20s", nombre).substring(0, TAMANYO_NOMBRE));
	}

	// Lee TAMANYO_NOMBRE caracteres desde la posición actual del cursor
	private static String leerNombre(RandomAccessFile raf) throws Exception {
		// StringBuilder es más eficiente que ir haciendo nombre = nombre + c,
		// porque los String son inmutables y cada + crea un String nuevo.
		StringBuilder nombre = new StringBuilder();
		for (int i = 0; i < TAMANYO_NOMBRE; i++)
			nombre.append(raf.readChar());
		return nombre.toString().trim(); // trim() quita los espacios de relleno
	}

	private static void leerRegistro(String fichero, int registro) throws Exception {
		try (RandomAccessFile raf = new RandomAccessFile(fichero, "r")) {
			// El registro 1 empieza en el byte 0, el 2 en el 44, etc.
			long offset = (long) (registro - 1) * TAMANYO_REGISTRO;

			// Si el offset está fuera del fichero, ese registro no existe
			// (también comprobamos que no sea un número negativo o cero)
			if (registro < 1 || offset >= raf.length()) {
				System.out.println("No existe el registro " + registro);
				System.out.println("El registro más alto es el " + raf.length() / TAMANYO_REGISTRO);
			} else {
				raf.seek(offset); // saltamos directamente al registro
				String nombre = leerNombre(raf); // el cursor avanza 40 bytes
				if (nombre.charAt(0) == '*')
					System.out.println("El registro " + registro + " está marcado para ser eliminado");
				else {
					int edad = raf.readInt(); // el cursor avanza 4 bytes más
					System.out.printf("Registro %d: '%s', %d años%n", registro, nombre, edad);
				}
			}
		}
	}

	private static void modificarRegistro(String fichero, int registro, String nombreNuevo, int edadNueva)
			throws Exception {
		try (RandomAccessFile raf = new RandomAccessFile(fichero, "rw")) {
			long offset = (long) (registro - 1) * TAMANYO_REGISTRO;
			if (registro < 1 || offset >= raf.length()) {
				System.out.println("No existe el registro " + registro);
				System.out.println("El registro más alto es el " + raf.length() / TAMANYO_REGISTRO);
			} else {
				raf.seek(offset);
				String nombre = leerNombre(raf); // ¿está borrado?
				if (nombre.charAt(0) != '*') {
					// Leer el nombre movió el cursor 40 bytes: hay que VOLVER
					// al inicio del registro antes de sobrescribirlo
					raf.seek(offset);
					escribirNombre(raf, nombreNuevo);
					raf.writeInt(edadNueva);
					// Como el tamaño es fijo, sobrescribimos exactamente
					// los 44 bytes del registro sin tocar los demás
					System.out.println("Registro " + registro + " modificado");
				} else
					System.out.println("El registro " + registro
							+ " no puede ser modificado porque está marcado para ser eliminado");
			}
		}
	}

	private static void leerTodosLosRegistros(String fichero) throws Exception {
		System.out.println("--- Todos los registros ---");
		try (RandomAccessFile raf = new RandomAccessFile(fichero, "r")) {
			// Nº de registros = tamaño del fichero / tamaño de un registro
			int numRegistros = (int) (raf.length() / TAMANYO_REGISTRO);
			// Aquí no hace falta seek: empezamos en 0 y cada lectura
			// deja el cursor justo al principio del siguiente registro
			for (int i = 0; i < numRegistros; i++) {
				String nombre = leerNombre(raf);
				// Leemos la edad AUNQUE esté borrado, para que el cursor
				// avance y quede bien colocado para el siguiente registro
				int edad = raf.readInt();
				if (nombre.charAt(0) != '*')
					System.out.printf("Registro %d: '%s', %d años%n", i + 1, nombre, edad);
			}
		}
	}

	private static void anyadeRegistro(String fichero, String nombre, int edad) throws Exception {
		try (RandomAccessFile raf = new RandomAccessFile(fichero, "rw")) {
			// Nos colocamos al final del fichero y escribimos ahí
			raf.seek(raf.length());
			escribirNombre(raf, nombre);
			raf.writeInt(edad);
			System.out.println("Registro añadido");
		}
	}

	/*
	 * BORRADO LÓGICO: no eliminamos los bytes del fichero (habría que mover
	 * todos los registros siguientes y cambiarían sus números). En su lugar
	 * MARCAMOS el registro poniendo un '*' como primer carácter del nombre.
	 * El resto de métodos comprueban esa marca y lo ignoran.
	 * (Borrado FÍSICO sería reescribir el fichero sin esos registros.)
	 */
	private static void borrarRegistro(String fichero, int registro) throws Exception {
		try (RandomAccessFile raf = new RandomAccessFile(fichero, "rw")) {
			long offset = (long) (registro - 1) * TAMANYO_REGISTRO;
			if (registro < 1 || offset >= raf.length()) {
				System.out.println("No existe el registro " + registro);
				System.out.println("El registro más alto es el " + raf.length() / TAMANYO_REGISTRO);
			} else {
				raf.seek(offset);
				String nombre = leerNombre(raf);
				if (nombre.charAt(0) == '*')
					System.out.println("El registro " + registro + " ya había sido borrado");
				else {
					// Sustituimos la primera letra por '*': "Luis" → "*uis"
					nombre = '*' + nombre.substring(1);
					raf.seek(offset);
					escribirNombre(raf, nombre);
					// Más eficiente: bastaría con raf.seek(offset); raf.writeChar('*');
					System.out.println("Registro " + registro + " eliminado con éxito");
				}
			}
		}
	}
}