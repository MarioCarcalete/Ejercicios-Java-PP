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
	 */

	public static void main(String[] args) throws Exception {
		// throws Exception en el main: si algo falla, el programa se para y
		// muestra el error. agenda.xml debe estar en la RAÍZ del proyecto.
		leerAgenda("agenda.xml");
		Buscar("Mario", "agenda.xml");
		Buscar("Pepe", "agenda.xml");          // no existe → mensaje de no encontrado
		eliminar("José María", "agenda.xml");  // OJO: modifica el fichero de verdad
		nuevoContacto("Sofia","756478654","agenda.xml");
	}

	// ---------------------------------------------------------------
	// CARGAR EL XML (forma corta, en un método aparte)
	// ---------------------------------------------------------------
	// Hace exactamente lo mismo que las 3 primeras líneas de cada método de abajo.
	// En cada método dejo tu forma original y, comentada, la alternativa que usa
	// este método. Las dos funcionan igual: elige la que prefieras.
	public static Document cargarXML(String fichero) throws Exception {
		DocumentBuilderFactory BF = DocumentBuilderFactory.newInstance();
		DocumentBuilder builder = BF.newDocumentBuilder();
		return builder.parse(fichero);
	}

	// ---------------------------------------------------------------
	// LEER TODA LA AGENDA
	// ---------------------------------------------------------------
	public static void leerAgenda(String fichero) throws Exception {
		// 1. La FACTORÍA: fabrica "constructores" de documentos.
		//    No se crea con new, sino con newInstance().
		DocumentBuilderFactory BF = DocumentBuilderFactory.newInstance();
		// 2. El BUILDER: el objeto que sabe leer un XML.
		DocumentBuilder builder = BF.newDocumentBuilder();
		// 3. parse() lee el fichero y devuelve el árbol entero en un Document.
		Document doc = builder.parse(fichero);
		// Alternativa con el método de arriba (sustituye a las 3 líneas):
		// Document doc = cargarXML(fichero);

		// creamos una lista iterable con todas las etiquetas <contacto>
		// del documento (el texto entre comillas es el nombre de la etiqueta)
		NodeList listaContactos = doc.getElementsByTagName("contacto");

		// NodeList no admite for-each: se recorre con getLength() e item(i)
		for (int i = 0; i < listaContactos.getLength(); i++) {
			// item(i) devuelve un Node genérico...
			Node nodo = listaContactos.item(i);
			// ...y lo convertimos (cast) a Element para poder buscar dentro de él
			Element contacto = (Element) nodo;

			// Dentro de ESTE contacto:
			//   getElementsByTagName("nombre") → sus etiquetas <nombre>
			//   item(0)                        → la primera (solo hay una)
			//   getTextContent()               → el texto de dentro: "Mario"
			String nombre = contacto.getElementsByTagName("nombre").item(0).getTextContent();
			String telefono = contacto.getElementsByTagName("telefono").item(0).getTextContent();

			// Funciona tal cual lo tenías. En printf lo habitual es poner %s y
			// pasar los valores detrás: printf("Nombre : %s%nTelefono : %s%n", nombre, telefono);
			System.out.printf("Nombre : " + nombre + "%nTelefono : " + telefono + "%n");
		}
	}

	/*
	 * Hasta aquí hemos creado un método para leer y para buscar un contacto.
	 * Vamos también a ver cómo se borra en XML.
	 */

	// ---------------------------------------------------------------
	// BUSCAR UN CONTACTO POR NOMBRE
	// ---------------------------------------------------------------
	// Funciona con B mayúscula, pero por convenio en Java los métodos empiezan
	// en minúscula (buscar). La mayúscula inicial se reserva para las clases.
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
				System.out.println("Telefono : " + telefono);
				encontrado = true;
			}
		}

		// Solo al terminar el bucle sabemos seguro que no estaba
		if (!encontrado) {
			System.out.println("No se ha encontrado al contacto ");
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
		String telefono;
		boolean encontrado = false;

		for (int i = 0; i < listaContactos.getLength(); i++) {
			Node nodo = listaContactos.item(i);
			Element contacto = (Element) nodo;

			nombre = contacto.getElementsByTagName("nombre").item(0).getTextContent();
			telefono = contacto.getElementsByTagName("telefono").item(0).getTextContent();

			if (nombreB.equalsIgnoreCase(nombre)) {
				System.out.println("Telefono : " + telefono);
				encontrado = true;

				// getDocumentElement() devuelve la RAÍZ (<agenda>).
				// Un nodo no se borra a sí mismo: se lo pide a su padre.
				// Funciona porque <contacto> es hijo directo de <agenda>.
				Element raiz = doc.getDocumentElement();
				raiz.removeChild(contacto);
				System.out.println("El contacto  : " + nombre + " ha sido eliminado ");

				// AJUSTE: la lista es "viva". Al borrar, los contactos siguientes
				// bajan una posición, y el i++ del bucle se saltaría al siguiente.
				// Restando 1 volvemos a revisar esa misma posición.
				i--;

				// AJUSTE: el guardado ya NO va aquí dentro. Así, si se borran
				// varios, el fichero se guarda una sola vez al final.
			}
		}

		// AJUSTE: guardamos SOLO si hemos borrado algo (si no, no hay cambios)
		if (encontrado) {
			// Igual que al leer: primero la factoría, luego el objeto que trabaja
			TransformerFactory transformerFactor = TransformerFactory.newInstance();
			// AJUSTE (error de compilación): antes ponía TransformerFactory.newTransformer(),
			// llamando a la CLASE. newTransformer() hay que llamarlo sobre el OBJETO
			// transformerFactor que acabamos de crear.
			Transformer transformer = transformerFactor.newTransformer();

			// Que el XML se guarde con saltos de línea y sangría.
			// AJUSTE: el valor es "yes" en minúsculas, no "YES".
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
			System.out.println("No se ha encontrado al contacto ");
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
		//
		// AJUSTE: hace falta "throws Exception" porque cargarXML() y transform()
		// pueden lanzar excepciones. Sin esto, Eclipse te lo marca en rojo.
		public static void nuevoContacto(String nuevoNombre, String telefonoN, String fichero) throws Exception {
			Document doc = cargarXML(fichero);
			NodeList listaContactos = doc.getElementsByTagName("contacto");
			boolean encontrado = false;

			// 1. BUSCAR SI YA EXISTE
			// La condición "&& encontrado == false" hace que el bucle pare en cuanto
			// lo encuentre: no tiene sentido seguir buscando.
			// (También se puede escribir más corto: && !encontrado)
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
				// Los nodos siempre se crean a través del Document (doc.createElement),
				// nunca con new. Al crearlos todavía están "sueltos", fuera del árbol.
				Element Nuevocontacto = doc.createElement("contacto");

				Element Elementonombre = doc.createElement("nombre");
				// setTextContent pone el texto entre las etiquetas: <nombre>Ana</nombre>
				Elementonombre.setTextContent(nuevoNombre);

				Element Elementotelefono = doc.createElement("telefono");
				Elementotelefono.setTextContent(telefonoN);

				// appendChild(hijo) mete un nodo DENTRO de otro, al final.
				// Metemos <nombre> y <telefono> dentro de <contacto>
				Nuevocontacto.appendChild(Elementonombre);
				Nuevocontacto.appendChild(Elementotelefono);

				// 3. COLGARLO DEL ÁRBOL
				// Lo añadimos al final de la raíz <agenda>. Hasta este momento el
				// contacto existía pero no formaba parte del documento.
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
}
