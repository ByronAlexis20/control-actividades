package com.actividades.modelo;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Query;

public class TipoUsuarioDAO extends ClaseDAO{
	@SuppressWarnings("unchecked")
	public List<TipoUsuario> getTiposUsuariosActivos() {
		List<TipoUsuario> resultado = new ArrayList<TipoUsuario>(); 
		Query query = getEntityManager().createNamedQuery("TipoUsuario.buscarActivos");
		query.setHint("javax.persistence.cache.storeMode", "REFRESH");
		resultado = (List<TipoUsuario>) query.getResultList();
		return resultado;
	}
	@SuppressWarnings("unchecked")
	public List<TipoUsuario> getListaTiposUsuarios() {
		List<TipoUsuario> retorno = new ArrayList<TipoUsuario>();
		Query query = getEntityManager().createNamedQuery("Perfil.findAll");
		retorno = (List<TipoUsuario>) query.getResultList();
		return retorno;
	}
}
