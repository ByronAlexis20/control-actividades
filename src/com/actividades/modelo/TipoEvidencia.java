package com.actividades.modelo;

import java.io.Serializable;
import javax.persistence.*;
import java.util.List;


/**
 * The persistent class for the tipo_evidencia database table.
 * 
 */
@Entity
@Table(name="tipo_evidencia")
@NamedQueries({
	@NamedQuery(name="TipoEvidencia.buscarPorPatron", query="SELECT t FROM TipoEvidencia t where "
			+ "lower(t.descripcion) like lower(:patron) and t.estado = 'A' order by t.idTipoEvidencia asc")
})
public class TipoEvidencia implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name="id_tipo_evidencia")
	private Integer idTipoEvidencia;

	private String descripcion;
	
	private String formato;

	private String estado;

	//bi-directional many-to-one association to Evidencia
	@OneToMany(mappedBy="tipoEvidencia")
	private List<Evidencia> evidencias;

	public TipoEvidencia() {
	}

	public Integer getIdTipoEvidencia() {
		return this.idTipoEvidencia;
	}

	public void setIdTipoEvidencia(Integer idTipoEvidencia) {
		this.idTipoEvidencia = idTipoEvidencia;
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

	public List<Evidencia> getEvidencias() {
		return this.evidencias;
	}

	public void setEvidencias(List<Evidencia> evidencias) {
		this.evidencias = evidencias;
	}
	public String getFormato() {
		return formato;
	}

	public void setFormato(String formato) {
		this.formato = formato;
	}

	public Evidencia addEvidencia(Evidencia evidencia) {
		getEvidencias().add(evidencia);
		evidencia.setTipoEvidencia(this);

		return evidencia;
	}

	public Evidencia removeEvidencia(Evidencia evidencia) {
		getEvidencias().remove(evidencia);
		evidencia.setTipoEvidencia(null);

		return evidencia;
	}

	@Override
	public String toString() {
		return this.descripcion + " - " + this.formato;
	}

}