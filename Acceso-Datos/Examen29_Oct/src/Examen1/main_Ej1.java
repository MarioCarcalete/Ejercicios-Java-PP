package Examen1;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.util.List;

//javax.xml.parsers → clases que LEEN el XML y lo convierten en un árbol
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
//javax.xml.transform → clases que ESCRIBEN el árbol de vuelta al fichero
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

//org.w3c.dom → las piezas del árbol XML: Document, Element, Node, NodeList...
import org.w3c.dom.*;


public class main_Ej1 {
	public static void main(String[] args) throws Exception {
		mostrar("pokedex.xml");
	}
	
	
	public static void mostrar(String fichero) throws Exception {
		Document doc = leerXML(fichero);

		NodeList listaP = doc.getElementsByTagName("pokemon");
		for (int i = 0; i < listaP.getLength(); i++) {
			Node nodo = listaP.item(i);
			
			Element pokemon = (Element) listaP.item(i);
			String id = pokemon.getAttribute("id");
			String nivel = pokemon.getAttribute("nivel");
			String nombre = pokemon.getElementsByTagName("nombre").item(0).getTextContent();
			String tipo = pokemon.getElementsByTagName("tipo").item(0).getTextContent();
			List<Pokemon> listaPP ;
			listaPP.add(List.of(nombre,tipo,nivel,id));
			

		}
	}
	
	public static Document leerXML(String fichero) throws Exception{
		DocumentBuilderFactory BF = DocumentBuilderFactory.newInstance();
		DocumentBuilder builder = BF.newDocumentBuilder();
		return (Document) builder.parse(fichero);
	}
	
	public static void pasar_a_txt(String origen,String destino) throws Exception {
		Document doc = leerXML(origen);
		List<Pokemon> listaPP ;
		
		try(BufferedWriter lc = new BufferedWriter(new FileWriter(destino))){
			System.out.println("===POKEDEX DEL PROFESOR OAF ===");
			
			
		}catch(Exception e) {
			System.out.println(e);
		}
		
		
		
	}
	

}


