package com.actividades.controlador;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.zkoss.bind.BindUtils;
import org.zkoss.bind.annotation.AfterCompose;
import org.zkoss.bind.annotation.Command;
import org.zkoss.bind.annotation.ContextParam;
import org.zkoss.bind.annotation.ContextType;
import org.zkoss.bind.annotation.GlobalCommand;
import org.zkoss.bind.annotation.NotifyChange;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.select.Selectors;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zul.Datebox;
import org.zkoss.zul.ListModelList;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Messagebox;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

import com.actividades.modelo.Departamento;
import com.actividades.modelo.DepartamentoDAO;
import com.actividades.modelo.Empleado;
import com.actividades.modelo.EmpleadoDAO;
import com.actividades.util.Constantes;
import com.actividades.util.PrintReport;

public class RAActividadesFechaC {
	@Wire private Listbox lstDepartamento;
	private List<DepartamentoResponsable> listaDepartamentos;
	private DepartamentoResponsable departamentoSeleccionado;
	@Wire private Textbox txtDepartamento;
	@Wire private Textbox txtJefe;
	@Wire private Datebox dtpFechaInicio;
	@Wire private Datebox dtpFechaFin;
	
	String textoBuscar;
	DepartamentoDAO departamentoDAO = new DepartamentoDAO();
	EmpleadoDAO empleadoDAO = new EmpleadoDAO();
	
	@AfterCompose
	public void aferCompose(@ContextParam(ContextType.VIEW) Component view) throws IOException{
		Selectors.wireComponents(view, this, false);
		listaDepartamentos = new ArrayList<>();
		textoBuscar="";
		buscar();
		dtpFechaInicio.setValue(new Date());
		dtpFechaFin.setValue(new Date());
		dtpFechaFin.setConstraint("after " + new SimpleDateFormat("yyyyMMdd").format(new Date()));
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
	
	@Command
	public void seleccionaFechaFin() {
		System.out.println(new SimpleDateFormat("yyyyMMdd").format(dtpFechaInicio.getValue()));
		dtpFechaFin.setConstraint("after " + new SimpleDateFormat("yyyyMMdd").format(dtpFechaInicio.getValue()));
		dtpFechaFin.setValue(dtpFechaInicio.getValue());
	}
	
	@Command
	public void cargarActividades() {
		if(departamentoSeleccionado == null) {
			Messagebox.show("Debe seleccionar un departamento!!");
			return;
		}
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("Empleado", departamentoSeleccionado.getEmpleado());
		params.put("FechaInicio", dtpFechaInicio.getValue());
		params.put("FechaFin", dtpFechaFin.getValue());
		Window ventanaCargar = (Window) Executions.createComponents("/formularios/reportes/act_fecha/RAActividadDep.zul", null, params);
		ventanaCargar.doModal();
	}
	@Command
	public void descargarReporte() {
		if(departamentoSeleccionado == null) {
			Messagebox.show("Debe seleccionar un departamento!!");
			return;
		}
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("ID_TIPO_ACTIVIDAD", Constantes.ID_TIPO_PRIMORDIALES);
		params.put("FECHA_INICIO", dtpFechaInicio.getValue());
		params.put("FECHA_FIN", dtpFechaFin.getValue());
		params.put("FECHA_BUSQUEDA", "Fecha de Búsqueda: " + new SimpleDateFormat("dd/MM/yyyy").format(dtpFechaInicio.getValue()) + " - " + new SimpleDateFormat("dd/MM/yyyy").format(dtpFechaFin.getValue()));
		params.put("ID_EMPLEADO", departamentoSeleccionado.getEmpleado().getIdEmpleado());
		PrintReport report = new PrintReport();
		report.crearReporte("/reportes/actividades.jasper",empleadoDAO, params);
	}
	public List<DepartamentoResponsable> getListaDepartamentos() {
		return listaDepartamentos;
	}
	public void setListaDepartamentos(List<DepartamentoResponsable> listaDepartamentos) {
		this.listaDepartamentos = listaDepartamentos;
	}

	public String getTextoBuscar() {
		return textoBuscar;
	}
	public void setTextoBuscar(String textoBuscar) {
		this.textoBuscar = textoBuscar;
	}
	public DepartamentoResponsable getDepartamentoSeleccionado() {
		return departamentoSeleccionado;
	}
	public void setDepartamentoSeleccionado(DepartamentoResponsable departamentoSeleccionado) {
		this.departamentoSeleccionado = departamentoSeleccionado;
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
