package com.actividades.modelo;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Query;

import com.actividades.util.Constantes;

public class DepartamentoDAO extends ClaseDAO{
	@SuppressWarnings("unchecked")
	public List<Departamento> getDepartamentosActivos() {
		List<Departamento> resultado = new ArrayList<Departamento>(); 
		Query query = getEntityManager().createNamedQuery("Departamento.buscarActivos");
		query.setHint("javax.persistence.cache.storeMode", "REFRESH");
		resultado = (List<Departamento>) query.getResultList();
		return resultado;
	}
	@SuppressWarnings("unchecked")
	public List<Departamento> getListaDepartamentos(String value) {
		List<Departamento> resultado = new ArrayList<Departamento>(); 
		Query query = getEntityManager().createNamedQuery("Departamento.buscarPorPatron");
		query.setHint("javax.persistence.cache.storeMode", "REFRESH");
		query.setParameter("patron","%" + value.toLowerCase() + "%");
		resultado = (List<Departamento>) query.getResultList();
		return resultado;
	}
	@SuppressWarnings("unchecked")
	public List<Departamento> getDepartamentosReporte(String buscar) {
		List<Departamento> resultado = new ArrayList<Departamento>(); 
		Query query = getEntityManager().createNamedQuery("Departamento.buscarActivosReporte");
		query.setHint("javax.persistence.cache.storeMode", "REFRESH");
		query.setParameter("idDepart",Constantes.ID_DEPARTAMENTO_GOBERNACION);
		query.setParameter("patron","%" + buscar.toLowerCase() + "%");
		resultado = (List<Departamento>) query.getResultList();
		return resultado;
	}
}
