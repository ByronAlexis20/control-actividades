package com.actividades.correo;

import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.mail.MessagingException;
import javax.swing.JOptionPane;

import com.actividades.util.Constantes;

public class EnviarCorreo {
	private boolean continuar = true;
	private String adjunto;
	private String[] adjuntos;
	private String[] destinatarios;
	private int servidor;
	private String asunto;
	private String mensaje;
	
	
	
	
	public EnviarCorreo(String adjunto, String[] adjuntos, String[] destinatarios, int servidor, String asunto,
			String mensaje) {
		super();
		this.adjunto = adjunto;
		this.adjuntos = adjuntos;
		this.destinatarios = destinatarios;
		this.servidor = servidor;
		this.asunto = asunto;
		this.mensaje = mensaje;
	}
	public static boolean ComprobarConexionInternet(){
		String dirWeb = "www.google.com";
		int puerto = 80;
		try{
			@SuppressWarnings("resource")
			Socket s = new Socket(dirWeb, puerto);
			if (s.isConnected()) {
				System.out.println("Conexión establecida con la dirección: " + dirWeb + " a través del puerto: " + puerto);
			}
		}
		catch (Exception e){
			System.err.println("No se pudo establecer conexión con: " + dirWeb + " a través del puerto: " + puerto);
			return false;
		}
		return true;
	}
	public void detenElHilo(){
		this.continuar = false;
	}
	int i = 0;
	public void enviarCorreo() {
		while (this.continuar) {
			i = i + 1;
			//adjunto es la direccion del archivo adjunto
			if (this.adjunto.isEmpty()) {
				EnviarMail propio = new EnviarMail(Constantes.CORREO_ORIGEN, Constantes.CONTRASENIA_ORIGEN, 
						this.destinatarios, this.asunto, this.mensaje, this.servidor);
				try {
					propio.enviar();
				}
				catch (MessagingException ex){
					JOptionPane.showMessageDialog(null, "Error" + ex.getMessage());
					Logger.getLogger(EnviarCorreo.class.getName()).log(Level.SEVERE, null, ex);
					detenElHilo();
				}
			}
			else {
				EnviarMailComplejo propio = new EnviarMailComplejo(Constantes.CORREO_ORIGEN, 
						Constantes.CONTRASENIA_ORIGEN, this.destinatarios, this.asunto, this.mensaje, this.adjuntos, this.servidor);
				try {
					propio.Enviar();
				}
				catch (MessagingException ex){
					JOptionPane.showMessageDialog(null, "Error" + ex.getMessage());
					Logger.getLogger(EnviarCorreo.class.getName()).log(Level.SEVERE, null, ex);
					detenElHilo();
				}
			}
			detenElHilo();
			System.out.println(i);
		}
	}
}
