package com.actividades.controlador;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.select.SelectorComposer;
import org.zkoss.zk.ui.select.annotation.Listen;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zul.ListModelList;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Messagebox;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

import com.actividades.modelo.Actividad;
import com.actividades.modelo.ActividadDAO;
import com.actividades.modelo.Agenda;
import com.actividades.modelo.AgendaDAO;
import com.actividades.util.Constantes;

@SuppressWarnings("serial")
public class ADiariaC extends SelectorComposer<Component>{
	@Wire private Window winActividades;
	@Wire private Listbox lstActividades;
	@Wire private Listbox lstAgenda;
	@Wire private Textbox txtAgendaSeleccionada;
	@Wire private Textbox txtFechaInicio;
	@Wire private Textbox txtFechaFin;
	
	private Agenda agendaSeleccionada;
	private Actividad actividadSeleccionado;
	private List<Agenda> listaAgenda;
	private AgendaDAO agendaDAO = new AgendaDAO();
	private ActividadDAO actividadDAO = new ActividadDAO();
	@Override
	public void doAfterCompose(Component comp) throws Exception {
		// TODO Auto-generated method stub
		super.doAfterCompose(comp);
		listaAgenda = new ArrayList<>();
		cargarAgendas();
	}
	@Listen("onClick=#btnNuevoAgenda")
	public void nuevaAgenda() {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("Ventana", this);
		params.put("Agenda", null);
		Window ventanaCargar = (Window) Executions.createComponents("/formularios/actividades/diaria/ANuevaAgenda.zul", winActividades, params);
		ventanaCargar.doModal();
	}
	@Listen("onClick=#btnEditarAgenda")
	public void editarAgenda() {
		if (agendaSeleccionada == null) {
			Messagebox.show("Debe seleccionar una agenda");
			return; 
		}
		// Actualiza la instancia antes de enviarla a editar.
		agendaDAO.getEntityManager().refresh(agendaSeleccionada);
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("Ventana", this);
		params.put("Agenda", agendaSeleccionada);
		Window ventanaCargar = (Window) Executions.createComponents("/formularios/actividades/diaria/ANuevaAgenda.zul", winActividades, params);
		ventanaCargar.doModal();
	}
	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Listen("onClick=#btnEliminarAgenda")
	public void eliminarAgenda() {
		if (agendaSeleccionada == null) {
			Messagebox.show("Debe seleccionar una agenda");
			return; 
		}
		Messagebox.show("Desea eliminar el registro seleccionado?", "Confirmación de Eliminación", Messagebox.YES | Messagebox.NO, Messagebox.QUESTION, new EventListener() {	
			@Override
			public void onEvent(Event event) throws Exception {
				if (event.getName().equals("onYes")) {
					try {
						agendaSeleccionada.setEstado(Constantes.ESTADO_INACTIVO);
						agendaDAO.getEntityManager().getTransaction().begin();
						agendaDAO.getEntityManager().merge(agendaSeleccionada);
						agendaDAO.getEntityManager().getTransaction().commit();
						Messagebox.show("Transaccion ejecutada con exito");
						cargarAgendas();
					} catch (Exception e) {
						e.printStackTrace();
						agendaDAO.getEntityManager().getTransaction().rollback();
					}
				}
			}
		});	
		
	}
	@Listen("onSelect=#lstAgenda")
	public void seleccionarAgenda() {
		try {
			if(agendaSeleccionada == null) {
				Messagebox.show("Debe seleccionar una agenda");
				return;
			}
			txtAgendaSeleccionada.setText(agendaSeleccionada.getDescripcion());
			txtFechaInicio.setText(new SimpleDateFormat("dd/MM/yyyy").format(agendaSeleccionada.getFechaInicio()));
			txtFechaFin.setText(new SimpleDateFormat("dd/MM/yyyy").format(agendaSeleccionada.getFechaFin()));
			cargarActividades(agendaSeleccionada);
		}catch(Exception ex) {
			System.out.println(ex.getMessage());
		}
	}
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	private void cargarActividades(Agenda agenda) {
		try {
			List<Actividad> lista = actividadDAO.obtenerActividad(agenda.getIdAgenda());
			lstActividades.setModel(new ListModelList(lista));
			actividadSeleccionado = null;
		}catch(Exception ex) {
			System.out.println(ex.getMessage());
		}
	}
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public void cargarAgendas() {
		try {
			if (listaAgenda != null)
				listaAgenda = null; 
			listaAgenda = agendaDAO.obtenerAgendaActiva();
			lstAgenda.setModel(new ListModelList(listaAgenda));
			agendaSeleccionada = null;	
		}catch(Exception ex) {
			System.out.println(ex.getMessage());
		}
	}
	@Listen("onClick=#btnNuevaActividad")
	public void nuevaActividad() {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("Ventana", this);
		params.put("Actividad", null);
		Window ventanaCargar = (Window) Executions.createComponents("/formularios/actividades/diaria/ANuevaActividad.zul", winActividades, params);
		ventanaCargar.doModal();
	}
	public Agenda getAgendaSeleccionada() {
		return agendaSeleccionada;
	}
	public void setAgendaSeleccionada(Agenda agendaSeleccionada) {
		this.agendaSeleccionada = agendaSeleccionada;
	}
	public List<Agenda> getListaAgenda() {
		return listaAgenda;
	}
	public void setListaAgenda(List<Agenda> listaAgenda) {
		this.listaAgenda = listaAgenda;
	}
	public Actividad getActividadSeleccionado() {
		return actividadSeleccionado;
	}
	public void setActividadSeleccionado(Actividad actividadSeleccionado) {
		this.actividadSeleccionado = actividadSeleccionado;
	}
	
}
