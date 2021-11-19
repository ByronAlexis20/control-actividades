package com.actividades.modelo;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Query;

public class QuejaDAO extends ClaseDAO{
	
	@SuppressWarnings("unchecked")
	public List<Queja> buscarPorResponsable(Integer id,String value,String estado) {
		List<Queja> resultado = new ArrayList<Queja>(); 
		Query query = getEntityManager().createNamedQuery("Queja.buscarPorResponsable");
		query.setHint("javax.persistence.cache.storeMode", "REFRESH");
		query.setParameter("id", id);
		query.setParameter("patron","%" + value.toLowerCase() + "%");
		query.setParameter("estado",estado);
		resultado = (List<Queja>) query.getResultList();
		return resultado;
	}
	
	@SuppressWarnings("unchecked")
	public List<Queja> buscarPorResponsableEstadoQuejaAtencion(Integer id,String value,String estadoQueja,String estadoAtencion) {
		List<Queja> resultado = new ArrayList<Queja>(); 
		Query query = getEntityManager().createNamedQuery("Queja.buscarPorResponsableEstadoQuejaAtencion");
		query.setHint("javax.persistence.cache.storeMode", "REFRESH");
		query.setParameter("id", id);
		query.setParameter("patron","%" + value.toLowerCase() + "%");
		query.setParameter("estadoQueja",estadoQueja);
		query.setParameter("estadoAtencion",estadoAtencion);
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
	
	@SuppressWarnings("unchecked")
	public List<Queja> buscarQuejas(String patron) {
		List<Queja> resultado = new ArrayList<Queja>(); 
		Query query = getEntityManager().createNamedQuery("Queja.buscarQueja");
		query.setHint("javax.persistence.cache.storeMode", "REFRESH");
		query.setParameter("patron","%" + patron.toLowerCase() + "%");
		resultado = (List<Queja>) query.getResultList();
		return resultado;
	}
	
	@SuppressWarnings("unchecked")
	public List<Queja> buscarActivos() {
		List<Queja> resultado = new ArrayList<Queja>(); 
		Query query = getEntityManager().createNamedQuery("Queja.buscarActivos");
		query.setHint("javax.persistence.cache.storeMode", "REFRESH");
		resultado = (List<Queja>) query.getResultList();
		return resultado;
	}
}