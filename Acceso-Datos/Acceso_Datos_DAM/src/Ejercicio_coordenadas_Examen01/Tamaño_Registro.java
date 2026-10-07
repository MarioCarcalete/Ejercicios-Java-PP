package Ejercicio_coordenadas_Examen01;

import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

public class Tamaño_Registro {
	
    final static String ruta = "coordenadas.dat";

	
	public static void main(String[] args) {
		final int TAMANYO_REGISTRO = 20;
		
		try(DataInputStream fichero = new DataInputStream(new FileInputStream(ruta))){
			File ficheroFisico = new 	File(ruta);
			
			final int NUM_REG = (int)ficheroFisico.length()/TAMANYO_REGISTRO;
			System.out.println("Satélites y coordenadas");
			 for (int i = 0; i < NUM_REG; i++) {

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

