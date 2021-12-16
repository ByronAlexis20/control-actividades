package com.actividades.controlador.dashboard;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.zkoss.bind.annotation.AfterCompose;
import org.zkoss.bind.annotation.Command;
import org.zkoss.bind.annotation.ContextParam;
import org.zkoss.bind.annotation.ContextType;
import org.zkoss.bind.annotation.GlobalCommand;
import org.zkoss.bind.annotation.NotifyChange;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.select.Selectors;
import org.zkoss.zk.ui.select.annotation.Wire;
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
	
	Empleado empleado;
	List<Actividad> listaActividad;
	ActividadDAO actividadDAO = new ActividadDAO();
	
	@AfterCompose
	public void afterCompose(@ContextParam(ContextType.VIEW) Component view) {
		Selectors.wireComponents(view, this, false);
		empleado = (Empleado) Executions.getCurrent().getArg().get("Empleado");
		cargarActividades();
	}
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	@GlobalCommand("Actividad.buscarPorAgenda")
	@Command
	@NotifyChange({"listaActividad"})
	public void cargarActividades() {
		if(listaActividad != null)
			listaActividad = null;
		List<String> estados = new ArrayList<>();
		estados.add(Constantes.ESTADO_NO_PUBLICADO);
		estados.add(Constantes.ESTADO_PUBLICADO);
		estados.add(Constantes.ESTADO_RECHAZADO);
		
		List<Actividad> lista = new ArrayList<>();
		List<Actividad> resultado = actividadDAO.buscarPorFechaEmpleado(new Date(),empleado.getIdEmpleado() ,Constantes.ID_TIPO_PRIMORDIALES);
		for(String est : estados) {
			for(Actividad act : resultado) {
				if(est.equals(act.getEstadoPublicado())) {
					lista.add(act);
				}
			}
		}
		listaActividad = lista;
		lstActividades.setModel(new ListModelList(listaActividad));
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
	
}