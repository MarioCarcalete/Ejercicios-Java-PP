package Ejercicio2Boletin1;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
public class Ejercicio2 {
    final static String ANIM = "DAM2" + File.separator + "josemaria" + File.separator + "animes.txt";
    final static String PERS_DAT ="DAM2" + File.separator + "josemaria" + File.separator + "personajes.dat";
public static void main(String[] args) {
	List<String[]> lineas = leerfichero(ANIM);
	
}
public static List<String[]> leerfichero(String ruta){
	List<String[]> lineas  = new ArrayList<>();
	
	try(BufferedReader  lc = new BufferedReader(new FileReader(ruta))){
		String linea;
		
		while((linea = lc.readLine())!=null) {
			String[] partes = linea.trim().split(" ",2);
			if(partes.length == 2 && partes[0].matches("\\d+")) {
				lineas.add(partes);
			}
		}
		
		
	}catch(IOException e) {
		System.out.println("Error  : " + e);
	}
	
	
	
	
	return lineas;
	
}


}
 