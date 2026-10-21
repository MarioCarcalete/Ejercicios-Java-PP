
package Ejercicio1Boletin;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class Ejercicio1 {

    public static void main(String[] args) {
        BuscarNombre("Patterson");
    }

    public static void BuscarNombre(String apellido) {

        String url = "jdbc:mysql://localhost:3306/classicmodels";
        String usuario = "profe";
        String pwd = "abc123";

        String consulta = "SELECT employeeNumber, firstName, lastName "
                        + "FROM employees WHERE lastName = ?";

        String consulta2 = "SELECT firstName, lastName "
                         + "FROM employees WHERE reportsTo = ?";

        try (Connection con = DriverManager.getConnection(url, usuario, pwd);
             PreparedStatement sql = con.prepareStatement(consulta);
             PreparedStatement sql2 = con.prepareStatement(consulta2)) {

            System.out.println("Conexión realizada");

            sql.setString(1, apellido);

            try (ResultSet resultado = sql.executeQuery()) {

                boolean encontrado = false;

                while (resultado.next()) {
                    encontrado = true;

                    int numero = resultado.getInt("employeeNumber");
                    String nombre = resultado.getString("firstName");
                    String ap = resultado.getString("lastName");

                    System.out.println("\nEmpleado: " + nombre + " " + ap);

                    sql2.setInt(1, numero);

                    try (ResultSet resultado2 = sql2.executeQuery()) {

                        boolean tieneReportes = false;

                        while (resultado2.next()) {
                            tieneReportes = true;

                            String nombreReporte =
                                    resultado2.getString("firstName");
                            String apellidoReporte =
                                    resultado2.getString("lastName");

                            System.out.println("  Le reporta: "
                                    + nombreReporte + " " + apellidoReporte);
                        }

                        if (!tieneReportes) {
                            System.out.println(
                                    "  Este empleado no tiene empleados a su cargo.");
                        }
                    }
                }

                if (!encontrado) {
                    System.out.println(
                            "Error: no existe ningún empleado con el apellido "
                            + apellido);
                }
            }

        } catch (SQLException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }
}
