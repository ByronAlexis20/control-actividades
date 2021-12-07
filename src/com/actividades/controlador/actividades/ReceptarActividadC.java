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

import com.actividades.modelo.Actividad;
import com.actividades.modelo.ActividadDAO;
import com.actividades.modelo.ActividadExterna;
import com.actividades.modelo.ActividadExternaDAO;
import com.actividades.modelo.Agenda;
import com.actividades.modelo.AgendaDAO;
import com.actividades.modelo.Empleado;
import com.actividades.modelo.EmpleadoDAO;
import com.actividades.modelo.TipoActividadDAO;
import com.actividades.util.Constantes;
import com.actividades.util.SecurityUtil;

public class ReceptarActividadC {
	List<ActividadExterna> listaActividadExterna;
	ActividadExterna actividadExternaSeleccionado;
	ActividadExternaDAO actividadExternaDAO = new ActividadExternaDAO();
	TipoActividadDAO tipoActividadDAO = new TipoActividadDAO();
	ActividadDAO actividadDAO = new ActividadDAO();
	AgendaDAO agendaDAO = new AgendaDAO();
	EmpleadoDAO usuarioDAO = new EmpleadoDAO();
	
	@AfterCompose
	public void afterCompose(@ContextParam(ContextType.VIEW) Component view) {
		Selectors.wireComponents(view, this, false);
		this.cargarActividades();
	}
	
	@GlobalCommand("ActividadExterna.buscarPendienteAsignacion")
	@Command
	@NotifyChange({"listaActividadExterna"})
	public void cargarActividades() {
		listaActividadExterna = this.actividadExternaDAO.obtenerActividadesPendienteAsignacion();
	}
	
	@Command
	public void nuevaActividad() {
		Window ventanaCargar = (Window) Executions.createComponents("/formularios/actividades/enviar-actividad-varios-funcionarios/nuevaActividadExterna.zul", null, null);
		ventanaCargar.doModal();
	}
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Command
	public void aceptarActividad() {
		if(actividadExternaSeleccionado == null) {
			Clients.showNotification("Debe seleccionar actividad");
			return;
		}
		Messagebox.show("Asignarme esta actividad", "Confirmación", Messagebox.YES | Messagebox.NO, Messagebox.QUESTION, new EventListener() {	
			@Override
			public void onEvent(Event event) throws Exception {
				if (event.getName().equals("onYes")) {
					try {
						actividadExternaSeleccionado.setEstadoActividad(Constantes.ESTADO_ASIGNADO);
						actividadExternaDAO.getEntityManager().getTransaction().begin();
						actividadExternaDAO.getEntityManager().merge(actividadExternaSeleccionado);
						
						//copiar actividad a la actividad principal
						Empleado usuario = usuarioDAO.getUsuario(SecurityUtil.getUser().getUsername().trim());
						List<Agenda> listaAgenda = agendaDAO.obtenerPorFechaEmpleado(usuario.getIdEmpleado(), actividadExternaSeleccionado.getFecha());
						if(listaAgenda.size() > 0) {//si hay agenda en esa fecha.. se agrega la actividad
							Actividad actividad = new Actividad();
							actividad.setAgenda(listaAgenda.get(0));
							actividad.setClaseActividad(actividadExternaSeleccionado.getClaseActividad());
							actividad.setDescripcion(actividadExternaSeleccionado.getDescripcion());
							actividad.setEstado(Constantes.ESTADO_ACTIVO);
							actividad.setEstadoActividad(Constantes.ESTADO_PENDIENTE);
							actividad.setEstadoPublicado(Constantes.ESTADO_NO_PUBLICADO);
							actividad.setFecha(actividadExternaSeleccionado.getFecha());
							actividad.setIdActividad(null);
							actividad.setIdResponsable(usuario.getIdEmpleado());
							actividad.setTipoActividad(tipoActividadDAO.obtenerPorId(Constantes.ID_TIPO_PRIMORDIALES).get(0));
							//codigo de actividad
							if(actividad.getIdActividad() == null) {
								String codigo = Constantes.CODIGO_ACTIVIDAD;
								Integer ultimoCodigo = 1;
								
								List<Actividad> acividades = actividadDAO.obtenerCodigoActividad(listaAgenda.get(0).getIdAgenda());
								if(acividades.size() > 0) {
									ultimoCodigo = acividades.get(0).getSecuencia() + 1;
									codigo = codigo + ultimoCodigo + "-" + usuario.getDepartamento().getCodigo(); 
								}else { //es la primera
									codigo = codigo + "1-" + usuario.getDepartamento().getCodigo();
								}
								actividad.setCodigo(codigo);
								actividad.setSecuencia(ultimoCodigo);
							}
							actividadExternaDAO.getEntityManager().persist(actividad);
						}else {
							//se crea una nueva agenda
							Agenda agenda = new Agenda();
							//codigo de la agenda
							String codigo = Constantes.CODIGO_AGENDA;
							Integer ultimoCodigo = 1;
							List<Agenda> agendas = new ArrayList<>();
							agendas = agendaDAO.obtenerCodigoAgendaActiva(usuario.getIdEmpleado());
							if(agendas.size() > 0) {
								ultimoCodigo = agendas.get(0).getSecuencia() + 1;
								codigo = codigo + ultimoCodigo + "-" + usuario.getDepartamento().getCodigo(); 
							}else { //es la primera
								codigo = codigo + "1-" + usuario.getDepartamento().getCodigo();
							}
							agenda.setCodigo(codigo);
							agenda.setSecuencia(ultimoCodigo);
							//*******************
							agenda.setEmpleado(usuario);
							agenda.setEstado(Constantes.ESTADO_ACTIVO);
							agenda.setIdAgenda(null);
							//fecha de inicio
							agenda.setFechaInicio(actividadExternaSeleccionado.getFecha());
							//fecha fin
							Date dt = actividadExternaSeleccionado.getFecha();
							Calendar c = Calendar.getInstance();
							c.setTime(dt);
							int dayOfWeek = c.get(Calendar.DAY_OF_WEEK);
							c.add(Calendar.DATE, Constantes.NUMERO_DIA_VIERNES - dayOfWeek);
							dt = c.getTime();
							agenda.setFechaFin(dt);
							agenda.setDescripcion("Agenda solicitada desde " + new SimpleDateFormat("dd/MM/yyyy").format(actividadExternaSeleccionado.getFecha()) + " " + new SimpleDateFormat("dd/MM/yyyy").format(dt));
							
							actividadExternaDAO.getEntityManager().persist(agenda);
							
							//actividad
							Actividad actividad = new Actividad();
							actividad.setAgenda(agenda);
							actividad.setClaseActividad(actividadExternaSeleccionado.getClaseActividad());
							actividad.setDescripcion(actividadExternaSeleccionado.getDescripcion());
							actividad.setEstado(Constantes.ESTADO_ACTIVO);
							actividad.setEstadoActividad(Constantes.ESTADO_PENDIENTE);
							actividad.setEstadoPublicado(Constantes.ESTADO_NO_PUBLICADO);
							actividad.setFecha(actividadExternaSeleccionado.getFecha());
							actividad.setIdActividad(null);
							actividad.setIdResponsable(usuario.getIdEmpleado());
							actividad.setTipoActividad(tipoActividadDAO.obtenerPorId(Constantes.ID_TIPO_PRIMORDIALES).get(0));
							//codigo de actividad
							if(actividad.getIdActividad() == null) {
								String codigoAc = Constantes.CODIGO_ACTIVIDAD;
								Integer ultimoCodigoAc = 1;
								codigoAc = codigoAc + "1-" + usuario.getDepartamento().getCodigo();
								actividad.setCodigo(codigo);
								actividad.setSecuencia(ultimoCodigoAc);
							}
							actividadExternaDAO.getEntityManager().persist(actividad);
						}
						actividadExternaDAO.getEntityManager().getTransaction().commit();
						Messagebox.show("Transaccion ejecutada con exito");
						BindUtils.postGlobalCommand(null, null, "ActividadExterna.buscarPendienteAsignacion", null);
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
