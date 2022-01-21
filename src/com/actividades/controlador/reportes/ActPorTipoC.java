package com.actividades.controlador.reportes;

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
import org.zkoss.zul.Messagebox;
import org.zkoss.zul.Radio;

import com.actividades.modelo.Actividad;
import com.actividades.modelo.ActividadDAO;
import com.actividades.modelo.ClaseActividad;
import com.actividades.modelo.ClaseActividadDAO;
import com.actividades.modelo.Empleado;
import com.actividades.modelo.EmpleadoDAO;
import com.actividades.util.Constantes;
import com.actividades.util.PrintReport;
import com.actividades.util.SecurityUtil;

public class ActPorTipoC {
	List<Actividad> listaActividad;
	ClaseActividadDAO claseActividadDAO = new ClaseActividadDAO();
	ActividadDAO actividadDAO = new ActividadDAO();
	EmpleadoDAO usuarioDAO = new EmpleadoDAO();
	@Wire Combobox cboTipoActivivdad;
	@Wire Datebox dtpFechaInicio;
	@Wire Datebox dtpFechaFin;
	@Wire Radio rbInternas;
	@Wire Radio rbPrincipales;
	
	@AfterCompose
	public void afterCompose(@ContextParam(ContextType.VIEW) Component view) {
		Selectors.wireComponents(view, this, false);
	}
	@GlobalCommand("Actividad.reportePorTipoActividad")
	@Command
	@NotifyChange({"listaActividad"})
	public void buscar() {
		try {
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
			Empleado usuario = usuarioDAO.getUsuario(SecurityUtil.getUser().getUsername().trim());
			ClaseActividad clase = (ClaseActividad) cboTipoActivivdad.getSelectedItem().getValue();
			if(rbInternas.isSelected()) {
				listaActividad = actividadDAO.reportePorTipoActividad(dtpFechaInicio.getValue(), dtpFechaFin.getValue(), usuario.getIdEmpleado(), Constantes.ID_TIPO_INTERNAS, clase.getIdClaseActividad());
			}else if(rbPrincipales.isSelected()) {
				listaActividad = actividadDAO.reportePorTipoActividad(dtpFechaInicio.getValue(), dtpFechaFin.getValue(), usuario.getIdEmpleado(), Constantes.ID_TIPO_PRIMORDIALES, clase.getIdClaseActividad());
			}
		}catch(Exception ex) {
			System.out.println(ex.getMessage());
		}
	}
	@Command
	public void imprimir() {
		try {
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
			Empleado usuario = usuarioDAO.getUsuario(SecurityUtil.getUser().getUsername().trim());
			ClaseActividad clase = (ClaseActividad) cboTipoActivivdad.getSelectedItem().getValue();
			Map<String, Object> params = new HashMap<String, Object>();
			params.put("ID_EMPLEADO", usuario.getIdEmpleado());
			params.put("FECHA_INICIO", dtpFechaInicio.getValue());
			params.put("FECHA_FIN", dtpFechaFin.getValue());
			if(rbInternas.isSelected()) {
				params.put("ID_TIPO_ACTIVIDAD", Constantes.ID_TIPO_INTERNAS);
			}else {
				params.put("ID_TIPO_ACTIVIDAD", Constantes.ID_TIPO_PRIMORDIALES);
			}
			params.put("ID_CLASE_ACTIVIDAD", clase.getIdClaseActividad());
			params.put("DEPARTAMENTO", usuario.getDepartamento().getNombre());
			params.put("FUNCIONARIO", usuario.getPersona().getNombre() + " " + usuario.getPersona().getApellido());
			
			PrintReport report = new PrintReport();
			report.crearReporte("/reportes/rptActividadPorTipo.jasper",usuarioDAO, params);
		}catch(Exception ex) {
			System.out.println(ex.getMessage());
		}
	}
	public List<ClaseActividad> getClaseActividades(){
		return claseActividadDAO.obtenerClaseActividad();
	}
	public List<Actividad> getListaActividad() {
		return listaActividad;
	}
	public void setListaActividad(List<Actividad> listaActividad) {
		this.listaActividad = listaActividad;
	}
}