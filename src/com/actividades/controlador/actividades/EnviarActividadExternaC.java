package com.actividades.controlador.actividades;

import java.util.List;

import org.zkoss.bind.annotation.AfterCompose;
import org.zkoss.bind.annotation.Command;
import org.zkoss.bind.annotation.ContextParam;
import org.zkoss.bind.annotation.ContextType;
import org.zkoss.bind.annotation.GlobalCommand;
import org.zkoss.bind.annotation.NotifyChange;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.select.Selectors;

import com.actividades.modelo.ActividadExterna;
import com.actividades.modelo.ActividadExternaDAO;

public class EnviarActividadExternaC {
	private List<ActividadExterna> listaActividadExterna;
	ActividadExterna actividadExternaSeleccionado;
	ActividadExternaDAO actividadExternaDAO = new ActividadExternaDAO();
	
	@AfterCompose
	public void afterCompose(@ContextParam(ContextType.VIEW) Component view) {
		Selectors.wireComponents(view, this, false);
		this.cargarActividades();
	}
	@GlobalCommand("ActividadExterna.buscarActivas")
	@Command
	@NotifyChange({"listaActividadExterna"})
	public void cargarActividades() {
		listaActividadExterna = this.actividadExternaDAO.obtenerActividades();
	}
	public List<ActividadExterna> getListaActividadExterna() {
		return listaActividadExterna;
	}
	public void setListaActividadExterna(List<ActividadExterna> listaActividadExterna) {
		this.listaActividadExterna = listaActividadExterna;
	}
	public ActividadExterna getActividadExternaSeleccionado() {
		return actividadExternaSeleccionado;
	}
	public void setActividadExternaSeleccionado(ActividadExterna actividadExternaSeleccionado) {
		this.actividadExternaSeleccionado = actividadExternaSeleccionado;
	}
}