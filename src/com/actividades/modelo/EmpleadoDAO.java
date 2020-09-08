package com.actividades.modelo;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Query;


public class EmpleadoDAO extends ClaseDAO{
	public Empleado getUsuario(String nombreUsuario) {
		Empleado empleado; 
		Query consulta;
		consulta = getEntityManager().createNamedQuery("Empleado.buscaUsuario");
		consulta.setParameter("nombreUsuario", nombreUsuario);
		empleado = (Empleado) consulta.getSingleResult();
		return empleado;
	}
	
	@SuppressWarnings("unchecked")
	public List<Empleado> getListausuarioBuscar(String value) {
		List<Empleado> resultado = new ArrayList<Empleado>(); 
		Query query = getEntityManager().createNamedQuery("Empleado.buscarPorPatron");
		query.setHint("javax.persistence.cache.storeMode", "REFRESH");
		query.setParameter("patron","%" + value.toLowerCase() + "%");
		resultado = (List<Empleado>) query.getResultList();
		return resultado;
	}
	
	
	@SuppressWarnings("unchecked")
	public List<Empleado> getBuscarUsuario(String value,Integer idUsuario) {
		List<Empleado> resultado = new ArrayList<Empleado>(); 
		Query query = getEntityManager().createNamedQuery("Empleado.buscarPorUsuario");
		query.setHint("javax.persistence.cache.storeMode", "REFRESH");
		query.setParameter("patron", value.toLowerCase());
		query.setParameter("idUsuario", idUsuario);
		resultado = (List<Empleado>) query.getResultList();
		return resultado;
	}
	
	@SuppressWarnings("unchecked")
	public List<Empleado> buscarEmpleados(String value) {
		List<Empleado> resultado; 
		String patron = value;

		if (value == null || value.length() == 0) {
			patron = "%";
		}else{
			patron = "%" + patron.toLowerCase() + "%";
		}
		Query query = getEntityManager().createNamedQuery("Empleado.buscarEmpleado");
		query.setHint("javax.persistence.cache.storeMode", "REFRESH");
		query.setParameter("patron", "%" + patron + "%");
		resultado = (List<Empleado>) query.getResultList();
		return resultado;
	}
	@SuppressWarnings("unchecked")
	public List<Empleado> getValidarUsuarioExistente(String cedulaUsuario) {
		List<Empleado> resultado; 
		Query query = getEntityManager().createNamedQuery("Empleado.buscarPorCedula");
		query.setHint("javax.persistence.cache.storeMode", "REFRESH");
		query.setParameter("cedula", cedulaUsuario);
		resultado = (List<Empleado>) query.getResultList();
		return resultado;
	}
	@SuppressWarnings("unchecked")
	public List<Empleado> getValidarUsuarioExistenteDiferente(String cedulaUsuario,Integer idEmpleado) {
		List<Empleado> resultado; 
		Query query = getEntityManager().createNamedQuery("Empleado.buscarPorCedulaDiferenteAlUsuarioActual");
		query.setHint("javax.persistence.cache.storeMode", "REFRESH");
		query.setParameter("cedula", cedulaUsuario);
		query.setParameter("idEmpleado", idEmpleado);
		resultado = (List<Empleado>) query.getResultList();
		return resultado;
	}
	@SuppressWarnings("unchecked")
	public List<Empleado> getValidarUsuarioNuevo(String usuario) {
		List<Empleado> resultado; 
		Query query = getEntityManager().createNamedQuery("Empleado.buscarPorUsuarioNuevo");
		query.setHint("javax.persistence.cache.storeMode", "REFRESH");
		query.setParameter("usuario", usuario);
		resultado = (List<Empleado>) query.getResultList();
		return resultado;
	}
	@SuppressWarnings("unchecked")
	public List<Empleado> getValidarUsuarioRegistrado(String cedulaUsuario,String usuario) {
		List<Empleado> resultado; 
		Query query = getEntityManager().createNamedQuery("Empleado.buscarPorUsuarioRegistrado");
		query.setHint("javax.persistence.cache.storeMode", "REFRESH");
		query.setParameter("cedula", cedulaUsuario);
		query.setParameter("usuario", usuario);
		resultado = (List<Empleado>) query.getResultList();
		return resultado;
	}
}
