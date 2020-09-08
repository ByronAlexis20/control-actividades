package com.actividades.controlador;

import java.util.ArrayList;
import java.util.List;

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
import org.zkoss.zul.Combobox;
import org.zkoss.zul.Messagebox;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

import com.actividades.modelo.Departamento;
import com.actividades.modelo.DepartamentoDAO;
import com.actividades.modelo.Empleado;
import com.actividades.modelo.EmpleadoDAO;
import com.actividades.modelo.Persona;
import com.actividades.modelo.TipoUsuario;
import com.actividades.modelo.TipoUsuarioDAO;
import com.actividades.util.ControllerHelper;

public class SEmpleadosEditarC {
	@Wire private Window winEmpleadoEditar;
	@Wire private Textbox txtCedula;
	@Wire private Textbox txtNombres;
	@Wire private Textbox txtApellidos;
	@Wire private Textbox txtEmail;
	@Wire private Textbox txtDireccion;
	@Wire private Textbox txtTelefono;
	@Wire private Combobox cboTipoUsuario;
	@Wire private Combobox cboDepartamento;
	@Wire private Textbox txtPuesto;
	@Wire private Textbox txtUsuario;
	@Wire private Textbox txtClave;
	

	private EmpleadoDAO empleadoDAO = new EmpleadoDAO();
	private Empleado empleado;
	private Persona persona;
	private TipoUsuarioDAO tipoUsuarioDAO = new TipoUsuarioDAO();
	private DepartamentoDAO departamentoDAO = new DepartamentoDAO();
	private ControllerHelper helper = new ControllerHelper();
	
	@AfterCompose
	public void afterCompose(@ContextParam(ContextType.VIEW) Component view) {
		Selectors.wireComponents(view, this, false);
		// Recupera el objeto pasado como parametro. 
		empleado = (Empleado) Executions.getCurrent().getArg().get("Empleado");
		if (empleado == null) {
			empleado = new Empleado();
			persona = new Persona();
		} else {
			recuperarDatos();
		}
	}

	private void recuperarDatos() {
		txtCedula.setText(empleado.getPersona().getCedula());
		txtNombres.setText(empleado.getPersona().getNombre());
		txtApellidos.setText(empleado.getPersona().getApellido());
		txtEmail.setText(empleado.getPersona().getEmail());
		txtDireccion.setText(empleado.getPersona().getDireccion());
		txtTelefono.setText(empleado.getPersona().getTelefono());
		cboTipoUsuario.setText(empleado.getTipoUsuario().getTipoUsuario());
		cboDepartamento.setText(empleado.getDepartamento().getDescripcion());
		txtPuesto.setText(empleado.getPuesto());
		txtUsuario.setText(empleado.getUsuario());
	}
	public List<TipoUsuario> getTipoUsuario() {		
		return tipoUsuarioDAO.getTiposUsuariosActivos();
	}

	public List<Departamento> getDepartamento() {		
		return departamentoDAO.getDepartamentosActivos();
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
						empleadoDAO.getEntityManager().getTransaction().begin();			
						if (empleado.getIdEmpleado() == null) {
							copiarDatos();
							empleadoDAO.getEntityManager().persist(persona);
						}else{
							copiarDatos();
							empleado = (Empleado) empleadoDAO.getEntityManager().merge(empleado);
						}			
						empleadoDAO.getEntityManager().getTransaction().commit();
						Clients.showNotification("Proceso Ejecutado con exito.");
						BindUtils.postGlobalCommand(null, null, "Empleado.buscarEmpleado", null);
						salir();						
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
			//llear datos de persona al insertar
			if (empleado.getIdEmpleado() == null) {
				persona.setCedula(txtCedula.getText().toString());
				persona.setNombre(txtNombres.getText().toString());
				persona.setApellido(txtApellidos.getText().toString());
				persona.setEmail(txtEmail.getText().toString());
				persona.setDireccion(txtDireccion.getText().toString());
				persona.setTelefono(txtTelefono.getText().toString());
				persona.setEstado("A");
				List<Empleado> lista = new ArrayList<>();
				lista.add(empleado);
				empleado.setPersona(persona);
				persona.setEmpleados(lista);
				empleado.setPuesto(txtPuesto.getText().toString());
				empleado.setUsuario(txtUsuario.getText().toString());
				empleado.setClave(helper.encriptar(txtClave.getText().toString()));
				empleado.setPrimeraVez("S");
				empleado.setEstado("A");
				TipoUsuario tipo = (TipoUsuario) cboTipoUsuario.getSelectedItem().getValue();
				empleado.setTipoUsuario(tipo);
				Departamento dep = (Departamento) cboDepartamento.getSelectedItem().getValue();
				empleado.setDepartamento(dep);
			
			}else {
				empleado.getPersona().setCedula(txtCedula.getText().toString());
				empleado.getPersona().setNombre(txtNombres.getText().toString());
				empleado.getPersona().setApellido(txtApellidos.getText().toString());
				empleado.getPersona().setEmail(txtEmail.getText().toString());
				empleado.getPersona().setDireccion(txtDireccion.getText().toString());
				empleado.getPersona().setTelefono(txtTelefono.getText().toString());
				empleado.setPuesto(txtPuesto.getText().toString());
				empleado.setUsuario(txtUsuario.getText().toString());
				empleado.setClave(helper.encriptar(txtClave.getText().toString()));
				empleado.setPrimeraVez("S");
				empleado.setEstado("A");
				TipoUsuario tipo = (TipoUsuario) cboTipoUsuario.getSelectedItem().getValue();
				empleado.setTipoUsuario(tipo);
				Departamento dep = (Departamento) cboDepartamento.getSelectedItem().getValue();
				empleado.setDepartamento(dep);
			}
			
		}catch(Exception ex) {
			System.out.println(ex.getMessage());
		}
	}
	@Command
	public void cedulaValidar() {
		try {
			if(helper.validarDeCedula(txtCedula.getText())== false) {
				Clients.showNotification("La cédula ingresada no es válida!!");
				txtCedula.focus();	
			}
			if(validarUsuarioExistente() == true) {
				Clients.showNotification("El usuario ingresado ya existe!!");
				txtCedula.focus();	
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	/** Validar Datos */
	public boolean validarDatos() {
		try {
			if(txtCedula.getText().isEmpty()) {
				Clients.showNotification("Debe registrar la cedula del empleado","info",txtCedula,"end_center",2000);
				txtCedula.focus();
				return false;
			}
			if(helper.validarDeCedula(txtCedula.getText())== false) {
				Clients.showNotification("La cédula ingresada no es válida!","info",txtCedula,"end_center",2000);
				txtCedula.focus();
				return false;
			}
			if(validarUsuarioExistente() == true) {
				Clients.showNotification("Ya hay un empleado con el número de cedula " + txtCedula.getText() + "!","info",txtCedula,"end_center",2000);
				txtCedula.focus();
				return false;
			}
			
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
			if(txtUsuario.getText().isEmpty()) {
				Clients.showNotification("Debe registrar el nombre de usuarioo","info",txtUsuario,"end_center",2000);
				txtUsuario.focus();
				return false;
			}
			if(txtClave.getText().isEmpty()) {
				Clients.showNotification("Debe registrar la clave del usuarioo","info",txtClave,"end_center",2000);
				txtClave.focus();
				return false;
			}
			if(validarUsuarioNuevo() == false) {
				Clients.showNotification("El nombre de usuario ya existe dentro de los registros","info",txtUsuario,"end_center",2000);
				txtUsuario.focus();
				return false;
			}else {
				if(validarUsuarioRegistradp() == false) {
					Clients.showNotification("El nombre de usuario ya existe dentro de los registros","info",txtUsuario,"end_center",2000);
					txtUsuario.focus();
					return false;
				}				
			}
			
			return true;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}
	private boolean validarUsuarioNuevo() {
		try {
			List<Empleado> listaEmpleados;
			listaEmpleados = empleadoDAO.getValidarUsuarioNuevo(txtCedula.getText().toString());
			if(listaEmpleados.size() > 0)
				return false;
			return true;
		}catch(Exception ex) {
			System.out.println(ex.getMessage());
			return false;
		}
	}
	private boolean validarUsuarioRegistradp() {
		try {
			List<Empleado> listaEmpleados;
			listaEmpleados = empleadoDAO.getValidarUsuarioRegistrado(txtCedula.getText().toString(),txtUsuario.getText().toString());
			if(listaEmpleados.size() > 0)
				return false;
			return true;
		}catch(Exception ex) {
			System.out.println(ex.getMessage());
			return false;
		}
	}
	/** Validar si el usuario existe a trav�s de la cedula */
	private boolean validarUsuarioExistente() {
		try {
			boolean bandera = false;
			List<Empleado> listaUsuario;
			if (empleado.getIdEmpleado() == null) {
				listaUsuario = empleadoDAO.getValidarUsuarioExistente(txtCedula.getText().toString());
			}else {
				listaUsuario = empleadoDAO.getValidarUsuarioExistenteDiferente(txtCedula.getText().toString(),empleado.getIdEmpleado());
			}
			
			if(listaUsuario.size() != 0)
				bandera = true;
			else
				bandera = false;
			return bandera;
		}catch(Exception ex) {
			return false;
		}
	}
	
	@Command
	public void salir(){
		winEmpleadoEditar.detach();
	}

	public Empleado getEmpleado() {
		return empleado;
	}

	public void setEmpleado(Empleado empleado) {
		this.empleado = empleado;
	}

	
}
