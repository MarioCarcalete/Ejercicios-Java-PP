package formatos;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import modelo.Pokemon;

/*
 * BASE DE DATOS (JDBC).  Tabla:
 *
 *   CREATE TABLE pokemon (
 *       nombre VARCHAR(30) PRIMARY KEY,
 *       tipos  VARCHAR(100),       -- "Agua,Volador"  (los tipos juntos en un texto)
 *       nivel  INT
 *   );
 *
 * La crea sola crearTabla() (CREATE TABLE IF NOT EXISTS) dentro de classicmodels,
 * la base que ya usas con tu Docker (puerto 3307, profe / abc123).
 *
 *   leer     -> SELECT  + while (rs.next())          -> List<Pokemon>
 *   escribir -> DELETE de todo + INSERT por cada uno  (dentro de una TRANSACCIÓN)
 */
public class BaseDatos {

    // Datos de conexión: los mismos de tu PoolConexiones_classic
    public static final String URL = "jdbc:mysql://localhost:3307/classicmodels";
    public static final String USUARIO = "profe";
    public static final String PASSWORD = "abc123";

    // Conexión directa. (Con tu pool sería: return PoolConexiones_classic.getConnection();)
    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USUARIO, PASSWORD);
    }

    // Crea la tabla si no existe (Statement normal: no lleva datos del usuario)
    public static void crearTabla() {
        String sql = "CREATE TABLE IF NOT EXISTS pokemon ("
                   + "nombre VARCHAR(30) PRIMARY KEY, tipos VARCHAR(100), nivel INT)";
        try (Connection con = getConnection();
             Statement st = con.createStatement()) {
            st.executeUpdate(sql);                              // CREATE/INSERT/UPDATE/DELETE -> executeUpdate
        } catch (SQLException e) {
            System.out.println("Error al crear la tabla: " + e.getMessage());
        }
    }

    // ================================================================ PARA MIGRAR
    // Tabla -> List<Pokemon>
    public static List<Pokemon> leer() {
        List<Pokemon> lista = new ArrayList<>();
        String sql = "SELECT nombre, tipos, nivel FROM pokemon ORDER BY nombre";
        try (Connection con = getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {                 // SELECT -> executeQuery
            while (rs.next()) {                                  // una vuelta por fila
                String nombre = rs.getString("nombre");
                List<String> tipos = Pokemon.tiposDesdeTexto(rs.getString("tipos"));   // "A,B" -> [A, B]
                int nivel = rs.getInt("nivel");
                lista.add(new Pokemon(nombre, tipos, nivel));
            }
        } catch (SQLException e) {
            System.out.println("Error al leer la BBDD: " + e.getMessage());
        }
        return lista;
    }

    // List<Pokemon> -> Tabla  (vacía la tabla y mete todos; si algo falla, rollback y no cambia nada)
    public static void escribir(List<Pokemon> lista) {
        crearTabla();
        try (Connection con = getConnection()) {
            con.setAutoCommit(false);                            // empiezo la transacción
            try (Statement st = con.createStatement();
                 PreparedStatement ps = con.prepareStatement("INSERT INTO pokemon (nombre, tipos, nivel) VALUES (?, ?, ?)")) {
                st.executeUpdate("DELETE FROM pokemon");         // vacío la tabla
                for (Pokemon p : lista) {
                    ps.setString(1, p.getNombre());              // 1er ?  (empiezan en 1)
                    ps.setString(2, p.tiposComoTexto());         // 2º ?
                    ps.setInt(3, p.getNivel());                  // 3er ?
                    ps.executeUpdate();                          // inserto esta fila
                }
                con.commit();                                    // todo bien -> confirmo
                System.out.println("BBDD guardada: tabla pokemon (" + lista.size() + " pokémon)");
            } catch (SQLException e) {
                con.rollback();                                  // algo falló -> deshago todo
                System.out.println("Error, se deshace: " + e.getMessage());
            }
        } catch (SQLException e) {
            System.out.println("Error de conexión: " + e.getMessage());
        }
    }

    // ================================================================ CRUD
    public static void mostrar() {
        System.out.println("=== tabla pokemon ===");
        for (Pokemon p : leer()) System.out.println(p);
    }

    // ¿Existe? (consulta de comprobación: if (rs.next()))
    public static boolean existe(Connection con, String nombre) throws SQLException {
        try (PreparedStatement ps = con.prepareStatement("SELECT 1 FROM pokemon WHERE nombre = ?")) {
            ps.setString(1, nombre);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();                                // true si hay alguna fila
            }
        }
    }

    public static void anyadir(Pokemon p) {
        crearTabla();
        try (Connection con = getConnection()) {
            if (existe(con, p.getNombre())) { System.out.println("Ya existe " + p.getNombre()); return; }
            try (PreparedStatement ps = con.prepareStatement("INSERT INTO pokemon VALUES (?, ?, ?)")) {
                ps.setString(1, p.getNombre());
                ps.setString(2, p.tiposComoTexto());
                ps.setInt(3, p.getNivel());
                ps.executeUpdate();
                System.out.println("Añadido " + p.getNombre());
            }
        } catch (SQLException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    public static void modificarNivel(String nombre, int nivel) {
        try (Connection con = getConnection();
             PreparedStatement ps = con.prepareStatement("UPDATE pokemon SET nivel = ? WHERE nombre = ?")) {
            ps.setInt(1, nivel);
            ps.setString(2, nombre);
            int filas = ps.executeUpdate();                      // devuelve cuántas filas cambió
            System.out.println(filas == 0 ? "No existe " + nombre : "Nivel de " + nombre + " -> " + nivel);
        } catch (SQLException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    public static void eliminar(String nombre) {
        try (Connection con = getConnection();
             PreparedStatement ps = con.prepareStatement("DELETE FROM pokemon WHERE nombre = ?")) {
            ps.setString(1, nombre);
            int filas = ps.executeUpdate();
            System.out.println(filas == 0 ? "No existe " + nombre : "Eliminado " + nombre);
        } catch (SQLException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    public static void main(String[] args) {
        escribir(FicheroTexto.leer("datos/pokemons.txt"));
        anyadir(new Pokemon("Lapras", List.of("Agua", "Hielo"), 30));
        modificarNivel("Pikachu", 50);
        eliminar("Bulbasaur");
        eliminar("Mewtwo");
        mostrar();
    }
}
