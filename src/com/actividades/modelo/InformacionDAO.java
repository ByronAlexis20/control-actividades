package com.actividades.modelo;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Query;

public class InformacionDAO extends ClaseDAO {
	@SuppressWarnings("unchecked")
	public List<Informacion> buscarTodos() {
		List<Informacion> resultado = new ArrayList<Informacion>(); 
		Query query = getEntityManager().createNamedQuery("Informacion.buscarTodos");
		query.setHint("javax.persistence.cache.storeMode", "REFRESH");
		resultado = (List<Informacion>) query.getResultList();
		return resultado;
	}
}
