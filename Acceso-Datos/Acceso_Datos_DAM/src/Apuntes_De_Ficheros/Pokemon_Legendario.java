package Apuntes_De_Ficheros;

/*
 * HERENCIA Y SERIALIZACIÓN
 * No hace falta poner "implements Serializable": si la clase padre lo es,
 * todas sus hijas también lo son automáticamente.
 *
 * (En los apuntes originales esta clase estaba dentro de Pokemon.java sin
 *  "public". Java permite varias clases en un archivo, pero solo UNA puede
 *  ser public y debe llamarse como el archivo. Separarla es más ordenado.)
 */
public class Pokemon_Legendario extends Pokemon {

	private static final long serialVersionUID = 1L;

	public Pokemon_Legendario(int c, String n, String t) {
		super(c, n, t); // llama al constructor del padre (obligatorio en la primera línea)
		// Sobrescribimos los PV: los legendarios tienen entre 100 y 300.
		// Podemos acceder a pv porque en Pokemon es protected.
		this.pv = (int) ((Math.random() * 201) + 100);
	}

	// Sobrescribe mostrar() del padre: añade una cabecera y luego
	// reutiliza el mostrar() original con super.mostrar()
	@Override
	public void mostrar() {
		System.out.println("------------------");
		System.out.println("*** LEGENDARIO ***");
		super.mostrar();
	}
}