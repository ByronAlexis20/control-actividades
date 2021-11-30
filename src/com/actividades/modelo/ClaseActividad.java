package com.actividades.modelo;

import java.io.Serializable;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Table;

@Entity
@Table(name="clase_actividad")
@NamedQueries({
	@NamedQuery(name="ClaseActividad.findAll", query="SELECT a FROM ClaseActividad a ")
})
public class ClaseActividad implements Serializable {
	private static final long serialVersionUID = 1L;
	
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name="id_clase_actividad")
	private Integer idClaseActividad;
	
	@Column(name="clase_actividad")
	private String claseActividad;
	
	private String estado;
	
	//bi-directional many-to-one association to Evidencia
	@OneToMany(mappedBy="claseActividad")
	private List<Actividad> actividades;

	@OneToMany(mappedBy="claseActividad")
	private List<ActividadExterna> actividadesExternas;
	
	public Integer getIdClaseActividad() {
		return idClaseActividad;
	}

	public void setIdClaseActividad(Integer idClaseActividad) {
		this.idClaseActividad = idClaseActividad;
	}

	public String getClaseActividad() {
		return claseActividad;
	}

	public void setClaseActividad(String claseActividad) {
		this.claseActividad = claseActividad;
	}

	public String getEstado() {
		return estado;
	}

	public void setEstado(String estado) {
		this.estado = estado;
	}

	public List<Actividad> getActividades() {
		return actividades;
	}

	public void setActividades(List<Actividad> actividades) {
		this.actividades = actividades;
	}
	
	public Actividad addActividades(Actividad actividad) {
		getActividades().add(actividad);
		actividad.setClaseActividad(this);

		return actividad;
	}

	public Actividad removeActividad(Actividad actividad) {
		getActividades().remove(actividad);
		actividad.setClaseActividad(null);

		return actividad;
	}

	public List<ActividadExterna> getActividadesExternas() {
		return actividadesExternas;
	}

	public void setActividadesExternas(List<ActividadExterna> actividadesExternas) {
		this.actividadesExternas = actividadesExternas;
	}

	public ActividadExterna addActividadesExternas(ActividadExterna actividadExterna) {
		getActividadesExternas().add(actividadExterna);
		actividadExterna.setClaseActividad(this);

		return actividadExterna;
	}

	public ActividadExterna removeActividadesExternas(ActividadExterna actividadExterna) {
		getActividadesExternas().remove(actividadExterna);
		actividadExterna.setClaseActividad(null);

		return actividadExterna;
	}
}