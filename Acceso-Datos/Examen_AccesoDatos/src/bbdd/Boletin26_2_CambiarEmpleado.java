package bbdd;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/*
 * BOLETÍN 26 · EJERCICIO 2 (classicmodels)
 * Cambia el empleado (salesRepEmployeeNumber) de un cliente. Muestra el antiguo y el nuevo.
 * Casos: cliente "Morales" no existe · empleado 666 no existe ·
 *        Warburg Exchange no tiene empleado (columna NULL -> wasNull())
 *        Kelly's Gift Shop tiene a Peter Marsh.
 *
 * CÓMO FUNCIONA (receta SECUENCIAL):
 *  1) ¿existe el cliente? -> leo su empleado actual (getInt + wasNull para saber si era NULL)
 *  2) ¿existe el empleado nuevo?
 *  3) mostrar el antiguo (o "no tenía") y hacer el UPDATE al final
 */
public class Boletin26_2_CambiarEmpleado {

    public static void main(String[] args) {
        cambiarEmpleado("Morales", 1370);
        cambiarEmpleado("Kelly's Gift Shop", 666);
        cambiarEmpleado("Kelly's Gift Shop", 1370);   // Peter Marsh -> Gerard Hernandez
        cambiarEmpleado("Kelly's Gift Shop", 1612);   // lo devuelvo a Peter Marsh
        cambiarEmpleado("Warburg Exchange", 1370);    // no tenía empleado (¡cambia la BBDD! para deshacerlo:
        //   UPDATE customers SET salesRepEmployeeNumber = NULL WHERE customerName = 'Warburg Exchange';)
    }

    public static void cambiarEmpleado(String cliente, int nuevo) {
        String qCliente  = "SELECT customerNumber, salesRepEmployeeNumber FROM customers WHERE customerName = ?";
        String qEmpleado = "SELECT firstName, lastName FROM employees WHERE employeeNumber = ?";
        String qUpdate   = "UPDATE customers SET salesRepEmployeeNumber = ? WHERE customerNumber = ?";

        try (Connection con = PoolConexiones_classic.getConnection();
             PreparedStatement psCli = con.prepareStatement(qCliente);
             PreparedStatement psEmp = con.prepareStatement(qEmpleado);
             PreparedStatement psUpd = con.prepareStatement(qUpdate)) {

            int numCliente, antiguo;
            boolean sinEmpleado;
            psCli.setString(1, cliente);                                       // 1)
            try (ResultSet rs = psCli.executeQuery()) {
                if (!rs.next()) { System.out.println("El cliente " + cliente + " no existe"); return; }
                numCliente = rs.getInt("customerNumber");
                antiguo = rs.getInt("salesRepEmployeeNumber");               // si es NULL devuelve 0...
                sinEmpleado = rs.wasNull();                                  // ...y wasNull() me dice que era NULL
            }

            String nombreNuevo = nombreEmpleado(psEmp, nuevo);                // 2)
            if (nombreNuevo == null) { System.out.println("El empleado " + nuevo + " no existe"); return; }

            if (sinEmpleado) System.out.println(cliente + " no tenía ningún empleado asignado");   // 3)
            else System.out.println("Empleado antiguo de " + cliente + ": " + nombreEmpleado(psEmp, antiguo));

            psUpd.setInt(1, nuevo);
            psUpd.setInt(2, numCliente);
            psUpd.executeUpdate();
            System.out.println("Empleado nuevo de " + cliente + ": " + nombreNuevo);
        } catch (SQLException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    // "Nombre Apellido" del empleado, o null si no existe (reutilizable)
    private static String nombreEmpleado(PreparedStatement ps, int num) throws SQLException {
        ps.setInt(1, num);
        try (ResultSet rs = ps.executeQuery()) {
            return rs.next() ? rs.getString("firstName") + " " + rs.getString("lastName") : null;
        }
    }
}
