package Ejercicio3Paises;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;



public class Ejercicio3 {
	
	public static String PAISES = "DAM2" + File.separator + "josemaria" + File.separator + "paises.csv";
	public static void main(String[] args) {
		leerArchivo();
	}
	
	public static void leerArchivo() {
		File archP = new File(PAISES);
		try(BufferedReader lector = new BufferedReader (new FileReader(archP.getPath()) )){
			String linea;
			while((linea=lector.readLine())!=null){
			System.out.println(linea);
			}
			
		}catch (IOException e) {
			System.out.println("Error: " + e.getMessage());
		}
	}

}
