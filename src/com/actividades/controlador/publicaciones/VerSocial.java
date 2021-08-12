package com.actividades.controlador.publicaciones;

import java.io.IOException;
import java.util.List;

import org.zkoss.bind.annotation.AfterCompose;
import org.zkoss.bind.annotation.BindingParam;
import org.zkoss.bind.annotation.Command;
import org.zkoss.bind.annotation.ContextParam;
import org.zkoss.bind.annotation.ContextType;
import org.zkoss.bind.annotation.GlobalCommand;
import org.zkoss.bind.annotation.NotifyChange;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.select.Selectors;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zk.ui.util.Clients;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Messagebox;

import com.actividades.modelo.Publicacion;
import com.actividades.modelo.PublicacionDAO;

public class VerSocial {
	
	@Wire private Listbox ltsLinks;
	List<Publicacion> listaPublicacion;
	
	PublicacionDAO publicacionDAO = new PublicacionDAO();
	
	@AfterCompose
	public void aferCompose(@ContextParam(ContextType.VIEW) Component view) throws IOException{
		Selectors.wireComponents(view, this, false);
		buscar();
	}
	
	@GlobalCommand("Publicacion.buscarPorPatron")
	@NotifyChange({"listaPublicacion", "publicacionSeleccionado"})
	@Command
	public void buscar(){
		if (listaPublicacion != null)
			listaPublicacion = null; 
		listaPublicacion = publicacionDAO.getListaPublicacion("");
		if(listaPublicacion.size() == 0) {
			Clients.showNotification("No hay datos para mostrar.!!");
		}
	}
	
	@Command
	public void ver(@BindingParam("publicacion") Publicacion pub){
		if (pub == null) {
			return;
		}
		Executions.getCurrent().sendRedirect(pub.getLink(), "_blank");
	}

	public List<Publicacion> getListaPublicacion() {
		return listaPublicacion;
	}

	public void setListaPublicacion(List<Publicacion> listaPublicacion) {
		this.listaPublicacion = listaPublicacion;
	}
	
}
