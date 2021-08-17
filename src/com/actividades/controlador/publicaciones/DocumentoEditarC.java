package com.actividades.controlador.publicaciones;

import org.zkoss.bind.BindContext;
import org.zkoss.bind.BindUtils;
import org.zkoss.bind.annotation.AfterCompose;
import org.zkoss.bind.annotation.Command;
import org.zkoss.bind.annotation.ContextParam;
import org.zkoss.bind.annotation.ContextType;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.event.UploadEvent;
import org.zkoss.zk.ui.select.Selectors;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zk.ui.util.Clients;
import org.zkoss.zul.Messagebox;
import org.zkoss.zul.Messagebox.ClickEvent;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

import com.actividades.modelo.Informacion;
import com.actividades.modelo.InformacionDAO;
import com.actividades.util.FileUtil;

public class DocumentoEditarC {
	
	@Wire private Window winDocumentoEditar;
	@Wire private Textbox txtArchivo;
	Informacion informacion;
	
	InformacionDAO informacionDAO = new InformacionDAO();
	
	@AfterCompose
	public void afterCompose(@ContextParam(ContextType.VIEW) Component view) {
		// Permite enlazar los componentes que se asocian con la anotacion @Wire
		Selectors.wireComponents(view, this, false);
		informacion = (Informacion) Executions.getCurrent().getArg().get("Informacion");
		if(informacion == null) {
			informacion = new Informacion();
			informacion.setEstado("A");
		}
	}
	@Command
	public void agregar(@ContextParam(ContextType.BIND_CONTEXT) BindContext contexto) {
		String pathRetornado; 
		UploadEvent eventoCarga = (UploadEvent) contexto.getTriggerEvent();
		pathRetornado = FileUtil.cargaArchivo(eventoCarga.getMedia());
		txtArchivo.setValue(eventoCarga.getMedia().getName());
		informacion.setRutaArchivo(pathRetornado);
		informacion.setNombreArchivo(eventoCarga.getMedia().getName());
	}
	@Command
	public void grabar(){
		if(validarDatos() == true) {
			EventListener<ClickEvent> clickListener = new EventListener<Messagebox.ClickEvent>() {
				public void onEvent(ClickEvent event) throws Exception {
					if(Messagebox.Button.YES.equals(event.getButton())) {
						informacionDAO.getEntityManager().getTransaction().begin();
						if(informacion.getIdInformacion() == null)
							informacionDAO.getEntityManager().persist(informacion);
						else
							informacionDAO.getEntityManager().merge(informacion);
						informacionDAO.getEntityManager().getTransaction().commit();
						Messagebox.show("Datos grabados con exito");
						BindUtils.postGlobalCommand(null, null, "Informacion.buscarTodos", null);
						salir();
					}
				}
			};
			Messagebox.show("¿Desea Grabar los Datos?", "Confirmación", new Messagebox.Button[]{
					Messagebox.Button.YES, Messagebox.Button.NO }, Messagebox.QUESTION, clickListener);
		}
	}
	private boolean validarDatos() {
		try {
			if(informacion.getNombreArchivo().isEmpty()) {
				Clients.showNotification("Debe seleccionar el archivo");
				return false;
			}
			return true;
		}catch(Exception ex) {
			return false;
		}
	}
	@Command
	public void salir() {
		winDocumentoEditar.detach();
	}
	public Informacion getInformacion() {
		return informacion;
	}
	public void setInformacion(Informacion informacion) {
		this.informacion = informacion;
	}
	
}