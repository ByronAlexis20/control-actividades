package com.actividades.controlador.administracion;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.zkoss.bind.BindUtils;
import org.zkoss.bind.annotation.AfterCompose;
import org.zkoss.bind.annotation.BindingParam;
import org.zkoss.bind.annotation.Command;
import org.zkoss.bind.annotation.ContextParam;
import org.zkoss.bind.annotation.ContextType;
import org.zkoss.bind.annotation.GlobalCommand;
import org.zkoss.bind.annotation.NotifyChange;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.select.Selectors;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zk.ui.util.Clients;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Messagebox;
import org.zkoss.zul.Window;

import com.actividades.modelo.Cargo;
import com.actividades.modelo.CargoDAO;

public class CargoListaC {
	public String textoBuscar;
	@Wire private Listbox ltsCargos;
	
	CargoDAO cargoDAO = new CargoDAO();
	List<Cargo> cargoLista;
	private Cargo cargoSeleccionado;
	
	@AfterCompose
	public void aferCompose(@ContextParam(ContextType.VIEW) Component view) throws IOException{
		Selectors.wireComponents(view, this, false);
		textoBuscar="";
		buscar();
	}
	
	@GlobalCommand("Cargo.buscarPorPatron")
	@NotifyChange({"cargoLista", "cargoSeleccionado"})
	@Command
	public void buscar(){
		if (cargoLista != null)
			cargoLista = null; 
		cargoLista = cargoDAO.getListaCargos(textoBuscar);
		if(cargoLista.size() == 0) {
			Clients.showNotification("No hay datos para mostrar.!!");
		}else {
			cargoSeleccionado = null;
		}
	}
	
	@Command
	public void nuevo(){
		Window ventanaCargar = (Window) Executions.createComponents("/formularios/administracion/cargo/CargoEditar.zul", null, null);
		ventanaCargar.doModal();
	}
	@Command
	public void editar(@BindingParam("cargo") Cargo car){
		if(car == null) {
			Clients.showNotification("Seleccione una opción de la lista.");
			return;
		}
		cargoDAO.getEntityManager().refresh(car);		
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("Cargo", car);
		Window ventanaCargar = (Window) Executions.createComponents("/formularios/administracion/cargo/CargoEditar.zul", null, params);
		ventanaCargar.doModal();

	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Command
	public void eliminar(@BindingParam("cargo") Cargo car){
		if (car == null) {
			Messagebox.show("Debe seleccionar un cargo para eliminar!");
			return; 
		}
		Messagebox.show("Desea eliminar el registro seleccionado?", "Confirmación de Eliminación", Messagebox.YES | Messagebox.NO, Messagebox.QUESTION, new EventListener() {	
			@Override
			public void onEvent(Event event) throws Exception {
				if (event.getName().equals("onYes")) {
					try {
						car.setEstado("I");
						cargoDAO.getEntityManager().getTransaction().begin();
						cargoDAO.getEntityManager().merge(car);
						cargoDAO.getEntityManager().getTransaction().commit();
						Messagebox.show("Transaccion ejecutada con exito");
						BindUtils.postGlobalCommand(null, null, "Cargo.buscarPorPatron", null);
					} catch (Exception e) {
						e.printStackTrace();
						cargoDAO.getEntityManager().getTransaction().rollback();
					}
				}
			}
		});	
	}
	public List<Cargo> getCargoLista() {
		return cargoLista;
	}
	public void setCargoLista(List<Cargo> cargoLista) {
		this.cargoLista = cargoLista;
	}
	public Cargo getCargoSeleccionado() {
		return cargoSeleccionado;
	}
	public void setCargoSeleccionado(Cargo cargoSeleccionado) {
		this.cargoSeleccionado = cargoSeleccionado;
	}

	public String getTextoBuscar() {
		return textoBuscar;
	}

	public void setTextoBuscar(String textoBuscar) {
		this.textoBuscar = textoBuscar;
	}
	
}
