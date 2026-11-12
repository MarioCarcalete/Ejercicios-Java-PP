package Examen1;

import java.io.FileReader;
import java.io.Reader;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import com.google.gson.Gson;

public class mainEjercicio2 {
	public static void main(String[] args) {
		
		final String ruta = "marvel.json";
		 //deJsonABbdd("datos/mig_origen.json");
		
		leerPokemon(ruta);
	        System.out.println("\n--- 15. deBbddAXml");
	}
	
	
	
	
	public static void leerPokemon (String ruta) {
		List<ucm> lista = cargarListaP(ruta);
		
		if(lista!=null) {
			for(ucm c : lista) {
				System.out.println(c);
			}
			System.out.println();
		}
		
		
	}
	  public static List<ucm> cargarListaP(String ruta) {
	        List<ucm> listaB = null;                          // si falla, devuelvo null

	        try (Reader lc = new FileReader(ruta)) {             // abro el fichero (se cierra solo)
	            Gson gson = new Gson();
	            Lista listaP = gson.fromJson(lc, Lista.class);   // JSON -> objeto raíz Pokedex
	            listaB = listaP.getUCM();                    // saco la lista de dentro
	        } catch (Exception e) {
	            System.out.println("Error : " + e);
	        }
	        return listaB;
	    }
	  
	  
	  public static void Crear_Base_De_Datos() throws SQLException {
		  String url = "jdbc:mysql://localhost:3306/classicmodels";
	        String usuario = "profe";
	        String pwd = "abc123";
		  
		  String consulta="create database if not exists ucm";
		  
		  
		  String consulta2 ="Create table if not exists peliculas(titulo VARCHAR(150) primary key,anio int not null,fase int not null,director varchar(100) not null" ;
		  
		  try(Connection conn = DriverManager.getConnection(url,usuario,pwd)){
			  PreparedStatement psO = conn.prepareStatement(consulta);
	             PreparedStatement psC  = conn.prepareStatement(consulta2);
	             
	            psO.executeUpdate();
	            
	              psC.executeUpdate();

	              
	              
	              
		  }
		  
		  
		  
		  
		  
		  
		 
		  
	  }
	
	
	

}
