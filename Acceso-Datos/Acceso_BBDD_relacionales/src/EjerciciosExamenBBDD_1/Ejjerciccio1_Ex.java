package EjerciciosExamenBBDD_1;

// Clases de JDBC que vamos a usar
import java.sql.Connection;          // la conexión con la base de datos
import java.sql.PreparedStatement;   // consulta SQL con huecos (?) que rellenamos después
import java.sql.ResultSet;           // las filas que devuelve la consulta
import java.sql.SQLException;        // el error que lanza JDBC si algo falla

// Tu clase del pool, que es la que nos da las conexiones
import Conexiones_y_Demás_Pull.PoolConexiones_classic;

public class Ejjerciccio1_Ex {

    public static void main(String[] args) {
        listar("Paris");    // caso con empleados
        listar("Madrid");   // caso sin oficina
    }

    // Recibe una ciudad y muestra sus empleados
    public static void listar(String ciudad) {

        // Consulta 1: busca si hay alguna oficina en esa ciudad
        String consultaOficina = "SELECT city FROM offices WHERE city LIKE ?";

        // Consulta 2: los empleados de la oficina de esa ciudad.
        // JOIN une employees y offices por officeCode.
        // "e" y "o" son alias, nombres cortos para las tablas.
        String consultaEmpleados = "SELECT e.firstName, e.lastName, e.email "
                + "FROM employees e JOIN offices o ON e.officeCode = o.officeCode "
                + "WHERE o.city LIKE ?";

        // try-with-resources: todo lo que se abre entre los paréntesis
        // (la conexión y los dos PreparedStatement) se cierra solo al acabar
        try (Connection conn = PoolConexiones_classic.getConnection();

             // TYPE_SCROLL_INSENSITIVE: el cursor puede moverse adelante y atrás.
             // Hace falta para poder usar last() y beforeFirst().
             // CONCUR_READ_ONLY: solo leemos, no vamos a modificar filas.
             PreparedStatement sqlOf = conn.prepareStatement(consultaOficina,
                     ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
             PreparedStatement sqlEmp = conn.prepareStatement(consultaEmpleados,
                     ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY)) {

            // ---------- 1. ¿Existe la oficina? ----------
            sqlOf.setString(1, ciudad);           // pone la ciudad en el primer ?
            ResultSet rOf = sqlOf.executeQuery(); // ejecuta el SELECT y guarda las filas
            rOf.last();                           // mueve el cursor a la última fila
            int existe = rOf.getRow();            // nº de esa fila; si no hay filas, da 0

            if (existe == 0) {
                System.out.println("No existe oficina en " + ciudad);
                return;   // sale del método: no hace falta seguir
            }

            // ---------- 2. Empleados de esa oficina ----------
            sqlEmp.setString(1, ciudad);
            ResultSet rEmp = sqlEmp.executeQuery();

            rEmp.last();                  // va a la última fila...
            int total = rEmp.getRow();    // ...y su número es el total de empleados
            rEmp.beforeFirst();           // vuelve antes de la primera fila para el while

            if (total == 0) {
                // la oficina existe, pero el JOIN no ha devuelto ningún empleado
                System.out.println("La oficina de " + ciudad + " no tiene empleados");
            } else {
                // primero la cabecera con el total (una sola vez, fuera del while)
                System.out.println("Hay " + total + " empleados en la oficina de " + ciudad + ". Sus datos son:");

                // next() avanza una fila; devuelve false cuando ya no quedan
                while (rEmp.next()) {
                    // leemos cada columna de la fila actual por su nombre
                    String nombre = rEmp.getString("firstName");
                    String apellido = rEmp.getString("lastName");
                    String email = rEmp.getString("email");

                    // formato del enunciado: Apellido, Nombre (email)
                    System.out.println(apellido + ", " + nombre + " (" + email + ")");
                }
            }

        } catch (SQLException e) {
            // si falla la conexión o una consulta, muestra el error
            e.printStackTrace();
        }
    }
}