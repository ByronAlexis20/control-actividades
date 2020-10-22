package com.actividades.modelo;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Query;

public class TipoActividadDAO extends ClaseDAO{
	@SuppressWarnings("unchecked")
	public List<TipoActividad> obtenerPorId(Integer id) {
		List<TipoActividad> resultado = new ArrayList<TipoActividad>(); 
		Query query = getEntityManager().createNamedQuery("TipoActividad.buscarPorId");
		query.setHint("javax.persistence.cache.storeMode", "REFRESH");
		query.setParameter("id",id);
		resultado = (List<TipoActividad>) query.getResultList();
		return resultado;
	}
}
