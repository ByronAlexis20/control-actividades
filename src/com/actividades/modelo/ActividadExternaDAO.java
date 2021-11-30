package com.actividades.modelo;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Query;

public class ActividadExternaDAO extends ClaseDAO {
	
	@SuppressWarnings("unchecked")
	public List<ActividadExterna> obtenerActividades() {
		List<ActividadExterna> resultado = new ArrayList<ActividadExterna>(); 
		Query query = getEntityManager().createNamedQuery("ActividadExterna.buscarActivas");
		query.setHint("javax.persistence.cache.storeMode", "REFRESH");
		resultado = (List<ActividadExterna>) query.getResultList();
		return resultado;
	}
	
}