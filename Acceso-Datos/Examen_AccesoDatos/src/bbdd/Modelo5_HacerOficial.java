package bbdd;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/*
 * EXAMEN MODELO 5 (base naciones)
 * ⚠ NO PROBADO: no tengo el .sql de naciones. Los nombres de tablas/columnas son SUPUESTOS.
 *   Antes de usarlo, en Workbench: SHOW TABLES; y DESCRIBE de cada tabla, y cambia los nombres.
 *   La LÓGICA (comprobar país, repartir en dos listas, comprobaciones antes del UPDATE) es la que vale.
 */
public class Modelo5_HacerOficial {

    public static void main(String[] args) {
        hacerOficial("Bemba", "Zambia");
        hacerOficial("klingon", "Zambia");
    }

    public static void hacerOficial(String idioma, String pais) {
        // mismas tablas supuestas que en el ejercicio 4
        String qPais   = "SELECT codigo FROM pais WHERE nombre = ?";
        String qExiste = "SELECT COUNT(*) FROM idioma_pais WHERE idioma = ?";        // o tabla idioma si existe
        String qEnPais = "SELECT es_oficial FROM idioma_pais WHERE codigo_pais = ? AND idioma = ?";
        String qUpdate = "UPDATE idioma_pais SET es_oficial = 'T' WHERE codigo_pais = ? AND idioma = ?";

        try (Connection conn = PoolConexiones_naciones.getConnection();
             PreparedStatement psPais = conn.prepareStatement(qPais);
             PreparedStatement psExiste = conn.prepareStatement(qExiste);
             PreparedStatement psEnPais = conn.prepareStatement(qEnPais);
             PreparedStatement psUpd = conn.prepareStatement(qUpdate)) {

            // 1. país
            String codigo;
            psPais.setString(1, pais);
            try (ResultSet rs = psPais.executeQuery()) {
                if (!rs.next()) {
                    System.out.println(pais + " no es un país real o no está dado de alta en la base de datos");
                    return;
                }
                codigo = rs.getString(1);
            }

            // 2. idioma real (se habla en algún país)
            psExiste.setString(1, idioma);
            try (ResultSet rs = psExiste.executeQuery()) {
                rs.next();
                if (rs.getInt(1) == 0) {
                    System.out.println("El " + idioma + " no es un idioma real o no está dado de alta en la base de datos");
                    return;
                }
            }

            // 3. ¿se habla en ese país? ¿ya es oficial?
            psEnPais.setString(1, codigo);
            psEnPais.setString(2, idioma);
            try (ResultSet rs = psEnPais.executeQuery()) {
                if (!rs.next()) {
                    System.out.println("El idioma " + idioma + " no se habla en " + pais + " y no puedo hacerlo oficial");
                    return;
                }
                if (rs.getString(1).equals("T")) {
                    System.out.println("El " + idioma + " ya es idioma oficial en " + pais);
                    return;
                }
            }

            // 4. UPDATE
            psUpd.setString(1, codigo);
            psUpd.setString(2, idioma);
            psUpd.executeUpdate();
            System.out.println("El " + idioma + " es ahora idioma oficial en " + pais);

        } catch (SQLException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }
}
