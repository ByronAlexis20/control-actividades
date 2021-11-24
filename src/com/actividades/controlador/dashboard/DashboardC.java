package com.actividades.controlador.dashboard;

import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.mail.MessagingException;

import org.zkoss.bind.annotation.AfterCompose;
import org.zkoss.bind.annotation.Command;
import org.zkoss.bind.annotation.ContextParam;
import org.zkoss.bind.annotation.ContextType;
import org.zkoss.image.AImage;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.select.Selectors;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zk.ui.util.Clients;
import org.zkoss.zul.Image;
import org.zkoss.zul.Label;
import org.zkoss.zul.Textbox;

import com.actividades.modelo.Actividad;
import com.actividades.modelo.ActividadDAO;
import com.actividades.modelo.Empleado;
import com.actividades.modelo.EmpleadoDAO;
import com.actividades.modelo.Queja;
import com.actividades.modelo.QuejaDAO;
import com.actividades.util.Constantes;
import com.actividades.util.SecurityUtil;

public class DashboardC {
	
	@Wire private Label lblCantidadEmpleados;
	@Wire private Label lblActividadesPendientes;
	@Wire private Label lblActividadesRechazadas;
	@Wire private Label lblQuejaRealizada;
	
	@Wire private Textbox txtAnio;
	
	@Wire private Image imGraficoResumenEmergencia;
	EmpleadoDAO usuarioDAO = new EmpleadoDAO();
	ActividadDAO actividadDAO = new ActividadDAO();
	QuejaDAO quejaDAO = new QuejaDAO();
	List<Trabajadores> listaEmpleados;
	List<Mes> listaMes;
	Mes mesSeleccionado;
	
	@AfterCompose
	public void aferCompose(@ContextParam(ContextType.VIEW) Component view) throws IOException, MessagingException{
		Selectors.wireComponents(view, this, false);
		this.cargarListadoMeses();
		
		Date date = new Date();
        ZoneId timeZone = ZoneId.systemDefault();
        LocalDate getLocalDate = date.toInstant().atZone(timeZone).toLocalDate();
		txtAnio.setValue(String.valueOf(getLocalDate.getYear()));
		
		this.contarCantidades();
	}
	private void cargarListadoMeses() {
		try {
			List<Mes> lista = new ArrayList<>();
			Mes mes1 = new Mes(1, "Enero");
			lista.add(mes1);
			Mes mes2 = new Mes(2, "Febrero");
			lista.add(mes2);
			Mes mes3 = new Mes(3, "Marzo");
			lista.add(mes3);
			Mes mes4 = new Mes(4, "Abril");
			lista.add(mes4);
			Mes mes5 = new Mes(5, "Mayo");
			lista.add(mes5);
			Mes mes6 = new Mes(6, "Junio");
			lista.add(mes6);
			Mes mes7 = new Mes(7, "Julio");
			lista.add(mes7);
			Mes mes8 = new Mes(8, "Agosto");
			lista.add(mes8);
			Mes mes9 = new Mes(9, "Septiembre");
			lista.add(mes9);
			Mes mes10 = new Mes(10, "Octubre");
			lista.add(mes10);
			Mes mes11 = new Mes(11, "Noviembre");
			lista.add(mes11);
			Mes mes12 = new Mes(12, "Diciembre");
			lista.add(mes12);
			listaMes = lista;
			
			Date date = new Date();
	        ZoneId timeZone = ZoneId.systemDefault();
	        LocalDate getLocalDate = date.toInstant().atZone(timeZone).toLocalDate();
			
			for(Mes m : lista) {
				if(m.getIdMes() == getLocalDate.getMonthValue()) {
					mesSeleccionado = m;
				}
			}
		}catch(Exception ex) {
			System.out.println(ex.getMessage());
		}
	}
	private void contarCantidades() {
		try {
			Empleado usuario = usuarioDAO.getUsuario(SecurityUtil.getUser().getUsername().trim());
			List<Empleado> lista = usuarioDAO.buscarPorDepartamentoYTipoUsuario(usuario.getDepartamento().getIdDepartamento(), Constantes.ID_ASISTENTE);
			List<Trabajadores> agg = new ArrayList<>();
			for(Empleado emp : lista) {
				Trabajadores t = new Trabajadores();
				t.setEmp(emp);
				if(emp.getFoto() != null) {
					File archivo = new File(emp.getFoto());
					if(archivo.exists())
						t.setFoto(new AImage(emp.getFoto()));
				}
				agg.add(t);
			}
			listaEmpleados = agg;
			lblCantidadEmpleados.setValue(String.valueOf(listaEmpleados.size()));
			
			//contar actividades rechazadas
			lblActividadesRechazadas.setValue("0");
			lblActividadesPendientes.setValue("0");
			lblQuejaRealizada.setValue("0");
			List<Empleado> jefeArea = usuarioDAO.buscarPorDepartamento(usuario.getDepartamento().getIdDepartamento());
			Integer cont = 0;
			if(jefeArea.size() > 0) {
				List<Actividad> acti = actividadDAO.obtenerRechazada(jefeArea.get(0).getIdEmpleado());
				for(Actividad ac : acti) {
					ZoneId timeZone = ZoneId.systemDefault();
			        LocalDate getLocalDate = ac.getFecha().toInstant().atZone(timeZone).toLocalDate();
			        if(getLocalDate.getYear() == Integer.parseInt(txtAnio.getText()) && getLocalDate.getMonthValue() == mesSeleccionado.getIdMes()) {
			        	cont = cont + 1;
			        }
				}
				lblActividadesRechazadas.setValue(String.valueOf(cont));
				cont = 0;
				
				List<Actividad> pend = actividadDAO.obtenerPendiente(jefeArea.get(0).getIdEmpleado());
				for(Actividad ac : pend) {
					ZoneId timeZone = ZoneId.systemDefault();
			        LocalDate getLocalDate = ac.getFecha().toInstant().atZone(timeZone).toLocalDate();
			        if(getLocalDate.getYear() == Integer.parseInt(txtAnio.getText()) && getLocalDate.getMonthValue() == mesSeleccionado.getIdMes()) {
			        	cont = cont + 1;
			        }
				}
				lblActividadesPendientes.setValue(String.valueOf(cont));
				
				cont = 0;
				List<Queja> listaQueja = quejaDAO.buscarPorResponsable(jefeArea.get(0).getIdEmpleado(), "", "");
				for(Queja q : listaQueja) {
					ZoneId timeZone = ZoneId.systemDefault();
			        LocalDate getLocalDate = q.getFechaEnvio().toInstant().atZone(timeZone).toLocalDate();
			        if(getLocalDate.getYear() == Integer.parseInt(txtAnio.getText()) && getLocalDate.getMonthValue() == mesSeleccionado.getIdMes()) {
			        	cont = cont + 1;
			        }
				}
				lblQuejaRealizada.setValue(String.valueOf(cont));
			}
		}catch(Exception ex) {
			System.out.println(ex.getMessage());
		}
	}
	@Command
	public void actualizar(){
		if(mesSeleccionado == null) {
			Clients.showNotification("Debe seleccionar un mes");
			return;
		}
		if(txtAnio.getText().isEmpty()) {
			Clients.showNotification("Debe registrar un a�o");
			return;
		}
		this.contarCantidades();
	}
	public List<Trabajadores> getListaEmpleados() {
		return listaEmpleados;
	}
	public void setListaEmpleados(List<Trabajadores> listaEmpleados) {
		this.listaEmpleados = listaEmpleados;
	}
	public List<Mes> getListaMes() {
		return listaMes;
	}
	public void setListaMes(List<Mes> listaMes) {
		this.listaMes = listaMes;
	}
	public Mes getMesSeleccionado() {
		return mesSeleccionado;
	}
	public void setMesSeleccionado(Mes mesSeleccionado) {
		this.mesSeleccionado = mesSeleccionado;
	}

	public class Trabajadores {
		private Empleado emp;
		private AImage foto;
		public Empleado getEmp() {
			return emp;
		}
		public void setEmp(Empleado emp) {
			this.emp = emp;
		}
		public AImage getFoto() {
			return foto;
		}
		public void setFoto(AImage foto) {
			this.foto = foto;
		}
		
	}
	public class Mes {
		private Integer idMes;
		private String mes;
		public Mes(Integer idMes, String mes) {
			super();
			this.idMes = idMes;
			this.mes = mes;
		}
		public Integer getIdMes() {
			return idMes;
		}
		public void setIdMes(Integer idMes) {
			this.idMes = idMes;
		}
		public String getMes() {
			return mes;
		}
		public void setMes(String mes) {
			this.mes = mes;
		}
	}
}