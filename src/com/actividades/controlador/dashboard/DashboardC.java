package com.actividades.controlador.dashboard;

import java.io.IOException;
import java.util.List;

import javax.mail.MessagingException;

import org.zkoss.bind.annotation.AfterCompose;
import org.zkoss.bind.annotation.ContextParam;
import org.zkoss.bind.annotation.ContextType;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.select.Selectors;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zul.Label;

import com.actividades.modelo.Empleado;
import com.actividades.modelo.EmpleadoDAO;
import com.actividades.util.Constantes;
import com.actividades.util.SecurityUtil;

public class DashboardC {
	@Wire
	private Label lblCantidadEmpleados;
	EmpleadoDAO usuarioDAO = new EmpleadoDAO();
	List<Empleado> listaEmpleados;
	
	@AfterCompose
	public void aferCompose(@ContextParam(ContextType.VIEW) Component view) throws IOException, MessagingException{
		Selectors.wireComponents(view, this, false);
		contarCantidadEmpleados();
	}
	
	private void contarCantidadEmpleados() {
		try {
			Empleado usuario = usuarioDAO.getUsuario(SecurityUtil.getUser().getUsername().trim());
			listaEmpleados = usuarioDAO.buscarPorDepartamentoYTipoUsuario(usuario.getDepartamento().getIdDepartamento(), Constantes.ID_ASISTENTE);
			lblCantidadEmpleados.setValue(String.valueOf(listaEmpleados.size()));
			
		}catch(Exception ex) {
			System.out.println(ex.getMessage());
		}
	}

	public List<Empleado> getListaEmpleados() {
		return listaEmpleados;
	}

	public void setListaEmpleados(List<Empleado> listaEmpleados) {
		this.listaEmpleados = listaEmpleados;
	}
	
}
