package com.actividades.modelo;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Query;

public class CargoDAO extends ClaseDAO{
	@SuppressWarnings("unchecked")
	public List<Cargo> getCargosActivos() {
		List<Cargo> resultado = new ArrayList<Cargo>(); 
		Query query = getEntityManager().createNamedQuery("Cargo.buscarActivos");
		query.setHint("javax.persistence.cache.storeMode", "REFRESH");
		resultado = (List<Cargo>) query.getResultList();
		return resultado;
	}
	@SuppressWarnings("unchecked")
	public List<Cargo> getListaCargos(String value) {
		List<Cargo> resultado = new ArrayList<Cargo>(); 
		Query query = getEntityManager().createNamedQuery("Cargo.buscarPorPatron");
		query.setHint("javax.persistence.cache.storeMode", "REFRESH");
		query.setParameter("patron","%" + value.toLowerCase() + "%");
		resultado = (List<Cargo>) query.getResultList();
		return resultado;
	}
}
