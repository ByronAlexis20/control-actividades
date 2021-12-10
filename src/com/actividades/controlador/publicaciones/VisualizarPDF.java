package com.actividades.controlador.publicaciones;

import java.io.File;
import java.io.FileInputStream;

import org.zkoss.util.media.AMedia;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.util.GenericForwardComposer;
import org.zkoss.zul.Iframe;
import org.zkoss.zul.Messagebox;

import com.actividades.modelo.Informacion;

@SuppressWarnings({ "serial", "rawtypes" })
public class VisualizarPDF extends GenericForwardComposer{

	Iframe report;
	Informacion informacion;
	
	@SuppressWarnings("unchecked")
	public void doAfterCompose(Component comp) throws Exception {
		super.doAfterCompose(comp);
		//src="http://wwwimages.adobe.com/www.adobe.com/content/dam/Adobe/en/devnet/pdf/pdfs/pdf_reference_archives/PDFReference16.pdf"
		
		informacion = (Informacion) Executions.getCurrent().getArg().get("Informacion");
	    try {
	    	File file = new File(informacion.getRutaArchivo());
	    	
	    	FileInputStream fl = new FileInputStream(file);
	    	byte[] arr = new byte[(int)file.length()];
	    	fl.read(arr);
	    	fl.close();
	    
	    	final AMedia amedia = new AMedia(informacion.getNombreArchivo(), "pdf","application/pdf", arr);
	    	report.setContent(amedia);
		} catch(Exception ex) {
	          	
		}
	}

	public void onClick$btn(Event e) throws InterruptedException{
		Messagebox.show("Hi btn");
	}
	
}
