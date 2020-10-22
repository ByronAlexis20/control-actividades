package com.actividades.controlador;

import java.util.Date;

import org.zkoss.bind.BindContext;
import org.zkoss.bind.BindUtils;
import org.zkoss.bind.annotation.AfterCompose;
import org.zkoss.bind.annotation.Command;
import org.zkoss.bind.annotation.ContextParam;
import org.zkoss.bind.annotation.ContextType;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.event.UploadEvent;
import org.zkoss.zk.ui.select.Selectors;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zk.ui.util.Clients;
import org.zkoss.zul.Messagebox;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

import com.actividades.modelo.Empleado;
import com.actividades.modelo.EmpleadoDAO;
import com.actividades.modelo.Queja;
import com.actividades.modelo.QuejaDAO;
import com.actividades.util.Constantes;
import com.actividades.util.FileUtil;
import com.actividades.util.SecurityUtil;

public class QQuejaEditarC {
	Queja queja;
	@Wire private Window winQuejaEditar;
	@Wire private Textbox txtProblema;
	@Wire private Textbox txtDescripcion;
	@Wire private Textbox txtArchivo;
	QuejaDAO quejaDAO = new QuejaDAO();
	EmpleadoDAO usuarioDAO = new EmpleadoDAO();
	
	@AfterCompose
	public void afterCompose(@ContextParam(ContextType.VIEW) Component view) {
		// Permite enlazar los componentes que se asocian con la anotacion @Wire
		Selectors.wireComponents(view, this, false);
		queja = (Queja) Executions.getCurrent().getArg().get("Queja");
		if(queja == null) {
			queja = new Queja();
			queja.setIdQueja(null);
			queja.setFechaEnvio(new Date());
		}
		Empleado usuario = usuarioDAO.getUsuario(SecurityUtil.getUser().getUsername().trim());
		queja.setEmpleado(usuario);
		queja.setEstado("A");
		queja.setEstadoQueja(Constantes.QUEJA_PENDIENTE);
		queja.setEstadoAtencion(Constantes.QUEJA_NO_ATENDIDA);
	}
	
	@Command
	public void agregar(@ContextParam(ContextType.BIND_CONTEXT) BindContext contexto) {
		String pathRetornado; 
		UploadEvent eventoCarga = (UploadEvent) contexto.getTriggerEvent();
		pathRetornado = FileUtil.cargaArchivo(eventoCarga.getMedia());
		txtArchivo.setValue(eventoCarga.getMedia().getName());
		queja.setAdjunto(pathRetornado);
		queja.setArchivoAdjunto(eventoCarga.getMedia().getName());
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Command
	public void grabar() {
		if(txtProblema.getText().isEmpty()) {
			Messagebox.show("Debe registrar un problema");
			return;
		}
		if(txtDescripcion.getText().isEmpty()) {
			Messagebox.show("Debe registrar una breve descripción del problema");
			return;
		}
		Messagebox.show("Desea guardar el registro?", "Confirmación de Guardar", Messagebox.YES | Messagebox.NO, Messagebox.QUESTION, new EventListener() {
			@Override
			public void onEvent(Event event) throws Exception {
				if (event.getName().equals("onYes")) {		
					try {
						quejaDAO.getEntityManager().getTransaction().begin();
						if (queja.getIdQueja() == null) {
							quejaDAO.getEntityManager().persist(queja);
						}else{
							queja = (Queja) quejaDAO.getEntityManager().merge(queja);
						}			
						quejaDAO.getEntityManager().getTransaction().commit();
						Clients.showNotification("Proceso Ejecutado con exito.");
						BindUtils.postGlobalCommand(null, null, "Queja.buscarPorResponsableEstadoQuejaAtencion", null);
						salir();						
					} catch (Exception e) {
						e.printStackTrace();
						quejaDAO.getEntityManager().getTransaction().rollback();
					}
				}
			}
		});
	}
	@Command
	public void salir() {
		winQuejaEditar.detach();
	}
	public Queja getQueja() {
		return queja;
	}

	public void setQueja(Queja queja) {
		this.queja = queja;
	}
	
}
