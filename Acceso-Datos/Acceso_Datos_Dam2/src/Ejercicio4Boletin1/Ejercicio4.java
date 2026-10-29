package Ejercicio4Boletin1;


// Clases que necesitamos importar
import java.io.BufferedReader;   // lee texto línea a línea (con buffer, más eficiente)
import java.io.BufferedWriter;   // escribe texto en un fichero (con buffer, más eficiente)
import java.io.File;             // para usar File.separator al construir la ruta
import java.io.FileReader;       // abre el fichero en modo lectura de texto
import java.io.FileWriter;       // abre el fichero en modo escritura de texto (lo sobrescribe)
import java.io.IOException;      // error que puede saltar al leer o escribir ficheros
import java.util.ArrayList;      // clase concreta que implementa List (la que creamos con new)
import java.util.List;           // interfaz de lista: el tipo "general" que usamos al declarar


public class Ejercicio4 {

  // Ruta del fichero. File.separator pone "/" o "\" según el sistema operativo.
  // Usamos paises2.csv (una copia) para no estropear el paises.csv original que usa el ejercicio 3.
  // Es "static" para poder usarla desde los métodos estáticos de la clase.
  public static String DIR_FICH = "DAM2" + File.separator + "josemaria" + File.separator + "paises2.csv";

  public static void main(String[] args) {

    // PASO 1: leemos el fichero y nos quedamos en memoria con las líneas ya sin la moneda.
    // Hay que leerlo TODO antes de escribir, porque al abrir el FileWriter el fichero se vacía.
    List<String> lineas = leersinMoneda(DIR_FICH);

    // Si no se ha leído nada (fichero vacío, no encontrado o sin líneas válidas),
    // no escribimos, porque si no el FileWriter dejaría el fichero en blanco.
    if (lineas.isEmpty()) {
      System.out.println("No se ha leído ninguna línea válida, no se modifica el fichero.");
      return;
    }

    // Mostramos por pantalla cada línea de la lista para comprobar que la lectura está bien.
    // Se lee: "por cada String, que llamo l, dentro de lineas..."
    for (String l : lineas) {
      System.out.println(l);
    }

    // PASO 2: sobrescribimos el mismo fichero con las líneas de la lista
    escribirLineas(DIR_FICH, lineas);
  }


  // Lee el fichero y devuelve una lista con las líneas válidas, ya sin la columna de moneda.
  // Recibe la ruta como parámetro para que sirva con cualquier fichero, no solo con DIR_FICH.
  public static List<String> leersinMoneda(String ruta) {

    // Lista donde guardamos las líneas ya transformadas. Empieza vacía.
    // A la izquierda la interfaz (List) y a la derecha la clase concreta (ArrayList).
    List<String> resultado = new ArrayList<>();

    // try-with-resources: el BufferedReader se cierra solo al salir del bloque,
    // haya error o no. Así no hace falta llamar a lc.close().
    try (BufferedReader lc = new BufferedReader(new FileReader(ruta))) {

      String linea; // aquí guardamos cada línea que leemos

      // Lee una línea, la guarda en "linea" y comprueba que no sea null.
      // readLine() devuelve null cuando ya no quedan líneas (fin del fichero).
      while ((linea = lc.readLine()) != null) {

        // Parte la línea por las comas: "Perú,Lima,Sol,Vicuña" -> [Perú, Lima, Sol, Vicuña]
        // El -1 hace que no se pierdan los campos vacíos del final
        // (sin él, "a,b,c," daría 3 trozos en vez de 4).
        String[] partes = linea.split(",", -1);

        // Si no hay exactamente 4 datos, la línea es errónea y no se escribe.
        // "continue" salta a la siguiente vuelta del while sin ejecutar lo de abajo.
        if (partes.length != 4) {
          continue;
        }

        // Juntamos los campos 0, 1 y 3 con comas, saltándonos el 2 (la moneda):
        // [Perú, Lima, Sol, Vicuña] -> "Perú,Lima,Vicuña"
        // La cabecera también tiene 4 campos, así que pierde "Moneda" igual que el resto.
        String nueva = String.join(",", partes[0], partes[1], partes[3]);

        // Guardamos la línea nueva en la lista
        resultado.add(nueva);
      }

    } catch (IOException e) {
      // Si falla la lectura (por ejemplo, el fichero no existe), mostramos el motivo
      System.out.println("Error : " + e);
    }

    // Devolvemos la lista al main (vacía si el fichero no tenía líneas válidas)
    return resultado;
  }


  // Escribe cada línea de la lista en el fichero, sustituyendo lo que hubiera.
  // Es void porque no devuelve nada, solo escribe.
  public static void escribirLineas(String ruta, List<String> lineas) {

    // new FileWriter(ruta) SOBRESCRIBE: vacía el fichero y escribe desde cero.
    // (Con new FileWriter(ruta, true) añadiría al final en vez de sobrescribir).
    // El try-with-resources cierra el BufferedWriter al terminar, y al cerrarse vuelca
    // al disco lo que tenía en el buffer. Si no se cerrara, el fichero podría quedar vacío.
    try (BufferedWriter ec = new BufferedWriter(new FileWriter(ruta))) {

      // Recorremos la lista. Aquí usamos for y no while porque ya tenemos todas las líneas.
      for (String l : lineas) {
        ec.write(l);     // escribe el texto de la línea
        ec.newLine();    // añade el salto de línea (write no lo pone solo)
      }

    } catch (IOException e) {
      // Si falla la escritura (por ejemplo, no hay permisos), mostramos el motivo
      System.out.println("Error : " + e);
    }
  }

}