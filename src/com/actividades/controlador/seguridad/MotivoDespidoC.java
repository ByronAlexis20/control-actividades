package com.actividades.controlador.seguridad;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.zkoss.bind.BindUtils;
import org.zkoss.bind.annotation.AfterCompose;
import org.zkoss.bind.annotation.Command;
import org.zkoss.bind.annotation.ContextParam;
import org.zkoss.bind.annotation.ContextType;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.select.Selectors;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zk.ui.util.Clients;
import org.zkoss.zul.Messagebox;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

import com.actividades.modelo.Empleado;
import com.actividades.modelo.EmpleadoDAO;
import com.actividades.util.Constantes;
import com.actividades.util.PrintReport;

public class MotivoDespidoC {
	@Wire Textbox txtMotivo;
	@Wire Window winMotivo;
	
	Empleado empleado;
	EmpleadoDAO empleadoDAO = new EmpleadoDAO();
	
	@AfterCompose
	public void afterCompose(@ContextParam(ContextType.VIEW) Component view) {
		Selectors.wireComponents(view, this, false);
		// Recupera el objeto pasado como parametro. 
		empleado = (Empleado) Executions.getCurrent().getArg().get("Empleado");
	}
	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Command
	public void grabar(){
		Messagebox.show("Desea realizar el despido?", "Confirmación", Messagebox.YES | Messagebox.NO, Messagebox.QUESTION, new EventListener() {
			@Override
			public void onEvent(Event event) throws Exception {
				if (event.getName().equals("onYes")) {		
					try {
						imprimirActividades(empleado);
						empleadoDAO.getEntityManager().getTransaction().begin();
						empleado.setMotivoSalida(txtMotivo.getText());
						empleado.setFechaSalida(new Date());
						empleado.setEstado("I");
						empleadoDAO.getEntityManager().merge(empleado);
						empleadoDAO.getEntityManager().getTransaction().commit();
						Clients.showNotification("Proceso Ejecutado con exito.");
						BindUtils.postGlobalCommand(null, null, "Empleado.buscarEmpleado", null);
						salir();						
					} catch (Exception e) {
						e.printStackTrace();
						empleadoDAO.getEntityManager().getTransaction().rollback();
					}
				}
			}
		});
	}	
	private void imprimirActividades(Empleado emp) {
		if(emp.getCargo().getIdCargo() == Constantes.ID_CARGO_JEFE) {
			System.out.println("Debe imprimir el reporte");
			System.out.println(emp.getIdEmpleado());
			Map<String, Object> params = new HashMap<String, Object>();
			params.put("ID_TIPO_ACTIVIDAD", Constantes.ID_TIPO_PRIMORDIALES);
			params.put("FECHA_INICIO", emp.getFechaIngreso());
			params.put("FECHA_FIN", new Date());
			params.put("FECHA_BUSQUEDA", "Fecha de contrato: Del " + new SimpleDateFormat("dd/MM/yyyy").format(emp.getFechaIngreso()) + " hasta " + new SimpleDateFormat("dd/MM/yyyy").format(new Date()));
			params.put("ID_EMPLEADO", emp.getIdEmpleado());
			PrintReport report = new PrintReport();
			report.crearReporte("/reportes/actividades.jasper",empleadoDAO, params);
		}
	}
	public Empleado getEmpleado() {
		return empleado;
	}
	public void setEmpleado(Empleado empleado) {
		this.empleado = empleado;
	}
	@Command
	public void salir(){
		winMotivo.detach();
	}
}