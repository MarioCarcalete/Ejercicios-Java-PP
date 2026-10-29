package Clase1BBDDRelacionales;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import Conexiones_y_Demas_Pull.PoolConexiones;

public class Clase1 {
	public static void main(String[] args) {
		
		
		try{
			Connection conn = PoolConexiones.getConnection();
			System.out.println("Conexion realizada");
			conn.setAutoCommit(false);
			Statement sql = conn.createStatement();
			
			sql.executeUpdate("Delete from alumnos where email= 'lucia.fernandez@email.com' ");
			

			ResultSet resultado2 = sql.executeQuery("Select * from alumnos");
			while(resultado2.next()) {
				String nombre = resultado2.getString("nombre");
				int edad = resultado2.getInt("edad");
				
				System.out.println("Nombre : " + nombre + "\nEdad : " + edad);

				
			}
			conn.commit();
			conn.close();
		}catch(SQLException e) {
			e.printStackTrace();
		}
	}

}

/*
 * Al poner el Autocomit false no se hace el commit y no se guarda solo modifica lo que se ve en la terminal pero si hacemos el commit se guarda de verdad 
 * 
 * 
 * */
