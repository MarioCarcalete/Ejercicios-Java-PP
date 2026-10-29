package bbdd;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/*
 * EXAMEN MODELO 1 (classicmodels)
 * Recibe una ciudad y lista sus empleados con email:
 *   Hay 5 empleados en la oficina de Paris. Sus datos son:
 *   Bondur, Gerard (gbondur@classicmodelcars.com) ...
 * Casos raros: "No existe oficina en Madrid" / "La oficina de X no tiene empleados"
 *
 * CÓMO FUNCIONA:
 *  1) Consulta de comprobación: ¿hay oficina en esa ciudad? (si no -> mensaje y return)
 *  2) Consulta de empleados con ResultSet DESPLAZABLE para contar antes de listar:
 *     last() + getRow() = total de filas; beforeFirst() para volver al principio
 *  3) Si total = 0 -> no tiene empleados; si no, cabecera + while(rs.next())
 */
public class Modelo1_EmpleadosCiudad {

    public static void main(String[] args) {
        empleadosCiudad("Paris");
        empleadosCiudad("Madrid");
    }

    public static void empleadosCiudad(String ciudad) {
        String qOficina = "SELECT officeCode FROM offices WHERE city = ?";
        String qEmpleados = "SELECT e.lastName, e.firstName, e.email FROM employees e "
                          + "JOIN offices o ON e.officeCode = o.officeCode WHERE o.city = ?";

        try (Connection con = PoolConexiones_classic.getConnection();
             PreparedStatement psOf = con.prepareStatement(qOficina);
             // TYPE_SCROLL_INSENSITIVE = puedo moverme adelante y atrás (para last/getRow)
             PreparedStatement psEm = con.prepareStatement(qEmpleados,
                     ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY)) {

            // 1) ¿existe la oficina?
            psOf.setString(1, ciudad);
            try (ResultSet rs = psOf.executeQuery()) {
                if (!rs.next()) {                                  // ninguna fila -> no existe
                    System.out.println("No existe oficina en " + ciudad);
                    return;
                }
            }

            // 2) empleados
            psEm.setString(1, ciudad);
            try (ResultSet rs = psEm.executeQuery()) {
                rs.last();                                         // me pongo en la última fila
                int total = rs.getRow();                           // su número = cuántas hay (0 si ninguna)
                rs.beforeFirst();                                  // vuelvo antes de la primera
                if (total == 0) {
                    System.out.println("La oficina de " + ciudad + " no tiene empleados");
                    return;
                }
                System.out.println("Hay " + total + " empleados en la oficina de " + ciudad + ". Sus datos son:");
                while (rs.next()) {
                    System.out.println(rs.getString("lastName") + ", " + rs.getString("firstName")
                            + " (" + rs.getString("email") + ")");
                }
            }
        } catch (SQLException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }
}
