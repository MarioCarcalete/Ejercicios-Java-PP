package Ejercicio2Boletin1;

import java.io.Serializable;

public class Personaje implements Serializable {
	private static final long serialVersionUID = 1L;

	private String titulo;
	private String nombre;
	
	public  Personaje(String n,String t){
		this.nombre=n;
	this.titulo=t;
	
	}
	
	public String getAnime() {
		return titulo;
	}
	
	public String getNombre() {
		return nombre;
	}
	
	public boolean estaCompleto() {
		return titulo !=null && !titulo.isBlank()
				&& nombre !=null && !nombre.isBlank();
	}
	
	public String tostring() {
		return nombre + " (" + titulo + ")";
		
	}

}
