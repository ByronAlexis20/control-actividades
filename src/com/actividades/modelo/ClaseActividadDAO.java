package com.actividades.modelo;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Query;

public class ClaseActividadDAO extends ClaseDAO {
	@SuppressWarnings("unchecked")
	public List<ClaseActividad> obtenerClaseActividad() {
		List<ClaseActividad> resultado = new ArrayList<ClaseActividad>(); 
		Query query = getEntityManager().createNamedQuery("ClaseActividad.findAll");
		query.setHint("javax.persistence.cache.storeMode", "REFRESH");
		resultado = (List<ClaseActividad>) query.getResultList();
		return resultado;
	}
}