package Ficheros1_Lines_String;            // paquete donde está la clase

import java.util.HashMap;                   // implementación del mapa (sin orden fijo)
import java.util.Map;                       // interfaz Map

public class Ejercicio1 {

    public static void main(String[] args) {           // aquí empieza el programa

        // ===== PARTE 1: trocear una línea =====
        String linea = "163, Hoothoot, 21.2, 0.7, Normal, Volador";  // línea como la de un fichero

        // split(", ", 5): corta por "coma + espacio" en 5 trozos COMO MÁXIMO.
        // Así el 5º trozo se queda entero: "Normal, Volador"
        String[] partes = linea.split(", ", 5);
        // partes = ["163", "Hoothoot", "21.2", "0.7", "Normal, Volador"]

        int numero = Integer.parseInt(partes[0]);       // "163" → 163 (texto a int)
        String nombre = partes[1];                      // "Hoothoot" (ya es texto)

        double peso = Double.parseDouble(partes[2]);    // "21.2" → 21.2 (texto a double)
        double altura = Double.parseDouble(partes[3]);  // "0.7" → 0.7
        String tipo = partes[4];                        // "Normal, Volador" (los dos tipos juntos)

        // Muestro algunos datos para comprobar que se ha troceado bien
        System.out.println("Tipo : " + tipo + " Numero :" + numero + " peso " + peso);

        // ===== PARTE 2: contar con un Map =====
        // Array con las IPs que hay que contar (algunas repetidas)
        String[] ips = {"192.168.1.1", "10.0.0.5", "192.168.1.1", "172.16.15.15", "192.168.1.1"};

        // Mapa: clave = IP (String), valor = veces que aparece (Integer)
        Map<String, Integer> mapa = new HashMap<>();

        for (String ip : ips) {                         // recorro las IPs una a una
            int veces = mapa.getOrDefault(ip, 0);       // cuántas llevo (0 si es la primera vez)
            mapa.put(ip, veces + 1);                    // guardo una más (put sustituye el valor viejo)
        }

        for (String ip : mapa.keySet()) {               // recorro cada IP distinta del mapa
            System.out.println(ip + " → " + mapa.get(ip));  // la IP y cuántas veces ha salido
        }
    }   // fin del main
}       // fin de la clase