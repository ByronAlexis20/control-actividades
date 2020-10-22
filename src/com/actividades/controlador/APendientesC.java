package com.actividades.controlador;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.zkoss.bind.BindUtils;
import org.zkoss.bind.annotation.AfterCompose;
import org.zkoss.bind.annotation.BindingParam;
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
import org.zkoss.zul.Button;
import org.zkoss.zul.ListModelList;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Messagebox;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;
import org.zkoss.zul.Messagebox.ClickEvent;

import com.actividades.modelo.Actividad;
import com.actividades.modelo.ActividadDAO;
import com.actividades.modelo.Agenda;
import com.actividades.modelo.AgendaDAO;
import com.actividades.modelo.Empleado;
import com.actividades.modelo.EmpleadoDAO;
import com.actividades.util.Constantes;
import com.actividades.util.SecurityUtil;

public class APendientesC {
	@Wire private Window winActividades;
	@Wire private Listbox lstActividades;
	@Wire private Listbox lstAgenda;
	@Wire private Textbox txtAgendaSeleccionada;
	@Wire private Textbox txtFechaInicio;
	@Wire private Textbox txtFechaFin;
	@Wire private Button btnPublicar;


	private Agenda agendaSeleccionada;
	private List<Agenda> listaAgenda;
	private List<Actividad> listaActividad;
	private AgendaDAO agendaDAO = new AgendaDAO();
	private ActividadDAO actividadDAO = new ActividadDAO();
	private Actividad actividadSeleccionada;

	private EmpleadoDAO usuarioDAO = new EmpleadoDAO();
	@AfterCompose
	public void aferCompose(@ContextParam(ContextType.VIEW) Component view) throws IOException{
		Selectors.wireComponents(view, this, false);
		listaAgenda = new ArrayList<>();
		listaActividad = new ArrayList<>();

		cargarAgendas();
		deshabilitarCampos();
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	private void deshabilitarCampos() {
		txtAgendaSeleccionada.setText("");
		txtFechaFin.setText("");
		txtFechaInicio.setText("");
		
		listaActividad = new ArrayList<>();
		lstActividades.setModel(new ListModelList(listaActividad));
	}

	private void habilitarCampos() {
		txtAgendaSeleccionada.setText("");
		txtFechaFin.setText("");
		txtFechaInicio.setText("");
	}

	@Command
	public void seleccionarAgenda() {
		try {
			if(agendaSeleccionada == null) {
				Messagebox.show("Debe seleccionar una agenda");
				return;
			}
			habilitarCampos();
			txtAgendaSeleccionada.setText(agendaSeleccionada.getDescripcion());
			txtFechaInicio.setText(new SimpleDateFormat("dd/MM/yyyy").format(agendaSeleccionada.getFechaInicio()));
			txtFechaFin.setText(new SimpleDateFormat("dd/MM/yyyy").format(agendaSeleccionada.getFechaFin()));
			BindUtils.postGlobalCommand(null, null, "Actividad.buscarPorAgenda", null);
		}catch(Exception ex) {
			System.out.println(ex.getMessage());
		}
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@GlobalCommand("Actividad.buscarPorAgenda")
	@Command
	@NotifyChange({"listaActividad"})
	public void cargarActividades() {
		if(listaActividad != null)
			listaActividad = null;
		
		List<Actividad> lista = new ArrayList<>();
		List<Actividad> resultado = actividadDAO.obtenerActividad(agendaSeleccionada.getIdAgenda(),Constantes.ID_TIPO_PRIMORDIALES);
		
		List<String> estados = new ArrayList<>();
		estados.add(Constantes.ESTADO_PENDIENTE);
		for(String est : estados) {
			for(Actividad act : resultado) {
				if(est.equals(act.getEstadoActividad())) {
					lista.add(act);
				}
			}
		}
		listaActividad = lista;
		lstActividades.setModel(new ListModelList(listaActividad));
	}


	@SuppressWarnings({ "unchecked", "rawtypes" })
	@GlobalCommand("Agenda.buscarActivos")
	@Command
	@NotifyChange({"listaAgenda"})
	public void cargarAgendas(){
		if (listaAgenda != null)
			listaAgenda = null; 
		Empleado usuario = usuarioDAO.getUsuario(SecurityUtil.getUser().getUsername().trim());
		listaAgenda = agendaDAO.obtenerAgendaActiva(usuario.getIdEmpleado());
		lstAgenda.setModel(new ListModelList(listaAgenda));
		deshabilitarCampos();
		agendaSeleccionada = null;	

	}
	
	@Command
	public void editarActividad() {
		if (actividadSeleccionada == null) {
			Messagebox.show("Debe seleccionar una Actividad");
			return; 
		}
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("Ventana", this);
		params.put("Actividad", actividadSeleccionada);
		params.put("Actividad", "PRINCIPAL");
		params.put("Agenda", agendaSeleccionada);
		Window ventanaCargar = (Window) Executions.createComponents("/formularios/actividades/diaria/ANuevaActividad.zul", winActividades, params);
		ventanaCargar.doModal();
	}
	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Command
	public void eliminarActividad() {
		if (actividadSeleccionada == null) {
			Messagebox.show("Debe seleccionar una Actividad");
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
						BindUtils.postGlobalCommand(null, null, "Actividad.buscarPorAgenda", null);
					} catch (Exception e) {
						e.printStackTrace();
						actividadDAO.getEntityManager().getTransaction().rollback();
					}
				}
			}
		});	
	}

	//evidencias
	@Command
	public void evidencias(@BindingParam("actividad") Actividad seleccion){
		if(seleccion == null) {
			Messagebox.show("Seleccione una opción de la lista.");
			return;
		}
		// Actualiza la instancia antes de enviarla a editar.
		actividadDAO.getEntityManager().refresh(seleccion);		
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("Actividad", seleccion);
		Window ventanaCargar = (Window) Executions.createComponents("/formularios/actividades/diaria/AEvidenciaLista.zul", null, params);
		ventanaCargar.doModal();
	}

	@Command
	public void publicar(@BindingParam("actividad") Actividad seleccion) {
		if(seleccion == null) {
			Messagebox.show("Seleccione una opción de la lista.");
			return;
		}
		EventListener<ClickEvent> clickListener = new EventListener<Messagebox.ClickEvent>() {
			public void onEvent(ClickEvent event) throws Exception {
				if(Messagebox.Button.YES.equals(event.getButton())) {
					seleccion.setEstadoPublicado(Constantes.ESTADO_PUBLICADO);
					seleccion.setEstadoActividad(Constantes.ESTADO_REALIZADO);
					agendaDAO.getEntityManager().getTransaction().begin();
					agendaDAO.getEntityManager().merge(seleccion);
					agendaDAO.getEntityManager().getTransaction().commit();
					Messagebox.show("Datos grabados con exito");
					cargarActividades();
				}
			}
		};
		Messagebox.show("¿Desea publicar la actividad?", "Confirmación", new Messagebox.Button[]{
				Messagebox.Button.YES, Messagebox.Button.NO }, Messagebox.QUESTION, clickListener);

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
