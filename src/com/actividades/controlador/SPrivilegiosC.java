package com.actividades.controlador;

import java.util.ArrayList;
import java.util.List;

import org.zkoss.bind.BindUtils;
import org.zkoss.bind.annotation.AfterCompose;
import org.zkoss.bind.annotation.Command;
import org.zkoss.bind.annotation.ContextParam;
import org.zkoss.bind.annotation.ContextType;
import org.zkoss.bind.annotation.GlobalCommand;
import org.zkoss.bind.annotation.NotifyChange;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.select.Selectors;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zk.ui.util.Clients;
import org.zkoss.zul.ListModelList;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Messagebox;

import com.actividades.modelo.Empleado;
import com.actividades.modelo.EmpleadoDAO;
import com.actividades.util.Constantes;
import com.actividades.util.SecurityUtil;

public class SPrivilegiosC {
	@Wire Listbox lstEmpleados;
	List<EmpleadosPresentar> listaEmpleados;
	EmpleadosPresentar empleadoSeleccionado;
	
	EmpleadoDAO empleadoDAO = new EmpleadoDAO();
	@AfterCompose
	public void afterCompose(@ContextParam(ContextType.VIEW) Component view) {
		// Permite enlazar los componentes que se asocian con la anotacion @Wire
		Selectors.wireComponents(view, this, false);
		buscar();
	}
	
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	@GlobalCommand("Empleado.buscarPorDepartamentoLogueado")
	@Command
	@NotifyChange({"listaEmpleados"})
	public void buscar(){
		if (listaEmpleados != null) {
			listaEmpleados = null; 
		}
		Empleado usuario = empleadoDAO.getUsuario(SecurityUtil.getUser().getUsername().trim());
		List<EmpleadosPresentar> listaPresentar = new ArrayList<>();
		List<Empleado> lista = empleadoDAO.buscarEmpleadoPorDepartamentoLogueado(usuario.getDepartamento().getIdDepartamento());
		for(Empleado emp : lista) {
			EmpleadosPresentar add = new EmpleadosPresentar();
			add.setEmpleado(emp);
			if(emp.getPermiso() != null) {
				if(emp.getPermiso().equals(Constantes.USUARIO_PERMITIDO))
					add.setEstado(Constantes.USUARIO_PERMITIDO);
				else
					add.setEstado(Constantes.USUARIO_NO_PERMITIDO);	
			}else {
				add.setEstado(Constantes.USUARIO_NO_PERMITIDO);
			}
			listaPresentar.add(add);
		}
		listaEmpleados = listaPresentar;
		lstEmpleados.setModel(new ListModelList(listaEmpleados));
		
		if(listaEmpleados.size() == 0) {
			Clients.showNotification("No hay datos para mostrar.!!");
		}else {
			empleadoSeleccionado = null;
		}		
	}
	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Command
	public void permitir() {
		if (empleadoSeleccionado == null) {
			Messagebox.show("Debe seleccionar un empleado");
			return; 
		}
		Messagebox.show("¿Desea Permitir a Empleado acceder al mantenimiento de actividades del departamento?", "Confirmación", Messagebox.YES | Messagebox.NO, Messagebox.QUESTION, new EventListener() {
			@Override
			public void onEvent(Event event) throws Exception {
				if (event.getName().equals("onYes")) {
					try {
						Empleado empleado = new Empleado();
						empleado = empleadoSeleccionado.getEmpleado();
						empleadoDAO.getEntityManager().getTransaction().begin();
						empleado.setPermiso(Constantes.USUARIO_PERMITIDO);
						empleadoDAO.getEntityManager().merge(empleado);
						empleadoDAO.getEntityManager().getTransaction().commit();;
						Clients.showNotification("Transaccion ejecutada con exito.");

						// Actualiza la lista
						BindUtils.postGlobalCommand(null, null, "Empleado.buscarPorDepartamentoLogueado", null);

					} catch (Exception e) {
						e.printStackTrace();
						empleadoDAO.getEntityManager().getTransaction().rollback();
					}
				}
			}
		});		
	}
	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Command
	public void quitarPermiso() {
		if (empleadoSeleccionado == null) {
			Messagebox.show("Debe seleccionar un empleado");
			return; 
		}
		Messagebox.show("¿Desea Quitar a Empleado acceder al mantenimiento de actividades del departamento?", "Confirmación", Messagebox.YES | Messagebox.NO, Messagebox.QUESTION, new EventListener() {
			@Override
			public void onEvent(Event event) throws Exception {
				if (event.getName().equals("onYes")) {
					try {
						Empleado empleado = new Empleado();
						empleado = empleadoSeleccionado.getEmpleado();
						empleadoDAO.getEntityManager().getTransaction().begin();
						empleado.setPermiso(Constantes.USUARIO_NO_PERMITIDO);
						empleadoDAO.getEntityManager().merge(empleado);
						empleadoDAO.getEntityManager().getTransaction().commit();;
						Clients.showNotification("Transaccion ejecutada con exito.");

						// Actualiza la lista
						BindUtils.postGlobalCommand(null, null, "Empleado.buscarPorDepartamentoLogueado", null);

					} catch (Exception e) {
						e.printStackTrace();
						empleadoDAO.getEntityManager().getTransaction().rollback();
					}
				}
			}
		});		
	}
	public List<EmpleadosPresentar> getListaEmpleados() {
		return listaEmpleados;
	}
	public void setListaEmpleados(List<EmpleadosPresentar> listaEmpleados) {
		this.listaEmpleados = listaEmpleados;
	}
	
	public EmpleadosPresentar getEmpleadoSeleccionado() {
		return empleadoSeleccionado;
	}

	public void setEmpleadoSeleccionado(EmpleadosPresentar empleadoSeleccionado) {
		this.empleadoSeleccionado = empleadoSeleccionado;
	}

	public class EmpleadosPresentar {
		private Empleado empleado;
		private String estado;
		public Empleado getEmpleado() {
			return empleado;
		}
		public void setEmpleado(Empleado empleado) {
			this.empleado = empleado;
		}
		public String getEstado() {
			return estado;
		}
		public void setEstado(String estado) {
			this.estado = estado;
		}
	}
}
