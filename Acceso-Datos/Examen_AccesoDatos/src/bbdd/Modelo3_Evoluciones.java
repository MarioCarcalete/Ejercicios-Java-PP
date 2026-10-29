package bbdd;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/*
 * EXAMEN MODELO 3 (pokemondb)
 * Recibe un nombre de pokémon y muestra en qué evoluciona y de cuál viene:
 *   Bulbasaur (#1)
 *   - evoluciona en Ivysaur (#2)
 *   - no evoluciona de ningún otro.
 *
 * TABLAS:  pokemon(numero_pokedex, nombre, peso, altura)
 *          evoluciona_de(pokemon_evolucionado, pokemon_origen)
 *   Una fila (2, 1) = "Ivysaur (2) viene de Bulbasaur (1)"
 *
 * CÓMO FUNCIONA:
 *  1) Busco el pokémon por nombre (si no está -> mensaje y return)
 *  2) "¿en qué evoluciono?"  -> filas donde YO soy el pokemon_origen
 *  3) "¿de quién vengo?"     -> filas donde YO soy el pokemon_evolucionado
 *  Las dos consultas usan la MISMA función listar() -> menos código (eso puntúa)
 */
public class Modelo3_Evoluciones {

    public static void main(String[] args) {
        evoluciones("Bulbasaur");   // evoluciona, no viene de nadie
        evoluciones("Ivysaur");     // las dos cosas
        evoluciones("Venusaur");    // en la BBDD está escrito "Venasaur": este no existe
        evoluciones("Venasaur");    // viene de otro, no evoluciona
        evoluciones("Mew");         // ninguna
        evoluciones("Eevee");       // 3 evoluciones
        evoluciones("Sylveon");     // no está
    }

    public static void evoluciones(String nombre) {
        String qPoke  = "SELECT numero_pokedex, nombre FROM pokemon WHERE nombre = ?";
        String qHacia = "SELECT p.numero_pokedex, p.nombre FROM evoluciona_de e "
                      + "JOIN pokemon p ON p.numero_pokedex = e.pokemon_evolucionado "
                      + "WHERE e.pokemon_origen = ? ORDER BY p.numero_pokedex";
        String qDesde = "SELECT p.numero_pokedex, p.nombre FROM evoluciona_de e "
                      + "JOIN pokemon p ON p.numero_pokedex = e.pokemon_origen "
                      + "WHERE e.pokemon_evolucionado = ?";

        try (Connection con = PoolConexiones_pokemon.getConnection();
             PreparedStatement psPoke  = con.prepareStatement(qPoke);
             PreparedStatement psHacia = con.prepareStatement(qHacia);
             PreparedStatement psDesde = con.prepareStatement(qDesde)) {

            int num;
            psPoke.setString(1, nombre);
            try (ResultSet rs = psPoke.executeQuery()) {                     // 1)
                if (!rs.next()) {
                    System.out.println("El pokemon " + nombre + " no está en la pokedex o no es de la primera generación");
                    return;
                }
                num = rs.getInt("numero_pokedex");
                System.out.println(rs.getString("nombre") + " (#" + num + ")");
            }
            System.out.println(listar(psHacia, num, "- evoluciona en ", "- no evoluciona en ningún otro."));   // 2)
            System.out.println(listar(psDesde, num, "- evoluciona de ", "- no evoluciona de ningún otro."));  // 3)
            System.out.println();
        } catch (SQLException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    // Ejecuta la consulta con ese número y devuelve el texto (o el de "no..." si no hay filas)
    private static String listar(PreparedStatement ps, int num, String inicio, String siVacio) throws SQLException {
        ps.setInt(1, num);
        List<String> nombres = new ArrayList<>();
        try (ResultSet rs = ps.executeQuery()) {
            while (rs.next()) nombres.add(rs.getString("nombre") + " (#" + rs.getInt("numero_pokedex") + ")");
        }
        return nombres.isEmpty() ? siVacio : inicio + String.join(" ", nombres);
    }
}
