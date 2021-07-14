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
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.event.UploadEvent;
import org.zkoss.zk.ui.select.Selectors;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zk.ui.util.Clients;
import org.zkoss.zul.Messagebox;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

import com.actividades.modelo.Empleado;
import com.actividades.modelo.EmpleadoDAO;
import com.actividades.util.ControllerHelper;
import com.actividades.util.FileUtil;
import com.actividades.util.SecurityUtil;

public class CuentaC {
	@Wire private Window winEmpleadoEditar;
	@Wire private Textbox txtCedula;
	@Wire private Textbox txtNombres;
	@Wire private Textbox txtApellidos;
	@Wire private Textbox txtEmail;
	@Wire private Textbox txtDireccion;
	@Wire private Textbox txtTelefono;
	@Wire private Textbox txtTipoUsuario;
	@Wire private Textbox txtDepartamento;
	@Wire private Textbox txtCargo;
	@Wire private Textbox txtUsuario;
	@Wire private Textbox txtClave;
	
	private EmpleadoDAO usuarioDAO = new EmpleadoDAO();
	private ControllerHelper helper = new ControllerHelper();
	Empleado empleado;
	@AfterCompose
	public void afterCompose(@ContextParam(ContextType.VIEW) Component view) {
		Selectors.wireComponents(view, this, false);
		empleado = new Empleado();
		empleado = usuarioDAO.getUsuario(SecurityUtil.getUser().getUsername().trim());
		recuperarDatos();
	}
	private void recuperarDatos() {
		txtCedula.setText(empleado.getPersona().getCedula());
		txtNombres.setText(empleado.getPersona().getNombre());
		txtApellidos.setText(empleado.getPersona().getApellido());
		txtEmail.setText(empleado.getPersona().getEmail());
		txtDireccion.setText(empleado.getPersona().getDireccion());
		txtTelefono.setText(empleado.getPersona().getTelefono());
		txtTipoUsuario.setText(empleado.getTipoUsuario().getTipoUsuario());
		txtDepartamento.setText(empleado.getDepartamento().getNombre());
		txtCargo.setText(empleado.getCargo().getDescripcion());
		txtUsuario.setText(empleado.getUsuario());
		txtClave.setText(empleado.getClaveNormal());
	}
	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Command
	public void grabar(){
		if(validarDatos() == false) {
			return;
		}
		Messagebox.show("Desea guardar el registro?", "Confirmación de Guardar", Messagebox.YES | Messagebox.NO, Messagebox.QUESTION, new EventListener() {
			@Override
			public void onEvent(Event event) throws Exception {
				if (event.getName().equals("onYes")) {		
					try {						
						usuarioDAO.getEntityManager().getTransaction().begin();			
						copiarDatos();
						usuarioDAO.getEntityManager().merge(empleado);
						usuarioDAO.getEntityManager().merge(empleado.getPersona());
						usuarioDAO.getEntityManager().getTransaction().commit();
						Clients.showNotification("Proceso Ejecutado con exito.");
					} catch (Exception e) {
						e.printStackTrace();
						// categoriaDao.getEntityManager().getTransaction().rollback();
					}
				}
			}
		});
	}
	private void copiarDatos() {
		try {
			empleado.getPersona().setCedula(txtCedula.getText().toString());
			empleado.getPersona().setNombre(txtNombres.getText().toString());
			empleado.getPersona().setApellido(txtApellidos.getText().toString());
			empleado.getPersona().setEmail(txtEmail.getText().toString());
			empleado.getPersona().setDireccion(txtDireccion.getText().toString());
			empleado.getPersona().setTelefono(txtTelefono.getText().toString());
			empleado.setClave(helper.encriptar(txtClave.getText().toString()));
			empleado.setClaveNormal(txtClave.getText().toString());
		}catch(Exception ex) {
			System.out.println(ex.getMessage());
		}
	}
	/** Validar Datos */
	public boolean validarDatos() {
		try {
			if(txtNombres.getText().isEmpty()) {
				Clients.showNotification("Debe registrar el nombre del empleado","info",txtNombres,"end_center",2000);
				txtNombres.focus();
				return false;
			}
			if(txtApellidos.getText().isEmpty()) {
				Clients.showNotification("Debe registrar el apellido del empleado","info",txtApellidos,"end_center",2000);
				txtApellidos.focus();
				return false;
			}
			if(txtClave.getText().isEmpty()) {
				Clients.showNotification("Debe registrar la clave del usuarioo","info",txtClave,"end_center",2000);
				txtClave.focus();
				return false;
			}
			return true;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}
	@Command
	@NotifyChange("imagenUsuario")
	public void subir(@ContextParam(ContextType.BIND_CONTEXT) BindContext contexto) {
		String pathRetornado; 
		UploadEvent eventoCarga = (UploadEvent) contexto.getTriggerEvent();
		pathRetornado = FileUtil.cargaArchivo(eventoCarga.getMedia());
		empleado.setFoto(pathRetornado);
	}
	@Command
	public void descargar() {
		if (empleado.getFoto() != null) {
			FileUtil.descargaArchivo(empleado.getFoto());
		}
	}
	public Empleado getEmpleado() {
		return empleado;
	}
	public void setEmpleado(Empleado empleado) {
		this.empleado = empleado;
	}
	public AImage getImagenUsuario() {
		AImage retorno = null;
		if (empleado.getFoto() != null) {
			try {
				retorno = FileUtil.getImagenTamanoFijo(new AImage(empleado.getFoto()), 100, -1);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return retorno; 
	}
}