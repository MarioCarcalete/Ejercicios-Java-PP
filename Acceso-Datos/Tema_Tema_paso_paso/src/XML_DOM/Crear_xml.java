package XML_DOM;
 
/*
 * ======================================================================
 *  POKEDEX XML  ·  TODO LO DE XML EN UNA CLASE
 * ======================================================================
 *  Estructura copiada de AgendaXML_limpio (profe):
 *     1) MÉTODOS CLAVE          → leerXML, grabarXML (+ quitarHuecos)
 *     2) FUNCIÓN PARA ENCONTRAR → existePokemon (= existeContacto del profe)
 *     3) MÉTODOS DE FUNCIONALIDADES:
 *          · DEL PROFE:  mostrarPokedex, buscarPokemonPorNombre, existePokemonBoolean,
 *                        aniadirPokemon, modificarNivel, eliminarPokemon
 *          · ETIQUETAS DUPLICADAS (varios <tipo>) + ATRIBUTOS: mostrarConTipos
 *          · NUEVO (por si cae): crearXML, nuevoTipo, modificarTipo, eliminarTipo,
 *                        cambiarAtributo, quitarAtributo, nuevoCampo, eliminarCampo,
 *                        modificarNombre, leerPokedex_crearObjetos (para migrar a JSON)
 *
 *  El XML con el que trabaja (se crea solo con crearXML):
 *
 *  <pokedex>                               ← RAÍZ (doc.getDocumentElement())
 *      <pokemon numero="25">               ← elemento con ATRIBUTO numero
 *          <nombre>Pikachu</nombre>        ← hijo con TEXTO
 *          <tipo>Eléctrico</tipo>          ← puede haber VARIOS <tipo>
 *          <nivel>25</nivel>
 *      </pokemon>
 *  </pokedex>
 *
 *  LA REGLA DE TODAS LAS FUNCIONES QUE CAMBIAN ALGO:
 *     leerXML  →  existePokemon  →  (cambio)  →  grabarXML
 *     Sin grabarXML el cambio solo está en MEMORIA y no se guarda en el fichero.
 *
 *  QUIÉN BORRA A QUIÉN:  siempre el PADRE borra al hijo
 *     raiz.removeChild(pokemon)  → borra un pokémon ENTERO
 *     pokemon.removeChild(tipo)  → borra algo de DENTRO del pokémon
 * ======================================================================
 */
 
import java.io.File;                                     // representa un fichero del disco
import java.util.ArrayList;                              // lista para leerPokedex_crearObjetos
import java.util.List;                                   // tipo List
import javax.xml.parsers.DocumentBuilder;                // "obrero" que crea/lee documentos XML
import javax.xml.parsers.DocumentBuilderFactory;         // "fábrica" que nos da el obrero
import javax.xml.transform.OutputKeys;                   // opciones de salida (sangría...)
import javax.xml.transform.Transformer;                  // pasa el árbol (memoria) al fichero
import javax.xml.transform.TransformerFactory;           // fábrica del Transformer
import javax.xml.transform.dom.DOMSource;                // QUÉ guardo: el árbol
import javax.xml.transform.stream.StreamResult;          // DÓNDE lo guardo: el fichero
import org.w3c.dom.Document;                             // el XML entero (OJO: org.w3c.dom, NO javax.swing)
import org.w3c.dom.Element;                              // una etiqueta: <pokemon>, <nombre>...
import org.w3c.dom.Node;                                 // cualquier nodo (etiqueta, texto, hueco...)
import org.w3c.dom.NodeList;                             // lista de nodos (se recorre con for + item(i))
import Pokemon_y_Demás_Serializables.Ejercicio_Pokemon;  // tu clase (para pasar del XML a objetos)
 
public class Crear_xml {
 
    // Constante con la ruta del XML (en la raíz del proyecto)
    public static final String FICHERO = "pokedex.xml";
 
    // ======================================================================
    // MAIN: como el del profe, SOLO llama a las funciones
    // throws Exception → los errores de las funciones suben hasta aquí
    // ======================================================================
    public static void main(String[] args) throws Exception {
 
        // ---------- CREAR Y RELLENAR ----------
        crearXML(FICHERO);                                    // XML nuevo con solo <pokedex>
        aniadirPokemon(FICHERO, "Pikachu", "Eléctrico", 25);  // añade pokémon
        aniadirPokemon(FICHERO, "Charizard", "Fuego", 50);
        aniadirPokemon(FICHERO, "Mewtwo", "Psíquico", 75);
        aniadirPokemon(FICHERO, "Pikachu", "Eléctrico", 30);  // repetido → no lo añade
        mostrarPokedex(FICHERO);                              // muestra todos
 
        // ---------- BUSCAR ----------
        buscarPokemonPorNombre(FICHERO, "Charizard");         // existe → muestra su nivel
        buscarPokemonPorNombre(FICHERO, "Mew");               // no existe
        existePokemonBoolean(FICHERO, "mewtwo");              // versión con boolean (XML_Ejercicios)
 
        // ---------- MODIFICAR (profe) ----------
        modificarNivel(FICHERO, "Pikachu", 100);
 
        // ---------- ETIQUETAS DUPLICADAS Y ATRIBUTOS (nuevo) ----------
        nuevoTipo(FICHERO, "Charizard", "Volador");           // Charizard tiene ahora 2 tipos
        nuevoTipo(FICHERO, "Charizard", "Volador");           // repetido → no lo añade
        modificarTipo(FICHERO, "Mewtwo", "Psíquico", "Lucha");// cambia UN tipo concreto
        cambiarAtributo(FICHERO, "Mewtwo", "numero", "150");  // cambia numero="3" → "150"
        cambiarAtributo(FICHERO, "Pikachu", "shiny", "si");   // crea un atributo nuevo
        mostrarConTipos(FICHERO);                             // como EtiquetasDuplicadas
 
        eliminarTipo(FICHERO, "Charizard", "Fuego");          // borra UN tipo (se queda Volador)
        quitarAtributo(FICHERO, "Pikachu", "shiny");          // quita el atributo
        nuevoCampo(FICHERO, "Pikachu", "region", "Kanto");    // etiqueta nueva <region>
        nuevoCampo(FICHERO, "Pikachu", "region", "Johto");    // ya la tiene → la modifica
        eliminarCampo(FICHERO, "Pikachu", "region");          // la borra
        modificarNombre(FICHERO, "Pikachu", "Raichu");        // cambia el nombre
 
        // ---------- ELIMINAR (profe) ----------
        eliminarPokemon(FICHERO, "Mewtwo");
        eliminarPokemon(FICHERO, "Mewtwo");                   // ya no existe → avisa
        mostrarConTipos(FICHERO);
 
        // ---------- XML → LISTA DE OBJETOS (paso 1 para migrar a JSON) ----------
        List<Ejercicio_Pokemon> lista = leerPokedex_crearObjetos(FICHERO);
        System.out.println("Objetos creados desde el XML:");
        for (Ejercicio_Pokemon p : lista) System.out.println("   " + p);
    }
 
    // ======================================================================
    // 1) MÉTODOS CLAVE  (iguales que los del profe: cópialos SIEMPRE)
    // ======================================================================
 
    // ---------- leerXML: abre el fichero y devuelve el árbol en memoria ----------
    // Recibe: la ruta del fichero.  Devuelve: Document (el XML entero)
    public static Document leerXML(String fichero) throws Exception {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();  // 1. la fábrica
        DocumentBuilder builder = factory.newDocumentBuilder();                  // 2. el obrero
        return builder.parse(fichero);                                          // 3. fichero → árbol
    }
 
    // ---------- grabarXML: guarda el árbol de memoria en el fichero ----------
    // Hace falta después de CUALQUIER cambio: añadir, modificar o eliminar se
    // hacen sobre la copia en memoria, no sobre el fichero.
    public static void grabarXML(Document doc, String fichero) throws Exception {
        quitarHuecos(doc);                                                         // (extra) quita líneas en blanco
        TransformerFactory transformerFactory = TransformerFactory.newInstance();  // fábrica
        Transformer transformer = transformerFactory.newTransformer();             // el transformer
        transformer.setOutputProperty(OutputKeys.INDENT, "yes");                   // con saltos de línea
        transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "4");  // y 4 espacios
        DOMSource source = new DOMSource(doc);                                     // QUÉ guardo
        StreamResult result = new StreamResult(new File(fichero));                 // DÓNDE lo guardo
        transformer.transform(source, result);                                     // ¡se escribe el fichero!
    }
 
    // ---------- quitarHuecos (EXTRA, no está en el del profe) ----------
    // Al abrir y guardar varias veces aparecen líneas en blanco de más (le pasa
    // a la agenda.xml de clase). Borra SOLO los textos que son espacios/saltos:
    // las etiquetas y los datos NO se tocan.
    public static void quitarHuecos(Node nodo) {
        NodeList hijos = nodo.getChildNodes();                    // todos los hijos del nodo
        for (int i = hijos.getLength() - 1; i >= 0; i--) {        // al REVÉS, porque voy borrando
            Node hijo = hijos.item(i);
            if (hijo.getNodeType() == Node.TEXT_NODE              // si es un texto...
                    && hijo.getTextContent().trim().isEmpty())    // ...vacío al quitarle espacios
                nodo.removeChild(hijo);                           // es un hueco → fuera
            else
                quitarHuecos(hijo);                               // si no, miro dentro (recursivo)
        }
    }
 
    // ======================================================================
    // 2) FUNCIÓN PARA ENCONTRAR UN POKÉMON  (= existeContacto del profe)
    // Recibe: el nombre y el doc YA ABIERTO (¡el mismo doc que luego modificas!)
    // Devuelve: el <pokemon> (Element) si está, o null si no está
    // ======================================================================
    public static Element existePokemon(String nombrePokemon, Document doc) {
        Element respuesta = null;                                       // de momento no lo he encontrado
        NodeList listaPokemon = doc.getElementsByTagName("pokemon");    // todos los <pokemon>
        for (int i = 0; i < listaPokemon.getLength() && respuesta == null; i++) {  // paro al encontrarlo
            Node nodo = listaPokemon.item(i);                           // el nodo i
            Element pokemon = (Element) nodo;                           // cast a Element para buscar dentro
            String nombre = pokemon.getElementsByTagName("nombre").item(0).getTextContent();  // su nombre
            if (nombre.equalsIgnoreCase(nombrePokemon))                 // ¿es este? (sin mirar mayúsculas)
                respuesta = pokemon;                                    // sí → lo guardo
        }
        return respuesta;                                               // el pokémon o null
    }
 
    // ======================================================================
    // 3) MÉTODOS DE FUNCIONALIDADES  ·  LOS DEL PROFE
    // ======================================================================
 
    // ---------- mostrarPokedex (= mostrarAgenda / leerAgenda) ----------
    // Recorre todos los <pokemon> y muestra nombre, tipo (el primero) y nivel
    public static void mostrarPokedex(String fichero) throws Exception {
        Document doc = leerXML(fichero);                                // abro el XML
        NodeList listaPokemon = doc.getElementsByTagName("pokemon");    // lista iterable de <pokemon>
        System.out.println("----- POKÉDEX -----");
        for (int i = 0; i < listaPokemon.getLength(); i++) {            // la recorro
            Node nodo = listaPokemon.item(i);                           // cojo el elemento i
            Element pokemon = (Element) nodo;                           // lo convierto a Element
            // item(0) = el primero (y normalmente único) <nombre> dentro del pokémon
            String nombre = pokemon.getElementsByTagName("nombre").item(0).getTextContent();
            String tipo   = pokemon.getElementsByTagName("tipo").item(0).getTextContent();
            String nivel  = pokemon.getElementsByTagName("nivel").item(0).getTextContent();
            System.out.println(nombre + " - " + tipo + " - nivel " + nivel);
        }
    }
 
    // ---------- buscarPokemonPorNombre (= buscarContactoPorNombre) ----------
    // Dice si existe y, si existe, muestra su nivel
    public static void buscarPokemonPorNombre(String fichero, String nom) throws Exception {
        Document doc = leerXML(fichero);                                // abro
        Element pokemon = existePokemon(nom, doc);                      // busco
        if (pokemon != null) {                                          // está
            String nivel = pokemon.getElementsByTagName("nivel").item(0).getTextContent();
            System.out.println("El nivel de " + nom + " es " + nivel);
        } else {                                                        // no está
            System.out.println("No existe ningún pokémon con nombre " + nom);
        }
    }
 
    // ---------- existePokemonBoolean (= existeContacto de XML_Ejercicios) ----------
    // Otra forma de buscar: con una BANDERA boolean en la condición del for
    public static void existePokemonBoolean(String fichero, String nombrePedido) throws Exception {
        Document doc = leerXML(fichero);
        NodeList listaPokemon = doc.getElementsByTagName("pokemon");
        boolean encontrado = false;                                     // bandera: aún no
        String nombrePokedex = "";                                      // aquí guardo el nombre que coincide
        // el for para cuando llega al final O cuando encontrado pasa a true
        for (int i = 0; i < listaPokemon.getLength() && encontrado == false; i++) {
            Element pokemon = (Element) listaPokemon.item(i);
            nombrePokedex = pokemon.getElementsByTagName("nombre").item(0).getTextContent();
            if (nombrePokedex.equalsIgnoreCase(nombrePedido))
                encontrado = true;                                      // ¡lo tengo! el for se para
        }
        if (encontrado) System.out.println("Pokémon encontrado: " + nombrePokedex);
        else            System.out.println("Pokémon NO ENCONTRADO");
    }
 
    // ---------- aniadirPokemon (= aniadirContacto / nuevoContacto) ----------
    // Crea un <pokemon> con su atributo y sus 3 hijos y lo cuelga de la raíz
    public static void aniadirPokemon(String fichero, String nombreNuevo, String tipoNuevo, int nivelNuevo) throws Exception {
        Document doc = leerXML(fichero);                                // abro
        Element pokemon = existePokemon(nombreNuevo, doc);              // ¿ya existe?
        if (pokemon != null) {
            System.out.println("Ya existe un pokémon de nombre: " + nombreNuevo);
        } else {
            // 1. FABRICO las etiquetas (aún sueltas, no están en el árbol)
            Element nuevoPokemon = doc.createElement("pokemon");
            Element elementoNombre = doc.createElement("nombre");
            Element elementoTipo = doc.createElement("tipo");
            Element elementoNivel = doc.createElement("nivel");
            // 2. les METO el contenido que llega por parámetro
            elementoNombre.setTextContent(nombreNuevo);
            elementoTipo.setTextContent(tipoNuevo);
            elementoNivel.setTextContent(String.valueOf(nivelNuevo));   // setTextContent solo acepta String
            // atributo numero = cuántos pokémon hay + 1
            int numero = doc.getElementsByTagName("pokemon").getLength() + 1;
            nuevoPokemon.setAttribute("numero", String.valueOf(numero));
            // 3. las ENGANCHO: hijos dentro de <pokemon>...
            nuevoPokemon.appendChild(elementoNombre);
            nuevoPokemon.appendChild(elementoTipo);
            nuevoPokemon.appendChild(elementoNivel);
            // ...y <pokemon> dentro de la raíz <pokedex>
            Element raiz = doc.getDocumentElement();
            raiz.appendChild(nuevoPokemon);
            // 4. GUARDO
            grabarXML(doc, fichero);
            System.out.println("Añadido: " + nombreNuevo);
        }
    }
 
    // ---------- modificarNivel (= modificarTelefono) ----------
    // Busca el pokémon, coge su <nivel> y le cambia el texto
    public static void modificarNivel(String fichero, String nombrePokemon, int nuevoNivel) throws Exception {
        Document doc = leerXML(fichero);
        Element pokemon = existePokemon(nombrePokemon, doc);
        if (pokemon != null) {
            Element nivel = (Element) pokemon.getElementsByTagName("nivel").item(0);  // su <nivel>
            int nivelViejo = Integer.parseInt(nivel.getTextContent());                // texto → int
            nivel.setTextContent(String.valueOf(nuevoNivel));                         // int → texto
            grabarXML(doc, fichero);
            System.out.println("Nivel de " + nombrePokemon + ": " + nivelViejo + " → " + nuevoNivel);
        } else {
            System.out.println("El pokémon " + nombrePokemon + " no existe");
        }
    }
 
    // ---------- eliminarPokemon (= eliminarContacto) ----------
    // La RAÍZ borra al pokémon entero
    public static void eliminarPokemon(String fichero, String nom) throws Exception {
        Document doc = leerXML(fichero);
        Element pokemon = existePokemon(nom, doc);                      // ¡buscado en el MISMO doc!
        if (pokemon != null) {
            Element raiz = doc.getDocumentElement();                    // la raíz <pokedex>
            raiz.removeChild(pokemon);                                  // el padre borra al hijo
            grabarXML(doc, fichero);
            System.out.println("Pokémon " + nom + " eliminado");
        } else {
            System.out.println("No se puede eliminar: " + nom + " no existe");
        }
    }
 
    // ======================================================================
    // 3) MÉTODOS DE FUNCIONALIDADES  ·  ETIQUETAS DUPLICADAS + ATRIBUTOS
    // ======================================================================
 
    // ---------- mostrarConTipos (= ejer_Etiquetasduplicadas_XML) ----------
    // Cada pokémon puede tener VARIOS <tipo>: segundo for (x) por dentro.
    // También saca el ATRIBUTO numero con getAttribute.
    public static void mostrarConTipos(String fichero) throws Exception {
        Document doc = leerXML(fichero);
        NodeList listaPokemon = doc.getElementsByTagName("pokemon");
        System.out.println("Pokémon: " + listaPokemon.getLength());          // cuántos hay
        for (int i = 0; i < listaPokemon.getLength(); i++) {                 // for de pokémon (i)
            Element pokemonElement = (Element) listaPokemon.item(i);
            String numero = pokemonElement.getAttribute("numero");           // ATRIBUTO ("" si no tiene)
            String shiny  = pokemonElement.getAttribute("shiny");            // otro atributo (puede no estar)
            String nombre = pokemonElement.getElementsByTagName("nombre").item(0).getTextContent();
            System.out.println("Nº " + numero + " " + nombre + (shiny.isEmpty() ? "" : " (shiny: " + shiny + ")"));
 
            NodeList listaTipos = pokemonElement.getElementsByTagName("tipo");   // TODOS sus tipos
            for (int x = 0; x < listaTipos.getLength(); x++) {               // for de tipos (x)
                Element tipoElement = (Element) listaTipos.item(x);
                System.out.println("   - " + tipoElement.getTextContent());
            }
        }
    }
 
    // ---------- existeTipo: busca un <tipo> DENTRO de un pokémon ----------
    // Igual que existePokemon pero un nivel más abajo. Devuelve el <tipo> o null
    public static Element existeTipo(Element pokemon, String tipoBuscado) {
        NodeList tipos = pokemon.getElementsByTagName("tipo");          // solo los de ESTE pokémon
        for (int x = 0; x < tipos.getLength(); x++) {
            Element t = (Element) tipos.item(x);
            if (t.getTextContent().equalsIgnoreCase(tipoBuscado)) return t;
        }
        return null;
    }
 
    // ======================================================================
    // 3) MÉTODOS DE FUNCIONALIDADES  ·  NUEVO (por si cae en el examen)
    // ======================================================================
 
    // ---------- crearXML: crea un XML DESDE CERO (solo con la raíz) ----------
    // newDocument() en vez de parse(): no hay fichero de partida
    // (lo necesitarás para pasar de JSON a XML)
    public static void crearXML(String fichero) throws Exception {
        DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
        Document doc = builder.newDocument();                // documento EN BLANCO
        Element raiz = doc.createElement("pokedex");         // fabrico la raíz
        doc.appendChild(raiz);                               // la cuelgo del documento
        grabarXML(doc, fichero);                             // guardo → <pokedex/>
        System.out.println("Creado " + fichero);
    }
 
    // ---------- nuevoTipo: añade OTRO <tipo> (ahora tendrá 2) ----------
    public static void nuevoTipo(String fichero, String nombrePokemon, String tipoNuevo) throws Exception {
        Document doc = leerXML(fichero);
        Element pokemon = existePokemon(nombrePokemon, doc);
        if (pokemon == null) {                                          // no existe el pokémon
            System.out.println(nombrePokemon + " no existe");
        } else if (existeTipo(pokemon, tipoNuevo) != null) {            // ya tiene ese tipo
            System.out.println(nombrePokemon + " ya es de tipo " + tipoNuevo);
        } else {
            Element tipo = doc.createElement("tipo");                   // fabrico otro <tipo>
            tipo.setTextContent(tipoNuevo);
            // insertBefore(nuevo, referencia) → lo pongo ANTES de <nivel> para que
            // los tipos queden juntos. (appendChild también vale, pero iría al final)
            Node nivel = pokemon.getElementsByTagName("nivel").item(0);
            pokemon.insertBefore(tipo, nivel);
            grabarXML(doc, fichero);
            System.out.println("Añadido el tipo " + tipoNuevo + " a " + nombrePokemon);
        }
    }
 
    // ---------- modificarTipo: cambia UN tipo concreto ----------
    public static void modificarTipo(String fichero, String nombrePokemon, String tipoViejo, String tipoNuevo) throws Exception {
        Document doc = leerXML(fichero);
        Element pokemon = existePokemon(nombrePokemon, doc);                     // 1º el pokémon
        Element tipo = (pokemon == null) ? null : existeTipo(pokemon, tipoViejo); // 2º su tipo
        if (tipo == null) {
            System.out.println(nombrePokemon + " no tiene el tipo " + tipoViejo);
        } else {
            tipo.setTextContent(tipoNuevo);                                       // cambio el texto
            grabarXML(doc, fichero);
            System.out.println(nombrePokemon + ": " + tipoViejo + " → " + tipoNuevo);
        }
    }
 
    // ---------- eliminarTipo: borra UN tipo concreto (el pokémon se queda) ----------
    public static void eliminarTipo(String fichero, String nombrePokemon, String tipoBorrar) throws Exception {
        Document doc = leerXML(fichero);
        Element pokemon = existePokemon(nombrePokemon, doc);
        Element tipo = (pokemon == null) ? null : existeTipo(pokemon, tipoBorrar);
        if (tipo == null) {
            System.out.println(nombrePokemon + " no tiene el tipo " + tipoBorrar);
        } else {
            pokemon.removeChild(tipo);                       // el PADRE (pokémon) borra al hijo (tipo)
            grabarXML(doc, fichero);
            System.out.println("Eliminado el tipo " + tipoBorrar + " de " + nombrePokemon);
        }
    }
 
    // ---------- cambiarAtributo: pone o cambia un atributo ----------
    // setAttribute: si el atributo existe lo CAMBIA; si no existe lo CREA
    public static void cambiarAtributo(String fichero, String nombrePokemon, String atributo, String valor) throws Exception {
        Document doc = leerXML(fichero);
        Element pokemon = existePokemon(nombrePokemon, doc);
        if (pokemon == null) {
            System.out.println(nombrePokemon + " no existe");
        } else {
            pokemon.setAttribute(atributo, valor);           // <pokemon numero="150">
            grabarXML(doc, fichero);
            System.out.println(nombrePokemon + ": " + atributo + "=\"" + valor + "\"");
        }
    }
 
    // ---------- quitarAtributo: borra un atributo ----------
    public static void quitarAtributo(String fichero, String nombrePokemon, String atributo) throws Exception {
        Document doc = leerXML(fichero);
        Element pokemon = existePokemon(nombrePokemon, doc);
        if (pokemon == null || !pokemon.hasAttribute(atributo)) {    // hasAttribute: ¿lo tiene?
            System.out.println(nombrePokemon + " no tiene el atributo " + atributo);
        } else {
            pokemon.removeAttribute(atributo);               // fuera el atributo
            grabarXML(doc, fichero);
            System.out.println("Quitado el atributo " + atributo + " de " + nombrePokemon);
        }
    }
 
    // ---------- nuevoCampo: añade una etiqueta nueva (si ya existe, la modifica) ----------
    public static void nuevoCampo(String fichero, String nombrePokemon, String etiqueta, String valor) throws Exception {
        Document doc = leerXML(fichero);
        Element pokemon = existePokemon(nombrePokemon, doc);
        if (pokemon == null) {
            System.out.println(nombrePokemon + " no existe");
            return;                                          // salgo del método
        }
        NodeList ya = pokemon.getElementsByTagName(etiqueta);    // ¿ya tiene esa etiqueta?
        if (ya.getLength() > 0) {                                // sí → modifico la primera
            ya.item(0).setTextContent(valor);
            System.out.println("Modificado <" + etiqueta + "> de " + nombrePokemon + ": " + valor);
        } else {                                                 // no → la creo y la cuelgo
            Element nueva = doc.createElement(etiqueta);
            nueva.setTextContent(valor);
            pokemon.appendChild(nueva);
            System.out.println("Añadido <" + etiqueta + "> a " + nombrePokemon + ": " + valor);
        }
        grabarXML(doc, fichero);
    }
 
    // ---------- eliminarCampo: borra una etiqueta concreta del pokémon ----------
    public static void eliminarCampo(String fichero, String nombrePokemon, String etiqueta) throws Exception {
        Document doc = leerXML(fichero);
        Element pokemon = existePokemon(nombrePokemon, doc);
        if (pokemon == null || pokemon.getElementsByTagName(etiqueta).getLength() == 0) {
            System.out.println(nombrePokemon + " no tiene <" + etiqueta + ">");
        } else {
            Node campo = pokemon.getElementsByTagName(etiqueta).item(0);
            pokemon.removeChild(campo);                      // el pokémon borra su <region>
            grabarXML(doc, fichero);
            System.out.println("Eliminado <" + etiqueta + "> de " + nombrePokemon);
        }
    }
 
    // ---------- modificarNombre: cambia el nombre (sin repetir) ----------
    public static void modificarNombre(String fichero, String viejo, String nuevo) throws Exception {
        Document doc = leerXML(fichero);
        Element pokemon = existePokemon(viejo, doc);
        if (pokemon == null) {
            System.out.println(viejo + " no existe");
        } else if (existePokemon(nuevo, doc) != null) {      // ya hay otro con el nombre nuevo
            System.out.println("Ya existe un pokémon llamado " + nuevo);
        } else {
            pokemon.getElementsByTagName("nombre").item(0).setTextContent(nuevo);
            grabarXML(doc, fichero);
            System.out.println(viejo + " ahora se llama " + nuevo);
        }
    }
 
    // ---------- leerPokedex_crearObjetos (= leerAgenda_crearObjeto de de_XML_a_JSON) ----------
    // Lee el XML y devuelve una LISTA DE OBJETOS. Es el PASO 1 de migrar a JSON:
    //   XML → List<Ejercicio_Pokemon> → (Gson) → JSON
    public static List<Ejercicio_Pokemon> leerPokedex_crearObjetos(String fichero) throws Exception {
        List<Ejercicio_Pokemon> listaPokemon = new ArrayList<>();       // lista vacía
        Document doc = leerXML(fichero);
        NodeList nodos = doc.getElementsByTagName("pokemon");
        for (int i = 0; i < nodos.getLength(); i++) {
            Element p = (Element) nodos.item(i);
            String nombre = p.getElementsByTagName("nombre").item(0).getTextContent();
            // si tiene varios tipos, los junto en un String: "Fuego, Volador"
            NodeList tipos = p.getElementsByTagName("tipo");
            String tipo = "";
            for (int x = 0; x < tipos.getLength(); x++) {
                if (x > 0) tipo += ", ";                                // coma entre tipos
                tipo += tipos.item(x).getTextContent();
            }
            int nivel = Integer.parseInt(p.getElementsByTagName("nivel").item(0).getTextContent());  // texto → int
            listaPokemon.add(new Ejercicio_Pokemon(nombre, tipo, nivel));   // creo el objeto y lo guardo
        }
        return listaPokemon;                                            // devuelvo la lista
    }
}
 