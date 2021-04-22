package com.actividades.controlador.administracion;

import org.zkoss.bind.BindUtils;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.select.SelectorComposer;
import org.zkoss.zk.ui.select.annotation.Listen;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zk.ui.util.Clients;
import org.zkoss.zul.Messagebox;
import org.zkoss.zul.Messagebox.ClickEvent;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

import com.actividades.modelo.Departamento;
import com.actividades.modelo.DepartamentoDAO;

@SuppressWarnings("serial")
public class DepartamentoEditarC extends SelectorComposer<Component>{
	@Wire private Window winDepartamentoEditar;
	@Wire private Textbox txtNombre;
	@Wire private Textbox txtDescripcion;
	
	DepartamentoDAO departamentoDAO = new DepartamentoDAO();
	DepartamentoListaC departamentoC;
	Departamento departamento;
	
	@Override
	public void doAfterCompose(Component comp) throws Exception{
		super.doAfterCompose(comp);
		try {
			departamentoC = (DepartamentoListaC)Executions.getCurrent().getArg().get("VentanaPadre");
			departamento = null;	
			if (Executions.getCurrent().getArg().get("Departamento") != null) {
				departamento = (Departamento) Executions.getCurrent().getArg().get("Departamento");
			}else {
				departamento = new Departamento();
				departamento.setEstado("A");
			}
		}catch(Exception ex) {
			System.out.println(ex.getMessage());
		}
	}
	@Listen("onClick=#btnGrabar")
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
		if(txtNombre.getText() == "") {
			Clients.showNotification("Debe registrar el nombre del departamento","info",txtNombre,"end_center",2000);
			txtNombre.setFocus(true);
			return false;
		}
		return true;
	}
	@Listen("onClick=#btnSalir")
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
