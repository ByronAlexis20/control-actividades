package com.actividades.modelo;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Query;

public class MenuDAO extends ClaseDAO{
	@SuppressWarnings("unchecked")
	public List<Menu> getOpciones(){
		List<Menu> opciones = new ArrayList<>();		
		Query query = getEntityManager().createNamedQuery("Menu.findAll");
		opciones = (List<Menu>) query.getResultList();		
		return opciones;
	}
	
	@SuppressWarnings("unchecked")
	public List<Menu> getListaMenuHijo(Integer idPadre){
		List<Menu> opciones = new ArrayList<>();		
		Query query = getEntityManager().createNamedQuery("Menu.buscarHijos");
		query.setHint("javax.persistence.cache.storeMode", "REFRESH");
		query.setParameter("idPadre", idPadre);
		opciones = (List<Menu>) query.getResultList();		
		return opciones;
	}
	@SuppressWarnings("unchecked")
	public List<Menu> getMenuPadre(Integer idMenuPadre){
		List<Menu> opciones = new ArrayList<>();		
		Query query = getEntityManager().createNamedQuery("Menu.menuPadre");
		query.setParameter("idMenuPadre", idMenuPadre);
		opciones = (List<Menu>) query.getResultList();		
		return opciones;
	}
	@SuppressWarnings("unchecked")
	public List<Menu> getListaHijos(){
		List<Menu> opciones = new ArrayList<>();		
		Query query = getEntityManager().createNamedQuery("Menu.buscarListadoHijos");
		query.setHint("javax.persistence.cache.storeMode", "REFRESH");
		opciones = (List<Menu>) query.getResultList();		
		return opciones;
	}
}
