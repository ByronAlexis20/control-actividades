package com.actividades.util;

import java.awt.Dialog.ModalExclusionType;
import java.io.File;
import java.io.FileOutputStream;
import java.sql.Connection;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.zkoss.zk.ui.Executions;
import org.zkoss.zul.Filedownload;

import com.actividades.modelo.ClaseDAO;

import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.JasperRunManager;
import net.sf.jasperreports.engine.util.JRLoader;
import net.sf.jasperreports.view.JasperViewer;

public class PrintReport {
	public static final String FORMATO_PDF = "PDF";
	public static final String FORMATO_XLS = "XLS";

	public void crearReporte(String path, ClaseDAO claseDAO,Map<String, Object> param) {
		try {
			String pathAbsoluto = Executions.getCurrent()
					.getDesktop().getWebApp()
					.getRealPath("/");
			String nombreReporte = pathAbsoluto + path;
			Connection cn = claseDAO.abreConexion();

			String nombreArchivo = null;
			nombreArchivo = pathAbsoluto + "temp";
			//Clients.showNotification("nombre de ruta: " + nombreArchivo);

			System.out.println(nombreArchivo);
			//pregunta si la carpeta existe
			File folder = new File(nombreArchivo);
			if (folder.exists()) {
			}else {
				folder.mkdir();
			}

			// Obtiene un nombre aleatorio para el reporte
			nombreArchivo = nombreArchivo + "/" + UUID.randomUUID().toString() + ".pdf";
			//Messagebox.show("nombre de ruta y archivo: " + nombreArchivo);



			System.out.println(nombreArchivo);
			byte[] b = null;
			b = JasperRunManager.runReportToPdf(nombreReporte, param, cn);
			FileOutputStream fos = new FileOutputStream(nombreArchivo);
			fos.write(b);
			fos.close();
			Filedownload.save(new File(nombreArchivo), "pdf"); 
			/*
			nombreArchivo = nombreArchivo + "/" + UUID.randomUUID().toString() + ".pdf";
			Messagebox.show("nombre de ruta y archivo: " + nombreArchivo);
			 */
		}catch(Exception ex) {
			ex.printStackTrace();
		}
	}

	public void exportToPDF(String destino) {
		try {
			//JasperExportManager.exportReportToPdfFile(destino);
		}catch(Exception ex) {
			ex.printStackTrace();
		}
	}



	public boolean validarDosReporte(String path, ClaseDAO claseDAO,Map<String, Object> param) {
		try {
			String pathAbsoluto = Executions.getCurrent()
					.getDesktop().getWebApp()
					.getRealPath("/");
			String nombreReporte = pathAbsoluto + path;
			Connection cn = claseDAO.abreConexion();

			String nombreArchivo = null;
			nombreArchivo = pathAbsoluto + "temp";
			//Clients.showNotification("nombre de ruta: " + nombreArchivo);

			System.out.println(nombreArchivo);
			//pregunta si la carpeta existe
			File folder = new File(nombreArchivo);
			if (folder.exists()) {
			}else {
				folder.mkdir();
			}

			// Obtiene un nombre aleatorio para el reporte
			nombreArchivo = nombreArchivo + "/" + UUID.randomUUID().toString() + ".pdf";
			//Messagebox.show("nombre de ruta y archivo: " + nombreArchivo);



			System.out.println(nombreArchivo);
			byte[] b = null;
			b = JasperRunManager.runReportToPdf(nombreReporte, param, cn);
			FileOutputStream fos = new FileOutputStream(nombreArchivo);
			fos.write(b);
			fos.close();
			Filedownload.save(new File(nombreArchivo), "pdf"); 
			return true;
			/*
			nombreArchivo = nombreArchivo + "/" + UUID.randomUUID().toString() + ".pdf";
			Messagebox.show("nombre de ruta y archivo: " + nombreArchivo);
			 */
		}catch(Exception ex) {
			ex.printStackTrace();
			return false;

		}
	}




	/*private JasperReport report;
	private JasperPrint reportFilled;
	private JasperViewer viewer;
	public void crearReporteVista(String path, ClaseDAO claseDAO,Map<String, Object> param) {
		try {
			String applicationPath = Executions.getCurrent()
		            .getDesktop().getWebApp()
		            .getRealPath("/");
			applicationPath = applicationPath + path;
			System.out.println("ruta archivo: " + applicationPath);
			//helper.mostrarAlertaAdvertencia(applicationPath, Context.getInstance().getStage());
			Connection cn = claseDAO.abreConexion();
			report = (JasperReport) JRLoader.loadObjectFromFile(applicationPath);
			reportFilled = JasperFillManager.fillReport(report, param, cn);
		}catch(Exception ex) {
			ex.printStackTrace();
		}
	}*/
	public void showReportVista(String titulo) {
		try {
			viewer = new JasperViewer(reportFilled,false);
			viewer.setTitle(titulo);
			viewer.setVisible(true);		
		}catch(Exception ex) {
			ex.printStackTrace();
		}
	}

	private JasperReport report;
	private JasperPrint reportFilled;
	private JasperViewer viewer;
	public void crearReporteVista(String path, ClaseDAO claseDAO,Map<String, Object> param) {
		try {
			String applicationPath = Executions.getCurrent()
					.getDesktop().getWebApp()
					.getRealPath("/");
			applicationPath = applicationPath + path;
			System.out.println("ruta archivo: " + applicationPath);
			//String archivoReporte = applicationPath + path;

			//helper.mostrarAlertaAdvertencia(applicationPath, Context.getInstance().getStage());
			Connection cn = claseDAO.abreConexion();
			/*	JasperPrint jasperPrintWindow = JasperFillManager.fillReport(archivoReporte, param,cn);

			JasperViewer jasperViewer = new JasperViewer(jasperPrintWindow, false);
			jasperViewer.setDefaultCloseOperation(JasperViewer.DISPOSE_ON_CLOSE);
			jasperViewer.setTitle("Reporte");
			jasperViewer.setZoomRatio((float) 1.25);
			jasperViewer.setExtendedState(JasperViewer.MAXIMIZED_BOTH);
			jasperViewer.setModalExclusionType(ModalExclusionType.APPLICATION_EXCLUDE);
			jasperViewer.requestFocus();
			jasperViewer.setVisible(true);*/

			report = (JasperReport) JRLoader.loadObjectFromFile(applicationPath);
			reportFilled = JasperFillManager.fillReport(report, param, cn);
			showReportVista(path);

		}catch(Exception ex) {
			ex.printStackTrace();
		}
	}

	public  void showViewReport(String pathReporte, 
			Map<String, Object> parametros, ClaseDAO claseDAO) {
		Connection connection = claseDAO.abreConexion();
		try {
			// Obtiene el path de la aplicacion.
			String pathAbsoluto = Executions.getCurrent()
					.getDesktop().getWebApp()
					.getRealPath("/");

			// Arma el path del reporte basado en el path de la aplicacion.
			String archivoReporte = pathAbsoluto + pathReporte;

			if (parametros == null) {
				parametros = new HashMap<String, Object>();
			}

			//if (formato.equals(FORMATO_PDF)) {			
			// se muestra en una ventana aparte para su descarga
			JasperPrint jasperPrintWindow = JasperFillManager.fillReport(archivoReporte, parametros,connection);

			JasperViewer jasperViewer = new JasperViewer(jasperPrintWindow, false);
			jasperViewer.setDefaultCloseOperation(JasperViewer.DISPOSE_ON_CLOSE);
			jasperViewer.setTitle("Reporte");
			jasperViewer.setZoomRatio((float) 1.25);
			jasperViewer.setExtendedState(JasperViewer.MAXIMIZED_BOTH);
			jasperViewer.setModalExclusionType(ModalExclusionType.APPLICATION_EXCLUDE);
			jasperViewer.requestFocus();
			jasperViewer.setVisible(true); 

			//}	

		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

}
