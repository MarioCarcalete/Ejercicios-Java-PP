package Clase1_json;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.Reader;
import java.io.Writer;
import java.util.List;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class JSON {

	public static void main(String[] args) {
		final String ruta = "agendaJ.json"; // debe estar en la RAÍZ del proyecto
		leerAgenda(ruta);
		Contacto nuevo = new Contacto("José María", "1213234435T", "989348945");
		Contacto nuevo2=new Contacto("Marta","435485845N","83482383");
		crearContacto(nuevo, ruta);
		crearContacto(nuevo2,ruta);// la 2ª ejecución dirá que ya existe
		leerAgenda(ruta);                  // comprobamos cómo ha quedado
	}

	// ---------------------------------------------------------------
	// CARGAR: lee el fichero JSON y devuelve la lista de contactos
	// ---------------------------------------------------------------
	// Gson convierte el JSON en un objeto Agenda, y de ahí sacamos la lista.
	// Si algo falla (no existe el fichero, JSON mal escrito...) devuelve null.
	public static List<Contacto> cargarListaContactos(String ruta) {
		List<Contacto> contactos = null;
		try (Reader lector = new FileReader(ruta)) {
			Gson gson = new Gson();
			Agenda agenda = gson.fromJson(lector, Agenda.class);
			contactos = agenda.getContactos();
		} catch (Exception e) {
			System.out.println("Error : " + e);
		}
		return contactos;
	}

	// ---------------------------------------------------------------
	// GUARDAR: escribe la lista de contactos en el fichero JSON
	// ---------------------------------------------------------------
	// Metemos la lista en un objeto Agenda y Gson lo convierte a JSON.
	public static void guardarAgenda(List<Contacto> contactos, String ruta) {
		Agenda agenda = new Agenda();
		agenda.setContactos(contactos);
		try (Writer escritor = new FileWriter(ruta)) {
			// setPrettyPrinting → JSON con saltos de línea y sangría (más legible)
			Gson gson = new GsonBuilder().setPrettyPrinting().create();
			gson.toJson(agenda, escritor);
		} catch (Exception e) {
			System.out.println("Error : " + e);
		}
	}

	// ---------------------------------------------------------------
	// LEER TODA LA AGENDA
	// ---------------------------------------------------------------
	public static void leerAgenda(String ruta) {
		System.out.println("===== AGENDA =====");
		List<Contacto> contactos = cargarListaContactos(ruta);
		if (contactos != null) {
			for (Contacto c : contactos) {
				System.out.println(c); // usa el toString() de Contacto
			}
		}
		System.out.println();
	}

	// ---------------------------------------------------------------
	// AÑADIR UN CONTACTO NUEVO
	// ---------------------------------------------------------------
	// Pasos:
	//   1. Cargar la lista
	//   2. Comprobar que no existe ya un contacto con ese nombre
	//   3. Si no existe: añadirlo a la lista (memoria) y GUARDAR (disco)
	public static void crearContacto(Contacto nuevo, String ruta) {
		// 1. CARGAR
		List<Contacto> contactos = cargarListaContactos(ruta);
		if (contactos == null) {
			System.out.println("No se ha podido cargar la agenda");
			return;
		}

		// 2. BUSCAR SI YA EXISTE (el bucle para en cuanto lo encuentra)
		// trim() quita espacios del principio y del final: "José María " = "José María"
		boolean encontrado = false;
		for (int i = 0; i < contactos.size() && !encontrado; i++) {
			String nombre = contactos.get(i).getNombre().trim();
			if (nombre.equalsIgnoreCase(nuevo.getNombre().trim())) {
				encontrado = true;
			}
		}

		// 3. DECIDIR DESPUÉS DEL BUCLE, cuando ya hemos mirado todos
		if (encontrado) {
			System.out.println("Ya existe un contacto con el nombre " + nuevo.getNombre());
		} else {
			contactos.add(nuevo);           // solo en memoria...
			guardarAgenda(contactos, ruta); // ...y ahora al fichero
			System.out.println("Contacto " + nuevo.getNombre() + " añadido");
		}
	}
	
	
	public static void modificarTelefono(String nombre,String telefono,String ruta) {
		List<Contacto> contactos =cargarListaContactos(ruta);
		if(contactos==null) {
			System.out.println("NO se puede cargar");
		}
		boolean encontrado = false;
		
		for(int i =0;i<contactos.size();i++) {
			 if (contactos.get(i).getNombre().trim().equalsIgnoreCase(nombre.trim())) {
		            contactos.get(i).setTelefono();
		            encontrado = true;
		        }
		    }

		    if (encontrado) {
		        guardarAgenda(contactos, ruta);
		        System.out.println("Teléfono de " + nombre + " actualizado a " + telefono);
		    } else {
		        System.out.println("No se encontró ningún contacto con el nombre " + nombre);
		    }
		}
}
		