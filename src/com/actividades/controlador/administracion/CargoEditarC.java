package com.actividades.controlador.administracion;

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

import com.actividades.modelo.Cargo;
import com.actividades.modelo.CargoDAO;

public class CargoEditarC {
	@Wire private Window winCargoEditar;
	@Wire private Textbox txtDescripcion;
	
	CargoDAO cargoDAO = new CargoDAO();
	Cargo cargo;
	
	@AfterCompose
	public void afterCompose(@ContextParam(ContextType.VIEW) Component view) {
		Selectors.wireComponents(view, this, false);
		// Recupera el objeto pasado como parametro. 
		cargo = (Cargo) Executions.getCurrent().getArg().get("Cargo");
		if (cargo == null) {
			cargo = new Cargo();
			cargo.setEstado("A");
		} 
	}
	
	@Command
	public void grabar(){
		if(validarDatos() == true) {
			EventListener<ClickEvent> clickListener = new EventListener<Messagebox.ClickEvent>() {
				public void onEvent(ClickEvent event) throws Exception {
					if(Messagebox.Button.YES.equals(event.getButton())) {
						cargoDAO.getEntityManager().getTransaction().begin();
						cargo.setEstado("A");
						if(cargo.getIdCargo() == null) {//es un nuevo
							cargoDAO.getEntityManager().persist(cargo);
						}else {//es modificacion
							cargoDAO.getEntityManager().merge(cargo);
						}
						cargoDAO.getEntityManager().getTransaction().commit();
						Messagebox.show("Datos grabados con exito");
						BindUtils.postGlobalCommand(null, null, "Cargo.buscarPorPatron", null);
						salir();
					}
				}
			};
			Messagebox.show("¿Desea Grabar los Datos?", "Confirmación", new Messagebox.Button[]{
					Messagebox.Button.YES, Messagebox.Button.NO }, Messagebox.QUESTION, clickListener);
		}
	}
	public boolean validarDatos() {
		if(txtDescripcion.getText() == "") {
			Clients.showNotification("Debe registrar la descripción del cargo","info",txtDescripcion,"end_center",2000);
			txtDescripcion.setFocus(true);
			return false;
		}
		return true;
	}
	@Command
	public void salir(){
		winCargoEditar.detach();
	}
	public Cargo getCargo() {
		return cargo;
	}
	public void setCargo(Cargo cargo) {
		this.cargo = cargo;
	}
}
