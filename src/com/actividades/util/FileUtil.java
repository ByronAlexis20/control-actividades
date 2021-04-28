package com.actividades.util;

import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import javax.imageio.ImageIO;

import org.zkoss.image.AImage;
import org.zkoss.io.Files;
import org.zkoss.util.media.Media;
import org.zkoss.zul.Filedownload;

public class FileUtil {
	
	public static String cargaArchivo(Media media) {
		String pathRepositorio = Constantes.PATH_ARCHIVOS_SISTEMA + Constantes.PATH_IMAGENES;  
		try {
			File baseDir = new File(pathRepositorio);
			if (!baseDir.exists()) {
				baseDir.mkdirs();
			}
			pathRepositorio = pathRepositorio + media.getName();
			Files.copy(new File(pathRepositorio), media.getStreamData());
		} catch (Exception e) {
			e.printStackTrace();
		}
		return pathRepositorio;
	}
	public static void descargaArchivo(String nombreArchivo) {
		try {
			Filedownload.save(new File(nombreArchivo), null);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} 
	}
	public static AImage getImagenTamanoFijo(AImage imagen, int ancho, int alto) {
		AImage retorno = null; 
		BufferedImage imagenOriginal;
		Image imagenEscalada;
		BufferedImage nuevaImagen;
		ByteArrayOutputStream salidaImagen = new ByteArrayOutputStream();
		try {
			imagenOriginal = ImageIO.read(imagen.getStreamData()); 
			imagenEscalada = imagenOriginal.getScaledInstance(ancho, alto, Image.SCALE_SMOOTH);

			nuevaImagen = new BufferedImage(imagenEscalada.getWidth(null), imagenEscalada.getHeight(null), imagenOriginal.getType());
			Graphics2D g = nuevaImagen.createGraphics();
			g.drawImage(imagenEscalada, 0, 0, null);
			g.dispose();	

			ImageIO.write(nuevaImagen, imagen.getFormat(),  salidaImagen);

			retorno = new AImage("", salidaImagen.toByteArray());

		} catch (IOException e) {
			e.printStackTrace();
		}
		return retorno;
	}
}
