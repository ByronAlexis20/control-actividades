package com.actividades.modelo;

import java.io.Serializable;
import javax.persistence.*;
import java.util.Date;
import java.util.List;


/**
 * The persistent class for the actividad database table.
 * 
 */
@Entity
@Table(name="actividad")
@NamedQueries({
	@NamedQuery(name="Actividad.buscarPorAgenda", query="SELECT a FROM Actividad a where a.estado = 'A' "
			+ "and a.agenda.idAgenda = :idAgenda and a.tipoActividad.idTipoActividad = :idTipoActividad order by a.idActividad asc"),
	@NamedQuery(name="Actividad.buscarCodigoPorAgenda", query="SELECT a FROM Actividad a "
			+ "where a.agenda.idAgenda = :idAgenda order by a.idActividad desc"),
	
	@NamedQuery(name="Actividad.buscarPorFecha", query="SELECT a FROM Actividad a where a.agenda.empleado.idEmpleado = :idEmpleado "
			+ " and (a.fecha between :fechaInicio and :fechaFin) and a.estado = 'A' and a.tipoActividad.idTipoActividad = :idTipoActividad"
			+ " order by a.idActividad desc")
})
public class Actividad implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name="id_actividad")
	private Integer idActividad;

	private String descripcion;

	private String estado;
	
	private Integer secuencia;
	
	private String codigo;

	@Column(name="estado_actividad")
	private String estadoActividad;

	@Column(name="estado_publicado")
	private String estadoPublicado;

	@Temporal(TemporalType.DATE)
	private Date fecha;

	//bi-directional many-to-one association to Agenda
	@ManyToOne
	@JoinColumn(name="id_agenda")
	private Agenda agenda;

	//bi-directional many-to-one association to TipoActividad
	@ManyToOne
	@JoinColumn(name="id_tipo_actividad")
	private TipoActividad tipoActividad;

	//bi-directional many-to-one association to Evidencia
	@OneToMany(mappedBy="actividad")
	private List<Evidencia> evidencias;

	public Actividad() {
	}

	public Integer getIdActividad() {
		return this.idActividad;
	}

	public void setIdActividad(Integer idActividad) {
		this.idActividad = idActividad;
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

	public String getEstadoActividad() {
		return this.estadoActividad;
	}

	public void setEstadoActividad(String estadoActividad) {
		this.estadoActividad = estadoActividad;
	}

	public String getEstadoPublicado() {
		return this.estadoPublicado;
	}

	public void setEstadoPublicado(String estadoPublicado) {
		this.estadoPublicado = estadoPublicado;
	}

	public Date getFecha() {
		return this.fecha;
	}

	public void setFecha(Date fecha) {
		this.fecha = fecha;
	}

	public Agenda getAgenda() {
		return this.agenda;
	}

	public void setAgenda(Agenda agenda) {
		this.agenda = agenda;
	}

	public TipoActividad getTipoActividad() {
		return this.tipoActividad;
	}

	public void setTipoActividad(TipoActividad tipoActividad) {
		this.tipoActividad = tipoActividad;
	}

	public String getCodigo() {
		return codigo;
	}

	public void setCodigo(String codigo) {
		this.codigo = codigo;
	}

	public Integer getSecuencia() {
		return secuencia;
	}

	public void setSecuencia(Integer secuencia) {
		this.secuencia = secuencia;
	}

	public List<Evidencia> getEvidencias() {
		return this.evidencias;
	}

	public void setEvidencias(List<Evidencia> evidencias) {
		this.evidencias = evidencias;
	}

	public Evidencia addEvidencia(Evidencia evidencia) {
		getEvidencias().add(evidencia);
		evidencia.setActividad(this);

		return evidencia;
	}

	public Evidencia removeEvidencia(Evidencia evidencia) {
		getEvidencias().remove(evidencia);
		evidencia.setActividad(null);

		return evidencia;
	}

	@Override
	public String toString() {
		return "Actividad [idActividad=" + idActividad + ", descripcion=" + descripcion + ", estado=" + estado
				+ ", estadoActividad=" + estadoActividad + ", estadoPublicado=" + estadoPublicado + ", fecha=" + fecha
				+ ", agenda=" + agenda + ", tipoActividad=" + tipoActividad + ", evidencias=" + evidencias + "]";
	}

}