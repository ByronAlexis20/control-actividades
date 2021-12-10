package com.actividades.controlador.publicaciones;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
import org.zkoss.zk.ui.util.Clients;
import org.zkoss.zul.Filedownload;
import org.zkoss.zul.Window;

import com.actividades.modelo.Informacion;
import com.actividades.modelo.InformacionDAO;

public class VerDocumentosC {
	
	List<Informacion> listaInformacion;
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
		}
	}
	@Command
	public void ver(@BindingParam("informacion") Informacion inf) throws FileNotFoundException{
		if (inf == null) {
			return;
		}
		String extension = "";
    	
    	int i = inf.getRutaArchivo().lastIndexOf('.');
    	if (i >= 0) {
    	    extension = inf.getRutaArchivo().substring(i+1);
    	}
    	if(extension.toLowerCase().equals("pdf")) {
    		Map<String, Object> params = new HashMap<String, Object>();
    		params.put("Informacion", inf);
    		Window ventanaCargar = (Window) Executions.createComponents("/formularios/visualizarPDF/visualizarPDF.zul", null, params);
    		ventanaCargar.doModal();
    	}else {
    		File file = new File(inf.getRutaArchivo());
    		Filedownload.save(file, null);    		
    	}
	}
	public List<Informacion> getListaInformacion() {
		return listaInformacion;
	}
	public void setListaInformacion(List<Informacion> listaInformacion) {
		this.listaInformacion = listaInformacion;
	}
}
