package Clase1BBDDRelacionales;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class Clase1 {
	public static void main(String[] args) {
		String url = "jdbc:mysql://localhost:3306/dam2";
		String usuario ="profe";
		String pwd="abc123";
		
		try{
			Connection conn = DriverManager.getConnection(url,usuario,pwd);
			System.out.println("Conexion realizada");
			Statement sql = conn.createStatement();
			

			ResultSet resultado2 = sql.executeQuery("Select * from alumnos");
			while(resultado2.next()) {
				String nombre = resultado2.getString("nombre");
				int edad = resultado2.getInt("edad");
				
				System.out.println("Nombre : " + nombre + "\nEdad : " + edad);

				
			}
			conn.close();
		}catch(SQLException e) {
			e.printStackTrace();
		}
	}

}
