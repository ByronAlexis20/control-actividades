package com.actividades.modelo;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Query;

public class EvidenciaDAO extends ClaseDAO{
	@SuppressWarnings("unchecked")
	public List<Evidencia> obtenerEvidencias(Integer idActividad) {
		List<Evidencia> resultado = new ArrayList<Evidencia>(); 
		Query query = getEntityManager().createNamedQuery("Evidencia.buscarPorActividad");
		query.setHint("javax.persistence.cache.storeMode", "REFRESH");
		query.setParameter("idActividad", idActividad);
		resultado = (List<Evidencia>) query.getResultList();
		return resultado;
	}
}
