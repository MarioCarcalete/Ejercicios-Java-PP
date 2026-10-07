package Clase2_Acc_AccAleatorio_Secuencial;

// javax.xml.parsers → clases que LEEN el XML y lo convierten en un árbol
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
// javax.xml.transform → clases que ESCRIBEN el árbol de vuelta al fichero
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

// org.w3c.dom → las piezas del árbol XML: Document, Element, Node, NodeList...
import org.w3c.dom.*;

/*
 * ============================================================
 *  VERSIÓN NUEVA (refactorizada)
 * ============================================================
 *  El código que se repetía en todos los métodos se saca a
 *  3 métodos AUXILIARES:
 *
 *   - cargarXML(fichero)          → Factory + Builder + parse
 *   - grabarXML(doc, fichero)     → todo el bloque del Transformer
 *   - buscarContacto(nombre, doc) → el bucle que busca por nombre;
 *                                   devuelve el <contacto> o null
 *
 *  Así cada método del CRUD queda corto y se lee de un vistazo.
 *  Si hay que cambiar cómo se guarda, se cambia en UN solo sitio.
 *
 *  Además los métodos van en minúscula (buscar, cambiarNumero),
 *  que es la convención de Java.
 * ============================================================
 */
public class Clase2Nuevo {

	public static void main(String[] args) throws Exception {
		// agenda.xml debe estar en la RAÍZ del proyecto
		leerAgenda("agenda.xml");
		buscar("Mario", "agenda.xml");
		buscar("Pepe", "agenda.xml");                        // no existe → mensaje de no encontrado
		eliminar("José María", "agenda.xml");                // OJO: modifica el fichero de verdad
		nuevoContacto("Sofia", "756478654", "agenda.xml");   // la 2ª ejecución dirá que ya existe
		cambiarNumero("Sofia", "348758734", "agenda.xml");
		leerAgenda("agenda.xml");                            // comprobamos cómo ha quedado
	}

	// ===============================================================
	//  MÉTODOS AUXILIARES
	// ===============================================================

	// ---------------------------------------------------------------
	// CARGAR EL XML: devuelve el árbol entero en un Document
	// ---------------------------------------------------------------
	public static Document cargarXML(String fichero) throws Exception {
		DocumentBuilderFactory BF = DocumentBuilderFactory.newInstance();
		DocumentBuilder builder = BF.newDocumentBuilder();
		return builder.parse(fichero);
	}

	// ---------------------------------------------------------------
	// GRABAR EL XML: vuelca el árbol de memoria al fichero
	// ---------------------------------------------------------------
	// Sin llamar a este método, los cambios (añadir, modificar,
	// eliminar) solo existen en memoria y el fichero no cambia.
	public static void grabarXML(Document doc, String fichero) throws Exception {
		TransformerFactory transformerFactor = TransformerFactory.newInstance();
		Transformer transformer = transformerFactor.newTransformer();
		transformer.setOutputProperty(OutputKeys.INDENT, "yes");
		transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "4");
		DOMSource source = new DOMSource(doc);          // origen: el árbol en memoria
		StreamResult result = new StreamResult(fichero); // destino: el fichero
		transformer.transform(source, result);
	}

	// ---------------------------------------------------------------
	// BUSCAR UN CONTACTO: devuelve su <contacto> o null si no existe
	// ---------------------------------------------------------------
	// Recibe el Document ya cargado (no el nombre del fichero), para
	// que quien lo llame pueda modificar ese mismo árbol y guardarlo.
	public static Element buscarContacto(String nombreB, Document doc) {
		NodeList listaContactos = doc.getElementsByTagName("contacto");
		for (int i = 0; i < listaContactos.getLength(); i++) {
			Element contacto = (Element) listaContactos.item(i);
			String nombre = contacto.getElementsByTagName("nombre").item(0).getTextContent();
			if (nombre.equalsIgnoreCase(nombreB)) {
				return contacto; // lo hemos encontrado: salimos ya del método
			}
		}
		return null; // hemos recorrido todos y no estaba
	}

	// ===============================================================
	//  CRUD
	// ===============================================================

	// ---------------------------------------------------------------
	// LEER TODA LA AGENDA
	// ---------------------------------------------------------------
	public static void leerAgenda(String fichero) throws Exception {
		System.out.println("===== AGENDA =====");
		Document doc = cargarXML(fichero);

		NodeList listaContactos = doc.getElementsByTagName("contacto");
		for (int i = 0; i < listaContactos.getLength(); i++) {
			Element contacto = (Element) listaContactos.item(i);
			String nombre = contacto.getElementsByTagName("nombre").item(0).getTextContent();
			String telefono = contacto.getElementsByTagName("telefono").item(0).getTextContent();
			System.out.printf("Nombre : %s%nTelefono : %s%n", nombre, telefono);
		}
		System.out.println();
	}

	// ---------------------------------------------------------------
	// BUSCAR UN CONTACTO POR NOMBRE
	// ---------------------------------------------------------------
	public static void buscar(String nombreB, String fichero) throws Exception {
		Document doc = cargarXML(fichero);
		Element contacto = buscarContacto(nombreB, doc);

		if (contacto != null) {
			String nombre = contacto.getElementsByTagName("nombre").item(0).getTextContent();
			String telefono = contacto.getElementsByTagName("telefono").item(0).getTextContent();
			System.out.println("Telefono de " + nombre + " : " + telefono);
		} else {
			System.out.println("No se ha encontrado al contacto " + nombreB);
		}
	}

	// ---------------------------------------------------------------
	// ELIMINAR UN CONTACTO
	// ---------------------------------------------------------------
	//   1. Quitar el nodo del árbol en MEMORIA (removeChild)
	//   2. GUARDAR el árbol en el fichero
	public static void eliminar(String nombreB, String fichero) throws Exception {
		Document doc = cargarXML(fichero);
		Element contacto = buscarContacto(nombreB, doc);

		if (contacto != null) {
			Element raiz = doc.getDocumentElement();
			raiz.removeChild(contacto);   // 1. fuera del árbol
			grabarXML(doc, fichero);      // 2. guardado
			System.out.println("El contacto " + nombreB + " ha sido eliminado");
		} else {
			System.out.println("No existe el contacto " + nombreB);
		}
	}

	// ---------------------------------------------------------------
	// AÑADIR UN CONTACTO NUEVO
	// ---------------------------------------------------------------
	//   1. Comprobar que no existe ya
	//   2. Crear las etiquetas nuevas en MEMORIA
	//   3. Colgarlas de la raíz <agenda>
	//   4. GUARDAR
	public static void nuevoContacto(String nuevoNombre, String telefonoN, String fichero) throws Exception {
		Document doc = cargarXML(fichero);

		// 1. ¿Ya existe?
		if (buscarContacto(nuevoNombre, doc) != null) {
			System.out.println("El contacto " + nuevoNombre + " ya existe, no se añade");
			return; // salimos: no hay que añadir nada
		}

		// 2. CREAR LOS NODOS (siempre con doc.createElement, nunca con new)
		Element nuevo = doc.createElement("contacto");

		Element elementoNombre = doc.createElement("nombre");
		elementoNombre.setTextContent(nuevoNombre);

		Element elementoTelefono = doc.createElement("telefono");
		elementoTelefono.setTextContent(telefonoN);

		nuevo.appendChild(elementoNombre);
		nuevo.appendChild(elementoTelefono);

		// 3. COLGARLO DE LA RAÍZ
		doc.getDocumentElement().appendChild(nuevo);

		// 4. GUARDAR
		grabarXML(doc, fichero);
		System.out.println("Contacto " + nuevoNombre + " añadido");
	}

	// ---------------------------------------------------------------
	// CAMBIAR EL NÚMERO DE TELÉFONO DE UN CONTACTO
	// ---------------------------------------------------------------
	//   1. Buscar el contacto
	//   2. Cambiar el texto de su <telefono> con setTextContent
	//   3. GUARDAR
	public static void cambiarNumero(String nombreC, String telefonoN, String fichero) throws Exception {
		Document doc = cargarXML(fichero);
		Element contacto = buscarContacto(nombreC, doc);

		if (contacto != null) {
			Node telefono = contacto.getElementsByTagName("telefono").item(0);
			String telefonoViejo = telefono.getTextContent(); // LEE
			telefono.setTextContent(telefonoN);               // SUSTITUYE
			grabarXML(doc, fichero);

			String nombre = contacto.getElementsByTagName("nombre").item(0).getTextContent();
			System.out.println("El telefono de " + nombre + " ha cambiado de "
					+ telefonoViejo + " a este telefono : " + telefonoN);
		} else {
			System.out.println("El contacto " + nombreC + " no existe, no se puede modificar");
		}
	}
}