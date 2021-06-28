package com.actividades.controlador.reportes;

import java.io.IOException;
import java.util.List;

import org.zkoss.bind.annotation.AfterCompose;
import org.zkoss.bind.annotation.BindingParam;
import org.zkoss.bind.annotation.Command;
import org.zkoss.bind.annotation.ContextParam;
import org.zkoss.bind.annotation.ContextType;
import org.zkoss.bind.annotation.GlobalCommand;
import org.zkoss.bind.annotation.NotifyChange;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.select.Selectors;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zul.ListModelList;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Window;

import com.actividades.modelo.Actividad;
import com.actividades.modelo.Evidencia;
import com.actividades.modelo.EvidenciaDAO;
import com.actividades.util.FileUtil;

public class EvidenciaC {
	@Wire Window winEvidenciaReporte;
	@Wire Listbox lstEvidencia;
	
	EvidenciaDAO evidenciaDAO = new EvidenciaDAO();
	
	List<Evidencia> listaEvidencia;
	Actividad actividad;
	
	@AfterCompose
	public void aferCompose(@ContextParam(ContextType.VIEW) Component view) throws IOException{
		Selectors.wireComponents(view, this, false);
		actividad = (Actividad) Executions.getCurrent().getArg().get("Actividad");
		cargarEvidencias();
	}
	@SuppressWarnings({ "unchecked", "rawtypes" })
	@GlobalCommand("Evidencia.buscarPorActividad")
	@Command
	@NotifyChange({"listaEvidencia"})
	public void cargarEvidencias(){
		if (listaEvidencia != null)
			listaEvidencia = null; 
		listaEvidencia = evidenciaDAO.obtenerEvidencias(actividad.getIdActividad());
		lstEvidencia.setModel(new ListModelList(listaEvidencia));
	}
	
	
	@Command
	public void descargar(@BindingParam("evidencia") Evidencia seleccion) {
		if (seleccion.getRutaArchivo() != null) {
			FileUtil.descargaArchivo(seleccion.getRutaArchivo());
		}
	}
	@Command
	public void salir() {
		winEvidenciaReporte.detach();
	}
	
	public List<Evidencia> getListaEvidencia() {
		return listaEvidencia;
	}
	public void setListaEvidencia(List<Evidencia> listaEvidencia) {
		this.listaEvidencia = listaEvidencia;
	}
	public Actividad getActividad() {
		return actividad;
	}
	public void setActividad(Actividad actividad) {
		this.actividad = actividad;
	}
}
