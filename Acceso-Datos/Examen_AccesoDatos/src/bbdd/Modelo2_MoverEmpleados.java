package bbdd;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/*
 * EXAMEN MODELO 2 (classicmodels)
 * Mueve todos los empleados de la oficina de una ciudad a la de otra:
 *   Moviendo todos los empleados de Tokyo a Paris.
 *   Se van a mover 2 empleados de Tokyo a Paris
 *   La oficina de Paris tiene ahora 7 empleados
 * ¡OJO! Modifica la BBDD: después de ejecutarlo, Tokyo se queda sin empleados.
 *
 * CÓMO FUNCIONA (receta SECUENCIAL: comprobar, comprobar, ... y UPDATE al final):
 *  1) código de la oficina origen y destino (si alguna no existe -> mensaje y return)
 *  2) contar empleados del origen (si 0 -> no tiene empleados)
 *  3) UN solo UPDATE ... SET officeCode = destino WHERE officeCode = origen (mueve a todos)
 *  4) volver a contar en el destino
 */
public class Modelo2_MoverEmpleados {

    public static void main(String[] args) {
        moverEmpleados("Tokyo", "Paris");
        moverEmpleados("Tokyo", "Paris");     // la 2ª vez: Tokyo ya no tiene empleados
        moverEmpleados("Madrid", "Paris");    // no existe oficina
        // ¡CAMBIA LA BBDD! Para dejarla como estaba, en Workbench (Tokyo = oficina 5):
        //   UPDATE employees SET officeCode = '5' WHERE employeeNumber IN (1621, 1625);
        // (NO llames a moverEmpleados("Paris", "Tokyo"): se llevaría a los 7 de Paris)
    }

    public static void moverEmpleados(String origen, String destino) {
        String qOficina = "SELECT officeCode FROM offices WHERE city = ?";
        String qContar  = "SELECT COUNT(*) FROM employees WHERE officeCode = ?";
        String qMover   = "UPDATE employees SET officeCode = ? WHERE officeCode = ?";

        try (Connection con = PoolConexiones_classic.getConnection();
             PreparedStatement psOficina = con.prepareStatement(qOficina);
             PreparedStatement psContar  = con.prepareStatement(qContar);
             PreparedStatement psMover   = con.prepareStatement(qMover)) {

            System.out.println("Moviendo todos los empleados de " + origen + " a " + destino + ".");

            String codOrigen = codigoOficina(psOficina, origen);             // 1)
            if (codOrigen == null) { System.out.println("No existe oficina en " + origen); return; }
            String codDestino = codigoOficina(psOficina, destino);
            if (codDestino == null) { System.out.println("No existe oficina en " + destino); return; }

            int aMover = contarEmpleados(psContar, codOrigen);              // 2)
            if (aMover == 0) { System.out.println("La oficina de " + origen + " no tiene empleados"); return; }
            System.out.println("Se van a mover " + aMover + " empleados de " + origen + " a " + destino);

            psMover.setString(1, codDestino);                              // 3) SET officeCode = ?
            psMover.setString(2, codOrigen);                               //    WHERE officeCode = ?
            psMover.executeUpdate();                                       //    UPDATE -> executeUpdate

            System.out.println("La oficina de " + destino + " tiene ahora "  // 4)
                    + contarEmpleados(psContar, codDestino) + " empleados");
        } catch (SQLException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    // officeCode de la ciudad, o null si no hay oficina (función reutilizable)
    private static String codigoOficina(PreparedStatement ps, String ciudad) throws SQLException {
        ps.setString(1, ciudad);
        try (ResultSet rs = ps.executeQuery()) {
            return rs.next() ? rs.getString("officeCode") : null;
        }
    }

    private static int contarEmpleados(PreparedStatement ps, String codOficina) throws SQLException {
        ps.setString(1, codOficina);
        try (ResultSet rs = ps.executeQuery()) {
            rs.next();                                                     // COUNT siempre da 1 fila
            return rs.getInt(1);
        }
    }
}
