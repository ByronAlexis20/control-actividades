package com.actividades.modelo;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Query;

public class TipoEvidenciaDAO extends ClaseDAO{
	@SuppressWarnings("unchecked")
	public List<TipoEvidencia> getListaTiposEvidencia(String value) {
		List<TipoEvidencia> resultado = new ArrayList<TipoEvidencia>(); 
		Query query = getEntityManager().createNamedQuery("TipoEvidencia.buscarPorPatron");
		query.setHint("javax.persistence.cache.storeMode", "REFRESH");
		query.setParameter("patron","%" + value.toLowerCase() + "%");
		resultado = (List<TipoEvidencia>) query.getResultList();
		return resultado;
	}
	
	@SuppressWarnings("unchecked")
	public List<TipoEvidencia> buscarPorId(Integer id) {
		List<TipoEvidencia> resultado = new ArrayList<TipoEvidencia>(); 
		Query query = getEntityManager().createNamedQuery("TipoEvidencia.buscarPorID");
		query.setHint("javax.persistence.cache.storeMode", "REFRESH");
		query.setParameter("id", id);
		resultado = (List<TipoEvidencia>) query.getResultList();
		return resultado;
	}
}
