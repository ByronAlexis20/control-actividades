package com.actividades.controlador.seguridad;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.zkoss.bind.BindContext;
import org.zkoss.bind.BindUtils;
import org.zkoss.bind.annotation.AfterCompose;
import org.zkoss.bind.annotation.Command;
import org.zkoss.bind.annotation.ContextParam;
import org.zkoss.bind.annotation.ContextType;
import org.zkoss.bind.annotation.NotifyChange;
import org.zkoss.image.AImage;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.event.UploadEvent;
import org.zkoss.zk.ui.select.Selectors;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zk.ui.util.Clients;
import org.zkoss.zul.Combobox;
import org.zkoss.zul.ListModelList;
import org.zkoss.zul.Messagebox;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

import com.actividades.modelo.Cargo;
import com.actividades.modelo.CargoDAO;
import com.actividades.modelo.Departamento;
import com.actividades.modelo.DepartamentoDAO;
import com.actividades.modelo.Empleado;
import com.actividades.modelo.EmpleadoDAO;
import com.actividades.modelo.Persona;
import com.actividades.modelo.TipoUsuario;
import com.actividades.modelo.TipoUsuarioDAO;
import com.actividades.util.Constantes;
import com.actividades.util.ControllerHelper;
import com.actividades.util.FileUtil;

public class EmpleadosEditarC {
	@Wire private Window winEmpleadoEditar;
	@Wire private Textbox txtCedula;
	@Wire private Textbox txtNombres;
	@Wire private Textbox txtApellidos;
	@Wire private Textbox txtEmail;
	@Wire private Textbox txtDireccion;
	@Wire private Textbox txtTelefono;
	@Wire private Combobox cboTipoUsuario;
	@Wire Combobox cboDepartamento;
	@Wire private Combobox cboCargo;
	@Wire private Textbox txtUsuario;
	@Wire private Textbox txtClave;
	Departamento departamentoSeleccionado;
	TipoUsuario tipoUsuarioseleccionado;
	Cargo cargoSeleccionado;
	
	private EmpleadoDAO empleadoDAO = new EmpleadoDAO();
	private Empleado empleado;
	private Persona persona;
	private TipoUsuarioDAO tipoUsuarioDAO = new TipoUsuarioDAO();
	private DepartamentoDAO departamentoDAO = new DepartamentoDAO();
	private CargoDAO cargoDAO = new CargoDAO();
	private ControllerHelper helper = new ControllerHelper();
	private List<Cargo> cargos;
	
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
			cboCargo.setDisabled(true);
			cboDepartamento.setDisabled(true);
			cboTipoUsuario.setDisabled(true);
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
		tipoUsuarioseleccionado = empleado.getTipoUsuario();
		cboDepartamento.setText(empleado.getDepartamento().getNombre());
		departamentoSeleccionado = empleado.getDepartamento();
		cboCargo.setText(empleado.getCargo().getDescripcion());
		cargoSeleccionado = empleado.getCargo();
		txtUsuario.setText(empleado.getUsuario());
		txtClave.setText(empleado.getClaveNormal());
	}
	public List<TipoUsuario> getTipoUsuario() {
		//validar cuando ya existe un usuario de maxima autoridad
		List<Empleado> lista = empleadoDAO.buscarEmpleadoPorTipoUsuario(Constantes.ID_AUTORIDAD_MAXIMA);
		if(lista.size() > 0) {
			List<TipoUsuario> listTipoUsuario = new ArrayList<>();
			List<TipoUsuario> tiposUsuarios = tipoUsuarioDAO.getTiposUsuariosActivos();
			for(TipoUsuario t : tiposUsuarios) {
				if(t.getIdTipoUsuario() != Constantes.ID_AUTORIDAD_MAXIMA) {
					listTipoUsuario.add(t);
				}
			}
			return listTipoUsuario;
		}else {
			return tipoUsuarioDAO.getTiposUsuariosActivos();
		}
	}

	public List<Departamento> getDepartamento() {		
		List<Departamento> list = departamentoDAO.getDepartamentosActivos();
		List<Departamento> listado = new ArrayList<>();
		for(Departamento d : list) {
			if(d.getIdDepartamento() != Constantes.ID_AUTORIDAD_MAXIMA && d.getIdDepartamento() != Constantes.ID_ADMINISTRACION_COMUNICACION && d.getIdDepartamento() != Constantes.ID_ADMINISTRADOR_SISTEMAS) {
				listado.add(d);
			}
		}
		return listado;
	}
	public List<Cargo> getCargos() {
		return cargos;
	}

	public void setCargos(List<Cargo> cargos) {
		this.cargos = cargos;
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
							empleado.setFechaIngreso(new Date());
							empleadoDAO.getEntityManager().persist(persona);
						}else{
							copiarDatos();
							empleadoDAO.getEntityManager().merge(empleado.getPersona());
							empleadoDAO.getEntityManager().merge(empleado);
						}			
						empleadoDAO.getEntityManager().getTransaction().commit();
						Clients.showNotification("Proceso Ejecutado con éxito.");
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
				persona.setCedula(txtCedula.getText());
				persona.setNombre(txtNombres.getText());
				persona.setApellido(txtApellidos.getText());
				persona.setEmail(txtEmail.getText());
				persona.setDireccion(txtDireccion.getText());
				persona.setTelefono(txtTelefono.getText());
				persona.setEstado("A");
				List<Empleado> lista = new ArrayList<>();
				empleado.setPersona(persona);
				empleado.setUsuario(txtUsuario.getText());
				empleado.setClave(helper.encriptar(txtClave.getText()));
				empleado.setClaveNormal(txtClave.getText());
				empleado.setPrimeraVez("S");
				empleado.setEstado("A");
				TipoUsuario tipo = (TipoUsuario) cboTipoUsuario.getSelectedItem().getValue();
				empleado.setTipoUsuario(tipo);
				Departamento dep = (Departamento) cboDepartamento.getSelectedItem().getValue();
				empleado.setDepartamento(dep);
				Cargo car = (Cargo) cboCargo.getSelectedItem().getValue();
				empleado.setCargo(car);
				lista.add(empleado);
				persona.setEmpleados(lista);
			
			}else {
				empleado.getPersona().setCedula(txtCedula.getText());
				empleado.getPersona().setNombre(txtNombres.getText());
				empleado.getPersona().setApellido(txtApellidos.getText());
				empleado.getPersona().setEmail(txtEmail.getText());
				empleado.getPersona().setDireccion(txtDireccion.getText());
				empleado.getPersona().setTelefono(txtTelefono.getText());
				empleado.setUsuario(txtUsuario.getText());
				empleado.setClave(helper.encriptar(txtClave.getText()));
				empleado.setClaveNormal(txtClave.getText());
				empleado.setPrimeraVez("S");
				empleado.setEstado("A");
				TipoUsuario tipo = (TipoUsuario) cboTipoUsuario.getSelectedItem().getValue();
				empleado.setTipoUsuario(tipo);
				Departamento dep = (Departamento) cboDepartamento.getSelectedItem().getValue();
				empleado.setDepartamento(dep);
				Cargo car = (Cargo) cboCargo.getSelectedItem().getValue();
				empleado.setCargo(car);
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
				Clients.showNotification("Debe registrar la cédula del empleado","info",txtCedula,"end_center",2000);
				txtCedula.focus();
				return false;
			}
			if(helper.validarDeCedula(txtCedula.getText())== false) {
				Clients.showNotification("La cédula ingresada no es válida!","info",txtCedula,"end_center",2000);
				txtCedula.focus();
				return false;
			}
			if(validarUsuarioExistente() == true) {
				Clients.showNotification("Ya hay un empleado con el número de cédula " + txtCedula.getText() + "!","info",txtCedula,"end_center",2000);
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
				Clients.showNotification("Debe registrar el nombre de usuario","info",txtUsuario,"end_center",2000);
				txtUsuario.focus();
				return false;
			}
			if(txtClave.getText().isEmpty()) {
				Clients.showNotification("Debe registrar la clave del usuarioo","info",txtClave,"end_center",2000);
				txtClave.focus();
				return false;
			}
			//hacer la validacion de la contrasenia
			if(validarClave(txtClave.getText()) == false) {
				return false;
			}
			if(!txtEmail.getText().isEmpty()) {
				if(!ControllerHelper.validarEmail(txtEmail.getText())) {
					Clients.showNotification("Correo no válido","info",txtEmail,"end_center",2000);
					txtEmail.focus();
					return false;
				}
			}
			if(tipoUsuarioseleccionado == null) {
				Clients.showNotification("Debe seleccionar un tipo de usuario","info",cboTipoUsuario,"end_center",2000);
				return false;
			}
			if(departamentoSeleccionado == null) {
				Clients.showNotification("Debe seleccionar un departamento","info",cboDepartamento,"end_center",2000);
				return false;
			}
			
			if(validarJefes() == true) {
				Clients.showNotification("Ya existe un jefe en el Departamento!","info",cboTipoUsuario,"end_center",2000);
				cboTipoUsuario.focus();
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
			//ademas validar si ya existen administradores de sistemas.. solo debe haber uno
			if(tipoUsuarioseleccionado.getIdTipoUsuario() == Constantes.ID_ADMINISTRADOR_SISTEMAS) {
				if(empleado != null) {
					if(empleado.getIdEmpleado() == null) {
						List<Empleado> listaEmpleadoSistema = empleadoDAO.buscarEmpleadoPorTipoUsuario(Constantes.ID_ADMINISTRADOR_SISTEMAS);
						if(listaEmpleadoSistema.size() > 0) {
							Clients.showNotification("Ya existe un administrador de sistemas","info",txtCedula,"end_center",2000);
							return false;
						}
					}
				}
			}
			if(tipoUsuarioseleccionado.getIdTipoUsuario() == Constantes.ID_ADMINISTRACION_COMUNICACION) {
				if(empleado != null) {
					if(empleado.getIdEmpleado() == null) {
						List<Empleado> listaEmpleadoComunicacion = empleadoDAO.buscarEmpleadoPorTipoUsuario(Constantes.ID_ADMINISTRACION_COMUNICACION);
						if(listaEmpleadoComunicacion.size() > 0) {
							Clients.showNotification("Ya existe un administrador de comunicación","info",txtCedula,"end_center",2000);
							return false;
						}
					}
				}
			}
			return true;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}
	private boolean validarClave(String clave) {
		if(clave.length() < 8) {
			Clients.showNotification("Clave debe contener al menos 8 caracteres","info",txtClave,"end_center",2000);
			txtClave.focus();
			return false;
		}
		if(ControllerHelper.validarContrasenia(clave) == false) {
			Clients.showNotification("Clave debe tener una mayúscula, un caracter especial, un número!","info",txtClave,"end_center",2000);
			txtClave.focus();
			return false;
		}
		return true;
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
	private boolean validarJefes() {
		//hay q validar si ya estan registrado los jefes de los departamentos cuando es un nuevo registro
		boolean bandera = false;

		if(empleado.getIdEmpleado() == null) {
			
			if(tipoUsuarioseleccionado.getIdTipoUsuario().equals(Constantes.ID_JEFE_AREA)) {

				List<Empleado> emp = empleadoDAO.validarJefeDepartamento(Constantes.ID_JEFE_AREA, departamentoSeleccionado.getIdDepartamento());
				if(emp.size() > 0)
					bandera = true;
			}
			
		}
		return bandera;
	}
	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Command
	public void cambiarUsuario() {
		TipoUsuario tipo = (TipoUsuario) cboTipoUsuario.getSelectedItem().getValue();
		if(tipo.getIdTipoUsuario().equals(Constantes.ID_JEFE_AREA) || tipo.getIdTipoUsuario().equals(Constantes.ID_ADMINISTRACION_COMUNICACION)
				|| tipo.getIdTipoUsuario().equals(Constantes.ID_ADMINISTRADOR_SISTEMAS) || tipo.getIdTipoUsuario().equals(Constantes.ID_AUTORIDAD_MAXIMA)) {
			cboCargo.setDisabled(true);
			List<Cargo> car = cargoDAO.cargoPorId(Constantes.ID_CARGO_JEFE);
			cargos = cargoDAO.getCargosActivos();
			cboCargo.setModel(new ListModelList(cargos));
			cboCargo.setText(car.get(0).getDescripcion());
		}else {
			cboCargo.setDisabled(false);
			cargos = cargoDAO.cargoSinJefe(Constantes.ID_CARGO_JEFE);
			cboCargo.setModel(new ListModelList(cargos));
			cboCargo.setText("");
		}
		cboDepartamento.setDisabled(false);
		cboDepartamento.setText("");
		if(tipo.getIdTipoUsuario().equals(Constantes.ID_ADMINISTRADOR_SISTEMAS)) {
			List<Departamento> dep = departamentoDAO.getDepartamentoPorId(Constantes.ID_DEPARTAMENTO_SISTEMAS);
			departamentoSeleccionado = dep.get(0);
			cboDepartamento.setText(dep.get(0).getNombre());
			cboDepartamento.setDisabled(true);
			cboDepartamento.setModel(new ListModelList(dep));
		}else {
			if(tipo.getIdTipoUsuario().equals(Constantes.ID_ADMINISTRACION_COMUNICACION)) {
				List<Departamento> dep = departamentoDAO.getDepartamentoPorId(Constantes.ID_DEPARTAMENTO_COMUNICACIONES);
				departamentoSeleccionado = dep.get(0);
				cboDepartamento.setText(dep.get(0).getNombre());
				cboDepartamento.setDisabled(true);
				cboDepartamento.setModel(new ListModelList(dep));
			}else {
				if(tipo.getIdTipoUsuario().equals(Constantes.ID_AUTORIDAD_MAXIMA)) {
					List<Departamento> dep = departamentoDAO.getDepartamentoPorId(Constantes.ID_DEPARTAMENTO_GOBERNACION);
					departamentoSeleccionado = dep.get(0);
					cboDepartamento.setText(dep.get(0).getNombre());
					cboDepartamento.setDisabled(true);
					cboDepartamento.setModel(new ListModelList(dep));
				}
			}
		}
		if(!tipo.getIdTipoUsuario().equals(Constantes.ID_AUTORIDAD_MAXIMA) && !tipo.getIdTipoUsuario().equals(Constantes.ID_ADMINISTRACION_COMUNICACION) && !tipo.getIdTipoUsuario().equals(Constantes.ID_ADMINISTRADOR_SISTEMAS)) {
			List<Departamento> list = departamentoDAO.getDepartamentosActivos();
			List<Departamento> listado = new ArrayList<>();
			for(Departamento d : list) {
				if(d.getIdDepartamento() != Constantes.ID_DEPARTAMENTO_GOBERNACION && d.getIdDepartamento() != Constantes.ID_DEPARTAMENTO_COMUNICACIONES && d.getIdDepartamento() != Constantes.ID_DEPARTAMENTO_SISTEMAS) {
					listado.add(d);
				}
			}
			cboDepartamento.setModel(new ListModelList(listado));
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
	/** Validar si el usuario existe a travï¿½s de la cedula */
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
	
	public Departamento getDepartamentoSeleccionado() {
		return departamentoSeleccionado;
	}

	public void setDepartamentoSeleccionado(Departamento departamentoSeleccionado) {
		this.departamentoSeleccionado = departamentoSeleccionado;
	}

	public TipoUsuario getTipoUsuarioseleccionado() {
		return tipoUsuarioseleccionado;
	}

	public void setTipoUsuarioseleccionado(TipoUsuario tipoUsuarioseleccionado) {
		this.tipoUsuarioseleccionado = tipoUsuarioseleccionado;
	}

	public Cargo getCargoSeleccionado() {
		return cargoSeleccionado;
	}

	public void setCargoSeleccionado(Cargo cargoSeleccionado) {
		this.cargoSeleccionado = cargoSeleccionado;
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
