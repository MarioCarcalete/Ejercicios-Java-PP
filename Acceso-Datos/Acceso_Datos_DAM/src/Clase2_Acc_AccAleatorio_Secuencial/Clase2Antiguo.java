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
 *  VERSIÓN ANTIGUA
 * ============================================================
 *  Cada método hace TODO por sí mismo:
 *   - Carga el XML con las 3 líneas (Factory → Builder → parse)
 *   - Recorre la lista de contactos con su propio bucle for
 *   - Guarda el fichero con su propio bloque de Transformer
 *
 *  Funciona, pero se repite mucho código. En Clase2Nuevo
 *  ese código repetido se saca a métodos auxiliares.
 * ============================================================
 */
public class Clase2Antiguo {

	public static void main(String[] args) throws Exception {
		// agenda.xml debe estar en la RAÍZ del proyecto
		leerAgenda("agenda.xml");
		Buscar("Mario", "agenda.xml");
		Buscar("Pepe", "agenda.xml");                        // no existe → mensaje de no encontrado
		eliminar("José María", "agenda.xml");                // OJO: modifica el fichero de verdad
		nuevoContacto("Sofia", "756478654", "agenda.xml");   // la 2ª ejecución dirá que ya existe
		CambiarNumero("Sofia", "348758734", "agenda.xml");
		leerAgenda("agenda.xml");                            // comprobamos cómo ha quedado
	}

	// ---------------------------------------------------------------
	// LEER TODA LA AGENDA
	// ---------------------------------------------------------------
	public static void leerAgenda(String fichero) throws Exception {
		System.out.println("===== AGENDA =====");
		// 1. La FACTORÍA: fabrica "constructores" de documentos (newInstance, no new)
		DocumentBuilderFactory BF = DocumentBuilderFactory.newInstance();
		// 2. El BUILDER: el objeto que sabe leer un XML
		DocumentBuilder builder = BF.newDocumentBuilder();
		// 3. parse() lee el fichero y devuelve el árbol entero en un Document
		Document doc = builder.parse(fichero);

		NodeList listaContactos = doc.getElementsByTagName("contacto");

		// NodeList no admite for-each: se recorre con getLength() e item(i)
		for (int i = 0; i < listaContactos.getLength(); i++) {
			Node nodo = listaContactos.item(i);
			Element contacto = (Element) nodo; // cast a Element para buscar dentro

			String nombre = contacto.getElementsByTagName("nombre").item(0).getTextContent();
			String telefono = contacto.getElementsByTagName("telefono").item(0).getTextContent();

			System.out.printf("Nombre : %s%nTelefono : %s%n", nombre, telefono);
		}
		System.out.println();
	}

	// ---------------------------------------------------------------
	// BUSCAR UN CONTACTO POR NOMBRE
	// ---------------------------------------------------------------
	public static void Buscar(String nombreB, String fichero) throws Exception {
		DocumentBuilderFactory BF = DocumentBuilderFactory.newInstance();
		DocumentBuilder builder = BF.newDocumentBuilder();
		Document doc = builder.parse(fichero);

		NodeList listaContactos = doc.getElementsByTagName("contacto");
		boolean encontrado = false; // bandera: pasa a true si lo encontramos

		// "&& encontrado == false" → el bucle para en cuanto lo encuentra
		for (int i = 0; i < listaContactos.getLength() && encontrado == false; i++) {
			Node nodo = listaContactos.item(i);
			Element contacto = (Element) nodo;
			String nombre = contacto.getElementsByTagName("nombre").item(0).getTextContent();
			String telefono = contacto.getElementsByTagName("telefono").item(0).getTextContent();

			// equalsIgnoreCase: compara sin importar mayúsculas ("mario" = "Mario")
			if (nombre.equalsIgnoreCase(nombreB)) {
				encontrado = true;
				System.out.println("Telefono de " + nombre + " : " + telefono);
			}
		}

		if (!encontrado) {
			System.out.println("No se ha encontrado al contacto " + nombreB);
		}
	}

	// ---------------------------------------------------------------
	// ELIMINAR UN CONTACTO
	// ---------------------------------------------------------------
	// Borrar tiene DOS pasos:
	//   1. Quitar el nodo del árbol en MEMORIA (removeChild)
	//   2. GUARDAR el árbol en el fichero (Transformer)
	public static void eliminar(String nombreB, String fichero) throws Exception {
		DocumentBuilderFactory BF = DocumentBuilderFactory.newInstance();
		DocumentBuilder builder = BF.newDocumentBuilder();
		Document doc = builder.parse(fichero);

		NodeList listaContactos = doc.getElementsByTagName("contacto");
		boolean encontrado = false;

		for (int i = 0; i < listaContactos.getLength() && encontrado == false; i++) {
			Node nodo = listaContactos.item(i);
			Element contacto = (Element) nodo;
			String nombre = contacto.getElementsByTagName("nombre").item(0).getTextContent();

			if (nombre.equalsIgnoreCase(nombreB)) {
				encontrado = true;
				// 1. Quitarlo del árbol: el padre (la raíz <agenda>) elimina al hijo
				Element raiz = doc.getDocumentElement();
				raiz.removeChild(contacto);
			}
		}

		if (encontrado) {
			// 2. GUARDAR EN EL FICHERO
			TransformerFactory transformerFactor = TransformerFactory.newInstance();
			Transformer transformer = transformerFactor.newTransformer();
			transformer.setOutputProperty(OutputKeys.INDENT, "yes");
			transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "4");
			DOMSource source = new DOMSource(doc);
			StreamResult result = new StreamResult(fichero);
			transformer.transform(source, result);

			System.out.println("El contacto " + nombreB + " ha sido eliminado");
		} else {
			System.out.println("No existe el contacto " + nombreB);
		}
	}

	// ---------------------------------------------------------------
	// AÑADIR UN CONTACTO NUEVO
	// ---------------------------------------------------------------
	// Pasos:
	//   1. Comprobar que no existe ya un contacto con ese nombre
	//   2. Crear las etiquetas nuevas en MEMORIA
	//   3. Colgarlas de la raíz <agenda>
	//   4. GUARDAR el árbol en el fichero
	public static void nuevoContacto(String nuevoNombre, String telefonoN, String fichero) throws Exception {
		DocumentBuilderFactory BF = DocumentBuilderFactory.newInstance();
		DocumentBuilder builder = BF.newDocumentBuilder();
		Document doc = builder.parse(fichero);

		NodeList listaContactos = doc.getElementsByTagName("contacto");
		boolean encontrado = false;

		// 1. BUSCAR SI YA EXISTE
		for (int i = 0; i < listaContactos.getLength() && encontrado == false; i++) {
			Node nodo = listaContactos.item(i);
			Element contacto = (Element) nodo;
			String nombre = contacto.getElementsByTagName("nombre").item(0).getTextContent();

			if (nuevoNombre.equalsIgnoreCase(nombre)) {
				encontrado = true;
			}
		}

		if (encontrado) {
			System.out.println("El contacto " + nuevoNombre + " ya existe, no se añade");
		} else {
			// 2. CREAR LOS NODOS NUEVOS (siempre con doc.createElement, nunca con new)
			Element Nuevocontacto = doc.createElement("contacto");

			Element Elementonombre = doc.createElement("nombre");
			Elementonombre.setTextContent(nuevoNombre);

			Element Elementotelefono = doc.createElement("telefono");
			Elementotelefono.setTextContent(telefonoN);

			// appendChild(hijo) mete un nodo DENTRO de otro, al final
			Nuevocontacto.appendChild(Elementonombre);
			Nuevocontacto.appendChild(Elementotelefono);

			// 3. COLGARLO DEL ÁRBOL: al final de la raíz <agenda>
			Element raiz = doc.getDocumentElement();
			raiz.appendChild(Nuevocontacto);

			// 4. GUARDAR EN EL FICHERO
			TransformerFactory transformerFactor = TransformerFactory.newInstance();
			Transformer transformer = transformerFactor.newTransformer();
			transformer.setOutputProperty(OutputKeys.INDENT, "yes");
			transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "4");
			DOMSource source = new DOMSource(doc);
			StreamResult result = new StreamResult(fichero);
			transformer.transform(source, result);

			System.out.println("Contacto " + nuevoNombre + " añadido");
		}
	}

	// ---------------------------------------------------------------
	// CAMBIAR EL NÚMERO DE TELÉFONO DE UN CONTACTO
	// ---------------------------------------------------------------
	// Pasos:
	//   1. Buscar el contacto por su nombre
	//   2. Coger su <telefono> y cambiarle el texto con setTextContent
	//   3. GUARDAR el árbol en el fichero, solo si lo hemos encontrado
	public static void CambiarNumero(String nombreC, String telefonoN, String fichero) throws Exception {
		DocumentBuilderFactory BF = DocumentBuilderFactory.newInstance();
		DocumentBuilder builder = BF.newDocumentBuilder();
		Document doc = builder.parse(fichero);

		NodeList listaContactos = doc.getElementsByTagName("contacto");
		boolean encontrado = false;

		// 1. BUSCAR
		for (int i = 0; i < listaContactos.getLength() && encontrado == false; i++) {
			Node nodo = listaContactos.item(i);
			Element contacto = (Element) nodo;
			String nombre = contacto.getElementsByTagName("nombre").item(0).getTextContent();

			if (nombreC.equalsIgnoreCase(nombre)) {
				encontrado = true;

				// 2. MODIFICAR (item(0) con el número 0, sin comillas)
				Node telefono = contacto.getElementsByTagName("telefono").item(0);
				String telefonoViejo = telefono.getTextContent(); // LEE el texto
				telefono.setTextContent(telefonoN);               // lo SUSTITUYE

				System.out.println("El telefono de " + nombre + " ha cambiado de "
						+ telefonoViejo + " a este telefono : " + telefonoN);
			}
		}

		// 3. GUARDAR
		if (encontrado) {
			TransformerFactory transformerFactor = TransformerFactory.newInstance();
			Transformer transformer = transformerFactor.newTransformer();
			transformer.setOutputProperty(OutputKeys.INDENT, "yes");
			transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "4");
			DOMSource source = new DOMSource(doc);
			StreamResult result = new StreamResult(fichero);
			transformer.transform(source, result);
		} else {
			System.out.println("El contacto " + nombreC + " no existe, no se puede modificar");
		}
	}
}