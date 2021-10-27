package com.actividades.controlador.dashboard;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.mail.MessagingException;

import org.zkoss.bind.annotation.AfterCompose;
import org.zkoss.bind.annotation.ContextParam;
import org.zkoss.bind.annotation.ContextType;
import org.zkoss.image.AImage;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.select.Selectors;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zul.Image;
import org.zkoss.zul.Label;

import com.actividades.modelo.Actividad;
import com.actividades.modelo.ActividadDAO;
import com.actividades.modelo.Empleado;
import com.actividades.modelo.EmpleadoDAO;
import com.actividades.util.Constantes;
import com.actividades.util.SecurityUtil;

public class DashboardC {
	
	@Wire private Label lblCantidadEmpleados;
	@Wire private Label lblActividadesPendientes;
	@Wire private Label lblActividadesRechazadas;
	@Wire private Label lblQuejaRealizada;
	
	@Wire private Image imGraficoResumenEmergencia;
	EmpleadoDAO usuarioDAO = new EmpleadoDAO();
	ActividadDAO actividadDAO = new ActividadDAO();
	List<Trabajadores> listaEmpleados;
	
	@AfterCompose
	public void aferCompose(@ContextParam(ContextType.VIEW) Component view) throws IOException, MessagingException{
		Selectors.wireComponents(view, this, false);
		contarCantidadEmpleados();
		contarActividades();
	}
	
	private void contarCantidadEmpleados() {
		try {
			Empleado usuario = usuarioDAO.getUsuario(SecurityUtil.getUser().getUsername().trim());
			List<Empleado> lista = usuarioDAO.buscarPorDepartamentoYTipoUsuario(usuario.getDepartamento().getIdDepartamento(), Constantes.ID_ASISTENTE);
			List<Trabajadores> agg = new ArrayList<>();
			for(Empleado emp : lista) {
				Trabajadores t = new Trabajadores();
				t.setEmp(emp);
				if(emp.getFoto() != null) {
					File archivo = new File(emp.getFoto());
					if(archivo.exists())
						t.setFoto(new AImage(emp.getFoto()));
				}
				agg.add(t);
			}
			listaEmpleados = agg;
			lblCantidadEmpleados.setValue(String.valueOf(listaEmpleados.size()));
			
			//contar actividades rechazadas
			lblActividadesRechazadas.setValue("0");
			lblActividadesPendientes.setValue("0");
			List<Empleado> jefeArea = usuarioDAO.buscarPorDepartamento(usuario.getDepartamento().getIdDepartamento());
			if(jefeArea.size() > 0) {
				List<Actividad> acti = actividadDAO.obtenerRechazada(jefeArea.get(0).getIdEmpleado());
				lblActividadesRechazadas.setValue(String.valueOf(acti.size()));
				List<Actividad> pend = actividadDAO.obtenerPendiente(jefeArea.get(0).getIdEmpleado());
				lblActividadesPendientes.setValue(String.valueOf(pend.size()));
			}
		}catch(Exception ex) {
			System.out.println(ex.getMessage());
		}
	}
	
	private void contarActividades() {
		
	}

	public List<Trabajadores> getListaEmpleados() {
		return listaEmpleados;
	}

	public void setListaEmpleados(List<Trabajadores> listaEmpleados) {
		this.listaEmpleados = listaEmpleados;
	}

	public class Trabajadores {
		private Empleado emp;
		private AImage foto;
		public Empleado getEmp() {
			return emp;
		}
		public void setEmp(Empleado emp) {
			this.emp = emp;
		}
		public AImage getFoto() {
			return foto;
		}
		public void setFoto(AImage foto) {
			this.foto = foto;
		}
		
	}
}