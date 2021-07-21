package com.actividades.modelo;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.Query;

public class AgendaDAO extends ClaseDAO {
	@SuppressWarnings("unchecked")
	public List<Agenda> obtenerAgendaActiva(Integer idEmpleado) {
		List<Agenda> resultado = new ArrayList<Agenda>(); 
		Query query = getEntityManager().createNamedQuery("Agenda.buscarPorEmpleadoLogeado");
		query.setHint("javax.persistence.cache.storeMode", "REFRESH");
		query.setParameter("idEmpleado",idEmpleado);
		resultado = (List<Agenda>) query.getResultList();
		return resultado;
	}
	@SuppressWarnings("unchecked")
	public List<Agenda> obtenerAgendaActivaYFechas(Integer idEmpleado,Date fechaInicio, Date fechaFin) {
		List<Agenda> resultado = new ArrayList<Agenda>(); 
		Query query = getEntityManager().createNamedQuery("Agenda.buscarPorEmpleadoLogeadoYFechas");
		query.setHint("javax.persistence.cache.storeMode", "REFRESH");
		query.setParameter("idEmpleado",idEmpleado);
		query.setParameter("fechaInicio",fechaInicio);
		query.setParameter("fechaFin",fechaFin);
		resultado = (List<Agenda>) query.getResultList();
		return resultado;
	}
	@SuppressWarnings("unchecked")
	public List<Agenda> obtenerCodigoAgendaActiva(Integer idEmpleado) {
		List<Agenda> resultado = new ArrayList<Agenda>(); 
		Query query = getEntityManager().createNamedQuery("Agenda.buscarPorEmpleadoLogeadoCodigoAgenda");
		query.setHint("javax.persistence.cache.storeMode", "REFRESH");
		query.setParameter("idEmpleado",idEmpleado);
		resultado = (List<Agenda>) query.getResultList();
		return resultado;
	}
	@SuppressWarnings("unchecked")
	public List<Agenda> obtenerUltimaAgenda(Integer idEmpleado) {
		List<Agenda> resultado = new ArrayList<Agenda>(); 
		Query query = getEntityManager().createNamedQuery("Agenda.buscarUltimaAgenda");
		query.setHint("javax.persistence.cache.storeMode", "REFRESH");
		query.setParameter("idEmpleado",idEmpleado);
		resultado = (List<Agenda>) query.getResultList();
		return resultado;
	}
}