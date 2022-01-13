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
@Table(name="tipo_queja")
@NamedQueries({
	@NamedQuery(name="TipoQueja.findAll", query="SELECT t FROM TipoQueja t ")
})
public class TipoQueja implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name="id_tipo_queja")
	private Integer idTipoQueja;
	
	@Column(name="tipo_queja")
	private String tipoQueja;
	
	private String estado;

	public TipoQueja() {
		super();
	}

	public TipoQueja(Integer idTipoQueja, String tipoQueja, String estado) {
		super();
		this.idTipoQueja = idTipoQueja;
		this.tipoQueja = tipoQueja;
		this.estado = estado;
	}

	public Integer getIdTipoQueja() {
		return idTipoQueja;
	}

	public void setIdTipoQueja(Integer idTipoQueja) {
		this.idTipoQueja = idTipoQueja;
	}

	public String getTipoQueja() {
		return tipoQueja;
	}

	public void setTipoQueja(String tipoQueja) {
		this.tipoQueja = tipoQueja;
	}

	public String getEstado() {
		return estado;
	}

	public void setEstado(String estado) {
		this.estado = estado;
	}
	
	

}
