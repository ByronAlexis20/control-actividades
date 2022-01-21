package com.actividades.modelo;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.Query;

import com.actividades.util.Constantes;

public class ActividadDAO extends ClaseDAO{
	
	@SuppressWarnings("unchecked")
	public List<Actividad> obtenerActividad(Integer idAgenda,Integer idTipoActividad) {
		List<Actividad> resultado = new ArrayList<Actividad>(); 
		Query query = getEntityManager().createNamedQuery("Actividad.buscarPorAgenda");
		query.setHint("javax.persistence.cache.storeMode", "REFRESH");
		query.setParameter("idAgenda", idAgenda);
		query.setParameter("idTipoActividad", idTipoActividad);
		resultado = (List<Actividad>) query.getResultList();
		return resultado;
	}
	
	@SuppressWarnings("unchecked")
	public List<Actividad> obtenerActividadNoAsignado(Integer idAgenda,Integer idTipoActividad) {
		List<Actividad> resultado = new ArrayList<Actividad>(); 
		Query query = getEntityManager().createNamedQuery("Actividad.buscarPorAgendaNoAsignado");
		query.setHint("javax.persistence.cache.storeMode", "REFRESH");
		query.setParameter("idAgenda", idAgenda);
		query.setParameter("idTipoActividad", idTipoActividad);
		query.setParameter("idTipoEstadoActividad", Constantes.ESTADO_NO_ASIGNADO);
		resultado = (List<Actividad>) query.getResultList();
		return resultado;
	}
	
	@SuppressWarnings("unchecked")
	public List<Actividad> obtenerRechazada(Integer idEmpleado) {
		List<Actividad> resultado = new ArrayList<Actividad>(); 
		Query query = getEntityManager().createNamedQuery("Actividad.buscarRechazadas");
		query.setHint("javax.persistence.cache.storeMode", "REFRESH");
		query.setParameter("idEmpleado", idEmpleado);
		resultado = (List<Actividad>) query.getResultList();
		return resultado;
	}
	
	@SuppressWarnings("unchecked")
	public List<Actividad> obtenerRechazadaPorAgenda(Integer idAgenda) {
		List<Actividad> resultado = new ArrayList<Actividad>(); 
		Query query = getEntityManager().createNamedQuery("Actividad.buscarRechazadasPorAgenda");
		query.setHint("javax.persistence.cache.storeMode", "REFRESH");
		query.setParameter("idAgenda", idAgenda);
		resultado = (List<Actividad>) query.getResultList();
		return resultado;
	}
	
	@SuppressWarnings("unchecked")
	public List<Actividad> obtenerPendiente(Integer idEmpleado) {
		List<Actividad> resultado = new ArrayList<Actividad>(); 
		Query query = getEntityManager().createNamedQuery("Actividad.buscarPendientes");
		query.setHint("javax.persistence.cache.storeMode", "REFRESH");
		query.setParameter("idEmpleado", idEmpleado);
		resultado = (List<Actividad>) query.getResultList();
		return resultado;
	}
	
	@SuppressWarnings("unchecked")
	public List<Actividad> obtenerPendientePorAgenda(Integer idAgenda) {
		List<Actividad> resultado = new ArrayList<Actividad>(); 
		Query query = getEntityManager().createNamedQuery("Actividad.buscarPendientesPorAgenda");
		query.setHint("javax.persistence.cache.storeMode", "REFRESH");
		query.setParameter("idAgenda", idAgenda);
		resultado = (List<Actividad>) query.getResultList();
		return resultado;
	}
	
	@SuppressWarnings("unchecked")
	public List<Actividad> obtenerCodigoActividad(Integer idAgenda) {
		List<Actividad> resultado = new ArrayList<Actividad>(); 
		Query query = getEntityManager().createNamedQuery("Actividad.buscarCodigoPorAgenda");
		query.setHint("javax.persistence.cache.storeMode", "REFRESH");
		query.setParameter("idAgenda", idAgenda);
		resultado = (List<Actividad>) query.getResultList();
		return resultado;
	}
	
	@SuppressWarnings("unchecked")
	public List<Actividad> buscarPorFecha(Date fechaInicio, Date fechaFin, Integer idEmpleado,Integer idTipoActividad) {
		List<Actividad> resultado = new ArrayList<Actividad>(); 
		Query query = getEntityManager().createNamedQuery("Actividad.buscarPorFecha");
		query.setHint("javax.persistence.cache.storeMode", "REFRESH");
		query.setParameter("fechaInicio", fechaInicio);
		query.setParameter("fechaFin", fechaFin);
		query.setParameter("idEmpleado", idEmpleado);
		query.setParameter("idTipoActividad", idTipoActividad);
		resultado = (List<Actividad>) query.getResultList();
		return resultado;
	}
	
	@SuppressWarnings("unchecked")
	public List<Actividad> buscarPorFechaEmpleado(Date fecha, Integer idEmpleado,Integer idTipoActividad) {
		List<Actividad> resultado = new ArrayList<Actividad>(); 
		Query query = getEntityManager().createNamedQuery("Actividad.buscarPorFechaEmpleado");
		query.setHint("javax.persistence.cache.storeMode", "REFRESH");
		query.setParameter("fecha", fecha);
		query.setParameter("idEmpleado", idEmpleado);
		query.setParameter("idTipoActividad", idTipoActividad);
		resultado = (List<Actividad>) query.getResultList();
		return resultado;
	}
	
	@SuppressWarnings("unchecked")
	public List<Actividad> reportePorTipoActividad(Date fechaInicio, Date fechaFin, Integer idEmpleado,Integer idTipoActividad,Integer idClaseActividad) {
		List<Actividad> resultado = new ArrayList<Actividad>(); 
		Query query = getEntityManager().createNamedQuery("Actividad.reportePorTipoActividad");
		query.setHint("javax.persistence.cache.storeMode", "REFRESH");
		query.setParameter("fechaInicio", fechaInicio);
		query.setParameter("fechaFin", fechaFin);
		query.setParameter("idEmpleado", idEmpleado);
		query.setParameter("idTipoActividad", idTipoActividad);
		query.setParameter("idClaseActividad", idClaseActividad);
		resultado = (List<Actividad>) query.getResultList();
		return resultado;
	}
	
	@SuppressWarnings("unchecked")
	public List<Actividad> buscarPorEmpleadoTipoActividad(Integer idEmpleado,Integer idTipoActividad) {
		List<Actividad> resultado = new ArrayList<Actividad>(); 
		Query query = getEntityManager().createNamedQuery("Actividad.buscarPorEmpleadoTipoActividad");
		query.setHint("javax.persistence.cache.storeMode", "REFRESH");
		query.setParameter("idEmpleado", idEmpleado);
		query.setParameter("idTipoActividad", idTipoActividad);
		resultado = (List<Actividad>) query.getResultList();
		return resultado;
	}
	
	@SuppressWarnings("unchecked")
	public List<Actividad> buscarPorEmpleadoTipoActividadFechas(Integer idEmpleado,Integer idTipoActividad, Date fechaInicio, Date fechaFin) {
		List<Actividad> resultado = new ArrayList<Actividad>(); 
		Query query = getEntityManager().createNamedQuery("Actividad.buscarPorEmpleadoTipoActividadFechas");
		query.setHint("javax.persistence.cache.storeMode", "REFRESH");
		query.setParameter("idEmpleado", idEmpleado);
		query.setParameter("idTipoActividad", idTipoActividad);
		query.setParameter("fechaInicio", fechaInicio);
		query.setParameter("fechaFin", fechaFin);
		resultado = (List<Actividad>) query.getResultList();
		return resultado;
	}
	
	@SuppressWarnings("unchecked")
	public List<Actividad> buscarPorEmpleadoTipoActividadFechasInternas(Integer idEmpleado,Integer idTipoActividad, Date fechaInicio, Date fechaFin) {
		List<Actividad> resultado = new ArrayList<Actividad>(); 
		Query query = getEntityManager().createNamedQuery("Actividad.buscarPorEmpleadoTipoActividadFechasInternas");
		query.setHint("javax.persistence.cache.storeMode", "REFRESH");
		query.setParameter("idEmpleado", idEmpleado);
		query.setParameter("idTipoActividad", idTipoActividad);
		query.setParameter("fechaInicio", fechaInicio);
		query.setParameter("fechaFin", fechaFin);
		resultado = (List<Actividad>) query.getResultList();
		return resultado;
	}
	
	@SuppressWarnings("unchecked")
	public List<Actividad> buscarActividadesPublicadas() {
		List<Actividad> resultado = new ArrayList<Actividad>(); 
		Query query = getEntityManager().createNamedQuery("Actividad.buscarActividadesPublicadas");
		query.setHint("javax.persistence.cache.storeMode", "REFRESH");
		resultado = (List<Actividad>) query.getResultList();
		return resultado;
	}
	
	@SuppressWarnings("unchecked")
	public List<Actividad> buscarActividadesPrincipalesPorDepartamento(Integer idDepartamento) {
		List<Actividad> resultado = new ArrayList<Actividad>(); 
		Query query = getEntityManager().createNamedQuery("Actividad.buscarPorDepartamento");
		query.setHint("javax.persistence.cache.storeMode", "REFRESH");
		query.setParameter("idDepartamento", idDepartamento);
		resultado = (List<Actividad>) query.getResultList();
		return resultado;
	}
	
	@SuppressWarnings("unchecked")
	public List<Actividad> buscarPorId(Integer id) {
		List<Actividad> resultado = new ArrayList<Actividad>(); 
		Query query = getEntityManager().createNamedQuery("Actividad.buscarPorId");
		query.setHint("javax.persistence.cache.storeMode", "REFRESH");
		query.setParameter("id", id);
		resultado = (List<Actividad>) query.getResultList();
		return resultado;
	}
}