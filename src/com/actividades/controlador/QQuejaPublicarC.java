package com.actividades.controlador;

import java.io.IOException;
import java.util.List;

import org.zkoss.bind.annotation.AfterCompose;
import org.zkoss.bind.annotation.Command;
import org.zkoss.bind.annotation.ContextParam;
import org.zkoss.bind.annotation.ContextType;
import org.zkoss.bind.annotation.GlobalCommand;
import org.zkoss.bind.annotation.NotifyChange;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.select.Selectors;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zk.ui.util.Clients;
import org.zkoss.zul.ListModelList;
import org.zkoss.zul.Listbox;

import com.actividades.modelo.Queja;
import com.actividades.modelo.QuejaDAO;
import com.actividades.util.Constantes;

public class QQuejaPublicarC {
	@Wire Listbox lstQuejas;
	
	List<Queja> listaQueja;
	Queja quejaSeleccionada;
	String textoBuscar;
	QuejaDAO quejaDAO = new QuejaDAO();
	
	@AfterCompose
	public void aferCompose(@ContextParam(ContextType.VIEW) Component view) throws IOException{
		Selectors.wireComponents(view, this, false);
		textoBuscar="";
		buscar();
	}
	@SuppressWarnings({ "unchecked", "rawtypes" })
	@GlobalCommand("Queja.buscarPorResponsable")
	@Command
	@NotifyChange({"listaQueja"})
	public void buscar(){
		if (listaQueja != null) {
			listaQueja = null; 
		}
		List<Queja> lista = quejaDAO.buscarPorEstado(Constantes.QUEJA_REVISION);
		listaQueja = lista;
		lstQuejas.setModel(new ListModelList(listaQueja));
		quejaSeleccionada = null;
		if(listaQueja.size() == 0) {
			Clients.showNotification("No hay datos para mostrar.!!");
		}
	}
	public List<Queja> getListaQueja() {
		return listaQueja;
	}
	public void setListaQueja(List<Queja> listaQueja) {
		this.listaQueja = listaQueja;
	}
	public Queja getQuejaSeleccionada() {
		return quejaSeleccionada;
	}
	public void setQuejaSeleccionada(Queja quejaSeleccionada) {
		this.quejaSeleccionada = quejaSeleccionada;
	}
	public String getTextoBuscar() {
		return textoBuscar;
	}
	public void setTextoBuscar(String textoBuscar) {
		this.textoBuscar = textoBuscar;
	}
	
}
