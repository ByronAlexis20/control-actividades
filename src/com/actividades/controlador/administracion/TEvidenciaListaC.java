package com.actividades.controlador.administracion;

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
import org.zkoss.zk.ui.util.Clients;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Messagebox;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

import com.actividades.modelo.TipoEvidencia;
import com.actividades.modelo.TipoEvidenciaDAO;

public class TEvidenciaListaC {
	public String textoBuscar;
	@Wire private Window winTipoEvidencia;
	@Wire private Textbox txtBuscar;
	@Wire private Listbox ltsTipoEvidencia;
	
	
	TipoEvidenciaDAO tipoEvidenciaDAO = new TipoEvidenciaDAO();
	List<TipoEvidencia> tipoEvidenciaLista;
	private TipoEvidencia tipoEvidenciaSeleccionado;
	
	@AfterCompose
	public void aferCompose(@ContextParam(ContextType.VIEW) Component view) throws IOException{
		Selectors.wireComponents(view, this, false);
		textoBuscar="";
		buscar();
	}
	
	@GlobalCommand("TipoEvidencia.buscarPorPatron")
	@NotifyChange({"tipoEvidenciaLista", "tipoEvidenciaSeleccionado"})
	@Command
	public void buscar(){
		if (tipoEvidenciaLista != null)
			tipoEvidenciaLista = null; 
		tipoEvidenciaLista = tipoEvidenciaDAO.getListaTiposEvidencia(textoBuscar);
		if(tipoEvidenciaLista.size() == 0) {
			Clients.showNotification("No hay datos para mostrar.!!");
		}else {
			tipoEvidenciaSeleccionado = null;
		}
	}
	
	@Command
	public void nuevo(){
		Window ventanaCargar = (Window) Executions.createComponents("/formularios/administracion/tipo_evidencia/TipoEvidenciaEditar.zul", null, null);
		ventanaCargar.doModal();
	}
	@Command
	public void editar(@BindingParam("tevidencia") TipoEvidencia tip){
		if (tip == null) {
			Messagebox.show("Debe seleccionar un tipo de evidencia para editar!");
			return; 
		}
		// Actualiza la instancia antes de enviarla a editar.
		tipoEvidenciaDAO.getEntityManager().refresh(tip);

		Map<String, Object> params = new HashMap<String, Object>();
		params.put("TipoEvidencia", tip);
		Window ventanaCargar = (Window) Executions.createComponents("/formularios/administracion/tipo_evidencia/TipoEvidenciaEditar.zul", winTipoEvidencia, params);
		ventanaCargar.doModal();

	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Command
	public void eliminar(@BindingParam("tevidencia") TipoEvidencia tip){
		if (tip == null) {
			Messagebox.show("Debe seleccionar un tipo de evidencia para eliminar!");
			return; 
		}
		List<TipoEvidencia> listaTipo = tipoEvidenciaDAO.buscarPorId(tip.getIdTipoEvidencia());
		if(listaTipo.size() != 0) {
			if(listaTipo.get(0).getEvidencias().size() > 0) {
				Messagebox.show("No se puede eliminar, es utilizada en otros registros!");
				return;
			}
		}
		
		Messagebox.show("Desea eliminar el registro seleccionado?", "Confirmación de Eliminación", Messagebox.YES | Messagebox.NO, Messagebox.QUESTION, new EventListener() {	
			@Override
			public void onEvent(Event event) throws Exception {
				if (event.getName().equals("onYes")) {
					try {
						tip.setEstado("I");
						tipoEvidenciaDAO.getEntityManager().getTransaction().begin();
						tipoEvidenciaDAO.getEntityManager().merge(tip);
						tipoEvidenciaDAO.getEntityManager().getTransaction().commit();
						Messagebox.show("Transaccion ejecutada con exito");
						BindUtils.postGlobalCommand(null, null, "TipoEvidencia.buscarPorPatron", null);
					} catch (Exception e) {
						e.printStackTrace();
						tipoEvidenciaDAO.getEntityManager().getTransaction().rollback();
					}
				}
			}
		});	
	}
	public List<TipoEvidencia> getTipoEvidenciaLista() {
		return tipoEvidenciaLista;
	}
	public void setTipoEvidenciaLista(List<TipoEvidencia> tipoEvidenciaLista) {
		this.tipoEvidenciaLista = tipoEvidenciaLista;
	}
	public TipoEvidencia getTipoEvidenciaSeleccionado() {
		return tipoEvidenciaSeleccionado;
	}
	public void setTipoEvidenciaSeleccionado(TipoEvidencia tipoEvidenciaSeleccionado) {
		this.tipoEvidenciaSeleccionado = tipoEvidenciaSeleccionado;
	}
	public String getTextoBuscar() {
		return textoBuscar;
	}
	public void setTextoBuscar(String textoBuscar) {
		this.textoBuscar = textoBuscar;
	}
}