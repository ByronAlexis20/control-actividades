package com.actividades.controlador.actividades;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
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
import org.zkoss.zk.ui.util.Clients;
import org.zkoss.zul.Button;
import org.zkoss.zul.Datebox;
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

public class RechazadaC {
	@Wire private Window winActividades;
	@Wire private Listbox lstActividades;
	@Wire private Listbox lstAgenda;
	@Wire private Textbox txtAgendaSeleccionada;
	@Wire private Textbox txtFechaInicio;
	@Wire private Textbox txtFechaFin;
	@Wire private Button btnPublicar;
	@Wire private Button btnEditarActividad;
	@Wire private Button btnEliminarActividad;
	
	@Wire private Datebox dtpFechaInicio;
	@Wire private Datebox dtpFechaFin;

	private Agenda agendaSeleccionada;
	private List<Agenda> listaAgenda;
	private List<Actividad> listaActividad;
	private AgendaDAO agendaDAO = new AgendaDAO();
	private ActividadDAO actividadDAO = new ActividadDAO();
	private Actividad actividadSeleccionada;

	private EmpleadoDAO usuarioDAO = new EmpleadoDAO();
	
	SimpleDateFormat formatoFechaMonth = new SimpleDateFormat("MM");
	SimpleDateFormat formatoFechaYear = new SimpleDateFormat("yyyy");
	
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
		btnEliminarActividad.setDisabled(true);
		btnEditarActividad.setDisabled(true);
		
		listaActividad = new ArrayList<>();
		lstActividades.setModel(new ListModelList(listaActividad));
	}

	private void habilitarCampos() {
		txtAgendaSeleccionada.setText("");
		txtFechaFin.setText("");
		txtFechaInicio.setText("");
		btnEliminarActividad.setDisabled(false);
		btnEditarActividad.setDisabled(false);
	}

	@Command
	public void seleccionarAgenda() {
		try {
			if(agendaSeleccionada == null) {
				Messagebox.show("Debe seleccionar una agenda");
				return;
			}
			habilitarCampos();
			//verificar si se ha pasado el mes
			Integer mesAgenda = Integer.parseInt(formatoFechaMonth.format(agendaSeleccionada.getFechaFin()));
			Integer mesActual = Integer.parseInt(formatoFechaMonth.format(new Date()));
			Integer anioAgenda = Integer.parseInt(formatoFechaYear.format(agendaSeleccionada.getFechaFin()));
			Integer anioActual = Integer.parseInt(formatoFechaYear.format(new Date()));
			
			if(anioAgenda.equals(anioActual)) {
				if(mesAgenda < mesActual) {
					System.out.println("menor");
					deshabilitarCampos();
					Clients.showNotification("Ya no puede realizar modificaciones en las actividades");
				}
			}else {
				System.out.println("anios diferentes");
				deshabilitarCampos();
				Clients.showNotification("Ya no puede realizar modificaciones en las actividades");
			}
			txtAgendaSeleccionada.setText(agendaSeleccionada.getDescripcion());
			txtFechaInicio.setText(new SimpleDateFormat("dd/MM/yyyy").format(agendaSeleccionada.getFechaInicio()));
			txtFechaFin.setText(new SimpleDateFormat("dd/MM/yyyy").format(agendaSeleccionada.getFechaFin()));
			BindUtils.postGlobalCommand(null, null, "Actividad.buscarPorAgenda", null);
		}catch(Exception ex) {
			System.out.println(ex.getMessage());
		}
	}

	@GlobalCommand("Actividad.buscarPorAgenda")
	@Command
	@NotifyChange({"listaActividad"})
	public void cargarActividades() {
		if(listaActividad != null)
			listaActividad = null;
		
		List<Actividad> lista = new ArrayList<>();
		List<Actividad> resultado = actividadDAO.obtenerActividad(agendaSeleccionada.getIdAgenda(),Constantes.ID_TIPO_PRIMORDIALES);
		
		List<String> estados = new ArrayList<>();
		estados.add(Constantes.ESTADO_RECHAZADO);
		for(String est : estados) {
			for(Actividad act : resultado) {
				if(est.equals(act.getEstadoActividad())) {
					lista.add(act);
				}
			}
		}
		listaActividad = lista;
	}


	@SuppressWarnings({ "unchecked", "rawtypes" })
	@GlobalCommand("Agenda.buscarActivos")
	@Command
	@NotifyChange({"listaAgenda"})
	public void cargarAgendas(){
		if (listaAgenda != null)
			listaAgenda = null; 
		Empleado usuario = usuarioDAO.getUsuario(SecurityUtil.getUser().getUsername().trim());
		listaAgenda = agendaDAO.obtenerAgendaActiva(usuario.getIdEmpleado(), Constantes.TIPO_AGENDA_PRINCIPALES);
		lstAgenda.setModel(new ListModelList(listaAgenda));
		deshabilitarCampos();
		agendaSeleccionada = null;	

	}
	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Command
	@NotifyChange({"listaActividad", "listaAgenda"})
	public void buscarPorFiltroFechas() {
		if(dtpFechaInicio.getValue() == null) {
			Clients.showNotification("Debe seleccionar fecha Inicio");
			return;
		}
		if(dtpFechaFin.getValue() == null) {
			Clients.showNotification("Debe seleccionar fecha fin");
			return;
		}
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
						listaAgenda = agendaDAO.obtenerAgendaActivaYFechas(jefeArea.get(0).getIdEmpleado(),dtpFechaInicio.getValue(),dtpFechaFin.getValue());
					}	
				}else {
					listaAgenda = new ArrayList<>();
				}
			}else {
				listaAgenda = new ArrayList<>();
			}
			
		}else {
			listaAgenda = agendaDAO.obtenerAgendaActivaYFechas(usuario.getIdEmpleado(),dtpFechaInicio.getValue(),dtpFechaFin.getValue());
		}
		lstAgenda.setModel(new ListModelList(listaAgenda));
		deshabilitarCampos();
		agendaSeleccionada = null;	
		
		
		if(listaActividad != null)
			listaActividad = null;
		List<String> estados = new ArrayList<>();
		estados.add(Constantes.ESTADO_NO_PUBLICADO);
		estados.add(Constantes.ESTADO_PUBLICADO);
		estados.add(Constantes.ESTADO_RECHAZADO);
		
		List<Actividad> lista = new ArrayList<>();
		listaActividad = lista;
	}
	@Command
	public void editarActividad() {
		if (actividadSeleccionada == null) {
			Messagebox.show("Debe seleccionar una Actividad");
			return; 
		}
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("Actividad", actividadSeleccionada);
		params.put("TipoActividad", "PRINCIPAL");
		params.put("Agenda", agendaSeleccionada);
		Window ventanaCargar = (Window) Executions.createComponents("/formularios/actividades/diaria/NuevaActividad.zul", winActividades, params);
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
		Window ventanaCargar = (Window) Executions.createComponents("/formularios/actividades/diaria/EvidenciaLista.zul", null, params);
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
					BindUtils.postGlobalCommand(null, null, "Actividad.buscarPorAgenda", null);
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
