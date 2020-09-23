package com.actividades.util;

import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Calendar;

import javax.imageio.ImageIO;

import org.zkoss.image.AImage;
import org.zkoss.io.Files;
import org.zkoss.util.media.Media;
import org.zkoss.zul.Filedownload;

public class FileUtil {
	// Ruta donde esta el respositorio de archivos.
	public static String PATH_ARCHIVO_DIGITAL = "/tmp/";

	/**
	 * Almacena un archivo en el repositorio y retorna el path de archivo cargado. 
	 * @param media
	 * @return
	 */
	public static String cargaArchivo(Media media) {

		String pathArchivo;
		String pathRepositorio;
		String nombreArchivo;
		String pathArchivoCargado = null; 

		try {

			// Obtiene la fecha del dia para definir el directorio donde se depositara el archivo.
			Calendar hoy = Calendar.getInstance();
			int anio = hoy.get(Calendar.YEAR);
			int mes = hoy.get(Calendar.MONTH);
			int dia = hoy.get(Calendar.DAY_OF_MONTH);

			pathArchivo = anio + "/" + mes + "/" + dia + "/";

			// Obtiene la ruta del archivo digital.
			pathRepositorio =  PATH_ARCHIVO_DIGITAL;

			// Define el path definitivo donde se depositara el archivo
			// Si no existe, lo crea.

			File baseDir = new File(pathRepositorio + pathArchivo);
			if (!baseDir.exists()) {
				baseDir.mkdirs();
			}

			// Obtiene el nombre del archivo.
			nombreArchivo = media.getName();

			// Construye el path completo del archivo.
			pathArchivoCargado = pathRepositorio + pathArchivo + nombreArchivo;

			// Copia el archivo en la ruta definitiva.
			Files.copy(new File(pathArchivoCargado), media.getStreamData());

		} catch (Exception e) {
			e.printStackTrace();
		}

		return pathArchivoCargado;
	}
	/**
	 * Proceso para descarga de un archivo dado.
	 * @param nombreArchivo
	 */
	public static void descargaArchivo(String nombreArchivo) {
		try {
			// Abre el archivo y lo pasa a la clase Filedowload para la descarga
			// Como el formato es "null" asume el formato de acuerdo a la extension.
			Filedownload.save(new File(nombreArchivo), null);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} 
	}

	/**
	 * Retorna una imagen con tamano ajustado.
	 * @param imagen: Imagen a transoformar
	 * @param ancho: Ancho a obtener, -1 si se debe ajustar automaticamente.
	 * @param alto: Alto a obtener, -1 si se debe ajustar automaticamente.
	 * @return
	 */
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
