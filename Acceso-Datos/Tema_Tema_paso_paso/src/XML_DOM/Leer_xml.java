package XML_DOM;

import java.io.File;                                     // el fichero
import java.util.ArrayList;                              // la lista
import javax.xml.parsers.DocumentBuilder;                // fichero → árbol
import javax.xml.parsers.DocumentBuilderFactory;         // fábrica del builder
import org.w3c.dom.Document;                             // el árbol XML
import org.w3c.dom.Element;   // una etiqueta
import org.w3c.dom.NodeList;                             // lista de etiquetas
import Pokemon_y_Demás_Serializables.Ejercicio_Pokemon;  // tu clase (otro paquete)

public class Leer_xml {

    public static final String DIR_ARCHIVO = "pokemons.xml";   // ruta del XML (constante)

    public static void main(String[] args) {
        ArrayList<Ejercicio_Pokemon> Lpokemon = new ArrayList<>();  // VACÍA: se llena con el XML

        File dir = new File(DIR_ARCHIVO);          // representa el fichero en disco

        if (!dir.exists()) {                       // si NO existe (fíjate en el !)...
            System.out.println("No existe el fichero " + DIR_ARCHIVO);
            return;                                // ...aviso y salgo del main
        }

        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();  // fábrica
            DocumentBuilder builder = factory.newDocumentBuilder();                  // builder
            Document doc = builder.parse(dir);                    // fichero → árbol

            NodeList nodos = doc.getElementsByTagName("pokemon"); // todos los <pokemon>

            for (int i = 0; i < nodos.getLength(); i++) {         // recorro por posición
                Element p = (Element) nodos.item(i);              // el pokémon i

                String numero = p.getAttribute("numero");         // atributo numero="25"
                String nombre = p.getElementsByTagName("nombre").item(0).getTextContent();  // hijo <nombre>
                String tipo   = p.getElementsByTagName("tipo").item(0).getTextContent();    // hijo <tipo>
                int nivel = Integer.parseInt(p.getElementsByTagName("nivel").item(0).getTextContent()); // texto → int

                Lpokemon.add(new Ejercicio_Pokemon(nombre, tipo, nivel));  // creo y guardo
                System.out.println("Nº " + numero + ": " + nombre);        // compruebo
            }
        } catch (Exception e) {                                   // cualquier error al leer
            System.out.println("Error leyendo XML: " + e.getMessage());
        }

        for (Ejercicio_Pokemon p : Lpokemon) System.out.println(p);   // lista final
    }
}