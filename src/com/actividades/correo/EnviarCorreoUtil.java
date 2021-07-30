package com.actividades.correo;

import java.util.Properties;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

public class EnviarCorreoUtil {
	public void SendMail() {
        Properties props = new Properties();
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.host", "smtp.gmail.com");
        props.put("mail.smtp.port", "587");

        Session session = Session.getInstance(props,
                new javax.mail.Authenticator() {
                    protected PasswordAuthentication getPasswordAuthentication() {
                        return new PasswordAuthentication("byronalexisramirez20@gmail.com", "tricampeon2015");
                    }
                });

        try {

            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress("byronalexisramirez20@gmail.com"));
            message.setRecipients(Message.RecipientType.TO,
                    InternetAddress.parse("byronalexisramirez20@gmail.com"));
            message.setSubject("asunto");
            message.setText("mensaje");

            Transport.send(message);
            
            System.out.println("Mensaje enviado");
        } catch (MessagingException e) {
        	System.out.println(e.getMessage());
            throw new RuntimeException(e);
        }
    }
}
