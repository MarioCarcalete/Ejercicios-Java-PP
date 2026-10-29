package Ejercicio3_Boletín;


import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;


import Conexiones_y_Demás_Pull.PoolConexiones_classic;

public class Ejercicio3 {
	public static void main(String[] args) {
		Stock(500);
	}
	
	public static void Stock(int numeroP) {
		
		String consulta = " Select products.productName,products.quantityInStock, sum(orderdetails.quantityOrdered) AS total_pedido  from products join orderdetails on products.productCode = orderdetails.productCode  where products.quantityInStock <= ? " + " group by products.productCode";
		
		try(Connection con = PoolConexiones_classic.getConnection()){
			PreparedStatement sql = con.prepareStatement(consulta);
			System.out.println("Conexión realizada ");
			
			sql.setInt(1, numeroP);
			try(ResultSet resultado = sql.executeQuery()){
				boolean dentro = false;
				System.out.println("Productos con menos stock que " + numeroP);
				while (resultado.next()) {
				dentro = true;
				String nombre = resultado.getString("productName");
				int stock = resultado.getInt("quantityInStock");
				int cantidadDeOrdenes = resultado.getInt("total_pedido");
				System.out.println(nombre + " su stock es : " + stock + " Ha sido pedido : " + cantidadDeOrdenes);
				}
				
				if(!dentro) {
					System.out.println("NO hay ningun producto con stock menor que  " + numeroP);
				}
			}
			
			
			
			
			
		}catch(SQLException e) {
			System.out.println("Error : " + e);
		}
	}

}
