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
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zk.ui.util.Clients;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Messagebox;
import org.zkoss.zul.Window;

import com.actividades.modelo.Publicacion;
import com.actividades.modelo.PublicacionDAO;

public class SocialC {
	public String textoBuscar;
	@Wire private Listbox ltsLinks;
	List<Publicacion> listaPublicacion;
	Publicacion publicacionSeleccionado;
	PublicacionDAO publicacionDAO = new PublicacionDAO();
	@AfterCompose
	public void aferCompose(@ContextParam(ContextType.VIEW) Component view) throws IOException{
		Selectors.wireComponents(view, this, false);
		textoBuscar="";
		buscar();
	}
	@GlobalCommand("Publicacion.buscarPorPatron")
	@NotifyChange({"listaPublicacion", "publicacionSeleccionado"})
	@Command
	public void buscar(){
		if (listaPublicacion != null)
			listaPublicacion = null; 
		listaPublicacion = publicacionDAO.getListaPublicacion(textoBuscar);
		if(listaPublicacion.size() == 0) {
			Clients.showNotification("No hay datos para mostrar.!!");
		}else {
			publicacionSeleccionado = null;
		}
	}
	@Command
	public void nuevo(){
		Window ventanaCargar = (Window) Executions.createComponents("/formularios/publicaciones/redes_sociales/socialEditar.zul", null, null);
		ventanaCargar.doModal();
	}
	@Command
	public void editar(@BindingParam("publicacion") Publicacion pub){
		if(pub == null) {
			Clients.showNotification("Seleccione una opción de la lista.");
			return;
		}
	
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("Publicacion", pub);
		Window ventanaCargar = (Window) Executions.createComponents("/formularios/publicaciones/redes_sociales/socialEditar.zul", null, params);
		ventanaCargar.doModal();

	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Command
	public void eliminar(@BindingParam("publicacion") Publicacion pub){
		if (pub == null) {
			Messagebox.show("Debe seleccionar un cargo para eliminar!");
			return; 
		}
		Messagebox.show("Desea eliminar el registro seleccionado?", "Confirmación de Eliminación", Messagebox.YES | Messagebox.NO, Messagebox.QUESTION, new EventListener() {	
			@Override
			public void onEvent(Event event) throws Exception {
				if (event.getName().equals("onYes")) {
					try {
						pub.setEstado("I");
						publicacionDAO.getEntityManager().getTransaction().begin();
						publicacionDAO.getEntityManager().merge(pub);
						publicacionDAO.getEntityManager().getTransaction().commit();
						Messagebox.show("Transaccion ejecutada con exito");
						BindUtils.postGlobalCommand(null, null, "Publicacion.buscarPorPatron", null);
					} catch (Exception e) {
						e.printStackTrace();
						publicacionDAO.getEntityManager().getTransaction().rollback();
					}
				}
			}
		});	
	}
	public List<Publicacion> getListaPublicacion() {
		return listaPublicacion;
	}
	public void setListaPublicacion(List<Publicacion> listaPublicacion) {
		this.listaPublicacion = listaPublicacion;
	}
	public Publicacion getPublicacionSeleccionado() {
		return publicacionSeleccionado;
	}
	public void setPublicacionSeleccionado(Publicacion publicacionSeleccionado) {
		this.publicacionSeleccionado = publicacionSeleccionado;
	}
	public String getTextoBuscar() {
		return textoBuscar;
	}
	public void setTextoBuscar(String textoBuscar) {
		this.textoBuscar = textoBuscar;
	}
	
}