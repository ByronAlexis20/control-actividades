package com.actividades.controlador.publicaciones;

import java.io.IOException;
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
import org.zkoss.zk.ui.util.Clients;
import org.zkoss.zul.Messagebox;
import org.zkoss.zul.Window;

import com.actividades.modelo.Informacion;
import com.actividades.modelo.InformacionDAO;

public class DocumentosC {
	List<Informacion> listaInformacion;
	Informacion informacionSeleccionado;
	InformacionDAO informacionDAO = new InformacionDAO();
	
	@AfterCompose
	public void aferCompose(@ContextParam(ContextType.VIEW) Component view) throws IOException{
		Selectors.wireComponents(view, this, false);
		buscar();
	}
	@GlobalCommand("Informacion.buscarTodos")
	@NotifyChange({"listaInformacion", "informacionSeleccionado"})
	@Command
	public void buscar(){
		if (listaInformacion != null)
			listaInformacion = null; 
		listaInformacion = informacionDAO.buscarTodos();
		if(listaInformacion.size() == 0) {
			Clients.showNotification("No hay datos para mostrar.!!");
		}else {
			informacionSeleccionado = null;
		}
	}
	@Command
	public void nuevo(){
		Window ventanaCargar = (Window) Executions.createComponents("/formularios/publicaciones/documentos/DocumentoEditar.zul", null, null);
		ventanaCargar.doModal();
	}
	@Command
	public void editar(@BindingParam("informacion") Informacion inf){
		if(inf == null) {
			Clients.showNotification("Seleccione una opción de la lista.");
			return;
		}
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("Informacion", inf);
		Window ventanaCargar = (Window) Executions.createComponents("/formularios/publicaciones/documentos/DocumentoEditar.zul", null, params);
		ventanaCargar.doModal();

	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Command
	public void eliminar(@BindingParam("informacion") Informacion inf){
		if (inf == null) {
			Messagebox.show("Debe seleccionar un cargo para eliminar!");
			return; 
		}
		Messagebox.show("Desea eliminar el registro seleccionado?", "Confirmación de Eliminación", Messagebox.YES | Messagebox.NO, Messagebox.QUESTION, new EventListener() {	
			@Override
			public void onEvent(Event event) throws Exception {
				if (event.getName().equals("onYes")) {
					try {
						inf.setEstado("I");
						informacionDAO.getEntityManager().getTransaction().begin();
						informacionDAO.getEntityManager().merge(inf);
						informacionDAO.getEntityManager().getTransaction().commit();
						Messagebox.show("Transaccion ejecutada con exito");
						BindUtils.postGlobalCommand(null, null, "Informacion.buscarTodos", null);
					} catch (Exception e) {
						e.printStackTrace();
						informacionDAO.getEntityManager().getTransaction().rollback();
					}
				}
			}
		});	
	}
	public List<Informacion> getListaInformacion() {
		return listaInformacion;
	}
	public void setListaInformacion(List<Informacion> listaInformacion) {
		this.listaInformacion = listaInformacion;
	}
	public Informacion getInformacionSeleccionado() {
		return informacionSeleccionado;
	}
	public void setInformacionSeleccionado(Informacion informacionSeleccionado) {
		this.informacionSeleccionado = informacionSeleccionado;
	}
}