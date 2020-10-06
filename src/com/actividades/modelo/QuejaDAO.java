package com.actividades.modelo;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Query;

public class QuejaDAO extends ClaseDAO{
	@SuppressWarnings("unchecked")
	public List<Queja> buscarPorResponsable(Integer id,String value) {
		List<Queja> resultado = new ArrayList<Queja>(); 
		Query query = getEntityManager().createNamedQuery("Queja.buscarPorResponsable");
		query.setHint("javax.persistence.cache.storeMode", "REFRESH");
		query.setParameter("id", id);
		query.setParameter("patron","%" + value.toLowerCase() + "%");
		resultado = (List<Queja>) query.getResultList();
		return resultado;
	}
	@SuppressWarnings("unchecked")
	public List<Queja> buscarPorEstado(String estado) {
		List<Queja> resultado = new ArrayList<Queja>(); 
		Query query = getEntityManager().createNamedQuery("Queja.buscarPorEstado");
		query.setHint("javax.persistence.cache.storeMode", "REFRESH");
		query.setParameter("estado", estado);
		resultado = (List<Queja>) query.getResultList();
		return resultado;
	}
}
