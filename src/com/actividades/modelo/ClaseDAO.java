package com.actividades.modelo;

import java.sql.Connection;
import java.sql.SQLException;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

public abstract class ClaseDAO {
	// Crea una sola instancia de EntityManagerFactory para toda la applicacion.
	private static final String PERSISTENCE_UNIT_NAME = "control-actividades";
	private static EntityManagerFactory emf = Persistence.createEntityManagerFactory(PERSISTENCE_UNIT_NAME);

	// Objeto Entity Manager para cada instancia de un objeto que 
	// herede de esta clase.
	private EntityManager em;

	/**
	 * Retorna el Entity Mananager, si no existe lo crea.
	 * @return
	 */
	public EntityManager getEntityManager() {
		if (em == null){
			em = emf.createEntityManager();
		}
		return em; 
	}	

	public Connection abreConexion() {
		EntityManager entityManager; 
		entityManager = Persistence.createEntityManagerFactory(PERSISTENCE_UNIT_NAME).createEntityManager();
	    Connection connection = null;
	    entityManager.getTransaction().begin();
	    connection = entityManager.unwrap(Connection.class);
	    System.out.println("Conexion obtenida : " + connection.toString());
	    return connection;
	  }

	/**
	 * Cierra una conexion JDBC.
	 * @param cn
	 */
	public void cierraConexion(Connection cn) {
		 try {
			cn.close();
			cn = null;
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
}
