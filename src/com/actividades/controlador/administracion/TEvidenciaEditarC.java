package com.actividades.controlador.administracion;

import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.select.SelectorComposer;
import org.zkoss.zk.ui.select.annotation.Listen;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zk.ui.util.Clients;
import org.zkoss.zul.Messagebox;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

import com.actividades.modelo.TipoEvidencia;
import com.actividades.modelo.TipoEvidenciaDAO;

import org.zkoss.zul.Messagebox.ClickEvent;

@SuppressWarnings("serial")
public class TEvidenciaEditarC extends SelectorComposer<Component>{
	@Wire private Window winTipoEvidenciaEditar;
	@Wire private Textbox txtTipoEvidencia;
	@Wire private Textbox txtFormato;
	
	TipoEvidenciaDAO companiaDAO = new TipoEvidenciaDAO();
	TEvidenciaListaC tipoEvidenciaC;
	TipoEvidencia tipoEvidencia;
	
	@Override
	public void doAfterCompose(Component comp) throws Exception{
		super.doAfterCompose(comp);
		try {
			tipoEvidenciaC = (TEvidenciaListaC)Executions.getCurrent().getArg().get("VentanaPadre");
			tipoEvidencia = null;	
			if (Executions.getCurrent().getArg().get("TipoEvidencia") != null) {
				tipoEvidencia = (TipoEvidencia) Executions.getCurrent().getArg().get("TipoEvidencia");
			}else {
				tipoEvidencia = new TipoEvidencia();
				tipoEvidencia.setEstado("A");
			}
		}catch(Exception ex) {
			System.out.println(ex.getMessage());
		}
	}
	@Listen("onClick=#btnGrabar")
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
						tipoEvidenciaC.buscarTipoEvidencia("");
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
	@Listen("onClick=#btnSalir")
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
