package com.actividades.modelo;
import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

@Entity
@Table(name="queja")
@NamedQueries({
	@NamedQuery(name="Queja.buscarPorResponsable", query="SELECT q FROM Queja q where q.empleado.idEmpleado = :id and q.estado = 'A' "
			+ "and lower(q.problema) like lower(:patron)"),
	@NamedQuery(name="Queja.buscarPorEstado", query="SELECT q FROM Queja q where q.estado = 'A' "
			+ "and q.estadoQueja = :estado")
})
public class Queja implements Serializable{
	private static final long serialVersionUID = 1L;
	
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name="id_queja")
	private Integer idQueja;
	
	private String problema;
	
	private String descripcion;
	
	@Temporal(TemporalType.DATE)
	@Column(name="fecha_envio")
	private Date fechaEnvio;
	
	@Temporal(TemporalType.DATE)
	@Column(name="fecha_aceptacion")
	private Date fechaAceptacion;
	
	private String adjunto;
	
	@Column(name="estado_queja")
	private String estadoQueja;
	
	private String estado;
	
	@ManyToOne
	@JoinColumn(name="id_empleado")
	private Empleado empleado;

	public Integer getIdQueja() {
		return idQueja;
	}

	public void setIdQueja(Integer idQueja) {
		this.idQueja = idQueja;
	}

	public String getProblema() {
		return problema;
	}

	public void setProblema(String problema) {
		this.problema = problema;
	}

	public String getDescripcion() {
		return descripcion;
	}

	public void setDescripcion(String descripcion) {
		this.descripcion = descripcion;
	}

	public Date getFechaEnvio() {
		return fechaEnvio;
	}

	public void setFechaEnvio(Date fechaEnvio) {
		this.fechaEnvio = fechaEnvio;
	}

	public Date getFechaAceptacion() {
		return fechaAceptacion;
	}

	public void setFechaAceptacion(Date fechaAceptacion) {
		this.fechaAceptacion = fechaAceptacion;
	}

	public String getAdjunto() {
		return adjunto;
	}

	public void setAdjunto(String adjunto) {
		this.adjunto = adjunto;
	}

	public String getEstadoQueja() {
		return estadoQueja;
	}

	public void setEstadoQueja(String estadoQueja) {
		this.estadoQueja = estadoQueja;
	}

	public String getEstado() {
		return estado;
	}

	public void setEstado(String estado) {
		this.estado = estado;
	}

	public Empleado getEmpleado() {
		return empleado;
	}

	public void setEmpleado(Empleado empleado) {
		this.empleado = empleado;
	}
	
	
}
