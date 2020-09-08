package com.actividades.modelo;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Query;

public class EmpresaDAO extends ClaseDAO{
	@SuppressWarnings("unchecked")
	public List<Empresa> obtenerEmpresa() {
		List<Empresa> resultado = new ArrayList<Empresa>(); 
		Query query = getEntityManager().createNamedQuery("Empresa.buscarEmpresa");
		query.setHint("javax.persistence.cache.storeMode", "REFRESH");
		resultado = (List<Empresa>) query.getResultList();
		return resultado;
	}
}
