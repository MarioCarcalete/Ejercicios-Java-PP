package Clase1_json;

public class Contacto {
	private String nombre;
	private String telefono;
	private String dni;

	// Ojo al orden: nombre, DNI, teléfono (igual que en el main)
	public Contacto(String n, String d, String t) {
		this.nombre = n;
		this.dni = d;
		this.telefono = t;
	}

	public String getNombre() {
		return nombre;
	}

	public String getTelefono() {
		return telefono;
	}

	public String getDni() {
		return dni;
	}

	@Override
	public String toString() {
		return "Nombre : " + nombre + "\nDNI: " + dni + "\nTelefono : " + telefono;
	}
	
	public String setTelefono() {
		return telefono;
	}
}