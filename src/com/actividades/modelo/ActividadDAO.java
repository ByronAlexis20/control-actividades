package com.actividades.modelo;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.Query;

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
}
