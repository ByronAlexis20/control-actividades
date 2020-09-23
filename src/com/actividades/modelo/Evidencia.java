package com.actividades.modelo;

import java.io.Serializable;
import javax.persistence.*;


/**
 * The persistent class for the evidencia database table.
 * 
 */
@Entity
@NamedQuery(name="Evidencia.buscarPorActividad", query="SELECT e FROM Evidencia e where e.actividad.idActividad = :idActividad and e.estado = 'A' order by e.idEvidencia asc")
public class Evidencia implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name="id_evidencia")
	private Integer idEvidencia;

	private String descripcion;

	private String estado;

	@Column(name="ruta_archivo")
	private String rutaArchivo;

	//bi-directional many-to-one association to Actividad
	@ManyToOne
	@JoinColumn(name="id_actividad")
	private Actividad actividad;

	//bi-directional many-to-one association to TipoEvidencia
	@ManyToOne
	@JoinColumn(name="id_tipo_evidencia")
	private TipoEvidencia tipoEvidencia;

	public Evidencia() {
	}

	public Integer getIdEvidencia() {
		return this.idEvidencia;
	}

	public void setIdEvidencia(Integer idEvidencia) {
		this.idEvidencia = idEvidencia;
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

	public String getRutaArchivo() {
		return this.rutaArchivo;
	}

	public void setRutaArchivo(String rutaArchivo) {
		this.rutaArchivo = rutaArchivo;
	}

	public Actividad getActividad() {
		return this.actividad;
	}

	public void setActividad(Actividad actividad) {
		this.actividad = actividad;
	}

	public TipoEvidencia getTipoEvidencia() {
		return this.tipoEvidencia;
	}

	public void setTipoEvidencia(TipoEvidencia tipoEvidencia) {
		this.tipoEvidencia = tipoEvidencia;
	}

	@Override
	public String toString() {
		return "Evidencia [idEvidencia=" + idEvidencia + ", descripcion=" + descripcion + ", estado=" + estado
				+ ", rutaArchivo=" + rutaArchivo + ", actividad=" + actividad + ", tipoEvidencia=" + tipoEvidencia
				+ "]";
	}
}