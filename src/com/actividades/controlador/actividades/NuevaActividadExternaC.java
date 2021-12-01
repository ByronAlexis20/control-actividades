package com.actividades.controlador.actividades;

import java.util.List;

import org.zkoss.bind.BindUtils;
import org.zkoss.bind.annotation.AfterCompose;
import org.zkoss.bind.annotation.Command;
import org.zkoss.bind.annotation.ContextParam;
import org.zkoss.bind.annotation.ContextType;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.select.Selectors;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zk.ui.util.Clients;
import org.zkoss.zul.Combobox;
import org.zkoss.zul.Datebox;
import org.zkoss.zul.Messagebox;
import org.zkoss.zul.Messagebox.ClickEvent;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

import com.actividades.modelo.ActividadExterna;
import com.actividades.modelo.ActividadExternaDAO;
import com.actividades.modelo.ClaseActividad;
import com.actividades.modelo.ClaseActividadDAO;
import com.actividades.util.Constantes;

public class NuevaActividadExternaC {
	@Wire private Window winActividadExternaEditar;
	@Wire private Textbox txtDescripcion;
	@Wire private Datebox dtpFecha;
	@Wire private Combobox cboTipoActivivdad;
	
	ClaseActividadDAO claseActividadDAO = new ClaseActividadDAO();
	ActividadExterna actividadExterna;
	ActividadExternaDAO actividadExternaDAO = new ActividadExternaDAO();
	
	@AfterCompose
	public void afterCompose(@ContextParam(ContextType.VIEW) Component view) {
		Selectors.wireComponents(view, this, false);
		
		if(actividadExterna == null) {
			actividadExterna = new ActividadExterna();
			actividadExterna.setEstado(Constantes.ESTADO_ACTIVO);
		}else {
			actividadExterna = (ActividadExterna) Executions.getCurrent().getArg().get("Actividad");
			if(actividadExterna.getClaseActividad() != null) {
				cboTipoActivivdad.setText(actividadExterna.getClaseActividad().getClaseActividad());
			}
		}
	}
	@Command
	public void grabar(){
		if(validarDatos() == true) {
			EventListener<ClickEvent> clickListener = new EventListener<Messagebox.ClickEvent>() {
				public void onEvent(ClickEvent event) throws Exception {
					if(Messagebox.Button.YES.equals(event.getButton())) {
						actividadExternaDAO.getEntityManager().getTransaction().begin();
						cargarDatos();
						if(actividadExterna.getIdActividadExterna() == null) {//es un nuevo
							actividadExternaDAO.getEntityManager().persist(actividadExterna);
						}else {//es modificacion
							actividadExternaDAO.getEntityManager().merge(actividadExterna);
						}

						actividadExternaDAO.getEntityManager().getTransaction().commit();
						Messagebox.show("Datos grabados con exito");
						salir();
					}
				}
			};
			Messagebox.show("¿Desea Grabar los Datos?", "Confirmación", new Messagebox.Button[]{
					Messagebox.Button.YES, Messagebox.Button.NO }, Messagebox.QUESTION, clickListener);
		}
	}
	private void cargarDatos() {
		actividadExterna.setDescripcion(txtDescripcion.getText().toString());
		actividadExterna.setEstado(Constantes.ESTADO_ACTIVO);
		actividadExterna.setEstadoActividad(Constantes.ESTADO_NO_ASIGNADO);
		actividadExterna.setFecha(dtpFecha.getValue());
		actividadExterna.setClaseActividad((ClaseActividad)cboTipoActivivdad.getSelectedItem().getValue());
	}
	private boolean validarDatos() {
		try {
			if(cboTipoActivivdad.getSelectedItem().getValue() == null) {
				Clients.showNotification("Debe seleccionar Tipo de Actividad","info",cboTipoActivivdad,"end_center",2000);
				return false;
			}
			if(txtDescripcion.getText().isEmpty()) {
				Clients.showNotification("Debe registrar la descripcion de la actividad","info",txtDescripcion,"end_center",2000);
				txtDescripcion.focus();
				return false;
			}
			if(dtpFecha.getValue() == null) {
				Clients.showNotification("Debe registrar la fecha de la actividad","info",dtpFecha,"end_center",2000);
				return false;
			}
			return true;
		}catch(Exception ex) {
			return false;
		}
	}
	@Command
	public void salir(){
		BindUtils.postGlobalCommand(null, null, "ActividadExterna.buscarActivas", null);
		winActividadExternaEditar.detach();
	}
	public List<ClaseActividad> getClaseActividades(){
		return claseActividadDAO.obtenerClaseActividad();
	}
	public ClaseActividadDAO getClaseActividadDAO() {
		return claseActividadDAO;
	}
	public void setClaseActividadDAO(ClaseActividadDAO claseActividadDAO) {
		this.claseActividadDAO = claseActividadDAO;
	}
}