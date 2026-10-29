package migraciones;

import java.util.List;

import formatos.BaseDatos;
import formatos.FicheroBinario;
import formatos.FicheroJSON;
import formatos.FicheroTexto;
import formatos.FicheroXML;
import modelo.Pokemon;

/*
 * LAS 18 MIGRACIONES.
 *
 * El truco: TODAS son lo mismo, en 2 pasos, con una List<Pokemon> en medio:
 *
 *     ORIGEN  --leer-->  List<Pokemon>  --escribir-->  DESTINO
 *
 *   formato      leer (origen -> lista)          escribir (lista -> destino)
 *   TXT          FicheroTexto.leer(ruta)         FicheroTexto.escribir(lista, ruta)
 *   binario      FicheroBinario.leer(ruta)       FicheroBinario.escribir(lista, ruta)
 *   XML          FicheroXML.leer(ruta)           FicheroXML.escribir(lista, ruta)
 *   JSON         FicheroJSON.leer(ruta)          FicheroJSON.escribir(lista, ruta)
 *   BBDD         BaseDatos.leer()                BaseDatos.escribir(lista)
 *
 * En el examen: copia la función LEER del formato de origen y la función ESCRIBIR
 * del formato de destino (están en el paquete "formatos") y únelas como aquí.
 * Si el enunciado tiene otra clase (Contacto, Articulo...), cambia Pokemon por esa clase
 * y adapta los datos que se leen/escriben; la estructura es idéntica.
 */
public class Migraciones {

    //  1. de fichero de texto a XML
    public static void deTxtAXml(String origen, String destino) {
        List<Pokemon> lista = FicheroTexto.leer(origen);               // 1) LEER el origen a una lista
        FicheroXML.escribir(lista, destino);       // 2) ESCRIBIR la lista en el destino
    }

    //  2. de fichero de texto a JSON
    public static void deTxtAJson(String origen, String destino) {
        List<Pokemon> lista = FicheroTexto.leer(origen);               // 1) LEER el origen a una lista
        FicheroJSON.escribir(lista, destino);      // 2) ESCRIBIR la lista en el destino
    }

    //  3. de fichero de texto a BBDD
    public static void deTxtABbdd(String origen) {
        List<Pokemon> lista = FicheroTexto.leer(origen);               // 1) LEER el origen a una lista
        BaseDatos.escribir(lista);                 // 2) ESCRIBIR la lista en el destino
    }

    //  4. de fichero binario a XML
    public static void deBinAXml(String origen, String destino) {
        List<Pokemon> lista = FicheroBinario.leer(origen);             // 1) LEER el origen a una lista
        FicheroXML.escribir(lista, destino);       // 2) ESCRIBIR la lista en el destino
    }

    //  5. de fichero binario a JSON
    public static void deBinAJson(String origen, String destino) {
        List<Pokemon> lista = FicheroBinario.leer(origen);             // 1) LEER el origen a una lista
        FicheroJSON.escribir(lista, destino);      // 2) ESCRIBIR la lista en el destino
    }

    //  6. de fichero binario a BBDD
    public static void deBinABbdd(String origen) {
        List<Pokemon> lista = FicheroBinario.leer(origen);             // 1) LEER el origen a una lista
        BaseDatos.escribir(lista);                 // 2) ESCRIBIR la lista en el destino
    }

    //  7. de XML a JSON
    public static void deXmlAJson(String origen, String destino) {
        List<Pokemon> lista = FicheroXML.leer(origen);                 // 1) LEER el origen a una lista
        FicheroJSON.escribir(lista, destino);      // 2) ESCRIBIR la lista en el destino
    }

    //  8. de XML a fichero de texto
    public static void deXmlATxt(String origen, String destino) {
        List<Pokemon> lista = FicheroXML.leer(origen);                 // 1) LEER el origen a una lista
        FicheroTexto.escribir(lista, destino);     // 2) ESCRIBIR la lista en el destino
    }

    //  9. de XML a fichero binario
    public static void deXmlABin(String origen, String destino) {
        List<Pokemon> lista = FicheroXML.leer(origen);                 // 1) LEER el origen a una lista
        FicheroBinario.escribir(lista, destino);   // 2) ESCRIBIR la lista en el destino
    }

    // 10. de XML a BBDD
    public static void deXmlABbdd(String origen) {
        List<Pokemon> lista = FicheroXML.leer(origen);                 // 1) LEER el origen a una lista
        BaseDatos.escribir(lista);                 // 2) ESCRIBIR la lista en el destino
    }

    // 11. de JSON a XML
    public static void deJsonAXml(String origen, String destino) {
        List<Pokemon> lista = FicheroJSON.leer(origen);                // 1) LEER el origen a una lista
        FicheroXML.escribir(lista, destino);       // 2) ESCRIBIR la lista en el destino
    }

    // 12. de JSON a fichero de texto
    public static void deJsonATxt(String origen, String destino) {
        List<Pokemon> lista = FicheroJSON.leer(origen);                // 1) LEER el origen a una lista
        FicheroTexto.escribir(lista, destino);     // 2) ESCRIBIR la lista en el destino
    }

    // 13. de JSON a fichero binario
    public static void deJsonABin(String origen, String destino) {
        List<Pokemon> lista = FicheroJSON.leer(origen);                // 1) LEER el origen a una lista
        FicheroBinario.escribir(lista, destino);   // 2) ESCRIBIR la lista en el destino
    }

    // 14. de JSON a BBDD
    public static void deJsonABbdd(String origen) {
        List<Pokemon> lista = FicheroJSON.leer(origen);                // 1) LEER el origen a una lista
        BaseDatos.escribir(lista);                 // 2) ESCRIBIR la lista en el destino
    }

    // 15. de BBDD a XML
    public static void deBbddAXml(String destino) {
        List<Pokemon> lista = BaseDatos.leer();                        // 1) LEER el origen a una lista
        FicheroXML.escribir(lista, destino);       // 2) ESCRIBIR la lista en el destino
    }

    // 16. de BBDD a JSON
    public static void deBbddAJson(String destino) {
        List<Pokemon> lista = BaseDatos.leer();                        // 1) LEER el origen a una lista
        FicheroJSON.escribir(lista, destino);      // 2) ESCRIBIR la lista en el destino
    }

    // 17. de BBDD a fichero de texto
    public static void deBbddATxt(String destino) {
        List<Pokemon> lista = BaseDatos.leer();                        // 1) LEER el origen a una lista
        FicheroTexto.escribir(lista, destino);     // 2) ESCRIBIR la lista en el destino
    }

    // 18. de BBDD a fichero binario
    public static void deBbddABin(String destino) {
        List<Pokemon> lista = BaseDatos.leer();                        // 1) LEER el origen a una lista
        FicheroBinario.escribir(lista, destino);   // 2) ESCRIBIR la lista en el destino
    }

    // Prueba de las 18: preparo los 4 ficheros de origen y la tabla, y migro todo
    public static void main(String[] args) {
        List<Pokemon> base = FicheroTexto.leer("datos/pokemons.txt");   // los datos de partida
        FicheroTexto.escribir(base, "datos/mig_origen.txt");
        FicheroBinario.escribir(base, "datos/mig_origen.dat");
        FicheroXML.escribir(base, "datos/mig_origen.xml");
        FicheroJSON.escribir(base, "datos/mig_origen.json");
        BaseDatos.escribir(base);

        System.out.println("\n--- 1. deTxtAXml");
        deTxtAXml("datos/mig_origen.txt", "datos/mig_01.xml");
        System.out.println("\n--- 2. deTxtAJson");
        deTxtAJson("datos/mig_origen.txt", "datos/mig_02.json");
        System.out.println("\n--- 3. deTxtABbdd");
        deTxtABbdd("datos/mig_origen.txt");
        System.out.println("\n--- 4. deBinAXml");
        deBinAXml("datos/mig_origen.dat", "datos/mig_04.xml");
        System.out.println("\n--- 5. deBinAJson");
        deBinAJson("datos/mig_origen.dat", "datos/mig_05.json");
        System.out.println("\n--- 6. deBinABbdd");
        deBinABbdd("datos/mig_origen.dat");
        System.out.println("\n--- 7. deXmlAJson");
        deXmlAJson("datos/mig_origen.xml", "datos/mig_07.json");
        System.out.println("\n--- 8. deXmlATxt");
        deXmlATxt("datos/mig_origen.xml", "datos/mig_08.txt");
        System.out.println("\n--- 9. deXmlABin");
        deXmlABin("datos/mig_origen.xml", "datos/mig_09.dat");
        System.out.println("\n--- 10. deXmlABbdd");
        deXmlABbdd("datos/mig_origen.xml");
        System.out.println("\n--- 11. deJsonAXml");
        deJsonAXml("datos/mig_origen.json", "datos/mig_11.xml");
        System.out.println("\n--- 12. deJsonATxt");
        deJsonATxt("datos/mig_origen.json", "datos/mig_12.txt");
        System.out.println("\n--- 13. deJsonABin");
        deJsonABin("datos/mig_origen.json", "datos/mig_13.dat");
        System.out.println("\n--- 14. deJsonABbdd");
        deJsonABbdd("datos/mig_origen.json");
        System.out.println("\n--- 15. deBbddAXml");
        deBbddAXml("datos/mig_15.xml");
        System.out.println("\n--- 16. deBbddAJson");
        deBbddAJson("datos/mig_16.json");
        System.out.println("\n--- 17. deBbddATxt");
        deBbddATxt("datos/mig_17.txt");
        System.out.println("\n--- 18. deBbddABin");
        deBbddABin("datos/mig_18.dat");

        System.out.println("\nComprobación: el último (BBDD -> binario) leído de nuevo:");
        for (Pokemon p : FicheroBinario.leer("datos/mig_18.dat")) System.out.println(p);
    }
}
