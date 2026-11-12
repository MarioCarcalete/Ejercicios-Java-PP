package Json_t_demás;

import java.util.List;

public class pokemon {
	
	private String nombre;
	
	private List<String> tipos;
	
	private int nivel;
	
	
	public pokemon(String n,List<String> t,int ni) {
		this.nombre=n;
		this.tipos=t;
		this.nivel=ni;
	}
	
	public String  getNombre() {
		return nombre;
	}
	
	public List<String> getTipo() {
		return tipos;
	}
	
	public int getNivel() {
		return nivel;
	}
	
	public  void setNombre(String nombreb) {
		this.nombre=nombreb;
	}
	public  void setTipo(List<String> tipoB) {
		this.tipos=tipoB;
	}
	public  void setNivel(int nivelB) {
		this.nivel=nivelB;
	}
	public void anyadirTipo(String tipo) { 
		tipos.add(tipo); } 
	
	@Override
	
	public String toString() {
		String  linea  = "Nombre:  " + nombre +  "\ntipo : " +  tipos  + "\nnivel :  " +  nivel + "\n";
	return linea;
	}

}

