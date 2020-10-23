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

public class AinternasC {
	@Wire private Window winActividades;
	@Wire private Listbox lstActividadesInternas;
	@Wire private Listbox lstAgenda;
	
	@Wire private Textbox txtAgendaSeleccionada;
	@Wire private Textbox txtFechaInicio;
	@Wire private Textbox txtFechaFin;
	
	@Wire private Button btnNuevaActividadInterna;
	@Wire private Button btnEditarActividadInterna;
	@Wire private Button btnEliminarActividadInterna;
	
	
	@Wire private Button btnPublicar;
	
	@Wire private Button btnNuevoAgenda;
	@Wire private Button btnEditarAgenda;
	@Wire private Button btnEliminarAgenda;


	private Agenda agendaSeleccionada;
	private List<Agenda> listaAgenda;
	private List<Actividad> listaActividadInterna;
	private AgendaDAO agendaDAO = new AgendaDAO();
	private ActividadDAO actividadDAO = new ActividadDAO();
	private Actividad actividadSeleccionada;
	private Actividad actividadSeleccionadaInterna;

	private EmpleadoDAO usuarioDAO = new EmpleadoDAO();
	@AfterCompose
	public void aferCompose(@ContextParam(ContextType.VIEW) Component view) throws IOException{
		Selectors.wireComponents(view, this, false);
		listaAgenda = new ArrayList<>();

		cargarAgendas();
		deshabilitarCampos();
	}

	@Command
	public void nuevaAgenda() {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("Ventana", this);
		params.put("Agenda", null);
		Window ventanaCargar = (Window) Executions.createComponents("/formularios/actividades/diaria/ANuevaAgenda.zul", winActividades, params);
		ventanaCargar.doModal();
	}

	@Command
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
	@Command
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
						deshabilitarCampos();


					} catch (Exception e) {
						e.printStackTrace();
						agendaDAO.getEntityManager().getTransaction().rollback();
					}
				}
			}
		});	

	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	private void deshabilitarCampos() {
		txtAgendaSeleccionada.setText("");
		txtFechaFin.setText("");
		txtFechaInicio.setText("");
		btnEditarActividadInterna.setDisabled(true);
		btnEliminarActividadInterna.setDisabled(true);
		btnNuevaActividadInterna.setDisabled(true);
		
		listaActividadInterna = new ArrayList<>();
		lstActividadesInternas.setModel(new ListModelList(listaActividadInterna));
	}

	private void habilitarCampos() {
		txtAgendaSeleccionada.setText("");
		txtFechaFin.setText("");
		txtFechaInicio.setText("");
		
		btnEditarActividadInterna.setDisabled(false);
		btnEliminarActividadInterna.setDisabled(false);
		btnNuevaActividadInterna.setDisabled(false);
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
	@NotifyChange({"listaActividadInterna"})
	public void cargarActividades() {
		if(listaActividadInterna != null)
			listaActividadInterna = null;
		
		
		
		List<String> estados = new ArrayList<>();
		estados.add(Constantes.ESTADO_NO_PUBLICADO);
		estados.add(Constantes.ESTADO_PUBLICADO);
		estados.add(Constantes.ESTADO_RECHAZADO);
		
		List<Actividad> listaInterna = new ArrayList<>();
		List<Actividad> resultadoInterno = actividadDAO.obtenerActividad(agendaSeleccionada.getIdAgenda(),Constantes.ID_TIPO_INTERNAS);
		
		for(String est : estados) {
			for(Actividad act : resultadoInterno) {
				if(est.equals(act.getEstadoPublicado())) {
					listaInterna.add(act);
				}
			}
		}
		listaActividadInterna = listaInterna;
		lstActividadesInternas.setModel(new ListModelList(listaActividadInterna));
		
	}


	@SuppressWarnings({ "unchecked", "rawtypes" })
	@GlobalCommand("Agenda.buscarActivos")
	@Command
	@NotifyChange({"listaAgenda"})
	public void cargarAgendas(){
		if (listaAgenda != null)
			listaAgenda = null; 
		listaAgenda = new ArrayList<>();
		Empleado usuario = usuarioDAO.getUsuario(SecurityUtil.getUser().getUsername().trim());
		if(!usuario.getTipoUsuario().getIdTipoUsuario().equals(Constantes.ID_JEFE_AREA)){
			if(usuario.getPermiso() != null) {
				if(usuario.getPermiso().equals(Constantes.USUARIO_PERMITIDO)) {
					//hay q buscar el jefe de ese departamento
					List<Empleado> jefeArea = usuarioDAO.buscarPorDepartamento(usuario.getDepartamento().getIdDepartamento());
					if(jefeArea.size() > 0) {
						listaAgenda = agendaDAO.obtenerAgendaActiva(jefeArea.get(0).getIdEmpleado());
					}	
				}else {
					listaAgenda = new ArrayList<>();
					btnNuevoAgenda.setDisabled(true);
					btnEditarAgenda.setDisabled(true);
					btnEliminarAgenda.setDisabled(true);
				}
			}else {
				listaAgenda = new ArrayList<>();
				btnNuevoAgenda.setDisabled(true);
				btnEditarAgenda.setDisabled(true);
				btnEliminarAgenda.setDisabled(true);
			}
			
		}else {
			listaAgenda = agendaDAO.obtenerAgendaActiva(usuario.getIdEmpleado());
		}
		
		lstAgenda.setModel(new ListModelList(listaAgenda));
		deshabilitarCampos();
		agendaSeleccionada = null;	

	}



	@Command
	public void nuevaActividad() {
		if (agendaSeleccionada == null) {
			Messagebox.show("Debe seleccionar una Agenda");
			return; 
		}
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("Ventana", this);
		params.put("Actividad", null);
		params.put("TipoActividad", "PRINCIPAL");
		params.put("Agenda", agendaSeleccionada);
		Window ventanaCargar = (Window) Executions.createComponents("/formularios/actividades/diaria/ANuevaActividad.zul", winActividades, params);
		ventanaCargar.doModal();
	}
	@Command
	public void editarActividad() {
		if (actividadSeleccionada == null) {
			Messagebox.show("Debe seleccionar una Actividad");
			return; 
		}
		if (actividadSeleccionada.getEstadoActividad().equals(Constantes.ESTADO_RECHAZADO)) {
			Messagebox.show("No se puede editar una Actividad RECHAZADA");
			return; 
		}
		if (actividadSeleccionada.getEstadoPublicado().equals(Constantes.ESTADO_PUBLICADO)) {
			Messagebox.show("No se puede editar una Actividad PUBLICADA");
			return; 
		}
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("Ventana", this);
		params.put("Actividad", actividadSeleccionada);
		params.put("TipoActividad", "PRINCIPAL");
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

	@Command
	public void nuevaActividadInterna() {
		if (agendaSeleccionada == null) {
			Messagebox.show("Debe seleccionar una Agenda");
			return; 
		}
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("Ventana", this);
		params.put("Actividad", null);
		params.put("TipoActividad", "INTERNA");
		params.put("Agenda", agendaSeleccionada);
		Window ventanaCargar = (Window) Executions.createComponents("/formularios/actividades/diaria/ANuevaActividad.zul", winActividades, params);
		ventanaCargar.doModal();
	}
	@Command
	public void editarActividadInterna() {
		if (actividadSeleccionadaInterna == null) {
			Messagebox.show("Debe seleccionar una Actividad");
			return; 
		}
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("Ventana", this);
		params.put("Actividad", actividadSeleccionadaInterna);
		params.put("TipoActividad", "INTERNA");
		params.put("Agenda", agendaSeleccionada);
		Window ventanaCargar = (Window) Executions.createComponents("/formularios/actividades/diaria/ANuevaActividad.zul", winActividades, params);
		ventanaCargar.doModal();
	}
	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Command
	public void eliminarActividadInterna() {
		if (actividadSeleccionadaInterna == null) {
			Messagebox.show("Debe seleccionar una Actividad");
			return; 
		}
		Messagebox.show("Desea eliminar el registro seleccionado?", "Confirmación de Eliminación", Messagebox.YES | Messagebox.NO, Messagebox.QUESTION, new EventListener() {	
			@Override
			public void onEvent(Event event) throws Exception {
				if (event.getName().equals("onYes")) {
					try {
						actividadSeleccionadaInterna.setEstado(Constantes.ESTADO_INACTIVO);
						actividadDAO.getEntityManager().getTransaction().begin();
						actividadDAO.getEntityManager().merge(actividadSeleccionadaInterna);
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
		
		if(seleccion.getEstadoPublicado().equals(Constantes.ESTADO_PUBLICADO)) {
			Messagebox.show("La Actividad ya ha sido publicada");
			return;
		}
		
		if(!seleccion.getEstadoActividad().equals(Constantes.ESTADO_REALIZADO)) {
			Messagebox.show("La Actividad debe de tener estado REALIZADO para poder publicarla");
			return;
		}
		if(seleccion.getEstadoActividad().equals(Constantes.ESTADO_RECHAZADO)) {
			Messagebox.show("La ha sido RECHAZADA");
			return;
		}
		EventListener<ClickEvent> clickListener = new EventListener<Messagebox.ClickEvent>() {
			public void onEvent(ClickEvent event) throws Exception {
				if(Messagebox.Button.YES.equals(event.getButton())) {
					seleccion.setEstadoPublicado(Constantes.ESTADO_PUBLICADO);
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

	public Actividad getActividadSeleccionada() {
		return actividadSeleccionada;
	}
	public void setActividadSeleccionada(Actividad actividadSeleccionada) {
		this.actividadSeleccionada = actividadSeleccionada;
	}
	public List<Actividad> getListaActividadInterna() {
		return listaActividadInterna;
	}
	public void setListaActividadInterna(List<Actividad> listaActividadInterna) {
		this.listaActividadInterna = listaActividadInterna;
	}
	public Actividad getActividadSeleccionadaInterna() {
		return actividadSeleccionadaInterna;
	}
	public void setActividadSeleccionadaInterna(Actividad actividadSeleccionadaInterna) {
		this.actividadSeleccionadaInterna = actividadSeleccionadaInterna;
	}
}
