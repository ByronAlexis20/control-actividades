package com.actividades.modelo;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Query;

public class PermisoDAO extends ClaseDAO{
	@SuppressWarnings("unchecked")
	public List<Permiso> getListaPermisosPadre(Integer idPerfil) {
		List<Permiso> resultado = new ArrayList<Permiso>(); 
		Query query = getEntityManager().createNamedQuery("Permiso.buscarPadrePorPerfil");
		query.setHint("javax.persistence.cache.storeMode", "REFRESH");
		query.setParameter("idperfil", idPerfil);
		resultado = (List<Permiso>) query.getResultList();
		return resultado;
	}
	
	@SuppressWarnings("unchecked")
	public List<Permiso> getListaPermisosTodos(Integer idPerfil) {
		List<Permiso> resultado = new ArrayList<Permiso>(); 
		Query query = getEntityManager().createNamedQuery("Permiso.buscarTodosPorPerfil");
		query.setHint("javax.persistence.cache.storeMode", "REFRESH");
		query.setParameter("idperfil", idPerfil);
		resultado = (List<Permiso>) query.getResultList();
		return resultado;
	}
	@SuppressWarnings("unchecked")
	public List<Permiso> getListaPermisosHijo(Integer idPerfil) {
		List<Permiso> resultado = new ArrayList<Permiso>(); 
		Query query = getEntityManager().createNamedQuery("Permiso.buscarTodosPorPerfil");
		query.setHint("javax.persistence.cache.storeMode", "REFRESH");
		query.setParameter("idperfil", idPerfil);
		resultado = (List<Permiso>) query.getResultList();
		return resultado;
	}
	
}
