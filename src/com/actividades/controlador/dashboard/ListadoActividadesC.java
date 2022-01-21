package com.actividades.controlador.dashboard;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.zkoss.bind.annotation.AfterCompose;
import org.zkoss.bind.annotation.BindingParam;
import org.zkoss.bind.annotation.Command;
import org.zkoss.bind.annotation.ContextParam;
import org.zkoss.bind.annotation.ContextType;
import org.zkoss.bind.annotation.GlobalCommand;
import org.zkoss.bind.annotation.NotifyChange;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.select.Selectors;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zk.ui.util.Clients;
import org.zkoss.zul.ListModelList;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Window;

import com.actividades.modelo.Actividad;
import com.actividades.modelo.ActividadDAO;
import com.actividades.modelo.Empleado;
import com.actividades.util.Constantes;

public class ListadoActividadesC {
	@Wire Window winActividades;
	@Wire Listbox lstActividades;
	@Wire Listbox lstActividadesInternas;
	
	Empleado empleado;
	Date fechaInicio;
	Date fechaFin;
	
	List<Actividad> listaActividad;
	List<Actividad> listaActividadInterna;
	ActividadDAO actividadDAO = new ActividadDAO();
	
	@AfterCompose
	public void afterCompose(@ContextParam(ContextType.VIEW) Component view) {
		Selectors.wireComponents(view, this, false);
		empleado = (Empleado) Executions.getCurrent().getArg().get("Empleado");
		fechaInicio = (Date) Executions.getCurrent().getArg().get("FechaInicio");
		fechaFin = (Date) Executions.getCurrent().getArg().get("FechaFin");
		cargarActividades();
	}
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	@GlobalCommand({"Actividad.buscarPorEmpleadoTipoActividadFechas", "Actividad.buscarPorEmpleadoTipoActividadFechasInternas"})
	@Command
	@NotifyChange({"listaActividad", "listaActividadInterna"})
	public void cargarActividades() {
		if(listaActividad != null)
			listaActividad = null;
		
		listaActividad = actividadDAO.buscarPorEmpleadoTipoActividadFechas(empleado.getIdEmpleado() ,Constantes.ID_TIPO_PRIMORDIALES, fechaInicio, fechaFin);
		
		lstActividades.setModel(new ListModelList(listaActividad));
		
		//actividades internas
		if(listaActividadInterna != null)
			listaActividadInterna = null;
		
		listaActividadInterna = actividadDAO.buscarPorEmpleadoTipoActividadFechasInternas(empleado.getIdEmpleado() ,Constantes.ID_TIPO_INTERNAS, fechaInicio, fechaFin);
		
		lstActividadesInternas.setModel(new ListModelList(listaActividadInterna));
	}
	
	@Command
	public void verEvidencias(@BindingParam("actividad") Actividad seleccion){
		if(seleccion == null) {
			Clients.showNotification("Seleccione una opción de la lista.");
			return;
		}
		// Actualiza la instancia antes de enviarla a editar.
		actividadDAO.getEntityManager().refresh(seleccion);		
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("Actividad", seleccion);
		Window ventanaCargar = (Window) Executions.createComponents("/formularios/reportes/actividades/Evidencia.zul", null, params);
		ventanaCargar.doModal();
	}
	
	@Command
	public void verEvidenciasInternas(@BindingParam("actividad") Actividad seleccion){
		if(seleccion == null) {
			Clients.showNotification("Seleccione una opción de la lista.");
			return;
		}
		// Actualiza la instancia antes de enviarla a editar.
		actividadDAO.getEntityManager().refresh(seleccion);		
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("Actividad", seleccion);
		Window ventanaCargar = (Window) Executions.createComponents("/formularios/reportes/actividades/Evidencia.zul", null, params);
		ventanaCargar.doModal();
	}
	
	
	@Command
	public void actualizar() {
		this.cargarActividades();
	}
	@Command
	public void salir() {
		winActividades.detach();
	}

	public Empleado getEmpleado() {
		return empleado;
	}

	public void setEmpleado(Empleado empleado) {
		this.empleado = empleado;
	}

	public List<Actividad> getListaActividad() {
		return listaActividad;
	}

	public void setListaActividad(List<Actividad> listaActividad) {
		this.listaActividad = listaActividad;
	}

	public List<Actividad> getListaActividadInterna() {
		return listaActividadInterna;
	}

	public void setListaActividadInterna(List<Actividad> listaActividadInterna) {
		this.listaActividadInterna = listaActividadInterna;
	}
	
}
