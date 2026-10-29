package bbdd;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/*
 * BOLETÍN 26 · EJERCICIO 1 (classicmodels)
 * Recibe un apellido y lista los empleados con ese apellido y, para cada uno,
 * los empleados que le reportan (employees.reportsTo = su employeeNumber).
 *   Patterson: a Mary le reportan 4, a William 3, a Steve nadie. Morales no existe.
 *
 * CÓMO FUNCIONA (consulta ANIDADA):
 *  1) empleados con ese apellido (si ninguno -> "No existe ningún empleado...")
 *  2) DENTRO del while, por cada uno: SELECT ... WHERE reportsTo = su número
 *  3) si esa segunda consulta no da filas -> "No le reporta nadie"
 */
public class Boletin26_1_Reportan {

    public static void main(String[] args) {
        reportan("Patterson");
        reportan("Morales");
    }

    public static void reportan(String apellido) {
        String qJefes = "SELECT employeeNumber, firstName, lastName FROM employees WHERE lastName = ?";
        String qSubs  = "SELECT firstName, lastName FROM employees WHERE reportsTo = ?";

        try (Connection con = PoolConexiones_classic.getConnection();
             PreparedStatement psJefes = con.prepareStatement(qJefes);
             PreparedStatement psSubs  = con.prepareStatement(qSubs)) {     // el de dentro, preparado fuera

            psJefes.setString(1, apellido);
            try (ResultSet rs = psJefes.executeQuery()) {
                boolean hay = false;
                while (rs.next()) {                                          // 1)
                    hay = true;
                    System.out.println(rs.getString("firstName") + " " + rs.getString("lastName") + ":");
                    psSubs.setInt(1, rs.getInt("employeeNumber"));           // 2) solo cambio el ?
                    try (ResultSet rs2 = psSubs.executeQuery()) {
                        boolean alguno = false;
                        while (rs2.next()) {
                            alguno = true;
                            System.out.println("   - " + rs2.getString("firstName") + " " + rs2.getString("lastName"));
                        }
                        if (!alguno) System.out.println("   No le reporta nadie");   // 3)
                    }
                }
                if (!hay) System.out.println("No existe ningún empleado con el apellido " + apellido);
            }
        } catch (SQLException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }
}
