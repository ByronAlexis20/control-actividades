package com.actividades.controlador;

import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.select.SelectorComposer;
import org.zkoss.zk.ui.select.annotation.Listen;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zk.ui.util.Clients;
import org.zkoss.zul.Messagebox;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;
import org.zkoss.zul.Messagebox.ClickEvent;

import com.actividades.modelo.Cargo;
import com.actividades.modelo.CargoDAO;


@SuppressWarnings("serial")
public class ADCargoEditarC extends SelectorComposer<Component>{
	@Wire private Window winCargoEditar;
	@Wire private Textbox txtDescripcion;
	
	CargoDAO cargoDAO = new CargoDAO();
	ADCargoC cargoC;
	Cargo cargo;
	
	@Override
	public void doAfterCompose(Component comp) throws Exception{
		super.doAfterCompose(comp);
		try {
			cargoC = (ADCargoC)Executions.getCurrent().getArg().get("VentanaPadre");
			cargo = null;	
			if (Executions.getCurrent().getArg().get("Cargo") != null) {
				cargo = (Cargo) Executions.getCurrent().getArg().get("Cargo");
			}else {
				cargo = new Cargo();
				cargo.setEstado("A");
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
						cargoDAO.getEntityManager().getTransaction().begin();
						cargo.setEstado("A");
						if(cargo.getIdCargo() == null) {//es un nuevo
							cargoDAO.getEntityManager().persist(cargo);
						}else {//es modificacion
							cargoDAO.getEntityManager().merge(cargo);
						}
						cargoDAO.getEntityManager().getTransaction().commit();
						Messagebox.show("Datos grabados con exito");
						cargoC.buscarCargo("");
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
	@Listen("onClick=#btnSalir")
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
