package com.actividades.controlador.dashboard;

import java.util.HashMap;
import java.util.Map;

import org.zkoss.bind.annotation.AfterCompose;
import org.zkoss.bind.annotation.Command;
import org.zkoss.bind.annotation.ContextParam;
import org.zkoss.bind.annotation.ContextType;
import org.zkoss.zhtml.Messagebox;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.select.Selectors;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zk.ui.util.Clients;
import org.zkoss.zul.Datebox;
import org.zkoss.zul.Window;

import com.actividades.modelo.Empleado;

public class BusquedaFechaC {
	@Wire private Window winFechaBusqueda;
	@Wire Datebox dtpFechaInicio;
	@Wire Datebox dtpFechaFin;
	
	Empleado empleado;
	
	@AfterCompose
	public void afterCompose(@ContextParam(ContextType.VIEW) Component view) {
		Selectors.wireComponents(view, this, false);
		empleado = (Empleado) Executions.getCurrent().getArg().get("Empleado");
	}
	
	@Command
	public void buscar(){
		if(dtpFechaInicio.getValue() == null) {
			Clients.showNotification("Debe seleccionar fecha Inicio");
			return;
		}
		if(dtpFechaFin.getValue() == null) {
			Clients.showNotification("Debe seleccionar fecha fin");
			return;
		}
		if(dtpFechaInicio.getValue().after(dtpFechaFin.getValue())) {
			Messagebox.show("Fecha inicio no debe ser mayor a fecha fin");
			return;
		}
		
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("Empleado", empleado);
		params.put("FechaInicio", dtpFechaInicio.getValue());
		params.put("FechaFin", dtpFechaFin.getValue());
		Window ventanaCargar = (Window) Executions.createComponents("/formularios/dashboard/ListadoActividades.zul", null, params);
		ventanaCargar.doModal();
	}
	
	@Command
	public void salir(){
		winFechaBusqueda.detach();
	}
}
