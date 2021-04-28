package com.actividades.controlador.administracion;

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
import org.zkoss.zul.Messagebox;
import org.zkoss.zul.Messagebox.ClickEvent;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

import com.actividades.modelo.TipoEvidencia;
import com.actividades.modelo.TipoEvidenciaDAO;

public class TEvidenciaEditarC {
	@Wire private Window winTipoEvidenciaEditar;
	@Wire private Textbox txtTipoEvidencia;
	@Wire private Textbox txtFormato;
	
	TipoEvidenciaDAO companiaDAO = new TipoEvidenciaDAO();
	TEvidenciaListaC tipoEvidenciaC;
	TipoEvidencia tipoEvidencia;
	
	@AfterCompose
	public void afterCompose(@ContextParam(ContextType.VIEW) Component view) {
		Selectors.wireComponents(view, this, false);
		// Recupera el objeto pasado como parametro. 
		tipoEvidencia = (TipoEvidencia) Executions.getCurrent().getArg().get("TipoEvidencia");
		if (tipoEvidencia == null) {
			tipoEvidencia = new TipoEvidencia();
			tipoEvidencia.setEstado("A");
		} 
	}

	@Command
	public void grabar(){
		if(validarDatos() == true) {
			EventListener<ClickEvent> clickListener = new EventListener<Messagebox.ClickEvent>() {
				public void onEvent(ClickEvent event) throws Exception {
					if(Messagebox.Button.YES.equals(event.getButton())) {
						companiaDAO.getEntityManager().getTransaction().begin();
						tipoEvidencia.setEstado("A");
						if(tipoEvidencia.getIdTipoEvidencia() == null) {//es un nuevo
							companiaDAO.getEntityManager().persist(tipoEvidencia);
						}else {//es modificacion
							companiaDAO.getEntityManager().merge(tipoEvidencia);
						}
						companiaDAO.getEntityManager().getTransaction().commit();
						Messagebox.show("Datos grabados con exito");
						BindUtils.postGlobalCommand(null, null, "TipoEvidencia.buscarPorPatron", null);
						salir();
					}
				}
			};
			Messagebox.show("¿Desea Grabar los Datos?", "Confirmación", new Messagebox.Button[]{
					Messagebox.Button.YES, Messagebox.Button.NO }, Messagebox.QUESTION, clickListener);
		}
	}
	public boolean validarDatos() {
		if(txtTipoEvidencia.getText() == "") {
			Clients.showNotification("Debe registrar la descripción del tipo de evidencia","info",txtTipoEvidencia,"end_center",2000);
			txtTipoEvidencia.setFocus(true);
			return false;
		}
		
		if(txtFormato.getText() == "") {
			Clients.showNotification("Campo obligatorio: formato del archivo","info",txtFormato,"end_center",2000);
			txtFormato.setFocus(true);
			return false;
		}
		return true;
	}
	@Command
	public void salir(){
		winTipoEvidenciaEditar.detach();
	}
	public TipoEvidencia getTipoEvidencia() {
		return tipoEvidencia;
	}
	public void setTipoEvidencia(TipoEvidencia tipoEvidencia) {
		this.tipoEvidencia = tipoEvidencia;
	}
}
