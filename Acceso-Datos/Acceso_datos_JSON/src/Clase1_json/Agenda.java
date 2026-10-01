package Clase1_json;

import java.util.List;

import com.google.gson.annotations.SerializedName;

public class Agenda {

	@SerializedName("agenda")         // en el JSON la clave se llama "agenda"...
	private List<Contacto> contactos; // ...y en Java el atributo se llama "contactos"

	public List<Contacto> getContactos() {
		return contactos;
	}

	public void setContactos(List<Contacto> contactos) {
		this.contactos = contactos;
	}
	
}