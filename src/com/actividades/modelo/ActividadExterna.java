package com.actividades.modelo;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

@Entity
@Table(name="actividad_externa")
@NamedQueries({
	@NamedQuery(name="ActividadExterna.buscarActivas", query="SELECT a FROM ActividadExterna a where a.estado = 'A' and a.estadoActividad = 'NO ASIGNADO'")
})
public class ActividadExterna implements Serializable {
	private static final long serialVersionUID = 1L;
	
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name="id_actividad_externa")
	private Integer idActividadExterna;
	
	private String descripcion;
	
	@Temporal(TemporalType.DATE)
	private Date fecha;
	
	@Column(name="estado_actividad")
	private String estadoActividad;
	
	private String estado;

	public Integer getIdActividadExterna() {
		return idActividadExterna;
	}

	public void setIdActividadExterna(Integer idActividadExterna) {
		this.idActividadExterna = idActividadExterna;
	}

	public String getDescripcion() {
		return descripcion;
	}

	public void setDescripcion(String descripcion) {
		this.descripcion = descripcion;
	}

	public Date getFecha() {
		return fecha;
	}

	public void setFecha(Date fecha) {
		this.fecha = fecha;
	}

	public String getEstadoActividad() {
		return estadoActividad;
	}

	public void setEstadoActividad(String estadoActividad) {
		this.estadoActividad = estadoActividad;
	}

	public String getEstado() {
		return estado;
	}

	public void setEstado(String estado) {
		this.estado = estado;
	}
	
}
