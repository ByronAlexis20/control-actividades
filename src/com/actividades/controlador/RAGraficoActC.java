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
import org.zkoss.zk.ui.select.Selectors;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zk.ui.util.Clients;
import org.zkoss.zul.Image;
import org.zkoss.zul.ListModelList;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

import com.actividades.modelo.Actividad;
import com.actividades.modelo.ActividadDAO;
import com.actividades.modelo.Agenda;
import com.actividades.util.Constantes;

public class RAGraficoActC {
	@Wire Window winGraficoAct;
	@Wire Textbox txtAgenda;
	@Wire Textbox txtNoActividades;
	@Wire Textbox txtNoPublicadas;
	@Wire Textbox txtFechaInicio;
	@Wire Textbox txtFechaFin;
	@Wire Textbox txtRechazadas;
	@Wire Image imGrafico;
	
	@Wire private Listbox lstActividadesPublicadas;
	@Wire private Listbox lstActividadesPendientes;
	@Wire private Listbox lstActividadesRechazadas;
	
	List<Actividad> listaActividadesPendientes;
	List<Actividad> listaActividadesRechazadas;
	List<Actividad> listaActividadesPublicadas;
	
	Agenda agenda;
	ActividadDAO actividadDAO = new ActividadDAO();
	
	@AfterCompose
	public void aferCompose(@ContextParam(ContextType.VIEW) Component view) throws IOException{
		Selectors.wireComponents(view, this, false);
		agenda = (Agenda) Executions.getCurrent().getArg().get("Agenda");
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
		if(listaActividadesPendientes != null)
			listaActividadesPendientes = null;
		
		if(listaActividadesRechazadas != null)
			listaActividadesRechazadas = null;
		
		if(listaActividadesPublicadas != null)
			listaActividadesPublicadas = null;
		
		int totalActividades = 0;
		int cantidadPublicadas = 0;
		int cantidadRechazadas = 0;
		int cantidadPendientes = 0;
		
		List<Actividad> listaPendientes = new ArrayList<>();
		List<Actividad> listaRechazadas = new ArrayList<>();
		List<Actividad> listaPublicadas = new ArrayList<>();
		
		List<Actividad> todas = actividadDAO.obtenerActividad(agenda.getIdAgenda(),Constantes.ID_TIPO_PRIMORDIALES);
		totalActividades = todas.size();
		
		for(Actividad act : todas) {
			if(act.getEstadoPublicado().equals(Constantes.ESTADO_PUBLICADO)) {
				if(act.getEstadoActividad().equals(Constantes.ESTADO_RECHAZADO)) {
					cantidadRechazadas ++;
					listaRechazadas.add(act);
				}
				else {
					cantidadPublicadas ++;
					listaPublicadas.add(act);
				}
			}else if(act.getEstadoActividad().equals(Constantes.ESTADO_PENDIENTE)) {
				cantidadPendientes ++;
				listaPendientes.add(act);
			}
		}
		
		txtNoActividades.setText(String.valueOf(totalActividades) + " Actividades Programadas");
		txtNoPublicadas.setText(String.valueOf(cantidadPublicadas) + " Actividades Publicadas");
		txtRechazadas.setText(String.valueOf(cantidadRechazadas) + " Actividades Rechazadas");
		
		try {
			realizarGrafica(totalActividades,cantidadPublicadas,cantidadRechazadas,cantidadPendientes);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		listaActividadesPendientes = listaPendientes; 
		System.out.println("Pendientes " + listaActividadesPendientes.size());
		listaActividadesRechazadas = listaRechazadas;
		System.out.println("Rechazadas " + listaActividadesRechazadas.size());
		listaActividadesPublicadas = listaPublicadas;
		System.out.println("Publicadas " + listaActividadesPublicadas.size());
		lstActividadesPublicadas.setModel(new ListModelList(listaActividadesPublicadas));
		lstActividadesPendientes.setModel(new ListModelList(listaActividadesPendientes));
		lstActividadesRechazadas.setModel(new ListModelList(listaActividadesRechazadas));
	}
	
	private void realizarGrafica(int totalActividades,int cantidadPublicadas,int cantidadRechazadas,int cantidadPendientes) throws IOException {
		double tPendiente  = 100.0;
		double tRechazadas = 0.0;
		double tPublicadas = 0.0;
		if(totalActividades > 0) {
			tPendiente = (cantidadPendientes * 100) / totalActividades;
			tRechazadas = (cantidadRechazadas * 100) / totalActividades;
			tPublicadas = (cantidadPublicadas * 100) / totalActividades;
		}
		
		DefaultPieDataset pieDataset = new DefaultPieDataset();
		pieDataset.setValue("PUBLICADAS [" + cantidadPublicadas + "]", tPublicadas);
		pieDataset.setValue("PENDIENTES [" + cantidadPendientes + "]", tPendiente);
		pieDataset.setValue("RECHAZADAS [" + cantidadRechazadas + "]", tRechazadas);
		pieDataset.setNotify(true);
		
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
	public void salir() {
		winGraficoAct.detach();
	}
	public List<Actividad> getListaActividadesPendientes() {
		return listaActividadesPendientes;
	}
	public void setListaActividadesPendientes(List<Actividad> listaActividadesPendientes) {
		this.listaActividadesPendientes = listaActividadesPendientes;
	}
	public List<Actividad> getListaActividadesRechazadas() {
		return listaActividadesRechazadas;
	}
	public void setListaActividadesRechazadas(List<Actividad> listaActividadesRechazadas) {
		this.listaActividadesRechazadas = listaActividadesRechazadas;
	}
	public List<Actividad> getListaActividadesPublicadas() {
		return listaActividadesPublicadas;
	}
	public void setListaActividadesPublicadas(List<Actividad> listaActividadesPublicadas) {
		this.listaActividadesPublicadas = listaActividadesPublicadas;
	}
	public Agenda getAgenda() {
		return agenda;
	}
	public void setAgenda(Agenda agenda) {
		this.agenda = agenda;
	}
}
