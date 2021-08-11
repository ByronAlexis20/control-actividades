package com.actividades.controlador.publicaciones;

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
import org.zkoss.zul.Messagebox;
import org.zkoss.zul.Messagebox.ClickEvent;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

import com.actividades.modelo.Publicacion;
import com.actividades.modelo.PublicacionDAO;

public class PublicacionEditar {
	@Wire private Window winPublicacionEditar;
	@Wire private Textbox txtDescripcion;
	
	PublicacionDAO publicacionDAO = new PublicacionDAO();
	Publicacion publicacion;
	
	@AfterCompose
	public void afterCompose(@ContextParam(ContextType.VIEW) Component view) {
		Selectors.wireComponents(view, this, false);
		// Recupera el objeto pasado como parametro. 
		publicacion = (Publicacion) Executions.getCurrent().getArg().get("Publicacion");
		if (publicacion == null) {
			publicacion = new Publicacion();
			publicacion.setEstado("A");
		} 
	}
	
	@Command
	public void grabar(){
		if(validarDatos() == true) {
			EventListener<ClickEvent> clickListener = new EventListener<Messagebox.ClickEvent>() {
				public void onEvent(ClickEvent event) throws Exception {
					if(Messagebox.Button.YES.equals(event.getButton())) {
						publicacionDAO.getEntityManager().getTransaction().begin();
						publicacion.setEstado("A");
						if(publicacion.getIdPublicacion() == null) {//es un nuevo
							publicacionDAO.getEntityManager().persist(publicacion);
						}else {//es modificacion
							publicacionDAO.getEntityManager().merge(publicacion);
						}
						publicacionDAO.getEntityManager().getTransaction().commit();
						Messagebox.show("Datos grabados con exito");
						BindUtils.postGlobalCommand(null, null, "Publicacion.buscarPorPatron", null);
						salir();
					}
				}
			};
			Messagebox.show("¿Desea Grabar los Datos?", "Confirmación", new Messagebox.Button[]{
					Messagebox.Button.YES, Messagebox.Button.NO }, Messagebox.QUESTION, clickListener);
		}
	}
	public boolean validarDatos() {
		if(txtDescripcion.getText() == "") {
			Clients.showNotification("Debe registrar la descripción","info",txtDescripcion,"end_center",2000);
			txtDescripcion.setFocus(true);
			return false;
		}
		return true;
	}
	@Command
	public void salir(){
		winPublicacionEditar.detach();
	}

	public Publicacion getPublicacion() {
		return publicacion;
	}

	public void setPublicacion(Publicacion publicacion) {
		this.publicacion = publicacion;
	}
	
}
