package Clase1_De_Acceso_Datos; // Paquete donde está la clase (organiza el proyecto en carpetas)

import java.io.File;            // Clase para representar rutas, archivos y directorios
import java.io.FileWriter;      // Clase para escribir texto en un archivo
import java.io.BufferedWriter;  // "Envuelve" a FileWriter para escribir de forma más eficiente (con buffer)
import java.io.IOException;     // Excepción que hay que controlar al trabajar con archivos
import java.nio.file.Files;     // (Importada pero no usada en este código; alternativa moderna a File)

public class Clase1 {

    // Constante con la ruta del archivo de configuración.
    // File.separator pone automáticamente "\" en Windows o "/" en Linux/Mac,
    // así la ruta funciona en cualquier sistema operativo sin tener que escribirla a mano.
    final static String DIR_CONFIG = "DAM2" + File.separator + "josemaria" + File.separator + "config.txt";
    
    // puedo hacer otro archivo_config y poner DIR_CONFIG + FILE.SEPARATOR MAS EL TXT 

    // para ver si existe ese directorio dentro de la carpeta o demás
    public static void main(String[] args) throws IOException {

        System.out.println("Hola mundo ");

        // ---------------------------------------------------------
        // 1) Ver la ruta actual desde donde se ejecuta el programa
        // ---------------------------------------------------------

        // Para ver la ruta, es decir, que se acople a la ruta en la que esté,
        // por si cambio de equipo, funcione igual (rutas absolutas y relativas).
        File directorioActual = new File(".");
        // El "." representa "aquí mismo", el directorio desde el que se ejecuta el programa.
        // Es una ruta RELATIVA (depende de desde dónde lances el programa).

        System.out.println(directorioActual.getAbsolutePath());
        // getAbsolutePath() convierte esa ruta relativa "." en la ruta ABSOLUTA completa
        // (por ejemplo: /home/usuario/proyecto/. ). Sirve para comprobar dónde está trabajando el programa.

        // ---------------------------------------------------------
        // 2) Comprobar si el archivo de configuración existe
        // ---------------------------------------------------------

        File dirConfig = new File(DIR_CONFIG);
        // Creamos un objeto File apuntando a la ruta "DAM2/josemaria/config.txt".
        // OJO: esto NO crea nada todavía, solo representa esa ruta en memoria.

   
                
        
        if (dirConfig.exists() == true) {
            // exists() comprueba si ese archivo YA existe físicamente en el disco.
            System.out.println("EL directorio " + DIR_CONFIG + " EXISTE ");
            // Si existe, no hacemos nada más: se deja tal cual está.
            
                                                                                                                                                                                                                                                                                 
        } else {
            // Si no existe, entramos aquí para crearlo.
            System.out.println("EL directorio " + DIR_CONFIG + " no EXISTE ");

            dirConfig.getParentFile().mkdirs();
            // getParentFile() -> nos da la carpeta que contiene al archivo, es decir "DAM2/josemaria".
            // mkdirs() (con "s", plural) crea TODAS las carpetas necesarias de ese camino,
            // incluso si faltan varios niveles (DAM2 Y josemaria si ninguna existiera).
            // (mkdir(), en singular, solo crearía UN nivel y fallaría si el padre no existe).

            // Ahora creamos el archivo config.txt dentro de esas carpetas ya creadas
            try (BufferedWriter bw = new BufferedWriter(new FileWriter(dirConfig))) {
                // new FileWriter(dirConfig) -> abre (y crea, si no existe) el archivo para escritura.
                // new BufferedWriter(...) -> añade un buffer para que escribir sea más eficiente.
                // El "try (...)" es un try-with-resources: al terminar el bloque,
                // Java CIERRA automáticamente el BufferedWriter (y el FileWriter), aunque salte un error.

                bw.write("Configuración inicial");
                bw.newLine();

                bw.write("ES aqui ");
                // Escribe ese texto como primera línea del archivo.

                System.out.println("Archivo " + DIR_CONFIG + " creado correctamente.");
               
            }
            
     

        catch (IOException e) {
                // Si algo falla al crear/escribir el archivo (por ejemplo, permisos denegados),
                // se captura aquí el error en vez de que el programa se rompa sin más.
                System.out.println("Error al crear el archivo: " + e.getMessage());
            }
        }
  

        // ---------------------------------------------------------
        // 3) Comparar el directorio actual con el directorio padre
        // ---------------------------------------------------------

        /*
         * . (un punto)
         * Significa "el directorio actual" — es decir, el directorio de trabajo desde donde se ejecuta el programa.
         * java
         * File directorioActual = new File(".");
         * System.out.println(directorioActual.getAbsolutePath());
         * // Imprime algo como: /home/usuario/miproyecto/.
         * System.out.println(directorioActual.getCanonicalPath());
         * // Imprime: /home/usuario/miproyecto
         * Es equivalente a usar System.getProperty("user.dir") en la mayoría de los casos.
         *
         * .. (dos puntos)
         * Significa "el directorio padre" — un nivel arriba del actual.
         * java
         * File directorioPadre = new File("..");
         * System.out.println(directorioPadre.getCanonicalPath());
         * // Si estás en /home/usuario/miproyecto, imprime: /home/usuario
         */

        if (!directorioActual.getAbsolutePath().equals(new File("..").getAbsolutePath())) {
            // new File("..") representa el directorio PADRE (un nivel arriba).
            // Comparamos su ruta absoluta con la del directorio actual (".").
            // El "!" invierte la condición: "si NO son iguales..."
            // (Casi siempre serán distintos, porque el actual y el padre son carpetas diferentes,
            // salvo casos muy raros de sistema de archivos en la raíz).

            System.out.println("El directorio actual y el padre son distintos, como es normal.");
        }
        long espacio = dirConfig.getFreeSpace()/1024/1024/1024;
        System.out.println("Espacio libre en el disco " + espacio + " GB");
    }
}

// La clase File y Files son para trabajar con los archivos para el acceso a datos
/*

if (dirConfig.exists() == true) {
    // exists() comprueba si ese archivo YA existe físicamente en el disco.
    System.out.println("EL directorio " + DIR_CONFIG + " EXISTE ");
    // Si existe, no hacemos nada más: se deja tal cual está.
                                                                                                                                                                                                                                                                         
} else {
    // Si no existe, entramos aquí para crearlo.
    System.out.println("EL directorio " + DIR_CONFIG + " no EXISTE ");

    if(dirConfig.mkdirs()==false) {
    	crearFichero = false;
    	system.out.println("NO he podido crear el archivo ");
    }
}

/*
 * 
 * otro metodo 
if(crearFichero == true) {
	FIleWriter escritor = new FileWriter(ARCHIVO_CONFIG,true);
	if(escritor == null)
		System.out.println("no se ha posiso");
	
	else {
		System.out.println("ya existia");
		
	}
	
}
	}
	
}

*/
