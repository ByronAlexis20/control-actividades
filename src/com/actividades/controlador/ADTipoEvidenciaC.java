package com.actividades.controlador;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.select.SelectorComposer;
import org.zkoss.zk.ui.select.annotation.Listen;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zul.ListModelList;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Messagebox;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

import com.actividades.modelo.TipoEvidencia;
import com.actividades.modelo.TipoEvidenciaDAO;


@SuppressWarnings("serial")
public class ADTipoEvidenciaC extends SelectorComposer<Component>{
	@Wire private Window winTipoEvidencia;
	@Wire private Textbox txtBuscar;
	@Wire private Listbox ltsTipoEvidencia;
	
	
	TipoEvidenciaDAO tipoEvidenciaDAO = new TipoEvidenciaDAO();
	List<TipoEvidencia> tipoEvidenciaLista;
	private TipoEvidencia tipoEvidenciaSeleccionado;
	
	@Override
	public void doAfterCompose(Component comp) throws Exception {
		super.doAfterCompose(comp);
		buscarTipoEvidencia("");
	}
	@Listen("onClick=#btnBuscar;onOK=#txtBuscar")
	public void buscar(){
		buscarTipoEvidencia(txtBuscar.getText());
	}
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public void buscarTipoEvidencia(String dato) {
		if (tipoEvidenciaLista != null)
			tipoEvidenciaLista = null; 
		tipoEvidenciaLista = tipoEvidenciaDAO.getListaTiposEvidencia(dato);
		ltsTipoEvidencia.setModel(new ListModelList(tipoEvidenciaLista));
		tipoEvidenciaSeleccionado = null;
	}
	@Listen("onClick=#btnNuevo")
	public void nuevo(){
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("TipoEvidencia", null);
		params.put("VentanaPadre", this);
		Window ventanaCargar = (Window) Executions.createComponents("/formularios/administracion/tipo_evidencia/ADTipoEvidenciaEditar.zul", winTipoEvidencia, params);
		ventanaCargar.doModal();
	}
	@Listen("onClick=#btnEditar")
	public void editar(){
		if (tipoEvidenciaSeleccionado == null) {
			Messagebox.show("Debe seleccionar un tipo de evidencia para editar!");
			return; 
		}
		// Actualiza la instancia antes de enviarla a editar.
		tipoEvidenciaDAO.getEntityManager().refresh(tipoEvidenciaSeleccionado);

		Map<String, Object> params = new HashMap<String, Object>();
		params.put("TipoEvidencia", tipoEvidenciaSeleccionado);
		params.put("VentanaPadre", this);
		Window ventanaCargar = (Window) Executions.createComponents("/formularios/administracion/tipo_evidencia/ADTipoEvidenciaEditar.zul", winTipoEvidencia, params);
		ventanaCargar.doModal();

	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Listen("onClick=#btnEliminar")
	public void eliminar(){
		if (tipoEvidenciaSeleccionado == null) {
			Messagebox.show("Debe seleccionar un tipo de evidencia para eliminar!");
			return; 
		}
		Messagebox.show("Desea eliminar el registro seleccionado?", "Confirmación de Eliminación", Messagebox.YES | Messagebox.NO, Messagebox.QUESTION, new EventListener() {	
			@Override
			public void onEvent(Event event) throws Exception {
				if (event.getName().equals("onYes")) {
					try {
						tipoEvidenciaSeleccionado.setEstado("I");
						tipoEvidenciaDAO.getEntityManager().getTransaction().begin();
						tipoEvidenciaDAO.getEntityManager().merge(tipoEvidenciaSeleccionado);
						tipoEvidenciaDAO.getEntityManager().getTransaction().commit();
						Messagebox.show("Transaccion ejecutada con exito");
						buscar();
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
}