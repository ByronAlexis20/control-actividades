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
	@SuppressWarnings("unchecked")
	public List<Departamento> getDepartamentoPorId(Integer id) {
		List<Departamento> resultado = new ArrayList<Departamento>(); 
		Query query = getEntityManager().createNamedQuery("Departamento.buscarDepartamentoPorId");
		query.setHint("javax.persistence.cache.storeMode", "REFRESH");
		query.setParameter("id", id);
		resultado = (List<Departamento>) query.getResultList();
		return resultado;
	}
	@SuppressWarnings("unchecked")
	public List<Departamento> getDepartamentoPorCodigo(String codigo) {
		List<Departamento> resultado = new ArrayList<Departamento>(); 
		Query query = getEntityManager().createNamedQuery("Departamento.buscarDepartamentoPorCodigo");
		query.setHint("javax.persistence.cache.storeMode", "REFRESH");
		query.setParameter("codigo", codigo);
		resultado = (List<Departamento>) query.getResultList();
		return resultado;
	}
	@SuppressWarnings("unchecked")
	public List<Departamento> getDepartamentoPorCodigoDiferenteId(String codigo, Integer id) {
		List<Departamento> resultado = new ArrayList<Departamento>(); 
		Query query = getEntityManager().createNamedQuery("Departamento.buscarDepartamentoPorCodigoId");
		query.setHint("javax.persistence.cache.storeMode", "REFRESH");
		query.setParameter("codigo", codigo);
		query.setParameter("id", id);
		resultado = (List<Departamento>) query.getResultList();
		return resultado;
	}
}