package com.actividades.controlador.actividades;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.zkoss.bind.BindUtils;
import org.zkoss.bind.annotation.AfterCompose;
import org.zkoss.bind.annotation.Command;
import org.zkoss.bind.annotation.ContextParam;
import org.zkoss.bind.annotation.ContextType;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.select.Selectors;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zk.ui.util.Clients;
import org.zkoss.zul.Datebox;
import org.zkoss.zul.Messagebox;
import org.zkoss.zul.Messagebox.ClickEvent;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

import com.actividades.modelo.ActividadDAO;
import com.actividades.modelo.Agenda;
import com.actividades.modelo.AgendaDAO;
import com.actividades.modelo.Empleado;
import com.actividades.modelo.EmpleadoDAO;
import com.actividades.util.Constantes;
import com.actividades.util.SecurityUtil;

public class NuevaAgendaC {
	@Wire private Window winAgendaEditar;
	@Wire private Textbox txtDescripcion;
	@Wire private Datebox dtpFechaInicio;
	@Wire private Datebox dtpFechaFin;
	
	private Agenda agenda;
	private AgendaDAO agendaDAO = new AgendaDAO();
	EmpleadoDAO usuarioDAO = new EmpleadoDAO();
	ActividadDAO actividadDAO = new ActividadDAO();
	String tipoAgenda;
	
	@AfterCompose
	public void afterCompose(@ContextParam(ContextType.VIEW) Component view) {
		Selectors.wireComponents(view, this, false);
		tipoAgenda = (String) Executions.getCurrent().getArg().get("tipoAgenda");
		if(Executions.getCurrent().getArg().get("Agenda") != null) {
			agenda = (Agenda) Executions.getCurrent().getArg().get("Agenda");
			txtDescripcion.setText(agenda.getDescripcion());
			dtpFechaInicio.setValue(agenda.getFechaInicio());
			dtpFechaFin.setValue(agenda.getFechaFin());
			dtpFechaInicio.setDisabled(true);
			dtpFechaFin.setDisabled(true);
			//validarActividades();
		}else {
			agenda = new Agenda();
			//dtpFechaInicio.setValue(new Date());
			dtpFechaFin.setConstraint("after " + new SimpleDateFormat("yyyyMMdd").format(new Date()));
			dtpFechaFin.setDisabled(true);
			validarFechasAgenda();
		}
	}
	private void validarFechasAgenda(){
		//tambien validar la fecha de ingreso del jefe, y la fecha de la ultima agenda para no repetir fechas
		//primero validar la ultima agenda
		Empleado usuario = usuarioDAO.getUsuario(SecurityUtil.getUser().getUsername().trim());
		List<Agenda> listaAgenda = agendaDAO.obtenerUltimaAgenda(usuario.getIdEmpleado(), tipoAgenda);
		if(listaAgenda.size() > 0) {
			Date dt = listaAgenda.get(0).getFechaFin();
	        Calendar c = Calendar.getInstance();
	        c.setTime(dt);
	        c.add(Calendar.DATE, 1);
	        dt = c.getTime();	        
			dtpFechaInicio.setConstraint("after " + new SimpleDateFormat("yyyyMMdd").format(dt));
		}else {//caso contrario se verifica por el dia de ingreso del empleado
			dtpFechaInicio.setConstraint("after " + new SimpleDateFormat("yyyyMMdd").format(usuario.getFechaIngreso()));
		}
	}
	//metodo que me valida si la agenda ya tiene actividades registradas
	/*private void validarActividades() {
		List<Actividad> listaActividad = actividadDAO.obtenerCodigoActividad(agenda.getIdAgenda());
		if(listaActividad.size() > 0) {
			dtpFechaInicio.setDisabled(true);
			dtpFechaFin.setDisabled(true);
		}else {
			dtpFechaInicio.setDisabled(false);
			dtpFechaFin.setDisabled(false);
		}
	}*/
	@Command
	public void grabar(){
		if(validarDatos() == true) {
			EventListener<ClickEvent> clickListener = new EventListener<Messagebox.ClickEvent>() {
				public void onEvent(ClickEvent event) throws Exception {
					if(Messagebox.Button.YES.equals(event.getButton())) {
						
						agenda.setDescripcion(txtDescripcion.getText().toString());
						agenda.setEstado(Constantes.ESTADO_ACTIVO);
						agenda.setFechaInicio(dtpFechaInicio.getValue());
						agenda.setFechaFin(dtpFechaFin.getValue());
						agenda.setTipoAgenda(tipoAgenda);
						
						//hay q preguntar por si es un usuario privilegiado
						Empleado usuario = usuarioDAO.getUsuario(SecurityUtil.getUser().getUsername().trim());
						
						if(!usuario.getTipoUsuario().getIdTipoUsuario().equals(Constantes.ID_JEFE_AREA)){
							if(usuario.getPermiso() != null) {
								if(usuario.getPermiso().equals(Constantes.USUARIO_PERMITIDO)) {
									//hay q buscar el jefe de ese departamento
									List<Empleado> jefeArea = usuarioDAO.buscarPorDepartamento(usuario.getDepartamento().getIdDepartamento());
									if(jefeArea.size() > 0) {
										agenda.setEmpleado(jefeArea.get(0));
									}										
								}
							}
						}else {
							agenda.setEmpleado(usuario);								
						}
						if(usuario.getTipoUsuario().getIdTipoUsuario().equals(Constantes.ID_AUTORIDAD_MAXIMA)) {
							agenda.setEmpleado(usuario);
						}
						codigoAgenda();
						
						agendaDAO.getEntityManager().getTransaction().begin();
						if(agenda.getIdAgenda() == null) {//es un nuevo
							agendaDAO.getEntityManager().persist(agenda);
						}else {//es modificacion
							agendaDAO.getEntityManager().merge(agenda);
						}
						agendaDAO.getEntityManager().getTransaction().commit();
						Messagebox.show("Datos grabados con exito");
						//aDiaria.cargarAgendas();
						BindUtils.postGlobalCommand(null, null, "Agenda.buscarActivos", null);
						salir();
					}
				}
			};
			Messagebox.show("¿Desea Grabar los Datos?", "Confirmación", new Messagebox.Button[]{
					Messagebox.Button.YES, Messagebox.Button.NO }, Messagebox.QUESTION, clickListener);
		}
	}
	
	private void codigoAgenda() {
		if(agenda.getIdAgenda() == null) {
			String codigo = Constantes.CODIGO_AGENDA;
			Integer ultimoCodigo = 1;
			List<Agenda> agendas = new ArrayList<>();
			Empleado usuario = usuarioDAO.getUsuario(SecurityUtil.getUser().getUsername().trim());
			if(!usuario.getTipoUsuario().getIdTipoUsuario().equals(Constantes.ID_JEFE_AREA)){
				if(usuario.getPermiso() != null) {
					if(usuario.getPermiso().equals(Constantes.USUARIO_PERMITIDO)) {
						//hay q buscar el jefe de ese departamento
						List<Empleado> jefeArea = usuarioDAO.buscarPorDepartamento(usuario.getDepartamento().getIdDepartamento());
						if(jefeArea.size() > 0) {
							agendas = agendaDAO.obtenerCodigoAgendaActiva(jefeArea.get(0).getIdEmpleado());
						}	
					}
				}
			}else {
				agendas = agendaDAO.obtenerCodigoAgendaActiva(usuario.getIdEmpleado());
			}
			
			if(agendas.size() > 0) {
				ultimoCodigo = agendas.get(0).getSecuencia() + 1;
				codigo = codigo + ultimoCodigo + "-" + usuario.getDepartamento().getCodigo(); 
			}else { //es la primera
				codigo = codigo + "1-" + usuario.getDepartamento().getCodigo();
			}
			agenda.setCodigo(codigo);
			agenda.setSecuencia(ultimoCodigo);
		}
	}
	
	@Command
	public void cambioFecha() {
		try {
			System.out.println("cambio de fecha");
			dtpFechaFin.setDisabled(false);
			
			dtpFechaFin.setConstraint("after " + new SimpleDateFormat("yyyyMMdd").format(dtpFechaInicio.getValue()));
			dtpFechaFin.setOpen(true);
		}catch(Exception ex) {
			System.out.println(ex.getMessage());
		}
	}
	private boolean validarDatos() {
		try {
			if(txtDescripcion.getText().isEmpty()) {
				Clients.showNotification("Debe registrar la actividad","info",txtDescripcion,"end_center",2000);
				txtDescripcion.focus();
				return false;
			}
			if(dtpFechaFin.getValue() == null) {
				Clients.showNotification("Debe registrar fecha fin","info",dtpFechaFin,"end_center",2000);
				return false;
			}
			return true;
		}catch(Exception ex) {
			return false;
		}
	}
	@Command
	public void salir(){
		winAgendaEditar.detach();
	}
	public Agenda getAgenda() {
		return agenda;
	}
	public void setAgenda(Agenda agenda) {
		this.agenda = agenda;
	}
	public String getTipoAgenda() {
		return tipoAgenda;
	}
	public void setTipoAgenda(String tipoAgenda) {
		this.tipoAgenda = tipoAgenda;
	}
}
