// Ejercicio 3
package Ejercicio3Paises;

// Clases que necesitamos importar
import java.io.BufferedReader;   // lee texto línea a línea (más eficiente que leer carácter a carácter)
import java.io.File;             // representa la ruta de un fichero
import java.io.FileReader;       // abre el fichero para leerlo como texto
import java.io.IOException;      // error que puede ocurrir al leer (fichero no existe, etc.)
import java.util.ArrayList;      // lista dinámica, crece según añadimos elementos

public class Ejercicio3 {

	// Ruta del fichero. File.separator pone "/" o "\" según el sistema operativo,
	// así el programa funciona igual en Windows, Linux y Mac.
	// Es "static" para poder usarla desde cualquier método estático de la clase.
	public static String PAISES = "DAM2" + File.separator + "josemaria" + File.separator + "paises.csv";

	// Punto de entrada del programa: solo llama al método que hace el trabajo
	public static void main(String[] args) {
		sacarColumnas();
	}

	public static void sacarColumnas() {

		// Una lista por cada columna del CSV.
		// La posición i de todas las listas corresponde al mismo país.
		ArrayList<String> paises = new ArrayList<>();
		ArrayList<String> capital = new ArrayList<>();
		ArrayList<String> moneda = new ArrayList<>();
		ArrayList<String> animal = new ArrayList<>();

		// Objeto File con la ruta del fichero
		File archP = new File(PAISES);

		// try-with-resources: el BufferedReader se cierra solo al terminar el bloque,
		// haya error o no. Así no hace falta llamar a lector.close().
		try (BufferedReader lector = new BufferedReader(new FileReader(archP.getPath()))) {

			// Lee la primera línea (la cabecera) y no la guarda en ninguna variable,
			// así se descarta. Si el fichero está vacío devuelve null y no pasa nada.
			lector.readLine();

			String linea;

			// Lee una línea, la guarda en "linea" y comprueba que no sea null.
			// readLine() devuelve null cuando ya no quedan líneas.
			while ((linea = lector.readLine()) != null) {

				// Parte la línea por las comas y guarda cada trozo en un array.
				// El "-1" hace que no se pierdan los campos vacíos del final
				// (sin él, "a,b,c," daría 3 trozos en vez de 4).
				String[] partes = linea.split(",", -1);

				// Si no hay exactamente 4 datos, la línea es errónea.
				// "continue" salta al siguiente ciclo del while sin ejecutar lo de abajo.
				if (partes.length != 4) {
					continue;
				}

				// Guarda cada dato en su lista
				paises.add(partes[0]);
				capital.add(partes[1]);
				moneda.add(partes[2]);
				animal.add(partes[3]);
			}

		} catch (IOException e) {
			// Si falla la lectura, muestra el motivo y sale del método con "return"
			// (no tiene sentido seguir si no se pudo leer el fichero)
			System.out.println("Error : " + e.getMessage());
			return;
		}

		// Si no se guardó ningún país (fichero vacío, solo cabecera o todo erróneo),
		// muestra el mensaje y termina
		if (paises.isEmpty()) {
			System.out.println("No hay datos de ningún país en el fichero");
			return;
		}

		// size() es el número de elementos de la lista = número de países válidos
		System.out.println("Países en el fichero: " + paises.size());

		// Cada línea llama a unir() para formatear la lista como "A, B y C"
		System.out.println("Nombres: " + unir(paises));
		System.out.println("Las capitales de los mismos son: " + unir(capital));
		System.out.println("Sus monedas oficiales son: " + unir(moneda));
		System.out.println("Sus animales más representativos son: " + unir(animal));
	}

	// Recibe una lista y devuelve un texto con los elementos separados por ", "
	// y con " y " antes del último. Se reutiliza para las cuatro columnas.
	public static String unir(ArrayList<String> lista) {

		String resultado = "";   // aquí vamos construyendo el texto final

		for (int i = 0; i < lista.size(); i++) {

			if (i == 0) {
				// Primer elemento: se añade tal cual, sin separador delante
				resultado += lista.get(i);

			} else if (i == lista.size() - 1) {
				// Último elemento (y no es el primero): se une con " y "
				resultado += " y " + lista.get(i);

			} else {
				// Elementos intermedios: se separan con coma y espacio
				resultado += ", " + lista.get(i);
			}
		}

		return resultado;
	}
}