package com.actividades.controlador;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.zkoss.bind.annotation.AfterCompose;
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
import org.zkoss.zul.Button;
import org.zkoss.zul.ListModelList;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Messagebox;
import org.zkoss.zul.Radio;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

import com.actividades.modelo.Empleado;
import com.actividades.modelo.EmpleadoDAO;
import com.actividades.modelo.Queja;
import com.actividades.modelo.QuejaDAO;
import com.actividades.util.Constantes;
import com.actividades.util.SecurityUtil;

public class QRealizarC {
	@Wire Listbox lstQuejas;
	@Wire Radio rbPendientes;
	@Wire Radio rbPublicadas;
	@Wire Radio rbAtendidas;
	
	@Wire Button btnNuevaQueja;
	@Wire Button btnEditarQueja;
	@Wire Button btnEliminarQueja;
	@Wire Button btnPasarRevision;
	
	@Wire Textbox txtBuscar;
	
	String textoBuscar;
	String estadoQuejaBuscar;
	List<Queja> listaQueja;
	Queja quejaSeleccionada;
	QuejaDAO quejaDAO = new QuejaDAO();
	EmpleadoDAO usuarioDAO = new EmpleadoDAO();
	
	@AfterCompose
	public void aferCompose(@ContextParam(ContextType.VIEW) Component view) throws IOException{
		Selectors.wireComponents(view, this, false);
		rbPendientes.setChecked(true);
		textoBuscar = "";
		estadoQuejaBuscar = Constantes.QUEJA_PENDIENTE;
		buscar();
	}
	@SuppressWarnings({ "unchecked", "rawtypes" })
	@GlobalCommand({"Queja.buscarPorResponsableEstadoQuejaAtencion"})
	@Command
	@NotifyChange({"listaQueja"})
	public void buscar(){
		if (listaQueja != null) {
			listaQueja = null; 
		}
		
		Empleado usuario = usuarioDAO.getUsuario(SecurityUtil.getUser().getUsername().trim());
		
		//List<Queja> lista = new ArrayList<>(); 
		if(rbPendientes.isChecked()) {
			System.out.println("Selecciono pendientes");
			listaQueja = quejaDAO.buscarPorResponsableEstadoQuejaAtencion(usuario.getIdEmpleado(), textoBuscar, Constantes.QUEJA_PENDIENTE, Constantes.QUEJA_NO_ATENDIDA);
		}else if(rbPublicadas.isChecked()) {
			System.out.println("Selecciono publicadas");
			listaQueja = quejaDAO.buscarPorResponsableEstadoQuejaAtencion(usuario.getIdEmpleado(), textoBuscar, Constantes.QUEJA_REVISION, Constantes.QUEJA_NO_ATENDIDA);
		}else if(rbAtendidas.isChecked()) {
			System.out.println("Selecciono atendidas");
			listaQueja = quejaDAO.buscarPorResponsableEstadoQuejaAtencion(usuario.getIdEmpleado(), textoBuscar, Constantes.QUEJA_PUBLICADA, Constantes.QUEJA_ATENDIDA);
		}
		
		lstQuejas.setModel(new ListModelList(listaQueja));
		quejaSeleccionada = null;
		if(listaQueja.size() == 0) {
			Clients.showNotification("No hay datos para mostrar.!!");
		}
	}
	@Command
	public void nuevaQueja() {
		Window ventanaCargar = (Window) Executions.createComponents("/formularios/quejas/realizar/QQuejaEditar.zul", null, null);
		ventanaCargar.doModal();
	}
	@SuppressWarnings({ "rawtypes", "unchecked" })
	@GlobalCommand({"Queja.buscarPorResponsableEstadoQuejaAtencion"})
	@Command
	@NotifyChange({"listaQueja"})
	public void cambia() {
		txtBuscar.setText("");
		textoBuscar = "";
		if(rbPendientes.isChecked()) {
			btnNuevaQueja.setDisabled(false);
			btnEditarQueja.setDisabled(false);
			btnEliminarQueja.setDisabled(false);
			btnPasarRevision.setDisabled(false);
		}else {
			btnNuevaQueja.setDisabled(true);
			btnEditarQueja.setDisabled(true);
			btnEliminarQueja.setDisabled(true);
			btnPasarRevision.setDisabled(true);
		}
		if (listaQueja != null) {
			listaQueja = null; 
		}
		Empleado usuario = usuarioDAO.getUsuario(SecurityUtil.getUser().getUsername().trim());
		if(rbPendientes.isChecked()) {
			System.out.println("Selecciono pendientes");
			listaQueja = quejaDAO.buscarPorResponsableEstadoQuejaAtencion(usuario.getIdEmpleado(), textoBuscar, Constantes.QUEJA_PENDIENTE, Constantes.QUEJA_NO_ATENDIDA);
		}else if(rbPublicadas.isChecked()) {
			System.out.println("Selecciono publicadas");
			listaQueja = quejaDAO.buscarPorResponsableEstadoQuejaAtencion(usuario.getIdEmpleado(), textoBuscar, Constantes.QUEJA_REVISION, Constantes.QUEJA_NO_ATENDIDA);
		}else if(rbAtendidas.isChecked()) {
			System.out.println("Selecciono atendidas");
			listaQueja = quejaDAO.buscarPorResponsableEstadoQuejaAtencion(usuario.getIdEmpleado(), textoBuscar, Constantes.QUEJA_PUBLICADA, Constantes.QUEJA_ATENDIDA);
		}
		lstQuejas.setModel(new ListModelList(listaQueja));
		quejaSeleccionada = null;
		if(listaQueja.size() == 0) {
			Clients.showNotification("No hay datos para mostrar.!!");
		}
	}
	@Command
	public void editarQueja() {
		if (quejaSeleccionada == null) {
			Messagebox.show("Debe seleccionar una queja");
			return; 
		}
		if(quejaSeleccionada.getEstadoQueja().equals(Constantes.QUEJA_REVISION)) {
			Messagebox.show("La queja se encuentra en REVISIÓN");
			return;
		}
		if(quejaSeleccionada.getEstadoQueja().equals(Constantes.QUEJA_PUBLICADA)) {
			Messagebox.show("La queja se encuentra en PUBLICADA");
			return;
		}
		// Actualiza la instancia antes de enviarla a editar.
		quejaDAO.getEntityManager().refresh(quejaSeleccionada);
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("Ventana", this);
		params.put("Queja", quejaSeleccionada);
		Window ventanaCargar = (Window) Executions.createComponents("/formularios/quejas/realizar/QQuejaEditar.zul", null, params);
		ventanaCargar.doModal();
	}
	
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Command
	public void pasarRevision() {
		if (quejaSeleccionada == null) {
			Messagebox.show("Debe seleccionar una Queja");
			return; 
		}
		Messagebox.show("Desea pasar a REVISION la QUEJA seleccionada?", "Confirmación de Eliminación", Messagebox.YES | Messagebox.NO, Messagebox.QUESTION, new EventListener() {	
			@Override
			public void onEvent(Event event) throws Exception {
				if (event.getName().equals("onYes")) {
					try {
						quejaSeleccionada.setEstadoQueja(Constantes.QUEJA_REVISION);
						quejaDAO.getEntityManager().getTransaction().begin();
						quejaDAO.getEntityManager().merge(quejaSeleccionada);
						quejaDAO.getEntityManager().getTransaction().commit();
						Messagebox.show("Transaccion ejecutada con exito");
						textoBuscar = "";
						buscar();
					} catch (Exception e) {
						e.printStackTrace();
						quejaDAO.getEntityManager().getTransaction().rollback();
					}
				}
			}
		});	
	}
	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Command
	public void eliminarQueja() {
		if (quejaSeleccionada == null) {
			Messagebox.show("Debe seleccionar una Queja");
			return; 
		}
		if(quejaSeleccionada.getEstadoQueja().equals(Constantes.QUEJA_REVISION)) {
			Messagebox.show("La queja se encuentra en REVISIÓN");
			return;
		}
		if(quejaSeleccionada.getEstadoQueja().equals(Constantes.QUEJA_PUBLICADA)) {
			Messagebox.show("La queja se encuentra en PUBLICADA");
			return;
		}
		Messagebox.show("Desea eliminar el registro seleccionado?", "Confirmación de Eliminación", Messagebox.YES | Messagebox.NO, Messagebox.QUESTION, new EventListener() {	
			@Override
			public void onEvent(Event event) throws Exception {
				if (event.getName().equals("onYes")) {
					try {
						quejaSeleccionada.setEstado(Constantes.ESTADO_INACTIVO);
						quejaDAO.getEntityManager().getTransaction().begin();
						quejaDAO.getEntityManager().merge(quejaSeleccionada);
						quejaDAO.getEntityManager().getTransaction().commit();
						Messagebox.show("Transaccion ejecutada con exito");
						textoBuscar = "";
						buscar();
					} catch (Exception e) {
						e.printStackTrace();
						quejaDAO.getEntityManager().getTransaction().rollback();
					}
				}
			}
		});	
	}
	public String getTextoBuscar() {
		return textoBuscar;
	}
	public void setTextoBuscar(String textoBuscar) {
		this.textoBuscar = textoBuscar;
	}
	public List<Queja> getListaQueja() {
		return listaQueja;
	}
	public void setListaQueja(List<Queja> listaQueja) {
		this.listaQueja = listaQueja;
	}
	public Queja getQuejaSeleccionada() {
		return quejaSeleccionada;
	}
	public void setQuejaSeleccionada(Queja quejaSeleccionada) {
		this.quejaSeleccionada = quejaSeleccionada;
	}
	public String getEstadoQuejaBuscar() {
		return estadoQuejaBuscar;
	}
	public void setEstadoQuejaBuscar(String estadoQuejaBuscar) {
		this.estadoQuejaBuscar = estadoQuejaBuscar;
	}
}
