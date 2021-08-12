package com.actividades.controlador.reportes;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
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
import org.zkoss.zk.ui.util.Clients;

import com.actividades.modelo.Empleado;
import com.actividades.modelo.EmpleadoDAO;
import com.actividades.util.Constantes;
import com.actividades.util.PrintReport;

public class ActAnterioresC {
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
	
	@GlobalCommand("Empleado.buscarEmpleadoJefesInactivos")
	@Command
	@NotifyChange({"listaEmpleados", "empleadoSeleccionado"})
	public void buscar(){
		if (listaEmpleados != null) {
			listaEmpleados = null; 
		}
		listaEmpleados = empleadoDAO.buscarEmpleadosJefesInactivos(textoBuscar);
		if(listaEmpleados.size() == 0) {
			Clients.showNotification("No hay datos para mostrar.!!");
		}else {
			empleadoSeleccionado = null;
		}
		System.out.println("entra a buscar empleado");
	}

	@Command
	public void imprimir(){
		if(empleadoSeleccionado == null) {
			Clients.showNotification("Seleccione una opción de la lista.");
			return;
		}
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("ID_TIPO_ACTIVIDAD", Constantes.ID_TIPO_PRIMORDIALES);
		params.put("FECHA_INICIO", empleadoSeleccionado.getFechaIngreso());
		params.put("FECHA_FIN", new Date());
		Date fechaIngreso = new Date();
		if(empleadoSeleccionado.getFechaIngreso() != null)
			fechaIngreso = empleadoSeleccionado.getFechaIngreso();
		Date fechaSalida = new Date();
		if(empleadoSeleccionado.getFechaSalida() != null)
			fechaIngreso = empleadoSeleccionado.getFechaSalida();
		params.put("FECHA_BUSQUEDA", "Fecha de Búsqueda: " + new SimpleDateFormat("dd/MM/yyyy").format(fechaIngreso) + " - " + new SimpleDateFormat("dd/MM/yyyy").format(fechaSalida));
		params.put("ID_EMPLEADO", empleadoSeleccionado.getIdEmpleado());
		PrintReport report = new PrintReport();
		report.crearReporte("/reportes/actividadesAnteriores.jasper",empleadoDAO, params);
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
