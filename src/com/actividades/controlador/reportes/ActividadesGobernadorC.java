package com.actividades.controlador.reportes;

import java.io.IOException;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
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
import org.zkoss.zk.ui.select.Selectors;
import org.zkoss.zk.ui.util.Clients;

import com.actividades.modelo.Actividad;
import com.actividades.modelo.ActividadDAO;
import com.actividades.util.Constantes;
import com.actividades.util.PrintReport;

public class ActividadesGobernadorC {
	public String textoBuscar;
	
	private List<Actividad> listaActividades;
	private ActividadDAO actividadDAO = new ActividadDAO();
	
	@AfterCompose
	public void aferCompose(@ContextParam(ContextType.VIEW) Component view) throws IOException{
		Selectors.wireComponents(view, this, false);
		textoBuscar="";
	}
	
	@GlobalCommand("Actividad.buscarPorTipoActividad")
	@Command
	@NotifyChange({"listaActividades"})
	public void buscar(){
		if (listaActividades != null) {
			listaActividades = null; 
		}
		listaActividades = new ArrayList<>();
		List<Actividad> lista = actividadDAO.buscarPorTipoActividad(Constantes.ID_TIPO_PRIMORDIALES);
		for(Actividad ac : lista) {
			ZoneId timeZone = ZoneId.systemDefault();
	        LocalDate getLocalDate = ac.getFecha().toInstant().atZone(timeZone).toLocalDate();
	        Integer anio = Integer.parseInt(textoBuscar);
	        if(anio.equals(getLocalDate.getYear())) {
	        	listaActividades.add(ac);
	        }
		}
		
		if(listaActividades.size() == 0) {
			Clients.showNotification("No hay datos para mostrar.!!");
		}
		System.out.println("entra a buscar empleado");
	}

	@Command
	public void imprimir(){
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("ID_TIPO_ACTIVIDAD", Constantes.ID_TIPO_PRIMORDIALES);
		params.put("FECHA_BUSQUEDA", "Año de Búsqueda: " + textoBuscar);
		params.put("ANIO", Integer.parseInt(textoBuscar));
		PrintReport report = new PrintReport();
		report.crearReporte("/reportes/actividadesGenerales.jasper",actividadDAO, params);
	}
	
	public String getTextoBuscar() {
		return textoBuscar;
	}
	public void setTextoBuscar(String textoBuscar) {
		this.textoBuscar = textoBuscar;
	}
	public List<Actividad> getListaActividades() {
		return listaActividades;
	}
	public void setListaActividades(List<Actividad> listaActividades) {
		this.listaActividades = listaActividades;
	}
}