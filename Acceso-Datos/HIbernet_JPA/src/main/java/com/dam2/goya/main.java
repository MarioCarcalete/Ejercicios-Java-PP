package com.dam2.goya;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;

public class main {
	private static final Logger LOG_HIBERNATE = Logger.getLogger("org.hibernate");

	public static void main(String[] args) {
		LOG_HIBERNATE.setLevel(Level.SEVERE); // solo muestra los errores

		EntityManagerFactory emf = Persistence.createEntityManagerFactory("clasePU");
		EntityManager em = emf.createEntityManager();

		try {
			// Producto es la CLASE, no la tabla
			// Esto es como hacer "SELECT * FROM productos"
			String jpql = "FROM Producto";
			String jpql2 = "FROM Producto p WHERE p.precio < 100";
			String jpql3 = "FROM Producto p WHERE p.nombre LIKE 'Teclado%' ORDER BY p.precio DESC";

			// 1) Todos los productos
			List<Producto> listaProductos = em.createQuery(jpql, Producto.class).getResultList();
			for (Producto p : listaProductos) {
				System.out.println(p);
			}
			System.out.println("Total de productos en la tabla: " + listaProductos.size());

			// 2) Productos con precio < 100
			System.out.println("\n--- Precio < 100 ---");
			List<Producto> baratos = em.createQuery(jpql2, Producto.class).getResultList();
			for (Producto p : baratos) {
				System.out.println(p);
			}

			// 3) Productos que empiezan por "Teclado", del más caro al más barato
			System.out.println("\n--- Teclados ordenados por precio DESC ---");
			List<Producto> teclados = em.createQuery(jpql3, Producto.class).getResultList();
			for (Producto p : teclados) {
				System.out.println(p);
			}

			// 4a) Parámetros POSICIONALES: ?1, ?2...
			System.out.println("\n--- Parámetros posicionales (?1, ?2) ---");
			String jpql5 = "FROM Producto p WHERE p.precio > ?1 AND p.nombre LIKE ?2";
			List<Producto> lista = em.createQuery(jpql5, Producto.class)
					.setParameter(1, 20.0)      // ?1 -> precio mínimo
					.setParameter(2, "Mon%")    // ?2 -> nombre que empieza por "Mon"
					.getResultList();
			for (Producto p : lista) {
				System.out.println(p);
			}

			// 4b) Parámetros CON NOMBRE: :nombre
			System.out.println("\n--- Parámetros con nombre (:precioMin, :nombreProd) ---");
			String jpql6 = "FROM Producto p WHERE p.precio > :precioMin AND p.nombre LIKE :nombreProd";
			List<Producto> lista2 = em.createQuery(jpql6, Producto.class)
					.setParameter("precioMin", 20.0)
					.setParameter("nombreProd", "Mon%")
					.getResultList();
			for (Producto p : lista2) {
				System.out.println(p);
			}

			// 5) Solo algunas columnas: devuelve Object[] por cada fila, no Producto
			System.out.println("\n--- Solo nombre y precio ---");
			String jpql4 = "SELECT p.nombre, p.precio FROM Producto p";
			List<Object[]> filas = em.createQuery(jpql4, Object[].class).getResultList();
			for (Object[] fila : filas) {
				String nombre = (String) fila[0];
				double precio = (Double) fila[1];
				System.out.println(nombre + " -> " + precio + " €");
			}

		} catch (Exception e) {
			System.out.println("Error: " + e.getMessage());
			e.printStackTrace();
		} finally {
			em.close();
			emf.close();
		}
	}
}