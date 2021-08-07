package com.actividades.controlador.actividades;

import java.util.ArrayList;
import java.util.List;

import org.zkoss.bind.BindContext;
import org.zkoss.bind.BindUtils;
import org.zkoss.bind.annotation.AfterCompose;
import org.zkoss.bind.annotation.Command;
import org.zkoss.bind.annotation.ContextParam;
import org.zkoss.bind.annotation.ContextType;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.event.UploadEvent;
import org.zkoss.zk.ui.select.Selectors;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zk.ui.util.Clients;
import org.zkoss.zul.Button;
import org.zkoss.zul.Combobox;
import org.zkoss.zul.Messagebox;
import org.zkoss.zul.Messagebox.ClickEvent;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

import com.actividades.modelo.Actividad;
import com.actividades.modelo.Evidencia;
import com.actividades.modelo.TipoEvidencia;
import com.actividades.modelo.TipoEvidenciaDAO;
import com.actividades.util.Constantes;
import com.actividades.util.FileUtil;

public class EvidenciaRegistroC {
	@Wire private Window winEvidencia;
	@Wire private Button btnAnadirArchivo;
	@Wire private Combobox cboTipoEvidencia;
	@Wire private Textbox txtArchivo;
	
	private Evidencia evidencia;
	Actividad actividad;
	
	private TipoEvidenciaDAO tipoEvidenciaDAO = new TipoEvidenciaDAO();
	
	@AfterCompose
	public void afterCompose(@ContextParam(ContextType.VIEW) Component view) {
		// Permite enlazar los componentes que se asocian con la anotacion @Wire
		Selectors.wireComponents(view, this, false);
		actividad = (Actividad) Executions.getCurrent().getArg().get("Actividad");
		evidencia = new Evidencia();
		evidencia.setIdEvidencia(null);
		evidencia.setEstado("A");
		//btnAnadirArchivo.setUpload("true,accept=.jpg");
		btnAnadirArchivo.setDisabled(true);
	}
	@Command
	public void cambioFormato() {
		TipoEvidencia tipo = new TipoEvidencia();
		tipo = (TipoEvidencia) cboTipoEvidencia.getSelectedItem().getValue();
		btnAnadirArchivo.setUpload("true,accept=" + tipo.getFormato());
		btnAnadirArchivo.setDisabled(false);
	}
	
	@Command
	public void agregar(@ContextParam(ContextType.BIND_CONTEXT) BindContext contexto) {
		String pathRetornado; 
		UploadEvent eventoCarga = (UploadEvent) contexto.getTriggerEvent();
		pathRetornado = FileUtil.cargaArchivo(eventoCarga.getMedia());
		txtArchivo.setValue(eventoCarga.getMedia().getName());
		evidencia.setRutaArchivo(pathRetornado);
		evidencia.setDescripcion(eventoCarga.getMedia().getName());
		evidencia.setTipoEvidencia((TipoEvidencia) cboTipoEvidencia.getSelectedItem().getValue());
	}

	@Command
	public void grabar(){
		if(validarDatos() == true) {
			EventListener<ClickEvent> clickListener = new EventListener<Messagebox.ClickEvent>() {
				public void onEvent(ClickEvent event) throws Exception {
					if(Messagebox.Button.YES.equals(event.getButton())) {
						tipoEvidenciaDAO.getEntityManager().getTransaction().begin();
						//hacer el enlace con la actividad
						enlazarObjetos();
						if(actividad.getEvidencias().size() > 0) {
							tipoEvidenciaDAO.getEntityManager().persist(evidencia);
						}else {
							tipoEvidenciaDAO.getEntityManager().merge(evidencia);
							actividad.setEstadoActividad(Constantes.ESTADO_REALIZADO);
							tipoEvidenciaDAO.getEntityManager().merge(actividad);
						}

						tipoEvidenciaDAO.getEntityManager().getTransaction().commit();
						Messagebox.show("Datos grabados con exito");
						//aDiaria.cargarAgendas();
						BindUtils.postGlobalCommand(null, null, "Evidencia.buscarPorActividad", null);
						salir();
					}
				}
			};
			Messagebox.show("¿Desea Grabar los Datos?", "Confirmación", new Messagebox.Button[]{
					Messagebox.Button.YES, Messagebox.Button.NO }, Messagebox.QUESTION, clickListener);
		}
	}
	private void enlazarObjetos() {
		if(actividad.getEvidencias().size() > 0) {
			evidencia.setActividad(actividad);
			actividad.getEvidencias().add(evidencia);
		}else {
			evidencia.setActividad(actividad);
			List<Evidencia> lista = new ArrayList<>();
			lista.add(evidencia);
			actividad.setEvidencias(lista);
		}
	}
	private boolean validarDatos() {
		try {
			if(evidencia.getDescripcion().isEmpty()) {
				Clients.showNotification("Debe seleccionar el archivo de evidencia");
				return false;
			}
			return true;
		}catch(Exception ex) {
			return false;
		}
	}
	
	@Command
	public void salir() {
		winEvidencia.detach();
	}
	
	public List<TipoEvidencia> getTipoEvidencia(){
		return tipoEvidenciaDAO.getListaTiposEvidencia("");
	}
	
	public Evidencia getEvidencia() {
		return evidencia;
	}
	public void setEvidencia(Evidencia evidencia) {
		this.evidencia = evidencia;
	}
	public Actividad getActividad() {
		return actividad;
	}
	public void setActividad(Actividad actividad) {
		this.actividad = actividad;
	}
}
