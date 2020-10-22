package com.actividades.controlador;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import org.zkoss.bind.BindUtils;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.select.SelectorComposer;
import org.zkoss.zk.ui.select.annotation.Listen;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zk.ui.util.Clients;
import org.zkoss.zul.Datebox;
import org.zkoss.zul.Messagebox;
import org.zkoss.zul.Messagebox.ClickEvent;
import org.zkoss.zul.Radio;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

import com.actividades.modelo.Actividad;
import com.actividades.modelo.ActividadDAO;
import com.actividades.modelo.Agenda;
import com.actividades.modelo.Empleado;
import com.actividades.modelo.EmpleadoDAO;
import com.actividades.modelo.TipoActividad;
import com.actividades.modelo.TipoActividadDAO;
import com.actividades.util.Constantes;
import com.actividades.util.SecurityUtil;

@SuppressWarnings("serial")
public class ANuevaActividadC extends SelectorComposer<Component>{
	@Wire private Window winActividadEditar;
	@Wire private Textbox txtDescripcion;
	@Wire private Datebox dtpFecha;
	@Wire private Radio rbPendiente;
	@Wire private Radio rbProceso;
	@Wire private Radio rbRealizado;
	
	@Wire private Radio rbPrincipal;
	@Wire private Radio rbInterna;
	
	
	private Agenda agenda;
	private Actividad actividad;
	ActividadDAO actividadDAO = new ActividadDAO();
	EmpleadoDAO usuarioDAO = new EmpleadoDAO();
	TipoActividadDAO tipoActividadDAO = new TipoActividadDAO();
	String tipoActividad = "";
	@Override
	public void doAfterCompose(Component comp) throws Exception {
		super.doAfterCompose(comp);
		agenda = (Agenda) Executions.getCurrent().getArg().get("Agenda");
		actividad = (Actividad) Executions.getCurrent().getArg().get("Actividad");
		tipoActividad = (String) Executions.getCurrent().getArg().get("TipoActividad");
		
		if(agenda == null) {
			agenda = new Agenda();
			agenda.setEstado("A");
		}else {
			//between 20071225 and 20071203
			dtpFecha.setConstraint("between " + new SimpleDateFormat("yyyyMMdd").format(agenda.getFechaInicio()) + " and " + new SimpleDateFormat("yyyyMMdd").format(agenda.getFechaFin()));
		}
		if(actividad == null) {
			actividad = new Actividad();
			actividad.setIdActividad(null);
			rbPendiente.setSelected(true);
			rbPrincipal.setSelected(true);
		}else {
			txtDescripcion.setText(actividad.getDescripcion());
			dtpFecha.setValue(actividad.getFecha());
			//seleccionar estado de actividad
			if(actividad.getEstadoActividad().equals(Constantes.ESTADO_PENDIENTE))
				rbPendiente.setSelected(true);
			else if(actividad.getEstadoActividad().equals(Constantes.ESTADO_EN_PROCESO))
				rbProceso.setSelected(true);
			else if(actividad.getEstadoActividad().equals(Constantes.ESTADO_REALIZADO))
				rbRealizado.setSelected(true);
			else if(actividad.getEstadoActividad().equals(Constantes.ESTADO_RECHAZADO)) {
				rbPendiente.setDisabled(true);
				rbProceso.setDisabled(true);
				rbRealizado.setSelected(true);
			}
			//seleccionar tipo de actividad
			if(actividad.getTipoActividad() != null) {
				if(actividad.getTipoActividad().getIdTipoActividad() == Constantes.ID_TIPO_PRIMORDIALES) {
					rbPrincipal.setChecked(true);
					rbInterna.setChecked(false);
				}else {
					rbPrincipal.setChecked(false);
					rbInterna.setChecked(true);
				}
			}else {
				rbPrincipal.setSelected(true);
			}
		}
		
		if(tipoActividad.equals("INTERNA")) {
			rbInterna.setChecked(true);
			rbPrincipal.setChecked(false);
		}else {
			rbInterna.setChecked(false);
			rbPrincipal.setChecked(true);
		}
	}
	@Listen("onClick=#btnGrabar")
	public void grabar(){
		if(validarDatos() == true) {
			EventListener<ClickEvent> clickListener = new EventListener<Messagebox.ClickEvent>() {
				public void onEvent(ClickEvent event) throws Exception {
					if(Messagebox.Button.YES.equals(event.getButton())) {
						actividadDAO.getEntityManager().getTransaction().begin();
						cargarDatos();
						if(actividad.getIdActividad() == null) {
							if(agenda.getIdAgenda() == null) {//es un nuevo
								actividadDAO.getEntityManager().persist(agenda);
							}else {//es modificacion
								actividadDAO.getEntityManager().merge(actividad);
							}
						}else {
							actividadDAO.getEntityManager().merge(actividad);
						}

						actividadDAO.getEntityManager().getTransaction().commit();
						Messagebox.show("Datos grabados con exito");
						//aDiaria.cargarAgendas();
						BindUtils.postGlobalCommand(null, null, "Actividad.buscarPorAgenda", null);
						salir();
					}
				}
			};
			Messagebox.show("¿Desea Grabar los Datos?", "Confirmación", new Messagebox.Button[]{
					Messagebox.Button.YES, Messagebox.Button.NO }, Messagebox.QUESTION, clickListener);
		}
	}
	private void cargarDatos() {
		actividad.setDescripcion(txtDescripcion.getText().toString());
		actividad.setEstado("A");
		actividad.setEstadoPublicado(Constantes.ESTADO_NO_PUBLICADO);
		actividad.setFecha(dtpFecha.getValue());
		//el estado de la actividad depende de los radios
		if(rbPendiente.isChecked())
			actividad.setEstadoActividad(Constantes.ESTADO_PENDIENTE);
		else if(rbProceso.isChecked())
			actividad.setEstadoActividad(Constantes.ESTADO_EN_PROCESO);
		else if(rbRealizado.isChecked())
			actividad.setEstadoActividad(Constantes.ESTADO_REALIZADO);
		
		//cargar el tipo de actividad
		
		List<TipoActividad> tipo = new ArrayList<>();
		if(rbInterna.isChecked()) {
			tipo = tipoActividadDAO.obtenerPorId(Constantes.ID_TIPO_INTERNAS);
		}else {
			tipo = tipoActividadDAO.obtenerPorId(Constantes.ID_TIPO_PRIMORDIALES);
		}
		actividad.setTipoActividad(tipo.get(0));
		//enlazar con la aganda
		actividad.setAgenda(agenda);
		codigoActividad();
		
		if(agenda.getActividads().size() > 0) {
			agenda.getActividads().add(actividad);
		}else {
			List<Actividad> lista = new ArrayList<Actividad>();
			lista.add(actividad);
			agenda.setActividads(lista);
		}
	}
	
	private void codigoActividad() {
		if(actividad.getIdActividad() == null) {
			String codigo = Constantes.CODIGO_ACTIVIDAD;
			Integer ultimoCodigo = 1;
			Empleado empleado = usuarioDAO.getUsuario(SecurityUtil.getUser().getUsername().trim());
			
			List<Actividad> acividades = actividadDAO.obtenerCodigoActividad(agenda.getIdAgenda());
			if(acividades.size() > 0) {
				ultimoCodigo = acividades.get(0).getSecuencia() + 1;
				codigo = codigo + ultimoCodigo + "-" + empleado.getDepartamento().getCodigo(); 
			}else { //es la primera
				codigo = codigo + "1-" + empleado.getDepartamento().getCodigo();
			}
			actividad.setCodigo(codigo);
			actividad.setSecuencia(ultimoCodigo);
		}
	}
	
	private boolean validarDatos() {
		try {
			if(txtDescripcion.getText().isEmpty()) {
				Clients.showNotification("Debe registrar la descripcion de la actividad","info",txtDescripcion,"end_center",2000);
				txtDescripcion.focus();
				return false;
			}
			if(dtpFecha.getValue() == null) {
				Clients.showNotification("Debe registrar la fecha de la actividad","info",dtpFecha,"end_center",2000);
				return false;
			}
			return true;
		}catch(Exception ex) {
			return false;
		}
	}
	@Listen("onClick=#btnSalir")
	public void salir(){
		BindUtils.postGlobalCommand(null, null, "Actividad.buscarPorAgenda", null);
		winActividadEditar.detach();
	}
	public Agenda getAgenda() {
		return agenda;
	}
	public void setAgenda(Agenda agenda) {
		this.agenda = agenda;
	}
	public Actividad getActividad() {
		return actividad;
	}
	public void setActividad(Actividad actividad) {
		this.actividad = actividad;
	}
	public String getTipoActividad() {
		return tipoActividad;
	}
	public void setTipoActividad(String tipoActividad) {
		this.tipoActividad = tipoActividad;
	}
}
