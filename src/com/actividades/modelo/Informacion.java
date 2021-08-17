package com.actividades.modelo;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;

@Entity
@Table(name = "informacion")
@NamedQueries({
		@NamedQuery(name = "Informacion.buscarTodos", query = "SELECT i FROM Informacion i where i.estado = 'A'") })
public class Informacion implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id_informacion")
	private Integer idInformacion;

	@Column(name = "nombre_archivo")
	private String nombreArchivo;

	@Column(name = "ruta_archivo")
	private String rutaArchivo;

	private String estado;

	public Informacion() {
		super();
	}

	public Informacion(Integer idInformacion, String nombreArchivo, String rutaArchivo, String estado) {
		super();
		this.idInformacion = idInformacion;
		this.nombreArchivo = nombreArchivo;
		this.rutaArchivo = rutaArchivo;
		this.estado = estado;
	}

	public Integer getIdInformacion() {
		return idInformacion;
	}

	public void setIdInformacion(Integer idInformacion) {
		this.idInformacion = idInformacion;
	}

	public String getNombreArchivo() {
		return nombreArchivo;
	}

	public void setNombreArchivo(String nombreArchivo) {
		this.nombreArchivo = nombreArchivo;
	}

	public String getRutaArchivo() {
		return rutaArchivo;
	}

	public void setRutaArchivo(String rutaArchivo) {
		this.rutaArchivo = rutaArchivo;
	}

	public String getEstado() {
		return estado;
	}

	public void setEstado(String estado) {
		this.estado = estado;
	}

}
