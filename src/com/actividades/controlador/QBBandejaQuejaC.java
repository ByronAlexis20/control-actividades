package com.actividades.controlador;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.zkoss.bind.annotation.AfterCompose;
import org.zkoss.bind.annotation.Command;
import org.zkoss.bind.annotation.ContextParam;
import org.zkoss.bind.annotation.ContextType;
import org.zkoss.bind.annotation.GlobalCommand;
import org.zkoss.bind.annotation.NotifyChange;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.select.Selectors;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zk.ui.util.Clients;
import org.zkoss.zul.Datebox;
import org.zkoss.zul.ListModelList;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Messagebox;
import org.zkoss.zul.Window;

import com.actividades.modelo.Queja;
import com.actividades.modelo.QuejaDAO;
import com.actividades.util.Constantes;

public class QBBandejaQuejaC {
	@Wire Listbox lstQuejas;
	@Wire Datebox dtpFechaInicio;
	@Wire Datebox dtpFechaFin;
	
	List<Queja> listaQuejas;
	Queja quejaSeleccionada;
	QuejaDAO quejaDAO = new QuejaDAO();
	String textoBuscar;
	@AfterCompose
	public void aferCompose(@ContextParam(ContextType.VIEW) Component view) throws IOException{
		Selectors.wireComponents(view, this, false);
		textoBuscar="";
		buscar();
	}
	@SuppressWarnings({ "unchecked", "rawtypes" })
	@GlobalCommand("Queja.buscarQueja")
	@Command
	@NotifyChange({"listaQuejas"})
	public void buscar(){
		if (listaQuejas != null) {
			listaQuejas = null; 
		}
		List<Queja> lista = quejaDAO.buscarQuejas(textoBuscar);
		listaQuejas = lista;
		lstQuejas.setModel(new ListModelList(listaQuejas));
		quejaSeleccionada = null;
		if(listaQuejas.size() == 0) {
			Clients.showNotification("No hay datos para mostrar.!!");
		}
	}
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	@GlobalCommand("Queja.buscarQueja")
	@Command
	@NotifyChange({"listaQuejas"})
	public void buscarPorFechas(){
		if(dtpFechaInicio.getValue() == null) {
			Clients.showNotification("Debe seleccionar fecha Inicio");
			return;
		}
		if(dtpFechaFin.getValue() == null) {
			Clients.showNotification("Debe seleccionar fecha fin");
			return;
		}
		if(dtpFechaInicio.getValue().after(dtpFechaFin.getValue())) {
			Messagebox.show("Fecha inicio no debe ser mayor a fecha fin");
			return;
		}
		if (listaQuejas != null) {
			listaQuejas = null; 
		}
		List<Queja> lista = quejaDAO.buscarQuejasFecha(dtpFechaInicio.getValue(), dtpFechaFin.getValue());
		listaQuejas = lista;
		lstQuejas.setModel(new ListModelList(listaQuejas));
		quejaSeleccionada = null;
		if(listaQuejas.size() == 0) {
			Clients.showNotification("No hay datos para mostrar.!!");
		}
	}
	
	@Command
	public void verQueja() {
		if(quejaSeleccionada == null) {
			Messagebox.show("Debe seleccionar una Queja");
			return;
		}
		if(quejaSeleccionada.getEstadoAtencion().equals(Constantes.QUEJA_ATENDIDA)) {
			Messagebox.show("La queja ha sido atendida");
			return;
		}
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("Ventana", this);
		params.put("Queja", quejaSeleccionada);
		Window ventanaCargar = (Window) Executions.createComponents("/formularios/quejas/bandeja/QuejaAtender.zul", null, params);
		ventanaCargar.doModal();
	}
	
	public List<Queja> getListaQuejas() {
		return listaQuejas;
	}
	public void setListaQuejas(List<Queja> listaQuejas) {
		this.listaQuejas = listaQuejas;
	}
	public String getTextoBuscar() {
		return textoBuscar;
	}
	public void setTextoBuscar(String textoBuscar) {
		this.textoBuscar = textoBuscar;
	}
	public Queja getQuejaSeleccionada() {
		return quejaSeleccionada;
	}
	public void setQuejaSeleccionada(Queja quejaSeleccionada) {
		this.quejaSeleccionada = quejaSeleccionada;
	}
}
