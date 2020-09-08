package com.actividades.modelo;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Query;

public class AgendaDAO extends ClaseDAO{
	@SuppressWarnings("unchecked")
	public List<Agenda> obtenerAgendaActiva() {
		List<Agenda> resultado = new ArrayList<Agenda>(); 
		Query query = getEntityManager().createNamedQuery("Agenda.buscarActivos");
		query.setHint("javax.persistence.cache.storeMode", "REFRESH");
		resultado = (List<Agenda>) query.getResultList();
		return resultado;
	}
}
