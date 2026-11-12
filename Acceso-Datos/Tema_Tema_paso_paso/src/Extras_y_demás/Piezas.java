
package Extras_y_demás;

import java.io.*;                 // ficheros: Data streams, RandomAccessFile, Readers...
import java.util.*;               // Scanner, List, ArrayList, Map, Collections...

public class Piezas {

    public static void main(String[] args) throws IOException {
        pieza1_scanner();
        pieza2_palabras();
        pieza3_invertir();
        pieza4_ordenar();
        pieza5_dataStreams();
        pieza6_randomAccess();
        pieza7_regex();
    }

    // ================================================================
    // PIEZA 1 · SCANNER: pedir datos por teclado (boletín 23.1, 24.7, 24.9)
    // ================================================================
    static Scanner teclado = new Scanner(System.in);     // UNO para todo el programa (no cerrarlo)

    static void pieza1_scanner() {
        System.out.print("Introduce el nombre del usuario: ");
        String usuario = teclado.nextLine();             // lee la línea ENTERA (con espacios)

        System.out.print("Introduce la contraseña: ");
        String pass1 = teclado.nextLine();
        System.out.print("Vuelve a introducir la contraseña de nuevo: ");
        String pass2 = teclado.nextLine();

        // Strings se comparan con equals, NUNCA con ==
        if (pass1.equals(pass2)) System.out.println("Cuenta de " + usuario + " grabada correctamente");
        else                     System.out.println("Las contraseñas no son iguales. No se puede grabar la nueva cuenta");

        System.out.print("¿Cuál es su edad? ");
        // OJO: nextInt() deja el ENTER sin leer y el siguiente nextLine() saldría vacío.
        // Truco: leer siempre con nextLine() y convertir con parseInt
        int edad = Integer.parseInt(teclado.nextLine().trim());
        System.out.println("Edad guardada: " + edad);
    }

    // ================================================================
    // PIEZA 2 · CONTAR PALABRAS aunque haya varios espacios (boletín 23.3)
    // ================================================================
    static void pieza2_palabras() {
        String linea = "  hola   mundo  esto es   Java ";
        // trim() quita los espacios del principio y del final
        // split(" +") corta por "uno o MÁS espacios" (el + = repetido 1 o más veces)
        String[] palabras = linea.trim().split(" +");
        System.out.println("Palabras: " + palabras.length);          // 5
        // Sin trim, el primer trozo sería "" (vacío) y contaría una de más.
        // Si la línea está vacía, trim() da "" y split da 1 trozo vacío → compruébalo:
        String vacia = "   ";
        int n = vacia.trim().isEmpty() ? 0 : vacia.trim().split(" +").length;
        System.out.println("Palabras en línea vacía: " + n);         // 0
    }

    // ================================================================
    // PIEZA 3 · INVERTIR: texto al revés y orden de líneas al revés (boletín 24.4)
    // ================================================================
    static void pieza3_invertir() {
        // Texto al revés: StringBuilder tiene reverse(); String NO
        String s = "Hola";
        String alReves = new StringBuilder(s).reverse().toString();  // "aloH"
        System.out.println(alReves);

        // Orden de las líneas al revés: las meto en una lista y le doy la vuelta
        List<String> lineas = new ArrayList<>(List.of("primera", "segunda", "tercera"));
        Collections.reverse(lineas);                                 // [tercera, segunda, primera]
        for (String l : lineas)                                      // y cada línea al revés también
            System.out.println(new StringBuilder(l).reverse());
    }

    // ================================================================
    // PIEZA 4 · ORDENAR (ficheros ej.5 elecciones: ordenar por votos)
    // ================================================================
    static void pieza4_ordenar() {
        // a) Lista de String o números: orden natural
        List<String> nombres = new ArrayList<>(List.of("Pepe", "Ana", "Luis"));
        Collections.sort(nombres);                        // [Ana, Luis, Pepe]
        System.out.println(nombres);

        // b) Ordenar las claves de un Map por su VALOR, de mayor a menor
        Map<String, Integer> votos = new HashMap<>();
        votos.put("PA", 134); votos.put("PB", 98); votos.put("PC", 456); votos.put("PD", 177);
        List<String> partidos = new ArrayList<>(votos.keySet());     // las claves en una lista
        // sort recibe CÓMO comparar dos elementos (a, b):
        //   resultado negativo → a va antes;  positivo → b va antes
        //   votos(b) - votos(a)  → de MAYOR a menor   (a - b sería de menor a mayor)
        partidos.sort((a, b) -> votos.get(b) - votos.get(a));
        for (String p : partidos)
            System.out.println("Partido " + p + ": " + votos.get(p) + " votos");

        // c) Lista de OBJETOS por un atributo (ej. nivel de un pokémon):
        //    lista.sort((a, b) -> b.getNivel() - a.getNivel());        // int, de mayor a menor
        //    lista.sort((a, b) -> a.getNombre().compareTo(b.getNombre())); // String, alfabético

        // d) Porcentaje con decimales: OJO con la división entera
        int total = 134 + 98 + 456 + 177, censo = 1730;
        System.out.println("Escrutinio: " + (total * 100 / censo) + "%");          // 50 (entero)
        System.out.printf("Escrutinio: %.2f%%%n", total * 100.0 / censo);          // 50,00% (el 100.0 lo hace decimal)
    }

    // ================================================================
    // PIEZA 5 · DataOutputStream / DataInputStream: binario de DATOS SUELTOS
    // (coordenadas.dat). Diferencia con ObjectStream: aquí NO hay objetos,
    // se escribe int, float, char... uno a uno, y se lee en el MISMO orden.
    // ================================================================
    static void pieza5_dataStreams() throws IOException {
        // Escribir 2 registros: int + float + float + 4 char = 4+4+4+8 = 20 bytes cada uno
        try (DataOutputStream out = new DataOutputStream(new FileOutputStream("prueba.dat"))) {
            out.writeInt(101); out.writeFloat(40.4167f); out.writeFloat(-3.7037f); out.writeChars("ACTV");
            out.writeInt(202); out.writeFloat(-22.9068f); out.writeFloat(-43.1729f); out.writeChars("WARN");
        }
        // Leer: mismos tipos y mismo orden. No sé cuántos hay → while(true) + EOFException
        try (DataInputStream in = new DataInputStream(new FileInputStream("prueba.dat"))) {
            while (true) {
                int id = in.readInt();                        // 4 bytes
                float lat = in.readFloat();                   // 4 bytes
                float lon = in.readFloat();                   // 4 bytes
                String estado = "";
                for (int i = 0; i < 4; i++) estado += in.readChar();   // 4 char × 2 bytes
                // %d entero, %.4f decimal con 4 decimales, %s texto, %n salto de línea
                System.out.printf("Satélite ID: %d | Posición: (%.4f, %.4f) | Estado: %s%n", id, lat, lon, estado);
            }
        } catch (EOFException e) {
            // fin del fichero: es la forma normal de salir del while(true)
        }
    }

    // ================================================================
    // PIEZA 6 · RandomAccessFile: ir DIRECTO al registro que quiero
    // Si todos los registros miden lo mismo (20 bytes), el registro n
    // empieza en la posición n * 20. seek() salta ahí sin leer lo anterior.
    // ================================================================
    static void pieza6_randomAccess() throws IOException {
        final int TAM = 20;                                   // bytes por registro
        try (RandomAccessFile raf = new RandomAccessFile("prueba.dat", "rw")) {   // "r" leer, "rw" leer y escribir
            long registros = raf.length() / TAM;              // cuántos registros hay
            System.out.println("Registros: " + registros);

            raf.seek(1 * TAM);                                // salto al registro 1 (el segundo)
            System.out.println("ID del segundo: " + raf.readInt());

            raf.seek(1 * TAM + 12);                           // id(4) + lat(4) + lon(4) = 12 → el estado
            raf.writeChars("LOST");                           // lo SOBRESCRIBO en su sitio
            raf.seek(1 * TAM + 12);
            String e = ""; for (int i = 0; i < 4; i++) e += raf.readChar();
            System.out.println("Nuevo estado: " + e);         // LOST

            raf.seek(raf.length());                           // al final → para AÑADIR un registro
            raf.writeInt(303); raf.writeFloat(1f); raf.writeFloat(2f); raf.writeChars("NEW ");
            System.out.println("Registros ahora: " + raf.length() / TAM);   // 3
        }
    }

    // ================================================================
    // PIEZA 7 · EXPRESIONES REGULARES con matches (boletín 24.8)
    // matches() mira si el texto ENTERO cumple el patrón → true/false
    //   [a-z]   una letra minúscula      [A-Za-zÁÉÍÓÚáéíóúÑñ]  cualquier letra
    //   [0-9] o \\d  un dígito            .  cualquier carácter   \\.  un punto de verdad
    //   +  1 o más    *  0 o más    ?  0 o 1 (opcional)    {3}  exactamente 3
    //   ( )  agrupa   |  "o"         ^ $ no hacen falta con matches
    // En Java la \ se escribe DOBLE: "\\d", "\\."
    // ================================================================
    static void pieza7_regex() {
        System.out.println("12345678Z".matches("[0-9]{8}[A-Z]"));      // DNI → true
        System.out.println("1599.56".matches("[0-9]+(\\.[0-9]+)?"));   // número con decimales opcionales → true
        System.out.println("1095".matches("[0-9]+(\\.[0-9]+)?"));      // sin decimales → true
        System.out.println("12a".matches("[0-9]+"));                   // tiene una letra → false

        // Línea del boletín 24.8: "Apellidos, Nombre;Puesto;Salario"
        String L = "[A-Za-zÁÉÍÓÚáéíóúÑñ]+";                     // una palabra solo de letras
        String PAL = L + "( " + L + ")*";                       // una o varias palabras de letras
        String FORMATO = PAL + ", " + PAL                       // Apellidos, Nombre
                       + ";[A-Za-z0-9ÁÉÍÓÚáéíóúÑñ ]+"           // ;Puesto (letras, números, espacios)
                       + ";[0-9]+(\\.[0-9]+)?";                 // ;Salario
        System.out.println("Imedio, Demetrio;Programador Categoría 2;1599.56".matches(FORMATO)); // true
        System.out.println("Imedio Demetrio;Programador;1599.56".matches(FORMATO));              // falta la coma → false
    }
}
