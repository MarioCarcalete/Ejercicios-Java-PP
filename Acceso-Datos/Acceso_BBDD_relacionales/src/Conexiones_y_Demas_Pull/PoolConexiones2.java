
package Conexiones_y_Demas_Pull;

import java.sql.Connection;
import java.sql.SQLException;

import org.apache.commons.dbcp2.BasicDataSource;

public class PoolConexiones2 {
	private static BasicDataSource dataSource;
	
	
	static/*Inicializació estatica antes de que empieze el programa, las cosas que queremos que sepa antes que empieze*/ {
		dataSource = new BasicDataSource();
		String url = "jdbc:mysql://localhost:3307/classicmodels";
		String usuario ="profe";
		String pwd="abc123";
		dataSource.setUrl(url);
		dataSource.setUsername(usuario);
		dataSource.setPassword(pwd);
		
		
		dataSource.setInitialSize(5);
		// entrega conexion automatica, para no perder tiempo 
		dataSource.setMaxTotal(100);
		
		dataSource.setPoolPreparedStatements(true);
		dataSource.setMaxOpenPreparedStatements(50);
		// maximos preparad statements cache para lgo ser mas rapidas al repetir  , son mejor mas seguros que statements
		
		
		
	}
	public static Connection getConnection() throws SQLException{
		return dataSource.getConnection();
		
		
	}

}
