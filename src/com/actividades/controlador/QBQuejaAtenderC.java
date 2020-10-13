package com.actividades.controlador;

import java.io.IOException;

import org.zkoss.bind.BindUtils;
import org.zkoss.bind.annotation.AfterCompose;
import org.zkoss.bind.annotation.Command;
import org.zkoss.bind.annotation.ContextParam;
import org.zkoss.bind.annotation.ContextType;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.select.Selectors;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zk.ui.util.Clients;
import org.zkoss.zul.Messagebox;
import org.zkoss.zul.Window;

import com.actividades.modelo.Queja;
import com.actividades.modelo.QuejaDAO;
import com.actividades.util.Constantes;
import com.actividades.util.FileUtil;

public class QBQuejaAtenderC {
	@Wire Window winQuejaAtender;
	Queja queja;
	QuejaDAO quejaDAO = new QuejaDAO();
	
	@AfterCompose
	public void aferCompose(@ContextParam(ContextType.VIEW) Component view) throws IOException{
		Selectors.wireComponents(view, this, false);
		queja = (Queja) Executions.getCurrent().getArg().get("Queja");
		
	}
	@Command
	public void descargar() {
		if (queja.getAdjunto() != null) {
			FileUtil.descargaArchivo(queja.getAdjunto());
		}
	}
	@Command
	public void salir() {
		winQuejaAtender.detach();
	}
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Command
	public void atender() {
		Messagebox.show("Desea Marcar como Atendido la Queja?", "Confirmación de Guardar", Messagebox.YES | Messagebox.NO, Messagebox.QUESTION, new EventListener() {
			@Override
			public void onEvent(Event event) throws Exception {
				if (event.getName().equals("onYes")) {		
					try {
						quejaDAO.getEntityManager().getTransaction().begin();
						queja.setEstadoAtencion(Constantes.QUEJA_ATENDIDA);
						if (queja.getIdQueja() == null) {
							quejaDAO.getEntityManager().persist(queja);
						}else{
							queja = (Queja) quejaDAO.getEntityManager().merge(queja);
						}			
						quejaDAO.getEntityManager().getTransaction().commit();
						Clients.showNotification("Proceso Ejecutado con exito.");
						BindUtils.postGlobalCommand(null, null, "Queja.buscarQueja", null);
						salir();						
					} catch (Exception e) {
						e.printStackTrace();
						quejaDAO.getEntityManager().getTransaction().rollback();
					}
				}
			}
		});
	}
	public Queja getQueja() {
		return queja;
	}
	public void setQueja(Queja queja) {
		this.queja = queja;
	}
}
