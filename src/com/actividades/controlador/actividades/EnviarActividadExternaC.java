package com.actividades.controlador.actividades;

import java.util.List;

import org.zkoss.bind.BindUtils;
import org.zkoss.bind.annotation.AfterCompose;
import org.zkoss.bind.annotation.Command;
import org.zkoss.bind.annotation.ContextParam;
import org.zkoss.bind.annotation.ContextType;
import org.zkoss.bind.annotation.GlobalCommand;
import org.zkoss.bind.annotation.NotifyChange;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.select.Selectors;
import org.zkoss.zk.ui.util.Clients;
import org.zkoss.zul.Messagebox;
import org.zkoss.zul.Window;

import com.actividades.modelo.ActividadExterna;
import com.actividades.modelo.ActividadExternaDAO;
import com.actividades.util.Constantes;

public class EnviarActividadExternaC {
	List<ActividadExterna> listaActividadExterna;
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
	
	@Command
	public void nuevaActividad() {
		Window ventanaCargar = (Window) Executions.createComponents("/formularios/actividades/enviar-actividad-varios-funcionarios/nuevaActividadExterna.zul", null, null);
		ventanaCargar.doModal();
	}
	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Command
	public void eliminarActividad() {
		if(actividadExternaSeleccionado == null) {
			Clients.showNotification("Debe seleccionar actividad");
			return;
		}
		Messagebox.show("Desea eliminar el registro seleccionado?", "Confirmación de Eliminación", Messagebox.YES | Messagebox.NO, Messagebox.QUESTION, new EventListener() {	
			@Override
			public void onEvent(Event event) throws Exception {
				if (event.getName().equals("onYes")) {
					try {
						actividadExternaSeleccionado.setEstado(Constantes.ESTADO_INACTIVO);
						actividadExternaDAO.getEntityManager().getTransaction().begin();
						actividadExternaDAO.getEntityManager().merge(actividadExternaSeleccionado);
						actividadExternaDAO.getEntityManager().getTransaction().commit();
						Messagebox.show("Transaccion ejecutada con exito");
						BindUtils.postGlobalCommand(null, null, "ActividadExterna.buscarActivas", null);
					} catch (Exception e) {
						e.printStackTrace();
						actividadExternaDAO.getEntityManager().getTransaction().rollback();
					}
				}
			}
		});	
	}
	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Command
	public void enviarActividad() {
		if(actividadExternaSeleccionado == null) {
			Clients.showNotification("Debe seleccionar actividad");
			return;
		}
		Messagebox.show("Enviar actividad?", "Confirmación", Messagebox.YES | Messagebox.NO, Messagebox.QUESTION, new EventListener() {	
			@Override
			public void onEvent(Event event) throws Exception {
				if (event.getName().equals("onYes")) {
					try {
						actividadExternaSeleccionado.setEstadoActividad(Constantes.ESTADO_PENDIENTE_ASIGNACION);
						actividadExternaDAO.getEntityManager().getTransaction().begin();
						actividadExternaDAO.getEntityManager().merge(actividadExternaSeleccionado);
						actividadExternaDAO.getEntityManager().getTransaction().commit();
						Messagebox.show("Transaccion ejecutada con exito");
						BindUtils.postGlobalCommand(null, null, "ActividadExterna.buscarActivas", null);
					} catch (Exception e) {
						e.printStackTrace();
						actividadExternaDAO.getEntityManager().getTransaction().rollback();
					}
				}
			}
		});	
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