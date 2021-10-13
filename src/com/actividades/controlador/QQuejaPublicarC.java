package com.actividades.controlador;

import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.zkoss.bind.BindUtils;
import org.zkoss.bind.annotation.AfterCompose;
import org.zkoss.bind.annotation.Command;
import org.zkoss.bind.annotation.ContextParam;
import org.zkoss.bind.annotation.ContextType;
import org.zkoss.bind.annotation.GlobalCommand;
import org.zkoss.bind.annotation.NotifyChange;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.select.Selectors;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zk.ui.util.Clients;
import org.zkoss.zul.ListModelList;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Messagebox;

import com.actividades.correo.EnviarCorreo;
import com.actividades.modelo.Empleado;
import com.actividades.modelo.EmpleadoDAO;
import com.actividades.modelo.Queja;
import com.actividades.modelo.QuejaDAO;
import com.actividades.util.Constantes;
import com.actividades.util.ControllerHelper;
import com.actividades.util.PrintReport;

public class QQuejaPublicarC {
	@Wire Listbox lstQuejas;
	
	List<Queja> listaQueja;
	Queja quejaSeleccionada;
	String textoBuscar;
	QuejaDAO quejaDAO = new QuejaDAO();
	EmpleadoDAO empleadoDAO = new EmpleadoDAO();
	
	
	@AfterCompose
	public void aferCompose(@ContextParam(ContextType.VIEW) Component view) throws IOException{
		Selectors.wireComponents(view, this, false);
		textoBuscar="";
		buscar();
	}
	@SuppressWarnings({ "unchecked", "rawtypes" })
	@GlobalCommand("Queja.buscarPorResponsable")
	@Command
	@NotifyChange({"listaQueja"})
	public void buscar(){
		if (listaQueja != null) {
			listaQueja = null; 
		}
		List<Queja> lista = quejaDAO.buscarPorEstado(Constantes.QUEJA_REVISION);
		listaQueja = lista;
		lstQuejas.setModel(new ListModelList(listaQueja));
		quejaSeleccionada = null;
		if(listaQueja.size() == 0) {
			Clients.showNotification("No hay datos para mostrar.!!");
		}
	}
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Command
	public void publicarQueja() {
		
		Messagebox.show("¿Desea Publicar las quejas y pasar al Departamento de Gobernación?", "Confirmación", Messagebox.YES | Messagebox.NO, Messagebox.QUESTION, new EventListener() {
			@Override
			public void onEvent(Event event) throws Exception {
				if (event.getName().equals("onYes")) {
					try {
						//
						
						int contadorQuejas = 0;
						contadorQuejas = listaQueja.size();
						enviarCorreoGobernador(contadorQuejas);
						
						quejaDAO.getEntityManager().getTransaction().begin();
						for(Queja queja : listaQueja) {
							queja.setEstadoQueja(Constantes.QUEJA_PUBLICADA);
							queja.setFechaAceptacion(new Date());
							quejaDAO.getEntityManager().merge(queja);
						}
						quejaDAO.getEntityManager().getTransaction().commit();
						Clients.showNotification("Transaccion ejecutada con exito.");
						
						// Actualiza la lista
						BindUtils.postGlobalCommand(null, null, "Queja.buscarPorResponsable", null);

					} catch (Exception e) {
						e.printStackTrace();
						quejaDAO.getEntityManager().getTransaction().rollback();
					}
				}
			}
		});
//		EnviarCorreoUtil util = new EnviarCorreoUtil();
//		util.SendMail();
	}
	private void enviarCorreoGobernador(int contadorQuejas) {
		List<Empleado> resultado = empleadoDAO.buscarEmpleadoPorTipoUsuarioDepartamento(Constantes.ID_AUTORIDAD_MAXIMA, Constantes.ID_DEPARTAMENTO_GOBERNACION);
		if(resultado.size() > 0) {
			Empleado gobernador = resultado.get(0);
			if(gobernador.getPersona().getEmail() != null) {
				if(ControllerHelper.validarEmail(gobernador.getPersona().getEmail()) == false) {
					Clients.showNotification("El Gobernador/a tiene un correo No Válido");
				}else {
					//crear el archivo adjunto
					Map<String, Object> params = new HashMap<String, Object>();
					params.put("ESTADO_QUEJA", Constantes.QUEJA_REVISION);
					PrintReport report = new PrintReport();
					String archivoAdjunto = report.crearArchivo("/reportes/quejas.jasper",empleadoDAO, params);
					
					String adjunto = archivoAdjunto;
					String[] adjuntos = adjunto.split(",");
					String asunto;
					String destinatario;
					String mensaje;
					int servidor;
					String[] destinatarios;
					asunto = "Notificación de quejas";
					destinatario = gobernador.getPersona().getEmail();
					System.out.println(gobernador.getPersona().getEmail());
					destinatarios = destinatario.split(";");
					servidor = 0;
					mensaje = "Estimado/a Gobernador/a \n\n" + gobernador.getPersona().getNombre() + " " + gobernador.getPersona().getApellido() + "\nPRESENTE\n";
					mensaje = mensaje + "\n\nSe ha publicado hacia su bandeja " + contadorQuejas + " nuevas quejas de los diferentes departamentos";
					mensaje = mensaje + "\n\n\nSaludos Cordiales!";
					mensaje = mensaje + "\n\nAtentamente\n\n\n_______________________\nDepartamento de Comunicaciones";
					EnviarCorreo miHilo = new EnviarCorreo(adjunto, adjuntos, destinatarios, servidor, asunto, mensaje);
					miHilo.enviarCorreo();
					Clients.showNotification("Notificación enviada al correo del Gobernador/a");
				}
			}else {
				Clients.showNotification("El Gobernador/a no tiene registrado una dirección de correo electronico");
			}
		}
	}
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Command
	public void darBaja() {
		if(quejaSeleccionada == null) {
			Messagebox.show("Debe seleccionar una QUEJA");
			return;
		}
		Messagebox.show("¿Seguro desea dar de baja la Queja seleccionada?", "Confirmación", Messagebox.YES | Messagebox.NO, Messagebox.QUESTION, new EventListener() {
			@Override
			public void onEvent(Event event) throws Exception {
				if (event.getName().equals("onYes")) {
					try {
						quejaDAO.getEntityManager().getTransaction().begin();
						for(Queja queja : listaQueja) {
							queja.setEstado(Constantes.ESTADO_INACTIVO);
							quejaDAO.getEntityManager().merge(queja);
						}
						quejaDAO.getEntityManager().getTransaction().commit();
						Clients.showNotification("Transaccion ejecutada con exito.");

						// Actualiza la lista
						BindUtils.postGlobalCommand(null, null, "Queja.buscarPorResponsable", null);

					} catch (Exception e) {
						e.printStackTrace();
						quejaDAO.getEntityManager().getTransaction().rollback();
					}
				}
			}
		});	
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
	public String getTextoBuscar() {
		return textoBuscar;
	}
	public void setTextoBuscar(String textoBuscar) {
		this.textoBuscar = textoBuscar;
	}
	
}
