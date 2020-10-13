package com.actividades.modelo;

import java.io.Serializable;
import javax.persistence.*;
import java.util.List;

@Entity
@Table(name="cargo")
@NamedQueries({
	@NamedQuery(name="Cargo.buscarActivos", query="SELECT c FROM Cargo c where c.estado = 'A' order by c.idCargo asc"),
	@NamedQuery(name="Cargo.buscarPorPatron", query="SELECT c FROM Cargo c where lower(c.descripcion) like lower(:patron) "
			+ "and c.estado = 'A' order by c.idCargo asc"),
	@NamedQuery(name="Cargo.buscarPorID", query="SELECT c FROM Cargo c where c.idCargo = :id"),
	@NamedQuery(name="Cargo.buscarSinJefe", query="SELECT c FROM Cargo c where c.estado = 'A' and c.idCargo <> :id order by c.idCargo asc"),
})

public class Cargo implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name="id_cargo")
	private Integer idCargo;

	private String descripcion;

	private String estado;

	//bi-directional many-to-one association to Empleado
	@OneToMany(mappedBy="cargo")
	private List<Empleado> empleados;

	public Cargo() {
	}

	public Integer getIdCargo() {
		return this.idCargo;
	}

	public void setIdCargo(Integer idCargo) {
		this.idCargo = idCargo;
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

	public List<Empleado> getEmpleados() {
		return this.empleados;
	}

	public void setEmpleados(List<Empleado> empleados) {
		this.empleados = empleados;
	}

	public Empleado addEmpleado(Empleado empleado) {
		getEmpleados().add(empleado);
		empleado.setCargo(this);

		return empleado;
	}

	public Empleado removeEmpleado(Empleado empleado) {
		getEmpleados().remove(empleado);
		empleado.setCargo(null);

		return empleado;
	}

	@Override
	public String toString() {
		return this.descripcion;
	}

}