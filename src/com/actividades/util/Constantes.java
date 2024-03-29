package com.actividades.util;

import org.zkoss.zk.ui.Executions;

public class Constantes {
	public static String ESTADO_ACTIVO = "A";
	public static String ESTADO_INACTIVO = "I";
	public static String ESTADO_PUBLICADO = "PUBLICADO";
	public static String ESTADO_NO_PUBLICADO = "NO PUBLICADO";
	public static String ESTADO_RECHAZADO = "RECHAZADO";
	
	public static String ESTADO_PENDIENTE = "PENDIENTE";
	public static String ESTADO_EN_PROCESO = "EN PROCESO";
	public static String ESTADO_REALIZADO = "REALIZADO";
	public static String ESTADO_PENDIENTE_ASIGNACION = "PENDINTE DE ASIGNACION";
	public static String ESTADO_NO_ASIGNADO = "NO ASIGNADO";
	public static String ESTADO_ASIGNADO = "ASIGNADO";
	
	public static String QUEJA_PENDIENTE = "PENDIENTE";
	public static String QUEJA_REVISION = "REVISION";
	public static String QUEJA_PUBLICADA = "PUBLICADO";
	
	public static String QUEJA_ATENDIDA = "ATENDIDA";
	public static String QUEJA_NO_ATENDIDA = "NO ATENDIDA";
	
	public static Integer ID_JEFE_AREA = 2;
	public static Integer ID_ADMINISTRADOR_SISTEMAS = 1;
	public static Integer ID_ADMINISTRACION_COMUNICACION = 3;
	public static Integer ID_AUTORIDAD_MAXIMA = 5;
	public static Integer ID_ASISTENTE = 4;
	
	public static Integer ID_CARGO_JEFE = 1;
	
	public static String CODIGO_AGENDA = "AG-";
	public static String CODIGO_ACTIVIDAD = "ACT-";
	
	/*codigos de departamentos*/
	public static Integer ID_DEPARTAMENTO_SISTEMAS = 1;
	public static Integer ID_DEPARTAMENTO_COMUNICACIONES = 2;
	public static Integer ID_DEPARTAMENTO_GOBERNACION = 7;
	
	
	public static String USUARIO_PERMITIDO = "PERMITIDO";
	public static String USUARIO_NO_PERMITIDO = "NO PERMITIDO";
	
	public static String CORREO_ORIGEN = "departamentoCGOB@gmail.com";
	public static String CONTRASENIA_ORIGEN = "2022santaelena";
	
	
	//tipo de actividades
	public static Integer ID_TIPO_PRIMORDIALES = 1;
	public static Integer ID_TIPO_INTERNAS = 2;
	
	//tipo de agenda
	public static String TIPO_AGENDA_PRINCIPALES = "PRINCIPALES";
	public static String TIPO_AGENDA_INTERNAS = "INTERNAS";
	
	public static String PATH_ARCHIVOS_SISTEMA = Executions.getCurrent().getDesktop().getWebApp().getRealPath("/") + "temp\\";
	public static String PATH_IMAGENES = "img\\";
	public static String PATH_REPORTE = "rpt\\";
	
	//dias de la semana
	public static Integer NUMERO_DIA_DOMINGO = 1;
	public static Integer NUMERO_DIA_LUNES = 2;
	public static Integer NUMERO_DIA_MARTES = 3;
	public static Integer NUMERO_DIA_MIERCOLES = 4;
	public static Integer NUMERO_DIA_JUEVES = 5;
	public static Integer NUMERO_DIA_VIERNES = 6;
	public static Integer NUMERO_DIA_SABADO = 7;
	
	public static Integer CODIGO_CLASE_ACTIVIDAD_POLITICO = 1;
	public static Integer CODIGO_CLASE_ACTIVIDAD_CULTURAL = 2;
	public static Integer CODIGO_CLASE_ACTIVIDAD_DEPORTIVO = 3;
	public static Integer CODIGO_CLASE_ACTIVIDAD_INTERNO = 4;
	public static Integer CODIGO_CLASE_ACTIVIDAD_SALUD = 5;
	
	public static String CODIGO_TIPO_AGENDA_ENVIADA_GOBERNADOR = "GOBERNADOR";
}