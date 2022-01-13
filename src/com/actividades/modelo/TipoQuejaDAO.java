package com.actividades.modelo;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Query;

public class TipoQuejaDAO extends ClaseDAO {
	
	@SuppressWarnings("unchecked")
	public List<TipoQueja> obtenerListaTipoQueja() {
		List<TipoQueja> resultado = new ArrayList<TipoQueja>(); 
		Query query = getEntityManager().createNamedQuery("TipoQueja.findAll");
		query.setHint("javax.persistence.cache.storeMode", "REFRESH");
		resultado = (List<TipoQueja>) query.getResultList();
		return resultado;
	}
}