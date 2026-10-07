package Clase1_json;

import java.io.FileReader;
import java.util.List;
import java.io.Reader;

import com.google.gson.Gson;

public class JsonAgenda2 {
	public static void main(String[] args) {
		String fichero = "agendaJ.json";
		try(Reader lc = new FileReader(fichero)){
			Gson gson = new Gson();
			Agenda agenda = gson.fromJson(lc, Agenda.class);
			List<Contacto> contactos = agenda.getContactos();
			System.out.println("Contactos : " + contactos.size());
			for(Contacto c : contactos)
				System.out.println(c);
		}catch(Exception e) {
			System.out.println("Error : " + e);
		}
	}

}
