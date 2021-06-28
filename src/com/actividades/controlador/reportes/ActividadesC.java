package com.actividades.controlador.reportes;

import java.io.IOException;
import java.util.ArrayList;
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
import org.zkoss.zk.ui.select.Selectors;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zk.ui.util.Clients;
import org.zkoss.zul.ListModelList;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Messagebox;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

import com.actividades.modelo.Agenda;
import com.actividades.modelo.AgendaDAO;
import com.actividades.modelo.Departamento;
import com.actividades.modelo.DepartamentoDAO;
import com.actividades.modelo.Empleado;
import com.actividades.modelo.EmpleadoDAO;

public class ActividadesC {
	@Wire private Listbox lstDepartamento;
	@Wire private Listbox lstAgendas;
	@Wire private Textbox txtDepartamento;
	@Wire private Textbox txtJefe;
	String textoBuscar;
	
	private List<DepartamentoResponsable> listaDepartamentos;
	private DepartamentoResponsable departamentoSeleccionado;
	private List<Agenda> listaAgendas;
	
	DepartamentoDAO departamentoDAO = new DepartamentoDAO();
	EmpleadoDAO empleadoDAO = new EmpleadoDAO();
	AgendaDAO agendaDAO = new AgendaDAO();
	
	@AfterCompose
	public void aferCompose(@ContextParam(ContextType.VIEW) Component view) throws IOException{
		Selectors.wireComponents(view, this, false);
		listaDepartamentos = new ArrayList<>();
		listaAgendas = new ArrayList<>();
		textoBuscar="";
		buscar();
		limpiarCampos();
		
	}
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	private void limpiarCampos() {
		txtDepartamento.setText("");
		txtJefe.setText("");
		
		listaAgendas = new ArrayList<>();
		lstAgendas.setModel(new ListModelList(listaAgendas));
	}
	@SuppressWarnings({ "unchecked", "rawtypes" })
	@GlobalCommand("Departamento.buscarActivosReporte")
	@Command
	@NotifyChange({"listaDepartamentos"})
	public void buscar() {
		if(listaDepartamentos != null)
			listaDepartamentos = null;
		List<DepartamentoResponsable> lista = new ArrayList<>();
		List<Departamento> resultado = departamentoDAO.getDepartamentosReporte(textoBuscar);
		for(Departamento dep : resultado) {
			DepartamentoResponsable add = new DepartamentoResponsable();
			add.setDepartamento(dep);
			List<Empleado> empleadoBuscar = empleadoDAO.buscarPorDepartamento(dep.getIdDepartamento()); 
			if(empleadoBuscar.size() > 0) {
				add.setEmpleado(empleadoBuscar.get(0));
				add.setNombreJefe(empleadoBuscar.get(0).getPersona().getNombre() + " " + empleadoBuscar.get(0).getPersona().getApellido());
				lista.add(add);
			}
			
		}
		listaDepartamentos = lista;
		lstDepartamento.setModel(new ListModelList(listaDepartamentos));
	}
	
	@Command
	public void seleccionarDepartamento() {
		try {
			if(departamentoSeleccionado == null) {
				Messagebox.show("Debe seleccionar un departamento");
				return;
			}
			txtDepartamento.setText(departamentoSeleccionado.getDepartamento().getNombre());
			txtJefe.setText(departamentoSeleccionado.getNombreJefe());
			
			BindUtils.postGlobalCommand(null, null, "Agenda.buscarPorEmpleadoLogeado", null);
		}catch(Exception ex) {
			System.out.println(ex.getMessage());
		}
	}
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	@GlobalCommand("Agenda.buscarPorEmpleadoLogeado")
	@Command
	@NotifyChange({"listaAgendas"})
	public void cargarAgenda() {
		if(listaAgendas != null)
			listaAgendas = null;
		
		listaAgendas = agendaDAO.obtenerAgendaActiva(departamentoSeleccionado.getEmpleado().getIdEmpleado());
		lstAgendas.setModel(new ListModelList(listaAgendas));
	}
	
	
	@Command
	public void verActividades(@BindingParam("agenda") Agenda seleccion){
		if(seleccion == null) {
			Clients.showNotification("Seleccione una opción de la lista.");
			return;
		}
		// Actualiza la instancia antes de enviarla a editar.
		agendaDAO.getEntityManager().refresh(seleccion);		
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("Agenda", seleccion);
		Window ventanaCargar = (Window) Executions.createComponents("/formularios/reportes/actividades/GraficoActividades.zul", null, params);
		ventanaCargar.doModal();
	}
	
	public List<DepartamentoResponsable> getListaDepartamentos() {
		return listaDepartamentos;
	}
	public void setListaDepartamentos(List<DepartamentoResponsable> listaDepartamentos) {
		this.listaDepartamentos = listaDepartamentos;
	}
	public DepartamentoResponsable getDepartamentoSeleccionado() {
		return departamentoSeleccionado;
	}
	public void setDepartamentoSeleccionado(DepartamentoResponsable departamentoSeleccionado) {
		this.departamentoSeleccionado = departamentoSeleccionado;
	}
	public List<Agenda> getListaAgendas() {
		return listaAgendas;
	}
	public void setListaAgendas(List<Agenda> listaAgendas) {
		this.listaAgendas = listaAgendas;
	}
	public String getTextoBuscar() {
		return textoBuscar;
	}
	public void setTextoBuscar(String textoBuscar) {
		this.textoBuscar = textoBuscar;
	}

	public class DepartamentoResponsable {
		private Departamento departamento;
		private Empleado empleado;
		private String nombreJefe;
		
		public Departamento getDepartamento() {
			return departamento;
		}
		public void setDepartamento(Departamento departamento) {
			this.departamento = departamento;
		}
		public Empleado getEmpleado() {
			return empleado;
		}
		public void setEmpleado(Empleado empleado) {
			this.empleado = empleado;
		}
		public String getNombreJefe() {
			return nombreJefe;
		}
		public void setNombreJefe(String nombreJefe) {
			this.nombreJefe = nombreJefe;
		}
	}
}
