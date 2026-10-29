package bbdd;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/*
 * EXAMEN MODELO 6 (pokemondb)
 * Recibe un tipo y lista sus pokémon con peso, altura y TODOS sus tipos:
 *   Los pokemon de tipo Dragón son:
 *   149 – Dragonite.
 *   Peso: 210.0. Altura: 2.2
 *   Tipos: Dragón, Volador
 * Casos raros: "Profesor no es un tipo reconocido en esta Pokedex"
 *              "No existe ningún Pokemon de tipo Acero en esta Pokedex"
 *   (para probar Acero: en Workbench  INSERT INTO tipo VALUES (null, 'Acero', 1);)
 *
 * TABLAS: tipo(id_tipo, nombre, id_tipo_ataque)   pokemon(numero_pokedex, nombre, peso, altura)
 *         pokemon_tipo(numero_pokedex, id_tipo)   <- tabla intermedia (un pokémon puede tener 2 tipos)
 *
 * CÓMO FUNCIONA (consulta ANIDADA):
 *  1) ¿existe el tipo? -> su id_tipo
 *  2) pokémon de ese tipo (JOIN pokemon_tipo)
 *  3) DENTRO del while, por cada pokémon, otra consulta para sacar todos sus tipos
 *     (el PreparedStatement de dentro se prepara FUERA del bucle; dentro solo cambio el ?)
 */
public class Modelo6_PokemonsTipo {

    public static void main(String[] args) {
        porTipo("Dragón");
        porTipo("Profesor");
        porTipo("Acero");     // "no es un tipo reconocido" hasta hacer en Workbench: INSERT INTO tipo VALUES (null, 'Acero', 1);
    }

    public static void porTipo(String tipo) {
        String qTipo  = "SELECT id_tipo FROM tipo WHERE nombre = ?";
        String qPokes = "SELECT p.numero_pokedex, p.nombre, p.peso, p.altura FROM pokemon p "
                      + "JOIN pokemon_tipo pt ON pt.numero_pokedex = p.numero_pokedex "
                      + "WHERE pt.id_tipo = ? ORDER BY p.numero_pokedex";
        String qTipos = "SELECT t.nombre FROM tipo t JOIN pokemon_tipo pt ON pt.id_tipo = t.id_tipo "
                      + "WHERE pt.numero_pokedex = ? ORDER BY t.id_tipo";

        try (Connection con = PoolConexiones_pokemon.getConnection();
             PreparedStatement psTipo  = con.prepareStatement(qTipo);
             PreparedStatement psPokes = con.prepareStatement(qPokes);
             PreparedStatement psTipos = con.prepareStatement(qTipos)) {

            int idTipo;                                                  // 1)
            psTipo.setString(1, tipo);
            try (ResultSet rs = psTipo.executeQuery()) {
                if (!rs.next()) {
                    System.out.println(tipo + " no es un tipo reconocido en esta Pokedex");
                    return;
                }
                idTipo = rs.getInt("id_tipo");
            }

            psPokes.setInt(1, idTipo);                                   // 2)
            try (ResultSet rs = psPokes.executeQuery()) {
                boolean hay = false;
                while (rs.next()) {
                    if (!hay) { System.out.println("Los pokemon de tipo " + tipo + " son:"); hay = true; }
                    int num = rs.getInt("numero_pokedex");
                    System.out.println(num + " – " + rs.getString("nombre") + ".");
                    System.out.println("Peso: " + rs.getDouble("peso") + ". Altura: " + rs.getDouble("altura"));

                    psTipos.setInt(1, num);                              // 3) consulta anidada
                    List<String> tipos = new ArrayList<>();
                    try (ResultSet rt = psTipos.executeQuery()) {
                        while (rt.next()) tipos.add(rt.getString("nombre"));
                    }
                    System.out.println("Tipos: " + String.join(", ", tipos));
                }
                if (!hay) System.out.println("No existe ningún Pokemon de tipo " + tipo + " en esta Pokedex");
            }
        } catch (SQLException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }
}
