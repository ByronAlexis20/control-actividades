package com.actividades.modelo;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Query;

public class AgendaDAO extends ClaseDAO{
	@SuppressWarnings("unchecked")
	public List<Agenda> obtenerAgendaActiva(Integer idEmpleado) {
		List<Agenda> resultado = new ArrayList<Agenda>(); 
		Query query = getEntityManager().createNamedQuery("Agenda.buscarPorEmpleadoLogeado");
		query.setHint("javax.persistence.cache.storeMode", "REFRESH");
		query.setParameter("idEmpleado",idEmpleado);
		resultado = (List<Agenda>) query.getResultList();
		return resultado;
	}
}
