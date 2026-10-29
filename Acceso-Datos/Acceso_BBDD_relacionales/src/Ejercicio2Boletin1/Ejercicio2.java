package Ejercicio2Boletin1;

// Imports de JDBC (siempre de java.sql, NUNCA de com.mysql...)
import java.sql.Connection;        // Representa la conexión con la base de datos
import java.sql.PreparedStatement; // Consulta SQL precompilada con parámetros "?"
import java.sql.ResultSet;         // Resultado de un SELECT (las filas que devuelve)
import java.sql.SQLException;      // Excepción que lanzan todos los métodos de JDBC

import Conexiones_y_Demas_Pull.PoolConexiones2; // Nuestra clase que nos da conexiones del pool

/*
 * EJERCICIO 2
 * Modificar el empleado asignado a un cliente (salesRepEmployeeNumber).
 * - Recibe el nombre del cliente y el código del nuevo empleado.
 * - Muestra el empleado antiguo y el nuevo.
 * - Controla: cliente que no existe, empleado que no existe y cliente sin empleado.
 *
 * ESTRUCTURA SECUENCIAL:
 *   1. Comprobar cliente   -> si falla, return
 *   2. Comprobar empleado  -> si falla, return
 *   3. Mostrar el antiguo
 *   4. UPDATE (solo cuando todo está comprobado)
 *   5. Mostrar el nuevo
 */
public class Ejercicio2 {

	public static void main(String[] args) {
		// Caso normal: Kelly's Gift Shop tiene a Peter Marsh y le ponemos el 1056
		ClienteACargo("Kelly's Gift Shop", 1056);

		// Casos de error para probar (descomentar de uno en uno):
		// ClienteACargo("Morales", 1056);            // el cliente no existe
		// ClienteACargo("Kelly's Gift Shop", 666);   // el empleado no existe
		// ClienteACargo("Warburg Exchange", 1056);   // el cliente no tiene empleado
	}

	public static void ClienteACargo(String nombre, int nuevoEmpleado) {

		// ---------- CONSULTAS SQL ----------
		// Cada "?" es un parámetro que rellenaremos luego con setString / setInt.

		// Busca el cliente y nos dice qué empleado tiene asignado (puede ser NULL)
		String consultaCliente = "SELECT salesRepEmployeeNumber FROM customers WHERE customerName = ?";

		// Busca un empleado por su código. La usamos DOS veces: para el nuevo y para el antiguo
		String consultaEmpleado = "SELECT firstName, lastName FROM employees WHERE employeeNumber = ?";

		// Cambia el empleado del cliente
		String actualizar = "UPDATE customers SET salesRepEmployeeNumber = ? WHERE customerName = ?";

		// ---------- TRY-WITH-RESOURCES ----------
		// Todo lo que se declara dentro de try( ... ) se cierra SOLO al terminar,
		// aunque haya un error o hagamos un return. Así no hace falta conn.close().
		try (Connection conn = PoolConexiones2.getConnection();                       // pedimos conexión al pool
			 PreparedStatement sqlCliente = conn.prepareStatement(consultaCliente);   // preparamos cada consulta
			 PreparedStatement sqlEmpleado = conn.prepareStatement(consultaEmpleado);
			 PreparedStatement sqlUpdate = conn.prepareStatement(actualizar)) {

			// ===== PASO 1: comprobar que el cliente existe y ver qué empleado tiene =====
			int antiguo;          // código del empleado antiguo
			boolean sinEmpleado;  // true si el cliente no tiene empleado asignado (NULL)

			sqlCliente.setString(1, nombre); // el primer "?" de consultaCliente = nombre del cliente

			try (ResultSet rs = sqlCliente.executeQuery()) { // executeQuery -> para SELECT
				// rs.next() mueve a la siguiente fila y devuelve false si no hay.
				// Esperamos UNA fila, así que usamos if, no while.
				if (!rs.next()) {
					System.out.println("Error: no existe el cliente " + nombre);
					return; // salimos del método; el try cierra todo solo
				}
				antiguo = rs.getInt("salesRepEmployeeNumber"); // leemos la columna
				// getInt devuelve 0 si la columna es NULL; wasNull() nos dice si era NULL de verdad
				sinEmpleado = rs.wasNull();
			} // aquí se cierra este ResultSet; ya tenemos los datos guardados en variables

			// ===== PASO 2: comprobar que el nuevo empleado existe =====
			String nombreNuevo; // guardamos su nombre para mostrarlo al final

			sqlEmpleado.setInt(1, nuevoEmpleado); // "?" = código del nuevo empleado

			try (ResultSet rs = sqlEmpleado.executeQuery()) {
				if (!rs.next()) { // no hay ninguna fila, así que el código es erróneo
					System.out.println("Error: no existe el empleado con código " + nuevoEmpleado);
					return; // salimos ANTES del UPDATE, así no se modifica nada
				}
				nombreNuevo = rs.getString("firstName") + " " + rs.getString("lastName");
			}

			// ===== PASO 3: mostrar el empleado antiguo =====
			if (sinEmpleado) {
				// Caso Warburg Exchange: el cliente existe pero no tenía empleado
				System.out.println("El cliente no tenía ningún empleado asignado");
			} else {
				// Reutilizamos el mismo PreparedStatement cambiando solo el parámetro
				sqlEmpleado.setInt(1, antiguo);
				try (ResultSet rs = sqlEmpleado.executeQuery()) {
					if (rs.next()) {
						System.out.println("Antiguo empleado: "
								+ rs.getString("firstName") + " " + rs.getString("lastName"));
					}
				}
			}

			// ===== PASO 4: hacer el UPDATE =====
			// Solo llegamos aquí si el cliente Y el empleado existen
			sqlUpdate.setInt(1, nuevoEmpleado); // primer "?"  -> SET salesRepEmployeeNumber = ?
			sqlUpdate.setString(2, nombre);     // segundo "?" -> WHERE customerName = ?
			sqlUpdate.executeUpdate();          // executeUpdate -> para UPDATE / INSERT / DELETE
			                                    // (devuelve un int con las filas modificadas)

			// ===== PASO 5: mostrar el nuevo empleado =====
			System.out.println("Nuevo empleado: " + nombreNuevo);

		} catch (SQLException e) {
			// Cualquier error de BBDD (conexión, SQL mal escrito...) acaba aquí
			System.out.println("Error: " + e.getMessage());
		}
	}
}
