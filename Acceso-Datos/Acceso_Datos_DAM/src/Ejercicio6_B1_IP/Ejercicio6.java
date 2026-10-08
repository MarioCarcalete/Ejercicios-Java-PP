package Ejercicio6_B1_IP;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Map;
import java.util.TreeMap;

public class Ejercicio6 {
	
	 final static String  DIR_FICH = "IP.txt";
	
	
	public static void main(String[] args) {
		 File ruta = new File(".");
		 
		 System.out.println(ruta.getAbsolutePath());

		
		leerFichero(DIR_FICH);
		
	}
	
	public static void leerFichero(String ruta) {
		File Ip = new File(DIR_FICH);
		try(BufferedReader lc = new BufferedReader(new FileReader(Ip.getPath()))){
			String linea;
			while((linea=lc.readLine())!=null) {
				Map<String,Integer> contadorRepeticiones = new TreeMap<>();

				System.out.println(linea.trim());
			}
		}catch(IOException e) {
			System.out.println("Error : " + e);
		}
	}

}
