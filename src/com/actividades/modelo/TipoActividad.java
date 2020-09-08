package com.actividades.modelo;

import java.io.Serializable;
import javax.persistence.*;
import java.util.List;


/**
 * The persistent class for the tipo_actividad database table.
 * 
 */
@Entity
@Table(name="tipo_actividad")
@NamedQuery(name="TipoActividad.findAll", query="SELECT t FROM TipoActividad t")
public class TipoActividad implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name="id_tipo_actividad")
	private Integer idTipoActividad;

	private String descripcion;

	private String estado;

	//bi-directional many-to-one association to Actividad
	@OneToMany(mappedBy="tipoActividad")
	private List<Actividad> actividads;

	public TipoActividad() {
	}

	public Integer getIdTipoActividad() {
		return this.idTipoActividad;
	}

	public void setIdTipoActividad(Integer idTipoActividad) {
		this.idTipoActividad = idTipoActividad;
	}

	public String getDescripcion() {
		return this.descripcion;
	}

	public void setDescripcion(String descripcion) {
		this.descripcion = descripcion;
	}

	public String getEstado() {
		return this.estado;
	}

	public void setEstado(String estado) {
		this.estado = estado;
	}

	public List<Actividad> getActividads() {
		return this.actividads;
	}

	public void setActividads(List<Actividad> actividads) {
		this.actividads = actividads;
	}

	public Actividad addActividad(Actividad actividad) {
		getActividads().add(actividad);
		actividad.setTipoActividad(this);

		return actividad;
	}

	public Actividad removeActividad(Actividad actividad) {
		getActividads().remove(actividad);
		actividad.setTipoActividad(null);

		return actividad;
	}

}