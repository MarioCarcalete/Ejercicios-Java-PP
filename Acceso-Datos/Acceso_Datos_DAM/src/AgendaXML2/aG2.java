package AgendaXML2;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

public class aG2 {
	public static void main(String[] args) {
		String fichero = "agenda.xml";
		try {
			Document doc = leerAgenda(fichero);
			NodeList listaContactos = doc.getElementsByTagName("contacto");
			System.out.println("Contactos : " + listaContactos.getLength());
			for (int i = 0; i < listaContactos.getLength(); i++) {
				Element contacto = (Element) listaContactos.item(i);
				String nombre = contacto.getElementsByTagName("nombre").item(0).getTextContent();
				System.out.println(nombre);
				NodeList telefonos = contacto.getElementsByTagName("telefono");   // ← 2. contacto, no doc
				for (int j = 0; j < telefonos.getLength(); j++) {
					Element telefono = (Element) telefonos.item(j);                // ← 3. telefonos, no listaContactos
					String tlf = telefono.getTextContent();
					String tipo = telefono.getAttribute("tipo"); 
					System.out.println("-" + tipo + ": " + tlf);
					//if tipo is null o no existe solo numero sino , se pone todo 
				}
			}
		} catch (Exception e) {
			System.out.println("Error : " + e);
		}
	}

	public static Document leerAgenda(String fichero) throws Exception {
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
		return doc;                                                                // ← 1. faltaba el return
	}

	public static Document cargarXML(String fichero) throws Exception {
		DocumentBuilderFactory BF = DocumentBuilderFactory.newInstance();
		DocumentBuilder builder = BF.newDocumentBuilder();
		return builder.parse(fichero);
	}
}
																									