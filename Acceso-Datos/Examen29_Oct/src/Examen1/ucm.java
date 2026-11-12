package Examen1;

import java.util.List;

public class ucm {
	
	private String titulo;
	private int anio;
	private int fase;
	private String director;
	
	
	public ucm(String t,int n,int f,String dir) {
		this.titulo=t;
		this.anio=n;
		this.fase=f;
		this.director=dir;
		
	}
	
	public String getTitulo() {
		return titulo;
		
	}
	
	public int getAnio() {
		return anio;
		
	}
	
	
	public int getFase() {
		return fase;
	}
	
	public String getDir() {
		return director;
		
	}
	
    public void setNombre(String nombreB) { this.titulo = nombreB; }
    public void setTipos(int anyoB) { this.anio = anyoB; }
    public void setNivel(int faseB) { this.fase = faseB; }
	public void setDirector(String dirB) {
		this.director=dirB;
	}
	
	
	
	@Override 
	public String toString() {
		String linea = "Titulo : " + titulo ; 
		return linea;
	}
	
	

}
