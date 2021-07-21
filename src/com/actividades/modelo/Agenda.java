package com.actividades.modelo;

import java.io.Serializable;
import javax.persistence.*;
import java.util.Date;
import java.util.List;


/**
 * The persistent class for the agenda database table.
 * 
 */
@Entity
@Table(name="agenda")
@NamedQueries({
	@NamedQuery(name="Agenda.buscarActivos", query="SELECT a FROM Agenda a where a.estado = 'A'  order by a.idAgenda asc"),
	@NamedQuery(name="Agenda.buscarPorEmpleadoLogeado", query="SELECT a FROM Agenda a where a.estado = 'A' "
			+ "and a.empleado.idEmpleado = :idEmpleado order by a.fechaInicio desc"),
	@NamedQuery(name="Agenda.buscarPorEmpleadoLogeadoYFechas", query="SELECT a FROM Agenda a where a.estado = 'A' "
			+ "and a.empleado.idEmpleado = :idEmpleado and (a.fechaInicio between :fechaInicio and :fechaFin) order by a.fechaInicio desc"),
	@NamedQuery(name="Agenda.buscarPorEmpleadoLogeadoCodigoAgenda", query="SELECT a FROM Agenda a where "
			+ "a.empleado.idEmpleado = :idEmpleado order by a.fechaInicio desc"),
	@NamedQuery(name="Agenda.buscarUltimaAgenda", query="SELECT a FROM Agenda a where "
			+ "a.empleado.idEmpleado = :idEmpleado order by a.fechaInicio desc"),
})
public class Agenda implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name="id_agenda")
	private Integer idAgenda;

	private String descripcion;
	
	private String codigo;
	
	private Integer secuencia;

	private String estado;

	@Temporal(TemporalType.DATE)
	@Column(name="fecha_fin")
	private Date fechaFin;

	@Temporal(TemporalType.DATE)
	@Column(name="fecha_inicio")
	private Date fechaInicio;

	//bi-directional many-to-one association to Actividad
	@OneToMany(mappedBy="agenda")
	private List<Actividad> actividads;

	//bi-directional many-to-one association to Empleado
	@ManyToOne
	@JoinColumn(name="id_empleado")
	private Empleado empleado;

	public Agenda() {
	}

	public Integer getIdAgenda() {
		return this.idAgenda;
	}

	public void setIdAgenda(Integer idAgenda) {
		this.idAgenda = idAgenda;
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

	public Date getFechaFin() {
		return this.fechaFin;
	}

	public void setFechaFin(Date fechaFin) {
		this.fechaFin = fechaFin;
	}

	public Date getFechaInicio() {
		return this.fechaInicio;
	}

	public void setFechaInicio(Date fechaInicio) {
		this.fechaInicio = fechaInicio;
	}
	
	public String getCodigo() {
		return codigo;
	}

	public void setCodigo(String codigo) {
		this.codigo = codigo;
	}

	public List<Actividad> getActividads() {
		return this.actividads;
	}

	public void setActividads(List<Actividad> actividads) {
		this.actividads = actividads;
	}

	public Integer getSecuencia() {
		return secuencia;
	}

	public void setSecuencia(Integer secuencia) {
		this.secuencia = secuencia;
	}

	public Actividad addActividad(Actividad actividad) {
		getActividads().add(actividad);
		actividad.setAgenda(this);

		return actividad;
	}

	public Actividad removeActividad(Actividad actividad) {
		getActividads().remove(actividad);
		actividad.setAgenda(null);

		return actividad;
	}

	public Empleado getEmpleado() {
		return this.empleado;
	}

	public void setEmpleado(Empleado empleado) {
		this.empleado = empleado;
	}

}