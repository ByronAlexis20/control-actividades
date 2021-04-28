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
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

import com.actividades.modelo.Departamento;
import com.actividades.modelo.DepartamentoDAO;

public class DepartamentoListaC {
	@Wire private Window winDepartamento;
	@Wire private Textbox txtBuscar;
	@Wire private Listbox ltsDepartamentos;
	private String textoBuscar = "";
	
	DepartamentoDAO departamentoDAO = new DepartamentoDAO();
	List<Departamento> departamentoLista;
	private Departamento departamentoSeleccionado;
	
	@AfterCompose
	public void aferCompose(@ContextParam(ContextType.VIEW) Component view) throws IOException{
		Selectors.wireComponents(view, this, false);
		textoBuscar="";
		buscar();
	}

	@GlobalCommand("Departamento.buscarPorPatron")
	@NotifyChange({"departamentoLista","departamentoSeleccionado"})
	@Command
	public void buscar(){
		if (departamentoLista != null)
			departamentoLista = null; 
		departamentoLista = departamentoDAO.getListaDepartamentos(textoBuscar);
		if(departamentoLista.size() == 0) {
			Clients.showNotification("No hay datos para mostrar.!!");
		}else {
			departamentoSeleccionado = null;
		}
	}
	@Command
	public void nuevo(){
		Window ventanaCargar = (Window) Executions.createComponents("/formularios/administracion/departamento/DepartamentoEditar.zul", null, null);
		ventanaCargar.doModal();
	}
	@Command
	public void editar(@BindingParam("departamento") Departamento dep){
		if (dep == null) {
			Messagebox.show("Debe seleccionar un tipo departamento para editar!");
			return; 
		}
		// Actualiza la instancia antes de enviarla a editar.
		departamentoDAO.getEntityManager().refresh(dep);

		Map<String, Object> params = new HashMap<String, Object>();
		params.put("Departamento", dep);
		Window ventanaCargar = (Window) Executions.createComponents("/formularios/administracion/departamento/DepartamentoEditar.zul", null, params);
		ventanaCargar.doModal();
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Command
	public void eliminar(@BindingParam("departamento") Departamento dep){
		if (dep == null) {
			Messagebox.show("Debe seleccionar un departamento para eliminar!");
			return; 
		}
		Messagebox.show("Desea eliminar el registro seleccionado?", "Confirmación de Eliminación", Messagebox.YES | Messagebox.NO, Messagebox.QUESTION, new EventListener() {	
			@Override
			public void onEvent(Event event) throws Exception {
				if (event.getName().equals("onYes")) {
					try {
						dep.setEstado("I");
						departamentoDAO.getEntityManager().getTransaction().begin();
						departamentoDAO.getEntityManager().merge(dep);
						departamentoDAO.getEntityManager().getTransaction().commit();
						Messagebox.show("Transaccion ejecutada con exito");
						BindUtils.postGlobalCommand(null, null, "Departamento.buscarPorPatron", null);
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
	public String getTextoBuscar() {
		return textoBuscar;
	}
	public void setTextoBuscar(String textoBuscar) {
		this.textoBuscar = textoBuscar;
	}
}