package MigrarXMLJSON;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class Tarea1 {
	public static void main(String[] args) throws Exception {
		
		leerAgenda("/home/alumno/eclipse-workspaceShadow/Acceso_Datos_DAM/agenda.xml");
	}
	
	
	
		public static void leerAgenda(String ruta) throws Exception {
			System.out.println("===== AGENDA =====");
			// 1. La FACTORÍA: fabrica "constructores" de documentos (newInstance, no new)
			DocumentBuilderFactory BF = DocumentBuilderFactory.newInstance();
			// 2. El BUILDER: el objeto que sabe leer un XML
			DocumentBuilder builder = BF.newDocumentBuilder();
			// 3. parse() lee el fichero y devuelve el árbol entero en un Document
			Document doc = builder.parse(ruta);

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
	
}
