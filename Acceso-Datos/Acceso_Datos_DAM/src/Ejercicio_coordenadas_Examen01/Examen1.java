package Ejercicio_coordenadas_Examen01;

import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.IOException;

public class Examen1 {
    final static String ruta = "coordenadas.dat";

    public static void main(String[] args) {
        try (DataInputStream fichero =
                     new DataInputStream(new FileInputStream(ruta))) {

            System.out.println("Satélites y coordenadas");

            final int NUM_REGISTROS = 3;

            for (int i = 0; i < NUM_REGISTROS; i++) {

                int id = fichero.readInt();
                float latitud = fichero.readFloat();
                float longitud = fichero.readFloat();

                String estado = "";

                for (int j = 0; j < 4; j++) {
                    estado += fichero.readChar();
                }

                System.out.printf(
                    "Satélite ID: %d | Posición: (%.4f, %.4f) | Estado: %s%n",
                    id, latitud, longitud, estado
                );
            }

        } catch (IOException e) {
            System.out.println("Error: " + e);
        }
    }
}

