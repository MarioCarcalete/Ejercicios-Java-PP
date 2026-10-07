package Clase1_json;

import java.util.List;

public class Contacto {
	private String nombre;
	private List<String> telefono;
	private String dni;

	// Ojo al orden: nombre, DNI, teléfono (igual que en el main)
	public Contacto(String n, String d, List<String> t) {   // ← List, no String
		this.nombre = n;
		this.dni = d;
		this.telefono = t;
	}

	public String getNombre() {
		return nombre;
	}

	public List<String> getTelefono() {                      // ← devuelve List
		return telefono;
	}

	public String getDni() {
		return dni;
	}

	public void setTelefono(List<String> telefono) {         // ← void
		this.telefono = telefono;
	}

	@Override
	public String toString() {
		
		String entrada = "Nombre : " + this.nombre + "\nDNI : " + this.dni + "\nTelefonos : ";
		for(String tlf : telefono) {
			entrada+="\n" + tlf;
			entrada +="\n";
		}
			return entrada;
		 
	}
}