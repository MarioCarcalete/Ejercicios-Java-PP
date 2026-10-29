package bbdd;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/*
 * BOLETÍN 26 · EJERCICIO 3 (classicmodels)
 * Recibe un número y lista los productos con stock <= ese número, su stock y
 * en cuántos pedidos aparece (orderdetails).
 *   500 -> 5 productos · 15 -> solo el 1960 BSA Gold Star DBD34 (28 pedidos) · 10 -> ninguno
 *
 * CÓMO FUNCIONA (una sola consulta con agregado):
 *  - LEFT JOIN: si un producto no está en ningún pedido, sale igual (con 0). Con JOIN desaparecería.
 *  - COUNT(od.orderNumber) cuenta pedidos (cuenta 0 si no hay). SUM(quantityOrdered) contaría UNIDADES: mal.
 *  - GROUP BY por producto: una fila por producto.
 */
public class Boletin26_3_PocoStock {

    public static void main(String[] args) {
        pocoStock(500);
        pocoStock(15);
        pocoStock(10);
    }

    public static void pocoStock(int maximo) {
        String sql = "SELECT p.productName, p.quantityInStock, COUNT(od.orderNumber) AS pedidos "
                   + "FROM products p LEFT JOIN orderdetails od ON p.productCode = od.productCode "
                   + "WHERE p.quantityInStock <= ? "
                   + "GROUP BY p.productCode, p.productName, p.quantityInStock "
                   + "ORDER BY p.quantityInStock";

        try (Connection con = PoolConexiones_classic.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, maximo);
            try (ResultSet rs = ps.executeQuery()) {
                boolean hay = false;
                System.out.println("Productos con stock <= " + maximo + ":");
                while (rs.next()) {
                    hay = true;
                    System.out.println("   " + rs.getString("productName") + " (stock " + rs.getInt("quantityInStock")
                            + ") aparece en " + rs.getInt("pedidos") + " pedidos");
                }
                if (!hay) System.out.println("   No hay ningún producto con stock inferior o igual a " + maximo);
            }
        } catch (SQLException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }
}
