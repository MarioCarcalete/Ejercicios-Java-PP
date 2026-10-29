package bbdd;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/*
 * EXAMEN MODELO 4 (base naciones)
 * ⚠ NO PROBADO: no tengo el .sql de naciones. Los nombres de tablas/columnas son SUPUESTOS.
 *   Antes de usarlo, en Workbench: SHOW TABLES; y DESCRIBE de cada tabla, y cambia los nombres.
 *   La LÓGICA (comprobar país, repartir en dos listas, comprobaciones antes del UPDATE) es la que vale.
 */
public class Modelo4_IdiomasPais {

    public static void main(String[] args) {
        idiomas("Zimbabwe");
        idiomas("Latveria");
    }

    // ⚠ Nombres de tablas/columnas supuestos (estilo base "world"):
    //   pais(codigo, nombre)  idioma_pais(codigo_pais, idioma, es_oficial)
    //   Comprueba los reales con DESCRIBE antes de usarlo.
    public static void idiomas(String pais) {
        String qPais    = "SELECT codigo FROM pais WHERE nombre = ?";
        String qIdiomas = "SELECT idioma, es_oficial FROM idioma_pais WHERE codigo_pais = ?";

        try (Connection conn = PoolConexiones_naciones.getConnection();
             PreparedStatement psPais = conn.prepareStatement(qPais);
             PreparedStatement psIdi  = conn.prepareStatement(qIdiomas)) {

            // 1. ¿Existe el país?
            String codigo;
            psPais.setString(1, pais);
            try (ResultSet rs = psPais.executeQuery()) {
                if (!rs.next()) {
                    System.out.println(pais + " no es un país real o no está dado de alta en la base de datos");
                    return;
                }
                codigo = rs.getString("codigo");
            }

            // 2. Repartir los idiomas en dos listas
            List<String> oficiales = new ArrayList<>();
            List<String> otros = new ArrayList<>();
            psIdi.setString(1, codigo);
            try (ResultSet rs = psIdi.executeQuery()) {
                while (rs.next()) {
                    if (rs.getString("es_oficial").equals("T"))   // o getBoolean si es BOOLEAN
                        oficiales.add(rs.getString("idioma"));
                    else
                        otros.add(rs.getString("idioma"));
                }
            }

            // 3. Mostrar cada caso
            if (oficiales.isEmpty())
                System.out.println("No hay idioma oficial en " + pais);
            else
                System.out.println("Idioma oficial en " + pais + ": " + String.join(", ", oficiales));

            if (otros.isEmpty())
                System.out.println("No hay otros idiomas");
            else
                System.out.println("Otros idiomas: " + String.join(", ", otros));

        } catch (SQLException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }
}
