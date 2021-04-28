package com.actividades.controlador.seguridad;

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

import com.actividades.modelo.Empleado;
import com.actividades.modelo.EmpleadoDAO;

public class EmpleadosListaC {
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
	
	@GlobalCommand("Empleado.buscarEmpleado")
	@Command
	@NotifyChange({"listaEmpleados", "empleadoSeleccionado"})
	public void buscar(){
		if (listaEmpleados != null) {
			listaEmpleados = null; 
		}
		listaEmpleados = empleadoDAO.buscarEmpleados(textoBuscar);
		if(listaEmpleados.size() == 0) {
			Clients.showNotification("No hay datos para mostrar.!!");
		}else {
			empleadoSeleccionado = null;
		}
	}

	@Command
	public void nuevo(){
		Window ventanaCargar = (Window) Executions.createComponents("/formularios/seguridad/empleado/EmpleadosEditar.zul", null, null);
		ventanaCargar.doModal();
	}

	@Command
	public void editar(@BindingParam("empleado") Empleado emp){
		if(emp == null) {
			Clients.showNotification("Seleccione una opción de la lista.");
			return;
		}
		// Actualiza la instancia antes de enviarla a editar.
		empleadoDAO.getEntityManager().refresh(emp);		
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("Empleado", emp);
		Window ventanaCargar = (Window) Executions.createComponents("/formularios/seguridad/empleado/EmpleadosEditar.zul", null, params);
		ventanaCargar.doModal();
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Command
	public void eliminar(@BindingParam("empleado") Empleado emp){

		if (emp == null) {
			Clients.showNotification("Seleccione una opción de la lista.");
			return; 
		}
		Messagebox.show("Desea eliminar el registro seleccionado?", "Confirmación de Eliminación", Messagebox.YES | Messagebox.NO, Messagebox.QUESTION, new EventListener() {
			@Override
			public void onEvent(Event event) throws Exception {
				if (event.getName().equals("onYes")) {
					try {
						empleadoDAO.getEntityManager().getTransaction().begin();
						emp.setEstado("I");
						empleadoDAO.getEntityManager().merge(emp);
						empleadoDAO.getEntityManager().getTransaction().commit();;
						Clients.showNotification("Transaccion ejecutada con exito.");
						// Actualiza la lista
						BindUtils.postGlobalCommand(null, null, "Empleado.buscarEmpleado", null);
					} catch (Exception e) {
						e.printStackTrace();
						empleadoDAO.getEntityManager().getTransaction().rollback();
					}
				}
			}
		});		
	}
	
	public String getTextoBuscar() {
		return textoBuscar;
	}
	public void setTextoBuscar(String textoBuscar) {
		this.textoBuscar = textoBuscar;
	}
	public List<Empleado> getListaEmpleados() {
		return listaEmpleados = empleadoDAO.buscarEmpleados("");
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