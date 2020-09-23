package com.actividades.modelo;

import java.io.Serializable;
import javax.persistence.*;
import java.util.List;


/**
 * The persistent class for the departamento database table.
 * 
 */
@Entity
@Table(name="departamento")
@NamedQueries({
	@NamedQuery(name="Departamento.buscarActivos", query="SELECT d FROM Departamento d where d.estado = 'A'"),
	@NamedQuery(name="Departamento.buscarPorPatron", query="SELECT d FROM Departamento d where lower(d.nombre) like lower(:patron) "
			+ "and d.estado = 'A' order by d.idDepartamento asc")
})
public class Departamento implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name="id_departamento")
	private Integer idDepartamento;

	private String descripcion;

	private String estado;

	private String nombre;

	//bi-directional many-to-one association to DepartamentoAsignado
	@OneToMany(mappedBy="departamento")
	private List<Empleado> empleados;
	
	@OneToMany(mappedBy="departamento")
	private List<DepartamentoAsignado> departamentoAsignados;

	public Departamento() {
	}

	public Integer getIdDepartamento() {
		return this.idDepartamento;
	}

	public void setIdDepartamento(Integer idDepartamento) {
		this.idDepartamento = idDepartamento;
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

	public String getNombre() {
		return this.nombre;
	}

	public void setNombre(String nombre) {
		this.nombre = nombre;
	}

	public List<DepartamentoAsignado> getDepartamentoAsignados() {
		return this.departamentoAsignados;
	}

	public void setDepartamentoAsignados(List<DepartamentoAsignado> departamentoAsignados) {
		this.departamentoAsignados = departamentoAsignados;
	}

	public DepartamentoAsignado addDepartamentoAsignado(DepartamentoAsignado departamentoAsignado) {
		getDepartamentoAsignados().add(departamentoAsignado);
		departamentoAsignado.setDepartamento(this);

		return departamentoAsignado;
	}

	public DepartamentoAsignado removeDepartamentoAsignado(DepartamentoAsignado departamentoAsignado) {
		getDepartamentoAsignados().remove(departamentoAsignado);
		departamentoAsignado.setDepartamento(null);

		return departamentoAsignado;
	}

	public List<Empleado> getEmpleados() {
		return empleados;
	}

	public void setEmpleados(List<Empleado> empleados) {
		this.empleados = empleados;
	}
	public Empleado addEmpleados(Empleado empleado) {
		getEmpleados().add(empleado);
		empleado.setDepartamento(this);

		return empleado;
	}

	public Empleado removeEmpleados(Empleado empleado) {
		getEmpleados().remove(empleado);
		empleado.setDepartamento(null);

		return empleado;
	}

	@Override
	public String toString() {
		return nombre;
	}
	
}