package com.actividades.controlador.reportes;

import java.io.IOException;
import java.util.ArrayList;
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
import org.zkoss.zk.ui.select.Selectors;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zk.ui.util.Clients;
import org.zkoss.zul.Combobox;
import org.zkoss.zul.Datebox;
import org.zkoss.zul.ListModelList;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Messagebox;
import org.zkoss.zul.Radio;
import org.zkoss.zul.Textbox;

import com.actividades.modelo.ClaseActividad;
import com.actividades.modelo.ClaseActividadDAO;
import com.actividades.modelo.Departamento;
import com.actividades.modelo.DepartamentoDAO;
import com.actividades.modelo.Empleado;
import com.actividades.modelo.EmpleadoDAO;
import com.actividades.util.Constantes;
import com.actividades.util.PrintReport;

public class ActPorTipoAdminC {
	String textoBuscar;
	@Wire private Listbox lstDepartamento;
	private List<DepartamentoResponsable> listaDepartamentos;
	private DepartamentoResponsable departamentoSeleccionado;
	@Wire private Textbox txtDepartamento;
	@Wire private Textbox txtJefe;
	
	@Wire Combobox cboTipoActivivdad;
	@Wire Datebox dtpFechaInicio;
	@Wire Datebox dtpFechaFin;
	@Wire Radio rbInternas;
	@Wire Radio rbPrincipales;
	
	DepartamentoDAO departamentoDAO = new DepartamentoDAO();
	EmpleadoDAO empleadoDAO = new EmpleadoDAO();
	ClaseActividadDAO claseActividadDAO = new ClaseActividadDAO();
	
	@AfterCompose
	public void aferCompose(@ContextParam(ContextType.VIEW) Component view) throws IOException{
		Selectors.wireComponents(view, this, false);
		listaDepartamentos = new ArrayList<>();
		textoBuscar="";
		buscar();
		limpiarCampos();
		
	}
	private void limpiarCampos() {
		txtDepartamento.setText("");
		txtJefe.setText("");
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
		}catch(Exception ex) {
			System.out.println(ex.getMessage());
		}
	}
	@Command
	public void imprimir() {
		try {
			if(departamentoSeleccionado == null) {
				Clients.showNotification("Debe seleccionar Departamento");
				return;
			}
			if(cboTipoActivivdad.getSelectedItem().getValue() == null) {
				Clients.showNotification("Debe seleccionar Tipo de Actividad","info",cboTipoActivivdad,"end_center",2000);
				return;
			}
			if(dtpFechaInicio.getValue() == null) {
				Clients.showNotification("Debe seleccionar Fecha de inicio","info",dtpFechaInicio,"end_center",2000);
				return;
			}
			if(dtpFechaFin.getValue() == null) {
				Clients.showNotification("Debe seleccionar Fecha fin","info",dtpFechaFin,"end_center",2000);
				return;
			}
			if(dtpFechaInicio.getValue().after(dtpFechaFin.getValue())) {
				Messagebox.show("Fecha inicio no debe ser mayor a fecha fin");
				return;
			}
			ClaseActividad clase = (ClaseActividad) cboTipoActivivdad.getSelectedItem().getValue();
			Map<String, Object> params = new HashMap<String, Object>();
			params.put("ID_EMPLEADO", departamentoSeleccionado.getEmpleado().getIdEmpleado());
			params.put("FECHA_INICIO", dtpFechaInicio.getValue());
			params.put("FECHA_FIN", dtpFechaFin.getValue());
			if(rbInternas.isSelected()) {
				params.put("ID_TIPO_ACTIVIDAD", Constantes.ID_TIPO_INTERNAS);
			}else {
				params.put("ID_TIPO_ACTIVIDAD", Constantes.ID_TIPO_PRIMORDIALES);
			}
			params.put("ID_CLASE_ACTIVIDAD", clase.getIdClaseActividad());
			params.put("DEPARTAMENTO", departamentoSeleccionado.getDepartamento().getNombre());
			params.put("FUNCIONARIO", departamentoSeleccionado.getEmpleado().getPersona().getNombre() + " " + departamentoSeleccionado.getEmpleado().getPersona().getApellido());
			
			PrintReport report = new PrintReport();
			report.crearReporte("/reportes/rptActividadPorTipo.jasper",departamentoDAO, params);
		}catch(Exception ex) {
			System.out.println(ex.getMessage());
		}
	}
	public List<ClaseActividad> getClaseActividades(){
		return claseActividadDAO.obtenerClaseActividad();
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