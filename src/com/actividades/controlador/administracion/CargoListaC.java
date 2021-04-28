package com.actividades.controlador.administracion;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.select.SelectorComposer;
import org.zkoss.zk.ui.select.annotation.Listen;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zul.ListModelList;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Messagebox;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

import com.actividades.modelo.Cargo;
import com.actividades.modelo.CargoDAO;

@SuppressWarnings("serial")
public class CargoListaC extends SelectorComposer<Component>{
	@Wire private Window winCargo;
	@Wire private Textbox txtBuscar;
	@Wire private Listbox ltsCargos;
	
	
	CargoDAO cargoDAO = new CargoDAO();
	List<Cargo> cargoLista;
	private Cargo cargoSeleccionado;
	
	@Override
	public void doAfterCompose(Component comp) throws Exception {
		super.doAfterCompose(comp);
		buscarCargo("");
	}
	@Listen("onClick=#btnBuscar;onOK=#txtBuscar")
	public void buscar(){
		buscarCargo(txtBuscar.getText());
	}
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public void buscarCargo(String dato) {
		if (cargoLista != null)
			cargoLista = null; 
		cargoLista = cargoDAO.getListaCargos(dato);
		ltsCargos.setModel(new ListModelList(cargoLista));
		cargoSeleccionado = null;
	}
	@Listen("onClick=#btnNuevo")
	public void nuevo(){
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("Cargo", null);
		params.put("VentanaPadre", this);
		Window ventanaCargar = (Window) Executions.createComponents("/formularios/administracion/cargo/ADCargoEditar.zul", winCargo, params);
		ventanaCargar.doModal();
	}
	@Listen("onClick=#btnEditar")
	public void editar(){
		if (cargoSeleccionado == null) {
			Messagebox.show("Debe seleccionar un cargo para editar!");
			return; 
		}
		// Actualiza la instancia antes de enviarla a editar.
		cargoDAO.getEntityManager().refresh(cargoSeleccionado);

		Map<String, Object> params = new HashMap<String, Object>();
		params.put("Cargo", cargoSeleccionado);
		params.put("VentanaPadre", this);
		Window ventanaCargar = (Window) Executions.createComponents("/formularios/administracion/cargo/ADCargoEditar.zul", winCargo, params);
		ventanaCargar.doModal();

	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Listen("onClick=#btnEliminar")
	public void eliminar(){
		if (cargoSeleccionado == null) {
			Messagebox.show("Debe seleccionar un cargo para eliminar!");
			return; 
		}
		Messagebox.show("Desea eliminar el registro seleccionado?", "Confirmación de Eliminación", Messagebox.YES | Messagebox.NO, Messagebox.QUESTION, new EventListener() {	
			@Override
			public void onEvent(Event event) throws Exception {
				if (event.getName().equals("onYes")) {
					try {
						cargoSeleccionado.setEstado("I");
						cargoDAO.getEntityManager().getTransaction().begin();
						cargoDAO.getEntityManager().merge(cargoSeleccionado);
						cargoDAO.getEntityManager().getTransaction().commit();
						Messagebox.show("Transaccion ejecutada con exito");
						buscar();
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
}
