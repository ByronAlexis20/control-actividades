package com.actividades.controlador;

import org.zkoss.bind.annotation.AfterCompose;
import org.zkoss.bind.annotation.Command;
import org.zkoss.bind.annotation.ContextParam;
import org.zkoss.bind.annotation.ContextType;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.select.Selectors;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zul.Button;
import org.zkoss.zul.Window;

public class AEvidenciaC {
	@Wire private Window winEvidencia;
	@Wire private Button btnAnadirArchivo;
	
	@AfterCompose
	public void afterCompose(@ContextParam(ContextType.VIEW) Component view) {
		// Permite enlazar los componentes que se asocian con la anotacion @Wire
		Selectors.wireComponents(view, this, false);
		btnAnadirArchivo.setUpload("true,accept=.jpg");
	}
	
	@Command
	public void salir() {
		winEvidencia.detach();
	}
	
	public void anadirArchivo() {
		
	}
	
	@Command
	public void agregar() {
		
	}
}
