package Clase2_Acc_AccAleatorio_Secuencial;

// ---------------------------------------------------------------
// IMPORTS
// ---------------------------------------------------------------
// javax.xml.parsers → las clases que LEEN el XML y lo convierten en un árbol
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
// javax.xml.transform → las clases que ESCRIBEN el árbol de vuelta al fichero
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
// org.w3c.dom → las piezas del árbol: Document, Element, Node, NodeList...
// (el * importa todas; lo normal sería importarlas una a una)
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class Clase2 {

	/*
	 * ============================================================
	 *  RESUMEN DE LO VISTO HASTA AHORA
	 * ============================================================
	 *
	 *  ACCESO SECUENCIAL (se lee de principio a fin, en orden)
	 *  -------------------------------------------------------
	 *   Ficheros de TEXTO
	 *     - Leer:     FileReader + BufferedReader
	 *     - Escribir: FileWriter + BufferedWriter, o PrintWriter
	 *
	 *   Ficheros BINARIOS
	 *     - Leer:     FileInputStream  + DataInputStream   (tipos primitivos)
	 *                 FileInputStream  + ObjectInputStream (objetos)
	 *     - Escribir: FileOutputStream + DataOutputStream
	 *                 FileOutputStream + ObjectOutputStream
	 *
	 *     Input / Output siempre desde el punto de vista del PROGRAMA:
	 *       Input  = los datos ENTRAN al programa (leer del fichero)
	 *       Output = los datos SALEN del programa (escribir al fichero)
	 *
	 *  ACCESO ALEATORIO (se salta directamente a una posición)
	 *  -------------------------------------------------------
	 *     - Leer y escribir: RandomAccessFile (modos "r" y "rw")
	 *
	 * ============================================================
	 *  ACCESO A DATOS EN FORMATOS ESTRUCTURADOS
	 * ============================================================
	 *   1. XML   ← esta clase
	 *   2. CSV
	 *   3. JSON
	 *
	 * ============================================================
	 *  XML Y DOM
	 * ============================================================
	 *  XML es un fichero de texto con ETIQUETAS que forman un árbol:
	 *
	 *    <agenda>                         ← elemento RAÍZ (solo hay uno)
	 *        <contacto>                   ← elemento hijo de agenda
	 *            <nombre>Mario</nombre>   ← hijo de contacto; "Mario" es su TEXTO
	 *            <telefono>600111222</telefono>
	 *        </contacto>
	 *        <contacto> ... </contacto>
	 *    </agenda>
	 *
	 *  DOM (Document Object Model) = cargar el XML ENTERO en memoria como un
	 *  árbol de objetos. Así podemos recorrerlo, buscar, modificar y borrar
	 *  nodos, y después volver a guardarlo en el fichero.
	 *   + Muy cómodo, puedes ir adelante y atrás en el árbol.
	 *   - Si el XML es enorme, ocupa mucha memoria (para eso existe SAX,
	 *     que lee el XML por eventos sin cargarlo entero).
	 *
	 *  Piezas principales:
	 *   - Document → el documento completo (el árbol entero)
	 *   - Node     → cualquier cosa del árbol: una etiqueta, un texto, un comentario...
	 *   - Element  → un tipo de Node: una ETIQUETA (<contacto>, <nombre>...)
	 *   - NodeList → una lista de nodos (con getLength() e item(i))
	 */

	public static void main(String[] args) throws Exception {
		// El fichero agenda.xml tiene que estar en la RAÍZ del proyecto
		// (al lado de la carpeta src), igual que con los ficheros de texto.
		leerAgenda("agenda.xml");
		buscar("Mario", "agenda.xml");
		buscar("Pepe", "agenda.xml");   // no existe → "No se ha encontrado"
		eliminar("José María", "agenda.xml");
		leerAgenda("agenda.xml");       // comprobamos que ya no está
		// OJO: eliminar() MODIFICA el fichero de verdad. Si lo ejecutas dos veces,
		// la segunda dirá que no encuentra a José María. Guarda una copia del XML.
	}

	// ---------------------------------------------------------------
	// MÉTODO AUXILIAR: cargar el XML en memoria
	// ---------------------------------------------------------------
	// Los tres métodos empezaban con las mismas 3 líneas repetidas.
	// Cuando algo se repite, se saca a un método → menos código y menos errores.
	public static Document cargarXML(String fichero) throws Exception {
		// 1. La FACTORÍA: un objeto cuya función es fabricar "constructores" de documentos.
		//    No se usa new, sino newInstance() (patrón Factory).
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		// 2. El CONSTRUCTOR (builder): el que sabe leer un XML.
		DocumentBuilder builder = factory.newDocumentBuilder();
		// 3. parse() lee el fichero y devuelve el árbol completo en un Document.
		//    Si el XML está mal formado (una etiqueta sin cerrar...) lanza SAXException.
		Document doc = builder.parse(fichero);
		// normalize() junta nodos de texto partidos. No siempre hace falta,
		// pero es buena costumbre llamarlo nada más cargar.
		doc.getDocumentElement().normalize();
		return doc;
	}

	// ---------------------------------------------------------------
	// LEER: mostrar todos los contactos
	// ---------------------------------------------------------------
	public static void leerAgenda(String fichero) throws Exception {
		System.out.println("===== AGENDA =====");
		Document doc = cargarXML(fichero);

		// getElementsByTagName("contacto") busca en TODO el documento las etiquetas
		// <contacto> y las devuelve en un NodeList (lista que podemos recorrer).
		NodeList listaContactos = doc.getElementsByTagName("contacto");

		// NodeList no es un ArrayList: no vale el for-each.
		// Se recorre con getLength() (tamaño) e item(i) (elemento i).
		for (int i = 0; i < listaContactos.getLength(); i++) {
			Node nodo = listaContactos.item(i);
			// item() devuelve un Node genérico. Lo convertimos (cast) a Element
			// porque Element tiene más métodos, como getElementsByTagName.
			// Aquí el cast es seguro: getElementsByTagName solo devuelve etiquetas.
			Element contacto = (Element) nodo;

			// Dentro de ESTE contacto buscamos su <nombre>:
			//   getElementsByTagName("nombre") → lista con los <nombre> de este contacto
			//   .item(0)                       → el primero (solo hay uno)
			//   .getTextContent()              → el texto que hay entre las etiquetas
			String nombre = contacto.getElementsByTagName("nombre").item(0).getTextContent();
			String telefono = contacto.getElementsByTagName("telefono").item(0).getTextContent();

			// printf: los valores van DETRÁS, separados por comas.
			// %s = String, %n = salto de línea
			System.out.printf("Nombre: %s%nTeléfono: %s%n%n", nombre, telefono);
		}
	}

	// ---------------------------------------------------------------
	// BUSCAR: mostrar el teléfono de un contacto por su nombre
	// ---------------------------------------------------------------
	// Nombre del método en minúscula: en Java los métodos van en camelCase
	// (buscar, leerAgenda). Mayúscula inicial solo para las clases.
	public static void buscar(String nombreBuscado, String fichero) throws Exception {
		Document doc = cargarXML(fichero);
		NodeList listaContactos = doc.getElementsByTagName("contacto");
		boolean encontrado = false; // "bandera" para saber si lo hemos encontrado

		for (int i = 0; i < listaContactos.getLength(); i++) {
			Element contacto = (Element) listaContactos.item(i);
			String nombre = contacto.getElementsByTagName("nombre").item(0).getTextContent();

			// equalsIgnoreCase: compara sin distinguir mayúsculas ("mario" = "Mario")
			if (nombreBuscado.equalsIgnoreCase(nombre)) {
				String telefono = contacto.getElementsByTagName("telefono").item(0).getTextContent();
				System.out.println("Teléfono de " + nombre + ": " + telefono);
				encontrado = true;
				// No ponemos break por si hubiera varios contactos con el mismo nombre
			}
		}

		// Solo después de recorrer TODA la lista sabemos si no estaba
		if (!encontrado)
			System.out.println("No se ha encontrado al contacto " + nombreBuscado);
	}

	// ---------------------------------------------------------------
	// ELIMINAR: borrar un contacto y guardar el XML
	// ---------------------------------------------------------------
	/*
	 * Borrar en XML tiene DOS PASOS:
	 *   1. Quitar el nodo del árbol que tenemos en MEMORIA (removeChild)
	 *   2. GUARDAR el árbol de nuevo en el fichero (Transformer)
	 * Si solo haces el paso 1, el fichero no cambia: solo has modificado la copia
	 * que hay en memoria.
	 */
	public static void eliminar(String nombreBuscado, String fichero) throws Exception {
		Document doc = cargarXML(fichero);
		NodeList listaContactos = doc.getElementsByTagName("contacto");
		boolean encontrado = false;

		// IMPORTANTE: recorremos la lista HACIA ATRÁS.
		// El NodeList es "vivo": si borras un nodo, la lista se actualiza sola
		// y los siguientes se desplazan una posición. Recorriendo hacia delante,
		// al borrar el i, el que era i+1 pasa a ser i y el bucle se lo SALTA.
		// Hacia atrás, borrar no afecta a los que quedan por revisar.
		for (int i = listaContactos.getLength() - 1; i >= 0; i--) {
			Element contacto = (Element) listaContactos.item(i);
			String nombre = contacto.getElementsByTagName("nombre").item(0).getTextContent();

			if (nombreBuscado.equalsIgnoreCase(nombre)) {
				// Un nodo NO se borra a sí mismo: se lo pide a su PADRE.
				// getParentNode() → el padre (aquí, <agenda>)
				// removeChild(hijo) → lo quita del árbol
				// (Con doc.getDocumentElement().removeChild(contacto) también funciona,
				// pero SOLO si contacto es hijo directo de la raíz. getParentNode()
				// funciona siempre, esté donde esté.)
				contacto.getParentNode().removeChild(contacto);
				System.out.println("El contacto " + nombre + " ha sido eliminado");
				encontrado = true;
			}
		}

		if (encontrado)
			guardarXML(doc, fichero); // solo guardamos si ha cambiado algo, y UNA sola vez
		else
			System.out.println("No se ha encontrado al contacto " + nombreBuscado);
	}

	// ---------------------------------------------------------------
	// MÉTODO AUXILIAR: guardar el árbol DOM en el fichero
	// ---------------------------------------------------------------
	// Lo usarás también para AÑADIR o MODIFICAR contactos, así que mejor aparte.
	public static void guardarXML(Document doc, String fichero) throws Exception {
		// Mismo patrón que al leer: factoría → objeto que hace el trabajo.
		TransformerFactory transformerFactory = TransformerFactory.newInstance();
		// CORREGIDO: en el original ponía TransformerFactory.newTransformer()
		// (con la C mayúscula, llamando a la CLASE). newTransformer() no es
		// estático: hay que llamarlo sobre el OBJETO que acabamos de crear.
		Transformer transformer = transformerFactory.newTransformer();

		// Opciones para que el XML quede bonito (con saltos de línea y sangría).
		// CORREGIDO: el valor es "yes" en minúsculas, no "YES".
		transformer.setOutputProperty(OutputKeys.INDENT, "yes");
		// Número de espacios de sangría (propiedad específica del motor de Java)
		transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "4");
		// Codificación: UTF-8 para que tildes y ñ se guarden bien
		transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");

		// DOMSource    → DE DÓNDE sale la información (nuestro árbol en memoria)
		// StreamResult → A DÓNDE va (el fichero)
		DOMSource source = new DOMSource(doc);
		StreamResult result = new StreamResult(new java.io.File(fichero));

		// transform() convierte el árbol en texto XML y lo escribe en el fichero
		transformer.transform(source, result);
		System.out.println("Fichero " + fichero + " guardado");
	}
}