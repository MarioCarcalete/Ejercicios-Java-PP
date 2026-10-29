package bbdd;

import java.sql.Connection;
import java.sql.SQLException;

import org.apache.commons.dbcp2.BasicDataSource;   // jars: commons-dbcp2, commons-pool2, commons-logging (en lib/)

/*
 * POOL DE CONEXIONES para la base classicmodels (como el del profe).
 * Un pool tiene varias conexiones ya abiertas y te presta una cuando haces getConnection().
 * Al cerrar la conexión (try-with-resources) NO se cierra de verdad: vuelve al pool.
 *
 * Uso:  try (Connection con = PoolConexiones_classic.getConnection()) { ... }
 */
public class PoolConexiones_classic {

    // Datos de tu Docker: puerto 3307 (el 3306 del contenedor sale por el 3307 de tu Mac)
    public static final String URL = "jdbc:mysql://localhost:3307/classicmodels";
    public static final String USUARIO = "profe";
    public static final String PASSWORD = "abc123";

    private static BasicDataSource dataSource;      // el pool (uno solo para todo el programa)

    // Bloque estático: se ejecuta UNA vez, antes de que nadie use la clase
    static {
        dataSource = new BasicDataSource();
        dataSource.setUrl(URL);                     // a dónde conecto
        dataSource.setUsername(USUARIO);
        dataSource.setPassword(PASSWORD);
        dataSource.setInitialSize(5);               // 5 conexiones abiertas desde el principio
        dataSource.setMaxTotal(100);                // como mucho 100 a la vez
        dataSource.setPoolPreparedStatements(true); // guarda los PreparedStatement para reutilizarlos
        dataSource.setMaxOpenPreparedStatements(50);
    }

    // Me presta una conexión del pool
    public static Connection getConnection() throws SQLException {
        return dataSource.getConnection();
    }
}
