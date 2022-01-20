package com.actividades.controlador.dashboard;

import java.awt.Color;
import java.awt.GradientPaint;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.mail.MessagingException;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.CategoryAxis;
import org.jfree.chart.axis.CategoryLabelPositions;
import org.jfree.chart.encoders.EncoderUtil;
import org.jfree.chart.encoders.ImageFormat;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.renderer.category.BarRenderer;
import org.jfree.data.category.DefaultCategoryDataset;
import org.zkoss.bind.annotation.AfterCompose;
import org.zkoss.bind.annotation.BindingParam;
import org.zkoss.bind.annotation.Command;
import org.zkoss.bind.annotation.ContextParam;
import org.zkoss.bind.annotation.ContextType;
import org.zkoss.image.AImage;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.select.Selectors;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zk.ui.util.Clients;
import org.zkoss.zul.Div;
import org.zkoss.zul.Image;
import org.zkoss.zul.Label;
import org.zkoss.zul.Listheader;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

import com.actividades.modelo.Actividad;
import com.actividades.modelo.ActividadDAO;
import com.actividades.modelo.ActividadExterna;
import com.actividades.modelo.ActividadExternaDAO;
import com.actividades.modelo.Departamento;
import com.actividades.modelo.DepartamentoDAO;
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
	@Wire private Listheader listHeaderVer;
	@Wire private Div divQuejas;
	@Wire private Div divActividadesDepartamento;
	
	@Wire private Textbox txtAnio;
	
	@Wire private Image imGraficoActividades;
	@Wire private Image imGraficoQuejas;
	@Wire private Label lblQuejas;
	
	@Wire private Image imGraficoActividadesPorDepartamento;
	@Wire private Label lblActividadesPorDepartamento;
	
	EmpleadoDAO usuarioDAO = new EmpleadoDAO();
	ActividadDAO actividadDAO = new ActividadDAO();
	ActividadExternaDAO actividadExternaDAO = new ActividadExternaDAO();
	DepartamentoDAO departamentoDAO = new DepartamentoDAO();
	QuejaDAO quejaDAO = new QuejaDAO();
	List<Trabajadores> listaEmpleados;
	List<Mes> listaMes;
	Mes mesSeleccionado;
	SimpleDateFormat formatoFecha = new SimpleDateFormat("dd/MM/yyyy");
	
	@AfterCompose
	public void aferCompose(@ContextParam(ContextType.VIEW) Component view) throws IOException, MessagingException{
		Selectors.wireComponents(view, this, false);
		listHeaderVer.setVisible(false);
		divQuejas.setVisible(false);
		divActividadesDepartamento.setVisible(false);
		this.cargarListadoMeses();
		
		Date date = new Date();
        ZoneId timeZone = ZoneId.systemDefault();
        LocalDate getLocalDate = date.toInstant().atZone(timeZone).toLocalDate();
		txtAnio.setValue(String.valueOf(getLocalDate.getYear()));
		this.verificarActividadesExternas();
		this.contarCantidades();
		this.cargarGraficoActividades();
		this.cargarGraficoQuejas();
		this.cargarGraficoActividadesPorDepartamento();
	}
	
	@Command
	public void verActividades(@BindingParam("empleado") Trabajadores emp){
		if(emp == null) {
			Clients.showNotification("Seleccione una opción de la lista.");
			return;
		}
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("Empleado", emp.getEmp());
		params.put("Anio", Integer.parseInt(txtAnio.getText()));
		params.put("Mes", mesSeleccionado.getIdMes());
		Window ventanaCargar = (Window) Executions.createComponents("/formularios/dashboard/ListadoActividades.zul", null, params);
		ventanaCargar.doModal();
	}
	
	private void verificarActividadesExternas() {
		Empleado usuario = usuarioDAO.getUsuario(SecurityUtil.getUser().getUsername().trim());
		if(usuario.getTipoUsuario().getIdTipoUsuario() != Constantes.ID_AUTORIDAD_MAXIMA) {
			List<ActividadExterna> listaExterna = this.actividadExternaDAO.obtenerActividadesPendienteAsignacion();
			if(listaExterna.size() > 0) {
				Clients.showNotification("Gobernadora ha publicado actividades a realizar... Revisar!!");
			}
		}else {
			listHeaderVer.setVisible(true);
			divQuejas.setVisible(true);
			divActividadesDepartamento.setVisible(true);
		}
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
			//Listado de empleados
			Empleado usuario = usuarioDAO.getUsuario(SecurityUtil.getUser().getUsername().trim());
			//verificar si es jefe o si es la maxima autoridad
			if(usuario.getTipoUsuario().getIdTipoUsuario() != Constantes.ID_AUTORIDAD_MAXIMA) {
				List<Empleado> lista = usuarioDAO.buscarPorDepartamentoYTipoUsuario(usuario.getDepartamento().getIdDepartamento(), Constantes.ID_ASISTENTE);
				List<Trabajadores> agg = new ArrayList<>();
				for(Empleado emp : lista) {
					Trabajadores t = new Trabajadores();
					emp.getPersona().setNombre(emp.getPersona().getNombre().toUpperCase());
					emp.getPersona().setApellido(emp.getPersona().getApellido().toUpperCase());
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
			}else {//es la maxima autoridad
				List<Empleado> lista = usuarioDAO.getListausuarioBuscar("");
				List<Empleado> emp = new ArrayList<>();
				for(Empleado ls : lista) {
					if(ls.getTipoUsuario().getIdTipoUsuario() != Constantes.ID_AUTORIDAD_MAXIMA)
						emp.add(ls);
				}
				List<Trabajadores> agg = new ArrayList<>();
				for(Empleado e : emp) {
					Trabajadores t = new Trabajadores();
					e.getPersona().setNombre(e.getPersona().getNombre().toUpperCase());
					e.getPersona().setApellido(e.getPersona().getApellido().toUpperCase() + " - " + e.getCargo().getDescripcion().toUpperCase() + " DE " + e.getDepartamento().getNombre().toUpperCase());
					t.setEmp(e);
					if(e.getFoto() != null) {
						File archivo = new File(e.getFoto());
						if(archivo.exists())
							t.setFoto(new AImage(e.getFoto()));
					}
					agg.add(t);
				}
				listaEmpleados = agg;
				lblCantidadEmpleados.setValue(String.valueOf(listaEmpleados.size()));
			}
			
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
	private void cargarGraficoActividades() throws IOException {
		List<Actividad> actividades = new ArrayList<>();
		Empleado usuario = usuarioDAO.getUsuario(SecurityUtil.getUser().getUsername().trim());
		if(usuario.getTipoUsuario().getIdTipoUsuario() != Constantes.ID_AUTORIDAD_MAXIMA) {
			actividades = this.actividadDAO.buscarPorEmpleadoTipoActividad(usuario.getIdEmpleado(), Constantes.ID_TIPO_PRIMORDIALES);
		}else {
			actividades = this.actividadDAO.buscarActividadesPublicadas();
		}
		
		Integer contadorActividadesPoliticasPublicadas = 0;
		Integer contadorActividadesCulturalPublicadas = 0;
		Integer contadorActividadesDeportivoPublicadas = 0;
		Integer contadorActividadesInternoPublicadas = 0;
		Integer contadorActividadesSaludPublicadas = 0;
		
		Integer contadorActividadesPoliticasNoPublicadas = 0;
		Integer contadorActividadesCulturalNoPublicadas = 0;
		Integer contadorActividadesDeportivoNoPublicadas = 0;
		Integer contadorActividadesInternoNoPublicadas = 0;
		Integer contadorActividadesSaludNoPublicadas = 0;
		
		for(Actividad ac : actividades) {
			ZoneId timeZone = ZoneId.systemDefault();
	        LocalDate getLocalDate = ac.getFecha().toInstant().atZone(timeZone).toLocalDate();
	        if(getLocalDate.getYear() == Integer.parseInt(txtAnio.getText()) && getLocalDate.getMonthValue() == mesSeleccionado.getIdMes()) {
	        	if(ac.getClaseActividad().getIdClaseActividad() == Constantes.CODIGO_CLASE_ACTIVIDAD_POLITICO) {
	        		if(ac.getEstadoPublicado().equals(Constantes.ESTADO_PUBLICADO))
	        			contadorActividadesPoliticasPublicadas ++;
	        		else
	        			contadorActividadesPoliticasNoPublicadas ++;
	        	}else if(ac.getClaseActividad().getIdClaseActividad() == Constantes.CODIGO_CLASE_ACTIVIDAD_CULTURAL) {
	        		if(ac.getEstadoPublicado().equals(Constantes.ESTADO_PUBLICADO))
	        			contadorActividadesCulturalPublicadas ++;
	        		else
	        			contadorActividadesCulturalNoPublicadas ++;
	        	}else if(ac.getClaseActividad().getIdClaseActividad() == Constantes.CODIGO_CLASE_ACTIVIDAD_DEPORTIVO) {
	        		if(ac.getEstadoPublicado().equals(Constantes.ESTADO_PUBLICADO))
	        			contadorActividadesDeportivoPublicadas ++;
	        		else
	        			contadorActividadesDeportivoNoPublicadas ++;
	        	}else if(ac.getClaseActividad().getIdClaseActividad() == Constantes.CODIGO_CLASE_ACTIVIDAD_INTERNO) {
	        		if(ac.getEstadoPublicado().equals(Constantes.ESTADO_PUBLICADO))
	        			contadorActividadesInternoPublicadas ++;
	        		else
	        			contadorActividadesInternoNoPublicadas ++;
	        	}else if(ac.getClaseActividad().getIdClaseActividad() == Constantes.CODIGO_CLASE_ACTIVIDAD_SALUD) {
	        		if(ac.getEstadoPublicado().equals(Constantes.ESTADO_PUBLICADO))
	        			contadorActividadesSaludPublicadas ++;
	        		else
	        			contadorActividadesSaludNoPublicadas ++;
	        	}
	        }
		}

		DefaultCategoryDataset dataset = new DefaultCategoryDataset();
		dataset.addValue(contadorActividadesPoliticasPublicadas, "Publicadas", "Políticas");
		dataset.addValue(contadorActividadesCulturalPublicadas, "Publicadas", "Cultural");
		dataset.addValue(contadorActividadesDeportivoPublicadas, "Publicadas", "Deportivo");
		dataset.addValue(contadorActividadesInternoPublicadas, "Publicadas", "Interno");
		dataset.addValue(contadorActividadesSaludPublicadas, "Publicadas", "Salud");
		
		dataset.addValue(contadorActividadesPoliticasNoPublicadas, "No Publicadas", "Políticas");
		dataset.addValue(contadorActividadesCulturalNoPublicadas, "No Publicadas", "Cultural");
		dataset.addValue(contadorActividadesDeportivoNoPublicadas, "No Publicadas", "Deportivo");
		dataset.addValue(contadorActividadesInternoNoPublicadas, "No Publicadas", "Interno");
		dataset.addValue(contadorActividadesSaludNoPublicadas, "No Publicadas", "Salud");
		
		JFreeChart chart = ChartFactory.createBarChart("Cantidad de actividades " + mesSeleccionado.getMes(), null, null, dataset, PlotOrientation.VERTICAL, true, true, false);
		chart.setBackgroundPaint( Color.WHITE );
		
		CategoryPlot plot = (CategoryPlot) chart.getPlot();
		//fondo del grafico
		plot.setBackgroundPaint( Color.WHITE );
		//lineas divisoras
		plot.setDomainGridlinesVisible( true );
        plot.setRangeGridlinePaint( Color.BLACK );
		
        BarRenderer renderer = (BarRenderer) plot.getRenderer();
        renderer.setDrawBarOutline(false);
        
        //Dar color a ada barra
        GradientPaint gp0= new GradientPaint(0.0f,0.0f,Color.blue,0.0f,0.0f,new Color(0,0,64));
        GradientPaint gp1= new GradientPaint(0.0f,0.0f,Color.green,0.0f,0.0f,new Color(0,64,0));
        
        renderer.setSeriesPaint(0,gp0);
        renderer.setSeriesPaint(1,gp1);

        CategoryAxis domainAxis = plot.getDomainAxis();
        domainAxis.setCategoryLabelPositions(
                CategoryLabelPositions.createUpRotationLabelPositions(Math.PI/6.0));
        
        
        
		BufferedImage bi = chart.createBufferedImage(700, 350, BufferedImage.SCALE_REPLICATE , null);
		byte[] bytes = EncoderUtil.encode(bi, ImageFormat.PNG, true);
		AImage imagen = new AImage("Actividades", bytes);
		
		imGraficoActividades.setContent(imagen);
	}
	private void cargarGraficoQuejas() throws IOException {
		lblQuejas.setValue("Gráfica de quejas realizadas el mes de " + mesSeleccionado.getMes() + " del año " + txtAnio.getText());
		
		List<Departamento> listaDepartamentos = this.departamentoDAO.getDepartamentosActivos();
		DefaultCategoryDataset dataset = new DefaultCategoryDataset();
		for(Departamento dep : listaDepartamentos) {
			if(dep.getEmpleados().size() > 0) {
				List<Queja> listaQuejas = this.quejaDAO.buscarPorDepartamento(dep.getIdDepartamento());
				Integer cantidadQuejas = 0;
				for(Queja q : listaQuejas) {
					ZoneId timeZone = ZoneId.systemDefault();
			        LocalDate getLocalDate = q.getFechaAceptacion().toInstant().atZone(timeZone).toLocalDate();
			        if(getLocalDate.getYear() == Integer.parseInt(txtAnio.getText()) && getLocalDate.getMonthValue() == mesSeleccionado.getIdMes()) {
			        	cantidadQuejas = cantidadQuejas + 1;
			        }
				}
				dataset.addValue(cantidadQuejas, "Quejas", dep.getNombre().substring(0, 6));
			}
		}
		
		JFreeChart chart = ChartFactory.createBarChart("Cantidad de quejas", null, null, dataset, PlotOrientation.VERTICAL, true, true, false);
		chart.setBackgroundPaint( Color.white );
		
		CategoryPlot plot = (CategoryPlot) chart.getPlot();
		//fondo del grafico
		plot.setBackgroundPaint( Color.WHITE );
		//lineas divisoras
		plot.setDomainGridlinesVisible( true );
        plot.setRangeGridlinePaint( Color.BLACK );
		
        BarRenderer renderer = (BarRenderer) plot.getRenderer();
        renderer.setDrawBarOutline(false);
        
        //Dar color a ada barra
        GradientPaint gp0= new GradientPaint(0.0f,0.0f,Color.cyan,0.0f,0.0f,new Color(0,0,64));
        
        renderer.setSeriesPaint(0,gp0);

        CategoryAxis domainAxis = plot.getDomainAxis();
        domainAxis.setCategoryLabelPositions(
                CategoryLabelPositions.createUpRotationLabelPositions(Math.PI/4.0));
		
		BufferedImage bi = chart.createBufferedImage(700, 350, BufferedImage.SCALE_REPLICATE , null);
		byte[] bytes = EncoderUtil.encode(bi, ImageFormat.PNG, true);
		AImage imagen = new AImage("Quejas", bytes);
		
		imGraficoQuejas.setContent(imagen);
	}
	private void cargarGraficoActividadesPorDepartamento() throws IOException {
		lblActividadesPorDepartamento.setValue("Gráfica de actividades realizadas el mes de " + mesSeleccionado.getMes() + " del año " + txtAnio.getText());
		List<Departamento> listaDepartamentos = this.departamentoDAO.getDepartamentosActivos();
		DefaultCategoryDataset dataset = new DefaultCategoryDataset();
		for(Departamento dep : listaDepartamentos) {
			List<Actividad> listaActividades = this.actividadDAO.buscarActividadesPrincipalesPorDepartamento(dep.getIdDepartamento());
			Integer cantidadPublicadas = 0;
			Integer cantidadNoPublicadas = 0;
			
			for(Actividad ac : listaActividades) {
				ZoneId timeZone = ZoneId.systemDefault();
		        LocalDate getLocalDate = ac.getFecha().toInstant().atZone(timeZone).toLocalDate();
		        if(getLocalDate.getYear() == Integer.parseInt(txtAnio.getText()) && getLocalDate.getMonthValue() == mesSeleccionado.getIdMes()) {
		        	if(ac.getEstadoPublicado().equals(Constantes.ESTADO_PUBLICADO))
						cantidadPublicadas ++;
					else
						cantidadNoPublicadas ++;
		        }
			}
			dataset.addValue(cantidadPublicadas, "Publicadas", dep.getNombre().substring(0, 6));
			dataset.addValue(cantidadNoPublicadas, "No Publicadas", dep.getNombre().substring(0, 6));
		}
		
		JFreeChart chart = ChartFactory.createBarChart("Cantidad de actividades", null, null, dataset, PlotOrientation.VERTICAL, true, true, false);
		chart.setBackgroundPaint( Color.white );
		
		CategoryPlot plot = (CategoryPlot) chart.getPlot();
		//fondo del grafico
		plot.setBackgroundPaint( Color.WHITE );
		//lineas divisoras
		plot.setDomainGridlinesVisible( true );
        plot.setRangeGridlinePaint( Color.BLACK );
		
        BarRenderer renderer = (BarRenderer) plot.getRenderer();
        renderer.setDrawBarOutline(false);
        
        //Dar color a ada barra
        GradientPaint gp0= new GradientPaint(0.0f,0.0f,Color.gray,0.0f,0.0f,new Color(0,0,64));
        GradientPaint gp1= new GradientPaint(0.0f,0.0f,Color.magenta,0.0f,0.0f,new Color(0,0,64));
        
        renderer.setSeriesPaint(0,gp0);
        renderer.setSeriesPaint(1,gp1);

        CategoryAxis domainAxis = plot.getDomainAxis();
        domainAxis.setCategoryLabelPositions(
                CategoryLabelPositions.createUpRotationLabelPositions(Math.PI/4.0));
		
		
		BufferedImage bi = chart.createBufferedImage(700, 350, BufferedImage.SCALE_REPLICATE , null);
		byte[] bytes = EncoderUtil.encode(bi, ImageFormat.PNG, true);
		AImage imagen = new AImage("Actividades", bytes);
		
		imGraficoActividadesPorDepartamento.setContent(imagen);
	}
	@Command
	public void actualizar() throws IOException{
		if(mesSeleccionado == null) {
			Clients.showNotification("Debe seleccionar un mes");
			return;
		}
		if(txtAnio.getText().isEmpty()) {
			Clients.showNotification("Debe registrar un año");
			return;
		}
		this.contarCantidades();
		this.cargarGraficoActividades();
		this.cargarGraficoActividadesPorDepartamento();
		this.cargarGraficoQuejas();
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