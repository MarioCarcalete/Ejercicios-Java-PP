package Examen1;

import java.util.List;

public class Pokemon {
	private String nombre;
	private List<String> tipos;
	private String id;
	private String nivel;
	
	public Pokemon(String n , List<String>tipo,String id,String niv) {
		this.nombre=n;
		this.tipos=tipo;
		this.id=id;
		this.nivel=niv;
	}
	
	
	public String getNombre() {
		return nombre;
	}
	
	public List<String> getTipos{
		return tipos
	}
	
	public String getId() {
		return id;
	}
	
	public String getNivel() {
		return nivel;
	}
	
	
	public void  setNombre(String nombreB) {
		this.nombre = nombreB;
	}
	public void setTipos(List<String>tiposB) {
		this.tipos=tiposB;
	}
	
	public void setId(String idN) {
		this.id=idN;
	}
	
	public void setNivel (String nivelB) {
		this.nivel=nivelB;
	}
}
