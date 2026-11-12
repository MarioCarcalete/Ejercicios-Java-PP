package Json_t_demás;

import java.util.List;

// RAÍZ del JSON: son las llaves { } de fuera de pokemons.json
public class Pokedex {

    // Se llama "pokedex" porque es la CLAVE del JSON: "pokedex": [ ... ]
    // Los [ ] del JSON en Java son una List
    private List<pokemon> pokedex;

    // getter: devuelve la LISTA entera de pokémon
    public List<pokemon> getPokedex() {
        return pokedex;
    }

    // setter: recibe una LISTA y la guarda (lo usarás para guardar el JSON)
    public void setPokedex(List<pokemon> pokedex) {
        this.pokedex = pokedex;
    }
}