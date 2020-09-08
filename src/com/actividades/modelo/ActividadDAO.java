package com.actividades.modelo;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Query;

public class ActividadDAO extends ClaseDAO{
	@SuppressWarnings("unchecked")
	public List<Actividad> obtenerActividad(Integer idAgenda) {
		List<Actividad> resultado = new ArrayList<Actividad>(); 
		Query query = getEntityManager().createNamedQuery("Actividad.buscarPorAgenda");
		query.setHint("javax.persistence.cache.storeMode", "REFRESH");
		query.setParameter("idAgenda", idAgenda);
		resultado = (List<Actividad>) query.getResultList();
		return resultado;
	}
}
