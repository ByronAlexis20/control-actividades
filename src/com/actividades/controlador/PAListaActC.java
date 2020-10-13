package com.actividades.controlador;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.encoders.EncoderUtil;
import org.jfree.chart.encoders.ImageFormat;
import org.jfree.chart.plot.PiePlot3D;
import org.jfree.data.general.DefaultPieDataset;
import org.zkoss.bind.annotation.AfterCompose;
import org.zkoss.bind.annotation.BindingParam;
import org.zkoss.bind.annotation.Command;
import org.zkoss.bind.annotation.ContextParam;
import org.zkoss.bind.annotation.ContextType;
import org.zkoss.bind.annotation.GlobalCommand;
import org.zkoss.bind.annotation.NotifyChange;
import org.zkoss.image.AImage;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.select.Selectors;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zk.ui.util.Clients;
import org.zkoss.zul.Image;
import org.zkoss.zul.ListModelList;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Messagebox;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;
import org.zkoss.zul.Messagebox.ClickEvent;

import com.actividades.modelo.Actividad;
import com.actividades.modelo.ActividadDAO;
import com.actividades.modelo.Agenda;
import com.actividades.util.Constantes;

public class PAListaActC {
	@Wire Window winGraficoAct;
	@Wire Listbox lstActividades;
	@Wire Textbox txtAgenda;
	@Wire Textbox txtNoActividades;
	@Wire Textbox txtNoPublicadas;
	@Wire Textbox txtFechaInicio;
	@Wire Textbox txtFechaFin;
	@Wire Textbox txtRechazadas;
	@Wire Image imGrafico;
	
	List<Actividad> listaActividades;
	Agenda agenda;
	ActividadDAO actividadDAO = new ActividadDAO();
	
	@AfterCompose
	public void aferCompose(@ContextParam(ContextType.VIEW) Component view) throws IOException{
		Selectors.wireComponents(view, this, false);
		agenda = (Agenda) Executions.getCurrent().getArg().get("Agenda");
		listaActividades = new ArrayList<>();
		if(agenda != null) {
			txtAgenda.setText(agenda.getDescripcion());
			txtFechaInicio.setText(new SimpleDateFormat("dd/MM/yyyy").format(agenda.getFechaInicio()));
			txtFechaFin.setText(new SimpleDateFormat("dd/MM/yyyy").format(agenda.getFechaFin()));
			cargarActividades();
		}
			
	}
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	@GlobalCommand("Departamento.buscarActivosReporte")
	@Command
	@NotifyChange({"listaDepartamentos"})
	public void cargarActividades() {
		if(listaActividades != null)
			listaActividades = null;
		int cantidadActividades = 0;
		int cantidadPublicadas = 0;
		int cantidadRechazadas = 0;
		
		List<Actividad> lista = new ArrayList<>();
		List<Actividad> listaAgregar = new ArrayList<>();
		List<Actividad> resultado = actividadDAO.obtenerActividad(agenda.getIdAgenda());
		for(Actividad act : resultado) {
			cantidadActividades ++;
			if(act.getEstadoPublicado().equals(Constantes.ESTADO_PUBLICADO)) {
				cantidadPublicadas ++;
				lista.add(act);
			}
			if(act.getEstadoActividad().equals(Constantes.ESTADO_RECHAZADO)) {
				cantidadRechazadas ++;
			}
		}
		
		for(Actividad act : lista) {
			if(!act.getEstadoActividad().equals(Constantes.ESTADO_RECHAZADO)) {
				listaAgregar.add(act);
			}
		}
		txtNoActividades.setText(String.valueOf(cantidadActividades) + " Actividades Programadas");
		txtNoPublicadas.setText(String.valueOf(cantidadPublicadas) + " Actividades Publicadas");
		txtRechazadas.setText(String.valueOf(cantidadRechazadas) + " Actividades Rechazadas");
		
		try {
			realizarGrafica(cantidadActividades,cantidadPublicadas,cantidadRechazadas);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		listaActividades = listaAgregar;
		lstActividades.setModel(new ListModelList(listaActividades));
	}
	
	private void realizarGrafica(int totalActividades, int totalPublicadas, int totalRechazadas) throws IOException {
		int pendientes = 0;
		double tPendiente  = 100.0;
		double tRechazadas = 0.0;
		double tPublicadas = 0.0;
		if(totalActividades > 0) {
			pendientes = totalActividades - totalPublicadas;
			pendientes = pendientes - totalRechazadas;
			tPendiente = (pendientes * 100) / totalActividades;
			tRechazadas = (totalRechazadas * 100) / totalActividades;
			tPublicadas = (totalPublicadas * 100) / totalActividades;
		}
		
		
		DefaultPieDataset pieDataset = new DefaultPieDataset();
		
		pieDataset.setValue("PUBLICADAS [" + totalPublicadas + "]" , tPublicadas);
		pieDataset.setValue("PENDIENTES [" + pendientes + "]", tPendiente);
		pieDataset.setValue("RECHAZADAS [" + totalRechazadas + "]", tRechazadas);
		JFreeChart chart = ChartFactory.createPieChart3D("Actividades", pieDataset,true,true,true);
		PiePlot3D plot = (PiePlot3D) chart.getPlot();
		plot.setForegroundAlpha(0.5f);
		BufferedImage bi = chart.createBufferedImage(500, 300, BufferedImage.TRANSLUCENT , null);
		byte[] bytes = EncoderUtil.encode(bi, ImageFormat.PNG, true);
		
		AImage image = new AImage("Pie Chart", bytes);
		imGrafico.setContent(image);
	}
	@Command
	public void verEvidencias(@BindingParam("actividad") Actividad seleccion){
		if(seleccion == null) {
			Clients.showNotification("Seleccione una opción de la lista.");
			return;
		}
		// Actualiza la instancia antes de enviarla a editar.
		actividadDAO.getEntityManager().refresh(seleccion);		
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("Actividad", seleccion);
		Window ventanaCargar = (Window) Executions.createComponents("/formularios/reportes/actividades/RAEvidencia.zul", null, params);
		ventanaCargar.doModal();
	}
	@Command
	public void darBaja(@BindingParam("actividad") Actividad seleccion) {
		if(seleccion == null) {
			Clients.showNotification("Seleccione una opción de la lista.");
			return;
		}
		actividadDAO.getEntityManager().refresh(seleccion);
		
		EventListener<ClickEvent> clickListener = new EventListener<Messagebox.ClickEvent>() {
			public void onEvent(ClickEvent event) throws Exception {
				if(Messagebox.Button.YES.equals(event.getButton())) {
					
					
					actividadDAO.getEntityManager().getTransaction().begin();
					seleccion.setEstadoActividad(Constantes.ESTADO_RECHAZADO);
					actividadDAO.getEntityManager().getTransaction().commit();
					Messagebox.show("Datos grabados con exito");
					cargarActividades();
				}
			}
		};
		Messagebox.show("¿Desea anular la actividad?", "Confirmación", new Messagebox.Button[]{
				Messagebox.Button.YES, Messagebox.Button.NO }, Messagebox.QUESTION, clickListener);
	}
	@Command
	public void salir() {
		winGraficoAct.detach();
	}
	
	public List<Actividad> getListaActividades() {
		return listaActividades;
	}
	public void setListaActividades(List<Actividad> listaActividades) {
		this.listaActividades = listaActividades;
	}
	public Agenda getAgenda() {
		return agenda;
	}
	public void setAgenda(Agenda agenda) {
		this.agenda = agenda;
	}
}
