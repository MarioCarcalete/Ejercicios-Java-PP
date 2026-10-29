package modelo;

import java.util.List;

/*
 * RAÍZ del JSON (solo la usa Gson). El JSON es:
 * {
 *   "pokedex": [ {pokemon}, {pokemon}, ... ]
 * }
 * Las llaves { } de fuera son esta clase; el array [ ] es la List.
 * El atributo se llama "pokedex" porque es la CLAVE del JSON.
 * (Si quisieras llamarlo distinto: @SerializedName("pokedex") private List<Pokemon> pokemons;)
 */
public class Pokedex {

    private List<Pokemon> pokedex;

    public Pokedex(List<Pokemon> pokedex) { this.pokedex = pokedex; }

    public List<Pokemon> getPokedex() { return pokedex; }
    public void setPokedex(List<Pokemon> pokedex) { this.pokedex = pokedex; }
}
