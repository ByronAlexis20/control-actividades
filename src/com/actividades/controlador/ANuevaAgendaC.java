package com.actividades.controlador;

import java.text.SimpleDateFormat;
import java.util.Date;

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
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;
import org.zkoss.zul.Messagebox.ClickEvent;

import com.actividades.modelo.Agenda;
import com.actividades.modelo.AgendaDAO;
import com.actividades.modelo.EmpleadoDAO;
import com.actividades.util.Constantes;
import com.actividades.util.SecurityUtil;

@SuppressWarnings("serial")
public class ANuevaAgendaC extends SelectorComposer<Component>{
	@Wire private Window winAgendaEditar;
	@Wire private Textbox txtDescripcion;
	@Wire private Datebox dtpFechaInicio;
	@Wire private Datebox dtpFechaFin;
	
	ADiariaC aDiaria;
	private Agenda agenda;
	private AgendaDAO agendaDAO = new AgendaDAO();
	EmpleadoDAO usuarioDAO = new EmpleadoDAO();
	
	@Override
	public void doAfterCompose(Component comp) throws Exception {
		// TODO Auto-generated method stub
		super.doAfterCompose(comp);
		aDiaria = (ADiariaC) Executions.getCurrent().getArg().get("Ventana");
		if(Executions.getCurrent().getArg().get("Agenda") != null) {
			agenda = (Agenda) Executions.getCurrent().getArg().get("Agenda");
			txtDescripcion.setText(agenda.getDescripcion());
			dtpFechaInicio.setValue(agenda.getFechaInicio());
			dtpFechaFin.setValue(agenda.getFechaFin());
			dtpFechaInicio.setDisabled(true);
			dtpFechaFin.setDisabled(true);
		}else {
			agenda = new Agenda();
			//dtpFechaInicio.setValue(new Date());
			dtpFechaFin.setConstraint("after " + new SimpleDateFormat("yyyyMMdd").format(new Date()));
			dtpFechaFin.setDisabled(true);
		}
	}
	@Listen("onClick=#btnGrabar")
	public void grabar(){
		if(validarDatos() == true) {
			EventListener<ClickEvent> clickListener = new EventListener<Messagebox.ClickEvent>() {
				public void onEvent(ClickEvent event) throws Exception {
					if(Messagebox.Button.YES.equals(event.getButton())) {
						agendaDAO.getEntityManager().getTransaction().begin();
						agenda.setDescripcion(txtDescripcion.getText().toString());
						agenda.setEstado(Constantes.ESTADO_ACTIVO);
						agenda.setFechaInicio(dtpFechaInicio.getValue());
						agenda.setFechaFin(dtpFechaFin.getValue());
						agenda.setEmpleado(usuarioDAO.getUsuario(SecurityUtil.getUser().getUsername().trim()));
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
	@Listen("onChange=#dtpFechaInicio")
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
			return true;
		}catch(Exception ex) {
			return false;
		}
	}
	@Listen("onClick=#btnSalir")
	public void salir(){
		winAgendaEditar.detach();
	}
	public Agenda getAgenda() {
		return agenda;
	}
	public void setAgenda(Agenda agenda) {
		this.agenda = agenda;
	}
}
