package bbdd;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/*
 * POKEMONDB: CONSULTAS DE MÁS FÁCIL A MÁS DIFÍCIL (todas probadas con tu pokemondb.sql)
 *
 * TABLAS QUE MÁS SALEN
 *   pokemon(numero_pokedex, nombre, peso, altura)
 *   tipo(id_tipo, nombre, id_tipo_ataque)              tipo_ataque(id_tipo_ataque, tipo)  Físico/Especial
 *   pokemon_tipo(numero_pokedex, id_tipo)               <- intermedia pokémon <-> tipo (1 o 2 tipos)
 *   estadisticas_base(numero_pokedex, ps, ataque, defensa, especial, velocidad)
 *   evoluciona_de(pokemon_evolucionado, pokemon_origen) (2,1) = Ivysaur viene de Bulbasaur
 *   pokemon_forma_evolucion(numero_pokedex, id_forma_evolucion)  <- cómo evoluciona ESTE pokémon
 *   forma_evolucion(id_forma_evolucion, tipo_evolucion) tipo_evolucion: 1 Nivel, 2 Piedra, 3 Intercambio
 *   nivel_evolucion(id_forma_evolucion, nivel)          piedra(id_forma_evolucion, id_tipo_piedra)
 *   tipo_piedra(id_tipo_piedra, nombre_piedra)
 *   movimiento(id_movimiento, nombre, potencia, precision_mov, descripcion, pp, id_tipo, prioridad)
 *   pokemon_movimiento_forma(numero_pokedex, id_movimiento, id_forma_aprendizaje)
 *   forma_aprendizaje(id_forma_aprendizaje, id_tipo_aprendizaje)  tipo_forma_aprendizaje: 1 MT, 2 MO, 3 Nivel
 *   nivel_aprendizaje(id_forma_aprendizaje, nivel)      MT(id_forma_aprendizaje, MT)   MO(id_forma_aprendizaje, MO)
 *   movimiento_efecto_secundario(id_movimiento, id_efecto_secundario, probabilidad)
 *   efecto_secundario(id_efecto_secundario, efecto_secundario)
 *
 * NIVELES
 *   1  SELECT de una tabla (WHERE, ORDER BY, LIKE, COUNT)
 *   2  JOIN de 2-3 tablas
 *   3  GROUP BY / agregados / HAVING / LIMIT
 *   4  relaciones largas (evoluciones, movimientos, efectos)
 *   5  subconsultas, consultas anidadas desde Java y modificar (INSERT / UPDATE / DELETE + transacción)
 */
public class ConsultasPokemon {

    public static void main(String[] args) {
        System.out.println("===== NIVEL 1 =====");
        todos();                                  // 1
        buscarPorNombre("Pikachu");               // 2
        buscarPorNombre("Agumon");
        masPesadosQue(300);                       // 3
        contarPokemon();                          // 4
        empiezanPor("Ch");                        // 5
        System.out.println("\n===== NIVEL 2 =====");
        tiposDe("Charizard");                     // 6
        deUnTipo("Fantasma");                     // 7
        estadisticas("Mewtwo");                   // 8
        movimientosDeTipo("Fuego", 90);           // 9
        System.out.println("\n===== NIVEL 3 =====");
        cuantosPorTipo();                         // 10
        pesoMedioPorTipo();                       // 11
        masRapidos(5);                            // 12
        tiposConMasDe(20);                        // 13
        System.out.println("\n===== NIVEL 4 =====");
        comoEvoluciona("Pikachu");                // 14
        comoEvoluciona("Charmander");
        comoEvoluciona("Mew");
        movimientosPorNivel("Bulbasaur");         // 15
        movimientosConEfecto();                   // 16
        System.out.println("\n===== NIVEL 5 =====");
        conDosTipos("Agua", "Volador");           // 17
        sinEvolucionNiOrigen();                   // 18
        masPesadoQueLaMedia();                    // 19
        conTiposAnidado("Psíquico");              // 20
        crearTipo("Acero", "Físico");             // 21 INSERT
        cambiarPeso("Pikachu", 7.0);              // 22 UPDATE
        cambiarPeso("Pikachu", 6.0);              //    (lo dejo como estaba)
        borrarTipoSinPokemon("Acero");            // 23 DELETE + transacción
    }

    // ================================================================ NIVEL 1: una tabla
    // 1. Todos los pokémon (Statement normal: la consulta no lleva datos de fuera)
    public static void todos() {
        String sql = "SELECT numero_pokedex, nombre FROM pokemon ORDER BY numero_pokedex";
        try (Connection con = PoolConexiones_pokemon.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            int n = 0;
            while (rs.next()) {
                n++;
                if (n <= 3) System.out.println(rs.getInt("numero_pokedex") + " " + rs.getString("nombre"));
            }
            System.out.println("... (" + n + " en total)");
        } catch (SQLException e) { System.out.println("Error: " + e.getMessage()); }
    }

    // 2. Uno por nombre: PreparedStatement con ? (if, no while: como mucho hay uno)
    public static void buscarPorNombre(String nombre) {
        String sql = "SELECT numero_pokedex, nombre, peso, altura FROM pokemon WHERE nombre = ?";
        try (Connection con = PoolConexiones_pokemon.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, nombre);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next())
                    System.out.println("#" + rs.getInt(1) + " " + rs.getString(2) + " peso " + rs.getDouble(3) + " altura " + rs.getDouble(4));
                else
                    System.out.println(nombre + " no está en la pokedex");
            }
        } catch (SQLException e) { System.out.println("Error: " + e.getMessage()); }
    }

    // 3. WHERE con número + ORDER BY DESC
    public static void masPesadosQue(double kg) {
        String sql = "SELECT nombre, peso FROM pokemon WHERE peso > ? ORDER BY peso DESC";
        try (Connection con = PoolConexiones_pokemon.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setDouble(1, kg);
            try (ResultSet rs = ps.executeQuery()) {
                System.out.println("Pesan más de " + kg + " kg:");
                while (rs.next()) System.out.println("   " + rs.getString("nombre") + " " + rs.getDouble("peso"));
            }
        } catch (SQLException e) { System.out.println("Error: " + e.getMessage()); }
    }

    // 4. COUNT: siempre devuelve UNA fila -> rs.next() y getInt(1)
    public static void contarPokemon() {
        try (Connection con = PoolConexiones_pokemon.getConnection();
             PreparedStatement ps = con.prepareStatement("SELECT COUNT(*) FROM pokemon");
             ResultSet rs = ps.executeQuery()) {
            rs.next();
            System.out.println("Hay " + rs.getInt(1) + " pokémon");
        } catch (SQLException e) { System.out.println("Error: " + e.getMessage()); }
    }

    // 5. LIKE: el % va en el VALOR del ?, no en la consulta
    public static void empiezanPor(String inicio) {
        String sql = "SELECT nombre FROM pokemon WHERE nombre LIKE ? ORDER BY nombre";
        try (Connection con = PoolConexiones_pokemon.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, inicio + "%");                     // "Ch%" = empieza por Ch
            List<String> nombres = new ArrayList<>();
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) nombres.add(rs.getString(1));
            }
            System.out.println("Empiezan por " + inicio + ": " + String.join(", ", nombres));
        } catch (SQLException e) { System.out.println("Error: " + e.getMessage()); }
    }

    // ================================================================ NIVEL 2: JOIN
    // 6. Tipos de un pokémon: pokemon -> pokemon_tipo -> tipo
    public static void tiposDe(String nombre) {
        String sql = "SELECT t.nombre FROM pokemon p "
                   + "JOIN pokemon_tipo pt ON pt.numero_pokedex = p.numero_pokedex "
                   + "JOIN tipo t ON t.id_tipo = pt.id_tipo WHERE p.nombre = ?";
        try (Connection con = PoolConexiones_pokemon.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, nombre);
            List<String> tipos = new ArrayList<>();
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) tipos.add(rs.getString(1));
            }
            System.out.println(tipos.isEmpty() ? nombre + " no existe" : nombre + ": " + String.join(", ", tipos));
        } catch (SQLException e) { System.out.println("Error: " + e.getMessage()); }
    }

    // 7. Pokémon de un tipo (lo mismo al revés)
    public static void deUnTipo(String tipo) {
        String sql = "SELECT p.numero_pokedex, p.nombre FROM pokemon p "
                   + "JOIN pokemon_tipo pt ON pt.numero_pokedex = p.numero_pokedex "
                   + "JOIN tipo t ON t.id_tipo = pt.id_tipo WHERE t.nombre = ? ORDER BY p.numero_pokedex";
        try (Connection con = PoolConexiones_pokemon.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, tipo);
            List<String> l = new ArrayList<>();
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) l.add(rs.getString("nombre") + " (#" + rs.getInt("numero_pokedex") + ")");
            }
            System.out.println("Tipo " + tipo + ": " + (l.isEmpty() ? "ninguno" : String.join(", ", l)));
        } catch (SQLException e) { System.out.println("Error: " + e.getMessage()); }
    }

    // 8. Estadísticas base de un pokémon (relación 1 a 1 por numero_pokedex)
    public static void estadisticas(String nombre) {
        String sql = "SELECT e.ps, e.ataque, e.defensa, e.especial, e.velocidad FROM pokemon p "
                   + "JOIN estadisticas_base e ON e.numero_pokedex = p.numero_pokedex WHERE p.nombre = ?";
        try (Connection con = PoolConexiones_pokemon.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, nombre);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next())
                    System.out.printf("%s -> PS %d, ataque %d, defensa %d, especial %d, velocidad %d%n", nombre,
                            rs.getInt("ps"), rs.getInt("ataque"), rs.getInt("defensa"), rs.getInt("especial"), rs.getInt("velocidad"));
                else System.out.println("No hay estadísticas de " + nombre);
            }
        } catch (SQLException e) { System.out.println("Error: " + e.getMessage()); }
    }

    // 9. Movimientos de un tipo con potencia mínima (movimiento -> tipo, dos condiciones)
    public static void movimientosDeTipo(String tipo, int potenciaMin) {
        String sql = "SELECT m.nombre, m.potencia, m.precision_mov FROM movimiento m "
                   + "JOIN tipo t ON t.id_tipo = m.id_tipo WHERE t.nombre = ? AND m.potencia >= ? "
                   + "ORDER BY m.potencia DESC";
        try (Connection con = PoolConexiones_pokemon.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, tipo);
            ps.setInt(2, potenciaMin);                          // 2º ?
            try (ResultSet rs = ps.executeQuery()) {
                System.out.println("Movimientos de " + tipo + " con potencia >= " + potenciaMin + ":");
                while (rs.next())
                    System.out.println("   " + rs.getString(1) + " (potencia " + rs.getInt(2) + ", precisión " + rs.getInt(3) + ")");
            }
        } catch (SQLException e) { System.out.println("Error: " + e.getMessage()); }
    }

    // ================================================================ NIVEL 3: agrupar
    // 10. Cuántos pokémon hay de cada tipo (LEFT JOIN para que salgan tipos con 0)
    public static void cuantosPorTipo() {
        String sql = "SELECT t.nombre, COUNT(pt.numero_pokedex) AS cuantos FROM tipo t "
                   + "LEFT JOIN pokemon_tipo pt ON pt.id_tipo = t.id_tipo "
                   + "GROUP BY t.id_tipo, t.nombre ORDER BY cuantos DESC, t.nombre";
        try (Connection con = PoolConexiones_pokemon.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            System.out.println("Pokémon por tipo:");
            while (rs.next()) System.out.println("   " + rs.getString("nombre") + ": " + rs.getInt("cuantos"));
        } catch (SQLException e) { System.out.println("Error: " + e.getMessage()); }
    }

    // 11. Peso medio por tipo (AVG + ROUND)
    public static void pesoMedioPorTipo() {
        String sql = "SELECT t.nombre, ROUND(AVG(p.peso), 1) AS media FROM tipo t "
                   + "JOIN pokemon_tipo pt ON pt.id_tipo = t.id_tipo "
                   + "JOIN pokemon p ON p.numero_pokedex = pt.numero_pokedex "
                   + "GROUP BY t.id_tipo, t.nombre ORDER BY media DESC";
        try (Connection con = PoolConexiones_pokemon.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            System.out.println("Peso medio por tipo:");
            while (rs.next()) System.out.println("   " + rs.getString(1) + ": " + rs.getDouble(2) + " kg");
        } catch (SQLException e) { System.out.println("Error: " + e.getMessage()); }
    }

    // 12. Los N más rápidos (ORDER BY + LIMIT ?)
    public static void masRapidos(int n) {
        String sql = "SELECT p.nombre, e.velocidad FROM pokemon p "
                   + "JOIN estadisticas_base e ON e.numero_pokedex = p.numero_pokedex "
                   + "ORDER BY e.velocidad DESC, p.nombre LIMIT ?";
        try (Connection con = PoolConexiones_pokemon.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, n);
            try (ResultSet rs = ps.executeQuery()) {
                System.out.println("Los " + n + " más rápidos:");
                int pos = 1;
                while (rs.next()) System.out.println("   " + (pos++) + ". " + rs.getString(1) + " (" + rs.getInt(2) + ")");
            }
        } catch (SQLException e) { System.out.println("Error: " + e.getMessage()); }
    }

    // 13. HAVING: condición sobre el grupo (WHERE filtra filas; HAVING filtra grupos)
    public static void tiposConMasDe(int minimo) {
        String sql = "SELECT t.nombre, COUNT(*) AS cuantos FROM tipo t "
                   + "JOIN pokemon_tipo pt ON pt.id_tipo = t.id_tipo "
                   + "GROUP BY t.id_tipo, t.nombre HAVING COUNT(*) > ? ORDER BY cuantos DESC";
        try (Connection con = PoolConexiones_pokemon.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, minimo);
            try (ResultSet rs = ps.executeQuery()) {
                System.out.println("Tipos con más de " + minimo + " pokémon:");
                while (rs.next()) System.out.println("   " + rs.getString(1) + " (" + rs.getInt(2) + ")");
            }
        } catch (SQLException e) { System.out.println("Error: " + e.getMessage()); }
    }

    // ================================================================ NIVEL 4: relaciones largas
    // 14. CÓMO evoluciona (nivel, piedra o intercambio): varios LEFT JOIN porque cada forma
    //     tiene sus datos en una tabla distinta (nivel_evolucion o piedra)
    public static void comoEvoluciona(String nombre) {
        String sql = "SELECT te.tipo_evolucion, ne.nivel, tp.nombre_piedra FROM pokemon p "
                   + "JOIN pokemon_forma_evolucion pfe ON pfe.numero_pokedex = p.numero_pokedex "
                   + "JOIN forma_evolucion fe ON fe.id_forma_evolucion = pfe.id_forma_evolucion "
                   + "JOIN tipo_evolucion te ON te.id_tipo_evolucion = fe.tipo_evolucion "
                   + "LEFT JOIN nivel_evolucion ne ON ne.id_forma_evolucion = fe.id_forma_evolucion "
                   + "LEFT JOIN piedra pi ON pi.id_forma_evolucion = fe.id_forma_evolucion "
                   + "LEFT JOIN tipo_piedra tp ON tp.id_tipo_piedra = pi.id_tipo_piedra "
                   + "WHERE p.nombre = ?";
        try (Connection con = PoolConexiones_pokemon.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, nombre);
            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) { System.out.println(nombre + " no evoluciona"); return; }
                String forma = rs.getString("tipo_evolucion");
                int nivel = rs.getInt("nivel");
                boolean sinNivel = rs.wasNull();                       // NULL si no es por nivel
                String piedra = rs.getString("nombre_piedra");         // null si no es por piedra
                if (!sinNivel) System.out.println(nombre + " evoluciona por nivel (" + nivel + ")");
                else if (piedra != null) System.out.println(nombre + " evoluciona con " + piedra);
                else System.out.println(nombre + " evoluciona por " + forma);
            }
        } catch (SQLException e) { System.out.println("Error: " + e.getMessage()); }
    }

    // 15. Movimientos que aprende por NIVEL (4 tablas), ordenados por nivel
    public static void movimientosPorNivel(String nombre) {
        String sql = "SELECT na.nivel, m.nombre FROM pokemon p "
                   + "JOIN pokemon_movimiento_forma pmf ON pmf.numero_pokedex = p.numero_pokedex "
                   + "JOIN nivel_aprendizaje na ON na.id_forma_aprendizaje = pmf.id_forma_aprendizaje "
                   + "JOIN movimiento m ON m.id_movimiento = pmf.id_movimiento "
                   + "WHERE p.nombre = ? ORDER BY na.nivel, m.nombre";
        try (Connection con = PoolConexiones_pokemon.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, nombre);
            try (ResultSet rs = ps.executeQuery()) {
                System.out.println(nombre + " aprende por nivel:");
                while (rs.next()) System.out.println("   nivel " + rs.getInt(1) + ": " + rs.getString(2));
            }
        } catch (SQLException e) { System.out.println("Error: " + e.getMessage()); }
    }

    // 16. Movimientos con efecto secundario y su probabilidad
    public static void movimientosConEfecto() {
        String sql = "SELECT m.nombre, es.efecto_secundario, mes.probabilidad FROM movimiento m "
                   + "JOIN movimiento_efecto_secundario mes ON mes.id_movimiento = m.id_movimiento "
                   + "JOIN efecto_secundario es ON es.id_efecto_secundario = mes.id_efecto_secundario "
                   + "ORDER BY mes.probabilidad DESC, m.nombre LIMIT 5";
        try (Connection con = PoolConexiones_pokemon.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            System.out.println("Movimientos con más probabilidad de efecto:");
            while (rs.next()) System.out.println("   " + rs.getString(1) + " -> " + rs.getString(2) + " (" + rs.getDouble(3) + ")");
        } catch (SQLException e) { System.out.println("Error: " + e.getMessage()); }
    }

    // ================================================================ NIVEL 5
    // 17. Pokémon que tienen LOS DOS tipos: los de tipo A que además están en la lista de tipo B (subconsulta IN)
    public static void conDosTipos(String tipoA, String tipoB) {
        String sql = "SELECT p.nombre FROM pokemon p "
                   + "JOIN pokemon_tipo pt ON pt.numero_pokedex = p.numero_pokedex "
                   + "JOIN tipo t ON t.id_tipo = pt.id_tipo WHERE t.nombre = ? "
                   + "AND p.numero_pokedex IN (SELECT pt2.numero_pokedex FROM pokemon_tipo pt2 "
                   + "    JOIN tipo t2 ON t2.id_tipo = pt2.id_tipo WHERE t2.nombre = ?) "
                   + "ORDER BY p.numero_pokedex";
        try (Connection con = PoolConexiones_pokemon.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, tipoA);
            ps.setString(2, tipoB);
            List<String> l = new ArrayList<>();
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) l.add(rs.getString(1));
            }
            System.out.println(tipoA + " y " + tipoB + ": " + String.join(", ", l));
        } catch (SQLException e) { System.out.println("Error: " + e.getMessage()); }
    }

    // 18. Ni evolucionan ni vienen de otro: NOT IN (o LEFT JOIN ... IS NULL)
    public static void sinEvolucionNiOrigen() {
        String sql = "SELECT nombre FROM pokemon WHERE numero_pokedex NOT IN (SELECT pokemon_origen FROM evoluciona_de) "
                   + "AND numero_pokedex NOT IN (SELECT pokemon_evolucionado FROM evoluciona_de) ORDER BY numero_pokedex";
        try (Connection con = PoolConexiones_pokemon.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            List<String> l = new ArrayList<>();
            while (rs.next()) l.add(rs.getString(1));
            System.out.println("Sin evoluciones (" + l.size() + "): " + String.join(", ", l));
        } catch (SQLException e) { System.out.println("Error: " + e.getMessage()); }
    }

    // 19. Subconsulta con agregado: más pesados que la media
    public static void masPesadoQueLaMedia() {
        String sql = "SELECT COUNT(*), (SELECT ROUND(AVG(peso), 1) FROM pokemon) FROM pokemon "
                   + "WHERE peso > (SELECT AVG(peso) FROM pokemon)";
        try (Connection con = PoolConexiones_pokemon.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            rs.next();
            System.out.println(rs.getInt(1) + " pokémon pesan más que la media (" + rs.getDouble(2) + " kg)");
        } catch (SQLException e) { System.out.println("Error: " + e.getMessage()); }
    }

    // 20. ANIDADA desde Java: por cada pokémon del tipo, otra consulta para sus tipos (como el ejercicio 6)
    public static void conTiposAnidado(String tipo) {
        String qPokes = "SELECT p.numero_pokedex, p.nombre FROM pokemon p "
                      + "JOIN pokemon_tipo pt ON pt.numero_pokedex = p.numero_pokedex "
                      + "JOIN tipo t ON t.id_tipo = pt.id_tipo WHERE t.nombre = ? ORDER BY p.numero_pokedex";
        String qTipos = "SELECT t.nombre FROM tipo t JOIN pokemon_tipo pt ON pt.id_tipo = t.id_tipo WHERE pt.numero_pokedex = ?";
        try (Connection con = PoolConexiones_pokemon.getConnection();
             PreparedStatement psP = con.prepareStatement(qPokes);
             PreparedStatement psT = con.prepareStatement(qTipos)) {   // el de dentro, preparado FUERA del bucle
            psP.setString(1, tipo);
            try (ResultSet rs = psP.executeQuery()) {
                System.out.println("Pokémon " + tipo + " con todos sus tipos:");
                while (rs.next()) {
                    psT.setInt(1, rs.getInt("numero_pokedex"));         // solo cambio el ?
                    List<String> tipos = new ArrayList<>();
                    try (ResultSet rt = psT.executeQuery()) {
                        while (rt.next()) tipos.add(rt.getString(1));
                    }
                    System.out.println("   " + rs.getString("nombre") + ": " + String.join(", ", tipos));
                }
            }
        } catch (SQLException e) { System.out.println("Error: " + e.getMessage()); }
    }

    // 21. INSERT con comprobaciones (¿ya existe? ¿existe el tipo de ataque?)
    public static void crearTipo(String nombre, String tipoAtaque) {
        try (Connection con = PoolConexiones_pokemon.getConnection();
             PreparedStatement psExiste = con.prepareStatement("SELECT 1 FROM tipo WHERE nombre = ?");
             PreparedStatement psAtaque = con.prepareStatement("SELECT id_tipo_ataque FROM tipo_ataque WHERE tipo = ?");
             PreparedStatement psIns = con.prepareStatement("INSERT INTO tipo (nombre, id_tipo_ataque) VALUES (?, ?)")) {
            psExiste.setString(1, nombre);
            try (ResultSet rs = psExiste.executeQuery()) {
                if (rs.next()) { System.out.println("El tipo " + nombre + " ya existe"); return; }
            }
            int idAtaque;
            psAtaque.setString(1, tipoAtaque);
            try (ResultSet rs = psAtaque.executeQuery()) {
                if (!rs.next()) { System.out.println(tipoAtaque + " no es un tipo de ataque"); return; }
                idAtaque = rs.getInt(1);
            }
            psIns.setString(1, nombre);
            psIns.setInt(2, idAtaque);
            psIns.executeUpdate();                                      // id_tipo es AUTO_INCREMENT: no lo pongo
            System.out.println("Tipo " + nombre + " creado");
        } catch (SQLException e) { System.out.println("Error: " + e.getMessage()); }
    }

    // 22. UPDATE: executeUpdate devuelve cuántas filas cambió (0 = no existía)
    public static void cambiarPeso(String nombre, double peso) {
        try (Connection con = PoolConexiones_pokemon.getConnection();
             PreparedStatement ps = con.prepareStatement("UPDATE pokemon SET peso = ? WHERE nombre = ?")) {
            ps.setDouble(1, peso);
            ps.setString(2, nombre);
            int filas = ps.executeUpdate();
            System.out.println(filas == 0 ? nombre + " no existe" : "Peso de " + nombre + " -> " + peso);
        } catch (SQLException e) { System.out.println("Error: " + e.getMessage()); }
    }

    // 23. DELETE dentro de una TRANSACCIÓN: solo borro el tipo si ningún pokémon lo usa
    public static void borrarTipoSinPokemon(String nombre) {
        try (Connection con = PoolConexiones_pokemon.getConnection()) {
            con.setAutoCommit(false);                                   // empiezo la transacción
            try (PreparedStatement psUso = con.prepareStatement(
                         "SELECT COUNT(*) FROM pokemon_tipo pt JOIN tipo t ON t.id_tipo = pt.id_tipo WHERE t.nombre = ?");
                 PreparedStatement psDel = con.prepareStatement("DELETE FROM tipo WHERE nombre = ?")) {
                psUso.setString(1, nombre);
                int usos;
                try (ResultSet rs = psUso.executeQuery()) { rs.next(); usos = rs.getInt(1); }
                if (usos > 0) {
                    System.out.println("No borro " + nombre + ": lo tienen " + usos + " pokémon");
                    con.rollback();
                    return;
                }
                psDel.setString(1, nombre);
                int filas = psDel.executeUpdate();
                con.commit();                                           // confirmo
                System.out.println(filas == 0 ? "El tipo " + nombre + " no existe" : "Tipo " + nombre + " borrado");
            } catch (SQLException e) {
                con.rollback();                                         // si algo falla, deshago
                System.out.println("Error, se deshace: " + e.getMessage());
            } finally {
                con.setAutoCommit(true);                                // la conexión vuelve al pool como estaba
            }
        } catch (SQLException e) { System.out.println("Error: " + e.getMessage()); }
    }
}
