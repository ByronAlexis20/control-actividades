package com.actividades.modelo;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Query;

public class PublicacionDAO extends ClaseDAO {
	@SuppressWarnings("unchecked")
	public List<Publicacion> getListaPublicacion(String value) {
		List<Publicacion> resultado = new ArrayList<Publicacion>(); 
		Query query = getEntityManager().createNamedQuery("Publicacion.buscarPorPatron");
		query.setHint("javax.persistence.cache.storeMode", "REFRESH");
		query.setParameter("patron","%" + value.toLowerCase() + "%");
		resultado = (List<Publicacion>) query.getResultList();
		return resultado;
	}
}
