package Apuntes_De_Ficheros;

import java.io.Serializable;

/*
 * ============================================================
 *  CLASE POKEMON (usada en Ficheros3)
 * ============================================================
 *  implements Serializable → permite guardar el objeto en un fichero binario
 *      (ObjectOutputStream). Es una interfaz "marcadora": no obliga a
 *      implementar ningún método, solo le dice a Java "esto se puede serializar".
 *  implements Comparable<Pokemon> → permite ordenar Pokemons
 *      (Collections.sort(lista)). Obliga a implementar compareTo().
 */
public class Pokemon implements Serializable, Comparable<Pokemon> {

	// serialVersionUID: "número de versión" de la clase para la serialización.
	// Si guardas un objeto, luego cambias la clase y lo intentas leer, Java
	// compara este número. Si no lo pones, Java calcula uno automáticamente
	// y cualquier cambio en la clase haría que los ficheros viejos no se
	// puedan leer (InvalidClassException). Eclipse avisa si falta.
	private static final long serialVersionUID = 1L;

	private int codigo;
	private String nombre;
	private String[] tipo = new String[2]; // tipo[1] es null si solo tiene un tipo
	protected int pv; // protected: accesible desde las clases hijas (PokemonLegendario)
	private Pokemon evolucion = null; // un objeto dentro de otro: también se serializa

	// Qué NO se guarda al serializar:
	//  - Los atributos static (son de la clase, no del objeto)
	//  - Los atributos marcados como transient (p.ej. información sensible).
	//    Al recuperar el objeto, valdrán su valor por defecto (null, 0, false).
	// private transient String password;

	// Constructor para Pokemon de un tipo
	public Pokemon(int c, String n, String t) {
		this.codigo = c;
		this.nombre = n;
		this.tipo[0] = t;
		// Math.random() → [0, 1). *51 → [0, 51). (int) → 0..50. +50 → 50..100
		this.pv = (int) ((Math.random() * 51) + 50);
	}

	// Constructor para Pokemon de dos tipos (sobrecarga de constructores:
	// mismo nombre, distintos parámetros)
	public Pokemon(int c, String n, String t1, String t2) {
		this.codigo = c;
		this.nombre = n;
		this.tipo[0] = t1;
		this.tipo[1] = t2;
		this.pv = (int) ((Math.random() * 51) + 50);
	}

	public void mostrar() {
		System.out.println("------------------");
		System.out.println(this.codigo + " - " + this.nombre);
		if (this.tipo[1] == null)
			System.out.println("Tipo: " + this.tipo[0]);
		else
			System.out.println("Tipos: " + this.tipo[0] + ", " + this.tipo[1]);
		if (this.evolucion != null)
			System.out.println("Evoluciona en: " + this.evolucion.nombre);
		System.out.println("PV: " + this.pv);
		System.out.println("------------------");
	}

	// @Override no es obligatorio pero sí MUY recomendado: si te equivocas
	// en el nombre o los parámetros, el compilador te avisa.

	// toString() se llama automáticamente al hacer System.out.println(pokemon)
	// o "texto" + pokemon
	@Override
	public String toString() {
		String texto = "(" + this.codigo + ") " + this.nombre + "\n";
		if (this.tipo[1] == null)
			texto += "Tipo: " + this.tipo[0];
		else
			texto += "Tipos: " + this.tipo[0] + " / " + this.tipo[1];
		texto += "\n-----------";
		return texto;
	}

	// equals() decide cuándo dos objetos se consideran iguales.
	// Lo usan métodos como lista.contains() o lista.indexOf().
	@Override
	public boolean equals(Object otro) {
		// Comprobaciones de seguridad para no provocar excepciones:
		if (this == otro)
			return true; // es el mismo objeto
		if (otro == null || getClass() != otro.getClass())
			return false; // null o de otra clase → no pueden ser iguales
		Pokemon comparado = (Pokemon) otro;
		// CORREGIDO: en los apuntes originales ponía this.nombre == comparado.nombre
		// Con Strings, == compara si son el MISMO objeto en memoria, no si tienen
		// el mismo texto. Para comparar el contenido se usa equals().
		return this.nombre.equals(comparado.nombre);
	}

	// Regla de Java: si sobrescribes equals, sobrescribe también hashCode,
	// y dos objetos iguales deben tener el mismo hashCode.
	// Lo usan HashMap y HashSet para funcionar bien.
	@Override
	public int hashCode() {
		return this.nombre.hashCode();
	}

	// compareTo() devuelve:
	//   negativo si this va ANTES que otro
	//   0        si son "iguales" en orden
	//   positivo si this va DESPUÉS que otro
	// Aquí ordenamos por código de Pokédex.
	@Override
	public int compareTo(Pokemon otro) {
		int devolver = 0;
		if (this.codigo > otro.codigo)
			devolver = 1;
		else if (this.codigo < otro.codigo)
			devolver = -1;
		return devolver;
		// Forma corta equivalente: return Integer.compare(this.codigo, otro.codigo);
	}

	public void setEvolucion(Pokemon p) {
		this.evolucion = p;
	}

	// Devuelve el Pokemon evolucionado, o él mismo si no puede evolucionar
	public Pokemon evoluciona() {
		Pokemon pokemon = this;
		if (this.evolucion == null)
			System.out.println("Este pokemon no sabe evolucionar");
		else
			pokemon = this.evolucion;
		return pokemon;
	}

	// Combate por turnos: ataca this, luego contraataca el atacado, y así
	// hasta que uno se queda sin PV.
	// Devuelve true si gana el atacante (this), false si gana el atacado.
	public boolean combateContra(Pokemon atacado) {
		boolean combateTerminado;
		boolean vencedor = true;
		if (this.pv <= 0 || atacado.pv <= 0)
			System.out.println("Un pokemon sin PV no puede combatir");
		else {
			do {
				int danyo = (int) ((Math.random() * 51) + 25); // daño entre 25 y 75
				atacado.pv -= danyo;
				if (atacado.pv > 0) {
					// El atacado sigue vivo → contraataca
					danyo = (int) ((Math.random() * 51) + 25);
					this.pv -= danyo;
					if (this.pv > 0)
						combateTerminado = false;
					else {
						vencedor = false;
						System.out.println(this.nombre + " ha sido derrotado");
						combateTerminado = true;
					}
				} else {
					vencedor = true;
					System.out.println(atacado.nombre + " ha sido derrotado");
					combateTerminado = true;
				}
			} while (!combateTerminado);
		}
		return vencedor;
	}
}