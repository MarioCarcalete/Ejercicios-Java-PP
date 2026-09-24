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
// El * importa todas las clases del paquete de golpe
import org.w3c.dom.*;

public class Clase2 {

	/*
	 * ============================================================
	 *  RESUMEN DE LO VISTO
	 * ============================================================
	 *
	 *  SECUENCIAL (se lee/escribe en orden, de principio a fin)
	 *  --------------------------------------------------------
	 *   Texto
	 *     - Lectura:   FileReader + BufferedReader
	 *     - Escritura: FileWriter + BufferedWriter, o si no PrintWriter
	 *
	 *   Binario
	 *     - Lectura:   FileInputStream + DataInputStream   (tipos primitivos)
	 *                  FileInputStream + ObjectInputStream (objetos)
	 *     - Escritura: FileOutputStream + DataOutputStream
	 *                  FileOutputStream + ObjectOutputStream
	 *
	 *     Siempre desde la perspectiva del programa:
	 *       Input  → la información SALE del fichero y ENTRA al programa (leer)
	 *       Output → la información SALE del programa y va al fichero (escribir)
	 *
	 *  ALEATORIO (se salta directamente a una posición)
	 *  --------------------------------------------------------
	 *     - Lectura y escritura: RandomAccessFile
	 *
	 * ============================================================
	 *  ACCESO A DATOS
	 * ============================================================
	 *   1. XML   ← esta clase
	 *   2. CSV
	 *   3. JSON
	 *
	 * ============================================================
	 *  XML Y DOM
	 * ============================================================
	 *  Un XML es texto con ETIQUETAS que forman un árbol:
	 *
	 *    <agenda>                           ← RAÍZ (solo hay una)
	 *        <contacto>                     ← hijo de agenda
	 *            <nombre>Mario</nombre>     ← hijo de contacto; "Mario" es su texto
	 *            <telefono>600111222</telefono>
	 *        </contacto>
	 *    </agenda>
	 *
	 *  DOM = cargar el XML ENTERO en memoria como un árbol de objetos para
	 *  recorrerlo, buscar, modificar o borrar, y luego volver a guardarlo.
	 *
	 *   - Document → el documento completo
	 *   - Node     → cualquier cosa del árbol (etiqueta, texto, comentario...)
	 *   - Element  → un Node que es una ETIQUETA (<contacto>, <nombre>...)
	 *   - NodeList → lista de nodos: se recorre con getLength() e item(i)
	 *
	 *  CRUD en XML con DOM (los 4 métodos que hay en esta clase):
	 *   - Leer/Buscar → getElementsByTagName + getTextContent
	 *   - Añadir      → createElement + setTextContent + appendChild + guardar
	 *   - Modificar   → setTextContent sobre el nodo existente + guardar
	 *   - Eliminar    → removeChild + guardar
	 */

	public static void main(String[] args) throws Exception {
		// throws Exception en el main: si algo falla, el programa se para y
		// muestra el error. agenda.xml debe estar en la RAÍZ del proyecto.
		leerAgenda("agenda.xml");
		Buscar("Mario", "agenda.xml");
		Buscar("Pepe", "agenda.xml");                        // no existe → mensaje de no encontrado
		eliminar("José María", "agenda.xml");                // OJO: modifica el fichero de verdad
		nuevoContacto("Sofia", "756478654", "agenda.xml");   // la 2ª ejecución dirá que ya existe
		CambiarNumero("Sofia", "348758734", "agenda.xml");
		leerAgenda("agenda.xml");                            // comprobamos cómo ha quedado
	}

	// ---------------------------------------------------------------
	// CARGAR EL XML (forma corta, en un método aparte)
	// ---------------------------------------------------------------
	// Hace exactamente lo mismo que las 3 primeras líneas de leerAgenda,
	// Buscar y eliminar. Las dos formas funcionan igual.
	public static Document cargarXML(String fichero) throws Exception {
		DocumentBuilderFactory BF = DocumentBuilderFactory.newInstance();
		DocumentBuilder builder = BF.newDocumentBuilder();
		return builder.parse(fichero);
	}

	// ---------------------------------------------------------------
	// LEER TODA LA AGENDA
	// ---------------------------------------------------------------
	public static void leerAgenda(String fichero) throws Exception {
		System.out.println("===== AGENDA =====");
		// 1. La FACTORÍA: fabrica "constructores" de documentos.
		//    No se crea con new, sino con newInstance().
		DocumentBuilderFactory BF = DocumentBuilderFactory.newInstance();
		// 2. El BUILDER: el objeto que sabe leer un XML.
		DocumentBuilder builder = BF.newDocumentBuilder();
		// 3. parse() lee el fichero y devuelve el árbol entero en un Document.
		Document doc = builder.parse(fichero);
		// Alternativa con el método de arriba (sustituye a las 3 líneas):
		// Document doc = cargarXML(fichero);

		// Lista con todas las etiquetas <contacto> del documento
		NodeList listaContactos = doc.getElementsByTagName("contacto");

		// NodeList no admite for-each: se recorre con getLength() e item(i)
		for (int i = 0; i < listaContactos.getLength(); i++) {
			// item(i) devuelve un Node genérico...
			Node nodo = listaContactos.item(i);
			// ...y lo convertimos (cast) a Element para poder buscar dentro de él
			Element contacto = (Element) nodo;

			// Dentro de ESTE contacto:
			//   getElementsByTagName("nombre") → sus etiquetas <nombre>
			//   item(0)                        → la primera (solo hay una). ¡0 sin comillas!
			//   getTextContent()               → el texto de dentro: "Mario"
			String nombre = contacto.getElementsByTagName("nombre").item(0).getTextContent();
			String telefono = contacto.getElementsByTagName("telefono").item(0).getTextContent();

			// En printf lo habitual es poner %s y pasar los valores detrás
			System.out.printf("Nombre : %s%nTelefono : %s%n", nombre, telefono);
		}
		System.out.println();
	}

	// ---------------------------------------------------------------
	// BUSCAR UN CONTACTO POR NOMBRE
	// ---------------------------------------------------------------
	// Nota de estilo: en Java los métodos van en minúscula (buscar).
	// Se puede cambiar con clic derecho → Refactor → Rename.
	public static void Buscar(String nombreB, String fichero) throws Exception {
		DocumentBuilderFactory BF = DocumentBuilderFactory.newInstance();
		DocumentBuilder builder = BF.newDocumentBuilder();
		Document doc = builder.parse(fichero);
		// Alternativa: Document doc = cargarXML(fichero);

		NodeList listaContactos = doc.getElementsByTagName("contacto");
		String nombre;
		String telefono;
		boolean encontrado = false; // bandera: pasa a true si lo encontramos

		for (int i = 0; i < listaContactos.getLength(); i++) {
			Node nodo = listaContactos.item(i);
			Element contacto = (Element) nodo;

			nombre = contacto.getElementsByTagName("nombre").item(0).getTextContent();
			telefono = contacto.getElementsByTagName("telefono").item(0).getTextContent();

			// equalsIgnoreCase: compara sin importar mayúsculas ("mario" = "Mario")
			if (nombreB.equalsIgnoreCase(nombre)) {
				System.out.println("Telefono de " + nombre + " : " + telefono);
				encontrado = true;
			}
		}

		// Solo al terminar el bucle sabemos seguro que no estaba
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
	// Sin el paso 2 el fichero no cambia.
	public static void eliminar(String nombreB, String fichero) throws Exception {
		DocumentBuilderFactory BF = DocumentBuilderFactory.newInstance();
		DocumentBuilder builder = BF.newDocumentBuilder();
		Document doc = builder.parse(fichero);
		// Alternativa: Document doc = cargarXML(fichero);

		NodeList listaContactos = doc.getElementsByTagName("contacto");
		String nombre;
		boolean encontrado = false;

		for (int i = 0; i < listaContactos.getLength(); i++) {
			Node nodo = listaContactos.item(i);
			Element contacto = (Element) nodo;

			nombre = contacto.getElementsByTagName("nombre").item(0).getTextContent();

			if (nombreB.equalsIgnoreCase(nombre)) {
				encontrado = true;

				// getDocumentElement() devuelve la RAÍZ (<agenda>).
				// Un nodo no se borra a sí mismo: se lo pide a su padre.
				// Funciona porque <contacto> es hijo directo de <agenda>.
				Element raiz = doc.getDocumentElement();
				raiz.removeChild(contacto);
				System.out.println("El contacto " + nombre + " ha sido eliminado");

				// La lista es "viva": al borrar, los contactos siguientes bajan
				// una posición y el i++ se saltaría uno. Restando 1 volvemos a
				// revisar esa misma posición.
				i--;
			}
		}

		// Guardamos SOLO si hemos borrado algo, y una sola vez
		if (encontrado) {
			// Igual que al leer: primero la factoría, luego el objeto que trabaja.
			// newTransformer() se llama sobre el OBJETO, no sobre la clase.
			TransformerFactory transformerFactor = TransformerFactory.newInstance();
			Transformer transformer = transformerFactor.newTransformer();

			// Que el XML se guarde con saltos de línea y sangría ("yes" en minúsculas)
			transformer.setOutputProperty(OutputKeys.INDENT, "yes");
			// Nº de espacios de sangría
			transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "4");

			// DOMSource    → de DÓNDE sale la información (el árbol en memoria)
			// StreamResult → a DÓNDE va (el fichero)
			DOMSource source = new DOMSource(doc);
			StreamResult result = new StreamResult(fichero);
			// transform() convierte el árbol en texto XML y lo escribe en el fichero
			transformer.transform(source, result);
		} else {
			System.out.println("No se ha encontrado al contacto " + nombreB);
		}
	}

	// ---------------------------------------------------------------
	// AÑADIR UN CONTACTO NUEVO
	// ---------------------------------------------------------------
	// Pasos:
	//   1. Comprobar que no existe ya un contacto con ese nombre
	//   2. Crear las etiquetas nuevas en MEMORIA:
	//        <contacto>
	//            <nombre>nuevoNombre</nombre>
	//            <telefono>telefonoN</telefono>
	//        </contacto>
	//   3. Colgarlas de la raíz <agenda>
	//   4. GUARDAR el árbol en el fichero
	public static void nuevoContacto(String nuevoNombre, String telefonoN, String fichero) throws Exception {
		Document doc = cargarXML(fichero);
		NodeList listaContactos = doc.getElementsByTagName("contacto");
		boolean encontrado = false;

		// 1. BUSCAR SI YA EXISTE
		// "&& encontrado == false" hace que el bucle pare en cuanto lo encuentre
		// (también se puede escribir más corto: && !encontrado)
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
			// 2. CREAR LOS NODOS NUEVOS
			// Los nodos se crean a través del Document (doc.createElement), nunca
			// con new. Al crearlos todavía están "sueltos", fuera del árbol.
			Element Nuevocontacto = doc.createElement("contacto");

			Element Elementonombre = doc.createElement("nombre");
			// setTextContent pone el texto entre las etiquetas: <nombre>Sofia</nombre>
			Elementonombre.setTextContent(nuevoNombre);

			Element Elementotelefono = doc.createElement("telefono");
			Elementotelefono.setTextContent(telefonoN);

			// appendChild(hijo) mete un nodo DENTRO de otro, al final.
			// Metemos <nombre> y <telefono> dentro de <contacto>
			Nuevocontacto.appendChild(Elementonombre);
			Nuevocontacto.appendChild(Elementotelefono);

			// 3. COLGARLO DEL ÁRBOL: al final de la raíz <agenda>
			Element raiz = doc.getDocumentElement();
			raiz.appendChild(Nuevocontacto);

			// 4. GUARDAR EN EL FICHERO (igual que en eliminar)
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
	//      (no se crea ni se borra nada: la etiqueta es la misma)
	//   3. GUARDAR el árbol en el fichero, solo si lo hemos encontrado
	public static void CambiarNumero(String nombreC, String telefonoN, String fichero) throws Exception {
		Document doc = cargarXML(fichero);
		NodeList listaContactos = doc.getElementsByTagName("contacto");
		boolean encontrado = false;

		// 1. BUSCAR (el bucle para en cuanto lo encuentra)
		for (int i = 0; i < listaContactos.getLength() && encontrado == false; i++) {
			Node nodo = listaContactos.item(i);
			Element contacto = (Element) nodo;
			String nombre = contacto.getElementsByTagName("nombre").item(0).getTextContent();

			if (nombreC.equalsIgnoreCase(nombre)) {
				encontrado = true;

				// 2. MODIFICAR
				// CORREGIDO: antes ponía item('0') con comillas simples. '0' es un
				// CARÁCTER, y Java lo convierte a su código numérico (48), así que
				// pedía el elemento 48 → null → NullPointerException.
				// El índice es el número 0, sin comillas.
				Node telefono = contacto.getElementsByTagName("telefono").item(0);
				String telefonoViejo = telefono.getTextContent();

				//   getTextContent()      → LEE el texto
				//   setTextContent(texto) → lo SUSTITUYE por uno nuevo
				telefono.setTextContent(telefonoN);

				System.out.println("El telefono de " + nombre + " ha cambiado de "
						+ telefonoViejo + " a este telefono : " + telefonoN);
			}
		}

		// 3. GUARDAR (el cambio solo está en memoria hasta este paso)
		// CORREGIDO: los mensajes de este bloque estaban al revés.
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
