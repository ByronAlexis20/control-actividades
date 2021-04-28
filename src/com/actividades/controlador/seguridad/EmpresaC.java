package com.actividades.controlador.seguridad;

import java.io.IOException;

import org.zkoss.bind.BindContext;
import org.zkoss.bind.annotation.AfterCompose;
import org.zkoss.bind.annotation.Command;
import org.zkoss.bind.annotation.ContextParam;
import org.zkoss.bind.annotation.ContextType;
import org.zkoss.bind.annotation.NotifyChange;
import org.zkoss.image.AImage;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.event.UploadEvent;
import org.zkoss.zk.ui.select.Selectors;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zk.ui.util.Clients;
import org.zkoss.zul.Messagebox;
import org.zkoss.zul.Window;
import org.zkoss.zul.Messagebox.ClickEvent;

import com.actividades.modelo.Empresa;
import com.actividades.modelo.EmpresaDAO;
import com.actividades.util.FileUtil;

public class EmpresaC {
	@Wire private Window winEmpresa;

	private Empresa empresa;
	private EmpresaDAO empresaDAO = new EmpresaDAO();

	@AfterCompose
	public void afterCompose(@ContextParam(ContextType.VIEW) Component view) {

		// Permite enlazar los componentes que se asocian con la anotacion @Wire
		Selectors.wireComponents(view, this, false);
		
		if(empresaDAO.obtenerEmpresa().size() > 0) {
			empresa = empresaDAO.obtenerEmpresa().get(0);
		}else {
			empresa = new Empresa();
		}
	}

	@Command
	public void grabar(){
		try {

			EventListener<ClickEvent> clickListener = new EventListener<Messagebox.ClickEvent>() {
				public void onEvent(ClickEvent event) throws Exception {
					if(Messagebox.Button.YES.equals(event.getButton())) {
						// Inicia la transaccion
						empresaDAO.getEntityManager().getTransaction().begin();
						// Si es nuevo sa el metodo "persist" de lo contrario usa el metodo "merge"
						if (empresa.getIdEmpresa() == null) {
							empresa.setEstado("A");
							empresaDAO.getEntityManager().persist(empresa);
						}else{
							empresa.setEstado("A");
							empresa = (Empresa) empresaDAO.getEntityManager().merge(empresa);
						}
						// Cierra la transaccion.
						empresaDAO.getEntityManager().getTransaction().commit();
						Clients.showNotification("Proceso Ejecutado con exito.");
					}
				}
			};
			Messagebox.show("¿Desea Grabar los Datos?", "Confirmación", new Messagebox.Button[]{
					Messagebox.Button.YES, Messagebox.Button.NO }, Messagebox.QUESTION, clickListener);
		} catch (Exception e) {
			e.printStackTrace();
			// Si hay error, reversa la transaccion.
			empresaDAO.getEntityManager().getTransaction().rollback();
		}

	}

	@Command
	@NotifyChange("logoInstitucion")
	public void subir(@ContextParam(ContextType.BIND_CONTEXT) BindContext contexto) {

		String pathRetornado; 

		// Extrae el evento de carga
		UploadEvent eventoCarga = (UploadEvent) contexto.getTriggerEvent();

		// Pasa el archivo a cargar al metodo "cargaArchivo" de la clase "FileUtil"
		pathRetornado = FileUtil.cargaArchivo(eventoCarga.getMedia());

		// Almacena el path retornado en el objeto.
		empresa.setLogo(pathRetornado);

	}
	@Command
	public void descargar() {
		if (empresa.getLogo() != null) {
			FileUtil.descargaArchivo(empresa.getLogo());
		}
	}
	public AImage getLogoInstitucion() {
		AImage retorno = null;
		if (empresa.getLogo() != null) {
			try {
				retorno = FileUtil.getImagenTamanoFijo(new AImage(empresa.getLogo()), 100, -1);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return retorno; 
	}
	public Empresa getEmpresa() {
		return empresa;
	}
	public void setEmpresa(Empresa empresa) {
		this.empresa = empresa;
	}
}
