package com.actividades.controlador.actividades;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.zkoss.bind.annotation.AfterCompose;
import org.zkoss.bind.annotation.Command;
import org.zkoss.bind.annotation.ContextParam;
import org.zkoss.bind.annotation.ContextType;
import org.zkoss.bind.annotation.GlobalCommand;
import org.zkoss.bind.annotation.NotifyChange;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.select.Selectors;
import org.zkoss.zk.ui.util.Clients;
import org.zkoss.zul.Window;

import com.actividades.modelo.Empleado;
import com.actividades.modelo.EmpleadoDAO;
import com.actividades.util.Constantes;

public class EnviarActividadesC {
	public String textoBuscar;
	private EmpleadoDAO empleadoDAO = new EmpleadoDAO();
	private List<Empleado> listaEmpleados;
	private Empleado empleadoSeleccionado;
	
	@AfterCompose
	public void aferCompose(@ContextParam(ContextType.VIEW) Component view) throws IOException{
		Selectors.wireComponents(view, this, false);
		textoBuscar="";
		buscar();
	}
	
	@GlobalCommand("Empleado.buscarPorCargoPatronBusqueda")
	@Command
	@NotifyChange({"listaEmpleados", "empleadoSeleccionado"})
	public void buscar(){
		if (listaEmpleados != null) {
			listaEmpleados = null; 
		}
		listaEmpleados = empleadoDAO.buscarPorCargoPatronBusqueda(Constantes.ID_CARGO_JEFE,textoBuscar);
		if(listaEmpleados.size() == 0) {
			Clients.showNotification("No hay datos para mostrar.!!");
		}else {
			empleadoSeleccionado = null;
		}
		System.out.println("entra a buscar empleado");
	}

	@Command
	public void enviarActividad(){
		if(empleadoSeleccionado == null) {
			Clients.showNotification("Seleccione una opción de la lista.");
			return;
		}
		// Actualiza la instancia antes de enviarla a editar.
		empleadoDAO.getEntityManager().refresh(empleadoSeleccionado);		
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("Empleado", empleadoSeleccionado);
		Window ventanaCargar = (Window) Executions.createComponents("/formularios/actividades/enviar-actividades/registrarActividadJefe.zul", null, params);
		ventanaCargar.doModal();
	}
	
	public String getTextoBuscar() {
		return textoBuscar;
	}
	public void setTextoBuscar(String textoBuscar) {
		this.textoBuscar = textoBuscar;
	}
	public List<Empleado> getListaEmpleados() {
		return listaEmpleados;
	}
	public void setListaEmpleados(List<Empleado> listaEmpleados) {
		this.listaEmpleados = listaEmpleados;
	}
	public Empleado getEmpleadoSeleccionado() {
		return empleadoSeleccionado;
	}
	public void setEmpleadoSeleccionado(Empleado empleadoSeleccionado) {
		this.empleadoSeleccionado = empleadoSeleccionado;
	}
}