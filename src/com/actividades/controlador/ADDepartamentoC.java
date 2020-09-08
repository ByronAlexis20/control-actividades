package com.actividades.controlador;

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

import com.actividades.modelo.Departamento;
import com.actividades.modelo.DepartamentoDAO;

@SuppressWarnings("serial")
public class ADDepartamentoC extends SelectorComposer<Component>{
	@Wire private Window winDepartamento;
	@Wire private Textbox txtBuscar;
	@Wire private Listbox ltsDepartamentos;
	
	
	DepartamentoDAO departamentoDAO = new DepartamentoDAO();
	List<Departamento> departamentoLista;
	private Departamento departamentoSeleccionado;
	
	@Override
	public void doAfterCompose(Component comp) throws Exception {
		super.doAfterCompose(comp);
		buscarDepartamento("");
	}
	@Listen("onClick=#btnBuscar;onOK=#txtBuscar")
	public void buscar(){
		buscarDepartamento(txtBuscar.getText());
	}
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public void buscarDepartamento(String dato) {
		if (departamentoLista != null)
			departamentoLista = null; 
		departamentoLista = departamentoDAO.getListaDepartamentos(dato);
		System.out.println("Tamaño de lista de departamentos: " + departamentoLista.size());
		ltsDepartamentos.setModel(new ListModelList(departamentoLista));
		departamentoSeleccionado = null;
	}
	@Listen("onClick=#btnNuevo")
	public void nuevo(){
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("Departamento", null);
		params.put("VentanaPadre", this);
		Window ventanaCargar = (Window) Executions.createComponents("/formularios/administracion/departamento/ADDepartamentoEditar.zul", winDepartamento, params);
		ventanaCargar.doModal();
	}
	@Listen("onClick=#btnEditar")
	public void editar(){
		if (departamentoSeleccionado == null) {
			Messagebox.show("Debe seleccionar un tipo departamento para editar!");
			return; 
		}
		// Actualiza la instancia antes de enviarla a editar.
		departamentoDAO.getEntityManager().refresh(departamentoSeleccionado);

		Map<String, Object> params = new HashMap<String, Object>();
		params.put("Departamento", departamentoSeleccionado);
		params.put("VentanaPadre", this);
		Window ventanaCargar = (Window) Executions.createComponents("/formularios/administracion/departamento/ADDepartamentoEditar.zul", winDepartamento, params);
		ventanaCargar.doModal();

	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Listen("onClick=#btnEliminar")
	public void eliminar(){
		if (departamentoSeleccionado == null) {
			Messagebox.show("Debe seleccionar un departamento para eliminar!");
			return; 
		}
		Messagebox.show("Desea eliminar el registro seleccionado?", "Confirmación de Eliminación", Messagebox.YES | Messagebox.NO, Messagebox.QUESTION, new EventListener() {	
			@Override
			public void onEvent(Event event) throws Exception {
				if (event.getName().equals("onYes")) {
					try {
						departamentoSeleccionado.setEstado("I");
						departamentoDAO.getEntityManager().getTransaction().begin();
						departamentoDAO.getEntityManager().merge(departamentoSeleccionado);
						departamentoDAO.getEntityManager().getTransaction().commit();
						Messagebox.show("Transaccion ejecutada con exito");
						buscar();
					} catch (Exception e) {
						e.printStackTrace();
						departamentoDAO.getEntityManager().getTransaction().rollback();
					}
				}
			}
		});	
	}
	public List<Departamento> getDepartamentoLista() {
		return departamentoLista;
	}
	public void setDepartamentoLista(List<Departamento> departamentoLista) {
		this.departamentoLista = departamentoLista;
	}
	public Departamento getDepartamentoSeleccionado() {
		return departamentoSeleccionado;
	}
	public void setDepartamentoSeleccionado(Departamento departamentoSeleccionado) {
		this.departamentoSeleccionado = departamentoSeleccionado;
	}
	
}
