package formatos;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import modelo.Pokemon;

/*
 * XML CON DOM.  Formato:
 *
 * <pokedex>                                   <- RAÍZ  (doc.getDocumentElement())
 *     <pokemon favorito="si">                 <- un elemento por pokémon (atributo opcional)
 *         <nombre>Gyarados</nombre>
 *         <tipo>Agua</tipo>                   <- puede haber VARIOS <tipo>
 *         <tipo>Volador</tipo>
 *         <nivel>40</nivel>
 *     </pokemon>
 * </pokedex>
 *
 * REGLA DE ORO de todas las funciones que cambian algo:
 *     Document doc = leerXML(ruta);    1) leer el árbol
 *     Element p = buscar(doc, nombre); 2) buscar en ESE MISMO doc
 *     ...cambiar...                    3) cambiar el árbol (en memoria)
 *     grabarXML(doc, ruta);            4) guardar (si no, el fichero no cambia)
 */
public class FicheroXML {

    // ================================================================ BÁSICAS
    // Fichero -> árbol en memoria (Document)
    public static Document leerXML(String ruta) throws Exception {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();   // 1. la fábrica
        DocumentBuilder builder = factory.newDocumentBuilder();                   // 2. el constructor
        return builder.parse(new File(ruta));                                    // 3. fichero -> árbol
    }

    // Árbol en memoria -> fichero (con sangría bonita)
    public static void grabarXML(Document doc, String ruta) throws Exception {
        quitarHuecos(doc);                                                        // quita líneas en blanco viejas
        Transformer t = TransformerFactory.newInstance().newTransformer();
        t.setOutputProperty(OutputKeys.INDENT, "yes");                            // con saltos de línea
        t.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "4");    // 4 espacios
        t.transform(new DOMSource(doc), new StreamResult(new File(ruta)));        // QUÉ -> DÓNDE
    }

    // Quita los nodos de texto que solo son espacios/saltos (si no, cada grabado añade líneas en blanco)
    public static void quitarHuecos(Node nodo) {
        NodeList hijos = nodo.getChildNodes();
        for (int i = hijos.getLength() - 1; i >= 0; i--) {             // al revés porque voy borrando
            Node hijo = hijos.item(i);
            if (hijo.getNodeType() == Node.TEXT_NODE && hijo.getTextContent().trim().isEmpty())
                nodo.removeChild(hijo);                                // hueco -> fuera
            else
                quitarHuecos(hijo);                                    // miro dentro (recursivo)
        }
    }

    // Texto de la PRIMERA etiqueta hija:  <nombre>Pikachu</nombre> -> "Pikachu"
    public static String texto(Element padre, String etiqueta) {
        return padre.getElementsByTagName(etiqueta).item(0).getTextContent().trim();
    }

    // Busca el <pokemon> con ese nombre dentro del doc. Devuelve el Element o null.
    public static Element buscar(Document doc, String nombre) {
        NodeList pokemons = doc.getElementsByTagName("pokemon");
        for (int i = 0; i < pokemons.getLength(); i++) {
            Element p = (Element) pokemons.item(i);                    // Node -> Element (cast)
            if (texto(p, "nombre").equalsIgnoreCase(nombre.trim())) return p;
        }
        return null;                                                   // no está
    }

    // Crea un <pokemon> completo a partir de un objeto Pokemon (lo usan añadir y escribir)
    public static Element crearElementoPokemon(Document doc, Pokemon p) {
        Element pok = doc.createElement("pokemon");                    // <pokemon>
        Element nombre = doc.createElement("nombre");                  //   <nombre>
        nombre.setTextContent(p.getNombre());                          //   Pikachu
        pok.appendChild(nombre);                                       //   lo cuelgo de <pokemon>
        for (String t : p.getTipos()) {                                //   un <tipo> por cada tipo
            Element tipo = doc.createElement("tipo");
            tipo.setTextContent(t);
            pok.appendChild(tipo);
        }
        Element nivel = doc.createElement("nivel");
        nivel.setTextContent(String.valueOf(p.getNivel()));            //   el texto siempre es String
        pok.appendChild(nivel);
        return pok;
    }

    // ================================================================ PARA MIGRAR
    // XML -> List<Pokemon>
    public static List<Pokemon> leer(String ruta) {
        List<Pokemon> lista = new ArrayList<>();
        if (!new File(ruta).exists()) { System.out.println("No existe " + ruta); return lista; }
        try {
            Document doc = leerXML(ruta);
            NodeList pokemons = doc.getElementsByTagName("pokemon");
            for (int i = 0; i < pokemons.getLength(); i++) {
                Element p = (Element) pokemons.item(i);
                String nombre = texto(p, "nombre");
                List<String> tipos = new ArrayList<>();
                NodeList nodosTipo = p.getElementsByTagName("tipo");          // TODOS los <tipo>
                for (int j = 0; j < nodosTipo.getLength(); j++) {
                    tipos.add(nodosTipo.item(j).getTextContent().trim());
                }
                int nivel = Integer.parseInt(texto(p, "nivel"));              // texto -> int
                lista.add(new Pokemon(nombre, tipos, nivel));
            }
        } catch (Exception e) {
            System.out.println("Error al leer XML: " + e.getMessage());
        }
        return lista;
    }

    // List<Pokemon> -> XML nuevo (crea el documento DESDE CERO, machaca el fichero)
    public static void escribir(List<Pokemon> lista, String ruta) {
        try {
            Document doc = DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument();  // doc VACÍO
            Element raiz = doc.createElement("pokedex");               // <pokedex>
            doc.appendChild(raiz);                                     // la raíz cuelga del doc
            for (Pokemon p : lista) {
                raiz.appendChild(crearElementoPokemon(doc, p));        // un <pokemon> por objeto
            }
            grabarXML(doc, ruta);
            System.out.println("XML guardado: " + ruta + " (" + lista.size() + " pokémon)");
        } catch (Exception e) {
            System.out.println("Error al escribir XML: " + e.getMessage());
        }
    }

    // ================================================================ CRUD
    // CREAR un XML vacío (<pokedex/>) si no existe
    public static void crearSiNoExiste(String ruta) throws Exception {
        if (new File(ruta).exists()) return;                           // ya está: no hago nada
        escribir(new ArrayList<>(), ruta);                             // lista vacía -> <pokedex/>
    }

    // MOSTRAR todo
    public static void mostrar(String ruta) {
        System.out.println("=== " + ruta + " ===");
        for (Pokemon p : leer(ruta)) System.out.println(p);
    }

    // AÑADIR un pokémon (si no existe)
    public static void anyadir(Pokemon nuevo, String ruta) throws Exception {
        crearSiNoExiste(ruta);
        Document doc = leerXML(ruta);                                  // 1) leer
        if (buscar(doc, nuevo.getNombre()) != null) {                  // 2) ¿ya está?
            System.out.println("Ya existe " + nuevo.getNombre());
            return;
        }
        doc.getDocumentElement().appendChild(crearElementoPokemon(doc, nuevo));   // 3) colgar de la raíz
        grabarXML(doc, ruta);                                          // 4) guardar
        System.out.println("Añadido " + nuevo.getNombre());
    }

    // MODIFICAR el nivel (un dato que solo aparece una vez)
    public static void modificarNivel(String nombre, int nivelNuevo, String ruta) throws Exception {
        Document doc = leerXML(ruta);
        Element p = buscar(doc, nombre);
        if (p == null) { System.out.println("No existe " + nombre); return; }
        p.getElementsByTagName("nivel").item(0).setTextContent(String.valueOf(nivelNuevo));   // cambio el texto
        grabarXML(doc, ruta);
        System.out.println("Nivel de " + nombre + " -> " + nivelNuevo);
    }

    // AÑADIR un valor más a algo repetido (un <tipo> más, como "un teléfono más")
    public static void anyadirTipo(String nombre, String tipoNuevo, String ruta) throws Exception {
        Document doc = leerXML(ruta);
        Element p = buscar(doc, nombre);
        if (p == null) { System.out.println("No existe " + nombre); return; }
        Element tipo = doc.createElement("tipo");
        tipo.setTextContent(tipoNuevo);
        Node nivel = p.getElementsByTagName("nivel").item(0);
        p.insertBefore(tipo, nivel);                                   // lo pongo ANTES de <nivel> (junto a los otros tipos)
        grabarXML(doc, ruta);
        System.out.println("Tipo " + tipoNuevo + " añadido a " + nombre);
    }

    // MODIFICAR un valor CONCRETO de algo repetido (cambiar el tipo "Agua" por "Hielo")
    public static void modificarTipo(String nombre, String tipoViejo, String tipoNuevo, String ruta) throws Exception {
        Document doc = leerXML(ruta);
        Element p = buscar(doc, nombre);
        if (p == null) { System.out.println("No existe " + nombre); return; }
        NodeList tipos = p.getElementsByTagName("tipo");
        for (int i = 0; i < tipos.getLength(); i++) {
            if (tipos.item(i).getTextContent().trim().equalsIgnoreCase(tipoViejo)) {
                tipos.item(i).setTextContent(tipoNuevo);               // cambio SOLO ese
                grabarXML(doc, ruta);
                System.out.println("Tipo " + tipoViejo + " -> " + tipoNuevo + " en " + nombre);
                return;
            }
        }
        System.out.println(nombre + " no tiene el tipo " + tipoViejo);
    }

    // ELIMINAR algo CONCRETO dentro de un elemento (quitar el tipo "Volador" de Gyarados)
    public static void eliminarTipo(String nombre, String tipoBorrar, String ruta) throws Exception {
        Document doc = leerXML(ruta);
        Element p = buscar(doc, nombre);
        if (p == null) { System.out.println("No existe " + nombre); return; }
        NodeList tipos = p.getElementsByTagName("tipo");
        for (int i = 0; i < tipos.getLength(); i++) {
            Node t = tipos.item(i);
            if (t.getTextContent().trim().equalsIgnoreCase(tipoBorrar)) {
                p.removeChild(t);                                      // el PADRE (<pokemon>) quita al hijo
                grabarXML(doc, ruta);
                System.out.println("Tipo " + tipoBorrar + " quitado de " + nombre);
                return;
            }
        }
        System.out.println(nombre + " no tiene el tipo " + tipoBorrar);
    }

    // ELIMINAR un elemento entero (un <pokemon>)
    public static void eliminar(String nombre, String ruta) throws Exception {
        Document doc = leerXML(ruta);
        Element p = buscar(doc, nombre);
        if (p == null) { System.out.println("No existe " + nombre); return; }
        p.getParentNode().removeChild(p);                              // su padre (<pokedex>) lo quita
        grabarXML(doc, ruta);
        System.out.println("Eliminado " + nombre);
    }

    // ATRIBUTOS: <pokemon favorito="si">  (poner / leer / quitar)
    public static void marcarFavorito(String nombre, boolean favorito, String ruta) throws Exception {
        Document doc = leerXML(ruta);
        Element p = buscar(doc, nombre);
        if (p == null) { System.out.println("No existe " + nombre); return; }
        if (favorito) p.setAttribute("favorito", "si");                // pone o cambia el atributo
        else p.removeAttribute("favorito");                            // lo quita
        grabarXML(doc, ruta);
        System.out.println(nombre + " favorito = " + (p.hasAttribute("favorito") ? p.getAttribute("favorito") : "no"));
    }

    public static void main(String[] args) throws Exception {
        String ruta = "datos/pruebaXML.xml";
        escribir(FicheroTexto.leer("datos/pokemons.txt"), ruta);       // creo el XML desde el TXT
        anyadir(new Pokemon("Lapras", List.of("Agua", "Hielo"), 30), ruta);
        anyadir(new Pokemon("Lapras", List.of("Agua"), 30), ruta);     // -> ya existe
        modificarNivel("Pikachu", 50, ruta);
        anyadirTipo("Pikachu", "Acero", ruta);
        modificarTipo("Pikachu", "Acero", "Volador", ruta);
        eliminarTipo("Gyarados", "Volador", ruta);
        marcarFavorito("Charmander", true, ruta);
        eliminar("Bulbasaur", ruta);
        eliminar("Mewtwo", ruta);                                      // -> no existe
        mostrar(ruta);
    }
}
