package com.actividades.controlador.administracion;

import java.util.List;

import org.zkoss.bind.BindUtils;
import org.zkoss.bind.annotation.AfterCompose;
import org.zkoss.bind.annotation.Command;
import org.zkoss.bind.annotation.ContextParam;
import org.zkoss.bind.annotation.ContextType;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.select.Selectors;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zk.ui.util.Clients;
import org.zkoss.zul.Messagebox;
import org.zkoss.zul.Messagebox.ClickEvent;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

import com.actividades.modelo.Departamento;
import com.actividades.modelo.DepartamentoDAO;

public class DepartamentoEditarC {
	@Wire private Window winDepartamentoEditar;
	@Wire private Textbox txtNombre;
	@Wire private Textbox txtDescripcion;
	@Wire private Textbox txtCodigo;
	
	DepartamentoDAO departamentoDAO = new DepartamentoDAO();
	Departamento departamento;
	
	@AfterCompose
	public void afterCompose(@ContextParam(ContextType.VIEW) Component view) {
		Selectors.wireComponents(view, this, false);
		// Recupera el objeto pasado como parametro. 
		departamento = (Departamento) Executions.getCurrent().getArg().get("Departamento");
		if (departamento == null) {
			departamento = new Departamento();
			departamento.setEstado("A");
		} 
	}
	
	@Command
	public void grabar(){
		if(validarDatos() == true) {
			EventListener<ClickEvent> clickListener = new EventListener<Messagebox.ClickEvent>() {
				public void onEvent(ClickEvent event) throws Exception {
					if(Messagebox.Button.YES.equals(event.getButton())) {
						departamentoDAO.getEntityManager().getTransaction().begin();
						departamento.setEstado("A");
						if(departamento.getIdDepartamento() == null) {//es un nuevo
							departamentoDAO.getEntityManager().persist(departamento);
						}else {//es modificacion
							departamentoDAO.getEntityManager().merge(departamento);
						}
						departamentoDAO.getEntityManager().getTransaction().commit();
						Messagebox.show("Datos grabados con exito");
						BindUtils.postGlobalCommand(null, null, "Departamento.buscarPorPatron", null);
						salir();
					}
				}
			};
			Messagebox.show("¿Desea Grabar los Datos?", "Confirmación", new Messagebox.Button[]{
					Messagebox.Button.YES, Messagebox.Button.NO }, Messagebox.QUESTION, clickListener);
		}
	}
	public boolean validarDatos() {
		if(txtCodigo.getText().toString().isEmpty()) {
			Clients.showNotification("Debe registrar el código del departamento","info",txtCodigo,"end_center",2000);
			txtCodigo.setFocus(true);
			return false;
		}
		if(txtNombre.getText().toString().isEmpty()) {
			Clients.showNotification("Debe registrar el nombre del departamento","info",txtNombre,"end_center",2000);
			txtNombre.setFocus(true);
			return false;
		}
		//validar departamento
		if(departamento.getIdDepartamento() == null) {
			List<Departamento> listaDepar = departamentoDAO.getDepartamentoPorCodigo(departamento.getCodigo());
			if(listaDepar.size() > 0) {
				Clients.showNotification("Código ya existe","info",txtCodigo,"end_center",2000);
				txtCodigo.setFocus(true);
				return false;
			}
		}else {
			List<Departamento> listaDepar = departamentoDAO.getDepartamentoPorCodigoDiferenteId(departamento.getCodigo(), departamento.getIdDepartamento());
			if(listaDepar.size() > 0) {
				Clients.showNotification("Código ya existe","info",txtCodigo,"end_center",2000);
				txtCodigo.setFocus(true);
				return false;
			}
		}
		return true;
	}
	@Command
	public void salir(){
		winDepartamentoEditar.detach();
	}
	public Departamento getDepartamento() {
		return departamento;
	}
	public void setDepartamento(Departamento departamento) {
		this.departamento = departamento;
	}
}
