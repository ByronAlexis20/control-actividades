package com.actividades.controlador;

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
import org.zkoss.zul.ListModelList;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Messagebox;
import org.zkoss.zul.Window;

import com.actividades.modelo.Actividad;
import com.actividades.modelo.Evidencia;
import com.actividades.modelo.EvidenciaDAO;
import com.actividades.util.Constantes;
import com.actividades.util.FileUtil;

public class AEvidenciaListaC {
	@Wire Window winEvidenciaLista;
	@Wire Listbox lstEvidencia;
	
	EvidenciaDAO evidenciaDAO = new EvidenciaDAO();
	
	List<Evidencia> listaEvidencia;
	Evidencia evidenciaSeleccionada;
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
		evidenciaSeleccionada = null;	
	}
	@Command
	public void nuevaEvidencia() {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("Ventana", this);
		params.put("Actividad", actividad);
		Window ventanaCargar = (Window) Executions.createComponents("/formularios/actividades/diaria/AEvidencia.zul", winEvidenciaLista, params);
		ventanaCargar.doModal();
	}
	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Command
	public void eliminarEvidencia() {
		if (evidenciaSeleccionada == null) {
			Messagebox.show("Debe seleccionar una evidencia");
			return; 
		}
		Messagebox.show("Desea eliminar el registro seleccionado?", "Confirmación de Eliminación", Messagebox.YES | Messagebox.NO, Messagebox.QUESTION, new EventListener() {	
			@Override
			public void onEvent(Event event) throws Exception {
				if (event.getName().equals("onYes")) {
					try {
						evidenciaSeleccionada.setEstado(Constantes.ESTADO_INACTIVO);
						evidenciaDAO.getEntityManager().getTransaction().begin();
						evidenciaDAO.getEntityManager().merge(evidenciaSeleccionada);
						evidenciaDAO.getEntityManager().getTransaction().commit();
						Messagebox.show("Transaccion ejecutada con exito");
						BindUtils.postGlobalCommand(null, null, "Evidencia.buscarPorActividad", null);
					} catch (Exception e) {
						e.printStackTrace();
						evidenciaDAO.getEntityManager().getTransaction().rollback();
					}
				}
			}
		});	
	}
	
	@Command
	public void descargar(@BindingParam("evidencia") Evidencia seleccion) {
		if (seleccion.getRutaArchivo() != null) {
			FileUtil.descargaArchivo(seleccion.getRutaArchivo());
		}
	}
	@Command
	public void salir() {
		winEvidenciaLista.detach();
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
	public Evidencia getEvidenciaSeleccionada() {
		return evidenciaSeleccionada;
	}
	public void setEvidenciaSeleccionada(Evidencia evidenciaSeleccionada) {
		this.evidenciaSeleccionada = evidenciaSeleccionada;
	}
}
