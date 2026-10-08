package Clase1_json;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.Reader;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class JSON {

	public static void main(String[] args) {
		final String ruta = "agendaJ.json"; // debe estar en la RAÍZ del proyecto
		leerAgenda(ruta);

		// El constructor pide una List<String>, así que creamos la lista con los 2 teléfonos.
		// new ArrayList<>(...) para que la lista se pueda modificar después
		Contacto nuevo  = new Contacto("José María", "1213234435T",
				new ArrayList<>(List.of("989348945", "600123123")));
		Contacto nuevo2 = new Contacto("Marta", "435485845N",
				new ArrayList<>(List.of("83482383", "611222333")));

		crearContacto(nuevo, ruta);
		crearContacto(nuevo2, ruta); // la 2ª ejecución dirá que ya existe
		leerAgenda(ruta);

		// Cambiar el 2º teléfono de Marta (0 = primero, 1 = segundo)
		modificarTelefono("Marta", 1, "699999999", ruta);
		leerAgenda(ruta);
	}

	// ---------------------------------------------------------------
	// CARGAR: lee el fichero JSON y devuelve la lista de contactos
	// ---------------------------------------------------------------
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
	public static void guardarAgenda(List<Contacto> contactos, String ruta) {
		Agenda agenda = new Agenda();
		agenda.setContactos(contactos);
		try (Writer escritor = new FileWriter(ruta)) {
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
				System.out.println(c);
			}
		}
		System.out.println();
	}

	// ---------------------------------------------------------------
	// AÑADIR UN CONTACTO NUEVO
	// ---------------------------------------------------------------
	public static void crearContacto(Contacto nuevo, String ruta) {
		List<Contacto> contactos = cargarListaContactos(ruta);
		if (contactos == null) {
			System.out.println("No se ha podido cargar la agenda");
			return;
		}

		boolean encontrado = false;
		for (int i = 0; i < contactos.size() && !encontrado; i++) {
			String nombre = contactos.get(i).getNombre().trim();
			if (nombre.equalsIgnoreCase(nuevo.getNombre().trim())) {
				encontrado = true;
			}
		}

		if (encontrado) {
			System.out.println("Ya existe un contacto con el nombre " + nuevo.getNombre());
		} else {
			contactos.add(nuevo);
			guardarAgenda(contactos, ruta);
			System.out.println("Contacto " + nuevo.getNombre() + " añadido");
		}
	}

	// ---------------------------------------------------------------
	// MODIFICAR UN TELÉFONO (posicion: 0 = primero, 1 = segundo)
	// ---------------------------------------------------------------
	public static void modificarTelefono(String nombre, int posicion, String telefono, String ruta) {
		List<Contacto> contactos = cargarListaContactos(ruta);
		if (contactos == null) {
			System.out.println("No se ha podido cargar la agenda");
			return; // sin esto, el for daría NullPointerException
		}

		boolean encontrado = false;
		for (int i = 0; i < contactos.size() && !encontrado; i++) {
			Contacto c = contactos.get(i);
			if (c.getNombre().trim().equalsIgnoreCase(nombre.trim())) {
				encontrado = true;

				// Cogemos su lista de teléfonos, cambiamos el de esa posición y se la volvemos a poner
				List<String> telefonos = new ArrayList<>(c.getTelefono());
				if (posicion >= 0 && posicion < telefonos.size()) {
					telefonos.set(posicion, telefono);
					c.setTelefono(telefonos);
					guardarAgenda(contactos, ruta);
					System.out.println("Teléfono " + (posicion + 1) + " de " + nombre + " actualizado a " + telefono);
				} else {
					System.out.println(nombre + " no tiene teléfono en la posición " + posicion);
				}
			}
		}

		if (!encontrado) {
			System.out.println("No se encontró ningún contacto con el nombre " + nombre);
		}
	}
}