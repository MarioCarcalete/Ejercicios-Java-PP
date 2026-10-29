package modelo;

import java.io.Serializable;              // para poder guardarlo en un .dat (binario)
import java.util.ArrayList;
import java.util.List;

/*
 * CLASE MOLDE: un objeto Pokemon = un pokémon.
 * Es la MISMA clase para TODOS los formatos:
 *   - TXT   -> cada línea "Pikachu;Eléctrico,Volador;25" se convierte en un Pokemon
 *   - .dat  -> se guarda el objeto entero (por eso implements Serializable)
 *   - XML   -> cada <pokemon> se convierte en un Pokemon
 *   - JSON  -> Gson rellena los atributos solo (por eso se llaman igual que las claves)
 *   - BBDD  -> cada fila de la tabla pokemon se convierte en un Pokemon
 * Gracias a esto, MIGRAR es siempre: leer a List<Pokemon> -> escribir esa lista.
 */
public class Pokemon implements Serializable {

    private static final long serialVersionUID = 1L;   // versión de la clase para el binario (evita avisos)

    private String nombre;          // "nombre": "Pikachu"
    private List<String> tipos;     // "tipos": ["Agua", "Volador"]   (1 o más tipos -> List)
    private int nivel;              // "nivel": 40

    // Constructor completo: new Pokemon("Pikachu", lista de tipos, 25)
    public Pokemon(String nombre, List<String> tipos, int nivel) {
        this.nombre = nombre;
        this.tipos = new ArrayList<>(tipos);   // copio a un ArrayList para poder hacer add/remove luego
        this.nivel = nivel;
    }

    // ===== GETTERS =====
    public String getNombre() { return nombre; }
    public List<String> getTipos() { return tipos; }
    public int getNivel() { return nivel; }

    // ===== SETTERS =====
    public void setNombre(String nombre) { this.nombre = nombre; }
    public void setTipos(List<String> tipos) { this.tipos = tipos; }
    public void setNivel(int nivel) { this.nivel = nivel; }

    // Tipos juntos en un texto: ["Agua","Volador"] -> "Agua,Volador"  (para TXT y BBDD)
    public String tiposComoTexto() {
        return String.join(",", tipos);
    }

    // Al revés: "Agua,Volador" -> ["Agua","Volador"]   (static: no necesita un pokémon creado)
    public static List<String> tiposDesdeTexto(String texto) {
        List<String> lista = new ArrayList<>();
        for (String t : texto.split(",")) {      // parto por las comas
            lista.add(t.trim());                  // trim por si hay espacios
        }
        return lista;
    }

    @Override
    public String toString() {                    // lo que sale con System.out.println(pokemon)
        return String.format("%-12s nivel %3d  tipos: %s", nombre, nivel, String.join(", ", tipos));
    }
}
