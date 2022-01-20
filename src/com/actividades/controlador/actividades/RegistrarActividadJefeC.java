package com.actividades.controlador.actividades;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zk.ui.util.Clients;
import org.zkoss.zul.ListModelList;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Messagebox;
import org.zkoss.zul.Window;

import com.actividades.modelo.Actividad;
import com.actividades.modelo.ActividadDAO;
import com.actividades.modelo.Agenda;
import com.actividades.modelo.AgendaDAO;
import com.actividades.modelo.Empleado;
import com.actividades.util.Constantes;

public class RegistrarActividadJefeC {
	@Wire Window winRegistrarActividadJefe;
	@Wire private Listbox lstActividades;
	List<Actividad> listaActividad;
	Actividad actividadSeleccionada;
	ActividadDAO actividadDAO = new ActividadDAO();
	AgendaDAO agendaDAO = new AgendaDAO();
	Empleado empleado;
	
	@AfterCompose
	public void afterCompose(@ContextParam(ContextType.VIEW) Component view) {
		Selectors.wireComponents(view, this, false);
		empleado = (Empleado) Executions.getCurrent().getArg().get("Empleado");
		this.cargarActividades();
	}
	@SuppressWarnings({ "unchecked", "rawtypes" })
	@GlobalCommand("Actividad.buscarPorAgendaNoAsignado")
	@Command
	@NotifyChange({"listaActividad"})
	public void cargarActividades() {
		if(empleado == null) {
			Clients.showNotification("No se ha seleccionado empleado");
			return;
		}
		List<Agenda> listaAgenda = this.agendaDAO.obtenerAgendaActiva(empleado.getIdEmpleado(), Constantes.TIPO_AGENDA_PRINCIPALES);
		if(listaAgenda.size() == 0) {
			Clients.showNotification("Empleado no tiene agendas, no se puede asignar tareas");
			return;
		}
		if(listaActividad != null)
			listaActividad = null;
		listaActividad = actividadDAO.obtenerActividadNoAsignado(listaAgenda.get(0).getIdAgenda(),Constantes.ID_TIPO_PRIMORDIALES);
		lstActividades.setModel(new ListModelList(listaActividad));
	}
	@Command
	public void nuevaActividad() {
		//traer la ultima agenda del empleado
		if(empleado == null) {
			Clients.showNotification("No se ha seleccionado empleado");
			return;
		}
		List<Agenda> listaAgenda = this.agendaDAO.obtenerAgendaActiva(empleado.getIdEmpleado(), Constantes.TIPO_AGENDA_PRINCIPALES);
		if(listaAgenda.size() == 0) {
			Clients.showNotification("Empleado no tiene agendas, no se puede asignar tareas");
			return;
		}
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("Actividad", null);
		params.put("TipoActividad", "PRINCIPAL");
		params.put("Agenda", listaAgenda.get(0));
		Window ventanaCargar = (Window) Executions.createComponents("/formularios/actividades/enviar-actividades/registrarNuevaActividad.zul", null, params);
		ventanaCargar.doModal();
	}
	@Command
	public void enviarActividad() {
		if(listaActividad == null) {
			Clients.showNotification("No hay lista");
			return;
		}
		if(listaActividad.size() == 0) {
			Clients.showNotification("No hay lista");
			return;
		}
		actividadDAO.getEntityManager().getTransaction().begin();
		for(Actividad ac : listaActividad) {
			ac.setEstadoActividad(Constantes.ESTADO_PENDIENTE);
			actividadDAO.getEntityManager().merge(ac);
		}
		actividadDAO.getEntityManager().getTransaction().commit();
		salir();
	}
	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Command
	public void eliminarActividad() {
		if(actividadSeleccionada == null) {
			Clients.showNotification("Debe seleccionar actividad");
			return;
		}
		Messagebox.show("Desea eliminar el registro seleccionado?", "Confirmación de Eliminación", Messagebox.YES | Messagebox.NO, Messagebox.QUESTION, new EventListener() {	
			@Override
			public void onEvent(Event event) throws Exception {
				if (event.getName().equals("onYes")) {
					try {
						actividadSeleccionada.setEstado(Constantes.ESTADO_INACTIVO);
						actividadDAO.getEntityManager().getTransaction().begin();
						actividadDAO.getEntityManager().merge(actividadSeleccionada);
						actividadDAO.getEntityManager().getTransaction().commit();
						Messagebox.show("Transaccion ejecutada con exito");
						BindUtils.postGlobalCommand(null, null, "Actividad.buscarPorAgendaNoAsignado", null);
					} catch (Exception e) {
						e.printStackTrace();
						actividadDAO.getEntityManager().getTransaction().rollback();
					}
				}
			}
		});	
	}
	@Command
	public void salir(){
		BindUtils.postGlobalCommand(null, null, "Empleado.buscarPorCargoPatronBusqueda", null);
		winRegistrarActividadJefe.detach();
	}
	public List<Actividad> getListaActividad() {
		return listaActividad;
	}
	public void setListaActividad(List<Actividad> listaActividad) {
		this.listaActividad = listaActividad;
	}
	public Actividad getActividadSeleccionada() {
		return actividadSeleccionada;
	}
	public void setActividadSeleccionada(Actividad actividadSeleccionada) {
		this.actividadSeleccionada = actividadSeleccionada;
	}
}
