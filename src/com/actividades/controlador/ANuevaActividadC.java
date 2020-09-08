package com.actividades.controlador;

import java.util.HashMap;
import java.util.Map;

import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.select.SelectorComposer;
import org.zkoss.zk.ui.select.annotation.Listen;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zul.Window;

@SuppressWarnings("serial")
public class ANuevaActividadC extends SelectorComposer<Component>{
	@Wire private Window winActividadEditar;
	
	@Override
	public void doAfterCompose(Component comp) throws Exception {
		super.doAfterCompose(comp);
	}
	@Listen("onClick=#btnAnadirEvidencia")
	public void nuevaEvidencia() {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("Ventana", this);
		Window ventanaCargar = (Window) Executions.createComponents("/formularios/actividades/diaria/AEvidencia.zul", winActividadEditar, params);
		ventanaCargar.doModal();
	}
	@Listen("onClick=#btnQuitarEvidencia")
	public void quitarEvidencia() {
		
	}
}
