package Pokemon_y_Demás_Serializables;      // paquete donde está la clase

import java.io.Serializable;                 // interfaz que permite guardar el objeto en binario

// implements Serializable → ObjectOutputStream puede escribir este objeto en un .dat
public class Ejercicio_Pokemon implements Serializable {

    // ===== ATRIBUTOS: los datos de cada pokémon =====
    // private = solo se pueden tocar desde dentro de esta clase
    private String nombre;                   // nombre del pokémon
    private String tipo;                     // tipo (Fuego, Agua...)
    private int nivel;                       // nivel (número entero)

    // ===== CONSTRUCTOR: se ejecuta al hacer new Ejercicio_Pokemon(...) =====
    public Ejercicio_Pokemon(String n, String t, int ni) {
        this.nombre = n;                     // guardo el parámetro n en el atributo nombre
        this.tipo = t;                       // guardo t en tipo
        this.nivel = ni;                     // guardo ni en nivel
    }

    // ===== GETTERS: para LEER los atributos desde otra clase =====
    public String getNombre() {
        return nombre;                       // devuelvo el nombre
    }

    public String getTipo() {
        return tipo;                         // devuelvo el tipo
    }

    public int getNivel() {
        return nivel;                        // devuelvo el nivel
    }

    // ===== SETTER: para CAMBIAR el nivel desde otra clase =====
    public void setNivel(int nivel2) {       // antes se llamaba setEdad: mejor que coincida con el atributo
        this.nivel = nivel2;                 // sustituyo el nivel por el nuevo valor
    }

    // ===== toString: el texto que sale al hacer System.out.println(pokemon) =====
    @Override                                // sustituyo el toString de Java (si lo escribo mal, Eclipse avisa)
    public String toString() {
        // monto el texto: pikachu (Eléctrico) nivel 25
        String linea = nombre + " (" + tipo + ")" + " nivel " + nivel;
        return linea;                        // lo devuelvo
    }
}