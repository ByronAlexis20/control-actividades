package com.actividades.modelo;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.Query;

import com.actividades.util.Constantes;

public class AgendaDAO extends ClaseDAO {
	@SuppressWarnings("unchecked")
	public List<Agenda> obtenerAgendaActiva(Integer idEmpleado, String tipoAgenda) {
		List<Agenda> resultado = new ArrayList<Agenda>(); 
		Query query = getEntityManager().createNamedQuery("Agenda.buscarPorEmpleadoLogeado");
		query.setHint("javax.persistence.cache.storeMode", "REFRESH");
		query.setParameter("idEmpleado",idEmpleado);
		query.setParameter("tipoAgenda",tipoAgenda);
		resultado = (List<Agenda>) query.getResultList();
		return resultado;
	}
	
	@SuppressWarnings("unchecked")
	public List<Agenda> obtenerAgendaActivaYGobernador(Integer idEmpleado, String tipoAgenda) {
		List<Agenda> resultado = new ArrayList<Agenda>(); 
		Query query = getEntityManager().createNamedQuery("Agenda.obtenerAgendaActivaYGobernador");
		query.setHint("javax.persistence.cache.storeMode", "REFRESH");
		query.setParameter("idEmpleado",idEmpleado);
		query.setParameter("tipoAgenda",tipoAgenda);
		query.setParameter("tipoAgendaGobernador", Constantes.CODIGO_TIPO_AGENDA_ENVIADA_GOBERNADOR);
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
	public List<Agenda> obtenerAgendaActivaYFechasTipos(Integer idEmpleado,Date fechaInicio, Date fechaFin) {
		List<Agenda> resultado = new ArrayList<Agenda>(); 
		Query query = getEntityManager().createNamedQuery("Agenda.buscarPorEmpleadoLogeadoYFechasTipos");
		query.setHint("javax.persistence.cache.storeMode", "REFRESH");
		query.setParameter("idEmpleado",idEmpleado);
		query.setParameter("fechaInicio",fechaInicio);
		query.setParameter("fechaFin",fechaFin);
		resultado = (List<Agenda>) query.getResultList();
		return resultado;
	}
	
	@SuppressWarnings("unchecked")
	public List<Agenda> obtenerAgendaActivaYFechasInternas(Integer idEmpleado,Date fechaInicio, Date fechaFin) {
		List<Agenda> resultado = new ArrayList<Agenda>(); 
		Query query = getEntityManager().createNamedQuery("Agenda.buscarPorEmpleadoLogeadoYFechasInternas");
		query.setHint("javax.persistence.cache.storeMode", "REFRESH");
		query.setParameter("idEmpleado",idEmpleado);
		query.setParameter("fechaInicio",fechaInicio);
		query.setParameter("fechaFin",fechaFin);
		query.setParameter("tipoAgenda",Constantes.TIPO_AGENDA_INTERNAS);
		resultado = (List<Agenda>) query.getResultList();
		return resultado;
	}
	
	@SuppressWarnings("unchecked")
	public List<Agenda> obtenerAgendaActivaYFechasYGobernador(Integer idEmpleado,Date fechaInicio, Date fechaFin) {
		List<Agenda> resultado = new ArrayList<Agenda>(); 
		Query query = getEntityManager().createNamedQuery("Agenda.buscarPorEmpleadoLogeadoYFechasTipoActividad");
		query.setHint("javax.persistence.cache.storeMode", "REFRESH");
		query.setParameter("idEmpleado",idEmpleado);
		query.setParameter("fechaInicio",fechaInicio);
		query.setParameter("fechaFin",fechaFin);
		query.setParameter("tipoAgendaPrincipal", Constantes.TIPO_AGENDA_PRINCIPALES);
		query.setParameter("tipoAgendaGobernador", Constantes.CODIGO_TIPO_AGENDA_ENVIADA_GOBERNADOR);
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
	public List<Agenda> obtenerUltimaAgenda(Integer idEmpleado, String tipoAgenda) {
		List<Agenda> resultado = new ArrayList<Agenda>(); 
		Query query = getEntityManager().createNamedQuery("Agenda.buscarUltimaAgenda");
		query.setHint("javax.persistence.cache.storeMode", "REFRESH");
		query.setParameter("idEmpleado",idEmpleado);
		query.setParameter("tipoAgenda",tipoAgenda);
		resultado = (List<Agenda>) query.getResultList();
		return resultado;
	}
	
	@SuppressWarnings("unchecked")
	public List<Agenda> obtenerPorFechaEmpleado(Integer idEmpleado,Date fecha) {
		List<Agenda> resultado = new ArrayList<Agenda>(); 
		Query query = getEntityManager().createNamedQuery("Agenda.buscarPorFechaEmpleado");
		query.setHint("javax.persistence.cache.storeMode", "REFRESH");
		query.setParameter("idEmpleado",idEmpleado);
		query.setParameter("fecha",fecha);
		resultado = (List<Agenda>) query.getResultList();
		return resultado;
	}
	
	@SuppressWarnings("unchecked")
	public List<Agenda> obtenerPorFechaEmpleadoEnviadaPorGobernador(Integer idEmpleado,Date fecha) {
		List<Agenda> resultado = new ArrayList<Agenda>(); 
		Query query = getEntityManager().createNamedQuery("Agenda.buscarPorFechaEmpleadoEnviadaPorGobernador");
		query.setHint("javax.persistence.cache.storeMode", "REFRESH");
		query.setParameter("idEmpleado",idEmpleado);
		query.setParameter("fecha",fecha);
		resultado = (List<Agenda>) query.getResultList();
		return resultado;
	}
}